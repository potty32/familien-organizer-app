package com.familienorganizer.repository;

import com.familienorganizer.entity.MealWish;
import com.familienorganizer.entity.MealWishStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MealWishRepository extends JpaRepository<MealWish, UUID> {

    List<MealWish> findAllByOrderByCreatedAtDesc();

    List<MealWish> findByStatusAndWeeklyPlanDateBetweenOrderByWeeklyPlanDate(
            MealWishStatus status, LocalDate from, LocalDate to);
}
