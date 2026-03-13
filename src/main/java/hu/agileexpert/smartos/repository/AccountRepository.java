package hu.agileexpert.smartos.repository;

import hu.agileexpert.smartos.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}