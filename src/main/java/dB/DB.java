package dB;

import entity.Book;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Created by Yerchik on 24.06.2017.
 */
public class DB {
    public static void addNewBook(Book book, EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(book);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public static List<Book> getAll(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Book> books = entityManager.createQuery("SELECT b FROM Book b").getResultList();
        entityManager.close();
        return books;
    }

    public static List<Book> findByName(String bookName, EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Book> books = entityManager.createQuery("SELECT b FROM Book b WHERE b.name = :name").
                setParameter("name", bookName).getResultList();
        entityManager.close();
        return books;
    }

    public static void removeBook(Book bookRemove, EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.createQuery("SELECT b FROM Book b WHERE b = :bookRemove").
                setParameter("bookRemove", bookRemove).getSingleResult());
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public static void editBook(Book newBook, EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(newBook);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
