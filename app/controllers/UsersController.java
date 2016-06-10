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
    public Result index() {
        return ok(views.html.users.index.render("Users", "Users", User.find.all()));
    }

    /**
     * Renders a profile page with user details
     * @return profile page HTML
     */
    public Result profile() {
        return ok(views.html.users.profile.render("Profile", "Profile", modelForm(currentUser())));
    }

    /**
     * Processes profile updates for a user
     * @return redirect to profile page or renders profile form with errors
     */
    public Result postProfile() {
        if (!currentUser().checkPassword(formParams().get("currentPassword"))) {
            flash("error", "Current password is not valid. Please try again.");
            return redirect("/profile");
        }

        currentUser().email = formParams().get("email");
        currentUser().name = formParams().get("name");

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
}
