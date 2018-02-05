package cash.xcl.api;

import cash.xcl.api.dto.*;

/**
 * Any responses from the Server to the client needed, in addition to ServerOut.
 */
public interface ClientIn {

    void queryFailedResponse(QueryFailedResponse queryFailedResponse);

    void addressInformationEvent(CreateNewAddressEvent createNewAddressEvent);

    void transferValueEvent(TransferValueEvent transferValueEvent);

    void clusterTransferInformationEvent(ClusterTransferStep3Event clusterTransferStep3Event);

    void depositValueEvent(DepositValueEvent depositValueEvent);

    void withdrawValueEvent(WithdrawValueEvent withdrawValueEvent);

    void subscriptionSuccess(SubscriptionSuccessResponse subscriptionSuccessResponse);

}
