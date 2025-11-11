package net.pvytykac.api;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import net.pvytykac.db.Workspace;
import net.pvytykac.repository.ApplicationRepository;
import net.pvytykac.repository.WorkspaceRepository;
import net.pvytykac.security.authorization.HasDeleteApplicationPermission;
import net.pvytykac.security.authorization.HasReadApplicationPermission;
import net.pvytykac.security.authorization.HasUpdateApplicationPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/applications/{applicationId}/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final ApplicationRepository applicationRepository;
    private final WorkspaceRepository repository;

    @GetMapping
    @HasReadApplicationPermission
    public Page<Workspace> listWorkspaces(@PathVariable String applicationId, Pageable pageable) {
        return repository.findAllByApplicationId(applicationId, pageable);
    }

    @PostMapping
    @HasUpdateApplicationPermission
    public Workspace createWorkspace(@PathVariable String applicationId,
                @RequestBody @Valid @NotNull Workspace workspace) {
        var application = applicationRepository.findById(applicationId)
                .orElseThrow(EntityNotFoundException::new);

        workspace.setApplication(application);
        return repository.save(workspace);
    }

    @PutMapping(path = "/{workspaceId}")
    @HasUpdateApplicationPermission
    public Workspace updateWorkspace(@PathVariable String applicationId, @PathVariable String workspaceId,
                @RequestBody @Valid @NotNull Workspace update) {
        var workspace = repository.findByApplicationIdAndId(applicationId, workspaceId);

        workspace.map(current -> {
            current.setName(update.getName());
            return repository.save(current);
        });

        return workspace.orElseThrow(EntityNotFoundException::new);
    }

    @DeleteMapping(path = "/{workspaceId}")
    @HasDeleteApplicationPermission
    public Workspace createWorkspace(@PathVariable String applicationId, @PathVariable String workspaceId) {
        var workspace = repository.findByApplicationIdAndId(applicationId, workspaceId);

        workspace.ifPresent(repository::delete);

        return workspace.orElseThrow(EntityNotFoundException::new);
    }

}
