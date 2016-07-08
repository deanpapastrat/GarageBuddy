package controllers;

import lib.GBController;
import play.data.Form;
import play.mvc.*;
import models.*;
import views.html.sales.*;

import java.util.*;


/**
 * Manages endpoints
 *
 * @author Dean Papastrat and Alex Woods
 */
public class SalesController extends GBController {

    // for presentation, it will be useful to have a hashmap between the roles and a nice descriptive string
    // we can put on the site




    /**
     * Returns an index of all sales
     * @return sale index page
     */
    @Security.Authenticated(Secured.class)
    public Result index() {
        List<Sale> allSales = Sale.find.all();
        Collections.sort(allSales,
                (o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
        return ok(views.html.sales.index.render("Sales", "Sales", allSales, currentUser()));
    }

    /**
     * Creates a sale in the database.
     * @return a webpage representing that sale
     */
    @Security.Authenticated(Secured.class)
    public Result create() {
        return ok(views.html.sales.create.render("New Sale", "Sales", emptyModelForm(Sale.class), currentUser()));
    }



    /**
     * Validates sale form and creates a sale with the provided data
     * @return redirect to sale page or renders sale form with validation errors
     */
    @Security.Authenticated(Secured.class)
    public Result postCreate() {
        Form<Sale> saleForm = modelForm(Sale.class);

        if (saleForm.hasErrors()) {
            return badRequest(views.html.sales.create.render("New Sale", "Sales", saleForm, currentUser()));
        } else {
            Sale sale = saleForm.get();
            sale.addUser(currentUser().email, Sale.Role.SALE_ADMIN);

            // if this next line looks weird, it's because we have to allow for sales that start and end on the same day
            if (!sale.getEndDate().before(sale.getStartDate())) {
                flash("success", "Sale created");
                sale.save();
                return redirect("/sales/" + Integer.toString(sale.id));
            }
            else {
                flash("error", "The end date can't be before the start date");
                return badRequest(views.html.sales.create.render("New Sale", "Sales", saleForm, currentUser()));
            }
        }
    }

    /**
     * Edits a sale in the database.
     * @return a webpage representing that sale
     */
    @Security.Authenticated(Secured.class)
    public Result edit(int id) {
        Sale sale = Sale.findById(id);
        if (sale.isClosed()) {
            flash("warning", "This sale has been closed, and can no longer be edited");
        }
        else {
            return ok(views.html.sales.edit.render(sale.name, "Sales", sale, modelForm(sale), currentUser()));
        }
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
            return badRequest(views.html.sales.edit.render(sale.name, "Sales", sale, saleForm, currentUser()));
        }
        else {
            sale.name = formParam("name");
            sale.setFormattedStartDate(formParam("startDate"));
            sale.setFormattedEndDate(formParam("endDate"));
            if (formParam("close") != null) {
                sale.close();
                flash("warning", "This sale has been closed, and can no longer be edited");
            }
            if (!sale.getEndDate().before(sale.getStartDate())) {
                sale.save();
                return redirect("/sales/" + Integer.toString(sale.id));
            }
            else {
                flash("error", "The end date can't be before the start date");
                return badRequest(views.html.sales.edit.render(sale.name, "Sales", sale, saleForm, currentUser()));
            }
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
        List<Item> queryItems = queryItems(Item.class, sale.findItems(), "name", "name", sale.items);
        return ok(views.html.sales.show.render(sale.name, "Sales", sale, queryItems, queryString(), currentUser()));
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

        return ok(views.html.sales.report.render(sale.name, "Financial Report", sale, trans, currentUser()));
    }


    /**
     * Provides a confirmation for deletion of a sale from the database.
     * @return a page showing the confirmation
     */
    @Security.Authenticated(Secured.class)
    public Result sell(int id) {
        Sale sale = Sale.findById(id);
        if (sale.isClosed()) {
            flash("warning", "This sale has been closed, and can no longer be edited");
        }
        else {
            return ok(views.html.sales.sell.render(sale, currentUser()));
        }
    }

    /**
     * Provides a confirmation for deletion of a sale from the database.
     * @return a page showing the confirmation
     */
    @Security.Authenticated(Secured.class)
    public Result delete(int id) {
        Sale sale = Sale.findById(id);
        return ok(views.html.sales.delete.render(sale, currentUser()));
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
        List<Item> tagItems = queryItems(Item.class, sale.findItems(), "name", "name", sale.items);
        if (sale.isClosed()) {
            flash("warning", "This sale has been closed, and can no longer be edited");
        }
        else {
            return ok(views.html.sales.tags.render(tagItems, currentUser()));
        }
    }

    /**
     * Displays the members page.
     *
     * @param id
     * @return a page of all the users associated with a sale
     */
    @Security.Authenticated(Secured.class)
    public Result members(int id) {
        Sale sale = Sale.findById(id);

        // I know this isn't pretty, but I needed a way (that worked) to be able to go from the role of the user
        // to a pretty displayable string. And the role came in long form
        Map<Long, String> roles = new HashMap<Long, String>();
        roles.put(new Long(1), "Guest");
        roles.put(new Long(2), "Book Keeper");
        roles.put(new Long(3), "Cashier");
        roles.put(new Long(4), "Clerk");
        roles.put(new Long(5), "Seller");
        roles.put(new Long(6), "Sale Administrator");
        roles.put(new Long(7), "Super User");


        Map<String, Long> membersWithRoles = sale.getUsers();
        Set temp = membersWithRoles.keySet();
        List<String> members = new ArrayList<String>(temp);

        return ok(views.html.sales.members.render("Members", "Members", sale, members, membersWithRoles, roles, currentUser()));
    }

    /**
     * The addMember page, which allows a user to add another user to the sale
     *
     * @param id - the sale id
     * @return
     */
    @Security.Authenticated(Secured.class)
    public Result addMember(int id) {
        Sale sale = Sale.findById(id);

        // unused so far, but we'll need to check if a user is already a member of a sale
        Map<String, Long> membersWithRoles = sale.getUsers();
        Set temp = membersWithRoles.keySet();
        List<String> members = new ArrayList<String>(temp);

        // right now this is just for display in the dropdown menu
        List<String> roles = Arrays.asList("Guest", "Book Keeper", "Cashier", "Clerk", "Seller", "Sale Administrator");


        return ok(views.html.sales.addMember.render("Members", "Members", sale, members, roles, currentUser()));
    }

    /**
     * Validates item form and creates an item with the provided data
     *
     * So far this method works to add members to a sale, however, it doesn't do the role yet.
     * TODO be able to associate a role with an added user
     * @return redirect to sale items index or renders item form with validation errors
     */
    @Security.Authenticated(Secured.class)
    public Result postAddMember(int id) {
        Sale sale = Sale.findById(id);
        User user = User.findByEmail(formParams().get("email"));

        // unused so far, but we'll need to check if a user is already a member of a sale
        Map<String, Long> membersWithRoles = sale.getUsers();
        Set temp = membersWithRoles.keySet();
        List<String> members = new ArrayList<String>(temp);

        // right now this is just for display in the dropdown menu
        List<String> roles = Arrays.asList("Guest", "Book Keeper", "Cashier", "Clerk", "Seller", "Sale Administrator");

        if (user == null) {
            flash("error", "Your friend doesn't have an account with Garage Buddy");
            return badRequest(views.html.sales.addMember.render("Members", "Members", sale, members, roles, currentUser()));

        }
        else {
        sale.addUser(user.email, Sale.Role.SELLER);
        sale.save();
        return redirect("/sales/" + Integer.toString(sale.id) + "/members");

        }
    }


}
