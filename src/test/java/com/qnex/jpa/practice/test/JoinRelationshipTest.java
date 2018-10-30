package com.qnex.jpa.practice.test;

import com.qnex.jpa.practice.PostgresDataSource;
import com.qnex.jpa.practice.entity.Author;
import com.qnex.jpa.practice.entity.Book;
import com.qnex.jpa.practice.entity.Library;
import com.qnex.jpa.practice.entity.LibraryType;
import org.apache.log4j.Logger;
import org.junit.Test;

import javax.persistence.EntityManager;

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


        Library library = entityManager.find(Library.class, 666L);

        LOG.debug(jdbcTemplate.queryForList("select * from Book"));
    }

    private void initializeData(EntityManager entityManager) {
        txOperations.execute(status -> {
            Library entity = new Library(666L, "Main", 1L, null);
            LibraryType libraryType = new LibraryType();
            libraryType.setType("Big Library");
            libraryType.setId(1L);
            entityManager.persist(libraryType);
            entityManager.persist(entity);

            entityManager.persist(new Author(1L, "Alex"));
            entityManager.persist(new Author(2L, "Igor"));

            entityManager.persist(new Book(1L, "The Witcher 3", null));
            return null;
        });
    }
}
