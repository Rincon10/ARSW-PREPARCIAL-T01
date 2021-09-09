package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;

import java.util.List;

public interface MoneyLaunderingService {
    void updateAccountStatus(SuspectAccount suspectAccount) throws Exception;
    SuspectAccount getAccountStatus(String accountId) throws Exception;
    List<SuspectAccount> getSuspectAccounts();
}
