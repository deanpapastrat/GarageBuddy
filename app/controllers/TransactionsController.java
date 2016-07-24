package controllers;

import lib.GBController;
import play.data.Form;
import play.mvc.*;
import models.*;
import views.html.transactions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages loading of pages related to transactions.
 *
 * @author Dean Papastrat
 * @version 1.0.0
 */
public class TransactionsController extends GBController {
    /**
     * Renders a list of all transactions for the specified sale.
     *
     * @param saleId id of the sale we want transactions for
     * @return a webpage showing transactions
     */
    @Security.Authenticated(Secured.class)
    public final Result index(final int saleId) {
        Sale sale = Sale.findById(saleId);
        List<String> parameters = new ArrayList<>(Arrays.asList("id",
                "customer_name", "customer_email"));
        List<Transaction> transactions = queryItems(Transaction.class,
                sale.findTransactions(), parameters, "created_at",
                sale.transactions);
        return ok(views.html.transactions.index.render(sale, transactions,
                queryString(), currentUser()));
    }

    /**
     * Renders an empty form to create a transaction for a sale.
     *
     * @param saleId id of the sale we want to create a transaction for
     * @return a webpage showing the transaction form
     */
    @Security.Authenticated(Secured.class)
    public final Result create(final int saleId) {
        Sale sale = Sale.findById(saleId);
        return ok(views.html.transactions.create.render(sale,
                emptyModelForm(Transaction.class), currentUser()));
    }

    /**
     * Creates a transaction using data passed from the form submission and
     * either displays the form with validation errors or redirects to the
     * transaction display page.
     *
     * @param saleId id of the sale to create the transaction for
     * @return a redirect to the transaction's add item page
     */
    @Security.Authenticated(Secured.class)
    public final Result postCreate(final int saleId) {
        Sale sale = Sale.findById(saleId);
        Form<Transaction> transactionForm = modelForm(Transaction.class);
        if (transactionForm.hasErrors()) {
            return badRequest(views.html.transactions.create.render(sale,
                    transactionForm, currentUser()));
        } else {
            Transaction transaction = new Transaction(sale, currentUser());
            transaction.customerName = transactionForm.get().customerName;
            transaction.save();
            return redirect("/transactions/" + transaction.id + "/items");
        }
    }

    /**
     * Displays a transaction and its associated details.
     *
     * @param id id of the transaction to show
     * @return a webpage showing the transaction's details
     */
    @Security.Authenticated(Secured.class)
    public final Result show(final int id) {
        Transaction transaction = Transaction.findById(id);
        return ok(views.html.transactions.show.render(transaction,
                transaction.items, currentUser()));
    }

    /**
     * Displays a receipt for a transaction.
     *
     * @param id id of the transaction to show
     * @return a webpage showing a receipt for the transaction
     */
    @Security.Authenticated(Secured.class)
    public final Result receipt(final int id) {
        Transaction transaction = Transaction.findById(id);
        return ok(views.html.transactions.receipt.render(transaction,
                transaction.items, currentUser()));
    }

    /**
     * Renders a page that allows the user to add and remove items from a
     * transaction.
     *
     * @param id id of the transaction to view
     * @return a webpage with a added and available items
     */
    @Security.Authenticated(Secured.class)
    public final Result items(final int id) {
        Transaction transaction = Transaction.findById(id);
        return ok(views.html.transactions.items.render(transaction,
                transaction.items, transaction.sale.findUnpurchasedItems()
                        .findList(), currentUser()));
    }

    /**
     * Adds an item to the specified transaction.
     *
     * @param id id of the transaction to modify
     * @param itemId id of the item to add
     * @return a redirect to the list of items to add
     */
    @Security.Authenticated(Secured.class)
    public final Result addItem(final int id, final int itemId) {
        Transaction transaction = Transaction.findById(id);
        transaction.addItem(Item.findById(itemId), true);
        return redirect("/transactions/" + transaction.id + "/items");
    }

    /**
     * Removes an item from the specified transaction.
     *
     * @param id id of the transaction to modify
     * @param itemId id of the item to remove
     * @return a redirect to the list of items to add
     */
    @Security.Authenticated(Secured.class)
    public final Result removeItem(final int id, final int itemId) {
        Transaction transaction = Transaction.findById(id);
        transaction.removeItem(Item.findById(itemId), true);
        return redirect("/transactions/" + transaction.id + "/items");
    }

    /**
     * Renders a confirmation page to delete the specified transaction.
     *
     * @param id the id of the transaction to confirm deletion of
     * @return a webpage confirming the deletion
     */
    @Security.Authenticated(Secured.class)
    public final Result delete(final int id) {
        Transaction transaction = Transaction.findById(id);
        return ok(views.html.transactions.delete.render(transaction,
                currentUser()));
    }

    /**
     * Deletes the specified transaction from the system.
     *
     * @param id the id of the transaction to delete
     * @return a redirect to the transactions index for the sale
     */
    @Security.Authenticated(Secured.class)
    public final Result postDelete(final int id) {
        Transaction transaction = Transaction.findById(id);
        int saleId = transaction.sale.id;
        transaction.removeItems();
        transaction.delete();
        return redirect("/sales/" + saleId + "/transactions");
    }
}
