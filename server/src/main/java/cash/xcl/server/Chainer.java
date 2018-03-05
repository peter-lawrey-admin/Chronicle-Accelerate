package cash.xcl.server;

import cash.xcl.api.ServerIn;
import cash.xcl.api.dto.TransactionBlockEvent;

public interface Chainer extends ServerIn {
    TransactionBlockEvent nextTransactionBlockEvent();
}
