package com.edgelesschat.phonecall;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class UdpSocket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DatagramSocket m_hSocket = null;
	private byte[] buffer = new byte[1024];
	private DatagramPacket dataPacket = new DatagramPacket(buffer, buffer.length);
	private InetSocketAddress inetOtherIPAddr;
	private InetSocketAddress inetOtherIPAddrServer;
	private InetSocketAddress inetServerCentre;
	private String nOhterId;
	private int nMyPort;
	private String strMyId;
	private String strToId;
	
	private AudioPlayer mAudioPlayer;
	private static final String PHONE_RESULT = "phone_result";
	private static final String PHONE_AUTO_TEN_SECONDS_HUNGUP = "phone_auto_ten_seconds_hungup";
	private static final String PHONE_MODE = "phone_mode";
	private static Context mContext;
	private boolean isPlay = false;
	private final static UdpSocket  sl2 = new UdpSocket();
	private static final int AUTO_TEN_SECONDS_HUNGUP = 188;
	
	
	public static final int TRANSFERMODE_AUTO = 0;
	public static final int TRANSFERMODE_CENTRE = 1;
	public static final int TRANSFERMODE_PROXY = 3;
	
	public static final int SENDEVERY_PACKAGECOUNT = 7;  //max=10
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private int nOtherTransMode = TRANSFERMODE_CENTRE;
	private int nTransferMode = TRANSFERMODE_CENTRE;
	private boolean bThreadRunning = false;
	private long nTimeTickcount = 0;
	private byte[] sendBuffer = new byte[300];
	int nbufcount = 0;
	private TcpSocket mProxyTcpSocket = null;
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	private Timer timer;
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case AUTO_TEN_SECONDS_HUNGUP:
				timer.cancel();
				timer.purge();
				Intent intent = new Intent();
				intent.setAction(PHONE_AUTO_TEN_SECONDS_HUNGUP);
				mContext.sendBroadcast(intent);
				break;

			default:
				break;
			}
		};
	};
	
	public static UdpSocket getInstance(Context context){
		mContext = context;
	    return sl2;
	}

	public UdpSocket(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public UdpSocket() {
		// TODO Auto-generated constructor stub
	}

	public boolean createUdpSocket(int nPort) {
		try {
			m_hSocket = new DatagramSocket(nPort);
			return true;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	// 接受数据
	/*public void startTransfer(int nPort) throws InterruptedException {
		// 检测端口是否被占用
		if (!createUdpSocket(nPort)) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Log.d("lt", "已进入收信息");
						m_hSocket.receive(dataPacket);
						mAudioPlayer.play(dataPacket.getData(), 0, dataPacket.getLength());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}*/
	
	public static int byteArrayToInt(byte[] b) 
	{  
	    return   b[0] & 0xFF |
	            (b[1] & 0xFF) << 8 |  
	            (b[2] & 0xFF) << 16 |  
	            (b[3] & 0xFF) << 24;  
	}
	
	public static int byteArrayToShort(byte[] b) 
	{  
	    return   (b[0]&0xFF)|((b[1] & 0xFF) << 8);
	}  
	
	public static void intToByteArray(byte[] bytes, int a, int offset)
	{
		bytes[0 + offset] = (byte)(a&0xFF);
		bytes[1 + offset] = (byte)((a>>8)&0xFF);
		bytes[2 + offset] = (byte)((a>>16)&0xFF);
		bytes[3 + offset] = (byte)((a>>24)&0xFF);
	}
	
	public static void shortToByteArray(byte[] bytes, int a, int offset)
	{
		bytes[0 + offset] = (byte)(a&0xFF);
		bytes[1 + offset] = (byte)((a>>8)&0xFF);
	}
	
	public static void uintToByteArray(byte[] bytes, long a, int offset)
	{
		bytes[0 + offset] = (byte)(a&0xFF);
		bytes[1 + offset] = (byte)((a>>8)&0xFF);
		bytes[2 + offset] = (byte)((a>>16)&0xFF);
		bytes[3 + offset] = (byte)((a>>24)&0xFF);
	}
	
	private boolean isRecvTimeout()
	{
		long nowTickcount = SystemClock.elapsedRealtime();
		if((nowTickcount - nTimeTickcount) > 5000)
		{
			return true;
		}
		return false;
	}
	
	private void startTimerThread()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				try
				{
					Thread.sleep(5000);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}	
				if(bThreadRunning&&isRecvTimeout())
				{
					setProxyTransferMode();
					try {
						for (int i = 0; i < 3; i++) {
							setTartgetChangeMode(nMyPort, PhoneMainActivity.SERVER_IP_ADRESS, PhoneMainActivity.SERVER_IP_PORT, strMyId, strToId);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		
	}
	
	int receiveFromServer()
	{
		if(dataPacket.getPort() == PhoneMainActivity.SERVER_IP_PORT
				|| dataPacket.getPort() == PhoneMainActivity.SERVER_IP_PORT
				|| dataPacket.getPort() == PhoneMainActivity.SERVER_PROXY_PORT)
		{
			byte data[] = dataPacket.getData(); // 接收的数据
			
			String str = new String(data);
			Intent intent = new Intent();
			intent.setAction(PHONE_RESULT);
			Log.d("lt", ">>>>>>接收数据"+dataPacket.getPort());
			if(str.contains("echo-receive"))//收到receive系统消息
			{
				intent.putExtra("result", str.substring(0, str.lastIndexOf("\n")));
				mContext.sendBroadcast(intent);
				return 2;
			}
			if (str.contains("echo") || str.lastIndexOf("\n") > 0)//有命令
			{
				intent.putExtra("result", str.substring(0, str.lastIndexOf("\n")));
				mContext.sendBroadcast(intent);
				if (str.substring(0, str.lastIndexOf("\n")).indexOf("echo-hungup") != -1 && isPlay) {//收到挂断后做的操作
					initializationMode();
					mAudioPlayer.stopPlay();
					isPlay = false;
				}
				return 1;
			}
		}
		return 0;
	}
	
	int receiveFromUser()
	{
		Log.d("lt", "=====Port====="+dataPacket.getPort());
		if (dataPacket.getPort() != PhoneMainActivity.SERVER_IP_PORT) 
		{
			if (timer != null) {
				timer.cancel();
				timer.purge();
			}
			Log.d("lt", "=====播放====="+dataPacket.getLength());
			if (dataPacket.getLength() > 0) 
			{
				if(null != mAudioPlayer)
				{
					mAudioPlayer.play(dataPacket.getData(), 0, dataPacket.getLength());
				}
			}
			isPlay = true;
			return 1;
		}
		return 0;
	}
	
	public void startTransferCommand(int nPort) throws InterruptedException {
		// 检测端口是否被占用
		if (!createUdpSocket(nPort)) {
			return;
		}
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d("lt", "已进入收信息Command");
				while (true) 
				{
					try 
					{
						Log.d("lt", ">>>>>>收消息"+dataPacket.getPort()+">>>>>>"+new String(dataPacket.getData()));
						m_hSocket.receive(dataPacket);
						//String receive = new String(dataPacket.getData());
						//System.out.println(receive);
						if(dataPacket.getLength() > 0)
						{
							// 自动挂断
							/*if (!bThreadRunning) {
								autoTenSecondsHungUp();
							}
							else{
								if (timer != null) {
									timer.cancel();
									timer.purge();
								}
							}*/
							if(0 != receiveFromUser())  //对方发送过来得
							{
								if (timer != null) {
									timer.cancel();
									timer.purge();
								}
								bThreadRunning = false;
							}
							if(2 == receiveFromServer())  //服务器的
							{
								bThreadRunning = true;
								nTimeTickcount = SystemClock.elapsedRealtime();
								startTimerThread();
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
		// thread.join();
		/*         
		 * if (!TextUtils.isEmpty(str)) { return str; }else { return null; }
		 */
	}
	
	
	public void setOtherProxyMode()
	{
		nOtherTransMode = TRANSFERMODE_PROXY;
	}
	
	// 切换TCP模式
	public void setProxyTransferMode()
	{
		nTransferMode = TRANSFERMODE_PROXY;
		if(mProxyTcpSocket == null)
		{
			mProxyTcpSocket = new TcpSocket();
			mProxyTcpSocket.startProxyThread(strMyId, mAudioPlayer);
		}
	}
	

	public void initializationMode(){//挂断后变成初始化状态
		nTransferMode = TRANSFERMODE_CENTRE;
		if(mProxyTcpSocket != null)
		{
			mProxyTcpSocket.stop();
		}
	}
	
	//更换模式
	public void setTartgetChangeMode(int nMyPort, String strIPAddr, int nPort, String strMyid, String strToid)
			throws IOException {
		inetOtherIPAddrServer = new InetSocketAddress(strIPAddr,nPort);
		String text = "<" + strMyid + ">mode,<" + strToid + ">\r\n";
		byte[] buf = text.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, inetOtherIPAddrServer);
		createUdpSocket(nMyPort);
		m_hSocket.send(packet);
		
		Intent intent = new Intent();
		intent.setAction(PHONE_MODE);
		intent.putExtra("MODE", "TCP");
		mContext.sendBroadcast(intent);
	}
	

	// 设置套接字
	public void setTarget(AudioPlayer audioPlayer, String strIPAddr, int nPort) {
		mAudioPlayer = audioPlayer;
		inetOtherIPAddr = new InetSocketAddress(strIPAddr, nPort);
	}

	// 打电话命令
	public void setTartgetCall(int nMyPort, String strIPAddr, int nPort, String strMyid, String strToid)
			throws IOException {
		this.nMyPort = nMyPort;
		this.strMyId = strMyid;
		this.strToId = strToid;
		
		InetSocketAddress inetOtherIPAddrServer1 = new InetSocketAddress(strIPAddr, /* 服务器地址 */ /* 端口 */nPort);
		String text = "<" + strMyid + ">call,<" + strToid + ">\r\n";
		byte[] buf = text.getBytes();
		// 构造数据报包，用来将长度为 length 的包发送到指定主机上的指定端口号。
		DatagramPacket packet = new DatagramPacket(buf, buf.length, inetOtherIPAddrServer1);
		m_hSocket.send(packet);
	}

	// 心跳命令
	public void setTartgetHeart(int nMyPort, String strIPAddr, int nPort, String strMyid, int onLine)
			throws IOException {
		this.nMyPort = nMyPort;
		this.strMyId = strMyid;
		inetOtherIPAddrServer = new InetSocketAddress(strIPAddr, /* 服务器地址 */ /* 端口 */nPort);
		String text = "<" + strMyid + ">heartbeat,<"+onLine+">\r\n";
		byte[] buf = text.getBytes();
		// 构造数据报包，用来将长度为 length 的包发送到指定主机上的指定端口号。
		DatagramPacket packet = new DatagramPacket(buf, buf.length, inetOtherIPAddrServer);
		m_hSocket.send(packet);
	}
	
	// 挂断命令
	public void setTartgetHungUp(int nMyPort, String strIPAddr, int nPort, String strMyid, String strToid)
			throws IOException {
		inetOtherIPAddrServer = new InetSocketAddress(strIPAddr, /* 服务器地址 */ /* 端口 */nPort);
		String text = "<" + strMyid + ">hungup,<" + strToid + ">\r\n";
		byte[] buf = text.getBytes();
		// 构造数据报包，用来将长度为 length 的包发送到指定主机上的指定端口号。
		DatagramPacket packet = new DatagramPacket(buf, buf.length, inetOtherIPAddrServer);
		createUdpSocket(nMyPort);
		for (int i = 0; i < 3; i++) {
			m_hSocket.send(packet);
		}
	}

	// 接听命令
	public void setTartgetReceive(int nMyPort, String strIPAddr, int nPort, String strMyid, String strToid)
			throws IOException {
		this.nMyPort = nMyPort;
		this.strMyId = strMyid;
		this.strToId = strToid;
		inetOtherIPAddrServer = new InetSocketAddress(strIPAddr, /* 服务器地址 */ /* 端口 */nPort);
		String text = "<" + strMyid + ">receive,<" + strToid + ">\r\n";
		byte[] buf = text.getBytes();
		// 构造数据报包，用来将长度为 length 的包发送到指定主机上的指定端口号。
		DatagramPacket packet = new DatagramPacket(buf, buf.length, inetOtherIPAddrServer);
		m_hSocket.send(packet);
	}

	// 发送数据-正式发送语音数据
	public void sendPakcet(byte[] buf, int nLength) {
		try {
			if(nTransferMode == TRANSFERMODE_AUTO)
			{
				DatagramPacket echo = new DatagramPacket(buf, nLength, inetOtherIPAddr);
				m_hSocket.send(echo);
			}
			else if((nTransferMode == TRANSFERMODE_CENTRE) || (nTransferMode == TRANSFERMODE_PROXY))
				{
					// Toast.makeText(mContext, "正在中转...", Toast.LENGTH_LONG).show();
					Log.d("LiaoTT", ">>>>>>正在中转...");
					// sendBuffer PhoneMainActivity.OTHER_IPADDR;
					inetServerCentre = new InetSocketAddress(PhoneMainActivity.SERVER_IP_ADRESS, PhoneMainActivity.SERVER_CENTRE_PORT);
					int nstart = 10 + nbufcount * 20;
					System.arraycopy(buf, 0, sendBuffer, nstart, 20);
					nbufcount++;
					if(nbufcount >= SENDEVERY_PACKAGECOUNT)
					{
						if(nOtherTransMode == TRANSFERMODE_PROXY)
						{
							long a = 0;
							short b = 0;
							UdpSocket.uintToByteArray(sendBuffer, a, 0);
							UdpSocket.shortToByteArray(sendBuffer, b, 4);
						}
						else
						{
							UdpSocket.uintToByteArray(sendBuffer, PhoneMainActivity.OTHER_IPADDR, 0);
							UdpSocket.shortToByteArray(sendBuffer, PhoneMainActivity.OTHER_PORT, 4);
						}
						UdpSocket.intToByteArray(sendBuffer, Integer.parseInt(strToId) , 6);
						nLength = 10 + nbufcount * 20;
						DatagramPacket echo = new DatagramPacket(sendBuffer, nLength, inetServerCentre);
						m_hSocket.send(echo);
						nbufcount = 0;
					}
				}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 如果30秒内没有接到用户发送的数据包则挂断
	private void autoTenSecondsHungUp(){
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				Log.d("lt", "-------autoTenSecondHungUp--------");
				Message msg = new Message();
				msg.what = AUTO_TEN_SECONDS_HUNGUP;
				mHandler.sendMessage(msg);
			}
		}, 30000);// 设定指定的时间time,此处为10000毫秒
	}
}
