package models;

import javax.persistence.*;
import com.avaje.ebean.Model;
import play.data.validation.*;
import java.text.DecimalFormat;

/**
 * Represents an item in GarageBuddy
 *
 * @author Taj Gillani and Dean Papastrat
 * @version 1.0.0
 */
@Entity
@Table(name="items")
public class Item extends Model {

    @Id
    public int id;  // integer id of the item
    
    @ManyToOne(optional=false)
    public User createdBy;  // user who is Selling the item

    @ManyToOne
    public User soldBy; // user who physically sold the item to a customer

    @ManyToOne
    @JoinColumn(name="transaction_id", referencedColumnName = "id")
    public Transaction transaction; // user who physically sold the item to a customer

    @ManyToOne
    @JoinColumn(name="sale_id", referencedColumnName = "id")
    public Sale sale;  // user who is Selling the item

    @Constraints.Required
    public String name; // name of the item

    public String description; // description of the item

    @Constraints.Required
    public double price; // marked price of the item
    
    public double minprice; // absolute minimum bargain price for the item

    public boolean purchased = false; // self explanatory
    public double soldFor;  // amount that the item actually sold for
    public static final Finder<String, Item> find = new Finder<String, Item>(Item.class);

    /* CONSTRUCTORS & EQUIVALENCY */
    /**
     * @param creator the user adding the item to GarageBuddy
     * @param name name of the item
     * @param price sell price of the item
     */
    public Item(User creator, String name, double price) {
        this(creator, name, price, "No Description");
    }

    /**
     * @param creator the user adding the item to GarageBuddy
     * @param name name of the item
     * @param price sell price of the item
     * @param description a brief description of the item
     */
    public Item(User creator, String name, double price, String description) {
        this.createdBy = creator;
        this.name = name;
        this.price = price;
        this.minprice = price;
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!Item.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Item other = (Item) obj;

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

        final Item other = (Item) obj;

        if (this.createdBy.equals(other.createdBy) && this.name.equals(other.name)
                && this.price == other.price && this.minprice == other.minprice
                && this.description.equals(other.description)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    /* SALE INTERACTIONS */

    /**
     * Adds an item to a sale
     * @param saleId integer of the sale's ID
     * @return whether or not the record was modified
     */
    public boolean addToSale(int saleId) {
        return addToSale(Sale.findById(saleId));
    }

    /**
     * Adds an item to a sale
     * @param saleToAddTo sale to add the item to
     * @return whether or not the record was modified
     */
    public boolean addToSale(Sale saleToAddTo) {
        if (saleToAddTo == null) {
            throw new IllegalArgumentException("Sale to add to must be non-null.");
        }
        if (this.sale != null && this.sale.equals(saleToAddTo)) {
            return false;
        } else {
            this.sale = saleToAddTo;
            save();
            return true;
        }
    }

    /**
     * Removes item from any sale
     * @return whether or not the record was modified
     */
    public boolean removeFromSale() throws ItemPurchasedException {
        if (purchased) {
            throw new ItemPurchasedException(this.id);
        }
        if (this.sale == null) {
            return false;
        } else {
            this.sale = null;
            save();
            return true;
        }
    }

    /**
     * Removes item from the designated sale, if it is currently part of the sale
     * @param saleId id of the sale to remove the item from
     * @return whether or not the record was modified
     */
    public boolean removeFromSale(int saleId) throws ItemPurchasedException {
        return removeFromSale(Sale.findById(saleId));
    }

    /**
     * Removes item from the designated sale, if it is currently part of the sale
     * @param saleToRemoveFrom sale to remove the item from
     * @return whether or not the record was modified
     */
    public boolean removeFromSale(Sale saleToRemoveFrom) throws ItemPurchasedException {
        if (saleToRemoveFrom == null) {
            throw new IllegalArgumentException("Sale to remove from must be non-null.");
        }
        if (this.sale.equals(saleToRemoveFrom)) {
            return removeFromSale();
        } else {
            return false;
        }
    }

    /* FORMATTERS */

    public String formattedPrice() {
        return formatPrice(price);
    }
    public String formattedTagPrice() { return formatTagPrice(price); }

    public String formattedMinprice() {
        return formatPrice(minprice);
    }

    static String formatPrice(Double number) {
        DecimalFormat df = new DecimalFormat("#.00");
        return "$" + df.format(number);
    }

    static String formatTagPrice(Double number) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(number);
    }

    /* PREBUILT QUERIES */

    /**
     * Returns an item from an ID
     * @param id id of item we want to find
     * @return an item with the specified id
     */
    public static Item findById(Integer id) {
        return Item.find.byId(id.toString());
    }

    /* EXCEPTIONS */

    /**
     * Represents the error thrown when a user tries to modify a purchased item, which should
     * not be modified for auditing purposes.
     *
     * @author Dean Papastrat
     */
    public static class ItemPurchasedException extends Exception {
        public ItemPurchasedException(int id) {
            super("Item: " + Integer.toString(id) + " cannot be modified;"
                    + " it has been purchased.");
        }
    }
}