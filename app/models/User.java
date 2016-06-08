package models;

import javax.persistence.*;

import com.avaje.ebean.Model;
import play.data.format.*;
import play.data.validation.*;

/**
 * A simple representation of a user.
 * @author Philip Johnson
 */
@Entity
@Table(name="users")
public class User extends Model {

    @Id
    public String email;
    public String name;
    public String password;

    /**
     * Creates a new User instance.
     * @param name The name.
     * @param email The email.
     * @param password The password.
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
     * @param password the password to set
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

}