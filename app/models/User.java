package models;

import javax.persistence.*;
import com.avaje.ebean.Model;
import play.Logger;
import play.data.validation.*;

/**
 * Represents a user in GarageBuddy
 *
 * @author Andre Allen, Taj Gillani, Dean Papastrat, and Alex Woods
 * @version 1.0.0
 */
@Entity
@Table(name="users")
public class User extends Model {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    @Id @Constraints.Email @Constraints.Required
    public String email;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String password;

    public String address;
    public String city;
    public String state;
    public String postalCode;

    public boolean isSuperUser;
    public static final Finder<String, User> find = new Finder<String, User>(User.class);
    private int loginAttempts = 0;

    /**
     * @param name name of the User
     * @param email email of the User
     * @param password password of the User
     */
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        try {
            this.password = PasswordStorage.createHash(password);
        } catch (Exception echo) {
            Logger.error("Password hashing failed.", password);
        }
    }

    /**
     * @param password the password to set in place of the existing password
     */
    public void setPassword(String password) {
        try {
            this.password = PasswordStorage.createHash(password);
        } catch (Exception echo) {
            System.out.println("uh oh");
        }
    }
    /**
     * @param login string of the password the user tries to log in with
     * @return true or false: is supplied password is the correct password?
     */
    public boolean checkPassword(String login) {
        try {
            return PasswordStorage.verifyPassword(login, this.password);
        } catch (Exception echo) {
            return false;
        }
    }

    /**
     * Checks if the user has all non-null fields.
     * @return true or false: this user is not null, i.e., it has values for all fields
     */

    public boolean exists() {
        return ((this.name != null) &&
                (this.email != null) &&
                (this.password != null));
    }

    /**
     * Find a user by email
     * @param email email address of user
     * @return the user matching the email
     */
    public static User findByEmail(String email) {
        if (email == null) {
            return null;
        }
        return find.where().ieq("email", email).findUnique();
    }

    /**
     * Ad-hoc validation for the user; ensures there isn't a user with the same email (uniqueness).
     * @return null if valid, message if not
     */
    public String validate() {
        User user = User.findByEmail(email);
        if (user != null && user.email.equals(email)) {
            return "Email " + email + " is already taken.";
        }
        return null;
    }

    /**
     * This is a query that uses email as the key to search for a particular user
     * Side note, we're going to want to make sure we don't add multiple users with the same email
     * @param email
     * @return true if we find a user with email, and false otherwise
     */
    public static boolean isUser(String email) {
        User testUser = User.find.byId(email);
        return testUser.exists();
    }

    /* LOGIN ATTEMPTS */

    /**
     * Gets the number of login attempts the user has done
     * @return number of times user has tried to log in
     */
    public int getLoginAttempts() {
        return loginAttempts;
    }

    /**
     * Gets the number of login attempts the user may do before being locked out
     * @return number of tries the user has left to log in
     */
    public int getLoginAttemptsRemaining() {
        return MAX_LOGIN_ATTEMPTS - loginAttempts;
    }

    /**
     * Increases the number of login attempts by 1
     */
    public void incLoginAttempts() {
        this.loginAttempts += 1;
        save();
    }

    /**
     * Resets the number of login attempts to 0
     */
    public void resetLoginAttempts() {
        this.loginAttempts = 0;
        save();
    }

    /**
     * Checks to see whether or not the user can attempt to login
     * @return whether or not the user should be allowed to try to login
     */
    public boolean canLogin() {
        return loginAttempts < MAX_LOGIN_ATTEMPTS;
    }

    /* PERMISSIONS MANAGEMENT */

    public boolean can(String action) {
        return isSuperUser; // TODO: fully implement permissions
    }
}