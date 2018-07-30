package town.lost.examples.appreciation.api;

import im.xcl.platform.api.TransactionListener;
import net.openhft.chronicle.bytes.MethodId;

public interface ApperciationResultsListener extends TransactionListener {
    @MethodId(0x3000)
    void onBalance(OnBalance onBalance);
}
