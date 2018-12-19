package cn.iecas.store.hbase.client;


import java.math.BigDecimal;

import org.apache.hadoop.hbase.util.Bytes;


public class BytesUtils extends Bytes {
	
	public static byte[] toBytes(Object value){
		byte[] b = null;
		
		if(value instanceof String){
			b = Bytes.toBytes(String.valueOf(value));
		}else if(value instanceof Integer){
			b = Bytes.toBytes((int)value);
		}else if(value instanceof Long){
			b = Bytes.toBytes((long)value);
		}else if(value instanceof Short){
			b = Bytes.toBytes((short)value);
		}else if(value instanceof Double){
			b = Bytes.toBytes((double)value);
		}else if(value instanceof Float){
			b = Bytes.toBytes((float)value);
		}else if(value instanceof BigDecimal){
			b = Bytes.toBytes(((BigDecimal)value).doubleValue());
		}else if(value instanceof Boolean){
			b = Bytes.toBytes((boolean)value);
		}
		return b;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T toValue(byte[] b, Class<T> returnClazz){
		Object o = null;
		if(returnClazz.equals(String.class)){
			if (b != null) {
				o = Bytes.toString(b);
			} else {
				return null;
			}
		}else if(returnClazz.equals(Integer.class) || returnClazz.equals(int.class)){
			if (b != null) {
				o = Bytes.toInt(b);
			} else {
				o = 0;
			}
		}else if(returnClazz.equals(Long.class) || returnClazz.equals(long.class)){
			if (b != null) {
				o = Bytes.toLong(b);
			} else {
				o = 0l;
			}
		}else if(returnClazz.equals(Short.class) || returnClazz.equals(short.class)){
			if (b != null) {
				o = Bytes.toShort(b);
			} else {
				o = 0;
			}
		}else if(returnClazz.equals(Double.class) || returnClazz.equals(double.class)){
			if (b != null) {
				o = Bytes.toDouble(b);
			} else {
				o = 0.0d;
			}
		}else if(returnClazz.equals(Float.class) || returnClazz.equals(float.class)){
			if (b != null) {
				o = Bytes.toFloat(b);
			} else {
				o = 0;
			}
		}else if(returnClazz.equals(BigDecimal.class)){
			if (b != null) {
				o = Bytes.toBigDecimal(b);
			} else {
				o = 0;
			}
		}else if(returnClazz.equals(Boolean.class) || returnClazz.equals(boolean.class)){
			if (b != null) {
				o = Bytes.toBoolean(b);
			} else {
				o = false;
			}
		}else {
			return null;
		}
		
		return (T) o;
	}
	
	public static void main(String[] args) {
		
		
		int a = BytesUtils.toValue(null, Integer.class);
		
		//System.out.println(a);
	}

}
