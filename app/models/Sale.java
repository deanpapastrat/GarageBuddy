package models;

import lib.Formatter;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.DbJsonB;
//import play.Logger;
import play.data.format.*;
import play.data.validation.*;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a sale in GarageBuddy.
 *
 * @author Z. Lin and Dean Papastrat
 * @version 1.0.7
 */
@Entity
@Table(name = "sales")
public class Sale extends Model {

    /* ATTRIBUTES */

    /**
     * Integer id of the Sale.
     */
    @Id
    public int id;

    /**
     * String name of owner of the Sale.
     */
    @Constraints.Required
    public String name;

    /**
     * Formatted date of start date of the Sale.
     */
    @Constraints.Required @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date startDate = new Date();

    /**
     * Formatted date of the end date of the Sale.
     */
    @Constraints.Required @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date endDate = new Date();

    /**
     * Sale closed flag.
     */
    private boolean isClosed;

    /**
     * Users associated with a Sale.
     */
    @DbJsonB
    public Map<String, Long> users = new HashMap<>();

    /**
     * Items associated with a Sale.
     */
    @OneToMany(mappedBy = "sale")
    public List<Item> items;

    /**
     * Transactions associated with a Sale.
     */
    @OneToMany(mappedBy = "sale")
    public List<Transaction> transactions;

    /**
     * Helper for query methods down below.
     */
    public static final Finder<String, Sale> FIND =
            new Finder<String, Sale>(Sale.class);

    /**
     * Enumerated list of roles a user could have in a Sale.
     */
    public enum Role {
        GUEST(1), BOOK_KEEPER(2), CASHIER(3), CLERK(4),
        SELLER(5), SALE_ADMIN(6), SUPER_USER(7);

        int permit;

        /**
         * Sets permit to p.
         * @param p int corresponding to enum's roles.
         */
        Role(final int p) {
            permit = p;
        }

        /**
         * Convert int to a long.
         * @return permit as an int
         */
        Long showPermission() {
            return ((Integer) permit).longValue();
        }

        /**
         * Displays permission as an int.
         * @return int corresponding to role
         */
        int showPermissionInt() {
            return permit;
        }

        /**
         * Given permit as a long, get the role.
         * @param permit
         * @return role corresponding to int
         */
        static Role fromPermit(final Long permit) {
            for (Role role : values()) {
                if (permit.equals(((Integer) role.permit).longValue())) {
                    return role;
                }
            }

            return GUEST;
        }

        /**
         * Convert role number to string.
         * @return string associated with role number
         */
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
     * Create a sale object.
     * @param saleAdmin is the user creating Sale
     */
    public Sale(final User saleAdmin) {
        this.users = new HashMap<>();
        this.addUser(saleAdmin.email, Role.SALE_ADMIN);
        this.isClosed = false;
    }


    /**
     * Check if two items are exactly the same.
     * @param obj to compare
     * @return True if obj exists, and is exactly this item
     */
    @Override
    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!Sale.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Sale other = (Sale) obj;

        return (this.id == other.id);
    }

    /**
     * Checks if all properties of the object are equivalent.
     * @param obj an object to compare
     * @return true if both objects contain same properties
     */
    public final boolean matches(final Object obj) {
        if (!this.equals(obj)) {
            return false;
        }

        final Sale other = (Sale) obj;

        return (this.users.equals(other.users));
    }

    /**
     * Gets id of this item, which is the same as its hash code.
     * @return id of this item
     */
    @Override
    public final int hashCode() {
        return this.id;
    }

    /* USER ROLES MANAGEMENT */

    /**
     * Add a user to the current sale.
     * @param email the user name
     * @param role the corresponding role
     * @return true if the user is added
     */
    public final boolean addUser(final String email, final Role role) {
        if (users == null) {
            this.users = new HashMap<>();
        }

        users.put(email, role.showPermission());
        return true;
    }

    /**
     * Get all the active users in this sale.
     * @return all the users
     */
    public final Map<String, Long> getUsers() {
        return this.users;
    }

    /**
     * Get the user role of current sale.
     * @param email the user name
     * @return the user role(***Which is a enum)
     */
    public final Role getUserRole(final String email) {
        Long permissionVal = users.get(email);

        if (permissionVal == null) {
            permissionVal = ((Integer) 0).longValue();
        }

        return Role.fromPermit(permissionVal);
    }

    /**
     * Get all roles except for Sale Admin.
     * @return All roles in a list
     */
    public static List<String> getUnrestrictedRoles() {
        List<String> roles = Arrays.asList("Guest", "Book Keeper", "Cashier",
                "Clerk", "Seller");
        return roles;
    }

    /**
     * Map string to enum of roles.
     * @return map of strings to roles
     */
    public static Map<String, Role> getRoleMap() {
        Map<String, Role> roleMap = new HashMap<>();
        roleMap.put("Guest", Role.GUEST);
        roleMap.put("Book Keeper", Role.BOOK_KEEPER);
        roleMap.put("Cashier", Role.CASHIER);
        roleMap.put("Clerk", Role.CLERK);
        roleMap.put("Seller", Role.SELLER);
        roleMap.put("Sale Administrator", Role.SALE_ADMIN);
        roleMap.put("Super User", Role.SUPER_USER);

        return roleMap;
    }

    /**
     * Get the user permission number based on their role.
     * @param email the username
     * @return the permission level from 1 to 7, 7 is the highest
     */
    public final int getUserPermission(final String email) {
        return getUserRole(email).showPermissionInt();
    }

    /* DATE GETTERS & SETTERS */

    /**
     * Get the start date in human format.
     *
     * @return a date in the pattern "MMM d, YYYY"
     */
    public final String getFormattedStartDate() {
        return Formatter.date(startDate);
    }


    /**
     * A method that returns the date as a date object,
     * so we can force the user to enter.
     * a valid start and end date (i.e. end date must be after the start date).
     * @return the start date, as a date object
     */
    public final Date getStartDate() {
        return this.startDate;
    }


    /**
     * @return the start date
     */
    public final Date getEndDate() {
        return this.endDate;
    }


    /**
     * Set the start date from string.
     *
     * @param input a date in the pattern "yyyy-mm-dd"
     * @return if startDate was set successfully or not
     */
    public final boolean setFormattedStartDate(final String input) {
        try {
            this.startDate = new SimpleDateFormat("yyyy-MM-dd").parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Get the end date in human format.
     *
     * @return a date in the pattern "MMM d, YYYY"
     */
    public final String getFormattedEndDate() {
        return Formatter.date(endDate);
    }

    /**
     * Set the end date from string.
     *
     * @param input a date in the pattern "yyyy-mm-dd"
     * @return if endDate was set successfully or not
     */
    public final boolean setFormattedEndDate(final String input) {
        try {
            this.endDate = new SimpleDateFormat("yyyy-MM-dd").parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }



    /* PREBUILT QUERIES */

    /**
     * Returns a sale from an ID.
     * @param id id of sale we want to find
     * @return a sale with the specified id
     */
    public static Sale findById(final int id) {
        return Sale.FIND.byId(id.toString());
    }

    /**
     * Builds a query for items related to this sale.
     *
     * @return an expression list for items that have this sale ID
     */
    public final ExpressionList<Item> findItems() {
        return Item.FIND.where().eq("sale_id", this.id);
    }

    /**
     * Builds a query for items related to this sale that are unpurchased.
     *
     * @return an expression list for unpurchased sale items
     */
    public final ExpressionList<Item> findUnpurchasedItems() {
        return findItems().eq("purchased", false);
    }

    /**
     * Builds a query for items related to this sale that are purchased.
     *
     * @return an expression list for purchased sale items
     */
    public final ExpressionList<Item> findPurchasedItems() {
        return findItems().eq("purchased", true);
    }

    /**
     * Builds a query for transactions related to this sale.
     *
     * @return an expression list for transactions
     */
    public final ExpressionList<Transaction> findTransactions() {
        return Transaction.find.where().eq("sale_id", this.id);
    }
    /**
     * Closes a Sale.
     */
    public final void close() {
        this.isClosed = true;
    }

    /**
     * Checks to see if a Sale is closed.
     * @return boolean true if the sale was closed, false otherwise
     */
    public final boolean isClosed() {
        return this.isClosed;
    }
}
