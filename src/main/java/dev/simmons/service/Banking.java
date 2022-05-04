package dev.simmons.service;

import dev.simmons.data.*;
import dev.simmons.entities.Account;
import dev.simmons.entities.Client;
import dev.simmons.utilities.lists.List;

public class Banking implements Bank{
    private final ClientDAO clientDAO;
    private final AccountDAO accountDAO;
    private final AccountOwnerDAO aoDAO;

    public Banking() {
        clientDAO = new PostgresClientDAO();
        accountDAO = new PostgresAccountDAO();
        aoDAO = new PostgresAccountOwnerDAO();
    }

    @Override
    public boolean deposit(Account account, double amount) {
        if (account == null) {
            return false;
        }

        boolean deposited = account.deposit(amount);
        if (!deposited){
            return false;
        }
        return accountDAO.updateAccount(account);
    }

    @Override
    public boolean withdraw(Account account, double amount) {
        if (account == null) {
            return false;
        }

        boolean withdrawn = account.withdraw(amount);
        if (!withdrawn) {
            return false;
        }
        return accountDAO.updateAccount(account);
    }

    @Override
    public boolean transfer(Account from, Account to, double amount) {
        if (from == null || to == null) {
            return false;
        }

        boolean result = from.withdraw(amount);
        result = result & to.deposit(amount);
        if (!result) {
            return false;
        }

        return accountDAO.updateAccounts(from, to);
    }

    @Override
    public List<Account> getAccountInfo(int clientId) {
        return aoDAO.getAccounts(clientId);
    }

    @Override
    public List<Client> getOwners(int accountId) {
        return aoDAO.getOwners(accountId);
    }

    @Override
    public Client getClient(int clientId) {
        return clientDAO.getClient(clientId);
    }

    @Override
    public Client getClient(Client client) {
        if (client == null || client.getClientUsername().length() <= 0 || client.getClientId() <= 0) {
            return null;
        }

        if (client.getClientUsername().length() > 0) {
            return getClient(client.getClientUsername());
        }
        return getClient(client.getClientId());
    }

    @Override
    public Client getClient(String username) {
        if (username == null || username.length() <= 0) {
            return null;
        }

        return clientDAO.getClient(username);
    }

    @Override
    public Account getAccount(int accountId) {
        return accountDAO.getAccount(accountId);
    }

    /*@Override
    public List<Account> getFullAccountInfo(int clientId) {
        return aoDAO.getFullAccountInfo(clientId);
    }*/

    @Override
    public List<Account> getFullAccountInfo() {
        return aoDAO.getFullAccountInfo();
    }

    @Override
    public List<Account> getAccountsSolelyOwned(int clientId) {
        return aoDAO.getAccountsSolelyOwned(clientId);
    }

    @Override
    public List<Account> getAccountsSolelyOwned(Client client) {
        return getAccountsSolelyOwned(client.getClientId());
    }

    @Override
    public boolean registerClient(Client client) {
        if (client == null) {
            return false;
        }
        return clientDAO.createClient(client) != null;
    }

    @Override
    public boolean createAccount(Account account, Client owner) {
        if (account == null || owner == null) {
            return false;
        }
        account = accountDAO.createAccount(account);
        return aoDAO.addOwner(account, owner);
    }

    @Override
    public boolean addOwner(Account account, Client owner) {
        if (account == null || owner == null) {
            return false;
        }
        return aoDAO.addOwner(account, owner);
    }

    @Override
    public boolean removeOwner(Account account, Client owner) {
        if (account == null || owner == null) {
            return false;
        }

        boolean result = aoDAO.deleteOwner(account, owner);
        List<Client> owners = aoDAO.getOwners(account);
        if (owners.length() > 0) {
            return result;
        }

        return result & accountDAO.deleteAccount(account);
    }

    @Override
    public boolean closeAccount(Account account, Client client) {
        return removeOwner(account, client);
    }

    @Override
    public boolean closeClient(Client client) {
        if (client == null) {
            return false;
        }

        List<Account> accounts = aoDAO.getAccounts(client.getClientId());
        Account account;
        boolean result = true;
        for(int i = 0; i < accounts.length(); i++) {
            account = accounts.get(i);
            result &= removeOwner(account, client);
        }

        result &= clientDAO.deleteClient(client);

        return result;
    }
}
