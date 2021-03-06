package cn.iecas.store.hbase.client;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class HBasePutUtils {
	public static void put(Put p, String family, String column, String value) {
		if (value == null) {
			return;
		}
		p.add(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));
	}

	public static void put(Put p, String family, String column, Integer value) {
		if (value == null) {
			return;
		}
		p.add(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));
	}

	public static void put(Put p, String family, String column, Long value) {
		if (value == null) {
			return;
		}
		p.add(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));
	}

	public static void put(Put p, String family, String column, Double value) {
		if (value == null) {
			return;
		}
		p.add(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));
	}

	public static void put(Put p, String family, String column, Short value) {
		if (value == null) {
			return;
		}
		p.add(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));
	}
}
