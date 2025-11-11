package net.pvytykac.security.authorization;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("#permissionResolver.hasAnyApplicationPermission(#applicationId)")
public @interface HasReadApplicationPermission {
}
