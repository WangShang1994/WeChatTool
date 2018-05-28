
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.cs.constant.URL;
import com.cs.wechat.httpclient.WeChatHttpClinet;
import com.sun.xml.internal.stream.Entity;

public class TestWeChatHttpClient {

	@Test
	public void test() throws Exception {
		WeChatHttpClinet client = new WeChatHttpClinet();

		// 1527043976062
		// 1527044372748

		// appid:wxeb7ec651dd0aefa9 fun: new lang: zh_CN _:systemtime
		// https://login.weixin.qq.com/jslogin
		 List<BasicNameValuePair> params = new
		 ArrayList<BasicNameValuePair>();
		
		 boolean redirect = false;
		 Map<String, String> headerMap = new HashMap<String, String>();
		 HttpEntity entry = client.doGet("https://login.wx.qq.com/jslogin?appid=wx782c26e4c19acffb&redirect_uri=https%3A%2F%2Fwx.qq.com%2Fcgi-bin%2Fmmwebwx-bin%2Fwebwxnewloginpage&fun=new&lang=en_&_=1527476222479", params, redirect,
		 headerMap);
		 try {
		 InputStream is = entry.getContent();
		 InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		 BufferedReader br = new BufferedReader(isr);
		 String tempLine = "";
		 while ((tempLine = br.readLine()) != null) {
		 System.out.println(tempLine);
		 }
		 } catch (UnsupportedOperationException e) {
		 e.printStackTrace();
		 } catch (IOException e) {
		 e.printStackTrace();
		 }

//		String url = "https://login.weixin.qq.com/jslogin";
//		Map<String, String> paras = new HashMap<>();
//		paras.put("appid", "wx782c26e4c19acffb");
//		paras.put("fun", "new");
//		paras.put("lang", "zh_CN");
//		paras.put("_", String.valueOf(System.currentTimeMillis()));
//		HttpEntity entry = client.doPost(url, JSON.toJSONString(paras));
//		System.out.println(EntityUtils.toString(entry));
	}

}
