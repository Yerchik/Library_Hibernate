package app;

import entity.Book;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Yerchik on 12.04.2017.
 */
public class Methods {
    static Scanner scanner = new Scanner(System.in);

    public static void menu() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Main");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        boolean switcher = true;
        do {
            System.out
                    .println("Menu:\n 1 - add book;\n 2 - remove;\n 3 - edit book;\n 4 - all books;\n 5 - Exit\n Please select number from the menu");

            int operation = scanner.nextInt();
            if (operation == 1) {
                add(entityManagerFactory);
            }
            if (operation == 2) {
                remove(entityManagerFactory);
            }
            if (operation == 3) {
                edit(entityManagerFactory);
            }
            if (operation == 4) {
                allBooks(entityManagerFactory);
                System.out.println("enter any key.");
                scanner.next();
            }
            if (operation == 5) {
                System.exit(0);
            }
            if (operation != 1 && operation != 2 && operation != 3 && operation != 4 && operation != 5)
                System.out.println("vedit nomer z menu");

        } while (switcher);

        entityManager.getTransaction().commit();
        entityManager.close();
        entityManagerFactory.close();
    }

    static void add(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        System.out.println("Input author_name:");
        String author = scanner.next();
        System.out.println("Input book_name: ");
        String bookName = scanner.next();
        entityManager.persist(new Book(author, bookName));
        entityManager.getTransaction().commit();
        System.out.println(new Book(author, bookName) + " was added.");
        System.out.println("enter any key:");
        scanner.next();
        entityManager.close();
    }

    static void allBooks(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        System.out.println("All books:");
        List<Book> books = entityManager.createQuery("SELECT b FROM Book b").getResultList();
        int i = 1;
        for (Book book : books) {
            System.out.println(i + ". " + book);
            i++;
        }
        entityManager.close();
    }

    static void remove(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        boolean a = false;
        Book book = new Book();
        do {
            System.out.println("Input book_name, that you want to remove:");
            String bookName = scanner.next();
            List<Book> books = entityManager.createQuery("SELECT b FROM Book b WHERE b.name = :name").
                    setParameter("name", bookName).getResultList();
            if (books.size() < 1) {
                System.out.println("we don't have such book.");
                allBooks(entityManagerFactory);
                System.out.println(" 1 - Try again.\n 2 - Main menu.");
                int operation = scanner.nextInt();
                if (operation == 1) a = true;
                if (operation == 2) a = false;
            }
            if (books.size() == 1) {
                for (Book bookRemove : books) {
                    entityManager.remove(bookRemove);
                    entityManager.getTransaction().commit();
                }
                System.out.println(books.get(0).toString() + " was removed.");
                System.out.println("enter any key:");
                scanner.next();
                a = false;
                entityManager.close();
            }
            if (books.size() > 1) {
                System.out.println("We have few books with such name please choose one by typing a number of book:");
                allSame(bookName, entityManager);
                int num = scanner.nextInt();
                removeSameBook(bookName, num, entityManager);
            }
        } while (a);
    }

    static void removeSameBook(String name, int num, EntityManager entityManager) {
        List<Book> books = entityManager.createQuery("SELECT b FROM Book b WHERE b.name = :name").
                setParameter("name", name).getResultList();
        int i = 1;
        for (Book book : books) {
            if (i == num){
                entityManager.remove(book);
                entityManager.getTransaction().commit();
                System.out.println(book.toString() + " was removed.");
                System.out.println("enter any key:");
            }
            i++;
        }
    }

    static void allSame(String name, EntityManager entityManager){
        List<Book> books = entityManager.createQuery("SELECT b FROM Book b WHERE b.name = :name").
                setParameter("name", name).getResultList();
        int i = 1;
        for (Book book : books) {
            System.out.println(i + ". " + book);
            i++;
        }
    }

    static void edit(EntityManagerFactory entityManagerFactory){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        boolean a = false;
        Book book = new Book();
        do {
            System.out.println("Input book_name, that you want to edit:");
            String bookName = scanner.next();
            List<Book> books = entityManager.createQuery("SELECT b FROM Book b WHERE b.name = :name").
                    setParameter("name", bookName).getResultList();
            if (books.size() < 1) {
                System.out.println("we don't have such book.");
                allBooks(entityManagerFactory);
                System.out.println(" 1 - Try again.\n 2 - Main menu.");
                int operation = scanner.nextInt();
                if (operation == 1) a = true;
                if (operation == 2) a = false;
            }
            if (books.size() == 1) {
                System.out.println("please enter new name of book.");
                String  newBookName = scanner.next();
                for (Book bookEdit : books) {
                    System.out.print(bookEdit);
                    bookEdit.setName(newBookName);
                    entityManager.merge(bookEdit);
                    entityManager.getTransaction().commit();
                    System.out.println(" was changed to: " + bookEdit);
                }

                System.out.println("enter any key:");
                scanner.next();
                a = false;
                entityManager.close();
            }
            if (books.size() > 1) {
                System.out.println("We have few books with such name please choose one by typing a number of book:");
                allSame(bookName, entityManager);
                int num = scanner.nextInt();
                editSameBook(bookName, num, entityManager);
            }
        } while (a);
    }

    static void editSameBook(String name, int num, EntityManager entityManager) {
        List<Book> books = entityManager.createQuery("SELECT b FROM Book b WHERE b.name = :name").
                setParameter("name", name).getResultList();
        int i = 1;
        System.out.println("please enter new name of book.");
        String  newBookName = scanner.next();
        for (Book book : books) {
            if (i == num){
                System.out.print(book);
                book.setName(newBookName);
                entityManager.merge(book);
                entityManager.getTransaction().commit();
                System.out.println(" was changed to: " + book);
            }
            i++;
        }
    }
}
