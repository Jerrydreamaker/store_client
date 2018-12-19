package cn.iecas.store.hbase.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBaseResultUtils {
	private static final Logger _logger = LoggerFactory.getLogger(HBaseResultUtils.class);
	public static Map<String, byte[]> getColumnValueMap(Result rs) {
		Map<String, byte[]> valuesMap = null;
		if (rs != null) {
			try {
				valuesMap = new HashMap<String, byte[]>();

				CellScanner cs = rs.cellScanner();
				while (cs.advance()) {
					Cell c = cs.current();
					String qua = new String(CellUtil.cloneQualifier(c));

					byte[] val = c.getValueArray();
					valuesMap.put(qua, val);
				}
			} catch (IOException e) {
				_logger.error(e.getMessage(), e);
			}
		}

		return valuesMap;
	}

	public static String getRowkey(Result rs) {
		byte[] r = rs.getRow();
		return Bytes.toString(r);
	}

	public static String getStringValue(Result rs, String family, String column, String def) {
		byte[] col = rs.getValue(Bytes.toBytes(family), Bytes.toBytes(column));
		if (col != null) {
			return Bytes.toString(col);
		} else {
			return def;
		}
	}

	public static String getStringValue(Result rs, String family, String column) {
		return getStringValue(rs, family, column, null);
	}

	public static Integer getIntValue(Result rs, String family, String column, Integer def) {
		byte[] col = rs.getValue(Bytes.toBytes(family), Bytes.toBytes(column));
		if (col != null) {
			return Bytes.toInt(col);
		} else {
			return def;
		}
	}

	public static Integer getIntValue(Result rs, String family, String column) {
		return getIntValue(rs, family, column, null);
	}

	public static Long getLongValue(Result rs, String family, String column, Long def) {
		byte[] col = rs.getValue(Bytes.toBytes(family), Bytes.toBytes(column));
		if (col != null) {
			return Bytes.toLong(col);
		} else {
			return def;
		}
	}

	public static Long getLongValue(Result rs, String family, String column) {
		return getLongValue(rs, family, column, null);
	}

	public static Short getShortValue(Result rs, String family, String column, Short def) {
		byte[] col = rs.getValue(Bytes.toBytes(family), Bytes.toBytes(column));
		if (col != null) {
			return Bytes.toShort(col);
		} else {
			return def;
		}
	}

	public static Short getShortValue(Result rs, String family, String column) {
		return getShortValue(rs, family, column, null);
	}

	/**
	 * 获取行列值，并返回对应类型
	 * 
	 * @param rs
	 * @param family
	 * @param column
	 * @param returnClazz
	 * @return
	 */
	public static <T> T getColumnValue(Result rs, String family, String column, Class<T> returnClazz) {
		return getColumnValue(rs, Bytes.toBytes(family), column, returnClazz);
	}

	/**
	 * 获取行列值，并返回对应类型
	 * 
	 * @param rs
	 * @param family
	 * @param column
	 * @param returnClazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getColumnValue(Result rs, byte[] family, String column, Class<T> returnClazz) {
		Object o = null;
		T t = null;
		try {
			byte[] col = rs.getValue(family, Bytes.toBytes(column));

			if (returnClazz.equals(String.class)) {
				if (col != null) {
					o = Bytes.toString(col);
				} else {
					return null;
				}
			} else if (returnClazz.equals(Integer.class)) {
				if (col != null) {
					o = Bytes.toInt(col);
				} else {
					o = 0;
				}
			} else if (returnClazz.equals(Double.class)) {
				if (col != null) {
					o = Bytes.toDouble(col);
				} else {
					o = 0.0d;
				}
			} else if (returnClazz.equals(Long.class)) {
				if (col != null) {
					o = Bytes.toLong(col);
				} else {
					o = 0l;
				}
			} else if (returnClazz.equals(Short.class)) {
				if (col != null) {
					o = Bytes.toShort(col);
				} else {
					o = 0;
				}
			} else {
				return null;
			}

		} catch (Exception e) {
			return null;
		}

		t = (T) o;

		return t;
	}
}
