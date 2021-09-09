package edu.eci.arsw.exams.moneylaunderingapi.model;

public class SuspectAccount {
    public String accountId;
    public int amountOfSmallTransactions;

    public SuspectAccount( String accountId, int amountOfSmallTransactions) {
        this.accountId=accountId;
        this.amountOfSmallTransactions=amountOfSmallTransactions;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getAmountOfSmallTransactions() {
        return amountOfSmallTransactions;
    }
}
