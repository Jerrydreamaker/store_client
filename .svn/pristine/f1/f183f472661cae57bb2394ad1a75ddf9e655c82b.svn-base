package cn.iecas.store.mongo.client;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

/**
 * mongodb 操作客户端 driver v3版本
 * 
 * @author hhm
 *
 */
public class MongoOptClient {

	private static final Logger logger = LoggerFactory.getLogger(MongoOptClient.class);

	/**
	 * 根据_id查询
	 * 
	 * @param collection
	 *            表名
	 * @param _id
	 *            查询_id
	 * @param projection
	 *            返回的字段
	 * @return Document or null
	 */
	private static Document findById(String collection, Object _id, Bson projection) {
		logger.debug("findById({}, {})", collection, _id);
		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);
		Document doc = null;
		if (projection == null) {
			doc = coll.find(eq("_id", _id)).first();
		} else {
			doc = coll.find(eq("_id", _id)).projection(projection).first();
		}

		return doc;
	}

	/**
	 * 根据_id查询
	 * 
	 * @param collection
	 *            表名
	 * @param _id
	 *            查询_id
	 * @param fieldNames
	 *            返回的字段
	 * @return Document or null
	 */
	public static Document findById(String collection, Object _id, String... fieldNames) {
		Bson projection = null;
		if (fieldNames.length != 0) {
			projection = fields(excludeId(), include(fieldNames));
		}
		return findById(collection, _id, projection);
	}

	/**
	 * 根据_id查询，并转化为对象
	 * 
	 * @param collection
	 *            表名
	 * @param _id
	 *            查询_id
	 * @param clazz
	 *            所要转化的对象的class
	 * @return T or null
	 */
	public static <T> T findByIdToObject(String collection, Object _id, Class<T> clazz) {
		Document doc = findById(collection, _id);
		if (doc == null) {
			return null;
		}
		return DocumentUtils.parseDocumentToObject(doc, clazz);
	}

	/**
	 * 根据表名和查询条件执行查询语句
	 * 
	 * @param collection
	 *            表明
	 * @param filter
	 *            查询条件
	 * @param fieldNames
	 *            返回的字段
	 * @param callback
	 *            Document 转化 T 对象处理回调函数
	 * @return List<T>
	 */
	public static <T> List<T> findAll(String collection, Bson filter, String[] fieldNames,
			final QueryCallback<T> callback) {
		final List<T> list = new ArrayList<T>();

		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);
		FindIterable<Document> fit = filter == null ? coll.find() : coll.find(filter);
		fit = (fieldNames == null || fieldNames.length == 0) ? fit
				: fit.projection(fields(excludeId(), include(fieldNames)));
		fit.forEach(new Block<Document>() {
			@Override
			public void apply(Document doc) {
				T t = callback.deal(doc);
				if (t != null)
					list.add(t);
			}
		});
		return list;
	}

	/**
	 * 根据表名和查询条件执行查询语句
	 * 
	 * @param collection
	 *            表明
	 * @param filter
	 *            查询条件
	 * @param fieldNames
	 *            返回的字段
	 * @return List<T>
	 */
	public static List<Document> findAll(String collection, Bson filter, String... fieldNames) {
		return findAll(collection, filter, fieldNames, new QueryCallback<Document>() {
			@Override
			public Document deal(Document doc) {
				return doc;
			}
		});
	}

	/**
	 * 根据表名和查询条件执行查询语句
	 * 
	 * @param collection
	 *            表明
	 * @param filter
	 *            查询条件
	 * @param fieldNames
	 *            返回的字段
	 * @return List<T>
	 */
	public static List<String> findAllToJson(String collection, Bson filter, String... fieldNames) {
		return findAll(collection, filter, fieldNames, new QueryCallback<String>() {
			@Override
			public String deal(Document doc) {
				return DocumentUtils.parseDocumentToJson(doc);
			}

		});
	}

	public static Document findAndOne(String collection,Bson filter){
		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);
		FindIterable<Document> fit = filter == null ? coll.find() : coll.find(filter);
		Document doc =fit.first();

		return doc;
	
	}
	public static Document findAndOne(String collection, Bson filter, Bson sort){
		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);
		
		FindIterable<Document> fit = filter == null ? coll.find() : coll.find(filter);
		if(sort!=null){
			fit = fit.sort(sort);
		}
		Document doc =fit.first();
		
		return doc;
		
	}
	public static <T> T findAndOne(String collection,Bson filter,Class<T> clazz){
		//logger.info("Find and one query:"+MongoFilterUtils.bsonToString(filter));
		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);
		FindIterable<Document> fit = filter == null ? coll.find() : coll.find(filter);
		Document doc =fit.first();
		if(doc==null){
			return null;
		}
		T t = DocumentUtils.parseDocumentToObject(doc,clazz);
		return t;
	}
	public static <T> T findAndOne(String collection, Bson filter, Bson sort,Class<T> clazz){
		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);
		
		FindIterable<Document> fit = filter == null ? coll.find() : coll.find(filter);
		if(sort!=null){
			fit = fit.sort(sort);
		}
		Document doc =fit.first();
		if(doc==null){
			return null;
		}
		T t = DocumentUtils.parseDocumentToObject(doc,clazz);
		return t;
	}
	/**
	 * 根据表名和查询条件执行查询语句
	 * 
	 * @param collection
	 *            表明
	 * @param filter
	 *            查询条件
	 * @param fieldNames
	 *            返回的字段
	 * @param clazz
	 *            Document 转化 T 对象
	 * @return List<T>
	 */
	public static <T> List<T> findAllToObject(String collection, Bson filter, String[] fieldNames,
			final Class<T> clazz) {
		return findAll(collection, filter, fieldNames, new QueryCallback<T>() {
			@Override
			public T deal(Document doc) {
				return DocumentUtils.parseDocumentToObject(doc, clazz);
			}
		});
	}

	/**
	 * 根据表名和查询条件执行查询语句
	 * 
	 * @param collection
	 *            表明
	 * @param filter
	 *            查询条件
	 * @param clazz
	 *            Document 转化 T 对象
	 * @return List<T>
	 */
	public static <T> List<T> findAllToObject(String collection, Bson filter, final Class<T> clazz) {
		return findAllToObject(collection, filter, new String[0], clazz);
	}

	/**
	 * 根据表名、mbbh和起始时间执行查询语句，返回查询结果
	 * 
	 * @param collection
	 *            表名
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param fieldNames
	 *            返回的字段
	 * @return List<T>
	 */
	public static <T> List<T> findAllToObjectByMbbhAndTime(String collection, String mbbh, long startTime, long endTime,
			final Class<T> clazz) {

		Bson mbbhFilter = Filters.eq("XTMBBH", mbbh);
		Bson timeFilter = Filters.and(Filters.gte("WZSJ", startTime), Filters.lte("WZSJ", endTime));
		Bson filter = Filters.and(timeFilter, mbbhFilter);

		return findAllToObject(collection, filter, clazz);
	}

	public static void findAndDeal(String collection, Bson filter, String[] fieldNames, final DealCallback callback) {
		findAndDeal(collection, filter, fieldNames, null, callback);
	}
	
	public static void findAndDeal(String collection, Bson filter, String[] fieldNames, Bson sort, final DealCallback callback) {
		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);
		FindIterable<Document> fit = filter == null ? coll.find() : coll.find(filter);
		fit = (fieldNames == null || fieldNames.length == 0) ? fit
				: fit.projection(fields(excludeId(), include(fieldNames)));
		
		if(sort!=null){
			fit = fit.sort(sort);
		}
		
		fit.forEach(new Block<Document>() {
			@Override
			public void apply(Document doc) {
				callback.deal(doc);
			}
		});
	}

	public static void findAndDeal(String collection, Bson filter, final DealCallback callback) {
		findAndDeal(collection, filter, null, callback);
	}

	/**
	 * 根据表名、mbbh和起始时间执行查询语句，查询结果在回调函数中进行处理
	 * 
	 * @param collection
	 *            表名
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param fieldNames
	 *            返回的字段
	 * @return
	 */
	public static void findAllByMbbhAndTime(String collection, String mbbh, long startTime, long endTime,
			String[] fieldNames, final DealCallback callback) {

		Bson mbbhFilter = Filters.eq("XTMBBH", mbbh);
		Bson timeFilter = Filters.and(Filters.gte("WZSJ", startTime), Filters.lte("WZSJ", endTime));
		Bson filter = Filters.and(timeFilter, mbbhFilter);

		findAndDeal(collection, filter, fieldNames, callback);

	}

	/**
	 * 执行插入操作
	 * 
	 * @param collection
	 * @param json
	 */
	public static void insert(String collection, String json) {
		Document doc = DocumentUtils.parseJsonToDocument(json);
		insert(collection, doc);

	}

	public static void insert(String collection, Object obj) {
		Document doc = DocumentUtils.parseObjectToDocument(obj);
		insert(collection, doc);
	}

	public static void insert(String collection, Document doc) {
		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);
		coll.insertOne(doc);
	}

	public static void insertOrUpdate(String collection, Document doc) {
		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);

		UpdateOptions opt = new UpdateOptions();
		opt.upsert(true);
		coll.replaceOne(eq("_id", doc.get("_id")), doc, opt);
	}
	
	public static boolean insertOrUpdate2(String collection,Document doc){
		boolean flag=false;
		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);

		UpdateOptions opt = new UpdateOptions();
		opt.upsert(true);
		UpdateResult updateValue=coll.replaceOne(eq("_id", doc.get("_id")), doc, opt);
		if(updateValue.getUpsertedId()!=null){
			flag=true;
		}
		return flag;
	}
	public static void insertOrUpdate(String collection, String json) {
		Document doc = DocumentUtils.parseJsonToDocument(json);
		insertOrUpdate(collection, doc);
	}

	public static void insertOrUpdate(String collection, Object obj) {
		Document doc = DocumentUtils.parseObjectToDocument(obj);
		insertOrUpdate(collection, doc);
	}

	public static boolean deleteById(String collection, Object _id) {
		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);
		DeleteResult rs = coll.deleteOne(eq("_id", _id));
		if (rs.getDeletedCount() == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static long delete(String collection, Bson filter) {
		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);
		DeleteResult rs = coll.deleteMany(filter);
		return rs.getDeletedCount();
	}

	public static long count(String collection, Bson filter) {
		MongoCollection<Document> coll = MongoPool.getMongoCollection(collection);
		return coll.count(filter);
	}

	public static interface QueryCallback<T> {
		public T deal(Document doc);
	}

	public static interface DealCallback {
		public void deal(Document doc);
	}

	public static void main(String[] args) {
		try {
			MongoOptClient.insert("test", new Document("_id", 2));
		} catch (Exception e1) {
		}
		//System.out.println("#######");
		try {
			MongoOptClient.insert("test", new Document("_id", 2));
		} catch (Exception e) {
		}
		//System.out.println("#######");
	}
}
