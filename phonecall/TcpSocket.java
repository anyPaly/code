package com.edgelesschat.phonecall;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpSocket {
	private Socket socket = null;
	private InputStream inputStream = null;
	private OutputStream outputStream = null;
	private AudioPlayer mAudioPlayer = null;
	private String strMyID;
	public void startProxyThread(final String strMyID, AudioPlayer audioPlayer)
	{
		mAudioPlayer = audioPlayer;
		socket = new Socket();
		this.strMyID = strMyID;
		new Thread(new Runnable(){
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try 
				{
					socket.connect(new InetSocketAddress(PhoneMainActivity.SERVER_IP_ADRESS, PhoneMainActivity.SERVER_PROXY_PORT), 3000);
					if(socket.isConnected())
					{
						inputStream = socket.getInputStream();
						outputStream = socket.getOutputStream();
						
						byte []data = new byte[10];
						int nMyId = Integer.parseInt(strMyID);
						UdpSocket.intToByteArray(data, nMyId, 0);
						send(data, 4);
						
						DataInputStream input = new DataInputStream(inputStream);
						byte[] buffer = new byte[300];
						final int MAX_RECVLEN = 200;
						while(true)
						{
							int length = recv(input, buffer, MAX_RECVLEN);
							if(length == MAX_RECVLEN)
							{
								mAudioPlayer.play(buffer, 0, length);
							}
						}
					}
				} 
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					try 
					{
						socket.close();
						if(inputStream != null)
						{
							inputStream.close();
						}
						if(outputStream != null)
						{
							outputStream.close();
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private int recv(DataInputStream input, byte [] buffer, int length) throws IOException
	{
		int nrecvlen = 0;
		int nrestlen = length;
		while(nrestlen > 0)
		{
			int len = input.read(buffer, nrecvlen, nrestlen);
			nrecvlen += len;
			nrestlen -= len;
		}
		return length;
	}
	public void send(byte [] buffer, int nLength)
	{
		try 
		{
			outputStream.write(buffer, 0, nLength);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop(){
		try 
		{
			if (socket != null) {
				socket.close();
			}
			if(inputStream != null)
			{
				inputStream.close();
			}
			if(outputStream != null)
			{
				outputStream.close();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
