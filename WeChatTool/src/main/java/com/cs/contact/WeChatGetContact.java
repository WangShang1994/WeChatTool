package com.cs.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cs.constant.URL;
import com.cs.login.LoginController;
import com.cs.login.LoginInfo;
import com.cs.wechat.httpclient.WeChatHttpClinet;

public class WeChatGetContact {

	public static ContactRepository repository = new ContactRepository();

	@SuppressWarnings("unchecked")
	public WeChatGetContact() {
		try {
			if ("T".equals(LoginController.loginInfo.get(LoginInfo.IS_LOGIN))) {
				WeChatHttpClinet httpClient = new WeChatHttpClinet();
				HttpEntity entry = httpClient.doPost(buildUrl(), buildParas());
				String resp = EntityUtils.toString(entry);
				JSONObject jsonObj = new JSONObject();
				jsonObj = JSON.parseObject(resp);
				Integer friendsCount = jsonObj.getInteger("MemberCount");
				System.out.println("FriendCount:" + friendsCount);
				List<Object> friends = new ArrayList<>();
				friends = jsonObj.getObject("MemberList", List.class);
				for (Object o : friends) {
					JSONObject singleUserInfo = new JSONObject();
					singleUserInfo = JSON.parseObject(o.toString());
					ContactUserInfo cui = new ContactUserInfo();
					cui.setCity(singleUserInfo.getString("City"));
					cui.setContactFlag(singleUserInfo.getIntValue("ContactFlag"));
					cui.setHeadImgUrl(singleUserInfo.getString("HeadImgUrl"));
					cui.setHideInputBarFlag(singleUserInfo.getIntValue("HideInputBarFlag"));
					cui.setNickName(singleUserInfo.getString("NickName"));
					cui.setProvince(singleUserInfo.getString("Province"));
					cui.setpYInitial(singleUserInfo.getString("PYInitial"));
					cui.setpYQuanPin(singleUserInfo.getString("PYQuanPin"));
					cui.setRemarkName(singleUserInfo.getString("RemarkName"));
					cui.setRemarkPYInitial(singleUserInfo.getString("RemarkPYInitial"));
					cui.setRemarkPYQuanPin(singleUserInfo.getString("RemarkPYQuanPin"));
					cui.setSex(singleUserInfo.getIntValue("Sex"));
					cui.setSignature(singleUserInfo.getString("Signature"));
					cui.setUserName(singleUserInfo.getString("UserName"));
					repository.addContactUser(cui);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String buildUrl() {
		String passTicket = LoginController.loginInfo.get(LoginInfo.PASS_TICKET);
		StringBuilder url = new StringBuilder(URL.GET_CONTACT_URL1);
		url.append("pass_ticket=").append(passTicket);
		url.append("&skey=").append(LoginController.loginInfo.get(LoginInfo.S_KEY));
		url.append("&r=").append(String.valueOf(System.currentTimeMillis()));
		System.out.println("GETCONTACT:" + url.toString());
		return url.toString();
	}

	private String buildParas() {
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("Skey", LoginController.loginInfo.get(LoginInfo.S_KEY));
		paraMap.put("DeviceID", LoginController.loginInfo.get(LoginInfo.PASS_TICKET));
		paraMap.put("Uin", LoginController.loginInfo.get(LoginInfo.WSX_UIN));
		paraMap.put("Sid", LoginController.loginInfo.get(LoginInfo.WX_SID));
		Map<String, Map<String, String>> baseReqMap = new HashMap<>();
		baseReqMap.put("BaseRequest", paraMap);
		String json = JSON.toJSONString(baseReqMap);
		System.out.println(json);
		return json;
	}

}
