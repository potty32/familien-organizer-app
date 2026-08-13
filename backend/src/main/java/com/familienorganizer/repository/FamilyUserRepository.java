package com.familienorganizer.repository;

import com.familienorganizer.entity.FamilyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FamilyUserRepository extends JpaRepository<FamilyUser, UUID> {

    List<FamilyUser> findByActiveTrueOrderByDisplayNameAsc();
}
