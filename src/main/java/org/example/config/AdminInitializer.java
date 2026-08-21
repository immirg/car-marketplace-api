package org.example.config;

import org.example.client.AppUser;
import org.example.dao.AppUserDAO;
import org.example.enums.AccountType;
import org.example.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer {
    private final AppUserDAO appUserDAO;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String email;
    @Value("${app.admin.password}")
    private String password;
    public AdminInitializer(AppUserDAO appUserDAO, PasswordEncoder passwordEncoder) {
        this.appUserDAO = appUserDAO;
        this.passwordEncoder = passwordEncoder;
    }

    public void init(){
        if (!appUserDAO.existsByRole(Role.PLATFORM_ADMIN)) {
            AppUser appUser = AppUser.builder()
                    .firstname("Admin")
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.PLATFORM_ADMIN)
                    .accountType(AccountType.PREMIUM)
                    .accountActive(true)
                    .build();
            appUserDAO.save(appUser);
        }
    }
}
