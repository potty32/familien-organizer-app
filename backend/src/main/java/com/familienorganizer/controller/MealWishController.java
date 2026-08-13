package com.familienorganizer.controller;

import com.familienorganizer.dto.AcceptMealWishRequest;
import com.familienorganizer.dto.CreateMealWishRequest;
import com.familienorganizer.dto.MealWishResponse;
import com.familienorganizer.service.MealWishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/meal-wishes")
@RequiredArgsConstructor
public class MealWishController {

    private final MealWishService mealWishService;

    @GetMapping
    public List<MealWishResponse> getAll() {
        return mealWishService.getAll();
    }

    @GetMapping("/weekly-plan")
    public List<MealWishResponse> getWeeklyPlan() {
        return mealWishService.getWeeklyPlan();
    }

    @PostMapping
    public ResponseEntity<MealWishResponse> create(
            @Valid @RequestBody CreateMealWishRequest request,
            @RequestHeader("X-Active-User-Id") UUID activeUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mealWishService.create(request, activeUserId));
    }

    @PatchMapping("/{id}/accept")
    public MealWishResponse accept(
            @PathVariable UUID id,
            @Valid @RequestBody AcceptMealWishRequest request,
            @RequestHeader("X-Active-User-Id") UUID activeUserId) {
        return mealWishService.accept(id, request, activeUserId);
    }

    @PatchMapping("/{id}/reject")
    public MealWishResponse reject(
            @PathVariable UUID id,
            @RequestHeader("X-Active-User-Id") UUID activeUserId) {
        return mealWishService.reject(id, activeUserId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        mealWishService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
