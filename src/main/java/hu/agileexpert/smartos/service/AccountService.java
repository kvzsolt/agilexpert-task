package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Account;
import hu.agileexpert.smartos.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class AccountService {
//TODO: Implement model mapper after dtos implemented.
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account create(Account account) {
        return accountRepository.save(account);
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + id));
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account update(Long id, Account account) {
        findById(id);
        return accountRepository.save(account);
    }

    public void deleteById(Long id) {
        findById(id);
        accountRepository.deleteById(id);
    }
}

