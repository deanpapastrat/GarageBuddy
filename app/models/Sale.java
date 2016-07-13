package models;

import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import com.avaje.ebean.annotation.Sql;
import lib.Formatter;

import com.avaje.ebean.annotation.DbJsonB;
import play.Logger;
import play.data.format.*;
import play.data.validation.*;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import lib.Formatter;

/**
 * Represents a sale in GarageBuddy
 *
 * @author Z. Lin and Dean Papastrat
 * @version 1.0.7
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
    
    // sale closed flag - TG
    private boolean isClosed;

    @DbJsonB
    public Map<String, Long> users = new HashMap<>();

    //public List<String> sellerEmails = new ArrayList<>();

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
        this.isClosed = false;
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
//        if (role.showPermission() >= 5) {
//            this.sellerEmails.add(email);
//        }
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

    public static List<String> getUnrestrictedRoles() {
        List<String> roles = Arrays.asList("Guest", "Book Keeper", "Cashier",
                "Clerk", "Seller");
        return roles;
    }

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
     * A method that returns the date as a date object, so we can force the user to enter
     * a valid start and end date (i.e. end date must be after the start date).
     * @return the start date, as a date object
     */
    public Date getStartDate() {
        return this.startDate;
    }


    /**
     * @return the start date
     */
    public Date getEndDate() {
        return this.endDate;
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
     * Find users in this sale that can sell items
     * @return the list of sellers
     */


    public List<User> findSellers() {
        //Can I keep track of the sellers instead of looping every time?
        List<String> sellerEmails = new ArrayList<>();
        for (String email: this.users.keySet()) {
            if (getUserPermission(email) >= 5) {
                sellerEmails.add(email);
            }
        }
        Query<User> query = Ebean.createQuery(User.class);
        List<User> sellers= query.where().in("email", sellerEmails).findList();
        return sellers;
    }

    /**
     * Build the query to retrieve the financial report of items sold by a seller
     * @param sellerEmail the email of the seller
     * @param saleId the sale id
     * @return the report of items sold by a seller
     */

    public List<SqlRow> findReport(String sellerEmail, int saleId) {
        String sql = "SELECT * FROM (SELECT items.name AS itemname, items.id AS itemid, items.created_by_email as owneremail,\n" +
                "items.description,items.price, items.sold_for, transactions.formatted_created_at AS soldat,\n" +
                "transactions.customer_name AS soldTo, transactions.sale_id AS saleid, transactions.id AS transid\n" +
                "FROM (items INNER JOIN transactions ON items.transaction_id = transactions.id) \n" +
                "WHERE(items.created_by_email = :ownerEmail AND transactions.sale_id = :id)) AS output1,\n" +
                "(SELECT users.name AS owner from users WHERE users.email = :userEmail) AS output2";

        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        sqlQuery.setParameter("ownerEmail", sellerEmail);
        sqlQuery.setParameter("id", saleId);
        sqlQuery.setParameter("userEmail", sellerEmail);

        List<SqlRow> report = sqlQuery.findList();

        return report;

    }

    /**
     * Build a query to  report the total of items sold by a specific seller
     * @param email the email of the seller
     * @param id the sale id
     * @return the total by the seller
     */
    public Double reportTotal(String email, int id) {

        String sql = "SELECT SUM(items.price) FROM (items INNER JOIN transactions ON items.transaction_id = transactions.id)\n" +
                "WHERE (items.created_by_email = :email AND transactions.sale_id = :id)";
        Double val = Ebean.createSqlQuery(sql).setParameter("email", email).setParameter("id", id).findUnique().getDouble("sum");
        if (val == null) {
            return 0.0;
        } else {
            return val;
        }
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
    
    /**
     * Closes a Sale
     */
    public void close() {
        this.isClosed = true;
    }
    
    /**
     * Checks to see if a Sale is closed
     * @return boolean true if the sale was closed, false otherwise
     */
    public boolean isClosed() {
        return this.isClosed;
    }
}
