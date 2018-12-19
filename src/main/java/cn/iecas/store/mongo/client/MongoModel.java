package cn.iecas.store.mongo.client;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * mongo 对象父类
 * @author hhm
 *
 */

@SuppressWarnings("serial")
public class MongoModel implements Serializable {
	public static final Logger _logger = LoggerFactory.getLogger(MongoModel.class);
	
	public DBObject toDBObject(){
		
		DBObject bson = new BasicDBObject();
		Field[] fields = getClass().getDeclaredFields();
		
		for(Field f : fields){
			try {
				f.setAccessible(true);
				bson.put(f.getName(), f.get(this));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return bson;
	}

}
