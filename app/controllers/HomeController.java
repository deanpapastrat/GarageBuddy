package controllers;

import java.util.Map;
import java.util.HashMap;
import play.mvc.*;
import play.mvc.Http.Context;
import views.html.*;
import views.html.home.*;
import models.User;
import javax.inject.Inject;
import play.data.FormFactory;
import play.data.Form;
import play.data.DynamicForm;
import play.Logger;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {


    // injecting formFactory so I can use it later in the login and postLogin methods
    @Inject
    public FormFactory formFactory;

    public static boolean startup = true;


    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     *
     * Dean will edit index.scala.html, and in the process will edit the @main method thing at
     * the top. We will use whatever parameters he specifies in the render() method below.
     */
    public Result index() {


        // TODO there is a problem in this block of code
        // I put the existing values in the form, hopefully that will make this unnecessary
//        if (startup) {
//            User test = new User("user", "user@gatech.edu", "pass");
//            test.save();
//            startup = false;
//        }
        
        if (Secured.isLoggedIn(ctx())) {
            return ok(views.html.home.home.render("Home", "Home"));
        } else {
            return ok(views.html.home.index.render());
        }
    }

    public Result login() {
        return ok(views.html.home.login.render());
    }

    public Result postLogin() {
        DynamicForm formData = formFactory.form().bindFromRequest();
        User user = User.find.where().ieq("email", formData.get("email")).findUnique();

        if (user != null && user.checkPassword(formData.get("password"))) {
            session().clear();
            session("email", formData.get("email"));
            return redirect("/home");
        }
        else {
            flash("error", "Wrong email or password. Please try again.");
            return badRequest(views.html.home.login.render());
        }
    }

    public Result register() {
        return ok(views.html.home.register.render(formFactory.form(User.class)));
    }

    public Result postRegister() {
        Form<User> userForm = formFactory.form(User.class).bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(views.html.home.register.render(userForm));
        } else {
            User user = userForm.get();
            user.save();
            return redirect("/home");
        }
    }

    /**
     * Takes the user inside to their profile.
     *
     */
    @Security.Authenticated(Secured.class)
    public Result home() {
        return ok(views.html.home.home.render("Home", "Home"));
    }

    /**
     * Takes you back to the page called index. We can change that name if you guys want.
     *
     */
    @Security.Authenticated(Secured.class)
    public Result logout() {
        session().clear();
        return redirect(routes.HomeController.index());
    }

    /**
     *
     * Please be aware of the routes file and HomeController as you write your scala.html files.
     *
     * A profile.scala.html file for whatever we're going to display
     * once the user is logged in.
     *
     * An edited version of index.scala.html that will display the page the user is
     * automatically taken to when they arrive.
     *
     * A Login.scala.html file, which represents the page where the user will login.
     * 
     *
     */

}
