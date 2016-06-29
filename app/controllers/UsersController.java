package controllers;

import models.User;
import lib.GBController;
import play.mvc.*;
import views.html.users.*;

/**
 * Manages routes for users
 * @author Dean Papastrat
 */
public class UsersController extends GBController {
    /**
     * Renders an index of all users
     * @return user index page HTML
     */
    @Security.Authenticated(Secured.class)
    public Result index() {
        return ok(views.html.users.index.render("Users", "Users", User.find.all()));
    }

    /**
     * Renders a profile page with user details
     * @return profile page HTML
     */
    @Security.Authenticated(Secured.class)
    public Result profile() {
        return ok(views.html.users.profile.render("Profile", "Profile", modelForm(currentUser())));
    }

    /**
     * Processes profile updates for a user
     * @return redirect to profile page or renders profile form with errors
     */
    @Security.Authenticated(Secured.class)
    public Result postProfile() {
        if (!currentUser().checkPassword(formParams().get("currentPassword"))) {
            flash("error", "Current password is not valid. Please try again.");
            return redirect("/profile");
        }

        currentUser().email = formParams().get("email");
        currentUser().name = formParams().get("name");
        currentUser().postalCode = formParams().get("postalCode");
        currentUser().state = formParams().get("state");
        currentUser().address = formParams().get("address");
        currentUser().city = formParams().get("city");

        if (formParams().get("newPassword") != null && !formParams().get("newPassword").isEmpty()) {
            currentUser().setPassword(formParams().get("newPassword"));
        }

        play.data.Form<User> userForm = modelForm(currentUser());

        if (!userForm.hasErrors()) {
            currentUser().update();
            flash("success", "Profile details saved.");
            return redirect("/profile");
        } else {
            return badRequest(views.html.users.profile.render("Profile", "Profile", userForm));
        }
    }

    /**
     * Renders a confirmation to delete current user
     * @return delete confirmation page HTML
     */
    @Security.Authenticated(Secured.class)
    public Result deleteProfile() {
        return ok(views.html.users.deleteProfile.render("Profile", "Profile"));
    }

    /**
     * Permanently deletes a user
     * @return redirect to welcome page
     */
    @Security.Authenticated(Secured.class)
    public Result postDeleteProfile() {
        currentUser().delete();
        return redirect("/logout");
    }

    /**
     * Resets a user's login attempts
     * @return redirect to users index
     */
    @Security.Authenticated(Secured.class)
    public Result resetLoginAttempts(String userEmail) {
        if (currentUser().can("resetAttempts")) {
            User user = User.findByEmail(userEmail);
            user.resetLoginAttempts();
        }
        return redirect("/users");
    }
}
