package cn.iecas.store.mongo.client;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import cn.iecas.store.mongo.annotation.Entity;

/**
 * mongo基本数据库操作方法
 * 
 * @author hhm
 *
 * @param <T>
 */
public abstract class MongoDaoSupport<T extends MongoModel> {
	private static final Logger _logger = LoggerFactory.getLogger(MongoDaoSupport.class);
	/**
	 * 对应操作的collection
	 */
	public  DBCollection collection;

	public MongoDaoSupport() {
		setDBCollection();
	}
	

	/**
	 * 设置操作的collection
	 */
	@SuppressWarnings("unchecked")
	public void setDBCollection(){
		String coll = "";
		
		//通过反射获取继承类的class实例对象
		Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		try {
			Entity entity = clazz.getAnnotation(Entity.class);
			coll = entity.name();
		} catch (Exception e) {
			coll = clazz.getSimpleName();
		}
		
		collection = MongoPool.getDBCollection(coll);
	}

	/**
	 * 插入数据，根据_id值查看数据库中是否已存在，如果存在则不插入数据，否则插入数据
	 * 
	 * @param obj
	 */
	public void save(T obj) {
		if (obj == null) {
			return;
		}

		DBObject dbo = obj.toDBObject();
		// 插入数据，id已存在则不插入
		collection.insert(dbo);
		
		_logger.debug("save object : "+dbo.toString());
	}

	/**
	 * 插入数据，根据_id值查看数据库中是否已存在，如果存在则替换所有数据，否则插入数据
	 * 
	 * @param obj
	 */
	public void saveOrUpdate(T obj) {
		if (obj == null) {
			return;
		}
		DBObject dbo = beanToBson(obj);

		// 插入或根据id做更新操作，有数据则更新无则插入
		collection.save(dbo);
		_logger.debug("save or update object : "+dbo.toString());
	}

	/**
	 * 根据_id查询对象，并返回T对象
	 * @param _id
	 * @return
	 */
	public T findById(Object _id) {
		DBObject bson = collection.findOne(_id);
		if (bson == null) {
			return null;
		}
		T t = bsonToBean(bson);
		_logger.debug("findById() object : "+bson.toString());
		return t;
	}
	
	/**
	 * 根据_id查询对象，并返回DBObject对象
	 * @param _id
	 * @return
	 */
	public DBObject findDBObjectById(Object _id) {
		DBObject bson = collection.findOne(_id);
		if (bson == null) {
			return null;
		}
		_logger.debug("findDBObjectById() object : "+bson.toString());
		return bson;
	}

	public List<T> find(DBObject ref) {
		List<T> ls = new ArrayList<T>();
		DBCursor cursor = collection.find(ref);

		while (cursor.hasNext()) {
			DBObject bson = cursor.next();
			
			T t = bsonToBean(bson);
			if(t!=null){
				ls.add(t);
			}
		}

		return ls;
	}
	public List<DBObject> findDBObjectList(DBObject ref) {
		List<DBObject> ls = new ArrayList<DBObject>();
		DBCursor cursor = collection.find(ref);
		
		while (cursor.hasNext()) {
			DBObject bson = cursor.next();
			ls.add(bson);
		}
		
		return ls;
	}
	
	@SuppressWarnings("unchecked")
	private T bsonToBean(DBObject bson){
		Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		
		T t = (T)JSONObject.parseObject(bson.toString(), clazz);
		
		return t;
	}
	private DBObject beanToBson(T t){
		DBObject bson = new BasicDBObject();
		Field[] fields = t.getClass().getDeclaredFields();
		
		for(Field f : fields){
			try {
				f.setAccessible(true);
				bson.put(f.getName(), f.get(t));
			} catch (IllegalArgumentException e) {
				_logger.error(e.getMessage(),e);
			} catch (IllegalAccessException e) {
				_logger.error(e.getMessage(),e);
			}
		}
		
		return bson;
	}

	public List<T> find(DBObject ref, int start, int count) {
		List<T> ls = new ArrayList<T>();
		DBCursor cursor = collection.find(ref,new BasicDBObject());
		cursor.skip(start);
		cursor.batchSize(count);

		while (cursor.hasNext()) {
			DBObject bson = cursor.next();
			
			T t = bsonToBean(bson);
			if(t!=null){
				ls.add(t);
			}
		}

		return ls;
	}

	public void deleteById(Object _id) {
		DBObject obj = collection.findOne(_id);
		collection.remove(obj);
	}
	
	
	public BasicDBList group(DBObject key, DBObject cond, DBObject initial, String reduce){
		
		BasicDBList rsls = (BasicDBList)collection.group(key, cond, initial, reduce);
		
		return rsls;
	}
	

}
