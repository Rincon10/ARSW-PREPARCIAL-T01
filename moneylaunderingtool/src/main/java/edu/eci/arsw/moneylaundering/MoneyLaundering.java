package edu.eci.arsw.moneylaundering;

import edu.eci.arsw.moneylaundering.threads.TransactionThread;

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
    public static TransactionAnalyzer transactionAnalyzer;
    public static TransactionReader transactionReader;
    public int amountOfFilesTotal;
    public static AtomicInteger amountOfFilesProcessed;

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

    public void processTransactionData( int numberOfThreads)
    {
        List<TransactionThread> transactionThreads;
        amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        int partition = numberOfThreads/amountOfFilesTotal;

        // Crecion de threads
        transactionThreads = prepareThreads( numberOfThreads,partition , amountOfFilesTotal, transactionFiles);
        transactionThreads.forEach( t -> t.start());

    }

    private List<TransactionThread> prepareThreads(int numberOfThreads, int partition, int amountOfFilesTotal, List<File> transactionFiles) {
        List<TransactionThread> threads = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            int b;
            TransactionThread thread;

            b = ( i == numberOfThreads - 1 )? amountOfFilesTotal: ((i+1)*partition)-1;
            thread = new TransactionThread(i*partition, b, transactionFiles);
            threads.add(thread);
        }

        return threads;
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
        int numberOfThreads = 5;
        MoneyLaundering moneyLaundering = new MoneyLaundering();
        moneyLaundering.processTransactionData( numberOfThreads );
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
