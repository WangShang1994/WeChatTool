package com.cs.constant;

public class URL {

	public static final String GET_UUID_URL = "https://login.wx.qq.com/jslogin?appid=wx782c26e4c19acffb&redirect_uri=https%3A%2F%2Flogin.weixin.qq.com%2Fcgi-bin%2Fmmwebwx-bin%2Fwebwxnewloginpage&fun=new&lang=en_&_="
			+ System.currentTimeMillis();
	public static final String GET_QRCODE_URL = "https://login.weixin.qq.com/qrcode/";

	public static final String GET_USER_DATA_URL = "https://login.wx.qq.com/cgi-bin/mmwebwx-bin/login";

	public static final String GET_CONTACT_URL = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxbatchgetcontact";

	public static final String GET_CONTACT_URL1 = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact?";

	public static final String INIT_URL = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit?";

	public static final String STATUS_NOTIFY_URL = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxstatusnotify";// ?lang=zh_CN&pass_ticket=xxx

	public static final String STATUS_NOTIFY_URL1 = "https://login.weixin.qq.com/webwxstatusnotify";// ?lang=zh_CN&pass_ticket=xxx

	public static final String CHECK_SYCN_URL = "https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin/synccheck";
	public static final String CHECK_SYCN_URL1 = "https://webpush.web2.wechat.com/cgi-bin/mmwebwx-bin/synccheck";

	public static final String SYCN_MSG_URL = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsync";// ?sid=xxx&skey=xxx&pass_ticket=xxx

}
