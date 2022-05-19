package sg.com.argus.www.conquestgroup.utils;

public class Constants {
    public static final String  BASE_URL = "https://train.enam.gov.in/NamWebSrv/rest/";

    public static final String GET_STATES_URL = BASE_URL + "MastersUpdate/getStates";
    public static final String VERIFY_USER_URL =  BASE_URL + "verifyUser";
    public static final String SEND_BAG_WEIGHT_URL = BASE_URL + "SendBagWeightAuto";

    //WelcomeUserActivity
    public static final String GET_FEE_CATEGORY_URL = BASE_URL + "FeeCategory/getFeeCategoryListAutoSaleAgreement";
    public static final String GET_LOT_DETAILS_URL = BASE_URL + "GetLotDetails";
    public static final String GET_BAG_TYPES_URL = BASE_URL + "MastersUpdate/getBagTypes";

    //PrintWeighingSlipActivity
    public static final String SUMMARY_PRINT_URL = "https://www.enam.gov.in/NamWebSrv/rest/getPosWeighmentInfo";
    public static final float DEFAULT_PRINT_SIZE = 24;
    public static final float HEADING_SIZE = 26;
    public static final boolean BOLD_OFF = false;
    public static final boolean BOLD_ON = true;


    //APMC OPR

    public static final String APMC_OPR_ID = "72";
    public static final String CONQUEST_GROUP = "Conquest Group";
}

