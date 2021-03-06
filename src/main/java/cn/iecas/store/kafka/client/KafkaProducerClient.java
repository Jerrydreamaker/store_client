package cn.iecas.store.kafka.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.iecas.general.utils.ConvertUtils;
import cn.iecas.general.utils.prop.ResourceLoad;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaProducerClient {
	private static final Logger logger = LoggerFactory.getLogger(KafkaProducerClient.class);

	private static Properties props = null;

	private static Producer<String, String> stringProducer = null;
	private static Producer<String, byte[]> byteProducer = null;
	
	private static synchronized void loadProperties() {

		if (props != null) {
			return;
		}

		String errMsg = "Not Found Kafka Config. Please set 'kafka.properties' file path param.  eg: -Dkafka.config.path=/xxx/kafka.properties";

		InputStream propFile = null;
		String defFile = "kafka.properties";

		
		String path = System.getProperty("kafka.config.path");
		//System.out.println(path);

		if (path != null) {

			if (Files.exists(Paths.get(path))) {
				try {
					propFile = new FileInputStream(path);
					logger.info("Load Kafka Config From Path : kafka.config.path={}", path);
				} catch (FileNotFoundException e) {
					logger.error(errMsg);
					throw new RuntimeException(errMsg);
				}
			} else {
				errMsg = "File Not Found Exception. Check : kafka.config.path=" + path;
				logger.error(errMsg);
				throw new RuntimeException(errMsg);
			}
		} else {
			propFile = ResourceLoad.searchResourceFile(defFile);
		}

		if (propFile != null) {
			try {
				props = new Properties();
				props.load(propFile);
				logger.info("Load Kafka Config Info : {}", props.toString());
			} catch (IOException e) {
				logger.error(errMsg);
				throw new RuntimeException(errMsg);
			}
		} else {
			logger.error(errMsg);
			throw new RuntimeException(errMsg);
		}

	}

	private static synchronized void initStringProducer() {

		if (stringProducer != null) {
			return;
		}
		loadProperties();
		// 设置配置属性
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		// key.serializer.class默认为serializer.class
		props.put("key.serializer.class", "kafka.serializer.StringEncoder");
		// 触发acknowledgement机制，否则是fire and forget，可能会引起数据丢失
		// 值为0,1,-1,可以参考
		// http://kafka.apache.org/08/configuration.html
		// props.put("request.required.acks",
		// PropertiesUtils.application.getProperty("request.required.acks"));
		ProducerConfig config = new ProducerConfig(props);

		// 创建producer
		stringProducer = new Producer<String, String>(config);
	}

	private static synchronized void initByteProducer() {
		if (byteProducer != null) {
			return;
		}
		loadProperties();
		// 设置配置属性
		props.put("serializer.class", "kafka.serializer.DefaultEncoder");
		// key.serializer.class默认为serializer.class
		props.put("key.serializer.class", "kafka.serializer.StringEncoder");
		// 触发acknowledgement机制，否则是fire and forget，可能会引起数据丢失
		// 值为0,1,-1,可以参考
		// http://kafka.apache.org/08/configuration.html
		// props.put("request.required.acks",
		// PropertiesUtils.application.getProperty("request.required.acks"));
		ProducerConfig config = new ProducerConfig(props);
		
		// 创建producer
		byteProducer = new Producer<String, byte[]>(config);
	}

	private static Producer<String, String> getStringProducer() {
		if (stringProducer == null) {
			initStringProducer();
		}
		return stringProducer;
	}
	private static Producer<String, byte[]> getByteProducer() {
		if (byteProducer == null) {
			initByteProducer();
		}
		return byteProducer;
	}

	/**
	 * 释放生产者资源
	 */
	public static synchronized void closeProducer() {
		if (stringProducer != null) {
			stringProducer.close();
			stringProducer = null;
		}
		if (byteProducer != null) {
			byteProducer.close();
			byteProducer = null;
		}
	}

	/**
	 * 向kafka消息队列指定topic发送一条消息
	 * 
	 * @param topicName
	 *            topic名称
	 * @param message
	 *            消息
	 */
	public static void sendString(String topicName, String message) {
		KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicName, message);
		getStringProducer().send(data);// 发送消息
	}
	
	public static void sendObject(String topicName, Object message){
		byte[] b = toBytes(message);
		if(b==null) return;
		sendBytes(topicName,b);
	}
	public static void sendBytes(String topicName, byte[] message){
		KeyedMessage<String, byte[]> data = new KeyedMessage<String, byte[]>(topicName, message);
		getByteProducer().send(data);
	}
	public static void sendString(String topicName, Collection<String> messages){
		List<KeyedMessage<String, String>> data = new ArrayList<KeyedMessage<String, String>>();
		
		for (String msg : messages) {
			data.add(new KeyedMessage<String, String>(topicName, msg));
		}
		
		getStringProducer().send(data);
	}
	public static void sendObject(String topicName, Collection<Object> messages){
		List<KeyedMessage<String, byte[]>> data = new ArrayList<KeyedMessage<String, byte[]>>();
		
		for (Object msg : messages) {
			byte[] b = toBytes(msg);
			if(b==null) continue;
			data.add(new KeyedMessage<String, byte[]>(topicName, b));
		}
		
		getByteProducer().send(data);
	}
	public static void sendBytes(String topicName, Collection<byte[]> messages){
		List<KeyedMessage<String, byte[]>> data = new ArrayList<KeyedMessage<String, byte[]>>();
		
		for (byte[] msg : messages) {
			data.add(new KeyedMessage<String, byte[]>(topicName, msg));
		}
		
		getByteProducer().send(data);
	}
	
	private static byte[] toBytes(Object object) {
		byte[] b = null;
		try {
			b = ConvertUtils.Byte.toBytes(object);
		} catch (IOException e) {
		}
		
		return b;
	}
	
	public static void main(String[] args) {
		while(true) {
			KafkaProducerClient.sendString("wlj9", System.currentTimeMillis()+"");
		}
		
	}

}
