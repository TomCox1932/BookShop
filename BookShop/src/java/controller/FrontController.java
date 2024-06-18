package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
//import dispatchers.*;
import model.Book;
import model.CartItem;
import utility.AdmitBookStoreDAO;

public class FrontController extends HttpServlet {

    private final HashMap actions = new HashMap();

    //Initialize global variables
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config);

    }

    //Process the HTTP Get request
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        System.err.println("doGet()");
        doPost(request, response);

    }

    //Process the HTTP Post request
   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/html");

    String requestedAction = request.getParameter("Action");
    System.out.println("Requested Action: " + requestedAction);
    HttpSession session = request.getSession();
    AdmitBookStoreDAO dao = new AdmitBookStoreDAO();
    String nextPage = null;

    if (requestedAction == null) 
    {
        List<Book> books = null;
        nextPage = "/jsp/error.jsp";
        try {
            books = dao.getAllBooks();
            System.out.println("Books retrieved: " + (books != null ? books.size() : "null"));
            if (books != null) {
                session.setAttribute("Books", books);
                nextPage = "/jsp/titles.jsp";
            }
        } catch (Exception ex) 
        {
            request.setAttribute("result", ex.getMessage());
            ex.printStackTrace();
        } finally
        {
            this.dispatch(request, response, nextPage);
        }
    } else if (requestedAction.equals("add_to_cart"))
    {
        nextPage = "/jsp/titles.jsp";

        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
        String[] selectedBooks = request.getParameterValues("add");
        System.out.println("Selected Books: " + Arrays.toString(selectedBooks));

        if (selectedBooks == null) 
        {
            request.setAttribute("result", "No books selected to add.");
            this.dispatch(request, response, nextPage);
            return;
        }

        if (cart == null) 
        {
            cart = new HashMap();
            for (String isbn : selectedBooks) 
            {
                String quantityStr = request.getParameter(isbn);
                System.out.println("ISBN: " + isbn + ", Quantity: " + quantityStr);
                if (quantityStr != null) 
                {
                    int quantity = Integer.parseInt(quantityStr);
                    Book book = this.getBookFromList(isbn, session);
                    System.out.println("Book: " + (book != null ? book.getTitle() : "null"));
                    if (book != null)
                    {
                        CartItem item = new CartItem(book);
                        item.setQuantity(quantity);
                        cart.put(isbn, item);
                    }
                }
            }
            session.setAttribute("cart", cart);
        } else {
            for (String isbn : selectedBooks) 
            {
                String quantityStr = request.getParameter(isbn);
                System.out.println("ISBN: " + isbn + ", Quantity: " + quantityStr);
                if (quantityStr != null)
                {
                    int quantity = Integer.parseInt(quantityStr);
                    if (cart.containsKey(isbn)) 
                    {
                        CartItem item = cart.get(isbn);
                        item.setQuantity(quantity);
                    } 
                    else
                    {
                        Book book = this.getBookFromList(isbn, session);
                        System.out.println("Book: " + (book != null ? book.getTitle() : "null"));
                        if (book != null) {
                            CartItem item = new CartItem(book);
                            item.setQuantity(quantity);
                            cart.put(isbn, item);
                        }
                    }
                }
            }
        }

        this.dispatch(request, response, nextPage);
    }
    else if (requestedAction.equals("Checkout")) 
    {
        nextPage = "/jsp/checkout.jsp";
        this.dispatch(request, response, nextPage);
    } 
    else if (requestedAction.equals("Continue")) 
    {
        nextPage = "/jsp/titles.jsp";
        this.dispatch(request, response, nextPage);
    } 
    else if (requestedAction.equals("update_cart")) 
    {
        nextPage = "/jsp/cart.jsp";
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
        if (cart != null) 
        {
            String[] booksToRemove = request.getParameterValues("remove");
            if (booksToRemove != null) 
            {
                for (String bookToRemove : booksToRemove) 
                {
                    cart.remove(bookToRemove);
                }
            }
            Set<Map.Entry<String, CartItem>> entries = cart.entrySet();
            Iterator<Map.Entry<String, CartItem>> iter = entries.iterator();
            while (iter.hasNext()) 
            {
                Map.Entry<String, CartItem> entry = iter.next();
                String isbn = entry.getKey();
                CartItem item = entry.getValue();
                String quantityStr = request.getParameter(isbn);
                System.out.println("ISBN: " + isbn + ", Quantity: " + quantityStr);
                if (quantityStr != null) 
                {
                    int quantity = Integer.parseInt(quantityStr);
                    item.updateQuantity(quantity);
                }
            }
        }
        this.dispatch(request, response, nextPage);
    }
    else if (requestedAction.equals("view_cart"))
    {
        nextPage = "/jsp/cart.jsp";
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
        if (cart == null) 
        {
            nextPage = "/jsp/titles.jsp";
        }
        this.dispatch(request, response, nextPage);
    }
}


    private Book getBookFromList(String isbn, HttpSession session)
    {
        List list = (List) session.getAttribute("Books");
        Book aBook = null;
        for (int i = 0; i < list.size(); i++)
        {
            aBook = (Book) list.get(i);
            if (isbn.equals(aBook.getIsbn())) 
            {
                break;
            }
        }
        return aBook;
    }

    private void dispatch(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
    if (page == null) {
        page = "/jsp/error.jsp";
    }
    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(page);
    dispatcher.forward(request, response);
}

    //Get Servlet information
    public String getServletInfo() 
    {
        return "controller.FrontController Information";
    }

}
