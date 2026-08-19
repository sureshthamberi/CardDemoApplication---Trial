package com.carddemo.card.controller;

import com.carddemo.card.dto.*;
import com.carddemo.card.service.CardService;
import com.carddemo.common.dto.ApiResponse;
import com.carddemo.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Card Management", description = "Card search, detail, and update")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    @Operation(summary = "Search Cards")
    public ResponseEntity<ApiResponse<PageResponse<CardSummaryDto>>> search(
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String cardNumber,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(ApiResponse.ok("Cards retrieved",
                cardService.searchCards(accountId, cardNumber, page, pageSize)));
    }

    @GetMapping("/{cardNumber}")
    @Operation(summary = "Get Card Detail")
    public ResponseEntity<ApiResponse<CardDetailDto>> get(@PathVariable String cardNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Card retrieved", cardService.getCard(cardNumber)));
    }

    @PutMapping("/{cardNumber}")
    @Operation(summary = "Update Card")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(
            @PathVariable String cardNumber,
            @Valid @RequestBody UpdateCardRequest request,
            @AuthenticationPrincipal String principal) {
        long newVersion = cardService.updateCard(cardNumber, request, principal);
        return ResponseEntity.ok(ApiResponse.ok("Changes committed to database",
                Map.of("cardNumberMasked", "************" + cardNumber.substring(cardNumber.length() - 4),
                       "rowVersion", newVersion)));
    }
}
