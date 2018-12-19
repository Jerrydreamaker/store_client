package cn.iecas.store.hbase.client;

import java.io.IOException;
import java.util.List;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HBase批量存储调用线程
 * @author hhm
 *
 */
public class HBaseStorageThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(HBaseStorageThread.class);
	
	private HTableInterface htable = null;
	private List<Put> puts = null;
	private String htableName = null;
	
	private HBaseStorageThread(String htableName, final List<Put> puts) {
		this.htableName = htableName;
		this.puts = puts;
	}

	public void run() {
		long st = System.currentTimeMillis();
		
		try {
			this.htable = HBasePool.getHTable(htableName);
		} catch (IOException e) {
			logger.error("HBase Table '"+htableName+"' Connection Exception!",e);
			throw new RuntimeException("HBase Table '"+htableName+"' Connection Exception!");
		}
		
		try {
			htable.setAutoFlushTo(false);
			htable.put(puts);
			htable.flushCommits();
			htable.close();
			
			logger.debug("HBase Save : [Table:"+htableName+", Count:"+puts.size()+", Millis:"+(System.currentTimeMillis()-st)+"]");
		} catch (IOException e) {
			logger.error("HBase Table '"+htableName+"' Save Exception!",e);
		}
	}
	
	/**
	 * 执行存储调用方法
	 * @param htableName
	 * @param puts
	 * @return
	 */
	public static Runnable save(String htableName, final List<Put> puts){
		return new HBaseStorageThread(htableName, puts);
	}
}
