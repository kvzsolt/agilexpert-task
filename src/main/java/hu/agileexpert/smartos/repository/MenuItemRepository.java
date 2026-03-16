package hu.agileexpert.smartos.repository;

import hu.agileexpert.smartos.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}

