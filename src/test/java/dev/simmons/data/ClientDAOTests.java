package dev.simmons.data;

import dev.simmons.entities.Client;
import dev.simmons.utilities.hashing.HashUtil;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientDAOTests {
    private static Client client;
    private static ClientDAO clientDAO;

    @Test
    @Order(1)
    public void createClient() {
        ClientDAOTests.clientDAO = new PostgresClientDAO();
        ClientDAOTests.client = new Client();
        client.setClientName("Testing1");
        client.setClientUsername("testing1");
        client.hashClientPassword("testing1");
        Client received = clientDAO.createClient(client);
        Assertions.assertNotNull(received);
        client.setClientId(received.getClientId());
        Assertions.assertTrue(received.getClientId() > 0);
        Assertions.assertEquals(received.getClientName(), client.getClientName());
        Assertions.assertNotEquals("testing1", client.getClientPassword());
        Assertions.assertNotNull(received.getClientSalt());
    }

    @Test
    @Order(2)
    public void getClient() {
        int id = client.getClientId();

        Client received = clientDAO.getClient(id);
        Client nothing = clientDAO.getClient(id+1);

        Assertions.assertNull(nothing);
        Assertions.assertNotNull(received);

        Assertions.assertEquals(client, received);
    }

    @Test
    @Order(3)
    public void updateClient() {
        ClientDAOTests.client.setClientName("Testing2");
        ClientDAOTests.client.setClientUsername("testing2");
        client.setClientPassword("testing2");
        boolean received = clientDAO.updateClient(client);
        Assertions.assertTrue(received);
    }

    @Test
    @Order(4)
    public void deleteClient() {
        Assertions.assertTrue(ClientDAOTests.clientDAO.deleteClient(ClientDAOTests.client));
        Assertions.assertNull(clientDAO.getClient(client.getClientId()));
    }
}
