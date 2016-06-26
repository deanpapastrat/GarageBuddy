package controllers;

import lib.GBController;
import play.data.Form;
import play.mvc.*;
import models.*;
import views.html.sales.*;
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

    @Security.Authenticated(Secured.class)
    public Result show(int id) {
        // below is a temp list of items just so I can have it communicate and set up the view. I really need
        // a method to access the current sale and get it's items.
        Sale sale = Sale.findById(id);

        Form<Item> searchForm = modelForm(Item.class);
        Form<Item> addItemForm = modelForm(Item.class);

        return ok(views.html.sales.show.render(sale.name, "Sales", sale, sale.items, searchForm, addItemForm));
    }
}