package com.smartwallet.infrastructure.adapter.in.web;

import com.smartwallet.application.usecase.InternalLiquidityService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/liquidity")
public class AdminLiquidityController {

    private final InternalLiquidityService internalLiquidityService;

    public AdminLiquidityController(InternalLiquidityService internalLiquidityService) {
        this.internalLiquidityService = internalLiquidityService;
    }

    @PostMapping("/fund")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> fundWallet(@RequestBody AdminFundRequest request) {
        internalLiquidityService.manualAdminTransfer(request.targetWalletId(), request.amount(), request.currency());
        return ResponseEntity.ok().build();
    }

    public record AdminFundRequest(
            @NotNull UUID targetWalletId,
            @NotNull @Positive BigDecimal amount,
            @NotBlank String currency) {}
}