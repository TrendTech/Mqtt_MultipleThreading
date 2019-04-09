package com.trend;

import com.trend.core.util.ThreadUtil;
import com.trend.web.common.Config;
import com.trend.web.common.Constants;
import com.trend.web.common.service.RespPackDealService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import mqtt.SysTest;

 
/**
 * 接口项目
 * @author TreadTec
 */
@SpringBootApplication
public class MqttTestApplication {
	
	
	@Autowired
	private RespPackDealService respPackDealService;
	
	private static final Logger logger = Logger.getLogger(MqttTestApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(MqttTestApplication.class, args);
		SysTest.test1000();
	}

	/**
	 * mqtt工厂
	 * @return
	 * @author TreadTec
	 */
	@Bean
	public MqttPahoClientFactory mqttClientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		factory.setServerURIs(Config.Broker);
		factory.setPassword(Config.MQTT_PASSWARD);
		factory.setUserName(Config.MQTT_USERNAME);
		return factory;
	}

	@Bean
	public IntegrationFlow mqttInFlow() {
		return IntegrationFlows.from(mqttInbound()).transform(p -> p).handle(handler()).get();
	}

	 
    /**
     * 响应消息处理
     * @return
     * @author TreadTec
     */
    public MessageHandler handler() {  
        return new MessageHandler() { 
        	@Override
            public void handleMessage(Message<?> message) throws MessagingException {
        		ThreadUtil.submit(new Runnable() {
                    @Override
                    public void run() {
                		try {
                			Object payload = message.getPayload();
                			byte[] packet = (byte[]) payload;
                			respPackDealService.deal(packet);
        				} catch (Throwable e) {
        					
        				}
                    }
                });
            }
        };  
    }

	/**
	 * mqtt订阅单主题
	 * @return
	 * @author TreadTec
	 */
	@Bean
	public MessageProducerSupport mqttInbound() {
		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(Constants.SubClientId+"t546t56",
				mqttClientFactory(), Constants.ResqTopic);  //订阅所有响应主题
		DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
		converter.setPayloadAsBytes(true);   //配置为返回原始byte[]类型
		adapter.setCompletionTimeout(5000);
		adapter.setConverter(converter);
		adapter.setQos(0);
		return adapter;
	}
}
