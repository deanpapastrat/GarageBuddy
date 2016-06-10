package controllers;

import play.mvc.Http;
import play.mvc.Http.*;
import play.mvc.Result;
import play.mvc.Security;
import models.User;

/**
 * This class will implement session storing. When you see @Security.Authentication(Secured)
 * above a method, it's invoking this class, and securing things.
 * @author Alex Woods
 */
public class Secured extends Security.Authenticator{

    /**
     * Used by authentication to determine if a user is logged in.
     * This method gets the username of the current logged in user.
     * If it is the email, as below, we set that as the "email" attribute of the session.
     *
     * @param ctx the context
     */
    public static String getCurrentUsername(Context ctx) {
        return ctx.session().get("email");
    }

    /**
     * Non-static implementation of getCurrentUsername for Security.Authenticator superclass
     * @param ctx
     * @return
     */
    @Override
    public String getUsername(Context ctx) {
        return getCurrentUsername(ctx);
    }

    /**
     * Retrieves a user object for the given context
     * @param ctx The context
     * @return a user for the given context
     */
    public static User getUser(Context ctx) {
        return User.findByEmail(ctx.session().get("email"));
    }

    /**
     * Instruct authenticator to automatically redirect to login page if unauthorized,
     * used if the above method returns null.
     *
     * Still not sure where it's used...
     * @param context The context.
     * @return The login page.
     */
    @Override
    public Result onUnauthorized(Context context) {
        return redirect(routes.HomeController.login());
    }

    /**
     * Determines whether or not the current user is logged in or not
     * @param ctx
     * @return whether or not the user is currently logged in
     */
    public static boolean isLoggedIn(Http.Context ctx) {
        return (getUser(ctx) != null);
    }
}
