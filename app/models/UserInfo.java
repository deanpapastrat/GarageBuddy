package models;

/**
 * A simple representation of a user. 
 * @author Philip Johnson
 */
public class UserInfo {
 
  private String name;
  private String email;
  private String password;
  
  /**
   * Creates a new UserInfo instance.
   * @param name The name.
   * @param email The email.
   * @param password The password.
   */
  public UserInfo(String name, String email, String password) {
    this.name = name;
    this.email = email;
    try {
        this.password = PasswordStorage.createHash(password);
    } catch (Exception echo) {
        System.out.println("uh oh");
    }
  }
  
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }
  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }
  /**
   * @return the password
   */
  public String getPassword() {
    return password;
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