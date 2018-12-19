package cn.iecas.store.kafka.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.iecas.general.utils.prop.ResourceLoad;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * kafka消费者
 * @author hhm
 *
 */
public class KafkaConsumer {
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
	
	private ExecutorService threadPool;
	
	private Properties props = null;
	
	private String groupid = null;
	private String topicName = null;
	private int partitionNum = 1;
	private MessageDealCallback messageDealCallback = null;
	
	private ConsumerConfig conf = null;
	private ConsumerConnector connector = null;
	
	public KafkaConsumer(String groupid, String topicName, int partitionNum, MessageDealCallback messageDealCallback){
		this.groupid = groupid;
		this.topicName = topicName;
		this.partitionNum = partitionNum;
		this.messageDealCallback = messageDealCallback;
		
		loadProperties();
		props.setProperty("group.id", groupid);
		conf = new ConsumerConfig(props);
	}
	public KafkaConsumer(String groupid, String topicName, MessageDealCallback messageDealCallback){
		this(groupid,topicName,1,messageDealCallback);
	}
	
	private void loadProperties() {

		if (props != null) {
			return;
		}

		String errMsg = "Not Found Kafka Config. Please set 'kafka.properties' file path param.  eg: -Dkafka.config.path=/xxx/kafka.properties";

		InputStream propFile = null;
		String defFile = "kafka.properties";
		//String defFile = "kafka-path.properties";
		String path = System.getProperty("kafka.config.path");

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
	
	private void initConsumer(){
		threadPool = Executors.newFixedThreadPool(partitionNum);
		connector = Consumer.createJavaConsumerConnector(conf);
	}
	public void start(){
		initConsumer();
		logger.info("Kafka Consumer Start. Group.Id:{}, Topic:{}, Partition.Count:{}",groupid,topicName,partitionNum);
		
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topicName, partitionNum);
		
		Map<String,List<KafkaStream<byte[],byte[]>>> topicMessageStreams = connector.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[],byte[]>> streams = topicMessageStreams.get(topicName);
		
		for (final KafkaStream<byte[], byte[]> kafkaStream : streams) {
			threadPool.submit(messageDealCallback.getMessageDealThread(kafkaStream));
		}
	}
	
	public void close(){
		connector.shutdown();
		threadPool.shutdown();
	}

	public static void main(String[] args) {
		KafkaConsumer kc = new KafkaConsumer("1", "wlj9", new TestMessageDealCallback());
		kc.start();
	}
}
