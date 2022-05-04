package dev.simmons.data;

import dev.simmons.entities.Account;
import dev.simmons.entities.Client;
import dev.simmons.utilities.logging.Logger;
import dev.simmons.utilities.connection.PostgresConnection;
import dev.simmons.utilities.lists.LinkedList;
import dev.simmons.utilities.lists.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresAccountOwnerDAO implements AccountOwnerDAO{
    @Override
    public boolean addOwner(int accountId, int clientId) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "insert into account_owner (account_id, client_id) values (?, ?);";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, accountId);
            statement.setInt(2, clientId);

            int updated = statement.executeUpdate();

            return updated == 1;
        } catch (SQLException | NullPointerException ex) {
            Logger.log(Logger.Level.INFO, ex);
        }

        return false;
    }

    @Override
    public boolean addOwner(Account account, Client client) {
        return addOwner(account.getId(), client.getClientId());
    }

    @Override
    public List<Client> getOwners(int accountId) {
        try (Connection conn = PostgresConnection.getConnection()) {
            // My thought process is, there's no reason they'd need to see the passwords/salt at this point...
            String sql = "select client_id, client_name, client_username from client where client.client_id in (" +
                    "select client_id from account_owner where account_id = ?);";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, accountId);

            ResultSet rs = statement.executeQuery();

            List<Client> owners = new LinkedList<>();

            while (rs.next()) {
                Client c = new Client();
                c.setClientId(rs.getInt("client_id"));
                c.setClientName(rs.getString("client_name"));
                c.setClientUsername(rs.getString("client_username"));
                owners.add(c);
            }

            return owners;
        } catch (SQLException | NullPointerException ex) {
            Logger.log(Logger.Level.ERROR, ex);
        }

        return null;
    }

    @Override
    public List<Client> getOwners(Account account) {
        return getOwners(account.getId());
    }

    @Override
    public List<Account> getAccounts(int clientId) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "select * from account where account_id in (select account_id from account_owner where client_id = ?);";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, clientId);

            ResultSet rs = statement.executeQuery();

            List<Account> accounts = new LinkedList<Account>();
            while (rs.next()) {
                Account account = Account.accountFactory(rs.getString("account_type"));
                account.setId(rs.getInt("account_id"));
                account.setBalance(rs.getDouble("account_balance"));
                accounts.add(account);
            }

            return accounts;
        } catch (SQLException | NullPointerException ex) {
            Logger.log(Logger.Level.ERROR, ex);
        }

        return null;
    }

    @Override
    public List<Account> getAccounts(Client client) {
        return getAccounts(client.getClientId());
    }

    /**
     * Gets all accounts, including populating the owner list, for a given client.
     *
     * Ideally, would get the full owner list (including co-owners), but currently
     *  I believe it's only returning the single owner in the where clause.
     * @param clientId The client_id whose accounts we're interested in.
     * @return The list of accounts if found, an empty list otherwise.
     */
    @Override
    public List<Account> getFullAccountInfo(int clientId) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "select *\n" +
                    "from account a \n" +
                    "inner join account_owner ao on a.account_id = ao.account_id \n" +
                    "inner join client c on c.client_id = ao.client_id \n" +
                    "where c.client_id = ? \n" +
                    "order by a.account_id";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, clientId);

            ResultSet rs = statement.executeQuery();
            List<Account> accounts = new LinkedList<>();
            int lastId = -1;
            int currId = -1;
            Account account = null;
            Client client = null;
            while (rs.next()) {
                currId = rs.getInt("account_id");
                if (currId != lastId) {
                    account = Account.accountFactory(rs.getString("account_type"));
                    account.setId(currId);
                    account.setBalance(rs.getDouble("account_balance"));
                    account.setOwners(new LinkedList<Client>());
                    accounts.add(account);
                    lastId = currId;
                }
                client = new Client();
                client.setClientName(rs.getString("client_name"));
                client.setClientUsername(rs.getString("client_username"));
                account.getOwners().add(client);
            }

            return accounts;
        } catch (SQLException se) {
            Logger.log(Logger.Level.ERROR, se);
        }

        return null;
    }

    @Override
    public List<Account> getFullAccountInfo() {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "select *\n" +
                    "from account a \n" +
                    "inner join account_owner ao on a.account_id = ao.account_id \n" +
                    "inner join client c on c.client_id = ao.client_id \n" +
                    "order by a.account_id";
            PreparedStatement statement = conn.prepareStatement(sql);

            ResultSet rs = statement.executeQuery();
            List<Account> accounts = new LinkedList<>();
            int lastId = -1;
            int currId = -1;
            Account account = null;
            Client client = null;
            while (rs.next()) {
                currId = rs.getInt("account_id");
                if (currId != lastId) {
                    account = Account.accountFactory(rs.getString("account_type"));
                    account.setId(currId);
                    account.setBalance(rs.getDouble("account_balance"));
                    account.setOwners(new LinkedList<Client>());
                    accounts.add(account);
                    lastId = currId;
                }
                client = new Client();
                client.setClientName(rs.getString("client_name"));
                client.setClientUsername(rs.getString("client_username"));
                account.getOwners().add(client);
            }

            return accounts;
        } catch (SQLException se) {
            Logger.log(Logger.Level.ERROR, se);
        }

        return null;
    }

    @Override
    public List<Account> getAccountsSolelyOwned(int clientId) {
        try (Connection conn = PostgresConnection.getConnection()) {
            // First: find the list of accounts that are solely owned.
            String sql = "select account_id from account_owner\n" +
                            "group by account_id\n" +
                            "having count(client_id) = 1;";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder("(");
            while (rs.next()) {
                sb.append(rs.getString(1) + ",");
            }
            if (sb.length() > 1) {
                sb.setCharAt(sb.length()-1, ')');
            } else {
                sb.append(" null )");
            }


            // With the list of solely owned accounts,
            // get the list of accounts owned by the client that show up in that list
            sql = "select * from account a inner join account_owner ao on a.account_id = ao.account_id " +
                    "where ao.client_id = ? and a.account_id in " + sb.toString() + ";";
            statement = conn.prepareStatement(sql);
            statement.setInt(1, clientId);

            rs = statement.executeQuery();

            List<Account> accounts = new LinkedList<Account>();
            while (rs.next()) {
                Account account = Account.accountFactory(rs.getString("account_type"));
                account.setId(rs.getInt("account_id"));
                account.setBalance(rs.getDouble("account_balance"));
                accounts.add(account);
            }

            return accounts;
        } catch (SQLException | NullPointerException ex) {
            Logger.log(Logger.Level.ERROR, ex);
        }

        return null;
    }

    @Override
    public boolean deleteOwner(Account account, Client client) {
        return deleteOwner(account.getId(), client.getClientId());
    }

    @Override
    public boolean deleteOwner(int accountId, int clientId) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "delete from account_owner where account_id = ? and client_id = ?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, accountId);
            statement.setInt(2, clientId);

            int updated = statement.executeUpdate();

            return updated == 1;
        } catch (SQLException | NullPointerException ex) {
            Logger.log(Logger.Level.ERROR, ex);
        }

        return false;
    }
}
