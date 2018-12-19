package cn.iecas.general.utils;

import java.io.*;
import java.math.BigDecimal;

/**
 * 数值转换工具类
 * @author hhm
 *
 */
public class ConvertUtils {

	public static class Byte {
		/**
		 * 将一个4byte数组转换成32为的int 高位在后
		 * 
		 * @param buf
		 *            字节数组
		 * @param pos
		 *            开始位置
		 * @return
		 */
		public static long unsigned4BytesToInt32(byte[] buf, int pos) {
			if(buf==null || buf.length<4){
				return -1;
			}
			
			long rs = 0;
			int index = pos;
			int a = (0x000000ff & ((int) buf[index + 3]));
			int b = (0x000000ff & ((int) buf[index + 2]));
			int c = (0x000000ff & ((int) buf[index + 1]));
			int d = (0x000000ff & ((int) buf[index]));
			rs = ((long) (a << 24 | b << 16 | c << 8 | d)) & 0xffffffffL;
			return rs;
		}

		/**
		 * 将一个4byte数组转换成32为的int 高位在后
		 * 
		 * @param buf
		 *            字节数组
		 * @return
		 */
		public static long unsigned4BytesToInt32(byte[] buf) {
			return unsigned4BytesToInt32(buf, 0);
		}

		/**
		 * 将一个2byte数组转换成16为的int
		 * 
		 * @param buf
		 *            字节数组
		 * @param pos
		 *            开始位置
		 * @return
		 */
		public static int unsigned4BytesToInt16(byte[] buf, int pos) {
			int rs = 0;
			int index = pos;
			int a = (0x000000ff & ((int) buf[index + 1]));
			int b = (0x000000ff & ((int) buf[index]));
			rs = ((int) (a << 8 | b)) & 0x0000ffff;
			return rs;
		}
		
		public static byte[] unsignedInt32To4Bytes(int n){
			byte[] b = new byte[4];
			for (int i = 0; i < 4 ; i++) {
				b[i] = (byte)(n>>>(24-i*8));
			}
			b[0] = (byte)(n>>>0);
			b[1] = (byte)(n>>>8);
			b[2] = (byte)(n>>>16);
			b[3] = (byte)(n>>>24);
			
			return b;
		}

		/**
		 * 字节数组转换为String，并使用gbk编码方式
		 * 
		 * @param buf
		 *            字节数组
		 * @param start
		 *            开始位置
		 * @param length
		 *            取长度
		 * @return
		 * @throws UnsupportedEncodingException
		 */
		public static String bytesToString(byte[] buf, int start, int length)
				throws UnsupportedEncodingException {

			byte[] tmp = new byte[length];

			int i = 0;
			for (; i < length; i++) {
				if (buf[start + i] == 0x00) {
					break;
				}
				tmp[i] = buf[start + i];
			}
			byte[] tt = new byte[i];
			System.arraycopy(tmp, 0, tt, 0, i);
			String str = new String(tt, "gbk");

			return str.trim();
		}
		
		/**
		 * 获取字节数组十六进制码字符串，并以空格分隔
		 * @param buf
		 * @return
		 */
		public static String getHexString(byte[] buf){
			String str = "";
			for(byte b : buf){
				str += String.format("%02X", b)+" ";
			}
			return str;
		}
		
		public static byte[] toBytes(Object obj) throws IOException{
			if(obj==null){
				return null;
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			
			return baos.toByteArray();
		}
		
		public static Object bytesToObject(byte[] data){
			Object obj = null;
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try {
				bais = new ByteArrayInputStream(data);
				ois = new ObjectInputStream(bais);
				obj = ois.readObject();
			} catch (ClassNotFoundException e) {
			} catch (IOException e) {
			} finally {
				try {
					ois.close();
				} catch (IOException e) {
				}
			}
			
			return obj;
		}
		
		public static <T> T bytesToObject(byte[] data, Class<T> clazz) {
			T obj = clazz.cast(bytesToObject(data)) ;
			return obj;
		}

	}
	
	public static class Double {
		/**
		 * 格式化double类型小数位数,四舍五入
		 * @param val  double值
		 * @param scale  最大小数位数
		 * @return
		 */
		public static double formatScale(double val, int scale){
			
			try {
				BigDecimal bg = new BigDecimal(val);
				return bg.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
			} catch (Exception e) {
				return val;
			}
		}
		
		public static String formatToPercent(double val){
			String s = formatScale(val*100, 2)+"%";
			return s;
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		
		System.out.println(ConvertUtils.Byte.getHexString(ConvertUtils.Byte.unsignedInt32To4Bytes(111111111)));
		System.out.println(ConvertUtils.Byte.unsigned4BytesToInt32(ConvertUtils.Byte.unsignedInt32To4Bytes(111111111)));
		System.out.println(ConvertUtils.Byte.unsigned4BytesToInt32(new byte[]{0x0f,0x00,0x00,0x00}));
		
	}

}
