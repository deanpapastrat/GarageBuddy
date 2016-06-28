package models;

import javax.persistence.*;

import com.avaje.ebean.*;
import org.joda.time.DateTime;
import play.data.validation.*;
import play.data.format.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a transaction between a customer and an item
 *
 * @author Dean Papastrat
 * @version 1.0.0
 */
@Entity
@Table(name="transactions")
public class Transaction extends Model {

    /* ATTRIBUTES */

    @Id
    public int id;

    @Constraints.Required @Formats.DateTime(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public DateTime createdAt = new DateTime();

    @Constraints.Required
    public String customerName;

    @Constraints.Required
    public Double value;

    @ManyToOne @Constraints.Required
    public User seller;

    @ManyToOne
    public User customer;

    @OneToMany(mappedBy = "transaction")
    public List<Item> items;

    public int numItems = 0;

    public static final Finder<String, Transaction> find = new Finder<>(Transaction.class);

    /* CONSTRUCTORS & EQUIVALENCY */

    /**
     * Create a transaction
     *
     * @param seller the person checking out the customer
     */
    public Transaction(User seller) {
        this(seller, new ArrayList<Item>());
    }

    /**
     * Create a transaction
     * @param seller the person checking out the customer
     * @param items items the customer is buying
     */
    public Transaction(User seller, List<Item> items) {
        this.seller = seller;
        this.numItems = addItems(items);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!Sale.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Transaction other = (Transaction) obj;

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

        final Transaction other = (Transaction) obj;

        if (this.createdAt.equals(other.createdAt) && this.customerName.equals(other.customerName)
                && this.value.equals(other.value) && this.seller.equals(other.seller)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    /* ADD AND REMOVE ITEMS */

    /**
     * Adds an item to the transaction
     * @param item item to add
     */
    private void addItem(Item item) {
        item.transation = this;
        item.save();
    }

    /**
     * Adds an item to the transaction
     * @param item item to add
     * @param updateCounts whether to update numItems or not
     */
    public void addItem(Item item, boolean updateCounts) {
        addItem(item);
        if (updateCounts) {
            this.numItems += 1;
            save();
        }
    }

    /**
     * Adds specified items to the transaction
     *
     * @param itemsToAdd a list of items to add to the transaction
     * @return how many items were added
     */
    private int addItems(List<Item> itemsToAdd) {
        String itemIds = "";
        for (int i = 0; i < itemsToAdd.size(); i++) {
            if (i != 0) {
                itemIds += ",";
            }

            itemIds += Integer.toString(itemsToAdd.get(i).id);
        }
        String sql = "UPDATE transactions SET transaction_id = :id WHERE id IN :items";
        SqlUpdate update = Ebean.createSqlUpdate(sql);
        update.setParameter("id", this.id);
        update.setParameter("items", itemIds);
        return update.execute();
    }

    /**
     * Adds specified items to the transaction
     * @param updateCounts whether to update numItems or not
     */
    public void addItems(List<Item> itemsToAdd, boolean updateCounts) {
        if (updateCounts) {
            this.numItems = removeItems(itemsToAdd);
            save();
        } else {
            removeItems(itemsToAdd);
        }
    }

    /**
     * Removes an item from the transaction
     * @param item item to remove
     */
    private void removeItem(Item item) {
        item.transation = null;
        item.save();
    }

    /**
     * Removes an item from the transaction
     * @param item item to remove
     * @param updateCounts whether to update numItems or not
     */
    public void removeItem(Item item, boolean updateCounts) {
        removeItem(item);
        if (updateCounts) {
            this.numItems -= 1;
            save();
        }
    }

    /**
     * Removes the specified items from the transaction
     *
     * @param itemsToRemove a list of items to remove from the transaction
     * @return how many items were removed
     */
    private int removeItems(List<Item> itemsToRemove) {
        String itemIds = "";
        for (int i = 0; i < itemsToRemove.size(); i++) {
            if (i != 0) {
                itemIds += ",";
            }

            itemIds += Integer.toString(itemsToRemove.get(i).id);
        }
        String sql = "UPDATE transactions SET transaction_id = :id WHERE transaction_id = :transactionId";
        SqlUpdate update = Ebean.createSqlUpdate(sql);
        update.setParameter("id", null);
        update.setParameter("transactionId", this.id);
        return update.execute();
    }

    /**
     * Removes the specified items from the transaction
     * @param updateCounts whether to update numItems or not
     */
    public void removeItems(List<Item> itemsToRemove, boolean updateCounts) {
        if (updateCounts) {
            this.numItems -= removeItems(itemsToRemove);
            save();
        } else {
            removeItems(itemsToRemove);
        }
    }

    /**
     * Removes all items from the transaction
     * @return how many items were removed
     */
    public int removeItems() {
        String sql = "UPDATE transactions SET transaction_id = :id WHERE transaction_id = :transactionId";
        SqlUpdate update = Ebean.createSqlUpdate(sql);
        update.setParameter("id", null);
        update.setParameter("transactionId", this.id);
        return update.execute();
    }

    /**
     * Removes all items from the transaction
     * @param updateCounts whether to update numItems or not
     */
    public void removeItems(boolean updateCounts) {
        if (updateCounts) {
            this.numItems = removeItems();
            save();
        } else {
            removeItems();
        }
    }

    /* PREBUILT QUERIES */

    /**
     * Returns a transaction from an ID
     * @param id id of transaction we want to find
     * @return a transaction with the specified id
     */
    public static Transaction findById(Integer id) {
        return Transaction.find.byId(id.toString());
    }

    /**
     * Builds a query for items related to this sale
     *
     * @return an expression list for items that have this sale ID
     */
    public ExpressionList<Item> findItems() {
        return Item.find.where().eq("transaction_id", this.id);
    }
}