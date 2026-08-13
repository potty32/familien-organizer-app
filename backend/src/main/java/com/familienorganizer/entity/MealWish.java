package com.familienorganizer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "meal_wishes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealWish {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MealWishStatus status = MealWishStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suggested_by_id", nullable = false)
    private FamilyUser suggestedBy;

    private LocalDate weeklyPlanDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean pointsAwarded = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
