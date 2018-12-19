package cn.iecas.store.hbase.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBaseOptUtils {
	private static final Logger _logger = LoggerFactory.getLogger(HBaseOptUtils.class);

	/**
	 * 创建Htable
	 * 
	 * @param tableName
	 *            表名
	 * @param columnFamily
	 *            列簇
	 * @throws Exception
	 */
	public static void createTable(String tableName, String... columnFamily) throws IOException {
		HBaseAdmin admin = null;
		try {
			admin = new HBaseAdmin(HBasePool.getConfiguration());
			if (admin.tableExists(tableName)) {
				_logger.info("table '" + tableName + "' has exists!");
			} else {

				HTableDescriptor tabledesc = new HTableDescriptor(TableName.valueOf(tableName));
				for (String cf : columnFamily) {
					tabledesc.addFamily(new HColumnDescriptor(cf));
				}
				admin.createTable(tabledesc);
				_logger.info("create table '" + tableName + "' success!");
			}

		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (admin != null) {
				try {
					admin.close();
				} catch (IOException e) {
					_logger.error(e.getMessage(), e);
				}
			}
		}
	}

	public static void closeHTable(HTableInterface htable) {
		closeHTable(htable, null);
	}

	public static void closeHTable(HTableInterface htable, ResultScanner scanner) {
		try {
			if (scanner != null) {
				scanner.close();
			}
			if (htable != null) {
				htable.close();
			}
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param tablename 表名
	 * @param rowkey
	 * @return 列族存储数据的 Map
	 * @throws IOException
	 */
	public static Map<String, byte[]> findByRowkey(String tablename, String rowkey) throws IOException {
		Map<String, byte[]> valuesMap = null;
		HTableInterface htable = null;
		try {
			htable = HBasePool.getHTable(tablename);
			Get g = new Get(Bytes.toBytes(rowkey));
			Result rs = htable.get(g);

			if(!rs.isEmpty()){
				valuesMap = HBaseResultUtils.getColumnValueMap(rs);
			}

		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			closeHTable(htable);
		}
		return valuesMap;
	}

	/**
	 *
	 * @param tablename
	 * 		表名
	 * @param rowkey
	 *
	 * @param callback
	 * @throws IOException
	 */
	public static void findByRowkey(String tablename, byte[] rowkey, ScanCallback callback) throws IOException {
		HTableInterface htable = null;
		try {
			htable = HBasePool.getHTable(tablename);
			Get g = new Get(rowkey);
			Result rs = htable.get(g);
			
			if(!rs.isEmpty()){
				callback.dealWith(rs);
			}
			
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			closeHTable(htable);
		}
	}
	public static void findByRowkey(String tablename, String rowkey, ScanCallback callback) throws IOException {
		HTableInterface htable = null;
		try {
			htable = HBasePool.getHTable(tablename);
			Get g = new Get(Bytes.toBytes(rowkey));
			Result rs = htable.get(g);
			if(!rs.isEmpty()){
				callback.dealWith(rs);
			}
			
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			closeHTable(htable);
		}
	}
	public static void findByRowkey(String tablename, List<String> rowkey, ScanCallback callback) throws IOException {
		long start = System.currentTimeMillis();
		HTableInterface htable = null;
		int count = 0;
		try {
			htable = HBasePool.getHTable(tablename);
			List<Get> gls = new ArrayList<Get>();
			for (String r : rowkey) {
				Get g = new Get(Bytes.toBytes(r));
				gls.add(g);
			}
			Result[] rsList = htable.get(gls);
			if(rsList!=null && rsList.length>0){
				count++;
				for (Result result : rsList) {
					callback.dealWith(result);
				}
			}
			
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			closeHTable(htable);
		}
		_logger.info("findByRowkey() | table:{}  count:{}  millis:{}",tablename,count,(System.currentTimeMillis()-start));
	}


	public static boolean existsRowkey(String tablename, String rowkey){
		boolean flag = false;
		HTableInterface htable = null;
		try {
			htable = HBasePool.getHTable(tablename);
			Get g = new Get(Bytes.toBytes(rowkey));
			flag = htable.exists(g);
			
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
		} finally {
			closeHTable(htable);
		}
		return flag;
	}

	/**
	 * 删除rowkey行
	 * 
	 * @param tablename
	 * @param rowkey
	 * @throws IOException
	 */
	public static void deleteByRowkey(String tablename, String rowkey) throws IOException {
		HTableInterface htable = null;
		try {
			htable = HBasePool.getHTable(tablename);
			Delete del = new Delete(Bytes.toBytes(rowkey));
			htable.delete(del);
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			closeHTable(htable);
		}
	}

	/**
	 * 根据起止rowkey和是否反转查询扫描hbase表，并通过回调函数逐行扫描处理
	 * 
	 * @param tablename
	 * @param startrow
	 * @param endrow
	 * @param reversed 是否倒序查询（从后往前查询）
	 * @param callback
	 * @throws IOException
	 */
	public static void scanningTable(String tablename, String startrow, String endrow, boolean reversed, ScanCallback callback) throws IOException {
		Scan scan = HBaseScanUtils.getScan(startrow, endrow, reversed);
		scanningTable(tablename, scan, callback);
	}

	public static void scanningTable(String tablename, Scan scan, ScanCallback callback) throws IOException {
		long start = System.currentTimeMillis();
		HTableInterface htable = null;
		ResultScanner scanner = null;
		int count = 0;
		try {
			
			htable = HBasePool.getHTable(tablename);
			scanner = htable.getScanner(scan);
			for (Result result : scanner) {
				count++;
				callback.dealWith(result);
			}
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			closeHTable(htable, scanner);
		}
		_logger.debug("scanningTable() | table:{}  count:{}  millis:{}",tablename,count,(System.currentTimeMillis()-start));
	}
	
	/**
	 * 根据起止rowkey查出区间内第一条数据
	 * 
	 * @param tablename
	 * @param startrow
	 * @param endrow
	 * @throws IOException
	 */
	public static Result getFirstRow(String tablename, String startrow, String endrow) throws IOException {
		return getFirstRow(tablename, startrow, endrow, new Scan());
	}
	
	public static Result getFirstRow(String tablename, String startrow, String endrow, final Scan scan) throws IOException {
		return getRow(tablename, HBaseScanUtils.getScan(startrow, endrow).setFilter(scan.getFilter()));
	}
	
	/**
	 * 根据起止rowkey查出区间内最后一条数据
	 * 
	 * @param tablename
	 * @param startrow
	 * @param endrow
	 * @throws IOException
	 */
	public static Result getLastRow(String tablename, String startrow, String endrow) throws IOException {
		return getLastRow(tablename, startrow, endrow, new Scan());
	}
	
	public static Result getLastRow(String tablename, String startrow, String endrow, final Scan scan) throws IOException {
		return getRow(tablename, HBaseScanUtils.getScan(startrow, endrow, true).setFilter(scan.getFilter()));
	}
	
	public static Result getRow(String tablename, Scan scan) throws IOException {
		long start = System.currentTimeMillis();
		HTableInterface htable = null;
		ResultScanner scanner = null;
		Result result = null;
		try {
			htable = HBasePool.getHTable(tablename);
			scanner = htable.getScanner(scan);
			for (Result row : scanner) {
				result = row;
				break;
			}
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			closeHTable(htable, scanner);
		}
		_logger.debug("scanningTable() | table:{} millis:{}",tablename,(System.currentTimeMillis()-start));
		return result;
	}

	/**
	 * hbase表ResultScanner扫描循环回调函数
	 */
	public static interface ScanCallback {
		void dealWith(Result result);
	}
	
	public static void scanningTable(String tablename, Scan scan, ScanFindCallback callback) throws IOException {
		long start = System.currentTimeMillis();
		HTableInterface htable = null;
		ResultScanner scanner = null;
		int count = 0;
		try {
			htable = HBasePool.getHTable(tablename);
			scanner = htable.getScanner(scan);
			for (Result result : scanner) {
				count++;
				if(callback.find(result)){
					break;
				}
			}
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			closeHTable(htable, scanner);
		}
		_logger.info("scanningTable() | table:{}  count:{}  millis:{}",tablename,count,(System.currentTimeMillis()-start));
	}
	
	/**
	 * hbase表ResultScanner扫描循环回调函数
	 */
	public static interface ScanFindCallback {
		boolean find(Result result);
	}

	/**
	 *
	 * @param tablename
	 * @param put
	 * @throws IOException
	 */
	public static void save(String tablename, Put put) throws IOException{
		HTableInterface htable = null;
		try {
			htable = HBasePool.getHTable(tablename);
			htable.put(put);
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			closeHTable(htable);
			_logger.info("close table");
		}
	}
	public static void save(String tablename, List<Put> puts) throws IOException{
		HTableInterface htable = null;
		try {
			htable = HBasePool.getHTable(tablename);
			htable.setAutoFlushTo(false);
			htable.put(puts);
			htable.flushCommits();
			
		} catch (IOException e) {
			_logger.error(e.getMessage(), e);
			throw e;
		} finally {
			closeHTable(htable);
		}
	}
}
