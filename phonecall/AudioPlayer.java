package com.edgelesschat.phonecall;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class AudioPlayer {
	private AudioTrack track = null;// 录音文件播放对象
	private int frequence = 8000;// 采样率 8000
	private int channelInConfig = AudioFormat.CHANNEL_OUT_MONO;// 定义采样通道
	private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;// 定义音频编码（16位）
	private int bufferSize = 1000;// 播放缓冲大小
	private Speex speex;
	private final int AUDIO_FRAME_SIZE = 320;
	private Context mContext;
	
	private Timer timer;
	private static final String PHONE_AUTO_TEN_SECONDS_HUNGUP = "phone_auto_ten_seconds_hungup";
	private static final int AUTO_TEN_SECONDS_HUNGUP = 188;
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

	public void init(Context context) {
		this.mContext = context;
		speex = new Speex();
		speex.init();
		// 获取缓冲 大小
		// bufferSize = AudioTrack.getMinBufferSize(frequence, channelInConfig,
		// audioEncoding);
		int trackBufferSizeInBytes = AudioTrack.getMinBufferSize(frequence, channelInConfig,
				audioEncoding);

		trackBufferSizeInBytes = AUDIO_FRAME_SIZE;

		// music模式回音大 STREAM_VOICE_CALL回音较小
		track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, frequence, channelInConfig,
				audioEncoding, trackBufferSizeInBytes, AudioTrack.MODE_STREAM);
		track.setStereoVolume(AudioTrack.getMinVolume(), AudioTrack.getMaxVolume());
		track.play();

		/*
		 * AudioManager audioManager =
		 * (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		 * audioManager.setMicrophoneMute(false);
		 * audioManager.setSpeakerphoneOn(false); // 使用扬声器播放，即使已经插入耳机
		 * audioManager.setMode(AudioManager.STREAM_VOICE_CALL); //
		 * audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);//通话模式。 int
		 * voiceMax =
		 * audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);//
		 * 得到听筒模式的最大值 int voiceOl =
		 * audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);//
		 * 得到听筒模式的当前值 Log.d("lt", "voiceMax="+voiceMax+" voiceOl="+voiceOl);
		 */

	}

	public void play(byte[] audioData, int offsetBuffer, int nSizeBuffer) {
		// speex解码
		int nPackageCount = nSizeBuffer/20;
		for(int i = 0; i < nPackageCount; i++)
		{
			byte [] buffer = new byte[20];
			System.arraycopy(audioData, i * 20, buffer, 0, 20);
			short[] lin = new short[speex.getFrameSize()];
			int size = speex.decode(buffer, lin, 20);
			Log.d("liaotian", "长度 = " + size + " nSizeBuffer=" + i * 20);
			//autoTenSecondsHungUp();
			if (size > 0) {
				/*if (timer != null) {
					timer.cancel();
					timer.purge();
				}*/
				track.write(lin, 0, size);
			}
		}

	}
	// 停止播放并释放
	public void stopPlay() { 
		if (track != null) {
			try {
				track.stop();
				if (track != null) {
					track.release();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// 如果10秒内没有接到用户发送的数据包则挂断
	private void autoTenSecondsHungUp(){
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				Log.d("lt", "-------autoTenSecondHungUp--------");
				Message msg = new Message();
				msg.what = AUTO_TEN_SECONDS_HUNGUP;
				mHandler.sendMessage(msg);
			}
		}, 10000);// 设定指定的时间time,此处为10000毫秒
	}
}
