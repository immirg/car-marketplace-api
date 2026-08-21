package org.example.dao;

import org.example.client.AppUser;
import org.example.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserDAO extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findAppUserByEmail(String email);
    List<AppUser> findAllByRole(Role role);
    Optional<AppUser> findByRole(Role role);
    boolean existsByRole(Role role);
}
