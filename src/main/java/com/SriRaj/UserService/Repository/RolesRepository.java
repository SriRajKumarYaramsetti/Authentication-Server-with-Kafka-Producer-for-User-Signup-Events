package com.SriRaj.UserService.Repository;

import com.SriRaj.UserService.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Role,Long> {
}
