package net.pvytykac.api;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import net.pvytykac.db.User;
import net.pvytykac.repository.UserRepository;
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
@RequestMapping("/public/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repository;

    @GetMapping
    public Page<User> listUsers(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @PostMapping
    public User createUser(@RequestBody @Valid @NotNull User user) {
        return repository.save(user);
    }

    @PutMapping(path = "/{userId}")
    public User updateUser(@PathVariable String userId, @RequestBody @Valid @NotNull User update) {
        return repository.findById(userId)
                .map(current -> {
                    current.setPassword(update.getPassword());
                    current.setRoles(update.getRoles());
                    return repository.save(current);
                })
                .orElseThrow(EntityNotFoundException::new);
    }

    @DeleteMapping(path = "/{userId}")
    public User deleteUser(@PathVariable String userId) {
        var role = repository.findById(userId);

        role.ifPresent(repository::delete);

        return role.orElseThrow(EntityNotFoundException::new);
    }

}
