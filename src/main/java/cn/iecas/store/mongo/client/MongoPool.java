package cn.iecas.store.mongo.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import cn.iecas.general.utils.prop.ResourceLoad;

/**
 * MongoDB连接池
 * 
 * @author hhm
 * 
 */
public class MongoPool {
	private static final Logger logger = LoggerFactory.getLogger(MongoPool.class);

	private static MongoClient mongoClient = null;

	// 配置文件中获取数据库名
	private static String dbName;
	// mongodb连接uri
	private static String mongodbURI;

	/**
	 * 获取mongo连接实例
	 * 
	 * @return MongoClient
	 */
	public static synchronized MongoClient getMongoClient() {

		if (mongoClient == null) {
			loadConfig();
			if (mongodbURI == null || mongodbURI.equals("")) {
				logger.error("MongoDB Connection URI is Null. Please set in 'mongodb_uri' property in 'mongo.properties' file.");
				throw new IllegalArgumentException("MongoDB Connection URI is Null. Please set in 'mongodb_uri' property in 'mongo.properties' file.");
			}

			// 创建mongo连接实例
			MongoClientURI uri = new MongoClientURI(mongodbURI);
			if(dbName==null){
				dbName = uri.getDatabase();
			}
			mongoClient = new MongoClient(uri);
			logger.info("Initialize MongoDB Connection Pool Finished.");
		}

		return mongoClient;
	}
	
	public static MongoDatabase getMongoDatabase(){
		return getMongoClient().getDatabase(dbName);
	}

	/**
	 * 根据表名获取集合实例
	 * 
	 * @param tablename
	 * @return DBCollection
	 */
	public static DBCollection getDBCollection(String tablename) {
		@SuppressWarnings("deprecation")
		DB db = getMongoClient().getDB(dbName);
		return db.getCollection(tablename);
	}

	/**
	 * 根据表名获取集合实例(new interface)
	 * 
	 * @param collection
	 * @return
	 */
	public static MongoCollection<Document> getMongoCollection(String collection) {
		return getMongoClient().getDatabase(dbName).getCollection(collection);
	}

	/**
	 * 加载配置文件 1.如果指定配置文件位置，优先加载 启动参数为-Dmongo.config.path=/xxx/mongo.properties
	 * 
	 */
	private static void loadConfig() {
		
		String errMsg = "Not Found MongoDB Config. Please set 'mongo.properties' file path param.  eg: -Dmongo.config.path=/xxx/mongo.properties";
		
		Properties prop = new Properties();
		InputStream propFile = null;
		String defFile = "mongo.properties";
		
		String path = System.getProperty("mongo.config.path");
		
		if(path!=null){
			
			if(Files.exists(Paths.get(path))){
				try {
					propFile = new FileInputStream(path);
					logger.info("Load MongoDB Config From Path : mongo.config.path={}", path);
				} catch (FileNotFoundException e) {
					logger.error(errMsg);
					throw new RuntimeException(errMsg);
				}
			}else{
				errMsg = "File Not Found Exception. Check : mongo.config.path="+path;
				logger.error(errMsg);
				throw new RuntimeException(errMsg);
			}
		}else{
			propFile = ResourceLoad.searchResourceFile(defFile);
		}

		if (propFile!=null) {
			try {
				prop.load(propFile);
				logger.info("Load MongoDB Config Info : {}", prop.toString());
			} catch (IOException e) {
				logger.error(errMsg);
				throw new RuntimeException(errMsg);
			}
		}else{
			logger.error(errMsg);
			throw new RuntimeException(errMsg);
		}

		dbName = prop.getProperty("mongodb_database",null);
		mongodbURI = prop.getProperty("mongodb_uri",null);

	}
	
	public static void main(String[] args) {
		MongoPool.getDBCollection("hm_point_group_info");
	}

}
