package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java)
 */
public interface SecurityType {
    /**
     * Tag number for this field
     */
    int FIELD = 167;

    String BANKERS_ACCEPTANCE = "BA";

    String CONVERTIBLE_BOND = "CB";

    String CERTIFICATE_OF_DEPOSIT = "CD";

    String COLLATERALIZE_MORTGAGE_OBLIGATION = "CMO";

    String CORPORATE_BOND = "CORP";

    String COMMERCIAL_PAPER = "CP";

    String CORPORATE_PRIVATE_PLACEMENT = "CPP";

    String COMMON_STOCK = "CS";

    String FEDERAL_HOUSING_AUTHORITY = "FHA";

    String FEDERAL_HOME_LOAN = "FHL";

    String FEDERAL_NATIONAL_MORTGAGE_ASSOCIATION = "FN";

    String FOREIGN_EXCHANGE_CONTRACT = "FOR";

    String FUTURE = "FUT";

    String GOVERNMENT_NATIONAL_MORTGAGE_ASSOCIATION = "GN";

    String TREASURIES_PLUS_AGENCY_DEBENTURE = "GOVT";

    String MUTUAL_FUND = "MF";

    String MORTGAGE_INTEREST_ONLY = "MIO";

    String MORTGAGE_PRINCIPAL_ONLY = "MPO";

    String MORTGAGE_PRIVATE_PLACEMENT = "MPP";

    String MISCELLANEOUS_PASSTHRU = "MPT";

    String MUNICIPAL_BOND = "MUNI";

    String NO_ISITC_SECURITY_TYPE = "NONE";

    String OPTION = "OPT";

    String PREFERRED_STOCK = "PS";

    String REPURCHASE_AGREEMENT = "RP";

    String REVERSE_REPURCHASE_AGREEMENT = "RVRP";

    String STUDENT_LOAN_MARKETING_ASSOCIATION = "SL";

    String TIME_DEPOSIT = "TD";

    String US_TREASURY_BILL = "USTB";

    String WARRANT = "WAR";

    String CATS_TIGERS = "ZOO";

    /**
     * @param securityType &gt; FIX TAG 167
     */
    void securityType(String securityType);

    default String securityType() {
        throw new UnsupportedOperationException();
    }

    static String asString(String value) {
        return value;
    }
}
