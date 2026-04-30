package model;
/**
 * This is the Base Class (Parent).
 * It contains the attributes that every person in the hospital needs.
 */
public abstract class User {
    // Attributes required by the class diagram
    protected int userId;
    protected String username;
    protected String password;

    // Constructor to initialize a user
    public User(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    // Common methods that every role must implement
    public abstract void login();
    public abstract void logout();

    // Getters so other parts of the app can see the ID and Name
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
}