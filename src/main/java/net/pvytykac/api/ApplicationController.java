package net.pvytykac.api;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import net.pvytykac.db.Application;
import net.pvytykac.db.User;
import net.pvytykac.repository.ApplicationRepository;
import net.pvytykac.security.authorization.HasDeleteApplicationPermission;
import net.pvytykac.security.authorization.HasUpdateApplicationPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationRepository repository;

    @GetMapping
    public Page<Application> getApplications(@AuthenticationPrincipal User user, Pageable pageable) {
        //todo: resolve permission tree and get scope ids
        var vendorIds = Collections.<String>emptySet();
        var applicationTypeIds = Collections.<String>emptySet();
        var ids = Collections.<String>emptySet();

        return repository.findAllByVendorIdInOrApplicationTypeIdInOrIdIn(vendorIds, applicationTypeIds, ids, pageable);
    }

    @PostMapping
    public Application createApplication(@AuthenticationPrincipal User user,
                @RequestBody @Valid @NotNull Application application) {
        // todo: throw authorization exception if no permission to create apps under vendor/applicationType
        return repository.save(application);
    }

    @PutMapping(path = "/{applicationId}")
    @HasUpdateApplicationPermission
    public Application updateApplication(@PathVariable String applicationId,
                @RequestBody @Valid @NotNull Application update) {
        var application = repository.findById(applicationId);
        return application.map(current -> {
            current.setName(update.getName());
            return repository.save(current);
        }).orElseThrow(EntityNotFoundException::new);
    }

    @DeleteMapping(path = "/{applicationId}")
    @HasDeleteApplicationPermission
    public Application deleteApplication(@PathVariable String applicationId) {
        var application = repository.findById(applicationId);

        application.ifPresent(repository::delete);

        return application.orElseThrow(EntityNotFoundException::new);
    }

}
