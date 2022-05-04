package dev.simmons.data;

import dev.simmons.entities.Account;
import dev.simmons.utilities.lists.List;
import dev.simmons.utilities.logging.Logger;
import dev.simmons.utilities.connection.PostgresConnection;

import java.sql.*;

public class PostgresAccountDAO implements AccountDAO {
    @Override
    public Account createAccount(Account account) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "insert into account (account_balance, account_type) values (?, ?);";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setDouble(1, account.getBalance());
            statement.setString(2, account.getType().name());

            int updated = statement.executeUpdate();

            if (updated == 1) {
                ResultSet rs = statement.getGeneratedKeys();
                rs.next();
                account.setId(rs.getInt(1));

                return account;
            }

            return null;
        } catch (SQLException | NullPointerException se) {
            // Logging here
            Logger.log(Logger.Level.ERROR, se);
        }

        return null;
    }

    @Override
    public Account getAccount(int id) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "select * from account where account_id = ?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();
            if (rs == null) {
                return null;
            }

            rs.next();
            Account account = Account.accountFactory(rs.getString("account_type"));
            account.setId(rs.getInt("account_id"));
            account.setBalance(rs.getDouble("account_balance"));

            return account;
        } catch (SQLException | NullPointerException se) {
            Logger.log(Logger.Level.ERROR, se);
        }

        return null;
    }

    @Override
    public boolean updateAccount(Account newAccount) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "update account set account_balance = ?, account_type = ? where account_id = ?;";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setDouble(1, newAccount.getBalance());
            statement.setString(2, newAccount.getType().name());
            statement.setInt(3, newAccount.getId());

            int updated = statement.executeUpdate();

            return updated == 1;
        } catch (SQLException | NullPointerException se) {
            Logger.log(Logger.Level.ERROR, se);
        }

        return false;
    }

    @Override
    public boolean updateAccounts(Account... accounts) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "update account set account_balance = ?, account_type = ? where account_id = ?;";
            conn.setAutoCommit(false);
            int updated = 0;
            for (Account account : accounts) {
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setDouble(1, account.getBalance());
                statement.setString(2, account.getType().name());
                statement.setInt(3, account.getId());
                updated = statement.executeUpdate();
                if (updated != 1) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch(SQLException se) {
            Logger.log(Logger.Level.ERROR, se);
        }
        return false;
    }

    @Override
    public boolean deleteAccount(Account account) {
        return deleteAccount(account.getId());
    }

    @Override
    public boolean deleteAccount(int id) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "delete from account where account_id = ?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);

            int updated = statement.executeUpdate();

            return updated == 1;
        } catch (SQLException | NullPointerException se) {
            Logger.log(Logger.Level.ERROR, se);
        }

        return false;
    }
}
