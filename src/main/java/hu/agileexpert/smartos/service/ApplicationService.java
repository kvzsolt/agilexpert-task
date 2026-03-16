package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Application;
import hu.agileexpert.smartos.exception.IdMismatchException;
import hu.agileexpert.smartos.exception.ResourceNotFoundException;
import hu.agileexpert.smartos.repository.ApplicationRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));
    }

    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    public Application update(Long id, Application application) {
        Application existing = findById(id);

        if (application.getId() != null && !application.getId().equals(id)) {
            throw new IdMismatchException("Application", id, application.getId());
        }

        existing.setExternalId(application.getExternalId());
        existing.setName(application.getName());

        return existing;
    }

    public void deleteById(Long id) {
        findById(id);
        applicationRepository.deleteById(id);
    }
}

