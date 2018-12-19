package cn.iecas.store.hbase.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.iecas.general.utils.prop.ResourceLoad;

/**
 * hbase连接池类
 * 该类全局维护一个HBaseConfiguration对象，
 * 该类全局维护一个HConnection对象，
 * 通过该对象获取HTable表实例
 * @author hhm
 *
 */
public final class HBasePool {
	
	private static final Logger logger = LoggerFactory.getLogger(HBasePool.class);
	
	private static Configuration conf = null;
	private static HConnection conn = null;
	static {
		getConfiguration();
		getHConnection();
	}
	
	private HBasePool(){}
	
	/**
	 * 获取hbase配置信息
	 * 1.hbase.rootdir
	 * 2.hbase.zookeeper.quorum
	 * @return
	 */
	public final static Configuration getConfiguration(){
		String defFile = "hbase.properties";
		
		if(conf==null){
			String errMsg = "Not Found HBase Config. Please set 'hbase.properties' file path param  eg: -Dhbase.config.path=/xxx/hbase.properties";
			conf = HBaseConfiguration.create();
			
			InputStream propFile = null;
			
			Properties prop = new Properties();
			String path = System.getProperty("hbase.config.path");
			if (path != null) {
				if(Files.exists(Paths.get(path))){
					try {
						propFile = new FileInputStream(path);
						logger.info("Load HBase Config From Path : hbase.config.path={}", path);
					} catch (FileNotFoundException e) {
						logger.error(errMsg);
						throw new RuntimeException(errMsg);
					}
				}else{
					errMsg = "File Not Found Exception. Check : hbase.config.path="+path;
					logger.error(errMsg);
					throw new RuntimeException(errMsg);
				}
			}else{
				propFile = ResourceLoad.searchResourceFile(defFile);
			}

			if (propFile!=null) {
				try {
					prop.load(propFile);
					logger.info("Load HBase Config Info : {}", prop.toString());
				} catch (IOException e) {
					logger.error(errMsg);
					throw new RuntimeException(errMsg);
				}
			}else{
				logger.error(errMsg);
				throw new RuntimeException(errMsg);
			}
			conf.set("hbase.zookeeper.quorum", prop.getProperty("hbase.zookeeper.quorum"));
			
		}
		return conf;
	}
	
	/**
	 * 获取hbase连接对象，全局维护一个对象
	 * @return
	 * @throws IOException
	 */
	public final static HConnection getHConnection(){
		if(conn==null){
			try {
				conn = HConnectionManager.createConnection(getConfiguration());
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
		
		return conn;		
	}
	
	/**
	 * 根据表名获取HTable实例
	 * 使用完时调用close对象
	 * @param tablename
	 * @return
	 * @throws IOException
	 */
	public static HTableInterface getHTable(String tablename) throws IOException{
		HTableInterface htable = getHConnection().getTable(tablename);
		
		return htable;
	}
	
	public static void main(String[] args) {
		HConnection hc = HBasePool.getHConnection();
		
	}
}
