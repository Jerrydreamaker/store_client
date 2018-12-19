package cn.iecas.store.mongo.client;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.regex.Pattern;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.json.JsonReader;

import com.mongodb.client.model.Filters;

public class MongoFilterUtils {
	
	/**
	 * 拼装between条件
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static <TItem> Bson keyBetween(String key, final TItem start, final TItem end){
		return Filters.and(Filters.gte(key, start),Filters.lte(key, end));
	}
	
	/**
	 * 拼装like条件  前后模糊匹配
	 * @param cond
	 * @param key
	 * @param val
	 * @return
	 */
	public static Bson keyLike(String key, String val){
		Pattern pattern = Pattern.compile("^.*" + val + ".*$",
				Pattern.CASE_INSENSITIVE);

		return Filters.regex(key, pattern);
	}
	
	
	/**
	 * 以一个点为圆点，以r为半径(公里) 查询该圆圈内的目标点信息
	 * @param cond
	 * @param circlePoints 圆点坐标
	 * @param r 半径   单位:公里
	 * @return
	 */
	public static Bson keyCenter(String key,double[] circlePoints,double r){
		return Filters.geoWithinCenter(key, circlePoints[0], circlePoints[1], r/111.0);
	}
	/**
	 * 以多边形的坐标点查询 多边形内的目标点信息
	 * @param key 字段名
	 * @param polygonPoints 多边形 的多个点包括经纬度坐标
	 * @return
	 */
	public static Bson keyPolygon(String key,List<List<Double>> polygonPoints){
		return Filters.geoWithinPolygon(key, polygonPoints);
	}
	/**
	 * 包含关系
	 * @param key
	 * @param vals
	 * @return
	 */
	@SafeVarargs
	public static <TItem> Bson keyIn(String key, final TItem... vals){
		return Filters.in(key, vals);
	}
	/**
	 * 不包含关系
	 * @param key
	 * @param vals
	 * @return
	 */
	public static Bson keyNotIn(String key, Object... vals){
		return Filters.nin(key, vals);
	}
	
	/**
	 * 大于等于
	 * @param key
	 * @param vals
	 * @return
	 */
	public static Bson keyGte(String key,Object... vals){
		return Filters.gte(key, vals);
	}
	/**
	 * 小于等于
	 * @param key
	 * @param vals
	 * @return
	 */
	public static Bson keyLte(String key,Object... vals){
		return Filters.lte(key, vals);
	}
	/**
	 * 等于
	 * @param key
	 * @param vals
	 * @return
	 */
	public static Bson keyEQ(String key,Object... vals){
		return Filters.eq(key, vals);
	}
	/**
	 * 不等于
	 * @param key
	 * @param vals
	 * @return
	 */
	public static Bson keyNE(String key,Object... vals){
		return Filters.ne(key, vals);
	}
	/**
	 * bson转换为json格式字符串
	 * @param bson
	 * @return
	 */
	public static String bsonToString(Bson bson){
		//编码解码器
		CodecRegistry documentRegistry = CodecRegistries.fromProviders(asList(new ValueCodecProvider(),new BsonValueCodecProvider(),
	            new DocumentCodecProvider()));
		return bson.toBsonDocument(Document.class,documentRegistry).toJson();
	}
	/**
	 * 将带有$条件的转化成Bson
	 * @param json
	 * @return
	 */
	public static Bson jsonToBson(String json){
		BsonDocument bsonDmt= new BsonDocumentCodec().decode(new JsonReader(json), DecoderContext.builder().build());
		return bsonDmt;
		
	}
	/**
	 * 将带有$条件的转化成Bson
	 * @param json
	 * @return
	 */
	public static Bson stringToBson(String filters){
		return jsonToBson(filters);
	}
	
	/**
	 * bson转换为json格式字符串
	 * @param bson
	 * @return
	 */
	public static String filterToString(Bson filter){
		return bsonToString(filter);
	}
}
