package hu.agileexpert.smartos.repository;

import hu.agileexpert.smartos.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByUniqueIdentifier(String uniqueIdentifier);
}
