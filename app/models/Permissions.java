package models;

import com.avaje.ebean.Model;

public class Permissions extends Model {

    // System-wide Permissions
    // Used for Superuser actions
    // Not really necessary, except for consistent style
    // Also need to answer the question of how does someone become a SuperUser in the first place?
    // Will also require an admin view, so that a SU can view/interact with accounts


    /**
     *
     * @param user => The user currently attempting to promote another user to SuperUser status
     * @return true if user is a SuperUser; false otherwise
     */
    public static boolean canSetSuperUser (User user) {
        return user.isSuperUser;
    }

    /**
     *
     * @param user => The user currently attempting to lock/unlock an account
     * @return true if user is a superuser; false otherwise
     */
    public static boolean canUnlockProfile (User user) {
        return user.isSuperUser;
    }

    // Sale-Specific Permissions
    // Used to check specific User actions in specific Sales

    /**
     *
     * @param sale => The Sale to which a User is trying to add a new User
     * @param user => The User trying to add a new User to the sale
     *             NOTE: user is NOT the person we are trying to add. user is ALREADY part of the sale
     * @return true if user is SuperUser or Sale Admin; false otherwise
     */
    public static boolean canAssignRole (Sale sale, User user) {
        return user.isSuperUser ||
                (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN);
    }

    public static boolean canCloseSale (Sale sale, User user) {
        return user.isSuperUser ||
                (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN);
    }

    public static boolean canUpdateCatalog (Sale sale, User user) {
        return user.isSuperUser ||
                (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN) ||
                (sale.getUserRole(user.email) == Sale.Role.SELLER);
    }

    public static boolean canUpdatePrices (Sale sale, User user) {
        return user.isSuperUser ||
                (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN) ||
                (sale.getUserRole(user.email) == Sale.Role.SELLER);
    }

    public static boolean canAdvertise (Sale sale, User user) {
        return user.isSuperUser ||
            (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN) ||
            (sale.getUserRole(user.email) == Sale.Role.SELLER);
    }

    public static boolean canPrintTags (Sale sale, User user) {
        return user.isSuperUser ||
                (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN) ||
                (sale.getUserRole(user.email) == Sale.Role.SELLER) ||
                (sale.getUserRole(user.email) == Sale.Role.CLERK);
    }

    public static boolean canSellItems (Sale sale, User user) {
        return user.isSuperUser ||
                (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN) ||
                (sale.getUserRole(user.email) == Sale.Role.SELLER) ||
                (sale.getUserRole(user.email) == Sale.Role.CASHIER);
    }

    public static boolean canCreateReceipts (Sale sale, User user) {
        return user.isSuperUser ||
                (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN) ||
                (sale.getUserRole(user.email) == Sale.Role.SELLER) ||
                (sale.getUserRole(user.email) == Sale.Role.CASHIER);
    }

    public static boolean canAccessFinances (Sale sale, User user) {
        return user.isSuperUser ||
                (sale.getUserRole(user.email) == Sale.Role.SALE_ADMIN) ||
                (sale.getUserRole(user.email) == Sale.Role.SELLER) ||
                (sale.getUserRole(user.email) == Sale.Role.BOOK_KEEPER);
    }
}