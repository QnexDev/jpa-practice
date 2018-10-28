package com.qnex.jpa.practice;

import com.qnex.jpa.practice.entity.Library;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class SampleTest {

    private static final Logger LOG = Logger.getLogger(SampleTest.class);
    private JpaTransactionManager transactionManager;
    private EntityManagerFactory entityManagerFactory;
    private TransactionTemplate txTemplate;
    private SharedEntityManagerBean entityManagerBean;

    @Before
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("sample-unit");

        transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        transactionManager.setPersistenceUnitName("sample-unit");
        transactionManager.afterPropertiesSet();

        txTemplate = new TransactionTemplate(transactionManager);

        entityManagerBean = new SharedEntityManagerBean();
        entityManagerBean.setEntityManagerFactory(entityManagerFactory);
        entityManagerBean.setPersistenceUnitName("sample-unit");
        entityManagerBean.setSynchronizedWithTransaction(true);
        entityManagerBean.afterPropertiesSet();
    }


    @Test
    public void shouldInitializeEntityManager() {
        Library newLibrary = txTemplate.execute(status -> {

            EntityManager entityManager = entityManagerFactory.createEntityManager();

            EntityTransaction transaction = entityManager.getTransaction();

            transaction.begin();

            Library entity = new Library(1L, "Main");
            entityManager.persist(entity);

            entityManager.flush();

            transaction.commit();

            Library library = entityManager.find(Library.class, 1L);

//            entityManager.close();


            return library;
        });

        LOG.debug(newLibrary);
    }

    @Test
    public void shouldInitializeSharedEntityManager() throws ExecutionException, InterruptedException {
        EntityManager entityManager = entityManagerBean.getObject();

        CompletableFuture<Library> future1 = CompletableFuture.supplyAsync(() -> {
            Library newLibrary = createAndFindLibrary(entityManager, 1L);

            LOG.debug(newLibrary);
            return newLibrary;
        });

        CompletableFuture<Library> future2 = CompletableFuture.supplyAsync(() -> {
            Library newLibrary = createAndFindLibrary(entityManager, 2L);

            LOG.debug(newLibrary);
            return newLibrary;
        });

//        CompletableFuture.allOf(future1, future2).get();

        future1.get();
        future2.get();

        System.out.println();


    }

    @Test
    public void testOptimisticLocking() throws Exception {
        final EntityManager entityManager = entityManagerBean.getObject();
        Assert.notNull(entityManager, "Must be not null");

        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final CountDownLatch afterCommitLatch = new CountDownLatch(1);


        txTemplate.execute(status -> {
            Library entity = new Library(1L, "Main", 1L);
            entityManager.persist(entity);
            return null;
        });

        CompletableFuture f1 = CompletableFuture.supplyAsync(() -> {
            txTemplate.execute(status -> {
                LOG.debug("The transaction 'T1' has been started");
                try {
                    latch1.countDown();
                    latch2.await();
                    Library library = entityManager.find(Library.class, 1L);
                    library.setName("Edited name");
                } catch (InterruptedException e) {
                    LOG.error(e);
                }
                LOG.debug("The transaction 'T1' was being committed");
                return null;
            });

            afterCommitLatch.countDown();
            return null;
        });

        CompletableFuture f2 = CompletableFuture.supplyAsync(() -> txTemplate.execute(status -> {
            LOG.debug("The transaction 'T2' has been started");
            try {
                latch2.countDown();
                latch1.await();
                Library library = entityManager.find(Library.class, 1L);
                library.setName("Another Edited name");
            } catch (InterruptedException e) {
                LOG.error(e);
            }
            try {
                afterCommitLatch.await();
            } catch (InterruptedException e) {
                LOG.error(e);
            }
            LOG.debug("The transaction 'T2' was being committed");
            return null;
        }));

        CompletableFuture.allOf(f1, f2).get();

        LOG.debug(entityManager.find(Library.class, 1L));

    }

    private Library createAndFindLibrary(EntityManager entityManager, Long id) {
        return txTemplate.execute(status -> {

            Library entity = new Library(id, "Main");
            entityManager.persist(entity);

            entityManager.flush();

            Library library = entityManager.find(Library.class, id);


            return library;
        });
    }
}
