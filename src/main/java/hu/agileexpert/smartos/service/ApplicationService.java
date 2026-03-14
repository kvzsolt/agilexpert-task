package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Application;
import hu.agileexpert.smartos.repository.ApplicationRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ApplicationService {
//TODO: Implement model mapper after dtos implemented.

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public Application create(Application application) {
        return applicationRepository.save(application);
    }

    public Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + id));
    }

    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    public Application update(Long id, Application application) {
        findById(id);
        return applicationRepository.save(application);
    }

    public void deleteById(Long id) {
        findById(id);
        applicationRepository.deleteById(id);
    }
}

