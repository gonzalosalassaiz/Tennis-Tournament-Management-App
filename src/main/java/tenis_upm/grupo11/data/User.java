package tenis_upm.grupo11.data;

public class User {
    private String name;
    private String phone;
    private String email;
    private String username;
    private String password;
    private boolean verificationStatus;
    private int totalPoints;
    private boolean isAdmin;
    private String verificationToken;

    // Constructor
    public User(String name, String phone, String email, String username, String password, boolean verification, boolean isAdmin) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.verificationStatus = verification;
        this.isAdmin = isAdmin;
    }

    /**
     * Creates a new instance of user with same values
     *
     * @param user the user to clone
     * @return the new, cloned user
     */
    public static User clone(User user) {
        return new User(
            user.name,
            user.phone,
            user.email,
            user.username,
            user.password,
            user.verificationStatus,
            user.isAdmin);
    }

    // Getters y setters
    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVerification() {
        return verificationStatus;
    }

    public void setVerification(boolean verification) {
        this.verificationStatus = verification;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}

