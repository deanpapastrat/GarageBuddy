package models;

import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import com.avaje.ebean.annotation.Sql;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lib.Formatter;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.DbJsonB;
//import play.Logger;
import play.Logger;
import play.data.format.*;
import play.data.validation.*;
import play.libs.Json;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

        private int permit;

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
         * @param permit the permission level as a number
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
        this.save();
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
        return Arrays.asList("Guest", "Book Keeper", "Cashier",
                "Clerk", "Seller");
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
    public static Sale findById(final Integer id) {
        return Sale.FIND.byId(id.toString());
    }


    /**
     * Find users in this sale that can sell items
     * @return the list of sellers
     */


    public List<User> findSellers() {

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
                "items.description,items.price, items.sold_for, transactions.created_at AS soldat,\n" +
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

    /**
     * IMPORTANT: THIS IS VULNERABLE TO SQL INJECTION. DO NOT ALLOW
     * USER INPUT TO BE SENT TO THIS FUNCTION!!!
     *
     * Returns SQL rows aggregated and grouped by date
     *
     * @param selector what the query should select along with date
     * @return a list of SQL rows
     */
    private final List<SqlRow> groupedByDay(String selector) {
        String sql = "SELECT " + selector + ", date_trunc('day', CREATED_AT) "
                + " as date FROM transactions WHERE sale_id = :id "
                + "GROUP BY date_trunc('day', CREATED_AT);";
        List<SqlRow> rows = Ebean.createSqlQuery(sql).setParameter("id", id)
                .findList();
        return rows;
    }

    /**
     * Maps date to number of transactions made on that day
     *
     * @return map of dates and number of transactions
     */
    public final Map<String, Integer> transactionsPerDay() {
        Map<String, Integer> datesMap = new HashMap<>();
        for (SqlRow row : groupedByDay("count(*)")) {
            datesMap.put(row.getString("date"), row.getInteger("count"));
        }
        return datesMap;
    }

    /**
     * Maps date to number of transactions made since the sale started
     *
     * @return map of dates and total num of transactions to date
     */
    public final Map<String, Integer> transactionsOverTime() {
        Map<String, Integer> datesMap = new HashMap<>();
        int count = 0;
        for (SqlRow row : groupedByDay("count(*)")) {
            count += row.getInteger("count");
            datesMap.put(row.getString("date"), count);
        }
        return datesMap;
    }

    /**
     * Maps date to total revenue made on that day
     *
     * @return map of dates and total revenue
     */
    public final Map<String, Double> revenuePerDay() {
        Map<String, Double> datesMap = new HashMap<>();
        for (SqlRow row : groupedByDay("sum(VALUE)")) {
            datesMap.put(row.getString("date"), row.getDouble("sum"));
        }
        return datesMap;
    }

    /**
     * Maps date to number of transactions made since the sale started
     *
     * @return map of dates and total num of transactions to date
     */
    public final Map<String, Double> revenueOverTime() {
        Map<String, Double> datesMap = new HashMap<>();
        Double sum = 0.0;
        for (SqlRow row : groupedByDay("sum(VALUE)")) {
            sum += row.getInteger("sum");
            datesMap.put(row.getString("date"), sum);
        }
        return datesMap;
    }

    /**
     * Maps date to total revenue made on that day
     *
     * @return map of dates and total revenue
     */
    public final Map<String, Double> itemsPerDay() {
        Map<String, Double> datesMap = new HashMap<>();
        for (SqlRow row : groupedByDay("sum(NUM_ITEMS)")) {
            datesMap.put(row.getString("date"), row.getDouble("sum"));
        }
        return datesMap;
    }

    /**
     * Maps date to number of transactions made since the sale started
     *
     * @return map of dates and total num of transactions to date
     */
    public final Map<String, Double> itemsOverTime() {
        Map<String, Double> datesMap = new HashMap<>();
        Double sum = 0.0;
        for (SqlRow row : groupedByDay("sum(NUM_ITEMS)")) {
            sum += row.getInteger("sum");
            datesMap.put(row.getString("date"), sum);
        }
        return datesMap;
    }

    /* JSON Stuff */

    /**
     * Converts stats into a JSON object
     * @return a json object with all stats
     */
    public final JsonNode statsJson() {
        ObjectNode json = Json.newObject();
        json.putPOJO("itemsPerDay", itemsPerDay());
        json.putPOJO("itemsOverTime", itemsOverTime());
        json.putPOJO("revenuePerDay", revenuePerDay());
        json.putPOJO("revenueOverTime", revenueOverTime());
        json.putPOJO("transactionsPerDay", transactionsPerDay());
        json.putPOJO("transactionsOverTime", transactionsOverTime());
        return json;
    }
}
