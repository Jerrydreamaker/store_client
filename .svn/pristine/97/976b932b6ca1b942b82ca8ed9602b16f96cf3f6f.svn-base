package cn.iecas.store.zmq.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import cn.iecas.general.utils.prop.PropsUtils;

/**
 * zmq接收转发线程基类
 * 
  * 提供接收publish的zmq数据,并转发publish数据 不转发数据的时候,构造方法不需要提供转发端口
 * 
 * @author hhm
 * 
 */
public abstract class ZmqSubPubThread implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ZmqSubPubThread.class);

	/**
	 * zmq 启动线程数
	 */
	private int zmqThreadCount = PropsUtils.getInt("zmq", "zmq_thread_count", 1);
	/**
	 * zmq 接收端口
	 */
	private int zmqRecvPort = PropsUtils.getInt("zmq", "zmq_sub_server_port", 0);
	/**
	 * zmq 接收服务端ip
	 */
	private String zmqRecvIP = PropsUtils.getString("zmq", "zmq_sub_server_ip", null);
	/**
	 * zmq 数据广播端口
	 */
	private int zmqSendPort = PropsUtils.getInt("zmq", "zmq_pub_server_port", 0);

	private ZMQ.Context context = null;
	private ZMQ.Socket subSock = null;
	private ZMQ.Socket pubSock = null;

	public ZmqSubPubThread() {
		initZMQ();
	}

	public ZmqSubPubThread(String zmqRecvIP, int zmqRecvPort, int zmqSendPort) {
		this.zmqRecvPort = zmqRecvPort;
		this.zmqRecvIP = zmqRecvIP;
		this.zmqSendPort = zmqSendPort;
		initZMQ();
	}

	/**
	 * 只提供接收,不转发数据
	 * 
	 * @param zmqRecvIP
	 * @param zmqRecvPort
	 */
	public ZmqSubPubThread(String zmqRecvIP, int zmqRecvPort) {
		this(zmqRecvIP, zmqRecvPort, 0);
	}

	/**
	 * 初始化zmq对象
	 */
	private void initZMQ() {

		if (zmqRecvIP == null || "".equals(zmqRecvIP)) {
			logger.error("\"zmq_sub_server_ip\" property is null! Set in file \"zmq.properties\"");
			throw new RuntimeException("\"zmq_sub_server_ip\" property is null! Set in file \"zmq.properties\"");
		}
		if (zmqRecvPort == 0) {
			logger.error("\"zmq_sub_server_port\" property is null! Set in file \"zmq.properties\"");
			throw new RuntimeException("\"zmq_sub_server_port\" property is null! Set in file \"zmq.properties\"");
		}

		context = ZMQ.context(zmqThreadCount);
		subSock = context.socket(ZMQ.SUB);
		String connUri = "tcp://" + zmqRecvIP + ":" + zmqRecvPort;
		
		subSock.connect(connUri);
		//订阅
		subSock.subscribe("".getBytes());
		logger.info("Init ZMQ Receive Socket : [ZMQ Thread Count:" + zmqThreadCount + ",Connect URI:" + connUri + "]");

		if (zmqSendPort != 0) {
			//
			pubSock = context.socket(ZMQ.PUB);
			String bindUri = "tcp://*:" + zmqSendPort;
			pubSock.bind(bindUri);
			logger.info("Init ZMQ Send Socket : [ZMQ Thread Count:" + zmqThreadCount + ",Bind URI:" + bindUri + "]");
		} else {
			logger.info("Only Receive Data And Deal With, Don't Send Data.");
		}
	}

	@Override
	public void run() {
		logger.info("ZmqSwitchThread Start...");

		// 是否向下转发数据
		boolean isSwitch = zmqSendPort != 0;

		while (true) {

			try {

				byte[] recvBuf = subSock.recv(ZMQ.SUB);
				if (recvBuf == null) {
					logger.warn("Receive Data Is Null!");
					continue;
				}

				byte[] sendBuf = dealWith(recvBuf);

				if (isSwitch && sendBuf != null && sendBuf.length != 0) {
					pubSock.send(sendBuf, ZMQ.NOBLOCK);
				}
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}

		}

	}

	/**
	 * 处理接受到的数据,并返回字节数组用于转发
	 * 
	 * @param data
	 * @return 返回发送的用于转发的字节数组
	 */
	public abstract byte[] dealWith(byte[] data);

}