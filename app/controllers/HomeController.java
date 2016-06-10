package controllers;

import play.mvc.*;
import views.html.*;
import views.html.home.*;
import models.User;
import play.data.Form;
import lib.GBController;

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
}
