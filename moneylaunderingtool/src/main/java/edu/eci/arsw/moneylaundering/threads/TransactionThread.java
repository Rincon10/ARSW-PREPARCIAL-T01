package edu.eci.arsw.moneylaundering.threads;

import edu.eci.arsw.moneylaundering.MoneyLaundering;
import edu.eci.arsw.moneylaundering.Transaction;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Iván Camilo Rincón Saavedra
 * @version 9/9/2021
 */
public class TransactionThread extends Thread {
    private int a;
    private int b;
    private boolean stop;
    private List<File> transactionFiles;

    public TransactionThread(int a, int b, List<File> transactionFiles) {
        this.a = a;
        this.b = b;
        this.transactionFiles = transactionFiles;
        stop=false;
        start();
    }

    @Override
    public void run() {
        try {
            for (int i = a; i < b; i++) {
                while(stop){
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TransactionThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                List<Transaction> transactions = MoneyLaundering.transactionReader.readTransactionsFromFile(transactionFiles.get(i));
                for(Transaction transaction : transactions) {
                    MoneyLaundering.transactionAnalyzer.addTransaction(transaction);
                }
                MoneyLaundering.amountOfFilesProcessed.incrementAndGet();
            }
        }
        catch ( Exception exception ){
            exception.printStackTrace();
        }

    }

    public void stopThread() {
        stop = true;
    }

    public void resumeThread() {
        stop = false;
        synchronized ( this ){
            notifyAll();
        }
    }
}
