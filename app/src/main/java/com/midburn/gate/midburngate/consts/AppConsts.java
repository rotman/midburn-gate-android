package com.midburn.gate.midburngate.consts;

public class AppConsts {

	public static final String TAG = "MIDBURN_GATE";

	public static final String SERVER_URL = "spark.midburn.org";

	public static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
	public static final int    RESPONSE_OK = 200;

	//audio
	public static final int OK_MUSIC    = 1;
	public static final int ERROR_MUSIC = 2;

	//errors
	public static final String QUOTA_REACHED_ERROR         = "QUOTA_REACHED";
	public static final String USER_OUTSIDE_EVENT_ERROR    = "USER_OUTSIDE_EVENT";
	public static final String GATE_CODE_MISSING_ERROR     = "GATE_CODE_MISSING";
	public static final String BAD_SEARCH_PARAMETERS_ERROR = "BAD_SEARCH_PARAMETERS";
	public static final String TICKET_NOT_FOUND_ERROR      = "TICKET_NOT_FOUND";
	public static final String ALREADY_INSIDE_ERROR        = "ALREADY_INSIDE";
	public static final String TICKET_NOT_IN_GROUP_ERROR   = "TICKET_NOT_IN_GROUP";
	public static final String INTERNAL_ERROR              = "Internal error: Cannot read property 'attributes' of null";

	//group types
	public static final String GROUP_TYPE_PRODUCTION = "PRODUCTION";
	public static final String GROUP_TYPE_ART        = "ART";
	public static final String GROUP_TYPE_CAMP       = "CAMP";
}
