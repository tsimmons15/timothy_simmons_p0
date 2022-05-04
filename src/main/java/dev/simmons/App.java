package dev.simmons;

import dev.simmons.entities.Account;
import dev.simmons.entities.Client;
import dev.simmons.service.Bank;
import dev.simmons.service.Banking;
import dev.simmons.utilities.hashing.HashUtil;
import dev.simmons.utilities.lists.List;
import dev.simmons.utilities.logging.Logger;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {
    private static final Bank bank = new Banking();
    private static final Scanner in = new Scanner(System.in);
    public static void main(String[] args) {
        outerMenu();
        System.out.println("Until next time! Bis dann!");
    }

    public static void outerMenu() {
        int choice;
        do {
            choice = mainMenuPrompt();
            switch (choice) {
                case 1: // Login to existing account
                    Client curr = loginClient();
                    if (curr == null) {
                        continue;
                    }
                    userMenu(curr);
                    break;
                case 2: // New User
                    curr = registerClient();
                    if (curr == null) {
                        continue;
                    }
                    userMenu(curr);
                    break;
                case 3: // Contact Info!
                    System.out.println("We would love to hear from you!");
                    System.out.println("Our business hours are:");
                    System.out.println(" - Monday    10:00AM - 12:00PM, 2:00PM - 5:00PM");
                    System.out.println(" - Tuesday   10:00AM - 12:00PM, 2:00PM - 5:00PM");
                    System.out.println(" - Wednesday 10:00AM - 12:00PM, 2:00PM - 5:00PM");
                    System.out.println(" - Thursday  10:00AM - 12:00PM, 2:00PM - 5:00PM");
                    System.out.println(" - Friday    10:00AM - 12:00PM");
                    System.out.println(" - Saturday   9:00AM - 11:00PM");
                    System.out.println("Or, you can call our toll-free help-line: 1-800-669-2942!");
                    System.out.println("Our customer service help email: help@bank.com");
                    break;
                case 4: // Exit
                    System.out.println("We would love to hear from you! Please fill out our survey!");
                    break;
            }
        } while (choice != 4);
    }

    private static int mainMenuPrompt() {
        int choice = -1;

        do {
            System.out.println("1.) Login to existing account!");
            System.out.println("2.) Create new account!");
            System.out.println("3.) Contact Us!");
            System.out.println("4.) Exit");
            System.out.print("> ");
            try {
                choice = Integer.parseInt(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Please enter a valid option!");
            }
        } while (choice < 1 || choice > 4);

        return choice;
    }

    public static void userMenu(Client client) {
        int choice;
        do {
            choice = userMenuPrompt(client);

            switch (choice) {
                case 1:
                    createAccount(client);
                    break;
                case 2:
                    showAccountDetails(client);
                    break;
                case 3:
                    client = closeClient(client);
                    if (client == null) {
                        return;
                    }
                    break;
                case 4:
                    closeAccount(client);
                    break;
                case 5:
                    handleTransaction(client);
                    break;
            }
        } while (choice != 6);
    }

    private static int userMenuPrompt(Client client) {
        int choice = -1;

        System.out.println("Greetings, " + client.getClientName() + "! Welcome back!");
        do {
            System.out.println("1.) Create Account!");
            System.out.println("2.) View Account Details!");
            System.out.println("3.) Close All Accounts");
            System.out.println("4.) Close an Account");
            System.out.println("5.) Transactions!");
            System.out.println("6.) Return");
            System.out.print("> ");
            try {
                choice = Integer.parseInt(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Please enter a valid option!");
            }
        } while (choice < 1 || choice > 6);

        return choice;
    }

    public static Client registerClient() {
        Client client = new Client();
        System.out.print("First, enter your name: ");
        String name = "";
        while (name.length() <= 0) {
            try {
                name = in.nextLine();
            } catch (NoSuchElementException nsee) {
                System.out.println("Please, enter your name to continue.");
            }
        }
        client.setClientName(name);

        System.out.println("Excellent, nice to meet you," + name +"!");
        System.out.print(name + ", please enter the username for your account: ");
        String username = "";
        while (username.length() <= 0) {
            try {
                username = in.nextLine();
            } catch (NoSuchElementException nsee) {
                System.out.println("Please, enter a username for the account to continue.");
            }
        }
        client.setClientUsername(username);

        System.out.println("Excellent! You'll use this to log-in for the future. Don't forget it!");
        System.out.print("Finally, please enter the password for the account: ");
        String password = "";
        while (password.length() <= 0) {
            try {
                password = in.nextLine();
            } catch (NoSuchElementException nsee) {
                System.out.println("Please, enter the password for your account to continue.");
            }
        }
        client.hashClientPassword(password);

        System.out.println("Awesome!");
        boolean result = bank.registerClient(client);
        if (result) {
            System.out.println("You're all set!");
            System.out.println("Next step: set up an account!");
        } else {
            System.out.println("Oops! Something went wrong. Please see an associate for help.");
            Logger.log(Logger.Level.INFO, "Issue creating user for: " + client.getDiagnostics());
            return null;
        }

        return client;
    }

    public static Client closeClient(Client client) {
        System.out.print("We're sure sorry to see you go! Are you sure you wish to close out your accounts? [Y/N] ");
        String cont = "";
        do {
            try {
                cont = in.nextLine();
            } catch (NoSuchElementException nsee) {
                System.out.println("Oops! Please just [Y/N] on whether you want to close out all your accounts.");
            }
        } while (!cont.startsWith("y") && !cont.startsWith("n"));

        if (cont.startsWith("n")) {
            System.out.println("Phew! That was scary! Thank you for reconsidering!");
            return client;
        }

        System.out.println("We're sorry it has to end this way. Please let us know if there was anything we could have done better!");
        List<Account> accounts = bank.getAccountsSolelyOwned(client);

        if (accounts.length() <= 0) {
            if (bank.closeClient(client)) {
                System.out.println("Well, it was fun while it lasted...");
                Logger.log(Logger.Level.INFO, "Client account closed: " + client.getDiagnostics());
                return null;
            } else {
                System.out.println("Oops! Something went wrong! Please see an associate for assistance!");
                Logger.log(Logger.Level.WARNING, "Unable to close account for client " + client.getDiagnostics());
            }
        }

        double total = 0;
        for (Account account : accounts) {
            total += account.getBalance();
        }
        System.out.println("Regardless, no hard feelings. After completing, you will receive $" + total + " back.");
        System.out.print("Continue? [Y/N] ");
        do {
            try {
                cont = in.nextLine();
            } catch (NoSuchElementException nsee) {
                System.out.println("Oops! Please just [Y/N] on whether you want to continue closing your accounts.");
            }
        } while (!cont.startsWith("y") && !cont.startsWith("n"));

        if (cont.startsWith("y")) {
            System.out.println("Closing accounts...");
            if (bank.closeClient(client)) {
                System.out.println("Well, it was fun while it lasted...");
                Logger.log(Logger.Level.INFO, "Client account closed: " + client.getDiagnostics());
                return null;
            } else {
                System.out.println("Oops! Something went wrong! Please see an associate for assistance!");
                Logger.log(Logger.Level.WARNING, "Unable to close account for client " + client.getDiagnostics());
            }
        }

        return client;
    }

    public static void closeAccount(Client client) {
        List<Account> accounts = bank.getAccountsSolelyOwned(client);
        if (accounts.length() <= 0) {
            System.out.println("Wait... You haven't got any accounts you can close, yet!");
            System.out.println("Joint owners need to agree on closing of accounts.");
            return;
        }

        int index = 0;
        do {
            System.out.println("Which of the following accounts would you like to close out?");
            for (int i = 0; i < accounts.length(); i++) {
                System.out.println((i+1) + ".) " + accounts.get(i));
            }
            try {
                index = Integer.parseInt(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Make sure you enter a value from above!");
            }
        } while (index < 1 || index > accounts.length());
        Account removed = accounts.get(index-1);
        System.out.println("You'll receive $" + removed.getBalance() + " back for closing this account.");
        System.out.print("Are you sure you wish to close out this account? [Y/N] ");
        String cont = "";
        do {
            try {
                cont = in.nextLine();
            } catch (NoSuchElementException nsee) {
                System.out.println("Oops! Please just [Y/N] on whether you want to close out all your accounts.");
            }
        } while (!cont.startsWith("y") && !cont.startsWith("n"));

        if (cont.startsWith("y")) {
            System.out.println("Closing account...");
            if (bank.closeAccount(removed, client)) {
                accounts.remove(index - 1);
                if (accounts.length() != 0) {
                    handleClosedAccountTransfer(accounts, removed.getBalance());
                }
                Logger.log(Logger.Level.INFO, "Account closed " + removed.getDiagnostics() +
                        " for client " + client.getDiagnostics() + ".");
            } else {
                System.out.println("Oops! We were unable to close out the account. Please see an associate for assistance!");
                Logger.log(Logger.Level.WARNING, "Unable to close out account " +
                        removed.getDiagnostics() + " owned by client " + client.getDiagnostics());
            }
        }

    }

    public static void handleClosedAccountTransfer(List<Account> accounts, double amount) {
        int choice = 0;
        do {
            for(int i = 0; i < accounts.length(); i++) {
                System.out.println((i+1) + ".) " + accounts.get(i));
            }
            System.out.print("To which account would you like to deposit? ");
            try {
                choice = Integer.parseInt(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Please select one of the above options.");
            }
        } while (choice < 1 || choice > accounts.length());

        System.out.println("Excellent!");

        Account account = accounts.get(choice-1);

        System.out.println("Transferring...");
        if (bank.deposit(account, amount)) {
            Logger.log(Logger.Level.INFO, "Transfer from closed account to " + account.getDiagnostics() + " of $" + amount);
        } else {
            Logger.log(Logger.Level.WARNING, "Failed transfer from closed account to " + account.getDiagnostics() + " of $" + amount);
        }
    }

    public static Client loginClient() {
        int invalidAttempt = 0;
        Client candidate;
        System.out.println("Welcome!");
        do {
            System.out.print("Username: ");
            String username = in.nextLine();
            System.out.print("Password: ");
            String password = in.nextLine();
            candidate = bank.getClient(username);
            // Possibly try to rework this logic to handle 3 attempts of the same username?
            // Put history in a map -> login attempts?
            if (invalidAttempt > 1) {
                System.out.println("Please change your password, or see an associate for help with your account.");
                Logger.log(Logger.Level.WARNING, "Too many invalid attempts on account using " + username);
                candidate = null;
                break;
            }
            if (candidate == null || candidate.getClientSalt() == null || candidate.getClientPassword() == null) {
                System.out.println("Oops. Something went wrong. Please confirm your username and password, and try again.");
                invalidAttempt++;
                candidate = null;
                continue;
            }
            String hashedPassword = HashUtil.hashSaltedString(password, candidate.getClientSalt());

            if (!hashedPassword.equals(candidate.getClientPassword())) {
                System.out.println("Oops. Something went wrong. Please confirm your username and password, and try again.");
                candidate = null;
            }
        } while (candidate == null);

        return candidate;
    }

    public static boolean createAccount(Client client) {
        boolean result;

        System.out.println("So, which kind of account would you like to create today, " + client.getClientName() + "?");
        int choice;
        do {
            System.out.println("1.) " + Account.AccountType.Checking.name());
            System.out.println("2.) " + Account.AccountType.Savings.name());
            System.out.println("3.) " + Account.AccountType.CD.name());
            System.out.print("> ");

            try {
                choice = Integer.parseInt(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Please enter one of the valid values.");
                choice = -1;
            }
        } while (choice < 1 || choice > 3);

        Account account = Account.accountFactory(choice);
        result = bank.createAccount(account, client);
        if (result) {
            System.out.println("Great! Your " + account.getType().name() + " account is all set!");
        } else {
            System.out.println("Oops! Something went wrong... Please see an associate for assistance!");
            Logger.log(Logger.Level.INFO, "Issue creating account: " + account.getDiagnostics() +
                    " for: " + client.getDiagnostics());
        }

        return result;
    }

    public static void showAccountDetails(Client client) {
        if (client == null) {
            Logger.log(Logger.Level.WARNING, "Unauthorized access to accounts.");
            return;
        }

        List<Account> accounts = bank.getAccountInfo(client.getClientId());
        if (accounts != null && accounts.length() <= 0) {
            System.out.println("Sorry, you don't yet have any accounts!");
            return;
        }
        System.out.println(String.format("Accounts for: %s (%s)", client.getClientName(), client.getClientUsername()));
        for (Account a : accounts) {
            System.out.println(a);
        }
    }

    public static void handleTransaction(Client client) {
        String cont;

        do {
            int choice = getTransactionType();

            switch(choice) {
                case 1:
                    handleWithdraw(client);
                    break;
                case 2:
                    handleDeposit(client);
                    break;
                case 3:
                    handleTransfer(client);
                    break;
                case 4:
                    return;
            }
            System.out.print("Another transaction? [Y/N] ");
            cont = in.nextLine().toLowerCase();
        } while (cont.startsWith("y"));
    }

    private static int getTransactionType() {
        int choice = 0;
        do {
            System.out.println("1.) Withdraw");
            System.out.println("2.) Deposit");
            System.out.println("3.) Transfer");
            System.out.println("4.) Return");
            System.out.println("> ");

            try {
                choice = Integer.parseInt(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Please enter one of the available choices!");
            }
        } while (choice < 1 || choice > 4);

        return choice;
    }

    private static void handleWithdraw(Client client) {
        List<Account> accounts = bank.getAccountInfo(client.getClientId());
        if (accounts.length() <= 0) {
            System.out.println("Wait... You have no accounts with us, yet!");
            return;
        }

        for (int i = 0; i < accounts.length(); i++) {
            if (accounts.get(i).getBalance() <= 0) {
                accounts.remove(i);
                i--;
            }
        }

        if (accounts.length() < 1) {
            System.out.println("You haven't deposited any money with us! Unable to make any withdraws currently!");
            return;
        }

        int choice = 0;
        do {
            for(int i = 0; i < accounts.length(); i++) {
                System.out.println((i+1) + ".) " + accounts.get(i));
            }
            System.out.print("From which account would you like to withdraw? ");
            try {
                choice = Integer.parseInt(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Please select one of the above options.");
            }
        } while (choice < 1 || choice > accounts.length());

        System.out.println("Excellent!");

        Account account = accounts.get(choice-1);
        double amount = -1;
        do {
            System.out.println(account);
            System.out.print("How much would you like to withdraw? $");
            try {
                amount = Double.parseDouble(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Make sure you enter a dollar amount!");
            }
        } while (amount < 0 || account.calculateAmountAfterWithdraw(amount) < 0);

        if (bank.withdraw(account, amount)) {
            System.out.println("Withdraw successful!");
            Logger.log(Logger.Level.INFO, "Withdraw from " + account.getDiagnostics() + " of $" + amount + " completed.");
        } else {
            System.out.println("Oops! Something went wrong... Please make sure you have enough money in the account");
            System.out.println("If you feel this is a mistake, please see an associate for assistance.");
            Logger.log(Logger.Level.WARNING, "Failed withdraw from " + account.getDiagnostics() + " of $" + amount + ".");
        }
    }

    private static void handleDeposit(Client client) {
        List<Account> accounts = bank.getAccountInfo(client.getClientId());
        if (accounts.length() <= 0) {
            System.out.println("Wait... You don't have any accounts with us, yet!");
            return;
        }
        int choice = 0;
        do {
            for(int i = 0; i < accounts.length(); i++) {
                System.out.println((i+1) + ".) " + accounts.get(i));
            }
            System.out.print("To which account would you like to deposit? ");
            try {
                choice = Integer.parseInt(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Please select one of the above options.");
            }
        } while (choice < 1 || choice > accounts.length());

        System.out.println("Excellent!");

        Account account = accounts.get(choice-1);
        double amount = -1;
        do {
            System.out.println(account);
            System.out.print("How much would you like to deposit? $");
            try {
                amount = Double.parseDouble(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Make sure you enter a dollar amount!");
            }
        } while (amount < 0);

        if (bank.deposit(account, amount)) {
            System.out.println("Deposit successful!");
            Logger.log(Logger.Level.INFO, "Deposit to " + account.getDiagnostics() + " of $" + amount + " completed.");
        } else {
            System.out.println("Oops! Something went wrong... Please see an associate for assistance.");
            Logger.log(Logger.Level.WARNING, "Failed deposit to " + account.getDiagnostics() + " of $" + amount + ".");
        }
    }

    private static void handleTransfer(Client client) {
        List<Account> accounts = bank.getFullAccountInfo();
        List<Account> ownedAccounts = bank.getAccountInfo(client.getClientId());
        if (accounts.length() <= 1 && ownedAccounts.length() <= 1) {
            System.out.println("Oops! There isn't a reasonable transfer you can make...!");
            return;
        }

        int choice = 0;
        for (int i = 0; i < ownedAccounts.length(); i++) {
            if (ownedAccounts.get(i).getBalance() <= 0) {
                ownedAccounts.remove(i);
                i--;
            }
        }

        if (ownedAccounts.length() < 1) {
            System.out.println("You haven't deposited any money with us! Unable to make any withdraws currently!");
            return;
        }

        do {
            for(int i = 0; i < ownedAccounts.length(); i++) {
                System.out.println((i+1) + ".) " + ownedAccounts.get(i));
            }
            System.out.print("From which account would you like to transfer? ");
            try {
                choice = Integer.parseInt(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Please select one of the above options.");
            }
        } while (choice < 1 || choice > ownedAccounts.length());

        System.out.println("Excellent!");

        Account account = ownedAccounts.get(choice-1);
        double amount = -1;
        do {
            System.out.println(account);
            System.out.print("How much would you like to transfer? $");
            try {
                amount = Double.parseDouble(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Make sure you enter a dollar amount!");
            }
        } while (amount < 0 || account.calculateAmountAfterWithdraw(amount) < 0);

        accounts.remove(account);

        choice = -1;
        do {
            System.out.println("To which account would you like to transfer? ");
            for(int i = 0; i < accounts.length(); i++) {
                System.out.println((i+1) + ".) " + accounts.get(i));
            }
            try {
                choice = Integer.parseInt(in.nextLine());
            } catch (NoSuchElementException | NumberFormatException nsee) {
                System.out.println("Oops! Please select one of the above options.");
            }
        } while (choice < 1 || choice > accounts.length());
        Account toAccount = accounts.get(choice-1);

        if (bank.transfer(account, toAccount, amount)) {
            System.out.println("Transfer successful!");
            Logger.log(Logger.Level.INFO, "Transfer from " + account.getDiagnostics() + " to " + toAccount.getDiagnostics() + " of $" + amount + ".");
        } else {
            System.out.println("Oops! Something went wrong! Please see an associate for assistance!");
            Logger.log(Logger.Level.WARNING, "Failed transfer from " + account.getDiagnostics() + " to " + toAccount.getDiagnostics() + " of $" + amount + ".'");
        }
    }
}
