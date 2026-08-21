package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.client.AppUser;
import org.example.dao.AppUserDAO;
import org.example.enums.AccountType;
import org.example.enums.Role;
import org.example.exceptions.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AppUserDAO appUserDAO;

    public ResponseEntity<Void> blockUser(Integer id) {
        AppUser user = appUserDAO.findById(id).orElseThrow(() -> new UserException("User not found"));
        if (!user.isAccountNonLocked()) {
            throw new UserException("Account locked");
        }
        user.setAccountActive(false);
        user.setRefreshToken(null);
        appUserDAO.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> unblockUser(Integer id) {
        AppUser user = appUserDAO.findById(id).orElseThrow(() -> new UserException("User not found"));
        if (user.isAccountNonLocked()) {
            throw new UserException("Account not locked");
        }
        user.setAccountActive(true);
        appUserDAO.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> makeManager(Integer id) {
        AppUser user = appUserDAO.findById(id).orElseThrow(() -> new UserException("User not found"));
        if (user.getRole() == Role.PLATFORM_MANAGER) {
            throw new UserException("User is already manager");
        }
        user.setRole(Role.PLATFORM_MANAGER);
        appUserDAO.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> makeAdmin(Integer id) {
        AppUser user = appUserDAO.findById(id).orElseThrow(() -> new UserException("User not found"));
        if (user.getRole() == Role.PLATFORM_ADMIN) {
            throw new UserException("User is already admin");
        }
        user.setRole(Role.PLATFORM_ADMIN);
        appUserDAO.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> createPremiumAccount(String userEmail) {
        AppUser user = appUserDAO.findAppUserByEmail(userEmail).orElseThrow(() -> new UserException("User not found"));

        if (user.getAccountType() == AccountType.PREMIUM) {
            throw new UserException("Account is already premium");
        }
        user.setAccountType(AccountType.PREMIUM);
        appUserDAO.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
