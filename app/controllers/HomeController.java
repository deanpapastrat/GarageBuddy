package controllers;

import play.mvc.*;
import views.html.*;
import views.html.home.*;
import models.User;
import models.Sale;
import models.Item;
import play.data.Form;
import lib.GBController;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages authentication, welcome, and application home page routes
 * @author Dean Papastrat, Alex Woods
 */
public class HomeController extends GBController {
    public static boolean startup = true;

    /**
     * Renders the welcome page or home page depending on auth status
     * @return if authenticated, home page HTML, else welcome page HTML
     */
    public Result index() {
        if (startup) {
            User test = new User("user", "user@gatech.edu", "pass");
            if (test.validate() == null) {
                test.save();
            }
            startup = false;
        }
        
        if (Secured.isLoggedIn(ctx())) {
            return ok(views.html.home.home.render("Home", "Home"));
        } else {
            return ok(views.html.home.index.render());
        }
    }

    /**
     * Renders the login page for a user
     * @return login page HTML
     */
    public Result login() {
        return ok(views.html.home.login.render());
    }

    /**
     * Finds an existing user and authenticates them if credentials are correct.
     * @return redirect to home page or renders login form with invalid credentials error
     */
    public Result postLogin() {
        User user = User.findByEmail(formParams().get("email"));

        if (user != null && user.checkPassword(formParams().get("password"))) {
            session().clear();
            session("email", formParams().get("email"));
            return redirect("/home");
        }
        else {
            flash("error", "Wrong email or password. Please try again.");
            return badRequest(views.html.home.login.render());
        }
    }

    /**
     * Renders the registration page for the user with a blank user form
     * @return register page HTML
     */
    public Result register() {
        return ok(views.html.home.register.render(emptyModelForm(User.class)));
    }

    /**
     * Validates registration form and creates a user with the provided data
     * @return redirect to home page or renders register form with validation errors
     */
    public Result postRegister() {
        Form<User> userForm = modelForm(User.class);

        if (!userForm.data().get("password").equals(userForm.data().get("confirmPassword"))) {
            userForm.reject("Password and confirmation do not match.");
        }

        if (userForm.hasErrors()) {
            return badRequest(views.html.home.register.render(userForm));
        } else {
            User user = userForm.get();
            user.save();
            session("email", user.email);
            return redirect("/home");
        }
    }

    /**
     * Renders the home page for the user
     * @return home page HTML
     */
    @Security.Authenticated(Secured.class)
    public Result home() {
        return ok(views.html.home.home.render("Home", "Home"));
    }

    /**
     * Clears the session for the user and redirects to welcome page
     * @return redirect to the welcome page
     */
    @Security.Authenticated(Secured.class)
    public Result logout() {
        session().clear();
        return redirect(routes.HomeController.index());
    }

    /************** Everything below here is TODO! **************/

    /**
     * Renders the events page with a list of garage sale events
     * @return events page HTML
     */
    public Result events() {
        return TODO;
    }

    /**
     * Renders the reports page with options for generating types of reports
     * @return reports page HTML
     */
    public Result reports() {
        return TODO;
    }

    /**
     * Renders the users page with a list of users
     * @return users page HTML
     */
    public Result users() {
        return TODO;
    }

    /**
     * Renders the profile page with profile data
     * @return profile page HTML
     */
    public Result profile() {
        return TODO;
    }

    /**
     * Creates a sale in the database.
     * @return a webpage representing that sale
     */
    public Result addSale() {
        Sale sale = new Sale();
        String userEmail = session("email");
        sale.addUser(userEmail, Sale.Role.SALE_ADMIN);
        // currently broken
//        sale.save()

        return redirect("/sale");
    }

    public Result sale() {
        // need a way to access the current sale
//        return ok(views.html.sale.sale.render("Sale", "Sale", currentSale.getSaleItems()));

        // below is a temp list of items just so I can have it communicate and set up the view. I really need
        // a method to access the current sale and get it's items.
        User Jodie = new User("Jodie", "Jodie@gatech.edu", "sunglasses");
        List<Item> itemsTemp = new ArrayList<>();

        Item item1 = new Item(Jodie, "Car", 3000.00);
        Item item2 = new Item(Jodie, "Phone", 200.00);
        Item item3 = new Item(Jodie, "Shirt", 20.00);
        itemsTemp.add(item1);
        itemsTemp.add(item2);
        itemsTemp.add(item3);

        return ok(views.html.sale.sale.render("Sale", "Sale", itemsTemp));
    }



}
