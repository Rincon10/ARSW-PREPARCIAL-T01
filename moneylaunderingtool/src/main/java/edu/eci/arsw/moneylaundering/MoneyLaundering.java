package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyLaundering
{
    private TransactionAnalyzer transactionAnalyzer;
    private TransactionReader transactionReader;
    private int amountOfFilesTotal;
    private AtomicInteger amountOfFilesProcessed;

    public MoneyLaundering()
    {
        transactionAnalyzer = new TransactionAnalyzer();
        transactionReader = new TransactionReader();
        amountOfFilesProcessed = new AtomicInteger();
    }

    public void processTransactionData()
    {
        amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        for(File transactionFile : transactionFiles)
        {            
            List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFile);
            for(Transaction transaction : transactions)
            {
                transactionAnalyzer.addTransaction(transaction);
            }
            amountOfFilesProcessed.incrementAndGet();
        }
    }

    public List<String> getOffendingAccounts()
    {
        return transactionAnalyzer.listOffendingAccounts();
    }

    private List<File> getTransactionFileList()
    {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/")).filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }

    public static void main(String[] args)
    {
        MoneyLaundering moneyLaundering = new MoneyLaundering();
        Thread processingThread = new Thread(() -> moneyLaundering.processTransactionData());
        processingThread.start();
        while(true)
        {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if(line.contains("exit"))
                break;
            String message = "Processed %d out of %d files.\nFound %d suspect accounts:\n%s";
            List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
            String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2)-> s1 + "\n"+s2);
            message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
            System.out.println(message);
        }

    }


}
