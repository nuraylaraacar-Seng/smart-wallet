package com.smartwallet.application.port.in;

import com.smartwallet.domain.model.Transaction;

public interface DepositUseCase {

    Transaction deposit(DepositCommand command);
}
