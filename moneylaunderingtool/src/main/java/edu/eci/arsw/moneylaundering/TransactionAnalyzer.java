package edu.eci.arsw.moneylaundering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionAnalyzer {
    private HashMap<String, Integer> smallTransactionsPerAccount;
    private static final int LAUNDERING_LIMIT_AMOUNT = 1250;
    private static final int LAUNDERING_LIMIT_COUNT = 100;

    public TransactionAnalyzer()
    {
        smallTransactionsPerAccount = new HashMap<>();
    }

    public synchronized void addTransaction(Transaction transaction)
    {
        if(transaction.amount < LAUNDERING_LIMIT_AMOUNT)
        {
            String destinationAccount = transaction.destinationAccount;
            if(!smallTransactionsPerAccount.containsKey(destinationAccount))
            {
                smallTransactionsPerAccount.put(destinationAccount, 1);
            }
            else
            {
                smallTransactionsPerAccount.put(destinationAccount, smallTransactionsPerAccount.get(destinationAccount) + 1);
            }
            if(smallTransactionsPerAccount.get(destinationAccount) > LAUNDERING_LIMIT_COUNT)
                AccountReporter.report(destinationAccount, smallTransactionsPerAccount.get(destinationAccount));
        }
    }

    public List<String> listOffendingAccounts()
    {
        return smallTransactionsPerAccount.entrySet().stream().filter(accountEntry-> accountEntry.getValue()>LAUNDERING_LIMIT_COUNT).map(Map.Entry::getKey).collect(Collectors.toList());
    }

}
