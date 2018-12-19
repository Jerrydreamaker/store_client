package cn.iecas.store.hbase.client;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

public interface HBaseModel {

	String getRowkey();
	
	Put getPut();
	
	<T> T getObject(Result rs);

}
