package dev.simmons.entities;

import dev.simmons.utilities.hashing.HashUtil;

import java.util.Random;

/**
 * The object representing a client in the underlying database.
 */
public class Client {
    private int clientId;
    private String clientUsername;
    private String clientName;
    private String clientPassword;
    private byte[] clientSalt;

    /**
     * Default constructor that randomly generates a 16 byte salt for the client.
     */
    public Client() {
        clientSalt = new byte[16];
        Random rand = new Random();
        rand.nextBytes(clientSalt);
    }

    /**
     * Gets the client_id representing this client in the database.
     * @return The client_id. Negative and 0 values represent values not in the database and shouldn't be queried.
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Sets the client_id representing this client in the database.
     * @param clientId The client_id.
     */
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    /**
     * Get the client's username.
     * @return The client's username.
     */
    public String getClientUsername() {
        return clientUsername;
    }

    /**
     * Set the client's username.
     * @param clientUsername The client's username.
     */
    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

    /**
     * Get the client's name, their given name not the account username.
     * @return The client's name.
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Set the client's name, their given name as opposed to the username associated with the account.
     * @param clientName The client's name.
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * Get the currently stored hashed and salted password.
     * @return The client's password, if it has been set.
     */
    public String getClientPassword() {
        return clientPassword;
    }

    /**
     * Set the client's password.
     * @param clientPassword The previously salted and hashed password.
     */
    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    /**
     * Salts and hashes the plaintext password of the user.
     * @param clientPassword The plaintext password.
     */
    public void hashClientPassword(String clientPassword) {
        this.clientPassword = HashUtil.hashSaltedString(clientPassword, this.clientSalt);
    }

    /**
     * Retrieves the salt for the given user.
     * @return The byte array of the salt.
     */
    public byte[] getClientSalt() {
        return clientSalt;
    }

    /**
     * Sets the salt for the given user. *Warning* Changing the password after resetting the salt would be required.
     * @param clientSalt A byte-array representing the user's salt.
     */
    public void setClientSalt(byte[] clientSalt) {
        this.clientSalt = clientSalt;
    }

    /**
     * Get diagnostic information representing the given client.
     * @return The formatted diagnostic string.
     */
    public String getDiagnostics() {
        return "[Client](id=" + clientId + ", name=" + this.getClientName() + ", username=" + this.getClientUsername() + ")";
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Client)) {
            return false;
        }

        Client other = (Client)o;
        return this.clientId == other.getClientId();
    }
}
