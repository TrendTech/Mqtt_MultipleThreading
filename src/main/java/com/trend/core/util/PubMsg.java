package com.trend.core.util;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.trend.web.common.Constants;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class PubMsg {
	private static final Logger logger = Logger.getLogger(PubMsg.class);
	
	private static int qos = 0; //只有一次
	private static String broker = Constants.Broker;
	private static MqttClient client = connect("testst");

	
	/**
	 * 链接类
	 * 
	 * @param clientId
	 * @return
	 * @throws MqttException
	 * @author
	 */
	private static MqttClient connect(String clientId){
		MemoryPersistence persistence = new MemoryPersistence();
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		connOpts.setConnectionTimeout(10);
		connOpts.setKeepAliveInterval(20);
		connOpts.setUserName(Constants.Uname);
		connOpts.setPassword(Constants.pwd.toCharArray());
		System.out.println(broker);
		System.out.println(Constants.Uname);
		System.out.println(Constants.pwd);
		try {
			MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
			mqttClient.connect(connOpts);
			return mqttClient;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发布消息
	 * 
	 * @param sampleClient
	 * @param topic
	 * @param pubBytes
	 * @throws MqttPersistenceException
	 * @throws MqttException
	 * @author
	 */
	private static void pub(MqttClient sampleClient,String topic,byte[] pubBytes) 
			throws MqttPersistenceException, MqttException {
		MqttMessage message = new MqttMessage(pubBytes);
		message.setQos(qos);
		message.setRetained(false);
		sampleClient.publish(topic, message);
	}
	
	/**
	 * 发布消息
	 */
	public static void publish(String clientId,String topic,byte[] pubBytes) {
		try {
			if (client != null) {
				long time = System.currentTimeMillis();
				byte[] timePacket = ByteUtils.writeLong(time);
				byte[] bytes = ByteUtils.combineByte(timePacket,pubBytes);
				pub(client,topic,bytes);
			}
		} catch (MqttException e) {
			logger.error("发送消息失败....",e);
		}
		
	}

	
	/**
	 * 发布消息
	 */
	public static void publish(String topic,byte[] pubBytes) {
		publish(Constants.PubClientId, topic, pubBytes);
	}
	
	
	private static SSLSocketFactory getSSLSocktet(String caPath,String crtPath, String keyPath, String password) throws Exception {
        // CA certificate is used to authenticate server
        CertificateFactory cAf = CertificateFactory.getInstance("X.509");
        FileInputStream caIn = new FileInputStream(caPath);
        X509Certificate ca = (X509Certificate) cAf.generateCertificate(caIn);
         KeyStore caKs = KeyStore.getInstance("JKS");
         caKs.load(null, null);
         caKs.setCertificateEntry("ca-certificate", ca);
         TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
         tmf.init(caKs);

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        FileInputStream crtIn = new FileInputStream(crtPath);
        X509Certificate caCert = (X509Certificate) cf.generateCertificate(crtIn);

        crtIn.close();
        // client key and certificates are sent to server so it can authenticate
        // us
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("certificate", caCert);
        ks.setKeyEntry("private-key", getPrivateKey(keyPath), password.toCharArray(),
                new java.security.cert.Certificate[]{caCert}  );
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX");
        kmf.init(ks, password.toCharArray());

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1");

        context.init(kmf.getKeyManagers(),tmf.getTrustManagers(), new SecureRandom());

        return context.getSocketFactory();
    }

    public static PrivateKey getPrivateKey(String path) throws Exception{  

        org.apache.commons.codec.binary.Base64 base64=new org.apache.commons.codec.binary.Base64();
        byte[] buffer=   base64.decode(getPem(path)); 

        PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  

    } 

     private static String getPem(String path) throws Exception{
            FileInputStream fin=new FileInputStream(path);
            BufferedReader br= new BufferedReader(new InputStreamReader(fin));  
            String readLine= null;  
            StringBuilder sb= new StringBuilder();  
            while((readLine= br.readLine())!=null){  
                if(readLine.charAt(0)=='-'){  
                    continue;  
                }else{  
                    sb.append(readLine);  
                    sb.append('\r');  
                }  
            }  
            fin.close();
            return sb.toString();
        }
	
}

 
