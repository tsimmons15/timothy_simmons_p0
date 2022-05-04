package dev.simmons.entities;

import dev.simmons.utilities.lists.List;

/**
 * The template associated with an account object.
 */
public interface Account {
    /**
     * Get the account_id associated with this record, if it's been retrieved from the database.
     * @return The account_id representing this account. Negative and 0 values represent an unset ID and shouldn't be queried.
     */
    int getId();

    /**
     * Set the account_id associated with this record.
     * @param id The account_id in the database representing this account object.
     */
    void setId(int id);

    /**
     * Get the balance of the account.
     * @return The balance of the account.
     */
    double getBalance();

    /**
     * Set the balance of the account.
     * @param balance The new balance of the account.
     */
    void setBalance(double balance);

    /**
     * Handle a deposit to this account, following any special rules for the account.
     * @param amount The amount you're depositing.
     * @return True if the deposit is successful, false otherwise.
     */
    boolean deposit(double amount);

    /**
     * Handle a withdraw to this account, following any special rules for the account.
     * @param amount The amount you're withdrawing.
     * @return True if the withdraw is successful, false otherwise.
     */
    boolean withdraw(double amount);

    /**
     * Calculate the amount, if withdraw is successful, that would remain.
     * @param amount The amount you're trying to withdraw.
     * @return The theoretical amount that would remain.
     */
    double calculateAmountAfterWithdraw(double amount);

    /**
     * Get the list of owners associated with this account.
     * @return The list of owners if one is available, null otherwise.
     */
    List<Client> getOwners();

    /**
     * Set the list of owners for this account.
     * @param owners The list of owners associated with this account.
     */
    void setOwners(List<Client> owners);

    /**
     * Get the enum AccountType that is associated with this object.
     * @return The AccountType value.
     */
    AccountType getType();

    /**
     * Get diagnostic information associated with this account.
     * @return The formatted string with the diagnostic information.
     */
    String getDiagnostics();

    /**
     * Static Account builder for building specific Account objects based on their AccountType
     * @param type The string containing one of the enum AccountType names representing this Account's type.
     * @return The account created, or null if the string is not recognized.
     */
    static Account accountFactory(String type) {
        Account result = null;
        switch (AccountType.valueOf(type)) {
            case Checking:
                result = new CheckingAccount();
                break;
            case Savings:
                result = new SavingsAccount();
                break;
            case CD:
                result = new CDAccount();
                break;
        }

        return result;
    }

    /**
     * Static Account builder for building specific Account objects. Currently:
     * 1 - Checking, 2 - Savings, 3 - CD
     * @param ordinal The ordinal representing which account type to create.
     * @return The account created, or null if the ordinal is not recognized.
     */
    static Account accountFactory(int ordinal) {
        Account result = null;
        switch (ordinal) {
            case 1:
                result = new CheckingAccount();
                break;
            case 2:
                result = new SavingsAccount();
                break;
            case 3:
                result = new CDAccount();
                break;
        }

        return result;
    }

    /**
     * The AccountType representing the various accounts, as well as their interest rates.
     */
    enum AccountType {
        Checking(0), Savings(.03), CD(.10);
        public final double interest;
        private AccountType(double interest) {
            this.interest = interest;
        }
    }
}
