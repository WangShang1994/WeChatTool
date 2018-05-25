
import java.io.IOException;

import org.junit.Test;

import com.cs.contact.WeChatGetContact;
import com.cs.login.LoginController;
import com.cs.sycn.MessageListener;

public class TestLoginController {

	@Test
	public void test() throws IOException {
		LoginController lc = new LoginController();
		lc.generateQRCode("D:\\weChatTest\\Login.jpg");
		String url = lc.getTicketURL();
		lc.getLoginInfo(url);
		lc.initAfterLogin();
		lc.statusNotify();
		MessageListener listener = new MessageListener();
		listener.start();
		//WeChatGetContact a = new WeChatGetContact();
	}

}
