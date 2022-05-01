package sg.com.argus.www.conquestgroup.utils;

public class Constants {
    public static final String  BASE_URL = "http://www.train.enam.gov.in/NamWebSrv/rest/";

    public static final String GET_STATES_URL = BASE_URL + "MastersUpdate/getStates";
    public static final String VERIFY_USER_URL =  BASE_URL + "verifyUser";
    public static final String SEND_BAG_WEIGHT_URL = BASE_URL + "SendBagWeightAuto";
    public static final String SUMMARY_PRINT_URL = BASE_URL+ "getPosWeighmentInfo";

    //WelcomeUserActivity
    public static final String GET_FEE_CATEGORY_URL = BASE_URL + "FeeCategory/getFeeCategoryListAutoSaleAgreement";
    public static final String GET_LOT_DETAILS_URL = BASE_URL + "GetLotDetails";
    public static final String GET_BAG_TYPES_URL = BASE_URL + "MastersUpdate/getBagTypes";
}
