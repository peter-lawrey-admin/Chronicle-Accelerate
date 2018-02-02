package cash.xcl.api;

import cash.xcl.api.dto.*;

/**
 * Any responses from the Server to the client needed, in addition to ServerOut.
 */
public interface ClientIn {

    void addressInformationEvent(AddressInformationEvent addressInformationEvent);
    void newAddressRejectedEvent(NewAddressRejectedEvent newAddressRejectedEvent);

    void transferValueInformationEvent(TransferInformationEvent transferInformationEvent);
    void transferValueRejectedEvent(TransferValueRejectedEvent transferValueRejectedEvent);

    void clusterTransferInformationEvent(ClusterTransferInformationEvent clusterTransferInformationEvent);
    void clusterTransferValueRejectedEvent(ClusterTransferValueRejectedEvent clusterTransferValueRejectedEvent);

    void depositValueInformationEvent(DepositValueInformationEvent depositValueInformationEvent);
    void depositValueRejectedEvent(DepositValueRejectedEvent depositValueRejectedEvent);

    void withdrawValueInformationEvent(WithdrawValueInformationEvent withdrawValueInformationEvent);
    void withdrawValueRejectedEvent(WithdrawValueRejectedEvent withdrawValueRejectedEvent);

    void subscriptionSuccess(SubscriptionSuccess subscriptionSuccess);
    void subscriptionFailed(SubscriptionFailed subscriptionFailed);

}
