package dev.simmons.data;

import dev.simmons.entities.Client;
import dev.simmons.utilities.logging.Logger;
import dev.simmons.utilities.connection.PostgresConnection;

import java.sql.*;
import org.postgresql.util.Base64;

public class PostgresClientDAO implements ClientDAO{
    @Override
    public Client createClient(Client client) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "insert into client (client_name, client_username, " +
                    "client_password, client_salt) values (?, ?, ?, ?);";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, client.getClientName());
            statement.setString(2, client.getClientUsername());
            statement.setString(3, client.getClientPassword());
            statement.setString(4, Base64.encodeBytes(client.getClientSalt()));

            int updated = statement.executeUpdate();
            if (updated != 1) {
                return null;
            }

            int id = 0;
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            id = rs.getInt(1);
            client.setClientId(id);

            return client;
        } catch (SQLException | NullPointerException npe) {
            Logger.log(Logger.Level.ERROR, npe);
        }

        return null;
    }

    @Override
    public Client getClient(int id) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "select * from client where client_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();

            if (rs == null) {
                return null;
            }

            Client client = new Client();
            rs.next();
            client.setClientId(rs.getInt("client_id"));
            client.setClientName(rs.getString("client_name"));
            client.setClientPassword(rs.getString("client_password"));
            client.setClientSalt(Base64.decode(rs.getString("client_salt")));

            return client;
        } catch (SQLException | NullPointerException npe) {
            Logger.log(Logger.Level.ERROR, npe);
        }

        return null;
    }

    @Override
    public Client getClient(String username) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "select * from client where client_username = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);

            ResultSet rs = statement.executeQuery();

            if (rs == null) {
                return null;
            }

            Client client = new Client();
            rs.next();
            client.setClientId(rs.getInt("client_id"));
            client.setClientName(rs.getString("client_name"));
            client.setClientUsername(rs.getString("client_username"));
            client.setClientPassword(rs.getString("client_password"));
            client.setClientSalt(Base64.decode(rs.getString("client_salt")));

            return client;
        } catch (SQLException | NullPointerException npe) {
            Logger.log(Logger.Level.ERROR, npe);
        }

        return null;
    }

    @Override
    public boolean updateClient(Client newClient) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "update client set client_name = ?, client_username = ?, client_password = ?, client_salt = ? where client_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, newClient.getClientName());
            statement.setString(2, newClient.getClientUsername());
            statement.setString(3, newClient.getClientPassword());
            statement.setString(4, Base64.encodeBytes(newClient.getClientSalt()));
            statement.setInt(5, newClient.getClientId());

            int updated = statement.executeUpdate();

            return updated == 1;
        } catch (SQLException | NullPointerException npe) {
            Logger.log(Logger.Level.ERROR, npe);
        }

        return false;
    }

    @Override
    public boolean deleteClient(Client client) {
        return deleteClient(client.getClientId());
    }

    @Override
    public boolean deleteClient(int id) {
        try (Connection conn = PostgresConnection.getConnection()) {
            String sql = "delete from client where client_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);

            int updated = statement.executeUpdate();
            if (updated == 1) {
                return true;
            }

            return false;
        } catch (SQLException | NullPointerException npe) {
            Logger.log(Logger.Level.ERROR, npe);
        }

        return false;
    }
}
