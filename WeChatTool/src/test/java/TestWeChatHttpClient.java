
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import com.cs.constant.URL;
import com.cs.wechat.httpclient.WeChatHttpClinet;

public class TestWeChatHttpClient {

	@Test
	public void test() {
		WeChatHttpClinet client = new WeChatHttpClinet();

		// 1527043976062
		// 1527044372748
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		boolean redirect = false;
		Map<String, String> headerMap = new HashMap<String, String>();
		HttpEntity entry = client.doGet(URL.GET_UUID_URL, params, redirect, headerMap);
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
	}

}
