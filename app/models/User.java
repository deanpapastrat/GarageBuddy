package models;

import javax.persistence.*;

import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.*;
import java.util.*;

/**
 * A User
 * likely that more Id vars get added as we move forwards
 */
@Entity
@Table(name="users")
public class User extends Model {

    @Id
    public String email;
    public String name;
    public String password;

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
            System.out.println("uh oh");
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
    public boolean validate(String login) {
        try {
            return PasswordStorage.verifyPassword(login, this.password);
        } catch (Exception echo) {
            return false;
        }
    }

    /**
     *
     * @return true or false: this user is not null, i.e., it has values for all fields
     */

    public boolean exists() {
        return ((this.name != null) &&
                (this.email != null) &&
                (this.password != null));
    }

    /**
     * This is needed to specify the find method, used to search the User database for users
     */
    public static Finder<String, User> find = new Finder<String, User>(User.class);


    /**
     * This is a query that uses email as the key to search for a particular user
     * Side note, we're going to want to make sure we don't add multiple users with the same email
     * @param email
     * @return true if we find a user with email, and false otherwise
     */
    public boolean isUser(String email) {
        User testUser = User.find.byId(email);
        return testUser.exists();
    }
}