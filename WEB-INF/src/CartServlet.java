import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CartManager cartManager;
    private DatabaseManager db;

    @Override
    public void init() throws ServletException {
        super.init();
        db = new DatabaseManager();
        Map<String, Map<CartItem, Integer>> userCarts = db.getUserCarts();
        cartManager = new CartManager(userCarts);
    }

    @Override
    public void destroy() {
        super.destroy();
        db.writeUserCarts(cartManager.getUserCarts());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect("/catalog/login.html");
            return;
        }

        String email = (String) session.getAttribute("email");
        Map<CartItem, Integer> userCart = cartManager.getUserCart(email);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (userCart == null || userCart.isEmpty()) {
            out.println("<html><body>");
            out.println("<h2>Your cart is empty.</h2>");
            out.println("<a href=\"/catalog/catalog.html\">Go back to catalog</a>");
            out.println("</body></html>");
        } else {
            String cartSummary = CartSummaryHtmlGenerator.getCartSummaryPage(userCart);
            out.println(cartSummary);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect("/catalog/login.html");
            return;
        }

        String email = (String) session.getAttribute("email");
        String imgAddress = request.getParameter("imgAddress");
        String itemName = request.getParameter("itemName");
        int itemPrice = Integer.parseInt(request.getParameter("itemPrice"));

        CartItem cartItem = new CartItem(imgAddress, itemName, itemPrice);
        cartManager.addToCart(email, cartItem);

        response.sendRedirect("/catalog/catalog.html");
    }
}
