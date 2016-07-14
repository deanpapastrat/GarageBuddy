package models;

import javax.persistence.*;
import com.avaje.ebean.Model;
import play.Logger;
import play.data.validation.*;

/**
 * Represents a user in GarageBuddy.
 *
 * @author Andre Allen, Taj Gillani, Dean Papastrat, and Alex Woods
 * @version 1.0.0
 */
@Entity
@Table(name = "users")
public class User extends Model {

    /**
     * Represents the number of login attempts a user can perform
     * before being locked out.
     */
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    /**
     * Represents the email of the user.
     */
    @Id @Constraints.Email @Constraints.Required
    public String email;

    /**
     * Represents the name of the user.
     */
    @Constraints.Required
    public String name;

    /**
     * Represents the SHA-1 hash of the user's password.
     */
    @Constraints.Required
    public String password;

    /**
     * Represents the street address of the user.
     */
    public String address;

    /**
     * Represents the city the user lives in.
     */
    public String city;

    /**
     * Represents the state the user lives in.
     */
    public String state;

    /**
     * Represents the ZIP code of the user.
     */
    public String postalCode;

    /**
     * Identifies the user as a super admin or not.
     */
    public boolean isSuperUser;

    /**
     * Provides a helper for a finder object for easy querying.
     */
    public static final Finder<String, User> FIND = new Finder<String, User>(
            User.class);

    /**
     * Number of times the user has tried to log in.
     */
    private int loginAttempts = 0;

    /**
     * Creates a user.
     *
     * @param name name of the User
     * @param email email of the User
     * @param password password of the User
     */
    public User(final String name, final String email, final String password) {
        this.name = name;
        this.email = email;
        try {
            this.password = PasswordStorage.createHash(password);
        } catch (Exception echo) {
            Logger.error("Password hashing failed.", password);
        }
    }

    /* PASSWORDS */

    /**
     * Converts the password to a secure hash and sets it on the user.
     *
     * @param password the password to set in place of the existing password
     */
    public final void setPassword(final String password) {
        try {
            this.password = PasswordStorage.createHash(password);
        } catch (Exception echo) {
            System.out.println("uh oh");
        }
    }
    /**
     * Checks to see if the password matches the one stored in the DB.
     *
     * @param login string of the password the user tries to log in with
     * @return true or false: is supplied password is the correct password?
     */
    public final boolean checkPassword(final String login) {
        try {
            return PasswordStorage.verifyPassword(login, this.password);
        } catch (Exception echo) {
            return false;
        }
    }

    /**
     * Checks if the user has all non-null fields.
     *
     * @return true or false: this user is not null
     */

    public final boolean exists() {
        return ((this.name != null)
                && (this.email != null)
                && (this.password != null));
    }

    /**
     * Ad-hoc validation for the user; ensures there isn't a user with the
     * same email (uniqueness).
     *
     * @return null if valid, message if not
     */
    public final String validate() {
        User user = User.findByEmail(email);
        if (user != null && user.email.equals(email)) {
            return "Email " + email + " is already taken.";
        }
        return null;
    }


    /* LOGIN ATTEMPTS */

    /**
     * Gets the number of login attempts the user has done.
     *
     * @return number of times user has tried to log in
     */
    public final int getLoginAttempts() {
        return loginAttempts;
    }

    /**
     * Gets the number of login attempts the user may perform before being
     * locked out.
     *
     * @return number of tries the user has left to log in
     */
    public final int getLoginAttemptsRemaining() {
        return MAX_LOGIN_ATTEMPTS - loginAttempts;
    }

    /**
     * Increases the number of login attempts by 1.
     */
    public final void incLoginAttempts() {
        this.loginAttempts += 1;
        save();
    }

    /**
     * Resets the number of login attempts to 0.
     */
    public final void resetLoginAttempts() {
        this.loginAttempts = 0;
        save();
    }

    /**
     * Checks to see whether or not the user can attempt to login.
     *
     * @return whether or not the user should be allowed to try to login
     */
    public final boolean canLogin() {
        return loginAttempts < MAX_LOGIN_ATTEMPTS;
    }

    /* PERMISSIONS MANAGEMENT */

    /**
     * Determines whether or not a user is allowed to perform an action.
     * TODO: fully implement permissions
     *
     * @param action the action the user wants to perform
     * @return whether or not the user may perform that action
     */
    public final boolean can(final String action) {
        return isSuperUser;
    }

    /* QUERIES */

    /**
     * Find a user by email.
     *
     * @param email email address of user
     * @return the user matching the email
     */
    public static final User findByEmail(final String email) {
        if (email == null) {
            return null;
        }
        return FIND.where().ieq("email", email).findUnique();
    }

    /**
     * This is a query that uses email as the key to search for a user.
     *
     * @param email email of user to find
     * @return true if we find a user with email, and false otherwise
     */
    public static final boolean isUser(final String email) {
        User testUser = User.FIND.byId(email);
        return testUser.exists();
    }
}
