package controllers;

import lib.GBController;
import play.data.Form;
import play.mvc.*;
import models.*;
import views.html.items.*;

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
     * Edits a sale in the database.
     * @return a webpage representing that sale
     */
    @Security.Authenticated(Secured.class)
    public Result edit(int id, int saleId) {
        Item item = Item.findById(id);
        return ok(views.html.items.edit.render(item, modelForm(item)));
    }

    /**
     * Validates sale form and edits a sale with the provided data
     * @return redirect to sale page or renders sale form with validation errors
     */
    @Security.Authenticated(Secured.class)
    public Result postEdit(int id, int saleId) {
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
}