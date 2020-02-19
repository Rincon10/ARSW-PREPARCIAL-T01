package edu.eci.arsw.moneylaundering;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountReporter {
        public static void report(String account, int amountOfSuspectTransactions) {
            java.lang.String payload = "{"
                    + "\"accountId\": \""+account+"\", "
                    + "\"transactionAmount\": \""+amountOfSuspectTransactions+"\" "
                    + "}";

            StringEntity entity = new StringEntity(payload,
                    ContentType.APPLICATION_JSON);

            try {
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost request = new HttpPost("http://localhost:8080/fraud-bank-accounts");
                request.setEntity(entity);

                HttpResponse response;
                response = httpClient.execute(request);
                System.out.println(response.getStatusLine().getStatusCode());

            } catch (IOException ex) {
                Logger.getLogger(AccountReporter.class.getName()).log(Level.SEVERE, "Unable to report fraudulent transactions for account", ex);
            }

        }
}
