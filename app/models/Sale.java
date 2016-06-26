package models;

import com.avaje.ebean.ExpressionList;
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
 * @author Z. Lin and Dean Papastrat
 * @version 1.0.0
 */
@Entity
@Table(name="sales")
public class Sale extends Model {

    /* ATTRIBUTES */

    @Id
    public int id;

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

    public static Finder<String, Sale> find = new Finder<String, Sale>(Sale.class);

    /* CONSTRUCTORS & EQUIVALENCY */

    /**
     * Create a sale object
     */
    public Sale(User sale_admin) {
        this.users = new HashMap<>();
        this.addUser(sale_admin.email, Role.SALE_ADMIN);
        this.save();
    }

    /**
     * Delete a sale object
     *
     * @return if the object was successfully deleted from the database.
     */
    public boolean deleteSale() {
        return delete();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!Sale.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Sale other = (Sale) obj;

        if (this.id == other.id) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if all properties of the object are equivalent
     * @param obj an object to compare
     * @return
     */
    public boolean matches(Object obj) {
        if (!this.equals(obj)) {
            return false;
        }

        final Sale other = (Sale) obj;

        if (this.users.equals(other.users)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    /* USER ROLES MANAGEMENT */

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
    public int getUserPermission(String email) {
        return users.get(email).showPermission();

    }

    /* PREBUILT QUERIES */

    /**
     * Returns a sale from an ID
     * @param id id of sale we want to find
     * @return a sale with the specified id
     */
    public static Sale findById(Integer id) {
        return Sale.find.byId(id.toString());
    }

    /**
     * Builds a query for items related to this sale
     *
     * @return an expression list for items that have this sale ID
     */
    public ExpressionList<Item> findItems() {
        return Item.find.where().eq("sale_id", this.id);
    }

    /**
     * Builds a query for items related to this sale that are unpurchased
     *
     * @return an expression list for unpurchased sale items
     */
    public ExpressionList<Item> findUnpurchasedItems() {
        return findItems().eq("purchased", false);
    }

    /**
     * Builds a query for items related to this sale that are purchased
     *
     * @return an expression list for purchased sale items
     */
    public ExpressionList<Item> findPurchasedItems() {
        return findItems().eq("purchased", true);
    }
}



