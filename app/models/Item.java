package models;

import javax.persistence.*;
import com.avaje.ebean.Model;
import lib.Formatter;
import play.data.validation.*;
import java.text.DecimalFormat;

/**
 * Represents an item in GarageBuddy.
 *
 * @author Taj Gillani and Dean Papastrat
 * @version 1.0.0
 */
@Entity
@Table(name = "items")
public class Item extends Model {

    /**
     * Integer id of the item.
     */
    @Id
    public int id;

    /**
     * User who is selling the item.
     */
    @ManyToOne(optional = false)
    public User createdBy;

    /**
     * User who physically sold the item to a customer.
     */
    @ManyToOne
    public User soldBy;


    /**
     * Transaction that the item belongs.
     */
    @ManyToOne
    @JoinColumn(name = "transaction_id", referencedColumnName = "id")
    public Transaction transaction;

    /**
     * Sale item belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "sale_id", referencedColumnName = "id")
    public Sale sale;

    /**
     * Name of the item.
     */
    @Constraints.Required
    public String name;

    /**
     * Description of the item.
     */
    public String description;

    /**
     * Marked price of the item.
     */
    @Constraints.Required
    public double price;

    /**
     * Absolute minimum bargain price for the item.
     */
    public double minprice;

    /**
     * Purchase status of item.
     */
    public boolean purchased = false;

    /**
     * Amount that the item actually sold for.
     */
    public double soldFor;  // amount that the item actually sold for

    /**
     * Helper to power finder functions down below.
     */
    public static final Finder<String, Item> FIND =
            new Finder<String, Item>(Item.class);

    /* CONSTRUCTORS & EQUIVALENCY */
    /**
     * Creates an item.
     *
     * @param pCreator the user adding the item to GarageBuddy
     * @param pName name of the item
     * @param pPrice sell price of the item
     */
    public Item(final User pCreator, final String pName, final double pPrice) {
        this(pCreator, pName, pPrice, "No Description");
    }

    /**
     * Creates an item.
     *
     * @param pCreator the user adding the item to GarageBuddy
     * @param pName name of the item
     * @param pPrice sell price of the item
     * @param pDescription a brief description of the item
     */
    public Item(final User pCreator, final String pName,
                final double pPrice, final String pDescription) {
        this.createdBy = pCreator;
        this.name = pName;
        this.price = pPrice;
        this.minprice = pPrice;
        this.description = pDescription;
    }

    /**
     * Check if two items are exactly the same.
     * @param obj
     * @return True if obj exists, and is exactly this item
     */
    @Override
    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!Item.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Item other = (Item) obj;

        return (this.id == other.id);
    }

    /**
     * Checks if all properties of the object are equivalent.
     *
     * @param obj an object to compare
     * @return true if other object exists and this and other are equal
     */
    public final boolean matches(final Object obj) {
        if (!this.equals(obj)) {
            return false;
        }

        final Item other = (Item) obj;

        return (this.createdBy.equals(other.createdBy)
                && this.name.equals(other.name)
                && this.price == other.price && this.minprice == other.minprice
                && this.description.equals(other.description));
    }

    /**
     * Gets id of this item, which is the same as its hash code.
     * @return id of this item
     */
    @Override
    public final int hashCode() {
        return this.id;
    }

    /* SALE INTERACTIONS */

    /**
     * Adds an item to a sale.
     *
     * @param saleId integer of the sale's ID
     * @return whether or not the record was modified
     */
    public final boolean addToSale(final int saleId) {
        return addToSale(Sale.findById(saleId));
    }

    /**
     * Adds an item to a sale.
     *
     * @param saleToAddTo sale to add the item to
     * @return whether or not the record was modified
     */
    public final boolean addToSale(final Sale saleToAddTo) {
        if (saleToAddTo == null) {
            throw new IllegalArgumentException("Must add to non-null Sale.");
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
     * Removes item from any sale.
     *
     * @return whether or not the record was modified
     * @throws ItemPurchasedException
     */
    public final boolean removeFromSale() throws ItemPurchasedException {
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
     * Remove item from designated sale, if it is currently part of the sale.
     *
     * @param saleId id of the sale to remove the item from
     * @return whether or not the record was modified
     * @throws ItemPurchasedException
     */
    public final boolean removeFromSale(final int saleId)
            throws ItemPurchasedException {
        return removeFromSale(Sale.findById(saleId));
    }

    /**
     * Remove item from designated sale, if it is currently part of the sale.
     *
     * @param saleToRemoveFrom sale to remove the item from
     * @return whether or not the record was modified
     * @throws ItemPurchasedException
     */
    public final boolean removeFromSale(final Sale saleToRemoveFrom)
            throws ItemPurchasedException {
        if (saleToRemoveFrom == null) {
            throw new IllegalArgumentException(
                    "Sale to remove from must be non-null.");
        }
        if (this.sale.equals(saleToRemoveFrom)) {
            return removeFromSale();
        } else {
            return false;
        }
    }

    /* FORMATTERS */

    /**
     * Returns the price in a human-readable string format.
     *
     * @return the price, with a dollar sign and to 2 decimal places
     */
    public final String formattedPrice() {
        return Formatter.currency(price);
    }

    /**
     * Returns the Tag Price in a human-readable string format.
     * @return the prince on Tag, with a dollar sign and to 2 decimal places
     */
    public final String formattedTagPrice() {
        return formatTagPrice(price);
    }

    /**
     * Returns the minimum price in a human-readable string format.
     *
     * @return the minum price, with a dollar sign and to 2 decimal places
     */
    public final String formattedMinprice() {
        return Formatter.currency(minprice);
    }

    /**
     * Format a number as a human-readable price.
     * @param number is price of item
     * @return string that is human-readable price
     */
    static String formatTagPrice(final double number) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(number);
    }

    /* PREBUILT QUERIES */

    /**
     * Returns an item from an ID.
     *
     * @param id id of item we want to find
     * @return an item with the specified id
     */
    public static Item findById(final Integer id) {
        return Item.FIND.byId(id.toString());
    }

    /* EXCEPTIONS */

    /**
     * Represents the error thrown when a user tries to modify a purchased item,
     * which should not be modified for auditing purposes.
     *
     * @author Dean Papastrat
     */
    public static class ItemPurchasedException extends Exception {
        /**
         *
         * @param id item that has already been purchased.
         */
        public ItemPurchasedException(final int id) {
            super("Item: " + Integer.toString(id) + " cannot be modified;"
                    + " it has been purchased.");
        }
    }
}
