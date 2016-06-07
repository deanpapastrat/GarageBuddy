package controllers;

import play.mvc.*;
import play.mvc.Http.Context;
import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

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
        return ok(index.render("We can change this filename to 'home' or " +
                "something if you guys think that would be more appropriate. " +
                "This is just their little template. - Alex, from HomeController"));
    }

    /**
     * This method will render the login page that Dean writes.
     * Right now it simply returns a to do object. Play is pretty cool.
     * Let's be clear - this renders the login page. It does not do anything with the data the user enters.
     * @return HTTP result
     */
    public Result login() {
        return TODO;
        // return ok(Login.render("Dean will write this file as well."));
    }

    /**
     * Once I get the data, I will bind it to a Form (play.data.Form), in whatever basic Java form it is
     * given to me, via Andre and Taj.
     *
     * If the form has errors, the method below will flash an error and return a badRequest().
     *
     * If the information is validated, it will clear the session, and return a redirect result
     * to profile, or home, or whatever we decide to call it.
     *
     * Probably the hardest method to write.
     *
     */
    public Result postLogin() {
        return TODO;

//        if (formData.hasErrors()) {
//            flash("error", "Login credentials not valid.");
//            return badRequest(Login.render("Login", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), formData));
//        }
//        else {
//            // things have been validated, now we go to the "home" page.
//            session().clear();
//            session("email", formData.get().email);
//            return redirect(routes.Application.profile());
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
     * This will render the profile.scala.html file that Dean writes.
     * It is where the user goes after a successful login. We need
     * to use this method for session storing.
     */
    @Security.Authenticated(Secured.class)
    public Result profile() {
        return TODO;
        // return ok(Profile.render("Profile", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx())));
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

