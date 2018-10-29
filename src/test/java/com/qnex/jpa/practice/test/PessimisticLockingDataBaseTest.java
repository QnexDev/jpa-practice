package com.qnex.jpa.practice.test;

import com.qnex.jpa.practice.PostgresDataSource;
import com.qnex.jpa.practice.entity.Library;
import com.qnex.jpa.practice.entity.LibraryType;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@PostgresDataSource
public class PessimisticLockingDataBaseTest extends AbstractDataBaseTest {

    private static final Logger LOG = Logger.getLogger(PessimisticLockingDataBaseTest.class);

    @Test
    public void testPessimisticLocking() throws Exception {
        final EntityManager entityManager = entityManagerBean.getObject();
        Assert.notNull(entityManager, "Must be not null");

        final CountDownLatch syncLatch = new CountDownLatch(1);
        final CountDownLatch afterCommitLatch = new CountDownLatch(1);

        initializeData(entityManager);

        CompletableFuture f1 = CompletableFuture.supplyAsync(() -> {
            txOperations.execute(status -> {
                try {
                    syncLatch.await();
                    Library library = entityManager.find(Library.class, 1L);
                    library.setName("Edited name");
                    entityManager.lock(library, LockModeType.PESSIMISTIC_READ);
                    afterCommitLatch.countDown();
                    delay(5000);
                } catch (InterruptedException e) {
                    LOG.error(e);
                }
                return null;
            });
            return null;
        });

        CompletableFuture f2 = CompletableFuture.supplyAsync(() -> txOperations.execute(status -> {
            try {
                syncLatch.await();
                Library library = entityManager.find(Library.class, 1L);
//                entityManager.lock(library, LockModeType.PESSIMISTIC_WRITE);
                library.setName("Another Edited name");
                afterCommitLatch.await();
            } catch (InterruptedException e) {
                LOG.error(e);
            }
            return null;
        }));

        syncLatch.countDown();
        CompletableFuture.allOf(f1, f2).get();

        LOG.debug(jdbcTemplate.queryForList("select * from Library"));

    }

    private void initializeData(EntityManager entityManager) {
        txOperations.execute(status -> {
            Library entity = new Library(1L, "Main", 1L, null);
            LibraryType libraryType = new LibraryType();
            libraryType.setType("Big Library");
            libraryType.setId(1L);
            entityManager.persist(libraryType);
            entityManager.persist(entity);
            return null;
        });
    }

}
