package com.cs.robot;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cs.wechat.httpclient.WeChatHttpClinet;

public class ChatRobot {

	private static final String TULING_API = "http://openapi.tuling123.com/openapi/api/v2";

	public String talk(String words) throws Exception {
		WeChatHttpClinet httpClient = new WeChatHttpClinet();
		HttpEntity entity = httpClient.doPost(TULING_API, buildParas(words));
		JSONObject respJson = JSON.parseObject(EntityUtils.toString(entity, "UTF-8"));
		JSONArray resultJson = respJson.getJSONArray("results");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < resultJson.size(); i++) {
			sb.append(resultJson.getJSONObject(i).getJSONObject("values").getString("text"));
		}
		return sb.toString();
	}

	private String buildParas(String words) {
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("reqType", "0");
		Map<String, Object> perception = new HashMap<>();
		Map<String, Object> text = new HashMap<>();
		text.put("text", words);
		perception.put("inputText", text);
		baseMap.put("perception", perception);
		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("apiKey", "ce55fe2a22c64874a807f1fda24577a0");
		userInfo.put("userId", "270527");
		baseMap.put("userInfo", userInfo);
		return JSON.toJSONString(baseMap);
	}

}
