import jakarta.persistence.EntityGraph;
import model.Nplus1problem.Author;
import model.Nplus1problem.Book;
import org.junit.jupiter.api.Test;

import java.util.List;

import static repository.HibernateUtil.doWithSession;

public class BookTest {
    @Test
    void testNplus1Problem() {
        Book book1 = new Book("Clean Code");
        Author robert = new Author("Robert C. Martin");
        book1.addAuthor(robert);

        Book book2 = new Book("Design Patterns");
        Author erich = new Author("Erich Gamma");
        book2.addAuthor(erich);
        Author helm = new Author("Richard Helm");
        book2.addAuthor(helm);

        Book book3 = new Book("Effective Java");
        Author bloch = new Author("Joshua Bloch");
        book3.addAuthor(bloch);

        Book book4 = new Book("Head First Java");
        Author sierra = new Author("Kathy Sierra");
        book4.addAuthor(sierra);
        Author bates = new Author("Bert Bates");
        book4.addAuthor(bates);

        Book book5 = new Book("Spring in Action");
        Author walls = new Author("Craig Walls");
        book5.addAuthor(walls);

        Book book6 = new Book("Java Puzzlers");
        book6.addAuthor(bloch);


        // Book list
        List<Book> books = List.of(book1, book2, book3, book4, book5, book6);

        // Authors list
        List<Author> authors = List.of(robert, erich, helm, bloch, sierra, bates, walls);

        // Persist all books and their authors
        doWithSession(session -> {
            authors.forEach(session::persist);
            books.forEach(session::persist);
            return null;
        });

        /* Showcase the n + 1 problem
            - Is a query pattern issue where you have 1 initial query followed by N additional queries
            - Results in multiple separate database round trips
         */
       /* doWithSession(session -> {
            List<Author> allAuthors = session.createQuery("from Author", Author.class).getResultList();
            for (Author author : allAuthors) {
                System.out.println("Author: " +author.getName());
                for (Book book : author.getBooks()) {
                    System.out.println("Book: " + book.getTitle());
                }
            }
            return null;
        });
        /*
        The result pattern shows exactly why it's called the N+1 problem:
            - 1 query to fetch all authors
            - N additional queries (one for each author) to fetch their books
        In this case, with 7 authors, we see:
            - 1 initial query for all authors
            - 7 separate queries for books (one per author) = 8 total queries

            Example output:
            -- First a query (1)
                select * from author
            -- Then N separate queries, one per author
                select * from book where author_id = ?
                select * from book where author_id = ?
                ...etc
         */
        // To fix this N+1 problem, you could use either "Join Fetch" or "Entity Graph"
        /*doWithSession(session -> {
            // Method 1: Join Fetch:
            List<Author> allAuthors = session.createQuery("from Author a join fetch a.books", Author.class).getResultList();
            for (Author author : allAuthors) {
                System.out.println("Author: " +author.getName());
                for (Book book : author.getBooks()) {
                    System.out.println("Book: " + book.getTitle());
                }
            }
            return null;
        });
        /*
        The result pattern shows that the "Join Fetch" approach is much more efficient:
            - 1 initial query for all authors
            - 1 additional query to fetch all books for each author = 2 total queries
         
        JOIN FETCH Limitations:
         * JOIN FETCH has limitations when used with pagination (setMaxResults and setFirstResult):
         * - Hibernate can't transform FirstResult and MaxResult into OFFSET and LIMIT in SQL
         * - Instead, it fetches ALL records first, then filters in memory
         * - This can lead to performance issues with large datasets
         * - Hibernate will issue a warning about in-memory pagination
         */

        // Method 2: Entity Graph:
        doWithSession(session -> {
            EntityGraph<Author> entityGraph = session.createEntityGraph(Author.class);
            entityGraph.addAttributeNodes("books");
            List<Author> allAuthors = session.createQuery("from Author", Author.class)
                    .setHint("javax.persistence.fetchgraph", entityGraph)
                    .getResultList();
            for (Author author : allAuthors) {
                System.out.println("Author: " +author.getName());
                for (Book book : author.getBooks()) {
                    System.out.println("Book: " + book.getTitle());
                }
            }
            return null;
        });
        /*
        The result pattern shows that the "Entity Graph" approach is much more efficient:
            - 1 initial query for all authors
            - 1 additional query to fetch all books for each author = 2 total queries

        The Entity Graph solution has worked correctly here because: `addAttributeNodes("books")`
            - It tells Hibernate to eagerly fetch the relationship of the book
            - It generates a single optimized SQL query instead of multiple queries
            - There are no subsequent queries when iterating through the books collection

        NB: The "Entity Graph" approach is only available in Hibernate 5.4 and above.
         */
    }

    @Test
    void testJoinFetchWithPagination() {
        doWithSession(session -> {
            // This will trigger JOIN FETCH pagination limitation warning
            List<Book> books = session.createQuery(
                            "SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.authors ORDER BY b.title",
                            Book.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList();

            for (Book book : books) {
                System.out.println("Book: " + book.getTitle());
                for (Author author : book.getAuthors()) {
                    System.out.println("- Author: " + author.getName());
                }
            }
            return null;
        });
    }
    
    /*
    Note that the N+1 problem and cartesian product (cross join) are different concepts, although both can impact performance:
    N+1 Problem:
        - Is a query pattern issue where you have 1 initial query followed by N additional queries
        - Results in multiple separate database round trips
        - Example from earlier logs:
            -- First query (1)
            select * from author
            -- Then N separate queries, one per author
            select * from book where author_id = ?
            select * from book where author_id = ?
            ...etc

    Cartesian Product:
        - Is a result set multiplication issue where every row from one table is matched with every row from another table
        - Happens in a single query
        - Example:
        SELECT * FROM author CROSS JOIN book
        -- If you have 10 authors and 100 books, you get 1000 rows (10 × 100)

    The solution we implemented using LEFT JOIN:
        ```sql
        select a1_0.id, b1_0.author_id, b1_1.id, b1_1.name, a1_0.name
        from author a1_0
        left join (book_author b1_0
        join book b1_1 on b1_1.id=b1_0.book_id)
        on a1_0.id=b1_0.author_id
        ```

    This avoids both problems because:
        1. It solves N+1 by fetching all data in one query
        2. It avoids cartesian product by using proper JOINs with matching conditions (`on` clauses)

    The key differences are:
        - N+1 is about query count (multiple separate queries)
        - Cartesian product is about row multiplication (within a single query)
        - N+1 wastes resources through multiple database calls
        - Cartesian product wastes resources through excessive result set size
     */
}
