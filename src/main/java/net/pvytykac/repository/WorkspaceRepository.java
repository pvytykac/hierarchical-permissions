package net.pvytykac.repository;

import net.pvytykac.db.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, String> {

    Page<Workspace> findAllByApplicationId(String applicationId, Pageable pageable);

    Optional<Workspace> findByApplicationIdAndId(String applicationId, String workspaceId);

}
