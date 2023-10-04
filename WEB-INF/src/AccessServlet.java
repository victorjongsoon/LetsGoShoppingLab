import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;


public class AccessServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserManager userManager;
    private DatabaseManager db; // Declare db as an instance variable

    @Override
    public void init() throws ServletException {
        super.init();
        DatabaseManager db = new DatabaseManager();
        userManager = new UserManager(db.getUsers(), db); // Pass DatabaseManager instance
    }

    @Override
    public void destroy() {
        super.destroy();
        db.writeUsers(userManager.getUsers());
    }

    private void loginAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.println(email);
        System.out.println(password);

        // Check if user is already logged in
        if (request.getSession(false) != null) {
            String sessionEmail = (String) request.getSession().getAttribute("email");
            if (sessionEmail != null && sessionEmail.equals(email)) {
                response.sendRedirect("/catalog/catalog.html");
                return;
            } else {
                request.getSession().invalidate();
            }
        }

        try {
            User userToLogin = new User(email, password);
            User loggedInUser = userManager.loginUser(userToLogin);

            // Set session attribute
            request.getSession().setAttribute("email", loggedInUser.getEmail());
            request.getSession().setAttribute("firstName", loggedInUser.getFirstName());
            System.out.println("LOGIN SUCCESSFUL");
            response.sendRedirect("/catalog/catalog.html");
        } catch (Exception e) {
            Map<String, User> users = db.getUsers();
            for (Map.Entry<String, User> entry : users.entrySet()) {
                String email1 = entry.getKey();
                User user1 = entry.getValue();
                System.out.println("Email: " + email1 + ", User: " + user1);
            }
            System.out.println(userManager.getUsers());
            String errorResponse = "Looks like there was an error with the user you tried to log in. " +
                    "Make sure that all the fields in the form have some value and are not empty.";
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorResponse);
        }
    }

    private void registerAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String password = request.getParameter("password");

        // Check if user is already logged in
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
            String errorResponse = "You are already registered. Please log out to register a new account.";
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorResponse);
            return;
        }

        try {
            User registeringUser = new User(email, password, firstName, lastName);
            userManager.registerUser(registeringUser);
            System.out.println("REGISTER SUCCESSFUL");
            response.sendRedirect("/catalog/login.html");
        } catch (Exception e) {
            String errorResponse = "Error occurred during registration. Please try again.";
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorResponse);
        }
    }

    private void logoutAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is logged in
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }
        response.sendRedirect("/catalog/login.html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");




        if ("login".equals(action)) {
            loginAction(request, response);
        } else if ("register".equals(action)) {
            registerAction(request, response);
        } else if ("logout".equals(action)) {
            logoutAction(request, response);
        } else {
            String errorResponse = "Invalid action requested.";
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorResponse);
        }
    }
}
