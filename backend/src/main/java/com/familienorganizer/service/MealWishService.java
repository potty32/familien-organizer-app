package com.familienorganizer.service;

import com.familienorganizer.dto.AcceptMealWishRequest;
import com.familienorganizer.dto.CreateMealWishRequest;
import com.familienorganizer.dto.MealWishResponse;
import com.familienorganizer.dto.TaskUserRef;
import com.familienorganizer.entity.*;
import com.familienorganizer.repository.FamilyUserRepository;
import com.familienorganizer.repository.MealWishRepository;
import com.familienorganizer.repository.PointTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealWishService {

    private final MealWishRepository mealWishRepository;
    private final FamilyUserRepository userRepository;
    private final PointTransactionRepository pointTransactionRepository;

    private static final int MEAL_WISH_POINTS = 5;

    public List<MealWishResponse> getAll() {
        return mealWishRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<MealWishResponse> getWeeklyPlan() {
        LocalDate monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate twoWeeksLater = monday.plusWeeks(2).with(java.time.DayOfWeek.SUNDAY);
        return mealWishRepository
                .findByStatusAndWeeklyPlanDateBetweenOrderByWeeklyPlanDate(
                        MealWishStatus.ACCEPTED, monday, twoWeeksLater)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public MealWishResponse create(CreateMealWishRequest request, UUID suggestedById) {
        FamilyUser user = findUserOrThrow(suggestedById);
        MealWish wish = MealWish.builder()
                .name(request.name())
                .description(request.description())
                .suggestedBy(user)
                .build();
        return toResponse(mealWishRepository.save(wish));
    }

    @Transactional
    public MealWishResponse accept(UUID id, AcceptMealWishRequest request, UUID activeUserId) {
        requireParent(activeUserId);
        MealWish wish = findOrThrow(id);

        if (wish.getStatus() == MealWishStatus.ACCEPTED) {
            // Datum aktualisieren ohne erneute Punkte
            wish.setWeeklyPlanDate(request.weeklyPlanDate());
            return toResponse(mealWishRepository.save(wish));
        }

        wish.setStatus(MealWishStatus.ACCEPTED);
        wish.setWeeklyPlanDate(request.weeklyPlanDate());

        if (!wish.isPointsAwarded()) {
            FamilyUser suggester = wish.getSuggestedBy();
            suggester.setTotalPoints(suggester.getTotalPoints() + MEAL_WISH_POINTS);
            userRepository.save(suggester);

            pointTransactionRepository.save(PointTransaction.builder()
                    .user(suggester)
                    .points(MEAL_WISH_POINTS)
                    .reason("Essenswunsch '" + wish.getName() + "' wurde in den Wochenplan aufgenommen")
                    .build());

            wish.setPointsAwarded(true);
        }

        return toResponse(mealWishRepository.save(wish));
    }

    @Transactional
    public MealWishResponse reject(UUID id, UUID activeUserId) {
        requireParent(activeUserId);
        MealWish wish = findOrThrow(id);
        wish.setStatus(MealWishStatus.REJECTED);
        wish.setWeeklyPlanDate(null);
        return toResponse(mealWishRepository.save(wish));
    }

    @Transactional
    public void delete(UUID id) {
        mealWishRepository.delete(findOrThrow(id));
    }

    private void requireParent(UUID userId) {
        FamilyUser user = findUserOrThrow(userId);
        if (user.getRole() != Role.PARENT) {
            throw new IllegalArgumentException("Nur Eltern dürfen Essenswünsche akzeptieren oder ablehnen.");
        }
    }

    private MealWish findOrThrow(UUID id) {
        return mealWishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Essenswunsch nicht gefunden: " + id));
    }

    private FamilyUser findUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Benutzer nicht gefunden: " + id));
    }

    private MealWishResponse toResponse(MealWish w) {
        FamilyUser u = w.getSuggestedBy();
        return new MealWishResponse(
                w.getId(), w.getName(), w.getDescription(), w.getStatus(),
                new TaskUserRef(u.getId(), u.getDisplayName(), u.getAvatarColor()),
                w.getWeeklyPlanDate(), w.isPointsAwarded(), w.getCreatedAt()
        );
    }
}
