package com.qnex.jpa.practice.test;

import com.qnex.jpa.practice.PostgresDataSource;
import com.qnex.jpa.practice.entity.Book;
import com.qnex.jpa.practice.entity.Library;
import com.qnex.jpa.practice.entity.LibraryType;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.apache.log4j.Logger;
import org.junit.Test;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;

@PostgresDataSource
public class JoinRelationshipTest extends AbstractDataBaseTest {

    private static final Logger LOG = Logger.getLogger(ModificationWithinTransactionTest.class);


    @Test
    public void testRelationship() {
        EntityManager entityManager = getEntityManager();

        initializeData(entityManager);

        txOperations.execute(status -> {
            Library library = entityManager.find(Library.class, 666L);
            Book book = entityManager.find(Book.class, 1L);
            library.getBooks().add(book);
            return null;
        });

        Library library = txOperations.execute(status -> entityManager.find(Library.class, 666L));

        assertEquals(3, QueryCountHolder.getGrandTotal().getSelect());

        LOG.debug(jdbcTemplate.queryForList("select * from Book"));
    }

    private void initializeData(EntityManager entityManager) {
        txOperations.execute(status -> {
            Library library = new Library(666L, "Main", 1L, null);
            LibraryType libraryType = new LibraryType();
            libraryType.setType("Big Library");
            libraryType.setId(1L);
            entityManager.persist(libraryType);
            entityManager.persist(library);

            library.getBooks().add(new Book(10L, "Test1", null));
            library.getBooks().add(new Book(11L, "Test2", null));


            Book book = new Book(1L, "The Witcher 3", null);

            addBookToLibrary(entityManager, book, 1L);
            addBookToLibrary(entityManager, book, 2L);
            addBookToLibrary(entityManager, book, 3L);

            return null;
        });
    }

    private void addBookToLibrary(EntityManager entityManager, Book book, Long id) {
        Library library = new Library(id, "Second", 1L, null);
        entityManager.persist(library);
        library.getBooks().add(book);
    }
}
