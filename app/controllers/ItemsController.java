package controllers;

import lib.GBController;
import play.data.Form;
import play.mvc.*;
import models.*;
import views.html.items.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Manages endpoints
 *
 * @author Dean Papastrat
 */
public class ItemsController extends GBController {

    /**
     * Creates an item in the database.
     * @return a webpage representing that item
     */
    @Security.Authenticated(Secured.class)
    public Result create(int saleId) {
        Sale sale = Sale.findById(saleId);
        return ok(views.html.items.create.render(sale, emptyModelForm(Item.class)));
    }

    /**
     * Validates item form and creates an item with the provided data
     * @return redirect to sale items index or renders item form with validation errors
     */
    @Security.Authenticated(Secured.class)
    public Result postCreate(int saleId) {
        Sale sale = Sale.findById(saleId);
        Form<Item> itemForm = modelForm(Item.class);

        if (itemForm.hasErrors()) {
            return badRequest(views.html.items.create.render(sale, itemForm));
        } else {
            Item item = itemForm.get();
            item.createdBy = currentUser();
            item.addToSale(sale); // Autosaves
            return redirect("/sales/" + Integer.toString(item.sale.id) + "/items");
        }
    }

    /**
     * Edits an item in the database.
     * @return a webpage representing that sale
     */
    @Security.Authenticated(Secured.class)
    public Result edit(int id) {
        Item item = Item.findById(id);
        return ok(views.html.items.edit.render(item, modelForm(item)));
    }

    /**
     * Validates item form and edits an item with the provided data
     * @return redirect to item page or renders item form with validation errors
     */
    @Security.Authenticated(Secured.class)
    public Result postEdit(int id) {
        Item item = Item.findById(id);
        Form<Item> itemForm = modelForm(Item.class);

        if (itemForm.hasErrors()) {
            return badRequest(views.html.items.edit.render(item, itemForm));
        } else {
            item.name = formParam("name");
            item.description = formParam("description");
            item.minprice = Double.parseDouble(formParam("minprice"));
            item.price = Double.parseDouble(formParam("price"));
            item.save();
            return redirect("/sales/" + Integer.toString(item.sale.id) + "/items");
        }
    }

    /**
     * Provides a confirmation for deletion of an item from the database.
     * @return a page showing the confirmation
     */
    @Security.Authenticated(Secured.class)
    public Result delete(int id) {
        Item item = Item.findById(id);
        return ok(views.html.items.delete.render(item));
    }

    /**
     * Deletes an item from the database.
     * @return a redirect to sale's item index
     */
    @Security.Authenticated(Secured.class)
    public Result postDelete(int id) {
        Item item = Item.findById(id);
        int saleId = item.sale.id;
        item.delete();
        return redirect("/sales/" + Integer.toString(saleId) + "/items");
    }


    /**
     * Shows a tag for the individual item
     * @param id of item
     * @return the print tag of the individual item
     */
    @Security.Authenticated(Secured.class)
    public Result tag(int id) {
        Item item = Item.findById(id);
        List<Item> items = new ArrayList<Item>();
        items.add(item);
        return ok(views.html.Tag.index.render(items));
    }


}