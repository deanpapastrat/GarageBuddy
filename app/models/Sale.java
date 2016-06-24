package models;

import com.avaje.ebean.Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lin on 6/13/16.
 */
public class Sale extends Model {
    private int saleId;
    public static int numberOfSale;
    private HashMap<String, Role> users;
    public ArrayList<Item> saleitems;

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
        saleitems = new ArrayList<>();
    }

    /**
     * Add a user to the current sale
     * @param email the user name
     * @param role the corresponding role
     * @return true if the user is added
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

    
    
    
    
    /**
     * TG - adding in some prelim code for items
     * Adds an item to the sale
     * should be wrapped in a helper method to get the user adding the item
     * @param seller The User who's item it is
     * @param itemname The name of the Item being added to the Sale
     * @param price The price of the Item being added to the Sale
     * @return boolean status code of if this was successful; if so, should take the user to a page to edit item info and/or add another item
     */
    private boolean addItemToSale(User seller, String itemname, double price) {
        try {
            Item i = new Item(seller, itemname, price);
            saleitems.add(i);
            // dont panic
            return true;
        } catch (Exception e) {
            // panic
            return false;
        }
    }
    
    /**
     * TG - adding in some prelim code for items
     * Used to return a List of items at the sale for the website to show
     * @return a copy of the items at the sale
     */
    public List<Item> getSaleItems() {
        return saleitems.clone();
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



