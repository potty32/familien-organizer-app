package com.familienorganizer.service;

import com.familienorganizer.dto.CreateUserRequest;
import com.familienorganizer.dto.SessionRequest;
import com.familienorganizer.dto.UserResponse;
import com.familienorganizer.entity.FamilyUser;
import com.familienorganizer.repository.FamilyUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final FamilyUserRepository repository;

    public List<UserResponse> getAll() {
        return repository.findByActiveTrueOrderByDisplayNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getById(UUID id) {
        return toResponse(findActiveOrThrow(id));
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        FamilyUser user = FamilyUser.builder()
                .displayName(request.displayName())
                .avatarColor(request.avatarColor())
                .role(request.role())
                .pinCode(request.pinCode())
                .build();
        return toResponse(repository.save(user));
    }

    @Transactional
    public UserResponse update(UUID id, CreateUserRequest request) {
        FamilyUser user = findActiveOrThrow(id);
        user.setDisplayName(request.displayName());
        user.setAvatarColor(request.avatarColor());
        user.setRole(request.role());
        user.setPinCode(request.pinCode());
        return toResponse(repository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        FamilyUser user = findActiveOrThrow(id);
        user.setActive(false);
        repository.save(user);
    }

    public UserResponse selectProfile(SessionRequest request) {
        FamilyUser user = findActiveOrThrow(request.userId());
        if (user.getPinCode() != null && !user.getPinCode().equals(request.pinCode())) {
            throw new IllegalArgumentException("PIN ist nicht korrekt.");
        }
        return toResponse(user);
    }

    private FamilyUser findActiveOrThrow(UUID id) {
        FamilyUser user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Benutzer nicht gefunden: " + id));
        if (!user.isActive()) {
            throw new EntityNotFoundException("Benutzer nicht gefunden: " + id);
        }
        return user;
    }

    private UserResponse toResponse(FamilyUser user) {
        return new UserResponse(
                user.getId(),
                user.getDisplayName(),
                user.getAvatarColor(),
                user.getRole(),
                user.getTotalPoints()
        );
    }
}
