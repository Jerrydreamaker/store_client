package cn.iecas.store.mongo.client;

import com.mongodb.DBObject;

public interface ResultCallback {

	<T> void execute(DBObject dbobject, T list);
}
