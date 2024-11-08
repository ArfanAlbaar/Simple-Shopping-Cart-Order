package com.arfan.shop.data;

import com.arfan.shop.model.Role;
import com.arfan.shop.model.User;
import com.arfan.shop.repository.RoleRepository;
import com.arfan.shop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.logging.Logger;

@Transactional
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN", "ROLE_USER");
        createDefaultRoleIfNotExists(defaultRoles);
        createDefaultUserIfNotExists();
        createDefaultAdminIfNotExists();
    }

    private void createDefaultRoleIfNotExists(Set<String> roles) {
        roles.stream()
                .filter(role -> roleRepository.findByRoleName(role).isEmpty())
                .map(Role::new).forEach(roleRepository::save);
    }

    private void createDefaultUserIfNotExists(){
        Role userRole = roleRepository.findByRoleName("ROLE_USER").get();
        for(int i = 1 ; i <= 5;  i++){
            String defaultEmail = "user" + i + "@mail.com";
            if(userRepository.existsByEmail(defaultEmail)){
                continue;
            }
            User user = new User();
            user.setFirstName("User " + i);
            user.setLastName(" User");
            user.setEmail(defaultEmail);
            user.setPassword(passwordEncoder.encode("123"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
            logger.info("Default vet user " + i + " created successfully.");
        }
    }

    private void createDefaultAdminIfNotExists(){
        Role userRole = roleRepository.findByRoleName("ROLE_ADMIN").get();
        for(int i = 1 ; i <= 5;  i++){
            String defaultEmail = "admin" + i + "@mail.com";
            if(userRepository.existsByEmail(defaultEmail)){
                continue;
            }
            User user = new User();
            user.setFirstName("admin " + i);
            user.setLastName(" admin");
            user.setEmail(defaultEmail);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
            logger.info("Default vet admin " + i + " created successfully.");
        }
    }


}
