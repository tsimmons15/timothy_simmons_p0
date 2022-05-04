package dev.simmons.data;

import dev.simmons.entities.Account;
import dev.simmons.entities.Client;
import dev.simmons.utilities.lists.List;

/**
 * A Data Access Object used to handle joins between Clients and Accounts.
 */
public interface AccountOwnerDAO {
    /**
     * Add the relationship between an account and an owner.
     * @param accountId The account_id of the account to join with an owner.
     * @param clientId The client_id of the owner of the account
     * @return True if the insertion is successful, false otherwise.
     */
    boolean addOwner(int accountId, int clientId);

    /**
     * Add the relationship between an account and an owner.
     * @param account The account object that holds the account_id of the account to join.
     * @param client The client object that holds the client_id of the account to join.
     * @return True if the insertion is successful, false otherwise.
     */
    boolean addOwner(Account account, Client client);

    /**
     * Get the list of owners listed on a given account.
     * @param accountId The account_id of the account to find the owners.
     * @return The found list of owners of the account. If no owners are registered, will return an empty list.
     */
    List<Client> getOwners(int accountId);

    /**
     * Get the list of owners listed on a given account.
     * @param account The account object that holds the account_id of the account to find the owners.
     * @return The found list of owners of the account. If no owners are registered, will return an empty list.
     */
    List<Client> getOwners(Account account);

    /**
     * Gets the accounts owned by a given client.
     * @param clientId The client_id of the client to search for.
     * @return The found list of accounts for the client. If no accounts exist, the list will be empty.
     */
    List<Account> getAccounts(int clientId);

    /**
     * Gets the accounts owned by a given client.
     * @param client The client object containing the client_id for the given client.
     * @return The found list of accounts for the client. If no accounts exist, the list will be empty.
     */
    List<Account> getAccounts(Client client);

    /**
     * Gets all accounts, including populating the owner list, for a given client.
     * @param clientId The client_id for the owner's accounts to be searched.
     * @return The found list of accounts, containing the list of 'owners'. If no accounts exist, the list will be empty.
     */
    List<Account> getFullAccountInfo(int clientId);

    /**
     * Gets all account information, including their full owner list, for all current accounts.
     * @return The list of accounts, containing the full list of owners. If no accounts exist, the list will be empty.
     */
    List<Account> getFullAccountInfo();

    /**
     * Gets accounts for a client where they are the sole owner.
     * @param clientId The client_id of the owner whose accounts we're trying to retrieve.
     * @return The list of all that client's accounts for which they are sole owner. If they are not the sole owner for any, the list will be empty.
     */
    List<Account> getAccountsSolelyOwned(int clientId);

    // Update doesn't necessarily make sense in this case... They can be added to/removed from accounts

    /**
     * Remove a client from the list of owners of an account.
     * @param account The account object containing the account_id to remove the client from.
     * @param client The client object containing the client_id to remove from the account.
     * @return True if the deletion was successful, false otherwise.
     */
    boolean deleteOwner(Account account, Client client);

    /**
     * Remove a client from the list of owners of an account.
     * @param accountId The account_id for the account which we are trying to remove the client.
     * @param clientId The client_id of the client we are trying to remove.
     * @return True if the deletion is successful, false otherwise.
     */
    boolean deleteOwner(int accountId, int clientId);
}
