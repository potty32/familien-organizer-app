package com.familienorganizer.config;

import com.familienorganizer.entity.FamilyUser;
import com.familienorganizer.entity.Role;
import com.familienorganizer.repository.FamilyUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final FamilyUserRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        List<FamilyUser> members = List.of(
                FamilyUser.builder().displayName("Mama").avatarColor("#EC4899").role(Role.PARENT).build(),
                FamilyUser.builder().displayName("Papa").avatarColor("#3B82F6").role(Role.PARENT).build(),
                FamilyUser.builder().displayName("Kind 1").avatarColor("#10B981").role(Role.CHILD).build(),
                FamilyUser.builder().displayName("Kind 2").avatarColor("#F59E0B").role(Role.CHILD).build(),
                FamilyUser.builder().displayName("Kind 3").avatarColor("#8B5CF6").role(Role.CHILD).build()
        );

        repository.saveAll(members);
        log.info("Dev-Daten: {} Familienmitglieder angelegt.", members.size());
    }
}
