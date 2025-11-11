package net.pvytykac.security.authorization;

import lombok.RequiredArgsConstructor;
import net.pvytykac.db.RoleScope;
import net.pvytykac.db.User;
import net.pvytykac.repository.ApplicationRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component("permissionResolver")
@RequestScope
@RequiredArgsConstructor
public class PermissionResolver {

    private final ApplicationRepository applicationRepository;
    //todo: instantiate and inject into context properly
    private final PermissionsModel permissionsModel;

    public boolean hasAnyApplicationPermission(String applicationId) {
        var vendorIds = permissionsModel.getVendorIdsWithAnyPermission();
        var applicationTypeIds = permissionsModel.getApplicationTypeIdsWithAnyPermission();
        var applicationIds = permissionsModel.getApplicationIdsWithAnyPermission();

        return applicationIds.contains(applicationId) ||
                // todo: make sure or/and precedence is ok
                applicationRepository.existsByVendorIdInOrApplicationTypeIdInAndId(
                        vendorIds, applicationTypeIds, applicationId);
    }

    public boolean hasApplicationPermission(String applicationId, String permission) {
        var vendorIds = permissionsModel.getVendorIdsWithPermission(permission);
        var applicationTypeIds = permissionsModel.getApplicationTypeIdsWithPermission(permission);
        var applicationIds = permissionsModel.getApplicationIdsWithPermission(permission);

        return applicationIds.contains(applicationId) ||
                // todo: make sure or/and precedence is ok
                applicationRepository.existsByVendorIdInOrApplicationTypeIdInAndId(
                        vendorIds, applicationTypeIds, applicationId);
    }

    public static class PermissionsModel {

        private final Map<RoleScope, Map<String, Set<String>>> permissions = new ConcurrentHashMap<>();

        public PermissionsModel(@AuthenticationPrincipal User user) {
            user.getRoles().forEach(role -> {
                this.permissions.computeIfAbsent(role.getScope(), key -> new ConcurrentHashMap<>());
                this.permissions.get(role.getScope())
                        .computeIfAbsent(role.getScopeId(), scopeId -> ConcurrentHashMap.newKeySet());
                this.permissions.get(role.getScope())
                        .get(role.getScopeId())
                        .addAll(role.getPermissions());
            });
        }

        public Set<String> getVendorIdsWithAnyPermission() {
            return getScopeIdsForScopeWithPermissionsMatching(RoleScope.VENDOR,
                    set -> !set.isEmpty());
        }

        public Set<String> getVendorIdsWithPermission(String permission) {
            return getScopeIdsForScopeWithPermissionsMatching(RoleScope.VENDOR,
                    set -> set.contains(permission));
        }

        public Set<String> getApplicationTypeIdsWithAnyPermission() {
            return getScopeIdsForScopeWithPermissionsMatching(RoleScope.APPLICATION_TYPE,
                    set -> !set.isEmpty());
        }

        public Set<String> getApplicationTypeIdsWithPermission(String permission) {
            return getScopeIdsForScopeWithPermissionsMatching(RoleScope.APPLICATION_TYPE,
                    set -> set.contains(permission));
        }

        public Set<String> getApplicationIdsWithAnyPermission() {
            return getScopeIdsForScopeWithPermissionsMatching(RoleScope.APPLICATION,
                    set -> !set.isEmpty());
        }

        public Set<String> getApplicationIdsWithPermission(String permission) {
            return getScopeIdsForScopeWithPermissionsMatching(RoleScope.APPLICATION,
                    set -> set.contains(permission));
        }

        private Set<String> getScopeIdsForScopeWithPermissionsMatching(RoleScope scope,
                Predicate<Set<String>> permissionsPredicate) {
            return permissions.getOrDefault(scope, Collections.emptyMap())
                    .entrySet().stream()
                    .filter(entry -> permissionsPredicate.test(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
        }
    }

}
