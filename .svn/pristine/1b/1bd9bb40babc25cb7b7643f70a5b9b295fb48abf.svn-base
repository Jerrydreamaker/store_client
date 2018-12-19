package cn.iecas.store.mongo.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

/**
 * mongodb工具类
 * 
 * @author hhm
 *
 */
public class MongoOptUtils {

	private static final Logger _logger = LoggerFactory.getLogger(MongoOptUtils.class);

	public static String executeQueryById(String tablename, Object _id) {
		DBCollection coll = MongoPool.getDBCollection(tablename);
		DBObject dbo = coll.findOne(_id);
		if (dbo == null) {
			return null;
		}
		return dbo.toString();
	}

	public static <T> T executeQueryById(String tablename, Object _id, Class<T> clazz) {
		DBCollection coll = MongoPool.getDBCollection(tablename);
		DBObject dbo = coll.findOne(_id);
		if (dbo != null) {
			T t = com.alibaba.fastjson.JSON.parseObject(dbo.toString(), clazz);
			return t;
		}
		return null;
	}

	/**
	 * 根据表名和查询条件执行查询语句
	 * 
	 * @param tablename
	 *            表明
	 * @param cond
	 *            查询条件
	 * @return
	 */
	public static List<String> executeQueryForString(String tablename, DBObject cond) {
		_logger.debug("executeQuery  tablename : " + tablename + "  condition : " + (cond == null ? "null" : cond.toString()));

		List<String> list = new ArrayList<String>();

		DBCollection coll = MongoPool.getDBCollection(tablename);

		DBCursor cursor = coll.find(cond);
		while (cursor.hasNext()) {
			DBObject bson = cursor.next();
			list.add(bson.toString());
		}
		cursor.close();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> executeQueryForMap(String tablename, DBObject cond) {
		_logger.debug("executeQuery  tablename : " + tablename + "  condition : " + (cond == null ? "null" : cond.toString()));

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		DBCollection coll = MongoPool.getDBCollection(tablename);

		DBCursor cursor = coll.find(cond);
		while (cursor.hasNext()) {
			DBObject bson = cursor.next();
			list.add(bson.toMap());
		}
		cursor.close();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> executeQuery(String tablename, DBObject cond) {
		_logger.debug("executeQuery  tablename : " + tablename + "  condition : " + (cond == null ? "null" : cond.toString()));

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		DBCollection coll = MongoPool.getDBCollection(tablename);

		DBCursor cursor = coll.find(cond);
		while (cursor.hasNext()) {
			DBObject bson = cursor.next();
			list.add(bson.toMap());
		}
		cursor.close();
		return list;
	}

	public static <T> void executeQuery(String tablename, DBObject cond, T resultList, ResultCallback callback) {
		_logger.debug("executeQuery  tablename : " + tablename + "  condition : " + (cond == null ? "null" : cond.toString()));

		DBCollection coll = MongoPool.getDBCollection(tablename);

		DBCursor cursor = coll.find(cond);
		while (cursor.hasNext()) {
			DBObject bson = cursor.next();
			callback.execute(bson, resultList);
		}
		cursor.close();
	}

	public static <T> void executeQuery(String tablename, DBObject cond, DBObject keys, T resultList, ResultCallback callback) {
		_logger.debug("executeQuery  tablename : " + tablename + "  condition : " + (cond == null ? "null" : cond.toString()));

		DBCollection coll = MongoPool.getDBCollection(tablename);

		DBCursor cursor = coll.find(cond, keys);
		while (cursor.hasNext()) {
			DBObject bson = cursor.next();
			callback.execute(bson, resultList);
		}
		cursor.close();
	}

	/**
	 * 根据表名和查询条件执行查询语句
	 * 
	 * @param tablename
	 *            表明
	 * @param cond
	 *            查询条件
	 * @param keys
	 *            返回的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> executeQuery(String tablename, DBObject cond, DBObject keys) {
		_logger.debug("executeQuery  tablename : " + tablename + "  condition : " + (cond == null ? "null" : cond.toString()) + "  keys : "
				+ (keys == null ? "null" : keys.toString()));
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		DBCollection coll = MongoPool.getDBCollection(tablename);

		DBCursor cursor = coll.find(cond, keys);
		while (cursor.hasNext()) {
			DBObject bson = cursor.next();
			list.add(bson.toMap());
		}
		cursor.close();
		return list;
	}

	/**
	 * 根据表名和查询条件执行查询语句
	 * 
	 * @param <T>
	 *            映射的类
	 * @param tablename
	 * @param cond
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> executeQuery(String tablename, DBObject cond, Class<T> clazz) {
		_logger.debug("executeQuery  tablename : " + tablename + "  condition : " + (cond == null ? "null" : cond.toString()));
		List<T> list = new ArrayList<T>();

		DBCollection coll = MongoPool.getDBCollection(tablename);

		DBCursor cursor = coll.find(cond);
		while (cursor.hasNext()) {
			DBObject bson = cursor.next();

			T t = com.alibaba.fastjson.JSON.parseObject(bson.toString(), clazz);

			list.add(t);
		}
		cursor.close();
		return list;
	}

	/**
	 * 执行插入或更新操作
	 * 
	 * @param tablename
	 * @param json
	 */
	public static void executeInsertOrUpdate(String tablename, String json) {
		// _logger.debug("executeInsertOrUpdate  tablename : "+tablename+"  content : "+json);

		DBCollection coll = MongoPool.getDBCollection(tablename);

		DBObject dbo = (DBObject) JSON.parse(json);
		coll.save(dbo);

	}
	public static void executeInsert(String tablename, String json) {
		// _logger.debug("executeInsertOrUpdate  tablename : "+tablename+"  content : "+json);
		
		DBCollection coll = MongoPool.getDBCollection(tablename);
		
		DBObject dbo = (DBObject) JSON.parse(json);
		coll.insert(dbo);
		
	}

	public static void executeInsertOrUpdateList(String tablename, List<Object> jsons) {
		if (jsons.isEmpty() || null == jsons) {
			return;
		}
		DBCollection coll = MongoPool.getDBCollection(tablename);
		List<DBObject> list = new ArrayList<DBObject>();
		for (Object s : jsons) {
			DBObject dbo = (DBObject) JSON.parse(com.alibaba.fastjson.JSON.toJSONString(s));
			list.add(dbo);
		}
		coll.insert(list);
	}

	/**
	 * 执行插入或更新操作
	 * 
	 * @param tablename
	 * @param obj
	 */
	public static void executeInsertOrUpdate(String tablename, Object obj) {
		String json = com.alibaba.fastjson.JSON.toJSONString(obj);
		executeInsertOrUpdate(tablename, json);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> executeGroupCount(String tablename, DBObject cond, String... groupKey) {
		String kls = "";
		DBObject key = new BasicDBObject();
		for (String k : groupKey) {
			key.put(k, true);

			kls += k + ",";
		}
		_logger.debug("executeCount() key:" + kls + "  condition : " + (cond == null ? "null" : cond.toString()));

		DBCollection coll = MongoPool.getDBCollection(tablename);

		DBObject initial = new BasicDBObject();
		initial.put("c", 0);
		String reduce = "function(obj,prev){" + " prev.c = parseInt(prev.c+1);" // 求所有符合条件的数量
				+ " }";
		BasicDBList ls = (BasicDBList) coll.group(key, cond, initial, reduce);

		Map<String, Object> map = ls.toMap();

		return map;
	}

	public static long executeCount(String tablename, DBObject cond) {

		DBCollection coll = MongoPool.getDBCollection(tablename);
		long c = coll.count(cond);

		return c;
	}

	public static int executeRemove(String tablename, DBObject cond) {
		DBCollection coll = MongoPool.getDBCollection(tablename);

		WriteResult wr = coll.remove(cond);
		return wr.getN();
	}

}
