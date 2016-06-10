package lib;

import models.User;
import controllers.Secured;
import play.data.DynamicForm;
import play.data.FormFactory;

import javax.inject.Inject;

/**
 * A collection of common methods for use in GarageBuddy controllers
 * @author Dean Papastrat
 */
public abstract class GBController extends play.mvc.Controller {

    @Inject
    protected FormFactory formFactory;
    private DynamicForm formParams;
    private boolean formParamsLoaded = false;
    private User currentUser;

    public User currentUser() {
        if (currentUser == null || !Secured.getCurrentUsername(ctx()).equals(currentUser.email)) {
            currentUser = Secured.getUser(ctx());
        }
        return currentUser;
    }

    /**
     * Creates a dynamic form to access attributes of non-model associated forms
     * @return
     */
    public DynamicForm formParams() {
        if (!formParamsLoaded) {
            formParams = formFactory.form().bindFromRequest();
        }
        return formParams;
    }

    /**
     * Creates a model form via the formFactory
     * @param klass The class to make a model form for
     * @param <T> A model object type
     * @return a play form to use in requests
     */
    public <T> play.data.Form<T> modelForm(Class<T> klass) {
        return (play.data.Form<T>) formFactory.form(klass).bindFromRequest();
    }

    /**
     * Creates a model form via the formFactory and fills it with an object
     * @param object An object to fill the form with
     * @param <T> A model object type
     * @return a play form to use in requests
     */
    @SuppressWarnings("unchecked")
    public <T> play.data.Form<T> modelForm(T object) {
        return ((play.data.Form<T>) formFactory.form(object.getClass())).fill(object);
    }

    /**
     * Creates a model form via the formFactory with no binding
     * @param klass The class to make a model form for
     * @param <T> A model object type
     * @return a play form to use in requests
     */
    public <T> play.data.Form<T> emptyModelForm(Class<T> klass) {
        return (play.data.Form<T>) formFactory.form(klass);
    }
}