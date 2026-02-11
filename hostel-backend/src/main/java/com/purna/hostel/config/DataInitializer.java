package com.purna.hostel.config;

import com.purna.hostel.entity.Role;
import com.purna.hostel.entity.RoleName;
import com.purna.hostel.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            // ✅ Use RoleName enum instead of String
            Role student = new Role(RoleName.ROLE_STUDENT);
            Role warden = new Role(RoleName.ROLE_WARDEN);
            Role admin = new Role(RoleName.ROLE_ADMIN);

            roleRepository.save(student);
            roleRepository.save(warden);
            roleRepository.save(admin);

            System.out.println("✅ Roles added successfully: STUDENT, WARDEN, ADMIN");
        }
    }
}
