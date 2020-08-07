package com.shrinkcom.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.shrinkcom.app.SplashPage;
import com.shrinkcom.pojo.EmergencyPojo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionManager {
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "PremKumar";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_AD = "IsADIn";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_USERID = "userid";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USERIMAGE = "userimage";
    public static final String KEY_GCM_ID = "gcmId";
    private static final String FIRST_LAUNCH = "firstLaunch";

    public static final String KEY_COUNTRYCODE = "countrycode";
    public static final String KEY_PHONENO = "phoneno";
    public static final String KEY_REFERAL_CODE = "referalcode";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_CATEGORY_ID = "categoryId";
    public static final String KEY_Language_code = "language_code";
    public static final String KEY_Language = "language";

    public static final String KEY_COUPON_CODE = "coupon";
    public static final String KEY_WALLET_AMOUNT = "walletAmount";

    public static final String KEY_XMPP_USERID = "xmppUserId";
    public static final String KEY_XMPP_SEC_KEY = "xmppSecKey";

    public static final String KEY_HOST_URL = "xmpphostUrl";
    public static final String KEY_HOST_NAME = "xmpphostName";
    public static final String KEY_ID_NAME = "Id_Name";
    public static final String KEY_VEHICLE_BitMap_IMAGE = "bitmap";
    public static final String KEY_ABOUT_US = "about_us";
    public static final String KEY_USER_IMAGE = "user_image";

    public static final String KEY_APP_STATUS = "appStatus";

    public static final String KEY_AD_TITLE = "ad_title";
    public static final String KEY_AD_MSG = "ad_msg";

    public static final String KEY_AD_BANNER = "ad_banner";
    public static final String KEY_PHONE_MASKING_STATUS= "phonemasking";

    public static final String KEY_SHARE_POOL_STATUS= "share_pool_status";

    public static final String KEY_CUS_PHO = "cus_phone";
    public static final String KEY_CUS_ADR = "cus_adr";
    public static final String EMERGENCYDETAILS = "emergencydetails";

    public static final String KEY_LNG = "lontitude";
    public static final String KEY_LAT = "latitude";
    public static final String KEY_ESTIMATE_AMOUNT = "estimateamount";

    public static final String KEY_EM_NUMB = "emergency_numbers";

    public static final String MY_LIST = "my_list";
    private static final Type LIST_TYPE = new TypeToken<List<EmergencyPojo>>() {}.getType();

    public static final String KEY_BaseUrl = "BaseUrl_";



    public static final String KEY_SERVICETYPE = "servicetype";
    public static final String KEY_PICKUP_LOCATION = "pickuplocation";
    public static final String KEY_PICKUP_LOCATION_LAT = "pickuplocationlat";
    public static final String KEY_PICKUP_LOCATION_LON = "pickuplocationlon";
    public static final String KEY_DROP_LOCATION = "droplocation";
    public static final String KEY_DROP_LOCATION_LAT = "droplocationlat";
    public static final String KEY_DROP_LOCATION_LON = "droplocationlon";

    public static final String KEY_PICK_COMPANYNAME = "pickcompanyname";
    public static final String KEY_PICK_USERNAME = "pickusername";
    public static final String KEY_PICK_PHONECODE = "pickphonecode";
    public static final String KEY_PICK_PHONE = "pickphonephone";
    public static final String KEY_PICK_EMAILID = "pickemailid";
    public static final String KEY_PICK_ADDRESS1 = "pickaddress1";
    public static final String KEY_PICK_ADDRESS2 = "pickaddress2";
    public static final String KEY_PICK_COUNTRY = "pickcountry";
    public static final String KEY_PICK_STATE = "pickstate";
    public static final String KEY_PICK_CITY = "pickcity";
    public static final String KEY_PICK_POSTCODE = "pickcity";









    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String email, String userid, String username, String userimage, String countrycode, String phoneno, String referalcode, String category,String gcmID) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USERID, userid);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USERIMAGE, userimage);
        editor.putString(KEY_COUNTRYCODE, countrycode);
        editor.putString(KEY_PHONENO, phoneno);
        editor.putString(KEY_REFERAL_CODE, referalcode);
        editor.putString(KEY_CATEGORY, category);
        editor.putString(KEY_GCM_ID, gcmID);

        // commit changes
        editor.commit();
    }

    public void setCategoryID(String categoryID)
    {
        editor.putString(KEY_CATEGORY_ID, categoryID);
        editor.commit();
    }

    public void createWalletAmount(String amount)
    {
        editor.putString(KEY_WALLET_AMOUNT, amount);
        // commit changes
        editor.commit();
    }

    //------username update code-----
    public void setUserNameUpdate(String name) {
        editor.putString(KEY_USERNAME, name);
        editor.commit();
    }

    //------MobileNumber update code-----
    public void setMobileNumberUpdate(String code,String mobile) {
        editor.putString(KEY_COUNTRYCODE, code);
        editor.putString(KEY_PHONENO, mobile);
        editor.commit();
    }

    //------string user coupon code-----
    public void setCouponCode(String code) {
        editor.putString(KEY_COUPON_CODE, code);
        editor.commit();
    }
    //------string user Estimate Amount-----
    public void setEstimateAmount(String code) {
        editor.putString(KEY_ESTIMATE_AMOUNT, code);
        editor.commit();
    }

    //------ Xmpp Connect Secrect Code-----
    public void setXmppKey(String userId,String secretKey) {
        editor.putString(KEY_XMPP_USERID, userId);
        editor.putString(KEY_XMPP_SEC_KEY, secretKey);
        editor.commit();
    }
    public void setEmergencyNumber(String gcmID) {
        editor.putString(KEY_EM_NUMB, gcmID);
        editor.commit();
    }
    public String getEmergencyNumber() {
        return pref.getString(KEY_EM_NUMB,"");

    }
    public void setXmpp(String userId,String secretKey) {
        editor.putString(KEY_HOST_URL, userId);
        editor.putString(KEY_HOST_NAME, secretKey);
        editor.commit();
    }

    public void setKeyPhoneMaskingStatus(String phonemasking)
    {
        editor.putString(KEY_PHONE_MASKING_STATUS, phonemasking);
        editor.commit();
    }
    public HashMap<String, String> getPhoneMasking() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_PHONE_MASKING_STATUS, pref.getString(KEY_PHONE_MASKING_STATUS, ""));
        return catID;
    }
    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
        user.put(KEY_USERID, pref.getString(KEY_USERID, ""));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, ""));
        user.put(KEY_USERIMAGE, pref.getString(KEY_USERIMAGE, ""));
        user.put(KEY_COUNTRYCODE, pref.getString(KEY_COUNTRYCODE, ""));
        user.put(KEY_PHONENO, pref.getString(KEY_PHONENO, ""));
        user.put(KEY_REFERAL_CODE, pref.getString(KEY_REFERAL_CODE, ""));
        user.put(KEY_CATEGORY, pref.getString(KEY_CATEGORY, ""));
        user.put(KEY_GCM_ID, pref.getString(KEY_GCM_ID, ""));
        user.put(KEY_ID_NAME, pref.getString(KEY_ID_NAME, ""));
        user.put(KEY_Language_code, pref.getString(KEY_Language_code, ""));
        user.put(KEY_ABOUT_US, pref.getString(KEY_ABOUT_US, ""));
        user.put(KEY_USER_IMAGE, pref.getString(KEY_USER_IMAGE, ""));
        user.put(KEY_SHARE_POOL_STATUS, pref.getString(KEY_SHARE_POOL_STATUS, ""));
        user.put(KEY_XMPP_SEC_KEY, pref.getString(KEY_XMPP_SEC_KEY, ""));
        return user;
    }

    public void setBaseUrl(String BaseUrl1){
        editor.putString(KEY_BaseUrl, BaseUrl1);
        editor.commit();
    }
    public String getBaseUrl(){
        return pref.getString(KEY_BaseUrl,"");
    }
    //-----------Get user coupon code-----
    public HashMap<String, String> getCouponCode() {
        HashMap<String, String> code = new HashMap<String, String>();
        code.put(KEY_COUPON_CODE, pref.getString(KEY_COUPON_CODE, ""));

        return code;
    }

    //-----------Get CategoryId code-----
    public HashMap<String, String> getCategoryID() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_CATEGORY_ID, pref.getString(KEY_CATEGORY_ID, ""));
        return catID;
    }
    public void setlamguage(String userId,String secretKey) {
        editor.putString(KEY_Language_code, userId);
        editor.putString(KEY_Language, secretKey);
        editor.commit();
    }
    public void setVehicle_BitmapImage(String count){
        editor.putString(KEY_VEHICLE_BitMap_IMAGE, count);
        editor.commit();
    }
    public HashMap<String, String> getVehicleBitmap() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_VEHICLE_BitMap_IMAGE, pref.getString(KEY_VEHICLE_BitMap_IMAGE, ""));
        return catID;
    }
    public HashMap<String, String> getLanaguage() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_Language, pref.getString(KEY_Language, ""));
        return catID;
    }
    public void setAgent(String userId) {
        editor.putString(KEY_ID_NAME, userId);

        editor.commit();
    }
    public void setAbout(String about) {
        editor.putString(KEY_ABOUT_US, about);

        editor.commit();
    }

    public void setAds(String title,String msg,String banner) {
        editor.putString(KEY_AD_BANNER, banner);
        editor.putString(KEY_AD_MSG, msg);
        editor.putString(KEY_AD_TITLE, title);

        editor.commit();
    }
    public HashMap<String, String> getAds() {
        HashMap<String, String> adDetail = new HashMap<String, String>();
        adDetail.put(KEY_AD_MSG, pref.getString(KEY_AD_MSG, ""));
        adDetail.put(KEY_AD_BANNER, pref.getString(KEY_AD_BANNER, ""));
        adDetail.put(KEY_AD_TITLE, pref.getString(KEY_AD_TITLE, ""));
        return adDetail;
    }


    public void setuser_image(String image) {
        editor.putString(KEY_USER_IMAGE, image);
        editor.commit();
    }


    public HashMap<String, String> getuser_image() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_USER_IMAGE, pref.getString(KEY_USER_IMAGE, ""));
        return catID;
    }
    public HashMap<String, String> getAbout() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_ABOUT_US, pref.getString(KEY_ABOUT_US, ""));
        return catID;
    }
    public HashMap<String, String> getAgent() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_ID_NAME, pref.getString(KEY_ID_NAME, ""));
        return catID;
    }
    public HashMap<String, String> getLanaguageCode() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_Language_code, pref.getString(KEY_Language_code, ""));
        return catID;
    }
    //-----------Get XMPP Secret Key-----
    public HashMap<String, String> getXmppKey() {
        HashMap<String, String> code = new HashMap<String, String>();
        code.put(KEY_XMPP_USERID, pref.getString(KEY_XMPP_USERID, ""));
        code.put(KEY_XMPP_SEC_KEY, pref.getString(KEY_XMPP_SEC_KEY, ""));
        return code;
    }
    public HashMap<String, String> getXmpp() {
        HashMap<String, String> code = new HashMap<String, String>();
        code.put(KEY_HOST_URL, pref.getString(KEY_HOST_URL, ""));
        code.put(KEY_HOST_NAME, pref.getString(KEY_HOST_NAME, ""));
        return code;
    }

    //-----------Get Wallet Amount-----
    public HashMap<String, String> getWalletAmount() {
        HashMap<String, String> code = new HashMap<String, String>();
        code.put(KEY_WALLET_AMOUNT, pref.getString(KEY_WALLET_AMOUNT, ""));
        return code;
    }

    //------string App Status----
    public void setAppStatus(String state) {
        editor.putString(KEY_APP_STATUS, state);
        editor.commit();
    }

    //-----------Get App Status-----
    public HashMap<String, String> getAppStatus() {
        HashMap<String, String> code = new HashMap<String, String>();
        code.put(KEY_APP_STATUS, pref.getString(KEY_APP_STATUS, ""));
        return code;
    }

    public String getEstimateAmount() {

        return pref.getString(KEY_ESTIMATE_AMOUNT, "");
    }

    //------string App Status----
    public void setShareStatus(String state) {
        editor.putString(KEY_SHARE_POOL_STATUS, state);
        editor.commit();
    }

    //-----------Get App Status-----
    public HashMap<String, String> getShareStatus() {
        HashMap<String, String> code = new HashMap<String, String>();
        code.put(KEY_SHARE_POOL_STATUS, pref.getString(KEY_SHARE_POOL_STATUS, ""));
        return code;
    }

    public void putEmergencyContactDetails(ArrayList<EmergencyPojo> aStandardDeliveryInfos) {

        Gson aGson = new Gson();

        String aStandardDeliveryInfoList = aGson.toJson(aStandardDeliveryInfos);

        editor.putString("emergency", aStandardDeliveryInfoList);

        editor.commit();
    }

    /**
     * Get the Quick Delivery Info list
     *
     * @return
     */
    public ArrayList<EmergencyPojo> getEmergencyContactDetails() {

        ArrayList<EmergencyPojo> aStandardDeliveryInfo = null;

        String aStandardDeliveryInfoJSON = pref.getString("emergency", null);

        if (aStandardDeliveryInfoJSON != null) {

            Gson aGson = new Gson();

            aStandardDeliveryInfo = aGson.fromJson(
                    aStandardDeliveryInfoJSON, new TypeToken<ArrayList<EmergencyPojo>>() {
                    }.getType());
        }

        return aStandardDeliveryInfo;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, SplashPage.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(i);

    }
    public void setUserImageUpdate(String image) {
        editor.putString(KEY_USERIMAGE, image);
        editor.commit();
    }

    public void setcustomerdetail(String phone,String addr) {
        editor.putString(KEY_CUS_PHO, phone);
        editor.putString(KEY_CUS_ADR, addr);
        editor.commit();
    }

    public HashMap<String, String> getcustomerdetail() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_CUS_ADR, pref.getString(KEY_CUS_ADR, ""));
        catID.put(KEY_CUS_PHO, pref.getString(KEY_CUS_PHO, ""));
        return catID;
    }

    public void setADS(Boolean categoryID)
    {
        editor.putBoolean(IS_AD, categoryID);
        editor.commit();
    }

    public boolean isAds() {
        return pref.getBoolean(IS_AD, false);
    }



    public void setlatlong(String lat,String lng) {
        editor.putString(KEY_LAT, lat);
        editor.putString(KEY_LNG, lng);
        editor.commit();
    }

    public HashMap<String, String> getlatlong() {
        HashMap<String, String> catID = new HashMap<String, String>();
        catID.put(KEY_LAT, pref.getString(KEY_LAT, ""));
        catID.put(KEY_LNG, pref.getString(KEY_LNG, ""));
        return catID;
    }


    /**
     * Quick check for login
     **/
    // Get Login State

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }




    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(FIRST_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean FirstLaunch() {
        return pref.getBoolean(FIRST_LAUNCH, true);
    }


    public  void setStringValue(String key,String value){
        editor.putString(key, value);
        editor.commit();
    }


    public String getStringValue(String key){
        return  pref.getString(key,"");
    }
}
