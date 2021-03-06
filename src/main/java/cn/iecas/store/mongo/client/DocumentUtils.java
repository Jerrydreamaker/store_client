package cn.iecas.store.mongo.client;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.iecas.general.utils.mongo.JsonWriter;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;

/**
 * MongoDB操作对象Document转换工具类
 * @author hhm
 *
 */
public class DocumentUtils {
	private static final Logger logger = LoggerFactory.getLogger(DocumentUtils.class);
	
	/**
	 * Document转换为指定Class对象,对象属性与document中key匹配时赋值
	 * @param doc
	 * @param clazz
	 * @return
	 */
	public static <T> T parseDocumentToObject(Document doc, Class<T> clazz){
		T t = null;
		try {
			t = (T) clazz.newInstance();

			Field[] fields = t.getClass().getFields();
			for (Field f : fields) {
				try {
					//忽略final, static修饰符的变量
					if(Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())){
						continue;
					}
					f.setAccessible(true);
					if(doc==null){
						return null;
					}
					if (doc.containsKey(f.getName())) {
						Object o = doc.get(f.getName());
						f.set(t, o);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.debug("parseDocumentToObject() Document : "+doc.toString(), e);
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			t = null;
			logger.debug("parseDocumentToObject() Document : "+doc.toString(), e);
		}
		
		return t;
	}
	
	/**
	 * 对象Object转化为MongoDB对象document
	 * @param obj
	 * @return
	 */
	public static Document parseObjectToDocument(Object obj){
		Document doc = new Document();
		Field[] fields = obj.getClass().getFields();
		for (Field f : fields) {
			try {
				//忽略final, static修饰符的变量
				if(Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())){
					continue;
				}
				f.setAccessible(true);
				doc.put(f.getName(), f.get(obj));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.debug("parseObjectToDocument() Field : "+f.toString(), e);
			}
		}
		
		return doc;
	}
	
	public static Document parseJsonToDocument(String json){
		return Document.parse(json);
	}
	
	public static String parseDocumentToJson(Document doc){
		
		JsonWriter writer = new JsonWriter(new StringWriter());
		DocumentCodec encoder = new DocumentCodec();
        encoder.encode(writer, doc, EncoderContext.builder().isEncodingCollectibleDocument(true).build());
        return writer.getWriter().toString();
		
	}

}
