package models;

import com.avaje.ebean.Model;

/**
 * Represents permissions for users.
 *
 * @version 1.0.0
 */
public class Permissions extends Model {

    // System-wide Permissions
    // Used for Superuser actions
    // Not really necessary, except for consistent style
    // Also, how does someone become a SuperUser in the first place?

    /**
     * SuperUsers can set SuperUsers.
     * @param user => The user currently attempting to set SuperUser status
     * @return true if user is a SuperUser; false otherwise
     */
    public static boolean canSetSuperUser(final User user) {
        return user.isSuperUser;
    }

    /**
     * SuperUsers can unlock profiles.
     * @param user => The user currently attempting to lock/unlock an account
     * @return true if user is a superuser; false otherwise
     */
    public static boolean canUnlockProfile(final User user) {
        return user.isSuperUser;
    }

    // Sale-Specific Permissions
    // Used to check specific User actions in specific Sales

    /**
     * SuperUsers and Sale Admins can assign roles on a Sale.
     * @param sale => The Sale to which a User is trying to add a new User
     * @param user => The User trying to add a new User to the sale
     * NOTE: user in param is NOT the person we are trying to add.
     * @return true if user is SuperUser or Sale Admin; false otherwise
     */
    public static boolean canAssignRole(final Sale sale, final User user) {
        return user.isSuperUser
                || (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN);
    }

    /**
     * Sale Admins can close Sales.
     * @param sale => Sale a User is trying to close
     * @param user => The User trying to close the Sale
     * @return true if user has permission
     */
    public static boolean canCloseSale(final Sale sale, final User user) {
        return user.isSuperUser
                || (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN);
    }

    /**
     * SuperUsers, SaleAdmins, and Sellers can update the catalog.
     * @param sale => Sale whose catalog User is trying to update
     * @param user => User attempting to update catalog
     * @return true if user has permission
     */
    public static boolean canUpdateCatalog(final Sale sale, final User user) {
        return user.isSuperUser
                || (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN)
                || (sale.getUserRole(user.email) == Sale.Role.SELLER);
    }

    /**
     * SuperUsers, SaleAdmins, and Sellers can update prices.
     * @param sale => Sale whose items' prices a User wants to update
     * @param user => User attempting to update prices
     * @return true if user has permission
     */
    public static boolean canUpdatePrices(final Sale sale, final User user) {
        return user.isSuperUser
                || (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN)
                || (sale.getUserRole(user.email) == Sale.Role.SELLER);
    }

    /**
     * SuperUsers, SaleAdmins, and Sellers can advertise.
     * @param sale => Sale for which User wants to advertise
     * @param user => User attempting to advertise
     * @return true if user has permission
     */
    public static boolean canAdvertise(final Sale sale, final User user) {
        return user.isSuperUser
                || (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN)
                || (sale.getUserRole(user.email) == Sale.Role.SELLER);
    }

    /**
     * SuperUsers, SaleAdmins, Sellers, and Clerks can print tags.
     * @param sale => Sale for which User wants to print tags
     * @param user => User attempting to print tags
     * @return true if user has permission
     */
    public static boolean canPrintTags(final Sale sale, final User user) {
        return user.isSuperUser
                || (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN)
                || (sale.getUserRole(user.email) == Sale.Role.SELLER)
                || (sale.getUserRole(user.email) == Sale.Role.CLERK);
    }

    /**
     * SuperUsers, SaleAdmins, Sellers, and Cashiers can sell items.
     * @param sale => Sale for which User wants to sell Items
     * @param user => User attempting to sell items
     * @return true if user has permission
     */
    public static boolean canSellItems(final Sale sale, final User user) {
        return user.isSuperUser
                || (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN)
                || (sale.getUserRole(user.email) == Sale.Role.SELLER)
                || (sale.getUserRole(user.email) == Sale.Role.CASHIER);
    }

    /**
     * SuperUsers, SaleAdmins, Sellers, and Cashiers can create receipts.
     * @param sale => Sale for which User wants to create receipts
     * @param user => User attempting to create receipts
     * @return true if user has permission
     */
    public static boolean canCreateReceipts(final Sale sale, final User user) {
        return user.isSuperUser
                || (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN)
                || (sale.getUserRole(user.email) == Sale.Role.SELLER)
                || (sale.getUserRole(user.email) == Sale.Role.CASHIER);
    }

    /**
     * SuperUsers, SaleAdmins, Sellers, and BookKeepers can access finances.
     * @param sale => Sale for which User wants to access finances
     * @param user => User attempting to access finances
     * @return true if user has permission
     */
    public static boolean canAccessFinances(final Sale sale, final User user) {
        return user.isSuperUser
                || (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN)
                || (sale.getUserRole(user.email) == Sale.Role.SELLER)
                || (sale.getUserRole(user.email) == Sale.Role.BOOK_KEEPER);
    }
}
