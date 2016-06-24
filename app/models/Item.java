package models;

import com.avaje.ebean.Model;
import play.data.validation.*;

/**
 * An Item
 * 2222ET 23 07 2016
 * consider adding Images at a later date
 */

public class User extends Model {

    @Id @Constraints.id @Constraints.Required
    public int id;  // integer id of the item
    
    @Constraints.Required
    public User belongsTo;  // user who is Selling the item

    @Constraints.Required
    public String name; // name of the item

    public String desc; // description of the item

    @Constraints.Required
    public double price; // marked price of the item
    
    public double minprice; // absolute minimum bargain price for the item

    public boolean hasBeenSold; // self explanatory
    public double soldFor;  // amount that the item actually sold for
    public User soldBy; // user who physically sold the item to a customer


    /**
     * @param seller the user adding the item to garagebuddy
     * @param name name of the item
     * @param price sell price of the item
     */
    public Item(User seller, String name, double price) {
        belongsTo = seller;
        this.name = name;
        this.price = price;
        
        minprice = price;
        hasBeenSold = false;
        desc = "No description";
    }

    
    
    /**
     * @param description the description to set for the Item
     */
    public void setdescription(String description) {
        this.desc = description;
    }
    
    /**
     * @param minprice the minimum bargain price to set for the Item
     */
    public void setminprice(double minprice) {
        this.minprice = minprice;
    }
 
    /**
     * @param sold the hasBeenSold to set for the Item
     */
    public void sethasBeenSold(boolean sold) {
        this.hasBeenSold = sold;
    }
 
    /**
     * @param soldFor the soldFor to set for the Item
     */
    public void setsoldFor(double soldFor) {
        this.soldFor = soldFor;
    }
 
    /**
     * @param soldBy the soldBy to set for the Item
     */
    public void setsoldBy(User soldBy) {
        this.soldBy = soldBy;
    }

}