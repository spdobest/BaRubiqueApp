package com.bestdealfinance.bdfpartner.application;

/**
 * Created by Harshit.Gupta on 28-Jul-16.
 */
public class URL {

    //TODO Production URLS


    //public static final String ROOT_URL_FI = "https://prod-fiapi.rubique.com/";
    //public static final String S3_URL = "https://staticcontent.rubique.com/json/";
    //public static final String ROOT_URL_V2 = "https://api.rubique.com/";

    //TODO Test URLS
    public static  String ROOT_URL_V2 =  "http://testapi.spot.rubique.com/";
    public static final String ROOT_URL_FI = "http://testfiapi.rubique.com/";// Not using
    public static final String S3_URL = "https://staticcontent.rubique.com/json/";// Not using

    /////////////// DONT EDIT BEFORE IT ////////////////////////////////////////////////////////////


    public static final String GCM_REGISTER = ROOT_URL_V2 + "notification/registerApp";
    public static final String GCM_UPDATE = ROOT_URL_V2 + "notification/updateAppRegistration";
    public static final String APP_UPDATE = ROOT_URL_V2 + "SystemVar/load";


    public static final String CUSTOMER_LOGIN = ROOT_URL_V2 + "Customer/loginCustomerOtp";
    public static final String CUSTOMER_REGISTER = ROOT_URL_V2 + "Customer/registerCustomerOtp";
    public static final String CUSTOMER_VERIFY_OTP = ROOT_URL_V2 + "Customer/verifyOtp";
    public static final String VERIFY_PROMO = ROOT_URL_V2 + "index.php/Promotion/getPromoCode";

    public static final String FETCH_ALL_PRODUCTS = ROOT_URL_V2 + "index.php/Product/getAllProductTypes"; // Not using
    public static final String FETCH_ALL_PRODUCTS_DETAILS = ROOT_URL_V2 + "index.php/Product/getProductDetailByProductType";
    public static final String FETCH_ALL_PRODUCTS_BY_TYPE = ROOT_URL_V2 + "index.php/Product/getProductByType";

    public static final String FETCH_PROFESSION = ROOT_URL_V2 + "Profile/profession";// Not using
    public static final String FETCH_ALL_CITY = ROOT_URL_V2 + "Profile/city";// Not using
    public static final String FETCH_APP_CITY = ROOT_URL_V2 + "Profile/applicationcity";// Not using
    public static final String FETCH_OCCUPATION = ROOT_URL_V2 + "Profile/occupation";// Not using
//    public static final String REFER_A_LEAD = ROOT_URL_V2 + "index.php/Customer/submitMobReferral";
public static final String REFER_A_LEAD = ROOT_URL_V2 + "leads/createReferredLead";
    //get profession url
    public static final String FETCH_ALL_PROFESSION = ROOT_URL_V2 + "List_controller/getList";

    public static final String GET_UBER_LIST_SUGGESTION = ROOT_URL_V2 + "List_controller/listAutoSuggestion";
    public static final String GET_UBER_LIST_FULL = ROOT_URL_V2 + "List_controller/getList";

    public static final String CREATE_INCOMPLETE_LEAD = ROOT_URL_V2 + "leads/createIncompleteLead";

    public static final String GETTEMPLATEPARTIAL = ROOT_URL_V2 + "leads/getTemplate";
    public static final String PRODUCT_INFO_BY_ID = ROOT_URL_V2 + "product/getProductInfoById";

    public static final String GET_LEAD_LIST = ROOT_URL_V2 + "leads/getMyApplications";

    public static final String UPDATE_INCOMPLETE_LEAD = ROOT_URL_V2 + "index.php/Leads/updateIncompleteLead";
    public static final String QUALIFY_SINGLE = ROOT_URL_V2 + "index.php/leads/qualifyByProduct";
    public static final String FETCH_PRODUCT_BY_ID = ROOT_URL_V2 + "Product/getProductInfoById";// Not using
    public static final String GET_SINGLE_TEMPLATE = ROOT_URL_V2 + "leads/template";// Not using
    public static final String FETCH_NEARBY_RFC = ROOT_URL_V2 + "Partner/getNearByPartnerOrg";
    public static final String GET_TEMPLATE_BY_META = ROOT_URL_V2 + "Leads/templateByMultimeta";
    public static final String SUBMIT_APPLICATION_BA = ROOT_URL_V2 + "leads/submitApplicationBa";
    ;
    public static final String GET_REFERRAL_DETAIL = ROOT_URL_V2 + "index.php/customer/getLeadDetails";
    public static final String PAYOUT_CALCULATOR = ROOT_URL_V2 + "Payout/payoutCalculator";
    public static final String LEADER_BOARD = ROOT_URL_V2 + "Leads/getLeadCount";
    public static final String MEETING_LIST = ROOT_URL_V2 + "Meeting/getAllMeetings";
    public static final String LEAD_COUNT = ROOT_URL_V2 + "Leads/getLeadCountAccStatus";
    public static final String UPDATE_FORM_COLLECTED = ROOT_URL_V2 + "leads/updateformCollected";
    public static final String GET_EXACT_PAYOUT = ROOT_URL_V2 + "Payout/getFinalPayout";
    public static final String UPDATE_DATA_LIST = ROOT_URL_V2 + "Data/insert";// Not using
    public static final String UPDATE_DATA_SMS = ROOT_URL_V2 + "Data/insertSMS";// Not using
    public static final String FETCH_DOCUMENT_LIST = ROOT_URL_V2 + "Documents/getProductTypeList";
    public static final String FETCH_ALL_LEAD_FIELDS = ROOT_URL_V2 + "leads/getLeadFields";
    public static final String UPDATE_LEAD_RFC = ROOT_URL_V2 + "leads/updateRFC";
    public static final String SCHEDULE_MEETING = ROOT_URL_V2 + "Meeting/insert";
    public static final String TNC_LINK = ROOT_URL_V2 + "ba_tc.html";
    public static final String VERIFY_ADHAR = ROOT_URL_V2 + "Perfios/startcall";
    public static final String OTP_VERIFICATION = ROOT_URL_V2 + "Perfios/otpverification";
    public static final String GET_ADHAR_DATA = ROOT_URL_V2 + "Perfios/getAdharData";
    public static final String UPDATE_ADHAR_DATA = ROOT_URL_V2 + "leads/AdharCardDataIncompletedLead";
    public static final String UPDATE_ADHAR_INDB = ROOT_URL_V2 + "Perfios/getAdharData";// Not using
    public static final String DUPLICATE_LEAD = ROOT_URL_V2 + "leads/duplicateLeadForBA";
    public static final String UPDATE_CUSTOMER_PROFILE = ROOT_URL_V2 + "Profile/upsertCustomerProfile";
    public static final String GET_CUSTOMER_PROFILE = Util.ROOT_URL_V2 + "Profile/getCustomerProfile";
    public static final String GIFTXOXO_AUTH = "https://enterprise.giftxoxo.com/home/prest/authentication";
    //public static final String GIFTXOXO_AUTH = "https://52.74.236.98:8094/home/prest/authentication";
    public static final String GET_REWARD_POINTS = "http://sandbox.xoxoengage.com/home/prest/getpoints";
    public static final String GET_LOGIN = ROOT_URL_V2 + "index.php/Customer/getLogin";
    public static final String GIFTXOXO_REDIRECT = "https://enterprise.giftxoxo.com/home/auth/login";
    //public static final String GIFTXOXO_REDIRECT = "https://52.74.236.98:8094/home/auth/login";
    public static final String UPDATE_CONSENT= "http://testapi-newarch.rubique.com/reminder/updateConsent";

    public interface UrlConstants {
        public static final String LIST_ID = "list_id";
        public static final String LIST_ID_VALUE = "96";
    }
}
