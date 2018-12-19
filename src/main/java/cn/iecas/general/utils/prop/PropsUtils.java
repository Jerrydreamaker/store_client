package cn.iecas.general.utils.prop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * 配置文件工具类
 * 对.properties配置文件的读取操作
 * 配置文件的加载,根据文件名(不带后缀.properties)加载配置文件
 * 配置文件在加载过一次后,后续的配置读取不做加载
 * 
 * @author hhm
 *
 */
public class PropsUtils {
	private static final Logger _logger = LoggerFactory.getLogger(PropsUtils.class);
	
	//缓存加载的.properties配置文件
	public static Map<String, Properties> propMap = new HashMap<String, Properties>();
	
	/**
	 * 读取配置文件中属性,不存在则返回默认值
	 * @param filename
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getProperty(String filename, String key, String defaultValue){
		String val = defaultValue;
		
		Properties p = loadProperties(filename);
		if(p!=null){
			val = p.getProperty(key, defaultValue);
		}
		
		return val;
	}
	
	public static String getPropertyNullException(String filename, String key){
		String val = getProperty(filename,key);
		if(val==null){
			throw new RuntimeException("\""+key+"\" property is null! Set in file \""+filename+"\".properties");
		}
		return val;
	}
	
	/**
	 * 读取配置文件中属性
	 * @param filename
	 * @param key
	 * @return
	 */
	public static String getProperty(String filename, String key){
		return getProperty(filename, key, null);
	}
	
	public static String getString(String filename, String key){
		return getProperty(filename, key, "");
	}
	public static String getString(String filename, String key, String defValue){
		return getProperty(filename, key, defValue);
	}
	public static int getInt(String filename, String key){
		return (int)getLong(filename,key);
	}
	public static int getInt(String filename, String key, int defValue){
		return (int)getLong(filename,key,defValue);
	}
	public static long getLong(String filename, String key, long defValue){
		String s = getProperty(filename, key, String.valueOf(defValue));
		return Long.parseLong(s);
	}
	public static long getLong(String filename, String key){
		String s = getProperty(filename, key, "0");
		return Long.parseLong(s);
	}
	
	/**
	 * 加载配置文件
	 * @param filename
	 * @return
	 */
	public final static Properties loadProperties(String filename){
		Properties p = null;
		if(propMap.containsKey(filename)){
			p = propMap.get(filename);
		}else{
			try {
				p = new Properties();
				String fname = filename+".properties";
				
				p.load(ResourceLoad.searchConfigFile(fname));
				
				propMap.put(filename, p);
				_logger.info("Properties file '"+fname+"' content : "+p.toString());
			} catch (Exception e) {
				_logger.error(e.getMessage(), e);
			}
		}
		return p;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		System.out.println(PropsUtils.getProperty("jdbc", "abc","111"));
		System.out.println(PropsUtils.getProperty("jdbc", "url","111"));
		System.out.println(PropsUtils.getProperty("log4j", "url","111"));
		
		int s = PropsUtils.getInt("log4j", "url");
		s = 1212121;
		
		System.out.println(s);
	}
}
