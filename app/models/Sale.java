package models;

import lib.Formatter;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.DbJsonB;
import play.Logger;
import play.data.format.*;
import play.data.validation.*;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Constraints.Required
    public String name;

    @Constraints.Required @Formats.DateTime(pattern="yyyy-MM-dd")
    public Date startDate = new Date();

    @Constraints.Required @Formats.DateTime(pattern="yyyy-MM-dd")
    public Date endDate = new Date();

    @DbJsonB
    public Map<String, Long> users = new HashMap<>();

    @OneToMany(mappedBy = "sale")
    public List<Item> items;

    @OneToMany(mappedBy = "sale")
    public List<Transaction> transactions;

    public static final Finder<String, Sale> find = new Finder<String, Sale>(Sale.class);

    public enum Role {
        GUEST(1), BOOK_KEEPER(2), CASHIER(3), CLERK(4), SELLER(5), SALE_ADMIN(6), SUPER_USER(7);

        int permit;

        Role(int p) {
            permit = p;
        }

        Long showPermission() {
            return ((Integer) permit).longValue();
        }

        int showPermissionInt() {
            return permit;
        }

        static Role fromPermit(Long permit) {
            for (Role role : values()) {
                if (permit.equals(((Integer) role.permit).longValue())) {
                    return role;
                }
            }

            return GUEST;
        }

        public String toString() {
            String result;
            switch (permit) {
                case 7: result = "Super User";
                    break;
                case 6: result = "Sale Administrator";
                    break;
                case 5: result = "Seller";
                    break;
                case 4: result = "Clerk";
                    break;
                case 3: result = "Cashier";
                    break;
                case 2: result = "Book Keeper";
                    break;
                default: result = "Guest";
                    break;
            }
            return result;
        }
    }

    /* CONSTRUCTORS & EQUIVALENCY */

    /**
     * Create a sale object
     */
    public Sale(User saleAdmin) {
        this.users = new HashMap<>();
        this.addUser(saleAdmin.email, Role.SALE_ADMIN);
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
        if (users == null) {
            this.users = new HashMap<>();
        }

        users.put(email, role.showPermission());
        this.save();
        return true;
    }

    /**
     * Get all the active users in this sale
     * @return all the users
     */
    public Map<String, Long> getUsers(){
        return this.users;
    }

    /**
     * Get the user role of current sale
     * @param email the user name
     * @return the user role(***Which is a enum)
     */
    public Role getUserRole(String email) {
        Long permissionVal = users.get(email);

        if (permissionVal == null) {
            permissionVal = ((Integer) 0).longValue();
        }

        return Role.fromPermit(permissionVal);
    }

    /**
     * Get the user permission number based on their role
     * @param email the username
     * @return the permission level from 1 to 7, 7 is the highest
     */
    public int getUserPermission(String email) {
        return getUserRole(email).showPermissionInt();

    }

    /* DATE GETTERS & SETTERS */

    /**
     * Get the start date in human format
     *
     * @return a date in the pattern "MMM d, YYYY"
     */
    public String getFormattedStartDate() {
        return Formatter.date(startDate);
    }

    /**
     * Set the start date from string
     *
     * @param input a date in the pattern "yyyy-mm-dd"
     * @return if startDate was set successfully or not
     */
    public boolean setFormattedStartDate(String input) {
        try {
            this.startDate = new SimpleDateFormat("yyyy-MM-dd").parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Get the end date in human format
     *
     * @return a date in the pattern "MMM d, YYYY"
     */
    public String getFormattedEndDate() {
        return Formatter.date(endDate);
    }

    /**
     * Set the end date from string
     *
     * @param input a date in the pattern "yyyy-mm-dd"
     * @return if endDate was set successfully or not
     */
    public boolean setFormattedEndDate(String input) {
        try {
            this.endDate = new SimpleDateFormat("yyyy-MM-dd").parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
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

    /**
     * Builds a query for transactions related to this sale
     *
     * @return an expression list for transactions
     */
    public ExpressionList<Transaction> findTransactions() {
        return Transaction.find.where().eq("sale_id", this.id);
    }
}
