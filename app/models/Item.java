package models;

import javax.persistence.*;
import com.avaje.ebean.Model;
import play.data.validation.*;

/**
 * Represents an item in GarageBuddy
 *
 * @author Taj Gillani and Dean Papastrat
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
    @JoinColumn(name="sale_id", referencedColumnName = "sale_id")
    public Sale sale;  // user who is Selling the item

    @Constraints.Required
    public String name; // name of the item

    public String description; // description of the item

    @Constraints.Required
    public double price; // marked price of the item
    
    public double minprice; // absolute minimum bargain price for the item

    public boolean purchased = false; // self explanatory
    public double soldFor;  // amount that the item actually sold for


    /**
     * @param creator the user adding the item to GarageBuddy
     * @param name name of the item
     * @param price sell price of the item
     */
    public Item(User creator, String name, double price) {
        this.createdBy = creator;
        this.name = name;
        this.price = price;
        this.minprice = price;
        this.description = "No description";
    }
}