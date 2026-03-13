package hu.agileexpert.smartos.repository;

import hu.agileexpert.smartos.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}

