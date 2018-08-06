package com.example.util;

import java.io.Serializable;

public class Constant implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static String SERVER_URL = "http://app.liveplanet_tv.com/";

	public static  String IMAGE_PATH=SERVER_URL + "images/";

	public static  String CATEGORY_URL = SERVER_URL + "api.php";

	public static  String LATEST_URL = SERVER_URL + "api.php?latest";

	public static  String CATEGORY_ITEM_URL = SERVER_URL + "api.php?cat_id=";

	public static  String REGISTER_URL = SERVER_URL + "user_register_api.php?name=";

	public static  String LOGIN_URL = SERVER_URL + "user_login_api.php?email=";

	public static  String FORGOT_PASSWORD_URL = SERVER_URL + "user_forgot_pass_api.php?email=";

	public static  String ABOUT_URL = SERVER_URL + "api.php?app_details";

	public static  String SEARCH_URL = SERVER_URL + "api.php?search=";

	public static  String USER_PROFILE_URL = SERVER_URL + "user_profile_api.php?id=";

	public static  String USER_PROFILE_UPDATE_URL = SERVER_URL + "user_profile_update_api.php?name=";

	public static  String SINGLE_CHANNEL_URL = SERVER_URL + "api.php?channel_id=";
	public static  String VIDEO_TOKEN_URL ="http://app.liveplanet_tv.com/token.php";

	public static  String REPORT_CHANNEL_URL = SERVER_URL + "api.php?name=";

	public static  String HOME_URL = SERVER_URL + "api.php?home";

	public static  String FEATURED_URL = SERVER_URL + "api_featured.php";

	public static final String ARRAY_NAME="LIVETV";

	public static final String CATEGORY_NAME="category_name";
	public static final String CATEGORY_CID="cid";
	public static final String CATEGORY_IMAGE="category_image";

	public static final String CHANNEL_ID="id";
	public static final String CHANNEL_TITLE="channel_title";
	public static final String CHANNEL_URL="channel_url";
	public static final String CHANNEL_IMAGE="channel_thumbnail";
	public static final String CHANNEL_DESC="channel_desc";
	public static final String CHANNEL_TYPE="channel_type";


	public static final String APP_NAME="app_name";
	public static final String APP_IMAGE="app_logo";
	public static final String APP_VERSION="app_version";
	public static final String APP_AUTHOR="app_author";
	public static final String APP_CONTACT="app_contact";
	public static final String APP_EMAIL="app_email";
	public static final String APP_WEBSITE="app_website";
	public static final String APP_DESC="app_description";
	public static final String APP_PRIVACY_POLICY="app_privacy_policy";
	public static final String keyIntertitialAdDelayTime="delayTime";

	public static final String USER_NAME="name";
	public static final String USER_ID="user_id";
	public static final String USER_EMAIL="email";
	public static final String USER_PHONE="phone";

	public static final String RELATED_ITEM_ARRAY_NAME="related";
	public static final String RELATED_ITEM_CHANNEL_ID="rel_id";
	public static final String RELATED_ITEM_CHANNEL_NAME="rel_channel_title";
	public static final String RELATED_ITEM_CHANNEL_THUMB="rel_channel_thumbnail";

	public static final String HOME_LATEST_ARRAY="latest_channels";
	public static final String HOME_FEATURED_ARRAY="featured_channels";

	public static final String SLIDER_ARRAY="slider_list";
	public static final String SLIDER_NAME="home_title";
	public static final String SLIDER_IMAGE="home_banner";
	public static final String SLIDER_LINK="home_url";
	public static final String TOKEN_PARAMETER="&wmsAuthSign=";
	public static final String DEVICE_ID_PARAMETER="&device_id=";

	public static int GET_SUCCESS_MSG;
	public static final String MSG="msg";
	public static final String SUCCESS="success";
	public static int AD_COUNT=0;
	public static int AD_COUNT_SHOW=2;
	public static boolean isFirstTime=true;
	public static boolean isFirstTime2=true;
	public static final String keyUserToken="userToken";
	public static final String keySelectedPlayer="selected_player";
	public static final String keyAdmobPriority="admob";
	public static final String keyStartAppPriority="startapp";
	public static final String keyFlurryPriority="flurry";
	public static final String keyAppLovinPriority="applovin";
	public static final String keyBaseUrl="baseurl";
	public static final String keyDeviceId="deviceId";
	public static final String keyMediaPlayerTokenUrl="mediaPlayerApi";
   public static void refreshConstantsData()
   {

	   IMAGE_PATH=SERVER_URL + "images/";

	   CATEGORY_URL = SERVER_URL + "api.php";

	   LATEST_URL = SERVER_URL + "api.php?latest";

	   CATEGORY_ITEM_URL = SERVER_URL + "api.php?cat_id=";

	  REGISTER_URL = SERVER_URL + "user_register_api.php?name=";

	  LOGIN_URL = SERVER_URL + "user_login_api.php?email=";

	   FORGOT_PASSWORD_URL = SERVER_URL + "user_forgot_pass_api.php?email=";

	    ABOUT_URL = SERVER_URL + "api.php?app_details";

	    SEARCH_URL = SERVER_URL + "api.php?search=";

	    USER_PROFILE_URL = SERVER_URL + "user_profile_api.php?id=";

	    USER_PROFILE_UPDATE_URL = SERVER_URL + "user_profile_update_api.php?name=";

	    SINGLE_CHANNEL_URL = SERVER_URL + "api.php?channel_id=";

	    REPORT_CHANNEL_URL = SERVER_URL + "api.php?name=";

	    HOME_URL = SERVER_URL + "api.php?home";

	    FEATURED_URL = SERVER_URL + "api_featured.php";



   }

}
