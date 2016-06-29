package controllers;

import lib.GBController;
import play.data.Form;
import play.mvc.*;
import models.*;
import views.html.sales.*;
import views.html.Tag.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages endpoints
 *
 * @author Dean Papastrat and Alex Woods
 */
public class SalesController extends GBController {

    /**
     * Returns an index of all sales
     * @return sale index page
     */
    @Security.Authenticated(Secured.class)
    public Result index() {
        return ok(views.html.sales.index.render("Sales", "Sales", Sale.find.all(), currentUser()));
    }

    /**
     * Creates a sale in the database.
     * @return a webpage representing that sale
     */
    @Security.Authenticated(Secured.class)
    public Result create() {
        return ok(views.html.sales.create.render("New Sale", "Sales", emptyModelForm(Sale.class)));
    }

    /**
     * Validates sale form and creates a sale with the provided data
     * @return redirect to sale page or renders sale form with validation errors
     */
    @Security.Authenticated(Secured.class)
    public Result postCreate() {
        Form<Sale> saleForm = modelForm(Sale.class);

        if (saleForm.hasErrors()) {
            return badRequest(views.html.sales.create.render("New Sale", "Sales", saleForm));
        } else {
            Sale sale = saleForm.get();
            sale.addUser(currentUser().email, Sale.Role.SALE_ADMIN);
            sale.save();
            return redirect("/sales/" + Integer.toString(sale.id));
        }
    }

    /**
     * Edits a sale in the database.
     * @return a webpage representing that sale
     */
    @Security.Authenticated(Secured.class)
    public Result edit(int id) {
        Sale sale = Sale.findById(id);
        return ok(views.html.sales.edit.render(sale.name, "Sales", sale, modelForm(sale)));
    }

    /**
     * Validates sale form and edits a sale with the provided data
     * @return redirect to sale page or renders sale form with validation errors
     */
    @Security.Authenticated(Secured.class)
    public Result postEdit(int id) {
        Sale sale = Sale.findById(id);
        Form<Sale> saleForm = modelForm(Sale.class);

        if (saleForm.hasErrors()) {
            return badRequest(views.html.sales.edit.render(sale.name, "Sales", sale, saleForm));
        } else {
            sale.name = formParam("name");
            sale.setFormattedStartDate(formParam("startDate"));
            sale.setFormattedEndDate(formParam("endDate"));
            sale.save();
            return redirect("/sales/" + Integer.toString(sale.id));
        }
    }

    /**
     * Shows the program dashboard
     *
     * @param id
     * @return
     */
    @Security.Authenticated(Secured.class)
    public Result show(int id) {
        // below is a temp list of items just so I can have it communicate and set up the view. I really need
        // a method to access the current sale and get it's items.
        Sale sale = Sale.findById(id);
        List<Item> queryItems;
        String query = formParam("q");

        if (query != null && !query.isEmpty()) {
            queryItems = sale.findItems().icontains("name", query).findList();
        } else {
            queryItems = sale.items;
        }

        return ok(views.html.sales.show.render(sale.name, "Sales", sale, queryItems, query));
    }

    /**
     * Provides a confirmation for deletion of a sale from the database.
     * @return a page showing the confirmation
     */
    @Security.Authenticated(Secured.class)
    public Result delete(int id) {
        Sale sale = Sale.findById(id);
        return ok(views.html.sales.delete.render(sale));
    }

    /**
     * Deletes a sale from the database.
     * @return a redirect to sales index
     */
    @Security.Authenticated(Secured.class)
    public Result postDelete(int id) {
        Sale sale = Sale.findById(id);
        sale.findItems().delete();
        sale.delete();
        return redirect("/sales");
    }

    /**
     * @param a list of items
     * @return a page of print tags for a given set of items
     */
    @Security.Authenticated(Secured.class)
    public Result getAllTags(int id) {
        Sale sale = Sale.findById(id);
        List<Item> queryItems;
        String query = formParam("q");

        if (query != null && !query.isEmpty()) {
            queryItems = sale.findItems().icontains("name", query).findList();
        } else {
            queryItems = sale.items;
        }

        return ok(views.html.Tag.index.render(queryItems));
    }




}











