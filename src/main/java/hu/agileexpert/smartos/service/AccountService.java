package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Account;
import hu.agileexpert.smartos.exception.IdMismatchException;
import hu.agileexpert.smartos.exception.ResourceNotFoundException;
import hu.agileexpert.smartos.repository.AccountRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account update(Long id, Account account) {
        Account existing = findById(id);

        if (account.getId() != null && !account.getId().equals(id)) {
            throw new IdMismatchException("Account", id, account.getId());
        }

        existing.setExternalId(account.getExternalId());
        existing.setName(account.getName());
        existing.setMenu(account.getMenu());
        existing.setApplications(account.getApplications());
        existing.setTheme(account.getTheme());
        existing.setWallpaper(account.getWallpaper());

        return existing;
    }

    public void deleteById(Long id) {
        findById(id);
        accountRepository.deleteById(id);
    }
}

