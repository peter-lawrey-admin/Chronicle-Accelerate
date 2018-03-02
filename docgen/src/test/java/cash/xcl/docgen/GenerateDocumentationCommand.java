package cash.xcl.docgen;

/**
 * === A command used for testing the documentation generation
 * This command is going to be fun to use.
 */
@XclCommand(success = "cash.xcl.docgen.GenerateDocumentationEvent", failure = "cash.xcl.docgen.GenerateDocumentationFailure", id = "0x1111")
public class GenerateDocumentationCommand {

    @XclMessageField(name = "Destination Address")
    private long toAddress;
    @XclMessageField(name = "Ammount to Transfer")
    private double amount;
    @XclMessageField(name = "The Fee willing to pay")
    private Double fee;
    @XclMessageField(name = "Currency of the ammount to transfer")
    private String currency;
    @XclMessageField(name = "Reference info")
    private String reference;

    public void uselessMethod() {

    }
}
