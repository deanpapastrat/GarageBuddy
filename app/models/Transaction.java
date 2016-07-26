package models;

import javax.persistence.*;

import com.avaje.ebean.*;
import lib.Formatter;
import play.data.validation.*;
import play.data.format.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a transaction between a customer and an item.
 *
 * @author Dean Papastrat
 * @version 1.0.2
 */
@Entity
@Table(name="transactions")
public class Transaction extends Model {

    /* ATTRIBUTES */

    @Id
    public int id;

    @Formats.DateTime(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public LocalDateTime createdAt = LocalDateTime.now();


    @Constraints.Required
    public String customerName;

    @Column(name="value", columnDefinition="decimal default '0.00'")
    public Double value;

    @ManyToOne
    public User seller;

    @ManyToOne
    public User customer;

    @ManyToOne
    @JoinColumn(name="sale_id", referencedColumnName = "id")
    public Sale sale;

    @OneToMany(mappedBy = "transaction")
    public List<Item> items;

    public int numItems = 0;

    public static final Finder<String, Transaction> find = new Finder<>(Transaction.class);

    /* CONSTRUCTORS & EQUIVALENCY */

    /**
     * Create a transaction.
     *
     * @param seller the person checking out the customer
     */
    public Transaction(Sale sale, User seller) {
        this(sale, seller, new ArrayList<Item>());
    }

    /**
     * Create a transaction.
     * @param seller the person checking out the customer
     * @param items items the customer is buying
     */
    public Transaction(Sale sale, User seller, List<Item> items) {
        this.sale = sale;
        this.seller = seller;
        this.createdAt = LocalDateTime.now();
        this.numItems = addItems(items);
        updateItemValues(false);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!Sale.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Transaction other = (Transaction) obj;

        return this.id == other.id;
    }

    /**
     * Checks if all properties of the object are equivalent.
     * @param obj an object to compare
     * @return if they are equivalent or not
     */
    public final boolean matches(Object obj) {
        if (!this.equals(obj)) {
            return false;
        }

        final Transaction other = (Transaction) obj;

        return this.createdAt.equals(other.createdAt)
                && this.customerName.equals(other.customerName)
                && this.value.equals(other.value)
                && this.seller.equals(other.seller);
    }

    @Override
    public final int hashCode() {
        return this.id;
    }

    /* ADD AND REMOVE ITEMS */

    /**
     * Adds an item to the transaction.
     * @param item item to add
     */
    private void addItem(Item item) {
        item.transaction = this;
        item.purchased = true;
        item.save();
    }

    /**
     * Adds an item to the transaction.
     * @param item item to add
     * @param updateCounts whether to update numItems or not
     */
    public void addItem(Item item, boolean updateCounts) {
        addItem(item);
        if (updateCounts) {
            this.numItems += 1;
            this.value += item.price;
            save();
        }
    }

    /**
     * Adds specified items to the transaction.
     *
     * @param itemsToAdd a list of items to add to the transaction
     * @return how many items were added
     */
    private int addItems(List<Item> itemsToAdd) {
        if (itemsToAdd.size() == 0) {
            return 0;
        }

//        String itemIds = "";
//        for (int i = 0; i < itemsToAdd.size(); i++) {
//            if (i != 0) {
//                itemIds = (new StringBuffer()).append(itemIds).append(",").toString();
//            }
//
//            itemIds = (new StringBuffer()).append(itemIds).append(Integer.toString(itemsToAdd.get(i).id)).toString();
//        }

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < itemsToAdd.size(); i++) {
            if (i != 0) {
                buf.append(',');
            }
            buf.append(Integer.toString(itemsToAdd.get(i).id));
        }
        String itemIds = buf.toString();

        String sql = "UPDATE items SET transaction_id = :id WHERE id IN :items;";
        SqlUpdate update = Ebean.createSqlUpdate(sql);
        update.setParameter("id", this.id);
        update.setParameter("items", itemIds);
        return update.execute();
    }

    /**
     * Adds specified items to the transaction.
     * @param updateCounts whether to update numItems or not
     */
    public final void addItems(List<Item> itemsToAdd, boolean updateCounts) {
        if (updateCounts) {
            this.numItems = addItems(itemsToAdd);
            updateItemValues(false);
            save();
        } else {
            addItems(itemsToAdd);
        }
    }

    /**
     * Removes an item from the transaction.
     * @param item item to remove
     */
    private void removeItem(Item item) {
        item.transaction = null;
        item.purchased = false;
        item.save();
    }

    /**
     * Removes an item from the transaction.
     * @param item item to remove
     * @param updateCounts whether to update numItems or not
     */
    public void removeItem(Item item, boolean updateCounts) {
        removeItem(item);
        if (updateCounts) {
            this.numItems -= 1;
            this.value -= item.price;
            save();
        }
    }

    /**
     * Removes the specified items from the transaction.
     *
     * @param itemsToRemove a list of items to remove from the transaction
     * @return how many items were removed
     */
    private int removeItems(List<Item> itemsToRemove) {
//        String itemIds = "";
//        for (int i = 0; i < itemsToRemove.size(); i++) {
//            if (i != 0) {
//                itemIds = (new StringBuilder()).append(itemIds).append(",").toString();
//            }
//
//            itemIds = (new StringBuilder()).append(itemIds).append(Integer.toString(itemsToRemove.get(i).id)).toString();
//        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < itemsToRemove.size(); i++) {
            if (i != 0) {
                buf.append(',');
            }
            buf.append(Integer.toString(itemsToRemove.get(i).id));
        }
        String itemIds = buf.toString();

        String sql = "UPDATE items SET transaction_id = :id, purchased = true WHERE transaction_id = :transactionId;";
        SqlUpdate update = Ebean.createSqlUpdate(sql);
        update.setParameter("id", null);
        update.setParameter("transactionId", this.id);
        return update.execute();
    }

    /**
     * Removes the specified items from the transaction.
     * @param updateCounts whether to update numItems or not
     */
    public final void removeItems(List<Item> itemsToRemove, boolean updateCounts) {
        if (updateCounts) {
            this.numItems -= removeItems(itemsToRemove);
            updateItemValues(false);
            save();
        } else {
            removeItems(itemsToRemove);
        }
    }

    /**
     * Removes all items from the transaction.
     * @return how many items were removed
     */
    public final int removeItems() {
        String sql = "UPDATE items SET transaction_id = :id, purchased = false WHERE transaction_id = :transactionId;";
        SqlUpdate update = Ebean.createSqlUpdate(sql);
        update.setParameter("id", null);
        update.setParameter("transactionId", this.id);
        return update.execute();
    }

    /**
     * Removes all items from the transaction.
     * @param updateCounts whether to update numItems or not
     */
    public final void removeItems(boolean updateCounts) {
        if (updateCounts) {
            this.numItems = removeItems();
            updateItemValues(false);
            save();
        } else {
            removeItems();
        }
    }

    /* AGGREGATE UPDATERS */

    /**
     * Sums the values of the items on this transaction.
     * @return live sum of all item prices
     */
    private Double sumItemValues() {
        String sql = "SELECT SUM(price) FROM items WHERE transaction_id = :id;";
        Double val = Ebean.createSqlQuery(sql).setParameter("id", this.id).findUnique().getDouble("sum");
        if (val == null) {
            return 0.0;
        } else {
            return val;
        }
    }

    /**
     * Updates the value of the transaction based on items linked to it.
     * @param save whether or not to save transaction
     */
    public final void updateItemValues(boolean save) {
        this.value = sumItemValues();
        if (save) {
            save();
        }
    }

    /* FORMATTERS */

    /**
     * Returns the value in a human-readable string format.
     *
     * @return the value, with a dollar sign and to 2 decimal places
     */
    public final String formattedValue() {
        return Formatter.currency(value);
    }

    /**
     * Returns the time the transaction was created in a human-readable string format.
     *
     * @return the time in the format mm/dd/yy H:mma
     */
    public final String formattedCreatedAt() {
        return Formatter.time(createdAt);
    }

    /* PREBUILT QUERIES */

    /**
     * Returns a transaction from an ID.
     * @param id id of transaction we want to find
     * @return a transaction with the specified id
     */
    public static Transaction findById(Integer id) {
        return Transaction.find.byId(id.toString());
    }

    /**
     * Builds a query for items related to this sale.
     *
     * @return an expression list for items that have this sale ID
     */
    public final ExpressionList<Item> findItems() {
        return Item.FIND.where().eq("transaction_id", this.id);
    }
}