import java.util.HashMap;
import java.util.Map;

public class CartManager {
    private Map<String, Map<CartItem, Integer>> userCarts;

    // Constructor to initialize userCarts with provided data
    public CartManager(Map<String, Map<CartItem, Integer>> userCarts) {
        this.userCarts = new HashMap<>(userCarts);
    }

    // Default constructor initializes userCarts to an empty HashMap
    public CartManager() {
        this.userCarts = new HashMap<>();
    }

    // Method to retrieve all user carts
    public Map<String, Map<CartItem, Integer>> getUserCarts() {
        return userCarts;
    }

    // Method to get user cart based on email
    public Map<CartItem, Integer> getUserCart(String email) {
        return userCarts.getOrDefault(email, new HashMap<>());
    }

    // Method to add an item to the user's cart
    public void addToCart(String email, CartItem item) {
        // Get user cart based on email
        Map<CartItem, Integer> userCart = userCarts.computeIfAbsent(email, k -> new HashMap<>());

        // Increment the quantity of the item in the cart
        userCart.put(item, userCart.getOrDefault(item, 0) + 1);

        // Update the user cart in the main map
        userCarts.put(email, userCart);
    }

    // Method to remove an item from the user's cart - can be implemented
    public void removeFromCart(String email, CartItem item) {
        // Get user cart based on email
        Map<CartItem, Integer> userCart = userCarts.get(email);

        // If user cart exists and the item is in the cart, decrement its quantity
        if (userCart != null && userCart.containsKey(item)) {
            int quantity = userCart.get(item);
            if (quantity > 1) {
                userCart.put(item, quantity - 1);
            } else {
                // If the item quantity is 1, remove it from the cart completely
                userCart.remove(item);
            }

            // Update the user cart in the main map
            userCarts.put(email, userCart);
        }
    }


    public static void main(String[] args) {
        // Testing CartManager functionality
        CartManager cartManager = new CartManager();

        // Adding items to user's cart
        cartManager.addToCart("user1@example.com", new CartItem("item1", "Item One", 20));
        cartManager.addToCart("user1@example.com", new CartItem("item2", "Item Two", 25));
        cartManager.addToCart("user2@example.com", new CartItem("item3", "Item Three", 30));
        cartManager.addToCart("user2@example.com", new CartItem("item1", "Item One", 20));

        // Retrieving user carts
        Map<String, Map<CartItem, Integer>> userCarts = cartManager.getUserCarts();
        System.out.println("User Carts: " + userCarts);

        // Retrieving specific user's cart
        Map<CartItem, Integer> user1Cart = cartManager.getUserCart("user1@example.com");
        System.out.println("User 1 Cart: " + user1Cart);
    }
}
