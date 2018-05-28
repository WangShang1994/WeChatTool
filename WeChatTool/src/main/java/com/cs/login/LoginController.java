package com.cs.login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cs.constant.URL;
import com.cs.contact.ContactUserInfo;
import com.cs.wechat.httpclient.MyHttpClient;
import com.cs.wechat.httpclient.WeChatHttpClinet;

public class LoginController {

	public String uuid;

	public static LoginInfo loginInfo = new LoginInfo();

	public static ContactUserInfo myselfInfo = new ContactUserInfo();

	public static Map<Integer, Object> sycnKeyMap = new HashMap<>();

	public static String BASE_URL = null;

	public LoginController() {
		try {
			uuid = getUUID();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getUUID() throws Exception {
		WeChatHttpClinet httpClient = new WeChatHttpClinet();
		HttpEntity entry = httpClient.doGet(URL.GET_UUID_URL, null, false, null);
		String resp = EntityUtils.toString(entry);
		return getUUIDFromResponse(resp);
	}

	private String getUUIDFromResponse(String resp) {
		String[] arr = resp.split(";");
		return arr[1].replaceAll(" window.QRLogin.uuid = \"", "").replaceAll("\"", "");
	}

	public boolean generateQRCode(String path) throws IOException {
		WeChatHttpClinet httpClient = new WeChatHttpClinet();
		HttpEntity entry = null;
		if (this.uuid != null && !this.uuid.isEmpty()) {
			entry = httpClient.doGet(URL.GET_QRCODE_URL + this.uuid, null, false, null);
		}
		return WeChatHttpClinet.storeHttpEnteyToFile(entry, path);
	}

	public String getTicketURL() {
		WeChatHttpClinet httpClient = new WeChatHttpClinet();
		HttpEntity entry = null;
		String url = null;
		try {
			entry = httpClient.doGet(buildGetTicketURL(), null, false, null);
			String resp = EntityUtils.toString(entry).trim();
			url = resp.substring(resp.indexOf("window.code=200;window.redirect_uri=\"") + 39, resp.length() - 2);
			System.out.println(resp);
			BASE_URL = url.substring(0, url.indexOf("webwxnewloginpage"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	public String getLoginInfo(String ticketUrl) {
		WeChatHttpClinet httpClient = new WeChatHttpClinet();
		HttpEntity entry = null;
		String resp = null;
		try {
			entry = httpClient.doGet(ticketUrl, null, false, null);
			resp = EntityUtils.toString(entry);
			System.out.println(resp);
			loginInfo.setSkey(resp.substring(resp.indexOf("<skey>") + 6, resp.indexOf("</skey>")));
			loginInfo.setSid(resp.substring(resp.indexOf("<wxsid>") + 7, resp.indexOf("</wxsid>")));
			loginInfo.setUin(resp.substring(resp.indexOf("<wxuin>") + 7, resp.indexOf("</wxuin>")));
			loginInfo.setPassTicket(resp.substring(resp.indexOf("<pass_ticket>") + 13, resp.indexOf("</pass_ticket>")));
			loginInfo.isLogin("T");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	public String initAfterLogin() {
		WeChatHttpClinet httpClient = new WeChatHttpClinet();
		HttpEntity entry = null;
		String resp = null;
		try {
			entry = httpClient.doPost(buildInitURL(), JSON.toJSONString(buildLoginInfoParas()));
			resp = EntityUtils.toString(entry);
			processInitResp(resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	private void processInitResp(String resp) throws UnsupportedEncodingException {
		JSONObject jsonResp = JSON.parseObject(resp);
		JSONObject jsonMySelf = JSON.parseObject(jsonResp.getString("User"));
		myselfInfo.setContactFlag(jsonMySelf.getIntValue("ContactFlag"));
		myselfInfo.setHeadImgUrl(jsonMySelf.getString("HeadImgUrl"));
		myselfInfo.setHideInputBarFlag(jsonMySelf.getIntValue("HideInputBarFlag"));
		myselfInfo.setNickName(jsonMySelf.getString("NickName"));
		myselfInfo.setpYInitial(jsonMySelf.getString("PYInitial"));
		myselfInfo.setRemarkName(jsonMySelf.getString("RemarkName"));
		myselfInfo.setRemarkPYInitial(jsonMySelf.getString("RemarkPYInitial"));
		myselfInfo.setRemarkPYQuanPin(jsonMySelf.getString("RemarkPYQuanPin"));
		myselfInfo.setSex(jsonMySelf.getIntValue("Sex"));
		myselfInfo.setSignature(jsonMySelf.getString("Signature"));
		myselfInfo.setUserName(jsonMySelf.getString("UserName"));
		System.out.println("Welcome " + new String(myselfInfo.getNickName().getBytes(), "UTF-8"));
		JSONObject sycnKeyJson = JSON.parseObject(jsonResp.getString("SyncKey"));
		sycnKeyMap.put(0, sycnKeyJson);
		JSONArray sycnListJsonArr = sycnKeyJson.getJSONArray("List");
		for (int i = 0; i < sycnListJsonArr.size(); i++) {
			sycnKeyMap.put(sycnListJsonArr.getJSONObject(i).getInteger("Key"),
					sycnListJsonArr.getJSONObject(i).getString("Val"));
		}
	}

	private String buildInitURL() {
		StringBuilder url = new StringBuilder(BASE_URL + "webwxinit");
		url.append("?r=").append(String.valueOf(System.currentTimeMillis() / 3158L));
		url.append("&pass_ticket=").append(loginInfo.get(LoginInfo.PASS_TICKET));
		return url.toString();
	}

	private Map<String, Object> buildLoginInfoParas() {
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("Skey", LoginController.loginInfo.get(LoginInfo.S_KEY));
		paraMap.put("DeviceID", LoginController.loginInfo.get(LoginInfo.PASS_TICKET));
		paraMap.put("Uin", LoginController.loginInfo.get(LoginInfo.WSX_UIN));
		paraMap.put("Sid", LoginController.loginInfo.get(LoginInfo.WX_SID));
		Map<String, Object> baseReqMap = new HashMap<>();
		baseReqMap.put("BaseRequest", paraMap);
		return baseReqMap;
	}

	public void statusNotify() {
		WeChatHttpClinet httpClient = new WeChatHttpClinet();
		HttpEntity entry = null;
		String resp = null;
		try {
			String strParas = buildStatusNotifyParas();
			String url = buildStatusNotifyURL();
			entry = httpClient.doPost(url, strParas);
			resp = EntityUtils.toString(entry);
			System.out.println("StatusNotifyResp:" + resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String buildStatusNotifyURL() {
		StringBuilder url = new StringBuilder(URL.STATUS_NOTIFY_URL);
		url.append("?lang=zh_CN&pass_ticket=").append(loginInfo.get(LoginInfo.PASS_TICKET));
		return url.toString();
	}

	private String buildStatusNotifyParas() {
		Map<String, Object> baseReqMap = buildLoginInfoParas();
		baseReqMap.put("Code", "3");
		baseReqMap.put("FromUserName", myselfInfo.getUserName());
		baseReqMap.put("ToUserName", myselfInfo.getUserName());
		baseReqMap.put("ClientMsgId", String.valueOf(System.currentTimeMillis()));
		return JSON.toJSONString(baseReqMap);
	}

	private List<BasicNameValuePair> buildGetTicketPara() {
		long time = System.currentTimeMillis();
		List<BasicNameValuePair> params = new ArrayList<>();
		BasicNameValuePair loginIcon = new BasicNameValuePair("loginicon", "true");
		BasicNameValuePair uuidPair = new BasicNameValuePair("loginicon", this.uuid);
		BasicNameValuePair tip = new BasicNameValuePair("tip", "0");
		BasicNameValuePair r = new BasicNameValuePair("r", String.valueOf(time / 1579L));
		BasicNameValuePair line = new BasicNameValuePair("_", String.valueOf(time));
		params.add(loginIcon);
		params.add(uuidPair);
		params.add(tip);
		params.add(r);
		params.add(line);
		return params;
	}

	private String buildGetTicketURL() {
		long time = System.currentTimeMillis();
		StringBuilder url = new StringBuilder(URL.GET_USER_DATA_URL);
		url.append("?loginicon=true&uuid=");
		url.append(this.uuid);
		url.append("&tip=0&r=");
		url.append(String.valueOf(time / 1579L));
		url.append("&_=");
		url.append(String.valueOf(time));
		System.out.println(url.toString());
		return url.toString();
	}

	private String buildGetUserDataURL() {
		long time = System.currentTimeMillis();
		StringBuilder url = new StringBuilder(URL.GET_USER_DATA_URL);
		url.append("?loginicon=true&uuid=");
		url.append(this.uuid);
		url.append("&tip=1&r=");
		url.append(String.valueOf(time / 1579L));
		url.append("&_=");
		url.append(String.valueOf(time));
		System.out.println(url.toString());
		return url.toString();
	}

}
