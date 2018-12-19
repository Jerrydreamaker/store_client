package cn.iecas.store.hbase.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * hbase 查询过滤条件工具类
 * @author hhm
 *
 */
public class HBaseFilterUtils {
	
	/**
	 * 单列值过滤条件
	 * @param family
	 * @param column
	 * @param compareOp
	 * @param value
	 * @return
	 */
	public static Filter columnValueFilter(String family,String column,final CompareOp compareOp,String value){
		return new SingleColumnValueFilter(Bytes.toBytes(family), Bytes.toBytes(column), compareOp, Bytes.toBytes(value));
	}
	
	public static Filter columnValueFilter(String family,String column,final CompareOp compareOp,Object value){
		byte[] b = BytesUtils.toBytes(value);
		
		return new SingleColumnValueFilter(Bytes.toBytes(family), Bytes.toBytes(column), compareOp, b);
	}
	
	/**
	 * 单列值过滤条件 等于
	 * @param family
	 * @param column
	 * @param value
	 * @return
	 */
	public static Filter columnValueEqual(String family, String column, Object value){
		return columnValueFilter(family, column, CompareOp.EQUAL, value);
	}
	
	/**
	 * 单列值过滤条件 包含 in
	 * @param family
	 * @param column
	 * @param value
	 * @return
	 */
	public static Filter columnValueIn(String family,String column,Object... values){
		List<Filter> filterList = new ArrayList<>();
		
		for (Object val : values) {
			filterList.add(columnValueEqual(family, column, val));
		}
		return new FilterList(Operator.MUST_PASS_ONE,filterList);
	}

	public static Filter and(Filter... filters){
		List<Filter> filterList = Arrays.asList(filters);
		return and(filterList);
	}
	public static Filter and(List<Filter> filterList){
		return new FilterList(Operator.MUST_PASS_ALL,filterList);
	}
	public static Filter or(Filter... filters){
		List<Filter> filterList = Arrays.asList(filters);
		return or(filterList);
	}
	public static Filter or(List<Filter> filterList){
		return new FilterList(Operator.MUST_PASS_ONE,filterList);
	}
	
	public static Filter jsonQueryToFilter(String family,String jsonQuery){
		Filter filter = null;
		if (jsonQuery != null) {
			JSONObject queryJson = JSONObject.parseObject(jsonQuery);
			List<Filter> filterList = new ArrayList<>();
			for (Entry<String, Object> entry : queryJson.entrySet()) {
				String column = entry.getKey();
				if (entry.getValue() instanceof JSONArray) {
					JSONArray jarr = (JSONArray) entry.getValue();
					//单值in条件
					filterList.add(HBaseFilterUtils.columnValueIn(family, column, jarr.toArray()));
				} else {
					//单值=条件
					filterList.add(HBaseFilterUtils.columnValueEqual(family, column, entry.getValue()));
				}
			}
			if (filterList.size() > 0) {
				filter = HBaseFilterUtils.and(filterList);
			}
		}
		return filter;
	}
	
}
