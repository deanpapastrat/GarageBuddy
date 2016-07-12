package controllers;

import models.User;
import lib.GBController;
import play.mvc.*;
import views.html.users.*;

/**
 * Manages routes for users.
 * @author Dean Papastrat
 */
public class UsersController extends GBController {
    /**
     * Renders an index of all users.
     * @return user index page HTML
     */
    @Security.Authenticated(Secured.class)
    public final Result index() {
        return ok(views.html.users.index.render(User.find.all(),
                currentUser()));
    }

    /**
     * Renders a profile page with user details.
     * @return profile page HTML
     */
    @Security.Authenticated(Secured.class)
    public final Result profile() {
        return ok(views.html.users.profile.render(modelForm(currentUser()),
                currentUser()));
    }

    /**
     * Processes profile updates for a user.
     * @return redirect to profile page or renders profile form with errors
     */
    @Security.Authenticated(Secured.class)
    public final Result postProfile() {
        if (!currentUser().checkPassword(formParams()
                .get("currentPassword"))) {
            flash("error", "Current password is not valid. Please try again.");
            return redirect("/profile");
        }

        currentUser().email = formParams().get("email");
        currentUser().name = formParams().get("name");
        currentUser().postalCode = formParams().get("postalCode");
        currentUser().state = formParams().get("state");
        currentUser().address = formParams().get("address");
        currentUser().city = formParams().get("city");

        if (formParams().get("newPassword") != null && !formParams()
                .get("newPassword").isEmpty()) {
            currentUser().setPassword(formParams().get("newPassword"));
        }

        play.data.Form<User> userForm = modelForm(currentUser());

        if (!userForm.hasErrors()) {
            currentUser().update();
            flash("success", "Profile details saved.");
            return redirect("/profile");
        } else {
            return badRequest(views.html.users.profile.render(userForm,
                    currentUser()));
        }
    }

    /**
     * Renders a confirmation to delete current user.
     * @return delete confirmation page HTML
     */
    @Security.Authenticated(Secured.class)
    public final Result deleteProfile() {
        return ok(views.html.users.deleteProfile.render(currentUser()));
    }

    /**
     * Permanently deletes a user.
     * @return redirect to welcome page
     */
    @Security.Authenticated(Secured.class)
    public final Result postDeleteProfile() {
        currentUser().delete();
        return redirect("/logout");
    }

    /**
     * Resets a user's login attempts.
     *
     * @param userEmail the email of the user to reset attempts for
     * @return redirect to users index
     */
    @Security.Authenticated(Secured.class)
    public final Result resetLoginAttempts(final String userEmail) {
        if (currentUser().can("resetAttempts")) {
            User user = User.findByEmail(userEmail);
            user.resetLoginAttempts();
        }
        return redirect("/users");
    }
}
