package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Qualifier("MoneyLaunderingServiceStub")
public class MoneyLaunderingServiceStub implements MoneyLaunderingService {
    private ConcurrentHashMap<String , SuspectAccount> suspectAccounts = new ConcurrentHashMap<>();

    private void loadData() {
        for (int i = 0; i < 4; i++) {
            suspectAccounts.put( i+"", new SuspectAccount(i+"", (i+1)*10000));
        }
    }

    public MoneyLaunderingServiceStub() {
        loadData();
    }

    @Override
    public void updateAccountStatus(SuspectAccount suspectAccount) throws Exception {
        Optional<SuspectAccount> optionalSuspectAccount = Optional.ofNullable( suspectAccounts.get(suspectAccount.getAccountId()));
        optionalSuspectAccount.orElseThrow( () -> new Exception("No Se puede actualizar la cuenta ya que no existe") );

        suspectAccounts.put( optionalSuspectAccount.get().getAccountId(), suspectAccount);
    }

    @Override
    public SuspectAccount getAccountStatus(String accountId) throws Exception {
        Optional<SuspectAccount> optionalSuspectAccount = Optional.ofNullable( suspectAccounts.get(accountId));
        optionalSuspectAccount.orElseThrow( () -> new Exception("No existe la cuenta ingresada") );
        return optionalSuspectAccount.get();
    }

    @Override
    public List<SuspectAccount> getSuspectAccounts() {
        return (List<SuspectAccount>) suspectAccounts;
    }
}
