package com.revenat.jcart.core.security;

import com.revenat.jcart.core.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
}
