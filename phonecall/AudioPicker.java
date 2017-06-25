package com.edgelesschat.phonecall;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

public class AudioPicker {

	private AudioRecord audioRecord;// 录音对象
	private int frequence = 8000;// 采样率 8000
	private int channelInConfig = AudioFormat.CHANNEL_IN_MONO;// 定义采样通道
	private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;// 定义音频编码（16位）
	private short[] buffer = null;// 录制的缓冲数组
	private boolean isRecording = false;
	private Context mContext;
	// private AudioTransimissionSession sessionForTransmission = null;
	private AudioPlayer mAudioPlayer = null;
	private String strOtherIPAddr = "";
	private int nOtherPort = 0;
	private int nMyPort = 0;
	private Speex speex;
	private final int AUDIO_FRAME_SIZE = 320;
	private short[] mAudioRecordData;
	private short[] mAudioTrackData;
	private UdpSocket mUdpSocket = null;
	private Thread sendThread;

	// 介入Speek
	private void startTransmission() {
		sendThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// sessionForTransmission = new
					// AudioTransimissionSession(mAudioPlayer, nMyPort,
					// strOtherIPAddr,
					// nOtherPort);
					// this.mUdpSocket = udpSocket;
					Log.d("LiaoTT", "strOtherIPAddr=" + strOtherIPAddr + " nOtherPort=" + nOtherPort);
					mUdpSocket.setTarget(mAudioPlayer, strOtherIPAddr, nOtherPort);
					// mUdpSocket.startTransfer(nMyPort);
					// mUdpSocket.startTransferCommand(nMyPort);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("RTP发送数据异常:" + e);
				}
			}
		});
		sendThread.start();
	}

	public void init(Context context, int nMyPort, String strOtherIPAddr, int nOtherPort, UdpSocket mUdpSocket) {
		// 初始化speex
		speex = new Speex();
		speex.init();
		try {
			// 最小缓冲区
			int recordBufferSizeInBytes = AudioRecord.getMinBufferSize(frequence, channelInConfig,
					audioEncoding);

			recordBufferSizeInBytes = AUDIO_FRAME_SIZE;
			Log.d("TAG", "recordBufferSizeInBytes=" + recordBufferSizeInBytes);

			mAudioRecordData = new short[recordBufferSizeInBytes];

			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequence, channelInConfig,
					audioEncoding, recordBufferSizeInBytes);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		// buffer = new short[recordBufferSizeInBytes/2];

		mAudioPlayer = new AudioPlayer();
		mAudioPlayer.init(context);
		mContext = context;

		this.strOtherIPAddr = strOtherIPAddr;
		this.nMyPort = nMyPort;
		this.nOtherPort = nOtherPort;

		this.mUdpSocket = mUdpSocket;
		// 开始传输
		startTransmission();
	}

	public void startRecord() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO); // 线程优先级
		isRecording = true;
		new Thread() {
			public void run() {
				try {
					// 防止某些手机崩溃
					if (audioRecord != null) {
						audioRecord.startRecording();// 开始录制
					}else{
						Toast.makeText(mContext, "语音功能异常", Toast.LENGTH_LONG).show();
						return;
					}
				} catch (IllegalStateException e) {
					e.printStackTrace();
					Log.e("lt", "开始语音异常！");
				}
				Log.d("liaotian", "已进入Recording");
				while (isRecording) {
					// saveFrames(buffer, bufferReadResult);
					/* int result = */
					if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING && audioRecord != null) {
						// int result = audioRecord.read(buffer, 0,
						// buffer.length);
						int sizeInShorts = speex.getFrameSize();
						short[] audioData = new short[sizeInShorts];
						int sizeInBytes = speex.getFrameSize();
						short[] dst = new short[sizeInBytes];
						int result = audioRecord.read(audioData, 0, sizeInShorts);
						// System.arraycopy(源数组, 源数组起始位置, 目标数组, 目标数组起始位置, 长度);
						System.arraycopy(audioData, 0, dst, 0, result);
						byte[] encoded = new byte[sizeInBytes];
						// 编码
						int totleByte = speex.encode(dst, 0, encoded, result);

						// int totleByte = speex.encode(buffer, 0, encoded,
						// 160);// 编码后总字节长度。
						// int totleByte = speex.encode(buffer, 0, encoded,
						// result);// 编码后总字节长度。
						if (totleByte != 0) {
							// sendData(encoded, totleByte);
							// mAudioPlayer.play(encoded, 0, totleByte);
							//	发送
							mUdpSocket.sendPakcet(encoded, totleByte);

							// 编码成功
							Log.d("liaotian", "totleByte=" + totleByte + " encoded.length=" + encoded.length);

							// Log.d("liaotian", "totleByte=" + totleByte + "
							// dst.length=" + dst.length);
							// dos.write(HEADER);//加入自定义头
							// dos.writeInt(totleByte);//有效数据的长度
							// byte[] sendData = new byte[totleByte];
							// //被压缩后的音频数据
							// System.arraycopy(processedData, 0, sendData, 0,
							// totleByte);
							// dos.write(sendData);//保存数据
						} else {
							Log.d("liaotian", "编码失败");
						}

						// mAudioPlayer.play(buffer, 0, result);
						/*
						 * .....result为buffer中录制数据的长度(貌似基本上都是640)。
						 * 剩下就是处理buffer了，是发送出去还是直接播放，这个随便你。
						 */
					}
				}
				// 录制循环结束后，记得关闭录制！！
				if (audioRecord != null) {
					try {
						if (audioRecord != null) {
							audioRecord.stop();
						}
						if (audioRecord != null) {
							audioRecord.release();
							audioRecord = null;
						}
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	/*
	 * public void sendData(byte[] data, int nLength) { if (null !=
	 * sessionForTransmission) {
	 * sessionForTransmission.rtpSession.sendData(data); } }
	 */
	// public void openSession()
	// {
	// InitSession session = new InitSession();
	// test.rtpSession.sendData(data);
	// long teststart = System.currentTimeMillis();
	// String str = "abce abcd abce abce abce abcd abcd abce " +
	// "abcd abce abcd abce abcd abce abcd abce abcd abce " +
	// "abcd abce abcd abce abcd abce abcd abce abcd abce abcd " +
	// "abce abcd abce abcd abce abcd abce abcd abce abcd abce " +
	// "abcd abce abcd abce abcd abce abcd abce abcd abce abcd " +
	// "abce abcd abce abcd abce abcd abce abcd abce abcd abce " +
	// "abcd abce abcd abce abcd abce abcd abce abcd abce abcd " +
	// "abce abcd abce abcd abce abcd abce abcd abce abcd abce " +
	// "abcd abce abcd abce abcd abce abcd abce abcd abce abcd " +
	// "abce abcd abce abcd abce abcd abce abcd abce abcd abce " +
	// "abcd abce abcd abce abcd abce abcd abce abcd abce abcd " +
	// "abce abcd abce abcd abce abcd abce abcd ";
	// byte[] data = str.getBytes();
	// System.out.println(data.length);
	// int i=0;
	// while(i<data.length) {
	// System.out.println("已发送第"+i+"个字节数组："+data);
	//
	// i++;
	// }

	// long testend = System.currentTimeMillis();

	// System.out.println("发送用时：" + (testend - teststart));
	// System.out.println("结束时间：" + testend);
	// System.out.println("开始时间：" + teststart);
	// }
	/*
	 * public void receiveData() { ReceiveData receive = new ReceiveData(); }
	 */
	public void stopRecord() {
		isRecording = false;
	}
}
