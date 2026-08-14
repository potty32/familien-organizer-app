package com.familienorganizer.service;

import com.familienorganizer.dto.CreateShoppingItemRequest;
import com.familienorganizer.dto.ShoppingItemResponse;
import com.familienorganizer.dto.TaskUserRef;
import com.familienorganizer.entity.*;
import com.familienorganizer.repository.FamilyUserRepository;
import com.familienorganizer.repository.PointTransactionRepository;
import com.familienorganizer.repository.ShoppingItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingItemService {

    private static final int SHOPPING_POINTS = 5;

    private final ShoppingItemRepository shoppingItemRepository;
    private final FamilyUserRepository familyUserRepository;
    private final PointTransactionRepository pointTransactionRepository;

    public List<ShoppingItemResponse> getAll() {
        return shoppingItemRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ShoppingItemResponse create(CreateShoppingItemRequest request, UUID userId) {
        FamilyUser user = familyUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Benutzer nicht gefunden"));
        ShoppingItem item = ShoppingItem.builder()
                .name(request.name())
                .note(request.note())
                .addedBy(user)
                .build();
        return toResponse(shoppingItemRepository.save(item));
    }

    @Transactional
    public ShoppingItemResponse buy(UUID itemId, UUID parentId) {
        requireParent(parentId);
        ShoppingItem item = findItem(itemId);
        requirePending(item);

        FamilyUser addedBy = item.getAddedBy();
        addedBy.setTotalPoints(addedBy.getTotalPoints() + SHOPPING_POINTS);
        familyUserRepository.save(addedBy);

        pointTransactionRepository.save(PointTransaction.builder()
                .user(addedBy)
                .points(SHOPPING_POINTS)
                .reason("Einkauf genehmigt: " + item.getName())
                .build());

        item.setStatus(ShoppingItemStatus.BOUGHT);
        item.setPointsProcessed(true);
        return toResponse(shoppingItemRepository.save(item));
    }

    @Transactional
    public ShoppingItemResponse reject(UUID itemId, UUID parentId) {
        requireParent(parentId);
        ShoppingItem item = findItem(itemId);
        requirePending(item);

        FamilyUser addedBy = item.getAddedBy();
        addedBy.setTotalPoints(addedBy.getTotalPoints() - SHOPPING_POINTS);
        familyUserRepository.save(addedBy);

        pointTransactionRepository.save(PointTransaction.builder()
                .user(addedBy)
                .points(-SHOPPING_POINTS)
                .reason("Einkauf abgelehnt: " + item.getName())
                .build());

        item.setStatus(ShoppingItemStatus.REJECTED);
        item.setPointsProcessed(true);
        return toResponse(shoppingItemRepository.save(item));
    }

    @Transactional
    public void delete(UUID itemId, UUID userId) {
        ShoppingItem item = findItem(itemId);
        FamilyUser user = familyUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Benutzer nicht gefunden"));

        boolean isParent = user.getRole() == Role.PARENT;
        boolean isCreator = item.getAddedBy().getId().equals(userId);

        if (!isParent && !isCreator) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nur Eltern oder der Ersteller dürfen löschen");
        }
        if (!isParent && item.isPointsProcessed()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nur Eltern dürfen verarbeitete Einträge löschen");
        }

        shoppingItemRepository.delete(item);
    }

    private void requireParent(UUID userId) {
        FamilyUser user = familyUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Benutzer nicht gefunden"));
        if (user.getRole() != Role.PARENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nur Eltern dürfen diese Aktion ausführen");
        }
    }

    private void requirePending(ShoppingItem item) {
        if (item.getStatus() != ShoppingItemStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Artikel wurde bereits bearbeitet");
        }
    }

    private ShoppingItem findItem(UUID id) {
        return shoppingItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artikel nicht gefunden"));
    }

    private ShoppingItemResponse toResponse(ShoppingItem item) {
        FamilyUser addedBy = item.getAddedBy();
        return new ShoppingItemResponse(
                item.getId(),
                item.getName(),
                item.getNote(),
                item.getStatus(),
                new TaskUserRef(addedBy.getId(), addedBy.getDisplayName(), addedBy.getAvatarColor()),
                item.isPointsProcessed(),
                item.getCreatedAt()
        );
    }
}
