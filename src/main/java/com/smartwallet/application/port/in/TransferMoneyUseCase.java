package com.smartwallet.application.port.in;

import com.smartwallet.domain.model.Transaction;

public interface TransferMoneyUseCase {

    Transaction transfer(TransferMoneyCommand command);
}
