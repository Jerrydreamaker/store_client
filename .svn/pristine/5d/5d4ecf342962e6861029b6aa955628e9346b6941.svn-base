package cn.iecas.store.hbase.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseScanUtils {

	/**
	 * 根据起止rowkey和是否反转查询设置扫描器
	 * 
	 * @param startrow
	 *            起始行
	 * @param endrow
	 *            终止行
	 * @param reversed
	 *            是否倒序查询（从后往前查询）
	 * @return
	 * 
	 * @author wlh 2016-10-19
	 */
	public static Scan getScan(String startrow, String stoprow, boolean reversed) {
		Scan scan = new Scan();
		if (reversed) {
			scan.setStartRow(Bytes.toBytes(stoprow));
			scan.setStopRow(Bytes.toBytes(startrow));
		} else {
			scan.setStartRow(Bytes.toBytes(startrow));
			scan.setStopRow(Bytes.toBytes(stoprow));
		}
		scan.setReversed(reversed);
		return scan;
	}
	
	public static Scan getScan(String startrow, String stoprow) {
		return getScan(startrow, stoprow, false);
	}

	public static void addColumn(Scan scan, String family, String... column) {
		for (String c : column) {
			scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(c));
		}
	}

	public static void setFilterList(Scan scan, final Operator operator, Filter... filters) {
		List<Filter> filtersList = new ArrayList<Filter>();
		for (Filter f : filters) {
			filtersList.add(f);
		}
		scan.setFilter(new FilterList(operator, filtersList));
	}
}
