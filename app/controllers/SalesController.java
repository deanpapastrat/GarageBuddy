package controllers;

import lib.GBController;
import play.data.Form;
import play.mvc.*;
import models.*;
import views.html.sales.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;

/**
 * Manages endpoints for sales.
 *
 * @author Dean Papastrat and Alex Woods
 */
public class SalesController extends GBController {

    /**
     * Returns an index of all sales.
     * @return sale index page
     */
    @Security.Authenticated(Secured.class)
    public final Result index() {
        List<Sale> allSales = Sale.FIND.all();
        Collections.sort(allSales,
                (o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
        return ok(views.html.sales.index.render(allSales, currentUser()));
    }

    /**
     * Creates a sale in the database.
     * @return a webpage representing that sale
     */
    @Security.Authenticated(Secured.class)
    public final Result create() {
        return ok(views.html.sales.create.render(emptyModelForm(Sale.class),
                currentUser()));
    }



    /**
     * Validates sale form and creates a sale with the provided data.
     * @return redirect to sale or renders sale form with validation errors
     */
    @Security.Authenticated(Secured.class)
    public final Result postCreate() {
        Form<Sale> saleForm = modelForm(Sale.class);

        if (saleForm.hasErrors()) {
            return badRequest(views.html.sales.create.render(saleForm,
                    currentUser()));
        } else {
            Sale sale = saleForm.get();
            sale.addUser(currentUser().email, Sale.Role.SALE_ADMIN);

            if (!sale.getEndDate().before(sale.getStartDate())) {
                flash("success", "Sale created");
                sale.save();
                return redirect("/sales/" + Integer.toString(sale.id));
            } else {
                flash("error", "The end date can't be before the start date");
                return badRequest(views.html.sales.create.render(saleForm,
                        currentUser()));
            }
        }
    }

    /**
     * Edits a sale in the database.
     *
     * @param id id of the sale
     * @return a page representing that sale
     */
    @Security.Authenticated(Secured.class)
    public final Result edit(final int id) {
        Sale sale = Sale.findById(id);
        if (sale.isClosed()) {
            flash("warning", "This sale has been closed, and can no longer"
                    + " be edited");
        }

        return ok(views.html.sales.edit.render(sale, modelForm(sale),
                currentUser()));
    }

    /**
     * Validates sale form and edits a sale with the provided data.
     *
     * @param id id of the sale
     * @return redirect to sale or renders sale form with validation errors
     */
    @Security.Authenticated(Secured.class)
    public final Result postEdit(final int id) {
        Sale sale = Sale.findById(id);
        Form<Sale> saleForm = modelForm(Sale.class);

        if (saleForm.hasErrors()) {
            return badRequest(views.html.sales.edit.render(sale, saleForm,
                    currentUser()));
        } else {
            sale.name = formParam("name");
            sale.setFormattedStartDate(formParam("startDate"));
            sale.setFormattedEndDate(formParam("endDate"));
            if (formParam("close") != null) {
                sale.close();
                flash("warning", "This sale has been closed, and can no longer"
                        + " be edited");
            }
            if (!sale.getEndDate().before(sale.getStartDate())) {
                sale.save();
                return redirect("/sales/" + Integer.toString(sale.id));
            } else {
                flash("error", "The end date can't be before the start date");
                return badRequest(views.html.sales.edit.render(sale, saleForm,
                        currentUser()));
            }
        }
    }

    /**
     * Shows the program dashboard.
     *
     * @param id id of the sale
     * @return a webpage showing the items
     */
    @Security.Authenticated(Secured.class)
    public final Result show(final int id) {
        Sale sale = Sale.findById(id);
        List<Item> queryItems = queryItems(Item.class, sale.findItems(),
                "name", "name", sale.items);
        return ok(views.html.sales.show.render(sale, queryItems,
                queryString(), currentUser()));
    }

    /**
     * Show the financial report for a sale.
     *
     * @param id the id of the sale
     * @return a webpage showing the financial report
     */
    @Security.Authenticated(Secured.class)
    public final Result report(final int id) {
        Sale sale = Sale.findById(id);
        List<Transaction> trans = sale.transactions;

        return ok(views.html.sales.report.render(sale, trans, currentUser()));
    }


    /**
     * Provides a confirmation for deletion of a sale from the database.
     *
     * @param id id of the sale
     * @return a page showing the confirmation
     */
    @Security.Authenticated(Secured.class)
    public final Result sell(final int id) {
        Sale sale = Sale.findById(id);
        if (sale.isClosed()) {
            flash("warning", "This sale has been closed, and can no"
                    + " longer be edited");
        }

        return ok(views.html.sales.sell.render(sale, currentUser()));
    }

    /**
     * Provides a confirmation for deletion of a sale from the database.
     *
     * @param id id of the sale
     * @return a page showing the confirmation
     */
    @Security.Authenticated(Secured.class)
    public final Result delete(final int id) {
        Sale sale = Sale.findById(id);
        return ok(views.html.sales.delete.render(sale, currentUser()));
    }

    /**
     * Deletes a sale from the database.
     *
     * @param id id of the sale
     * @return a redirect to sales index
     */
    @Security.Authenticated(Secured.class)
    public final Result postDelete(final int id) {
        Sale sale = Sale.findById(id);
        sale.findItems().delete();
        sale.delete();
        return redirect("/sales");
    }

    /**
     * Retrieves all tags for a sale in a printable format.
     *
     * @param id a list of items
     * @return a page of print tags for a given set of items
     */
    @Security.Authenticated(Secured.class)
    public final Result tags(final int id) {
        Sale sale = Sale.findById(id);
        List<Item> tagItems = queryItems(Item.class, sale.findItems(), "name",
                "name", sale.items);
        return ok(views.html.sales.tags.render(tagItems, currentUser()));
    }

    /**
     * Displays the members page.
     * TODO - sort the members of a sale so the display is nicer
     *
     * @param id id of the sale
     * @return a page of all the users associated with a sale
     */
    @Security.Authenticated(Secured.class)
    public final Result members(final int id) {
        Sale sale = Sale.findById(id);
        List<String> members = new ArrayList<String>(sale.getUsers().keySet());
        return ok(views.html.sales.members.render(sale, members,
                currentUser()));
    }

    /**
     * Allows for a user to add a member with an associated role, and the
     * role that the current user can add is limited by their role for the
     * sale. For example, only Sale Administrator's can add other Sale
     * Administrators, Guests can't add, and no one can add a super user.
     *
     * @param id - the sale id
     * @return a page where a user can add members to a sale
     */
    @Security.Authenticated(Secured.class)
    public final Result addMember(final int id) {
        Sale sale = Sale.findById(id);
        List<String> roles = Sale.getUnrestrictedRoles();
        return ok(views.html.sales.addMember.render(sale, roles,
                currentUser()));
    }


    /**
     * Adds a member (a user associated with a sale) to a given sale. The
     * ember is associated with some role.
     *
     * TODO provide a user with a way of changing another users role on
     *
     * @param id id of the sale
     * @return redirect to sale items or render item form with errors
     */
    @Security.Authenticated(Secured.class)
    public final Result postAddMember(final int id) {
        Sale sale = Sale.findById(id);
        User user = User.findByEmail(formParams().get("email"));
        List<String> members = new ArrayList<String>(sale.getUsers().keySet());
        List<String> roles = Sale.getUnrestrictedRoles();
        Map<String, Sale.Role> roleMap = Sale.getRoleMap();

        if (user == null) {
            flash("error", "Your friend doesn't have an account with"
                    + " GarageBuddy.");
            return badRequest(views.html.sales.addMember.render(sale, roles,
                    currentUser()));
        } else if (members.contains(user.email)) {
            flash("error", "This user is already a member of the sale!");
            return badRequest(views.html.sales.addMember.render(sale, roles,
                    currentUser()));
        } else {
            String role = formParams().get("role");
            sale.addUser(user.email, roleMap.get(role));
            sale.save();
            return redirect("/sales/" + Integer.toString(sale.id)
                    + "/members");
        }
    }


}
