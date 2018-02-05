package cash.xcl.api;

import cash.xcl.api.dto.*;
import net.openhft.chronicle.core.io.Closeable;

public interface WeeklyEvents extends Closeable {
    // weekly events
    void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent);

    void treeBlockEvent(TreeBlockEvent treeBlockEvent);

    void feesEvent(FeesEvent feesEvent);

    void createNewAddressEvent(CreateNewAddressEvent createNewAddressEvent);

    void exchangeRateEvent(ExchangeRateEvent exchangeRateEvent);

    void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent);


    // FIXME work in progress - move me to the correct interface
    void clusterStatusQuery(ClusterStatusQuery clusterStatusQuery);
    //    void clusterStatusResponse(ClusterStatusResponse clusterStatusResponse);
    void clustersStatusQuery(ClustersStatusQuery clustersStatusQuery);
    //    void clustersStatusResponse(ClustersStatusResponse clustersStatusResponse);
    void currentBalanceEvent(CurrentBalanceEvent currentBalanceEvent);
    void currentBalanceQuery(CurrentBalanceQuery currentBalanceQuery);
    //    void currentBalanceResponse(CurrentBalanceResponse currentBalanceResponse);
    void exchangeRateQuery(ExchangeRateQuery exchangeRateQuery);
    //    void exchangeRateResponse(ExchangeRateResponse exchangeRateResponse);
    void executionReportEvent(ExecutionReportEvent executionReportEvent);


}
