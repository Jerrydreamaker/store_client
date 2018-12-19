package cn.iecas.store.kafka.client;

import java.io.UnsupportedEncodingException;

import cn.iecas.general.utils.ConvertUtils;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

/**
 * kafka消息处理回调函数,抽象方法doDeal(final byte[] msg)处理单条消息
 * @author hhm
 *
 */
public abstract class MessageDealCallback {
	
	/**
	 * 满足多线程处理kafka消息
	 * @param kafkaStream
	 * @return
	 */
	public final Runnable getMessageDealThread(final KafkaStream<byte[], byte[]> kafkaStream){
		return new Runnable() {
			
			public void run() {
				ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
				while(it.hasNext()){
					MessageAndMetadata<byte[], byte[]> item = it.next();

					byte[] msgByte = item.message();
					doDeal(msgByte);
				}
				
			}
		};
	}
	
	/**
	 * kafka消息回调处理方法
	 * @param msg
	 */
	public abstract void doDeal(final byte[] msg); 
	
	/**
	 * 字节数组转换为字符串  默认 utf-8编码
	 * @param msg
	 * @return
	 */
	public String bytesToString(final byte[] msg){
		String m = null;
		try {
			m = new String(msg,"utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		
		return m;
	}
	/**
	 * 字节数组转换为字符串  指定编码
	 * @param msg
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String bytesToString(final byte[] msg, String charset) throws UnsupportedEncodingException{
		return new String(msg,charset);
	}
	/**
	 * 字节数组反序列化指定对象(保证与发送时对象一致性)
	 * @param msg
	 * @param clazz
	 * @return
	 */
	public <T> T bytesToObject(final byte[] msg, Class<T> clazz){
		return ConvertUtils.Byte.bytesToObject(msg, clazz);
	}

}
