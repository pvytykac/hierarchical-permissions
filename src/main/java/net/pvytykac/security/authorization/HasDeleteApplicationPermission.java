package net.pvytykac.security.authorization;


import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("#permissionResolver.hasApplicationPermission(#applicationId, 'delete')")
public @interface HasDeleteApplicationPermission {
}
