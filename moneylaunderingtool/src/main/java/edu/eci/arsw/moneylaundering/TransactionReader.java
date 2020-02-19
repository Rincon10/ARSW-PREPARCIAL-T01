package edu.eci.arsw.moneylaundering;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//DO NOT MODIFY
public class TransactionReader {

    public static final int THREAD_DELAY = 10;

    public TransactionReader()
    {

    }

    public List<Transaction> readTransactionsFromFile(File transactionFile)
    {
        ArrayList<Transaction> transactions = new ArrayList<>();
        int count = 0;
        try {
            CSVParser csvParser = CSVFormat.newFormat(',').parse(new InputStreamReader(new FileInputStream(transactionFile)));
            for (CSVRecord record : csvParser) {
                Transaction transaction = new Transaction();
                transaction.amount = Integer.parseInt(record.get(1));
                transaction.originAccount = record.get(2);
                transaction.destinationAccount = record.get(3);
                transactions.add(transaction);
                try {
                    if(count % 20 == 0) {
                        Thread.sleep(THREAD_DELAY);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactions;
    }

}
