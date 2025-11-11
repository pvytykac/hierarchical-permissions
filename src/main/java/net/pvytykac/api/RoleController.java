package net.pvytykac.api;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import net.pvytykac.db.Role;
import net.pvytykac.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleRepository repository;

    @GetMapping
    public Page<Role> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @PostMapping
    public Role createRole(@RequestBody @Valid @NotNull Role role) {
        return repository.save(role);
    }

    @DeleteMapping(path = "/{roleId}")
    public Role createRole(@PathVariable String roleId) {
        var role = repository.findById(roleId);

        role.ifPresent(repository::delete);

        return role.orElseThrow(EntityNotFoundException::new);
    }

}
