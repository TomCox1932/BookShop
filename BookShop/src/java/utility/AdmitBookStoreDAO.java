package utility;

import java.util.*;
import java.sql.*;
import model.Book;

public class AdmitBookStoreDAO {

    private Connection con;

    public AdmitBookStoreDAO() {
        try {
            // Load the Driver class file
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            System.err.println("Getting Connection!");
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/BooksDB",
                    "user1", "password");

            if (con != null) {
                System.err.println("Got Connection!");
            }// end if
        } catch (Exception ex) {
            ex.printStackTrace();
        }// end catch

    }// end of Contructor

    // get info on all courses taught by a lecturer
    public List getAllBooks() throws SQLException {
        System.out.println("inside getAllBooks");
        Statement statement = con.createStatement();
        ResultSet rs = null;
        List list = new ArrayList();

       try {
        statement = con.createStatement();
        rs = statement.executeQuery("SELECT * FROM USER1.TBooks");

        String isbn = "";
        String title = "";
        String author = "";
        double price = 0.00;

        while (rs.next()) {
            System.out.println("rs has records");
            isbn = rs.getString(1);
            title = rs.getString(2);
            author = rs.getString(3);
            price = rs.getDouble(4);

            System.out.println("ISBN: " + isbn + ", Title: " + title + ", Author: " + author + ", Price: " + price);

            if (isbn != null && title != null && author != null) {
                list.add(new Book(isbn, title, author, price));
            } else {
                System.err.println("Null value detected in result set.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw e;
    } finally {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
        return list;
    } // end getAllBooks

}
