package models;

import com.avaje.ebean.Model;

import java.util.HashMap;

/**
 * Created by Lin on 6/13/16.
 */
public class Sale extends Model {
    private int saleId;
    public static int numberOfSale;
    private HashMap<String, Role> users;

    public enum Role {
        GUEST(1), BOOK_KEEPER(2), CASHIER(3), CLERK(4), SELLER(5), SALE_ADMIN(6), SUPER_USER(7);

        int permit;
        Role(int p) {
            permit = p;
        }

        int showPermission() {
            return permit;
        }
    }

    /**
     * Create a sale object
     */
    public Sale() {
        this.saleId = ++numberOfSale;
        this.users = new HashMap<>();
    }

    /**
     * Add a user to the current sale
     * @param email the user name
     * @param role the corresponding role
     * @return ture if the user is added
     */
    public boolean addUser(String email, Role role) {
        users.put(email, role);
        return true;
    }

    /**
     * Get all the active users in this sale
     * @return all the users
     */
    public HashMap<String, Role> getUsers(){
        return this.users;
    }

    /**
     * Get the user role of current sale
     * @param email the user name
     * @return the user role(***Which is a enum)
     */
    public Role getUserRole(String email) {
        return users.get(email);
    }

    /**
     * Get the user permission number based on their role
     * @param email the username
     * @return the permission level from 1 to 7, 7 is the highest
     */
    public int getUserPermit(String email) {
        return users.get(email).showPermission();

    }

    /**
     * get sale ID
     * @return sale ID
     */
    public int getSaleId() {
        return this.saleId;
    }

    /* Used for debugging
    public static void main(String args[]) {
        Sale s1 = new Sale();
        s1.addUser("user@gatech.edu", Role.CLERK);
        //HashMap<String, Role> alls = s1.getUsers();
        System.out.println(s1.getUserRole("user@gatech.edu"));
        System.out.println(s1.getUserPermit("user@gatech.edu"));
    }
    */

}



