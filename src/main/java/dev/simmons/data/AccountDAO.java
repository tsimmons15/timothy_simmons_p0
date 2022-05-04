package dev.simmons.data;

import dev.simmons.entities.Account;

/**
 * A Database Access Object handling manipulation of the Account table.
 */
public interface AccountDAO {
    /**
     * Persist an account into the database.
     * @param account The account to persist in the database.
     * @return The account as it exists in the database
     */
    Account createAccount(Account account);

    /**
     * Retrieve an account from the database
     * @param id The account_id of the account to retrieve.
     * @return If an account with account_id exists, the account. Null, otherwise.
     */
    Account getAccount(int id);

    /**
     * Update an account with the values passed in.
     * @param newAccount The account with the updated values.
     * @return True if the update goes successfully, false otherwise.
     */
    boolean updateAccount(Account newAccount);

    /**
     * A batch update given a variable number of accounts. WIP!
     * @param accounts The variable number of accounts to update.
     * @return True if the update goes successfully, false otherwise.
     */
    boolean updateAccounts(Account... accounts);

    /**
     * Removes an account record from the database.
     * @param account The account object containing the account_id to delete.
     * @return True if the deletion is successful, false otherwise.
     */
    boolean deleteAccount(Account account);

    /**
     * Removes an account record from the database.
     * @param id The account_id to delete.
     * @return True if the deletion is successful, false otherwise.
     */
    boolean deleteAccount(int id);
}
