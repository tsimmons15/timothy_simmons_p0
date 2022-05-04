package dev.simmons.data;

import dev.simmons.entities.Client;

/**
 * The Data Access Object to handle manipulation of the Client table.
 */
public interface ClientDAO {
    /**
     * Persist a new client into the database.
     * @param client The client object with the values to add into the database.
     * @return The client object representing the full client record successfully inserted. Null if the insertion fails.
     */
    Client createClient(Client client);

    /**
     * Retrieves a client from the database.
     * @param id The client_id of the client that should be retrieved.
     * @return The client object representing the client found, null if nothing is found.
     */
    Client getClient(int id);

    /**
     * Retrieves a client from the database.
     * @param username The username belonging to the client you're looking for.
     * @return The client that has the username if one is found, null otherwise.
     */
    Client getClient(String username);

    /**
     * Updates the record for a given client with new values.
     * @param newClient The client object with the new values to be updated.
     * @return True if the update is successful, false otherwise.
     */
    boolean updateClient(Client newClient);

    /**
     * Remove a client from the database.
     * @param client The client object with the client_id to be removed.
     * @return True if the deletion is successful, false otherwise.
     */
    boolean deleteClient(Client client);

    /**
     * Remove a client from the database.
     * @param id The client_id of the client to be removed.
     * @return True if the deletion is successful, false otherwise.
     */
    boolean deleteClient(int id);
}
