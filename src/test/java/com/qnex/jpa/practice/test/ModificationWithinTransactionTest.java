package com.qnex.jpa.practice.test;

import com.qnex.jpa.practice.PostgresDataSource;
import com.qnex.jpa.practice.entity.Library;
import com.qnex.jpa.practice.entity.LibraryType;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@PostgresDataSource
public class ModificationWithinTransactionTest extends AbstractDataBaseTest {

    private static final Logger LOG = Logger.getLogger(ModificationWithinTransactionTest.class);


    @Test
    public void checking() throws Exception {
        final EntityManager entityManager = entityManagerBean.getObject();
        Assert.notNull(entityManager, "Must not be null");

        final CountDownLatch syncLatch = new CountDownLatch(1);
        final CountDownLatch afterCommitLatch = new CountDownLatch(1);

        initializeData(entityManager);

        CompletableFuture f1 = CompletableFuture.supplyAsync(() -> {
            txOperations.execute(status -> {
                Library library = entityManager.find(Library.class, 1L);
                LibraryType libraryType = entityManager.find(LibraryType.class, 1L);
                library.setLibraryType(libraryType);

                entityManager.flush();

                delay(3000);

                return null;
            });


            return null;
        });

        CompletableFuture f2 = CompletableFuture.supplyAsync(() -> txOperations.execute(status -> {
//            delay(1000);
            entityManager.remove(entityManager.find(LibraryType.class, 1L));
            return null;
        }));

        syncLatch.countDown();
        try {
            CompletableFuture.allOf(f1, f2).get();
        } catch (Exception e) {
            if (!(e.getCause() instanceof DataIntegrityViolationException)) {
                throw e;
            }
            LOG.error(e.getCause());
        }

        LOG.debug(jdbcTemplate.queryForList("select * from LibraryType"));

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
