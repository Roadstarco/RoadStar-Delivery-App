package com.shrinkcom.iconstant;

public interface Iconstant {



     String Main_URL = "http://wsalna.com/roadstar/";

    String BaseUrl = Main_URL + "v7/";
    String URL = Main_URL + "v7/api/";
    String Main_URL_Login_Base_Url_URL = Main_URL + "v7/api/";
    String XMPP_HOST_URL = "zuber.xyz";
    String XMPP_SERVICE_NAME = "messaging.dectar.com";

    //Local Url
 /*  String Main_URL = "http://192.168.1.251:8081/product-working/dectar/cabily/";
    String BaseUrl = Main_URL + "v7/";
    String URL = Main_URL + "v7/api/";
    String XMPP_HOST_URL = "192.168.1.150";
    String XMPP_SERVICE_NAME = "casp83";
*/
    /*String Main_URL = "http://project.dectar.com/cabilydemo/";
    String BaseUrl = Main_URL + "v7/";
    String URL = Main_URL + "v7/api/";
    String XMPP_HOST_URL = "192.168.1.150";
    String XMPP_SERVICE_NAME = "casp83";
*/

    String setUserLocation = URL + "user/location/update";
    String rideassist = URL + "user/ride_assite";
    String loginurl = Main_URL_Login_Base_Url_URL + "user/login";
    String register_url = URL + "user/validate";
    String facebook_register_url = URL + "user/login/social";
    String social_check_url = URL + "user/validate/social";


    String register_otp_url = URL + "user/register";
    String forgot_password_url = URL + "user/password/forgot";
    String reset_password_url = URL + "user/password/reset";
    String otp_resend_url = URL + "user/otp/resend";

    String BookMyRide_url = URL + "map/drivers";
    String UpdateToken = URL + "user/testing";
    String couponCode_apply_url = URL + "coupon/apply";
    String confirm_ride_url = URL + "booking/make";
    String retry_ride_url = URL + "booking/retry";
    String delete_ride_url = URL + "booking/delete";
    String estimate_price_url = URL + "estimate/get";
    String place_search_url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyD_p2BnXUNOgG-h5n04BrmO57qxcjZeZZo&input=";
    String GetAddressFrom_LatLong_url = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyD_p2BnXUNOgG-h5n04BrmO57qxcjZeZZo&placeid=";
    /*    String place_search_url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyAKDx43QL5xXBitDdviXavpqLPsGZ3uY6o&input=";
        String GetAddressFrom_LatLong_url = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyAKDx43QL5xXBitDdviXavpqLPsGZ3uY6o&placeid=";*/
    String changePassword_url = URL + "user/profile/change/password";
    String profile_edit_userName_url = URL + "user/profile/change/name";
    String profile_edit_mobileNo_url = URL + "user/profile/change/mobile";
    String profile_page_url = URL + "user/profile/get";
    String logout_url = URL + "user/logout";
    String send_report = URL + "report/send";
   public static String getcontactus  = "http://wsalna.com/roadstar/v7/api/user/contactus";

    String Makepayment_url = URL + "payment/auto";
    String emergencycontact_add_url = URL + "user/emergency/contact/update";
    String emergencycontact_view_url = URL + "user/emergency/contact/view";
    String emergencycontact_delete_url = URL + "user/emergency/contact/remove";
    String emergencycontact_send_message_url = URL + "user/emergency/contact/alert";

    String invite_earn_friends_url = URL + "invites/get";

    String phoneMasking = URL + "masking/call";
    String phoneMasking_sms = URL + "masking/sms";
    String ratecard_select_city_url = URL + "locations";
    String ratecard_select_cartype_url = URL + "locations/category";
    String ratecard_display_url = URL + "ratecard";
    String change_Language = URL + "language/update";

    String cabily_money_url = URL + "wallet/get";
    String cabily_add_money_url = BaseUrl + "webview/wallet/auto";
    String cabily_add_money_url_addamount = URL + "user/bank";
    String cabily_recepit_url_addamount = URL + "user/banklist";
    String cabily_recepit_url_addamount_image_pre_url = Main_URL + "images/bank/";
    String cabily_money_webview_url = BaseUrl + "webview/wallet/form?user_id=";
    String cabily_money_transaction_url = URL + "wallet/trans";


    public static String GETCATEGORY  = URL+"user/get_category_parcel";
    public static String GETSUBCATEGORY  = URL+"user/get_subcategory_parcel";
    public static String UPDATE_PROFILE  = URL+"user/profile/update_profile";


    String myrides_url = URL + "trip/list/user";
    String myride_details_inVoiceEmail_url = URL + "invoice/send";
    String myride_details_url = URL + "trip/view/user";
    String myride_details_track_your_ride_url = URL + "booking/track/user";
    String cancel_myride_reason_url = URL + "booking/cancel/reason";
    String cancel_myride_url = URL + "booking/cancel";
    String paymentList_url = URL + "payment/list";
    String getfareBreakUpURL = URL + "fare/breakup";

    String makepayment_cash_url = URL + "payment/cash";
    String makepayment_wallet_url = URL + "payment/wallet";
    String makepayment_autoDetect_url = URL + "payment/auto";
    String makepayment_Get_webview_mobileId_url = URL + "payment/gateway";
    String makepayment_webview_url = URL + "payment/proceed?mobileId=";

    String myride_rating_url = URL + "reviews/options";
    String myride_rating_submit_url = URL + "reviews/submit";

    String favoritelist_display_url = URL + "favourite/location/display";
    String favoritelist_add_url = URL + "favourite/location/add";
    String favoritelist_edit_url = URL + "favourite/location/edit";
    String favoritelist_delete_url = URL + "favourite/location/remove";

    String tip_add_url = URL + "tips/apply";
    String tip_remove_url = URL + "tips/remove";

    String share_trip_url = URL + "trip/share";

    String app_info_url = URL + "get-app-info";


    String privacy_policy_url = Main_URL + "pages/privacy-and-policy";


    String app_facebook_post_url = URL + "get-app-info";
    String Edit_profile_image_url = URL + "user/profile/change/image";

    String updateAppStatus_url = URL + "notification/status";


    //----------------------UserAgent---------------------
    String cabily_userAgent = "cabily2k15android";
    String cabily_IsApplication = "1";
    String cabily_AppLanguage = "en";
    String cabily_AppType = "android";
    String cabily_AppToken = "";


    //-----------------PushNotification Key--------------------

    String PushNotification_AcceptRide_Key = "ride_confirmed";
    String PushNotification_AcceptRideLater_Key = "ride_later_confirmed";
    String PushNotification_CabArrived_Key = "cab_arrived";
    String PushNotification_RideCancelled_Key = "ride_cancelled";
    String PushNotification_RideCompleted_Key = "ride_completed";
    String PushNotification_RequestPayment_Key = "requesting_payment";
    String PushNotification_RequestPayment_makepayment_Stripe_Key = "make_payment";
    String PushNotification_PaymentPaid_Key = "payment_paid";
    String pushNotificationBeginTrip = "trip_begin";
    String pushNotificationDriverLoc = "driver_loc";


    /*Ride Accept Key*/
    String DriverID = "key1";
    String DriverName = "key2";
    String DriverEmail = "key3";
    String DriverImage = "key4";
    String DriverRating = "key5";
    String DriverLat = "key6";
    String DriverLong = "key7";
    String DriverTime = "key8";
    String RideID = "key9";
    String DriverMobile = "key10";
    String DriverCar_No = "key11";
    String DriverCar_Model = "key12";
    String UserLat = "key14";
    String UserLong = "key15";
    String Push_Message = "message";
    String Push_Action = "action";
    String latitude = "latitude";
    String longitude = "longitude";
    String bearing = "bearing";
    String ride_id = "ride_id";
    String isContinousRide = "isContinousRide";
    /*Ride Arrived Key*/
    String UserID_Arrived = "key1";
    String RideID_Arrived = "key2";
    String Push_Message_Arrived = "message";
    String Push_Action_Arrived = "action";

    /*Ride Cancelled Key*/
    String RideID_Cancelled = "key1";
    String RideID_Later = "key9";
    String Push_Message_Cancelled = "message";
    String Push_Action_Cancelled = "action";

    /*Ride Completed Key*/
    String Push_Message_Completed = "message";
    String Push_Action_Completed = "action";

    /*Request Payment Key*/
    String Push_Message_Request_Payment = "message";
    String Push_Action_Request_Payment = "action";
    String CurrencyCode_Request_Payment = "key1";
    String TotalAmount_Request_Payment = "key2";
    String TravelDistance_Request_Payment = "key3";
    String Duration_Request_Payment = "key4";
    String WaitingTime_Request_Payment = "key5";
    String RideID_Request_Payment = "key6";
    String UserID_Request_Payment = "key7";
    String TipStatus_Request_Payment = "key8";
    String TipAmount_Request_Payment = "key9";
    String DriverName_Request_Payment = "key10";
    String DriverImage_Request_Payment = "key11";
    String DriverRating_Request_Payment = "key12";
    String Driver_Latitude_Request_Payment = "key13";
    String Driver_Longitude_Request_Payment = "key14";
    String UserName_Request_Payment = "key15";
    String User_Latitude_Request_Payment = "key16";
    String User_Longitude_Request_Payment = "key17";
    String subTotal_Request_Payment = "key18";
    String coupon_Request_Payment = "key19";
    String serviceTax_Request_Payment = "key20";
    String Total_Request_Payment = "key21";

    String Make_Payment = "key1";
    String drop_lat = "key3";
    String drop_lan = "key4";


    /*Payment Paid Key*/
    String Push_Message_Payment_paid = "message";
    String Push_Action_Payment_paid = "action";
    String RideID_Payment_paid = "key1";
    String UserID_Payment_paid = "key2";


    /*Ads Key*/
    String pushNotification_Ads = "ads";
    String Ads_title = "key1";
    String Ads_Message = "key2";
    String Ads_image = "key3";

    /* TrackingPage Reload*/
    String pushNotification_ReloadTrackingPage_Key = "track_reload";
    String ReloadTrackingPage_RideId = "key1";


}
