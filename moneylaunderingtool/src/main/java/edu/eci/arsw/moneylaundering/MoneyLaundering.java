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
    public static int amountOfFilesTotal;
    public static AtomicInteger amountOfFilesProcessed;
    public static  boolean stop = false;
    public static List<TransactionThread> threads;


    public MoneyLaundering()
    {
        transactionAnalyzer = new TransactionAnalyzer();
        transactionReader = new TransactionReader();
        amountOfFilesProcessed = new AtomicInteger(0);
    }

    public void processTransactionData()
    {

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

    public void processTransactionData( int numberOfThreads) {
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        int partition = amountOfFilesTotal/numberOfThreads;

        // Crecion de threads
        prepareThreads( numberOfThreads,partition , amountOfFilesTotal, transactionFiles);
    }

    private void prepareThreads(int numberOfThreads, int partition, int amountOfFilesTotal, List<File> transactionFiles) {
        threads = new ArrayList<>();
        int start = 0;
        int end = 0;
        for (int i = 0; i < numberOfThreads; i++) {

            end = start + partition;
            end+= ( i == 0 )? amountOfFilesTotal%numberOfThreads :0 ;

            threads.add(new TransactionThread(start , end ,transactionFiles));

            //System.out.println(start+" "+end);
            start = end;

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

    public static void printMessage(MoneyLaundering moneyLaundering) {
        String message = "Processed %d out of %d files.\nFound %d suspect accounts:\n%s";
        List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
        String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2)-> s1 + "\n"+s2);
        message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
        System.out.println(message);
    }

    private static void pauseAllThreads() {
        System.out.println("=================================Pausado====================================");
        threads.forEach( t -> t.stopThread() );

    }

    private static void continueAllThreads() {
        System.out.println("=================================Continuando=================================");
        threads.forEach( t -> t.resumeThread() );
    }

    public static void main(String[] args) {
        // Number of threads debe ser menor a los archivos a analizar
        int numberOfThreads = 22;
        MoneyLaundering moneyLaundering = new MoneyLaundering();
        moneyLaundering.processTransactionData( numberOfThreads );

        System.out.println("Presione enter");
        while( true ) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            stop=!stop;
            if ( stop ){
                pauseAllThreads();
                printMessage( moneyLaundering);
            }
            else{
                continueAllThreads();
            }

            if(line.contains("exit"))
                break;
        }
        System.out.println("=================================Termine=================================");
        printMessage( moneyLaundering);
    }


}
