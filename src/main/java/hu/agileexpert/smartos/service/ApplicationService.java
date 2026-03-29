package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Application;
import hu.agileexpert.smartos.dto.application.ApplicationRequest;
import hu.agileexpert.smartos.dto.application.ApplicationResponse;
import hu.agileexpert.smartos.exception.account.IdMismatchException;
import hu.agileexpert.smartos.exception.ResourceNotFoundException;
import hu.agileexpert.smartos.repository.ApplicationRepository;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ModelMapper modelMapper;

    public ApplicationService(ApplicationRepository applicationRepository, ModelMapper modelMapper) {
        this.applicationRepository = applicationRepository;
        this.modelMapper = modelMapper;
    }

    public ApplicationResponse create(ApplicationRequest request) {
        Application app = modelMapper.map(request, Application.class);
        Application created = create(app);
        return modelMapper.map(created, ApplicationResponse.class);
    }

    public ApplicationResponse findByIdResponse(Long id) {
        return modelMapper.map(findById(id), ApplicationResponse.class);
    }

    public List<ApplicationResponse> findAllResponses() {
        return findAll().stream()
                .map(app -> modelMapper.map(app, ApplicationResponse.class))
                .toList();
    }

    public ApplicationResponse update(Long id, ApplicationRequest request) {
        Application app = modelMapper.map(request, Application.class);
        Application updated = update(id, app);
        return modelMapper.map(updated, ApplicationResponse.class);
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

        existing.setUniqueIdentifier(application.getUniqueIdentifier());
        existing.setName(application.getName());

        return existing;
    }

    public void deleteById(Long id) {
        findById(id);
        applicationRepository.deleteById(id);
    }
}
