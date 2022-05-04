package dev.simmons.service;

import dev.simmons.entities.Account;
import dev.simmons.entities.Client;
import dev.simmons.utilities.lists.List;

/**
 * The business rules associated with the given bank.
 */
public interface Bank {
    /**
     * Deposit a specified amount to a given account
     * @param account The account object containing the current balance and account_id.
     * @param amount The double amount to be deposited.
     * @return True if the deposit happens successfully, false otherwise.
     */
    boolean deposit(Account account, double amount);

    /**
     * Withdraw a specified amount from a given account.
     * @param account The account object containing the current balance and account_id.
     * @param amount The double amount to be withdrawn.
     * @return True if the withdrawal is successful, false otherwise.
     */
    boolean withdraw(Account account, double amount);

    /**
     * Transfer from a given account a specific amount to another account.
     * @param from The account object containing the current balance and account_id from which the amount is being withdrawn.
     * @param to The account object containing the current balance and account_id to which the amount is being deposited.
     * @param amount The double amount to be transferred.
     * @return True if the transfer is successful, false otherwise.
     */
    boolean transfer(Account from, Account to, double amount);

    /**
     * Retrieve account information for a given client.
     * @param clientId The client_id for the client whose account info we want.
     * @return A List containing the client's accounts, an empty list if none exist.
     */
    List<Account> getAccountInfo(int clientId);

    /**
     * Get the owners associated with a given account. Since an owner-less account should be closed, the list returned should be non-empty.
     * @param accountId The account_id for the account we're looking for the owners.
     * @return A List containing the owners of a given account.
     */
    List<Client> getOwners(int accountId);

    /**
     * Get a client associated with a given client_id.
     * @param clientId The client_id associated with the user.
     * @return A client object containing all the fields associated with that client.
     */
    Client getClient(int clientId);

    /**
     * Get a client associated with a given client_id or client_username.
     * @param client The client object with the required fields.
     * @return A client object containing all the fields associated with that client.
     */
    Client getClient(Client client);

    /**
     * Get a client associated with a given client_username.
     * @param username The client_username associated with the client.
     * @return A client object containing all the fields associated with that client.
     */
    Client getClient(String username);

    /**
     * Get account information associated with a given account_id.
     * @param accountId The account_id to search for.
     * @return An account object containing all the fields of the given account_id.
     */
    Account getAccount(int accountId);

    /**
     * Get full account information, including complete list of owners, for all accounts currently active.
     * @return A list of accounts if any are found, an empty list otherwise.
     */
    List<Account> getFullAccountInfo();

    /**
     * Retrieve a list of accounts of which a given client is the sole owner.
     * @param clientId The client_id of the given client.
     * @return A List containing the accounts.
     */
    List<Account> getAccountsSolelyOwned(int clientId);

    /**
     * Retrieve a list of accounts of which a given client is the sole owner.
     * @param client The client object containing the client_id of the given client.
     * @return A List containing the accounts.
     */
    List<Account> getAccountsSolelyOwned(Client client);

    /**
     * Create a new client.
     * @param client The client object with all the fields set representing the new client.
     * @return True if the client is inserted successfully, false otherwise.
     */
    boolean registerClient(Client client);

    /**
     * Open a new account.
     * @param account The account object with all the fields set representing the new account.
     * @param owner The client object representing the new owner containing at least the client_id.
     * @return True if the account was successfully created.
     */
    boolean createAccount(Account account, Client owner);

    /**
     * Add an owner to the list of owners associated with an account.
     * @param account The account object containing the account_id.
     * @param owner The client object containing the new co-owner's client_id.
     * @return True if the ownership was added, false otherwise.
     */
    boolean addOwner(Account account, Client owner);

    /**
     * Remove an owner from the list of owner associated with an account.
     * @param account The account object containing the account_id.
     * @param owner The client object containing the former co-owner's client_id.
     * @return True if the ownership was added, false otherwise.
     */
    boolean removeOwner(Account account, Client owner);

    /**
     * Close out an account.
     * @param account The account objected containing at least the account_id of the account to be closed.
     * @param client The client object containing at least the client_id of the owner of the account.
     * @return True if the account was successfully closed, false otherwise.
     */
    boolean closeAccount(Account account, Client client);

    /**
     * Close out a client, remove their ownership of any accounts and closing out all solely owned accounts.
     * @param client The client object containing at least the client_id of the given client.
     * @return True if the client was closed out successfully, false otherwise.
     */
    boolean closeClient(Client client);
}
