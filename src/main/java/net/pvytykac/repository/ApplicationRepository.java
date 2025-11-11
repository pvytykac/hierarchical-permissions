package net.pvytykac.repository;

import net.pvytykac.db.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {

    Page<Application> findAllByVendorIdInOrApplicationTypeIdInOrIdIn(
            Set<String> vendorIds, Set<String> applicationTypeIds, Set<String> ids, Pageable pageable);

    boolean existsByVendorIdInOrApplicationTypeIdInAndId(Set<String> vendorIds,
            Set<String> applicationTypeIds, String id);

}
