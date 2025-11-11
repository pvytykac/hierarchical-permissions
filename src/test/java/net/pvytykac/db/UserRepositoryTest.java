package net.pvytykac.db;

import net.pvytykac.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = PostgresContainerInitializer.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void persistUser() {
        var user = User.builder()
                .username("paly")
                .password("pA$$w0rd")
                .roles(Collections.emptySet())
                .build();

        user = userRepository.save(user);

        assertThat(user)
                .isNotNull()
                .returns(Collections.emptySet(), User::getRoles)
                .returns("pA$$w0rd", User::getPassword)
                .returns("paly", User::getUsername)
                .extracting(User::getId).isNotNull();
    }

}
