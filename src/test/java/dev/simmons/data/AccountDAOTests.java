package dev.simmons.data;

import dev.simmons.data.AccountDAO;
import dev.simmons.data.PostgresAccountDAO;
import dev.simmons.entities.Account;
import dev.simmons.entities.CDAccount;
import dev.simmons.entities.CheckingAccount;
import dev.simmons.entities.SavingsAccount;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountDAOTests {
    private static AccountDAO accountDAO;
    private static Account checking;
    private static Account savings;
    private static Account cd;
    @Test
    @Order(1)
    public void createAccounts() {
        accountDAO = new PostgresAccountDAO();

        checking = Account.accountFactory(Account.AccountType.Checking.name());
        Assertions.assertTrue(checking instanceof CheckingAccount);
        Assertions.assertEquals(Account.AccountType.Checking, checking.getType());
        Assertions.assertEquals(0, checking.getBalance());
        Account account = accountDAO.createAccount(checking);
        Assertions.assertNotNull(account);
        Assertions.assertNotEquals(0, account.getId());

        savings = Account.accountFactory(Account.AccountType.Savings.name());
        Assertions.assertTrue(savings instanceof SavingsAccount);
        Assertions.assertEquals(Account.AccountType.Savings, savings.getType());
        account = accountDAO.createAccount(savings);
        Assertions.assertNotNull(account);
        Assertions.assertNotEquals(0, account.getId());

        cd = Account.accountFactory(Account.AccountType.CD.name());
        Assertions.assertTrue(cd instanceof CDAccount);
        Assertions.assertEquals(Account.AccountType.CD, cd.getType());
        account = accountDAO.createAccount(cd);
        Assertions.assertNotNull(account);
        Assertions.assertNotEquals(0, account.getId());
    }

    @Test
    @Order(2)
    public void getAccount() {
        Account account = accountDAO.getAccount(checking.getId());
        Assertions.assertEquals(account, checking);

        account = accountDAO.getAccount(cd.getId());
        Assertions.assertEquals(account, cd);

        account = accountDAO.getAccount(savings.getId());
        Assertions.assertEquals(account, savings);
    }

    @Test
    @Order(3)
    public void updateAccount() {
        checking.deposit(100000);
        accountDAO.updateAccount(checking);
        Account received = accountDAO.getAccount(checking.getId());
        Assertions.assertNotNull(received);
        Assertions.assertEquals(checking.getBalance(), received.getBalance());

        cd.deposit(10000);
        accountDAO.updateAccount(cd);
        received = accountDAO.getAccount(cd.getId());
        Assertions.assertNotNull(received);
        Assertions.assertEquals(cd.getBalance(), received.getBalance());

        savings.deposit(10000);
        accountDAO.updateAccount(savings);
        received = accountDAO.getAccount(savings.getId());
        Assertions.assertNotNull(received);
        Assertions.assertEquals(savings.getBalance(), received.getBalance());
    }

    @Test
    @Order(4)
    public void updateAccounts() {
        checking.withdraw(1000);
        cd.withdraw(1000);
        savings.withdraw(1000);
        Assertions.assertTrue(accountDAO.updateAccounts(cd, checking, savings));

        Account received = accountDAO.getAccount(checking.getId());
        Assertions.assertEquals(checking.getBalance(), received.getBalance());

        received = accountDAO.getAccount(cd.getId());
        Assertions.assertEquals(cd.getBalance(), received.getBalance());

        received = accountDAO.getAccount(savings.getId());
        Assertions.assertEquals(savings.getBalance(), received.getBalance());
    }

    @Test
    @Order(5)
    public void deleteAccount() {
        Assertions.assertTrue(accountDAO.deleteAccount(checking.getId()));
        Assertions.assertTrue(accountDAO.deleteAccount(cd.getId()));
        Assertions.assertTrue(accountDAO.deleteAccount(savings.getId()));

        Account received = accountDAO.getAccount(checking.getId());
        Assertions.assertNull(received);

        received = accountDAO.getAccount(cd.getId());
        Assertions.assertNull(received);

        received = accountDAO.getAccount(savings.getId());
        Assertions.assertNull(received);
    }
}
