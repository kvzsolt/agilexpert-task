package hu.agileexpert.smartos.repository;

import hu.agileexpert.smartos.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}

