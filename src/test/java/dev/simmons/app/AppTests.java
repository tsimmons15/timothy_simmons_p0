package dev.simmons.app;

import dev.simmons.entities.Client;
import dev.simmons.service.Bank;
import dev.simmons.service.Banking;
import dev.simmons.utilities.hashing.HashUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AppTests {
    @Test
    public void loginClient() {
        Bank bank = new Banking();

        Client client = new Client();
        byte[] clientSalt = client.getClientSalt();
        client.setClientName("name");
        client.setClientUsername("namename");
        client.hashClientPassword("usingname");
        Assertions.assertTrue(bank.registerClient(client));
        Client received = bank.getClient("namename");
        Assertions.assertNotNull(received);
        Assertions.assertEquals(client, received);
        Assertions.assertEquals(clientSalt.length, received.getClientSalt().length);
        for (int i = 0; i < clientSalt.length; i++) {
            Assertions.assertEquals(clientSalt[i], received.getClientSalt()[i]);
        }

        Client other = new Client();
        byte[] otherSalt = other.getClientSalt();
        other.setClientName("othername");
        other.setClientUsername("othername");
        other.hashClientPassword("otherPassword");
        Assertions.assertTrue(bank.registerClient(other));
        Client otherReceived = bank.getClient("othername");
        Assertions.assertNotNull(otherReceived);
        Assertions.assertEquals(other, otherReceived);
        Assertions.assertEquals(otherSalt.length, otherReceived.getClientSalt().length);
        for (int i = 0; i < otherSalt.length; i++) {
            Assertions.assertEquals(otherSalt[i], otherReceived.getClientSalt()[i]);
        }
        String invalidUsername = "doesntexist";

        Assertions.assertNull(bank.getClient(invalidUsername));
        Assertions.assertEquals(HashUtil.hashSaltedString("usingname", clientSalt),
                received.getClientPassword());
        Assertions.assertEquals(HashUtil.hashSaltedString("otherPassword", otherSalt),
                otherReceived.getClientPassword());

        Assertions.assertTrue(bank.closeClient(client));
        Assertions.assertTrue(bank.closeClient(other));
    }
}
