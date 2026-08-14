package com.familienorganizer.controller;

import com.familienorganizer.dto.CreateShoppingItemRequest;
import com.familienorganizer.dto.ShoppingItemResponse;
import com.familienorganizer.service.ShoppingItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-items")
@RequiredArgsConstructor
public class ShoppingItemController {

    private final ShoppingItemService shoppingItemService;

    @GetMapping
    public List<ShoppingItemResponse> getAll() {
        return shoppingItemService.getAll();
    }

    @PostMapping
    public ResponseEntity<ShoppingItemResponse> create(
            @Valid @RequestBody CreateShoppingItemRequest request,
            @RequestHeader("X-Active-User-Id") UUID activeUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shoppingItemService.create(request, activeUserId));
    }

    @PatchMapping("/{id}/buy")
    public ShoppingItemResponse buy(
            @PathVariable UUID id,
            @RequestHeader("X-Active-User-Id") UUID activeUserId) {
        return shoppingItemService.buy(id, activeUserId);
    }

    @PatchMapping("/{id}/reject")
    public ShoppingItemResponse reject(
            @PathVariable UUID id,
            @RequestHeader("X-Active-User-Id") UUID activeUserId) {
        return shoppingItemService.reject(id, activeUserId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader("X-Active-User-Id") UUID activeUserId) {
        shoppingItemService.delete(id, activeUserId);
        return ResponseEntity.noContent().build();
    }
}
