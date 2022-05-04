package dev.simmons.service;

import dev.simmons.entities.Account;
import dev.simmons.entities.Client;
import dev.simmons.utilities.lists.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BankTests {
    @Test
    public void handleClosingCoOwnedAccount() {
        Bank bank = new Banking();
        Client client = new Client();
        client.setClientName("Testing1");
        client.setClientUsername("testing1");
        client.hashClientPassword("testing1");
        Assertions.assertTrue(bank.registerClient(client));
        Account account = Account.accountFactory(Account.AccountType.Checking.name());
        Assertions.assertTrue(bank.createAccount(account, client));

        Client coowner = new Client();
        coowner.setClientName("Testing2");
        coowner.setClientUsername("testing2");
        coowner.setClientPassword("testing2");
        Assertions.assertTrue(bank.registerClient(coowner));
        Assertions.assertTrue(bank.addOwner(account, coowner));

        Client coowner2 = new Client();
        coowner2.setClientName("Testing3");
        coowner2.setClientUsername("testing3");
        coowner2.setClientPassword("testing2");
        Assertions.assertTrue(bank.registerClient(coowner2));
        Assertions.assertTrue(bank.addOwner(account, coowner2));

        Assertions.assertEquals(3, bank.getOwners(account.getId()).length());

        Assertions.assertTrue(bank.removeOwner(account, client));
        Assertions.assertEquals(2, bank.getOwners(account.getId()).length());

        Assertions.assertFalse(bank.removeOwner(account, client));
        Assertions.assertEquals(2, bank.getOwners(account.getId()).length());

        Assertions.assertTrue(bank.removeOwner(account, coowner));
        Assertions.assertEquals(1, bank.getOwners(account.getId()).length());

        Assertions.assertTrue(bank.removeOwner(account, coowner2));
        Assertions.assertEquals(0, bank.getOwners(account.getId()).length());

        // An account with no owners should be closed out.
        Assertions.assertNull(bank.getAccount(account.getId()));

        Assertions.assertTrue(bank.closeClient(client));
        Assertions.assertTrue(bank.closeClient(coowner2));
        Assertions.assertTrue(bank.closeClient(coowner));
    }

    @Test
    public void handleCheckingTransactions() {
        Bank bank = new Banking();
        Client client = new Client();
        client.setClientName("Testing1");
        client.setClientUsername("testing1");
        client.hashClientPassword("testing1");
        Assertions.assertTrue(bank.registerClient(client));
        Account account = Account.accountFactory(Account.AccountType.Checking.name());
        Assertions.assertTrue(bank.createAccount(account, client));

        Assertions.assertFalse(bank.withdraw(account,100));
        Assertions.assertFalse(bank.withdraw(account, -100));
        // If this fails, may need to reget the account. I'm trying to get away with using the object reference
        Assertions.assertEquals(0, account.getBalance());

        Assertions.assertFalse(bank.deposit(account,-100));
        Assertions.assertEquals(0, account.getBalance());

        Assertions.assertFalse(bank.deposit(account, Double.MAX_VALUE));
        Assertions.assertEquals(0, account.getBalance());

        Assertions.assertTrue(bank.deposit(account, 500000));
        double expected = 500000 * (1.00 + account.getType().interest);
        Assertions.assertEquals(expected, account.getBalance(), .01);

        Assertions.assertTrue(bank.withdraw(account,1000));
        expected -= 1000 * (1.00 + account.getType().interest);
        Assertions.assertEquals(expected, account.getBalance(), .01);

        // Client is leaving, so should close out accounts (if they are the only owners)
        bank.closeClient(client);
        Assertions.assertNull(bank.getClient(client));
        Assertions.assertEquals(0, bank.getAccountInfo(client.getClientId()).length());
    }

    @Test
    public void handleSavingsTransactions() {
        Bank bank = new Banking();
        Client client = new Client();
        client.setClientName("Testing1");
        client.setClientUsername("testing1");
        client.hashClientPassword("testing1");
        Assertions.assertTrue(bank.registerClient(client));
        Account account = Account.accountFactory(Account.AccountType.Savings.name());
        Assertions.assertTrue(bank.createAccount(account, client));

        Assertions.assertFalse(bank.withdraw(account,100));
        Assertions.assertFalse(bank.withdraw(account, -100));
        // If this fails, may need to reget the account. I'm trying to get away with using the object reference
        Assertions.assertEquals(0, account.getBalance());

        Assertions.assertFalse(bank.deposit(account,-100));
        Assertions.assertEquals(0, account.getBalance());

        Assertions.assertFalse(bank.deposit(account, Double.MAX_VALUE));
        Assertions.assertEquals(0, account.getBalance());

        Assertions.assertTrue(bank.deposit(account, 500000));
        double expected = 500000 * (1.00 + account.getType().interest);
        Assertions.assertEquals(expected, account.getBalance(), .01);

        Assertions.assertTrue(bank.withdraw(account,1000));
        expected -= 1000 * (1.00 + account.getType().interest * 1.01);
        Assertions.assertEquals(expected, account.getBalance(), .01);

        bank.closeClient(client);
        Assertions.assertNull(bank.getClient(client));
        Assertions.assertEquals(0, bank.getAccountInfo(client.getClientId()).length());
    }

    @Test
    public void handleCDTransactions() {
        Bank bank = new Banking();
        Client client = new Client();
        client.setClientName("Testing1");
        client.setClientUsername("testing1");
        client.hashClientPassword("testing1");
        Assertions.assertTrue(bank.registerClient(client));
        Account account = Account.accountFactory(Account.AccountType.CD.name());
        Assertions.assertTrue(bank.createAccount(account, client));

        Assertions.assertFalse(bank.withdraw(account,100));
        Assertions.assertFalse(bank.withdraw(account, -100));
        // If this fails, may need to reget the account. I'm trying to get away with using the object reference
        Assertions.assertEquals(0, account.getBalance());

        Assertions.assertFalse(bank.deposit(account,-100));
        Assertions.assertEquals(0, account.getBalance());

        Assertions.assertFalse(bank.deposit(account, Double.MAX_VALUE));
        Assertions.assertEquals(0, account.getBalance());

        Assertions.assertTrue(bank.deposit(account, 500000));
        double expected = 500000 * (1.00 + account.getType().interest);
        Assertions.assertEquals(expected, account.getBalance(), .01);

        Assertions.assertTrue(bank.withdraw(account,1000));
        expected -= 1000 * (1.00 + account.getType().interest * 1.5);
        Assertions.assertEquals(expected, account.getBalance(), .01);

        bank.closeClient(client);
        Assertions.assertNull(bank.getClient(client));
        Assertions.assertEquals(0, bank.getAccountInfo(client.getClientId()).length());
    }

    @Test
    public void handleTransfers() {
        Account cd = Account.accountFactory(Account.AccountType.CD.name());
        Account checking = Account.accountFactory(Account.AccountType.Checking.name());
        Account otherChecking = Account.accountFactory(Account.AccountType.Checking.name());

        Client client = new Client();
        client.setClientName("Testing 1");
        client.setClientUsername("testing1");
        client.setClientPassword("testing1");

        Client other = new Client();
        other.setClientName("Testing 2");
        other.setClientUsername("otherTesting");
        other.setClientPassword("other");

        Bank bank = new Banking();
        Assertions.assertTrue(bank.registerClient(client));
        Assertions.assertTrue(bank.registerClient(other));

        Assertions.assertTrue(bank.createAccount(cd, client));
        Assertions.assertTrue(bank.createAccount(checking, client));
        Assertions.assertTrue(bank.createAccount(otherChecking, other));

        Assertions.assertEquals(2, bank.getAccountInfo(client.getClientId()).length());
        Assertions.assertEquals(1, bank.getAccountInfo(other.getClientId()).length());

        Assertions.assertTrue(bank.deposit(cd, 100000));
        Assertions.assertTrue(bank.deposit(checking, 100000));
        Assertions.assertTrue(bank.deposit(otherChecking, 100000));


        // Owned account transferring
        Assertions.assertTrue(bank.transfer(cd, checking, 1000));
        Assertions.assertTrue(bank.transfer(checking, cd, 1000));
        Assertions.assertEquals(100000, bank.getAccount(checking.getId()).getBalance(), .01);
        double expected = 100000 * (1.00 + cd.getType().interest)
                            - 1000 * (1.00 + cd.getType().interest * 1.5)
                            + 1000 * (1.00 + cd.getType().interest);
        Assertions.assertEquals(expected, bank.getAccount(cd.getId()).getBalance(), .01);

        // Between client transferring
        Assertions.assertTrue(bank.transfer(checking, otherChecking, 1000));
        Assertions.assertEquals(100000 - 1000, bank.getAccount(checking.getId()).getBalance(), .01);
        Assertions.assertEquals(100000 + 1000, bank.getAccount(otherChecking.getId()).getBalance(), .01);

        Assertions.assertTrue(bank.closeClient(client));
        Assertions.assertTrue(bank.closeClient(other));
    }

    @Test
    public void handleFullAccountInfo() {
        Bank bank = new Banking();
        Client client = new Client();
        client.setClientName("Testing1");
        client.setClientUsername("testing1");
        client.hashClientPassword("testing1");
        Assertions.assertTrue(bank.registerClient(client));
        Account account = Account.accountFactory(Account.AccountType.Checking.name());
        Assertions.assertTrue(bank.createAccount(account, client));

        Client coowner = new Client();
        coowner.setClientName("Testing2");
        coowner.setClientUsername("testing2");
        coowner.setClientPassword("testing2");
        Assertions.assertTrue(bank.registerClient(coowner));
        Assertions.assertTrue(bank.addOwner(account, coowner));

        Client owner2 = new Client();
        owner2.setClientName("Testing3");
        owner2.setClientUsername("testing3");
        owner2.setClientPassword("testing2");
        Assertions.assertTrue(bank.registerClient(owner2));
        Account account2 = Account.accountFactory(Account.AccountType.Checking.name());
        Assertions.assertTrue(bank.createAccount(account2, owner2));
        Assertions.assertTrue(bank.addOwner(account2, coowner));

        List<Account> accounts = bank.getFullAccountInfo();
        Assertions.assertNotNull(accounts);
        Assertions.assertEquals(2, accounts.length());
        Assertions.assertNotNull(accounts.get(0).getOwners());
        Assertions.assertEquals(2, accounts.get(0).getOwners().length());
        Assertions.assertNotNull(accounts.get(1).getOwners());
        Assertions.assertEquals(2, accounts.get(1).getOwners().length());

        Assertions.assertTrue(bank.closeClient(client));
        Assertions.assertTrue(bank.closeClient(owner2));
        Assertions.assertTrue(bank.closeClient(coowner));
    }
}
