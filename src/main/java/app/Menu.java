package app;

import dB.DB;
import entity.Book;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Yerchik on 12.04.2017.
 */
public class Menu {
    static Scanner scanner = new Scanner(System.in);

    public static void menu() throws IOException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Main");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, User! Welcome to our Library!");
        boolean switcher = true;
        do {
            System.out
                    .println("Menu:\n 1 - add book;\n 2 - remove;\n 3 - edit book;\n 4 - all books;\n 5 - Exit\n Please select number from the menu");

            String operation = scanner.next();
            if (operation.equals("1")) {
                add(entityManagerFactory);
            }
            else if (operation.equals("2")) {
                remove(entityManagerFactory);
            }
            else if (operation.equals("3")) {
                edit(entityManagerFactory);
            }
            else if (operation.equals("4")) {
                allBooks(entityManagerFactory);
            }
            else if (operation.equals("5")) {
                System.exit(0);
            }
            else System.out.println("You've putted wrong number, try again.");

        } while (switcher);

        entityManager.getTransaction().commit();
        entityManager.close();
        entityManagerFactory.close();
    }

    static void add(EntityManagerFactory entityManagerFactory) throws IOException {
        System.out.println("Input author_name:");
        String author = scanner.next();
        System.out.println("Input book_name: ");
        String bookName = scanner.next();
        DB.addNewBook(new Book(author, bookName), entityManagerFactory);
        System.out.println(new Book(author, bookName) + " was added.");
        System.out.println("Pres any button:");
        System.in.read();
    }

    static void allBooks(EntityManagerFactory entityManagerFactory) throws IOException {
        System.out.println("All books:");
        int i = 1;
        for (Book book : DB.getAll(entityManagerFactory)) {
            System.out.println("" + i + ". " + book);
            i++;
        }
        System.out.println("Pres any button:");
        System.in.read();
    }

    static void remove(EntityManagerFactory entityManagerFactory) throws IOException {
        boolean a = true;
        do {
            System.out.println("Input book_name, that you want to remove:");
            String bookName = scanner.next();
            List<Book> books = DB.findByName(bookName, entityManagerFactory);
            if (DB.findByName(bookName, entityManagerFactory).size() == 0){
                System.out.println("we don't have such book.");
                allBooks(entityManagerFactory);
                System.out.println(" 1 - Try again.\n 2 - Main menu.");
                String operation = scanner.next();
                if(operation.equals("1")) a = true;
                if(operation.equals("2")) a = false;
            }
            else if (books.size() == 1){
                System.out.print(books.get(0));
                DB.removeBook(books.get(0), entityManagerFactory);
                a = false;
                System.out.println(" was removed.");
            }
            else {
                int i = 1;
                System.out.println("We have few books with such name please choose one by typing a number of book:");
                for (Book book : DB.findByName(bookName, entityManagerFactory)) {
                    System.out.println("" + i + ". " + book);
                    i++;
                }
                int number = scanner.nextInt();
                if (number < i){
                    System.out.print(books.get(number - 1));
                    DB.removeBook(books.get(number - 1), entityManagerFactory);
                    a = false;
                    System.out.println(" was removed.");
                }
                else System.out.println("You've putted wrong number, try again.");
            }

        }while (a);
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
//            if (books.size() < 1) {
//                System.out.println("we don't have such book.");
//                allBooks(entityManagerFactory);
//                System.out.println(" 1 - Try again.\n 2 - Main menu.");
//                int operation = scanner.nextInt();
//                if (operation == 1) a = true;
//                if (operation == 2) a = false;
//            }
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
