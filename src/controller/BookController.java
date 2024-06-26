package controller;

import entity.Book;
import entity.Customer;
import entity.Loan;
import jdbc.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookController {
    private Connection conn;

    public BookController() {
        conn = DatabaseManager.getConnection();
    }

    public void closeConnection() {
        DatabaseManager.closeConnection(conn);
    }

    public void addBook(String title, String category, String author, int quantity, String publishedDate) {
        // Add book
        String sql = "INSERT INTO books (title, category, author, quantity, publishedDate) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, title);
            statement.setString(2, category);
            statement.setString(3, author);
            statement.setInt(4, quantity);
            statement.setString(5, publishedDate);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Thêm sách thành công");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm sách");
            e.printStackTrace();
        }
    }

    public void updateBook(int book_Id, String title, String category, String author, int quantity, String publishedDate) {
        // Update book
        String sql = "UPDATE books SET title = ?, category = ?, author = ?, quantity = ?, publishedDate = ? WHERE book_Id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, title);
            statement.setString(2, category);
            statement.setString(3, author);
            statement.setInt(4, quantity);
            statement.setString(5, publishedDate);
            statement.setInt(6, book_Id);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cập nhật thông tin sách thành công");
            } else {
                System.out.println("Không tìm thấy sách cần cập nhật");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật thông tin sách");
            e.printStackTrace();
        }
    }

    public void deleteBook(int book_Id) {
        // Delete book
        String sql = "DELETE FROM books WHERE booK_Id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, book_Id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Xóa sách thành công");
            } else {
                System.out.println("Không tìm thấy sách cần xóa");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa sách");
            e.printStackTrace();
        }
    }

    public Book getBook(int book_Id) {
        // Get book
        String sql = "SELECT * FROM books WHERE book_Id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, book_Id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Book(resultSet.getInt("book_Id"), resultSet.getString("title"), resultSet.getString("category"),
                        resultSet.getString("author"), resultSet.getInt("quantity"), resultSet.getString("publishedDate"));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy thông tin sách");
            e.printStackTrace();
        }
        return null;
    }

    public List<Book> getAllBooks() {
        // Get all books
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int book_Id = resultSet.getInt("book_Id");
                String title = resultSet.getString("title");
                String category = resultSet.getString("category");
                String author = resultSet.getString("author");
                int quantity = resultSet.getInt("quantity");
                String publishedDate = resultSet.getString("publishedDate");
                Book newBook = new Book(book_Id, title, category, author, quantity, publishedDate);
                books.add(newBook);

//                books.add(new Book(resultSet.getInt("book_Id"), resultSet.getString("title"), resultSet.getString("category"),
//                        resultSet.getString("author"), resultSet.getInt("quantity"), resultSet.getString("publishedDate")));

            } // Đóng vòng lặp while của ResultSet

// In danh sách sách ra console
            System.out.println("---------------------------------------------------------------------------------------------");
            System.out.printf("%5s %20s %12s %15s %10s %15s", "BOOK_ID", "TITLE", "CATEGORY", "AUTHOR", "QUANTITY", "PUBLISHED DATE");
            System.out.println();
            System.out.println("---------------------------------------------------------------------------------------------");
            for (Book book : books) {
                System.out.format("%7s %20s %12s %15s %10s %15s", book.getBook_Id(), book.getTitle(), book.getCategory(), book.getAuthor(), book.getQuantity(), book.getPublished_date());
                System.out.println();
            }
            System.out.println("----------------------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy thông tin sách");
            e.printStackTrace();
        }
        return books;
    }




    public List<Book> searchBookByTitle(String bookTitle) {
        List<Book> result = new ArrayList<>();

        int bookId = getBookIdByTitle(bookTitle);

        if (bookId == -1) {
            System.out.println("Không tìm thấy sách");
            return result;
        }

        String bookQuery = "SELECT * FROM books WHERE book_Id = ?";

        try (PreparedStatement bookStatement = conn.prepareStatement(bookQuery)) {
            bookStatement.setInt(1, bookId);
            try (ResultSet bookResultSet = bookStatement.executeQuery()) {
                while (bookResultSet.next()) {
                    int bookIdResult = bookResultSet.getInt("book_Id");
                    String title = bookResultSet.getString("title");
                    String category = bookResultSet.getString("category");
                    String author = bookResultSet.getString("author");
                    int quantity = bookResultSet.getInt("quantity");
                    String publishedDate = bookResultSet.getString("publishedDate");
                    Book book = new Book(bookIdResult, title, category, author, quantity, publishedDate);
                    result.add(book);
                }
            }


//            System.out.println("---------------------------------------------------------------------------------------------");
//            System.out.printf("%5s %10s %10s %10 %10", "ID", "TITLE", "CATEGORY", "AUTHOR", "QUANTITY");
//            System.out.println();
//            System.out.println("---------------------------------------------------------------------------------------------");
//            for (Book book : result) {
//                System.out.format("%7s %14s %7s %14 %7 ",book.getBook_Id(), book.getTitle(), book.getCategory(), book.getAuthor(), book.getQuantity());
//                System.out.println();
//            }
//            System.out.println("----------------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            System.out.println("Lỗi khi tìm kiếm sách");
            e.printStackTrace();
        }

        return result;
    }
    private int getBookIdByTitle(String bookTitle) {
        int bookId = -1;
        String sql = "SELECT book_Id FROM BOOKS WHERE title LIKE ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, "%" + bookTitle + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    bookId = resultSet.getInt("book_Id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy ID của sách từ tên sách");
            e.printStackTrace();
        }
        return bookId;
    }
}
