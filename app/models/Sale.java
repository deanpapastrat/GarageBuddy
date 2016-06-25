package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.DbEnumType;
import com.avaje.ebean.annotation.DbEnumValue;
import com.avaje.ebean.annotation.DbJsonB;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a sale in GarageBuddy
 *
 * @author Z. Lin
 */
@Entity
@Table(name="sales")
public class Sale extends Model {

    @Id
    public int saleId;

    @DbJsonB
    public Map<String, Role> users;

    @OneToMany(mappedBy = "sale")
    public List<Item> items;

    public enum Role {
        GUEST(1), BOOK_KEEPER(2), CASHIER(3), CLERK(4), SELLER(5), SALE_ADMIN(6), SUPER_USER(7);

        int permit;

        Role(int p) {
            permit = p;
        }

        int showPermission() {
            return permit;
        }

        @DbEnumValue(storage = DbEnumType.INTEGER)
        public int getValue() {
            return permit;
        }
    }

    /**
     * Create a sale object
     */
    public Sale(User sale_admin) {
        this.users = new HashMap<>();
        this.addUser(sale_admin.email, Role.SALE_ADMIN);
        this.save();
    }

    /**
     * This is needed to specify the find method, used to search the Sale database for sales
     */
    public static Finder<Integer, Sale> find = new Finder<Integer, Sale>(Sale.class);

    /**
     * Delete a sale object
     */
    public void deleteSale() {
        Sale sale = find.byId(this.saleId);
        sale.delete();
    }

    /**
     * Add a user to the current sale
     * @param email the user name
     * @param role the corresponding role
     * @return true if the user is added
     */
    public boolean addUser(String email, Role role) {
        users.put(email, role);
        this.save();
        return true;
    }

    /**
     * Get all the active users in this sale
     * @return all the users
     */
    public Map<String, Role> getUsers(){
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

//    TODO: Fix all this stuffffff
//    /**
//     * TG - adding in some prelim code for items
//     * Adds an item to the sale
//     * should be wrapped in a helper method to get the user adding the item
//     * @param seller The User who's item it is
//     * @param itemname The name of the Item being added to the Sale
//     * @param price The price of the Item being added to the Sale
//     * @return boolean status code of if this was successful; if so, should take the user to a page to edit item info and/or add another item
//     */
//    private boolean addItemToSale(User seller, String itemname, double price) {
//        try {
//            Item i = new Item(seller, itemname, price);
//            saleitems.add(i);
//            // dont panic
//            return true;
//        } catch (Exception e) {
//            // panic
//            return false;
//        }
//    }
//
//    /**
//     * TG - adding in some prelim code for items
//     * Used to return a List of items at the sale for the website to show
//     * @return a copy of the items at the sale
//     */
//    public List<Item> getSaleItems() {
//        return saleitems.clone();
//    }
//
//
    
    
    
    //Used for debugging


}



