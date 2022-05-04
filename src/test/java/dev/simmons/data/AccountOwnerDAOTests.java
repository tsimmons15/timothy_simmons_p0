package dev.simmons.data;

import dev.simmons.data.*;
import dev.simmons.entities.Account;
import dev.simmons.entities.CheckingAccount;
import dev.simmons.entities.Client;
import dev.simmons.utilities.lists.LinkedList;
import dev.simmons.utilities.lists.List;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountOwnerDAOTests {
    private static AccountOwnerDAO aoDAO;
    private static AccountDAO accountDAO;
    private static ClientDAO clientDAO;
    private static List<Account> accounts;
    private static List<Client> clients;

    @Test
    @Order(1)
    public void addOwners() {
        aoDAO = new PostgresAccountOwnerDAO();
        accountDAO = new PostgresAccountDAO();
        clientDAO = new PostgresClientDAO();

        accounts = new LinkedList<>();
        clients = new LinkedList<>();

        for(int i = 0; i < 5; i++) {
            Account account = new CheckingAccount();
            account = accountDAO.createAccount(account);
            accounts.add(account);

            Client client = new Client();
            client.setClientUsername("testing" + i);
            client.setClientName("Testing " + i);
            client.setClientPassword("testing" + i);
            client = clientDAO.createClient(client);
            clients.add(client);

            Assertions.assertTrue(aoDAO.addOwner(account.getId(), client.getClientId()));

            Assertions.assertEquals(1, aoDAO.getAccounts(client.getClientId()).length());
            Assertions.assertEquals(1, aoDAO.getOwners(account.getId()).length());
        }

        Assertions.assertTrue(aoDAO.addOwner(accounts.get(0), clients.get(1)));
        Assertions.assertTrue(aoDAO.addOwner(accounts.get(0), clients.get(2)));
        Assertions.assertTrue(aoDAO.addOwner(accounts.get(0), clients.get(3)));
        Assertions.assertTrue(aoDAO.addOwner(accounts.get(0), clients.get(4)));

        Assertions.assertTrue(aoDAO.addOwner(accounts.get(1), clients.get(2)));
        Assertions.assertTrue(aoDAO.addOwner(accounts.get(1), clients.get(3)));
        Assertions.assertTrue(aoDAO.addOwner(accounts.get(1), clients.get(4)));


        Assertions.assertTrue(aoDAO.addOwner(accounts.get(2), clients.get(3)));
        Assertions.assertTrue(aoDAO.addOwner(accounts.get(2), clients.get(4)));

        Assertions.assertTrue(aoDAO.addOwner(accounts.get(3), clients.get(4)));

        Assertions.assertFalse(aoDAO.addOwner(accounts.get(0).getId(), 100));
    }

    @Test
    @Order(2)
    public void clientAccounts() {
        Assertions.assertEquals(1, aoDAO.getAccounts(clients.get(0)).length());
        Assertions.assertEquals(2, aoDAO.getAccounts(clients.get(1)).length());
        Assertions.assertEquals(3, aoDAO.getAccounts(clients.get(2)).length());
        Assertions.assertEquals(4, aoDAO.getAccounts(clients.get(3)).length());
        Assertions.assertEquals(5, aoDAO.getAccounts(clients.get(4)).length());
    }

    @Test
    @Order(3)
    public void accountOwners() {
        Assertions.assertEquals(5, aoDAO.getOwners(accounts.get(0)).length());
        Assertions.assertEquals(4, aoDAO.getOwners(accounts.get(1)).length());
        Assertions.assertEquals(3, aoDAO.getOwners(accounts.get(2)).length());
        Assertions.assertEquals(2, aoDAO.getOwners(accounts.get(3)).length());
        Assertions.assertEquals(1, aoDAO.getOwners(accounts.get(4)).length());
    }

    @Test
    @Order(4)
    public void removeOwners() {

        //Remove everyone from the accounts except for 1.
        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(0), clients.get(1)));
        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(0), clients.get(2)));
        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(0), clients.get(3)));
        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(0), clients.get(4)));

        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(1), clients.get(2)));
        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(1), clients.get(3)));
        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(1), clients.get(4)));

        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(2), clients.get(3)));
        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(2), clients.get(4)));

        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(3), clients.get(4)));

        //Recheck the clients
        Assertions.assertEquals(1, aoDAO.getAccounts(clients.get(0)).length());
        Assertions.assertEquals(1, aoDAO.getAccounts(clients.get(1)).length());
        Assertions.assertEquals(1, aoDAO.getAccounts(clients.get(2)).length());
        Assertions.assertEquals(1, aoDAO.getAccounts(clients.get(3)).length());
        Assertions.assertEquals(1, aoDAO.getAccounts(clients.get(4)).length());

        // Recheck the accounts
        Assertions.assertEquals(1, aoDAO.getOwners(accounts.get(0)).length());
        Assertions.assertEquals(1, aoDAO.getOwners(accounts.get(1)).length());
        Assertions.assertEquals(1, aoDAO.getOwners(accounts.get(2)).length());
        Assertions.assertEquals(1, aoDAO.getOwners(accounts.get(3)).length());
        Assertions.assertEquals(1, aoDAO.getOwners(accounts.get(4)).length());

        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(0), clients.get(0)));
        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(1), clients.get(1)));
        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(2), clients.get(2)));
        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(3), clients.get(3)));
        Assertions.assertTrue(aoDAO.deleteOwner(accounts.get(4), clients.get(4)));

        for (int i = 0; i < clients.length(); i++) {
            clientDAO.deleteClient(clients.get(i));
        }
        for (int i = 0; i < accounts.length(); i++) {
            accountDAO.deleteAccount(accounts.get(i));
        }
    }
}
