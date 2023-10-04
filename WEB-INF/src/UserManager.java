import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, User> users;
    private DatabaseManager db; // DatabaseManager instance

    // Constructor to initialize users and DatabaseManager with arguments
    public UserManager(Map<String, User> users, DatabaseManager db) {
        this.users = users;
        this.db = db;
    }

    // Default constructor initializes users to an empty HashMap
    public UserManager() {
        this.users = new HashMap<>();
        this.db = new DatabaseManager();
    }

    // Method to retrieve all User data
    public Map<String, User> getUsers() {
        return users;
    }

    // Method to register a new user
    // Method to register a new user
    public void registerUser(User userToRegister) {
        // Validate userToRegister fields
        if (userToRegister.getEmail() == null || userToRegister.getEmail().isEmpty() ||
                userToRegister.getPassword() == null || userToRegister.getPassword().isEmpty() ||
                userToRegister.getFirstName() == null || userToRegister.getFirstName().isEmpty() ||
                userToRegister.getLastName() == null || userToRegister.getLastName().isEmpty()) {
            throw new InvalidParameterException("Invalid user parameters");
        }

        // Check if user is already registered
        if (users.containsKey(userToRegister.getEmail())) {
            throw new IllegalStateException("User is already registered");
        }

        // Register the user
        users.put(userToRegister.getEmail(), userToRegister);

        // Write the updated users map to the database
        db.writeUsers(users);
    }

    // Method to login a user
    public User loginUser(User userToLogin) {
        // Validate userToLogin fields
        if (userToLogin.getEmail() == null || userToLogin.getEmail().isEmpty() ||
                userToLogin.getPassword() == null || userToLogin.getPassword().isEmpty()) {
            throw new InvalidParameterException("Invalid login parameters");
        }

        // Check if user exists in the users map
        if (!users.containsKey(userToLogin.getEmail())) {
            throw new IllegalArgumentException("User does not exist");
        }

        // Retrieve the user from the map
        User storedUser = users.get(userToLogin.getEmail());

        // Check if the provided password matches the stored password
        if (!storedUser.getPassword().equals(userToLogin.getPassword())) {
            throw new IllegalStateException("Incorrect email or password");
        }

        // Return the validated user
        return storedUser;
    }

    public static void main(String[] args) {
        // Testing UserManager functionality
        UserManager userManager = new UserManager();
        User user1 = new User("test@example.com", "password123", "John", "Doe");

        userManager.registerUser(user1);

        // Register a new user
        try {
            userManager.registerUser(user1);
            System.out.println("User registered successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Login as a registered user
        try {
            User loggedInUser = userManager.loginUser(new User("test@example.com", "password123"));
            System.out.println("User logged in: " + loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Attempt to login with incorrect password
        try {
            userManager.loginUser(new User("test@example.com", "wrongpassword"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Attempt to register an incomplete user
        try {
            userManager.registerUser(new User("incomplete@example.com", "", "Jane", "Doe"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Retrieve all registered users
        System.out.println("All registered users: " + userManager.getUsers());
    }
}
