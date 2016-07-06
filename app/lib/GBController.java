package lib;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import models.User;
import controllers.Secured;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * A collection of common methods for use in GarageBuddy controllers
 * @author Dean Papastrat
 */
public abstract class GBController extends play.mvc.Controller {

    /**
     * Provides form factory to all sub-controllers
     */
    @Inject
    protected FormFactory formFactory;

    /**
     * Cache for request parameter parsing
     */
    private DynamicForm formParams;

    /**
     * Determine whether to use cached form params or not
     */
    private boolean formParamsLoaded = false;

    /**
     * Cached copy of the current user
     */
    private User currentUser;

    /**
     * Retrieves the current authenticated user using the session's context
     * @return current user
     */
    public User currentUser() {
        if (currentUser == null || !Secured.getCurrentUsername(ctx()).equals(currentUser.email)) {
            currentUser = Secured.getUser(ctx());
        }
        return currentUser;
    }

    /**
     * Creates a dynamic form to access attributes of non-model associated forms
     * @return a dynamic form get attributes from
     */
    public DynamicForm formParams() {
        if (!formParamsLoaded) {
            formParams = formFactory.form().bindFromRequest();
        }
        return formParams;
    }

    /**
     * Performs the same action as queryItems, but takes a single parameter instead of a list
     *
     * @param query an expression list for a given model
     * @param parameter a single parameter to search for
     * @param sort the field to sort by
     * @param defaultItems items to return it there's no query
     * @param <T> a model object
     * @return a list of items that match the query
     */
    public <T extends Model> List<T> queryItems(Class<T> klass, ExpressionList<T> query, String parameter, String sort, List<T> defaultItems) {
        List<String> parameters = new ArrayList<>();
        parameters.add(parameter);
        return queryItems(klass, query, parameters, sort, defaultItems);
    }

    /**
     * Automatically processes a search query and returns a list of items that match it
     *
     * @param query an expression list for a given model
     * @param parameters a list of fields to search for the query value
     * @param sort the field to sort by
     * @param defaultItems items to return it there's no query
     * @param <T> a model object
     * @return a list of items that match the query
     */
    public <T extends Model> List<T> queryItems(Class<T> klass, ExpressionList<T> query, List<String> parameters, String sort, List<T> defaultItems) {
        if (queryString() == null) {
            return defaultItems;
        }

        ExpressionList<T> originalQuery = query;

        for (int i = 0; i < parameters.size(); i++) {
            if (i != 0) {
                query = query.or();
            }

            if (!parameters.get(i).equals("id")) {
                query = query.icontains(parameters.get(i), queryString());
            }
        }

        List<T> result = query.setOrderBy(sort).findList();

        if (result.size() == 0 && parameters.contains("id")) {
            Model.Finder<String, T> finder = new Model.Finder(klass);
            result = finder.where().idEq(Integer.parseInt(queryString())).findList();
        }

        return result;
    }

    /**
     * Parses the query parameter out of the params for the page
     * @return the user's input or null if empty
     */
    public String queryString() {
        String query = formParam("q");
        if (query == null || query.isEmpty()) {
            return null;
        }
        return query;
    }

    /**
     * Retrieves a param from the submitted parameters
     * @return a string representation of the parameter sent
     */
    public String formParam(String key) {
        return formParams().get(key);
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