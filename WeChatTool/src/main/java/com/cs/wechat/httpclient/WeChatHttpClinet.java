package com.cs.wechat.httpclient;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class WeChatHttpClinet {

	public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";

	static CloseableHttpClient httpClient = HttpClients.createDefault();

	private static CookieStore cookieStore;

	static {
		cookieStore = new BasicCookieStore();

		httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
	}

	public HttpEntity doGet(String url, List<BasicNameValuePair> params, boolean redirect,
			Map<String, String> headerMap) {
		HttpGet get = new HttpGet();
		try {
			if (params != null && !params.isEmpty()) {
				String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
				get = new HttpGet(url + "?" + paramStr);
			} else {
				get = new HttpGet(url);
			}
			if (!redirect) {
				get.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());
			}
			get.setHeader("User-Agent", USER_AGENT);
			if (headerMap != null && !headerMap.isEmpty()) {
				for (Entry<String, String> e : headerMap.entrySet()) {
					get.setHeader(e.getKey(), e.getValue());
				}
			}
			System.out.println("GETURL:" + get.getURI());
			return httpClient.execute(get).getEntity();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public HttpEntity doPost(String url, String paras) {
		HttpPost post = new HttpPost();
		if (url != null && !url.isEmpty()) {
			post = new HttpPost(url);
		}
		StringEntity strEntity = new StringEntity(paras, Consts.UTF_8);
		post.setEntity(strEntity);
		post.setHeader("Content-type", "application/json; charset=utf-8");
		post.setHeader("User-Agent", USER_AGENT);
		System.out.println("POSTURL:" + url);
		System.out.println("POSTPARA:" + strEntity.toString());
		try {
			return httpClient.execute(post).getEntity();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean storeHttpEnteyToFile(HttpEntity entry, String path) {
		File f = new File(path);
		OutputStream os = null;
		InputStream is = null;
		try {
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			is = entry.getContent();
			os = new FileOutputStream(f);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
				File file = new File(path);
				Desktop.getDesktop().open(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

}
