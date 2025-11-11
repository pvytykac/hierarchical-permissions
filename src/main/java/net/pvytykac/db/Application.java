package net.pvytykac.db;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "applications")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @NotBlank
    String name;

    @NotBlank
    String vendorId;

    @NotBlank
    String applicationTypeId;

}
