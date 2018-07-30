package town.lost.examples.appreciation;

import im.xcl.platform.api.MessageRouter;
import town.lost.examples.appreciation.api.AppreciationResultsListener;
import town.lost.examples.appreciation.api.AppreciationTransactionListener;

/**
 * Combining interface for all messages
 */
public interface AppreciationTester extends
        MessageRouter<AppreciationResultsListener>,
        AppreciationResultsListener,
        AppreciationTransactionListener {
}
