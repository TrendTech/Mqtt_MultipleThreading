package mqtt;

import com.trend.core.util.ConvertUtil;
import com.trend.core.util.PubMsg;
import com.trend.core.util.ThreadUtil;
import com.trend.web.common.Config;
import com.trend.web.common.Constants;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;


/**
 * 测试系统模块
 *
 */
public class SysTest {
	private static final Logger logger = Logger.getLogger(SysTest.class);
	public static String session = "";
	public static int uid = 0;
	
	public static void main(String[] args) throws MqttException, InterruptedException {
       test1000();
	}

	public static void test1000() {
		InitReqVO req = new InitReqVO();
		req.setCmd(1000);
		req.setTempUid("ttttttttttt1");
		req.setPlatform(Config.PLATFORM);
		req.setVersion(Config.VERSION);
        PubMsg.publish("testst", Constants.ReqTopic, ConvertUtil.voToBytes(req));
	}
	
	public static void patchTest1000() {
		int size = 10;
		Config.INIT_START_TIME = System.currentTimeMillis();
		for(int i=0;i<size;i++) {
			ThreadUtil.submit(new Runnable() {
                @Override
                public void run() {
                	InitReqVO req = new InitReqVO();
            		req.setCmd(1000);
            		req.setTempUid("ttttttttttt1");
            		req.setPlatform(Config.PLATFORM);
            		req.setVersion(Config.VERSION);
                    PubMsg.publish("testst", Constants.ReqTopic, ConvertUtil.voToBytes(req));
                }
            });
		}
	}

}
