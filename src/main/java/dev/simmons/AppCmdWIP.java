package dev.simmons;

import dev.simmons.entities.Client;
import dev.simmons.service.Bank;
import dev.simmons.service.Banking;
import dev.simmons.utilities.hashing.HashUtil;
import dev.simmons.utilities.logging.Logger;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class AppCmdWIP {
    private final Scanner in = new Scanner(System.in);
    private final Bank bank = new Banking();
    private Client client;
    private String accountOverview = "";
    private int context;
    private String[] cmd;

    public static void main(String[] args) {
        AppCmdWIP app = new AppCmdWIP();

        try {
            app.mainLoop();
        } catch (Exception ex) {
            Logger.log(Logger.Level.ERROR, ex);
        }

        System.out.println("Please visit again! Bis dann!");
    }

    private static final int MAIN           = 0b00000000;
    private static final int EXIT           = 0b10000000;
    private static final int LOGIN_U        = 0b00000001;
    private static final int LOGIN_P        = 0b00000011;
    private static final int CREATE         = 0b00000100;


    private void mainLoop() {
        do {
            // Yeah, I know this probably isn't massively "efficient".
            // It's something I've always wanted to implement, and I had time...
            System.out.println("-=-=- The Bank -=-=-");
            if (client != null) {
                System.out.println("Welcome, " + client.getClientName());
            }
            if (!accountOverview.equals("")) {
                System.out.println(accountOverview);
            }
            processContext();
        } while (context != EXIT);
    }

    private void processContext() {
        switch (context) {
            case MAIN:
                System.out.print("> ");
                String input = in.nextLine();
                processInput(input);
                break;
            case LOGIN_U:

        }

    }
    private void processInput(String input) {
        cmd = input.split(" ");

        switch (cmd[0]) {
            case "help":
                processHelp();
                context = MAIN;
                break;
            case "login":
                context = LOGIN_U;
                break;
            case "create":
                context = CREATE;
                break;
            case "return":

                break;
            case "exit":
                context = EXIT;
                break;
        }
    }

    public void processHelp() {
        System.out.println("Important note! All arguments not expected will be ignored!");
        if (cmd.length == 1) {
            System.out.println("Our Bank Commands!");
            System.out.println("\"help [cmd]\" - show the list of commands and brief description!");
            System.out.println("\t -> If provided, [cmd] represents a request to get more info about whatever command is passed in!");
            System.out.println("\"login [-u username] [-p password]\" - attempt to login to our system!");
            System.out.println("\t -> If provided, the optional fields represent passing in the given information!");
            System.out.println("\"create (account|user) [optionals]- start the process to create either a new account or new user!");
            System.out.println("\t -> [optionals] refers to the information needed for creating an account!");
            System.out.println("\t -> (account) - [-b (balance)] !");
            System.out.println("\t -> (user)    - not applicable, currently !");
            System.out.println("\"exit\" - we sure hate to see you go, but this will exit the terminal!");
            System.out.println("\"return\" - will return you to the previous menu, if a previous one exists!");
            return;
        }

        switch(cmd[2]) {
            case "help":
                System.out.println("You don't look like you need help to me....");
                break;
            case "login":
                System.out.println("User Login");
                System.out.println("If presented with no arguments, you will be prompted for username and password.");
                System.out.println("Alternatively, you can provide them in the call");
                System.out.println("-u username, for the username");
                System.out.println("-p password, for the password");
                System.out.println("If the username/password combination match, you will be taken to your account screen!");
                break;
            case "create":
                break;
            case "exit":
                System.out.println("System Exit");
                System.out.println("Will stop the terminal, regardless of current activity!");
                break;
            case "return":
                System.out.println("Context Menu Return");
                System.out.println("Will return you to the previous menu, if applicable.");
                System.out.println("Has no use in the top-level menu!");
                break;
        }
    }

    public void processLogin() {
        String username = "";
        String password = "";

        for (int i = 1; i < cmd.length-1; i++) {
            if (cmd[i].toLowerCase().equals("-u")) {
                username = cmd[i+1];
                i++;
            } else if (cmd[i].toLowerCase().equals("-p")) {
                password = cmd[i+1];
                i++;
            }
        }

        if (username.equals("")) {
            System.out.print("Username: ");
            try {
                username = in.next();
            } catch (NoSuchElementException nsee) {
                System.out.println("Unable to handle the input.");
                context = MAIN;
                return;
            }

        }
        if (password.equals("")) {
            System.out.print("Password: ");
            try {
                password = in.nextLine();
            } catch (NoSuchElementException nsee) {
                System.out.println("Unable to handle the input.");
                context = MAIN;
            }
        }

        client = login(username, password);
    }

    private Client login(String username, String password) {
        Client candidate = bank.getClient(username);
        if (candidate == null) {
            System.out.println("Unable to find a match! Please check your username and password!");
            return null;
        }
        String hash = HashUtil.hashSaltedString(password, candidate.getClientSalt());
        if (hash.equals(candidate.getClientPassword())) {
            return candidate;
        }

        System.out.println("Unable to find a match! Please check your username and password!");
        return null;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getContext() {
        return context;
    }

    public void setContext(int context) {
        this.context = context;
    }

}
