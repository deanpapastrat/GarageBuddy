package controllers;

import lib.GBController;
import play.data.Form;
import play.mvc.*;
import models.*;
import views.html.sales.*;
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
     * @param id id of the sale
     * @return a webpage showing the items
     */
    @Security.Authenticated(Secured.class)
    public Result show(int id) {
        Sale sale = Sale.findById(id);
        List<Item> queryItems = queryItems(sale.findItems(), "name", "name", sale.items);
        return ok(views.html.sales.show.render(sale.name, "Sales", sale, queryItems, queryString()));
    }

    /**
     * Show the financial report for a sale
     * @param id the id of the sale
     * @return a webpage showing the financial report
     */
    @Security.Authenticated(Secured.class)
    public Result report(int id) {
        Sale sale = Sale.findById(id);
        List<Transaction> trans = sale.transactions;

        return ok(views.html.sales.report.render(sale.name, "Financial Report", sale, trans));
    }


    /**
     * Provides a confirmation for deletion of a sale from the database.
     * @return a page showing the confirmation
     */
    @Security.Authenticated(Secured.class)
    public Result sell(int id) {
        Sale sale = Sale.findById(id);
        return ok(views.html.sales.sell.render(sale));
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
     * Retrieves all tags for a sale in a printable format
     * @param id a list of items
     * @return a page of print tags for a given set of items
     */
    @Security.Authenticated(Secured.class)
    public Result tags(int id) {
        Sale sale = Sale.findById(id);
        List<Item> tagItems = queryItems(sale.findItems(), "name", "name", sale.items);
        return ok(views.html.sales.tags.render(tagItems));
    }
}
