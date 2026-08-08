package com.smartwallet.application.port.in;

import com.smartwallet.domain.model.Transaction;

public interface WithdrawUseCase {

    Transaction withdraw(WithdrawCommand command);
}
