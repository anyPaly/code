package com.edgelesschat.phonecall;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.edgelesschat.R;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneMainActivity extends Activity implements OnClickListener, SensorEventListener {

	private static final String PHONE_RESULT = "phone_result";
	private static final String PHONE_MAIN = "phone_main";
	public static final String MESSAGE_RETRANSMISSION_ACTION = "com.edgelsschat.message.retransmission_action";
	private static final String PHONE_AUTO_TEN_SECONDS_HUNGUP = "phone_auto_ten_seconds_hungup";
	private static final String PHONE_MODE = "phone_mode";
	private static final int PHONE_HEARTBEAT = 169;
	private static final int PHONE_RINGUP_MSG = 170;
	private static final int PHONE_HUNGUP_MSG = 171;
	private static final int PHONE_ING_MSG = 172;
	private static final int PHONE_AWSWER_MSG = 173;
	private static final int TIME_START = 174;
	private static final int PHONE_AUTO_HUNGUP = 175;
	private static final int PHONE_OTHER_BUSY = 176;
	private static final int PHONE_CHANGE_NETMODE = 177;

	public static boolean RING_UP_STATE = false;

	// private TextView mSwitch;
	private TextView mHungup;
	private ImageView mHunguping;
	private ImageView mAvatarAnswer, mAvatarCall, mAvatarIng;
	private TextView mNicknameAnswer, mNicknameCall, mNicknameIng;
	private TextView mAnswer;
	private TextView mCancel;
	private TextView mModeI, mModeYou;
	private ImageView mMute;
	private ImageView mVoice;
	private Chronometer mTimer;
	private LinearLayout lin_call, lin_answer, lin_ing;
	private int i = 0;
	private int nMyPort, nOtherPort;
	private String strOtherIPAddr, strMyId, strToId, strNickname, strHeadsmall;
	private boolean isCall = false; // 是否是打电话
	private boolean isMute = false; // 是否打开麦克风
	private boolean isVoice = false; // 是否打开扬声器
	private boolean isSendHeartComand = false; // 是否发送心跳
	private boolean isInCall = false; // 是否有点电话接入
	private boolean isNotAnswer = false; // 是否自动挂断
	private boolean isCallTime = false; // 是否接听
	private boolean isNotGetData = false; // 15秒内没有接收到数据
	private boolean isWakelock = false;	//	是否使用Wakelock
	private UdpSocket mUdpSocket = null;
	public static final String SERVER_IP_ADRESS = "65.41.122.9"; // 服务器IP
	// public static final String SERVER_IP_ADRESS = "192.168.0.118";
	public static final int SERVER_IP_PORT = 5060; // 服务器端口
	public static final int SERVER_CENTRE_PORT = 5070; // 服务器端口
	public static final int SERVER_PROXY_PORT = 5080;

	public static long OTHER_IPADDR = 0;
	public static short OTHER_PORT = 0;

	private DynamicReceiver dynamicReceiver;
	private Timer timer;
	// private long otherIntAddr = 0;
	// private byte[] centrePreHeader = new byte[6];
	private AudioPicker mAudioPicker;
	// private AudioPlayer mAudioPlayer = null;
	private AudioManager mAudioManager;
	private MediaPlayer mp;
	private boolean isBroadcastRegistered = false;

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private SensorManager sensorManager;
	private PowerManager localPowerManager = null;// 电源管理对象
	private WakeLock localWakeLock = null;// 电源锁
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static String longToIP(long longIp) {
		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf((longIp >>> 24)));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}

	// 广播
	class DynamicReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == PHONE_RESULT) {
				final String strResult = intent.getStringExtra("result");
				//打给别人
				if (!TextUtils.isEmpty(strResult)) {
					// 拨打之后收到接听消息
					Log.d("lt", "strResult=" + strResult);
					if (strResult.indexOf("echo-receive") != -1) {
						isCallTime = false;
						String strAll = strResult.substring(strResult.indexOf("<") + 1, strResult.indexOf(">"));//接听
						String[] strUserInfo = strAll.split(",");
						strToId = strUserInfo[0];
						strOtherIPAddr = strUserInfo[1];
						nOtherPort = Integer.parseInt(strUserInfo[2]);
						// String strOhterIp =
						// longToIP(Integer.parseInt(strUserInfo[3]));
						if (strUserInfo.length > 3) {
							PhoneMainActivity.OTHER_IPADDR = Long.parseLong(strUserInfo[3]);
							PhoneMainActivity.OTHER_PORT = (short) nOtherPort;
						}
						Log.d("LiaoTT", "strOtherIPAddr=" + strOtherIPAddr + " nOtherPort" + nOtherPort);
						mAudioPicker.init(PhoneMainActivity.this, nMyPort, strOtherIPAddr, nOtherPort, mUdpSocket); // 初始化
						mAudioPicker.startRecord();
					}
					// 收到拨打之后的操作
					/*if (strResult.indexOf("echo-ringup") != -1) {
						// 响铃
						isCallTime = false;
						Message msg = new Message();
						msg.what = PHONE_RINGUP_MSG;
						mHandler.sendMessage(msg);
					}*/
					// 收到挂断之后的操作
					if (strResult.indexOf("echo-hungup") != -1) {
						// 挂断
						isCallTime = false;
						Message msg = new Message();
						msg.what = PHONE_HUNGUP_MSG;
						mHandler.sendMessage(msg);
						isSendHeartComand = true;
					}
					// 收到接听之后的操作
					if (strResult.indexOf("echo-receive") != -1) {

						stopRingUpVoice();
						isNotAnswer = true;
						isCallTime = true;
						timer.cancel();
						timer.purge();
						Message msg = new Message();
						msg.what = PHONE_AWSWER_MSG;
						mHandler.sendMessage(msg);
					}
					//别人说忙，服务器返回给我的
					if (strResult.indexOf("echo-call") != -1) {
						String strAll = strResult.substring(strResult.indexOf("<") + 1, strResult.indexOf(">"));
						if (strAll.indexOf("-1") != -1) {
							mHandler.sendEmptyMessage(PHONE_OTHER_BUSY);
						}
					}
					//模式切换
					if (strResult.indexOf("echo-mode") != -1) {
						Log.d("LiaoTT", ">>>>>>MODE");
						mHandler.sendEmptyMessage(PHONE_CHANGE_NETMODE);
					}
				}
			}
			//收到
			if (intent.getAction() == PHONE_MAIN) {
				mUdpSocket = (UdpSocket) intent.getSerializableExtra("UdpSocketJs");
			}
			//自动挂断（15秒没收到数据）
			if (intent.getAction() == PHONE_AUTO_TEN_SECONDS_HUNGUP) {
				isCallTime = false;
				if (!isNotGetData) {
					Message msg = new Message();
					msg.what = PHONE_HUNGUP_MSG;
					mHandler.sendMessage(msg);
					isSendHeartComand = true;
					isNotGetData = true;
				}
			}
			//切换模式之后的界面更改
			if (intent.getAction() == PHONE_MODE) {
				mModeI.setText("自己：TCP模式" + "&" + intent.getStringExtra("MODE"));
			}
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case PHONE_HEARTBEAT:
				sendHeartCommand();
				break;

			case PHONE_RINGUP_MSG:
				showHomeLayout(1);
				palyRingUpVoice();
				Log.d("lt", "=====RINGUP=====");
				break;

			case PHONE_HUNGUP_MSG:
				if (isNotGetData) {
					Toast.makeText(getApplicationContext(), "语音异常,请检查网络。", Toast.LENGTH_LONG).show();
					isNotGetData = false;
				}
				sendHeantCMDoline();
				stopRingUpVoice();
				isSendHeartComand = true;
				stop();
				finish();
				break;
			case PHONE_ING_MSG:
				showHomeLayout(2);
				break;
			case PHONE_AWSWER_MSG:
				showHomeLayout(2);
				stopRingUpVoice();
				mTimer.setBase(SystemClock.elapsedRealtime());// 计时器清零
				int hour = (int) ((SystemClock.elapsedRealtime() - mTimer.getBase()) / 1000 / 60);
				mTimer.setFormat("0" + String.valueOf(hour) + ":%s");
				mTimer.start();
				break;
			case TIME_START:
				// 未使用
				break;
			case PHONE_AUTO_HUNGUP:
				timer.cancel();
				timer.purge();
				sendHeantCMDoline();
				if (!isNotAnswer) {
					hungUpPhone();
					Toast.makeText(getApplicationContext(), "暂时无人接听！", Toast.LENGTH_LONG).show();
					finish();
				}
				break;
			case PHONE_OTHER_BUSY:
				sendHeantCMDoline();
				Toast.makeText(getApplicationContext(), "对方忙，请稍后再拨！", Toast.LENGTH_LONG).show();
				// mAudioPicker.stopRecord();
				finish();
				break;
			case PHONE_CHANGE_NETMODE:
				if (mUdpSocket != null) {
					mUdpSocket.setOtherProxyMode();
					mModeYou.setText("对方：TCP模式");
				}
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.phone_main);
		initSensorEvent();
		autoHungup(); // 30秒未接通自动挂断
		callState(); // 监听电话状态
		getValue(); // 获取参数
		init();
		registerLisener(); // 注册监听
		registerDynamicRec(); // 注册广播
		sendHeartCommand(); // 进入先发送一条心跳
		heartCommand(); // 每10秒发送一条心跳
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
				SensorManager.SENSOR_DELAY_UI);
	}
	
	//	用于关闭屏幕 
	private void initSensorEvent() {
		try {
			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			localPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
			// 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
			localWakeLock = this.localPowerManager.newWakeLock(32, "WakeLock");// 第一个参数为电源锁级别，第二个是日志tag
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void registerDynamicRec() {
		if (!isBroadcastRegistered) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(PHONE_RESULT);
			filter.addAction(PHONE_MAIN);
			filter.addAction(PHONE_AUTO_TEN_SECONDS_HUNGUP);
			filter.addAction(PHONE_MODE);
			dynamicReceiver = new DynamicReceiver();
			registerReceiver(dynamicReceiver, filter);
			isBroadcastRegistered = true;
		}
	}

	private void registerLisener() {
		// mSwitch.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		mHunguping.setOnClickListener(this);
		mHungup.setOnClickListener(this);
		mAnswer.setOnClickListener(this);
		mMute.setOnClickListener(this);
		// mVoice.setOnClickListener(this);
	}

	private void init() {
		// mSwitch = (TextView) findViewById(R.id.btn_switch);
		lin_answer = (LinearLayout) findViewById(R.id.lin_answer);
		lin_call = (LinearLayout) findViewById(R.id.lin_call);
		lin_ing = (LinearLayout) findViewById(R.id.lin_ing);
		mCancel = (TextView) findViewById(R.id.phone_hungup_call_txtbtn);
		mHunguping = (ImageView) findViewById(R.id.phone_hungup_ing_txtbtn);
		mHungup = (TextView) findViewById(R.id.phone_hungup_answer_txtbtn);
		mAnswer = (TextView) findViewById(R.id.phone_answer_answer_txtbtn);
		mMute = (ImageView) findViewById(R.id.phone_mute_ing_txtbtn);
		// mVoice = (ImageView) findViewById(R.id.phone_voice_ing_txtbtn);
		mTimer = (Chronometer) findViewById(R.id.phone_timer_ing);
		mAvatarAnswer = (ImageView) findViewById(R.id.phone_avatar_answer);
		mAvatarCall = (ImageView) findViewById(R.id.phone_avatar_call);
		mAvatarIng = (ImageView) findViewById(R.id.phone_avatar_ing);
		mNicknameAnswer = (TextView) findViewById(R.id.phone_nickname_answer);
		mNicknameCall = (TextView) findViewById(R.id.phone_nickname_call);
		mNicknameIng = (TextView) findViewById(R.id.phone_nickname_ing);

		mModeI = (TextView) findViewById(R.id.mode_i);
		mModeYou = (TextView) findViewById(R.id.mode_you);

		isMute = false;
		isVoice = false;
		isSendHeartComand = false;
		mAudioPicker = new AudioPicker();
		// mAudioPlayer = new AudioPlayer();
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mp = MediaPlayer.create(PhoneMainActivity.this, R.raw.ring); // 创建mediaplayer对象
		// 实例化
		mUdpSocket = UdpSocket.getInstance(PhoneMainActivity.this);
		// 多发送几次命令 收到回调则关闭 保证能够打通
		if (isCall) {//是否自己拨打
			try {
				for (int i = 0; i < 3; i++) {
					if (!isCallTime) {
						mUdpSocket.startTransferCommand(nMyPort);//绑定udp
					} else {
						isCallTime = false;
						break;
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showHomeLayout(0);
			for (int i = 0; i < 3; i++) {
				callPhone();
			}
			palyRingUpVoice();
		} else {
			Message msg = new Message();
			msg.what = PHONE_RINGUP_MSG;
			mHandler.sendMessage(msg);
		}
		setAvatar(strHeadsmall);
		setNickname(strNickname);
		if (!isWifi(this)) {
			Toast.makeText(getApplicationContext(), "您正在处于非WIFI网络环境！", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_switch:
			if (i == 0) {
				showHomeLayout(i);
				i++;
			} else if (i == 1) {
				showHomeLayout(i);
				i++;
			} else if (i == 2) {
				showHomeLayout(i);
				i = 0;
			}
			break;
		case R.id.phone_hungup_call_txtbtn:
			// 拨打时挂断
			stopRingUpVoice();
			hungUpPhone();
			isSendHeartComand = true;
			// MessageCallback();
			break;
		case R.id.phone_hungup_ing_txtbtn:
			// 通话时挂断
			hungUpPhone();
			isSendHeartComand = true;
			// MessageCallback();
			break;
		case R.id.phone_hungup_answer_txtbtn:
			// 拒听
			stopRingUpVoice();
			hungUpPhone();
			isSendHeartComand = true;
			// MessageCallback();
			break;
		case R.id.phone_answer_answer_txtbtn:
			// 接听
			stopRingUpVoice();
			awswerPhone();
			showHomeLayout(2);
			mTimer.setBase(SystemClock.elapsedRealtime());// 计时器清零
			int hour = (int) ((SystemClock.elapsedRealtime() - mTimer.getBase()) / 1000 / 60);
			mTimer.setFormat("0" + String.valueOf(hour) + ":%s");
			mTimer.start();
			break;
		case R.id.phone_mute_ing_txtbtn:
			// 静音
			if (!isMute) {
				isMute = true;
				mMute.setImageResource(R.drawable.phone_mute_pass);
				System.out.println("isMicrophoneMute =" + mAudioManager.isMicrophoneMute());
				mAudioManager.setMicrophoneMute(true);
				Toast.makeText(this, "已关闭麦克风", Toast.LENGTH_LONG).show();
			} else {
				isMute = false;
				mMute.setImageResource(R.drawable.phone_mute);
				System.out.println("isMicrophoneMute =" + mAudioManager.isMicrophoneMute());
				mAudioManager.setMicrophoneMute(false);
				Toast.makeText(this, "已开启麦克风", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.phone_voice_ing_txtbtn:
			// 免提 暂未启用
			if (!isVoice) {
				isVoice = true;
				mVoice.setImageResource(R.drawable.phone_voice_pass);
				Toast.makeText(this, "已关闭扬声器", Toast.LENGTH_LONG).show();
				System.out.println("isSpeakerphoneOn =" + mAudioManager.isSpeakerphoneOn());
				mAudioManager.setSpeakerphoneOn(true);
			} else {
				isVoice = false;
				mVoice.setImageResource(R.drawable.phone_voice);
				Toast.makeText(this, "已开启扬声器", Toast.LENGTH_LONG).show();
				System.out.println("isSpeakerphoneOn =" + mAudioManager.isSpeakerphoneOn());
				mAudioManager.setSpeakerphoneOn(false);
			}
			break;
		default:
			break;
		}
	}

	// 产生随机端口排除服务器的端口
	public int getRandomPort() {
		Random random = new Random();
		int num = 0;
		while (true) {
			num = random.nextInt(60000 - 10000 + 1) + 10000;
			// num = random.nextInt(6000 - 1000 + 1) + 1000;
			if (num != PhoneMainActivity.SERVER_IP_PORT || num != PhoneMainActivity.SERVER_IP_PORT) {
				break;
			}
		}
		Log.d("lt", "port=" + num);
		return num;
	}

	public void callPhone() {
		// 拨打方电话 向服务器请求
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 发送拨打命令
					mUdpSocket.setTartgetCall(nMyPort, SERVER_IP_ADRESS, SERVER_IP_PORT, strMyId, strToId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void hungUpPhone() {
		// 挂断
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mUdpSocket.setTartgetHungUp(nMyPort, SERVER_IP_ADRESS, SERVER_IP_PORT, strMyId, strToId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		isSendHeartComand = true;
		isInCall = true;
		stopRingUpVoice();
		sendHeantCMDoline();
		stop();
		finish();
	}

	public void awswerPhone() {
		// 接听
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mUdpSocket.setTartgetReceive(nMyPort, SERVER_IP_ADRESS, SERVER_IP_PORT, strMyId, strToId);
					// Log.d("lt", "result=" + result);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		stopRingUpVoice();
	}

	private void heartCommand() {
		new Thread(new Thread() {
			@Override
			public void run() {
				while (!isSendHeartComand) {
					// 发送心跳命令
					try {
						Message msg = new Message();
						msg.what = PHONE_HEARTBEAT;
						mHandler.sendMessage(msg);
						sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void sendHeartCommand() {
		// 发送心跳命令
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mUdpSocket.setTartgetHeart(nMyPort, SERVER_IP_ADRESS, SERVER_IP_PORT, strMyId, 1);//1在通话  0不在通话
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void getValue() {
		Intent intent = getIntent();

		// strOtherIPAddr = intent.getStringExtra("strOtherIPAddr");
		strOtherIPAddr = null;
		isCall = intent.getBooleanExtra("isCall", false);
		nOtherPort = 0;
		if (isCall) {
			nMyPort = /* intent.getIntExtra("nMyPort", -1) */getRandomPort();
		} else {
			nMyPort = intent.getIntExtra("MyPort", -1);
			// Bundle bundle = new Bundle();
			// bundle = intent.getBundleExtra("UdpSocket");
			// mUdpSocket = (UdpSocket) bundle.getSerializable("UdpSocket");
		}
		strMyId = intent.getStringExtra("strUid"); // 我的id
		strToId = intent.getStringExtra("strToId"); // 他的id
		strHeadsmall = intent.getStringExtra("headsmall");
		strNickname = intent.getStringExtra("nickname");
	}

	private void palyRingUpVoice() {
		try {
			mp.reset();
			mp = MediaPlayer.create(PhoneMainActivity.this, R.raw.ring);// 重新设置要播放的音频
																		// 、
			mp.setLooping(true);
			mp.start();// 开始播放
		} catch (Exception e) {
			e.printStackTrace();// 输出异常信息
		}
	}

	private void stopRingUpVoice() {
		if (mp != null) {
			mp.stop();
		}
		try {
			if (mp != null) {
				mp.prepare(); // stop后下次重新播放要首先进入prepared状态
				mp.seekTo(0); // 须将播放时间设置到0；这样才能在下次播放是重新开始，否则会继续上次播放
				mp.release();
				mp = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showHomeLayout(int i) {
		if (i == 0) {
			// 拨打界面
			lin_answer.setVisibility(View.GONE);
			lin_call.setVisibility(View.VISIBLE);
			lin_ing.setVisibility(View.GONE);
		} else if (i == 1) {
			// 答应界面
			lin_answer.setVisibility(View.VISIBLE);
			lin_call.setVisibility(View.GONE);
			lin_ing.setVisibility(View.GONE);
		} else if (i == 2) {
			// 通话中界面
			lin_answer.setVisibility(View.GONE);
			lin_call.setVisibility(View.GONE);
			lin_ing.setVisibility(View.VISIBLE);
		}
	}

	public void stop() {
		mAudioPicker.stopRecord();
		// mAudioPlayer.stopPaly();
	}

	private void setAvatar(String url) {
		Picasso.with(getApplicationContext()).load(url).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5")))
				.into(mAvatarAnswer);
		Picasso.with(getApplicationContext()).load(url).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5")))
				.into(mAvatarIng);
		Picasso.with(getApplicationContext()).load(url).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5")))
				.into(mAvatarCall);
	}

	private void setNickname(String name) {
		mNicknameAnswer.setText(name);
		mNicknameCall.setText(name);
		mNicknameIng.setText(name);
	}

	

	// 检测是否为wifi
	private static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	// 不使用
	/*
	 * private void MessageCallback() { Intent intent = new Intent();
	 * intent.setAction(MESSAGE_RETRANSMISSION_ACTION);
	 * 
	 * // 查询我的信息 Login login =
	 * ResearchCommon.getLoginResult(PhoneMainActivity.this); // 添加数据
	 * ArrayList<String> chattypes = new ArrayList<String>(); chattypes.add("" +
	 * 100); ArrayList<String> uids = new ArrayList<String>();
	 * uids.add(strToId); ArrayList<String> names = new ArrayList<String>();
	 * names.add(strNickname); ArrayList<String> headurls = new
	 * ArrayList<String>(); headurls.add(strHeadsmall); // Messageinfo填充
	 * MessageInfo msg = new MessageInfo(); msg.fromid = strMyId; msg.tag =
	 * UUID.randomUUID().toString(); msg.fromname = login.nickname; msg.fromurl
	 * = login.headsmall; msg.toid = strToId; msg.toname = strNickname;
	 * msg.tourl = strHeadsmall; msg.typefile = MessageType.TEXT; msg.content =
	 * "已结束"; msg.typechat = 100; msg.setSendState(2); // 发送状态为0 msg.time =
	 * System.currentTimeMillis(); msg.readState = 1;
	 * 
	 * String strPassword = ChatSecure.getDecryptPassword(msg.fromid); String
	 * strMessage = ChatSecure.encrypt(msg.content, strPassword); msg.bEncrypted
	 * = true; msg.strEncrypttData = strMessage; // 传递
	 * intent.putExtra("chattypes", chattypes); intent.putExtra("uids", uids);
	 * intent.putExtra("names", names); intent.putExtra("headurls", headurls);
	 * intent.putExtra("message", msg); sendBroadcast(intent); }
	 */
	// 判断电话状态
	private void callState() {
		// 获得相应的系统服务
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				/**
				 * 返回电话状态
				 * 
				 * CALL_STATE_IDLE 无任何状态时 CALL_STATE_OFFHOOK 接起电话时
				 * CALL_STATE_RINGING 电话进来时
				 */
				tm.getCallState();
				while (!isInCall) {
					/*
					 * if (tm.getCallState() ==
					 * TelephonyManager.CALL_STATE_IDLE) { //Log.d("lt",
					 * "call state idle..."); } else if (tm.getCallState() ==
					 * TelephonyManager.CALL_STATE_OFFHOOK) { //Log.d("lt",
					 * "call state offhook..."); } else
					 */if (tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
						Log.d("lt", "call state ringing...");
						isInCall = true;
						hungUpPhone();
					}
				}
			}
		}).start();
	}

	// 30秒未接通自动挂断
	private void autoHungup() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				System.out.println("-------设定要指定任务--------");
				Message msg = new Message();
				msg.what = PHONE_AUTO_HUNGUP;
				mHandler.sendMessage(msg);
			}
		}, 30000);// 设定指定的时间time,此处为30000毫秒
	}

	private void sendHeantCMDoline() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < 3; i++) {
						mUdpSocket.setTartgetHeart(nMyPort, SERVER_IP_ADRESS, SERVER_IP_PORT, strMyId, 0);
						Log.d("lt", "=====HeartCMDonline=====");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (localWakeLock != null && isWakelock) {
			localWakeLock.release();//释放电源锁，如果不释放finish这个acitivity后仍然会有自动锁屏的效果，不信可以试一试  
			//localWakeLock = null;
			isWakelock = false;
		}
		localWakeLock = null;
		if(sensorManager != null){  
            sensorManager.unregisterListener(this);//注销传感器监听  
            sensorManager = null;
        }
	}
	
	@SuppressLint("Wakelock")
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopRingUpVoice();
		stop();
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
		isInCall = true;
		sendHeantCMDoline();
		if (localWakeLock != null && isWakelock) {
			localWakeLock.release();//释放电源锁，如果不释放finish这个acitivity后仍然会有自动锁屏的效果，不信可以试一试  
			//localWakeLock = null;
			isWakelock = false;
		}
		localWakeLock = null;
		if(sensorManager != null){  
            sensorManager.unregisterListener(this);//注销传感器监听  
            sensorManager = null;
        }
		if (isBroadcastRegistered) {
			isBroadcastRegistered = false;
			unregisterReceiver(dynamicReceiver);
		}
		this.finish();
	}

	@SuppressLint("Wakelock")
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float[] values = event.values;
		/*
		 * switch (event.sensor.getType()) { case Sensor.TYPE_PROXIMITY: if
		 * (values[0] == 0.0) {// 贴近手机 System.out.println("hands up");
		 * Log.d("lt", "hands up in calling activity"); if
		 * (localWakeLock.isHeld()) { return; } else {
		 * localWakeLock.acquire();// 申请设备电源锁 } } else {// 远离手机
		 * System.out.println("hands moved"); Log.d("lt",
		 * "hands moved in calling activity"); if (localWakeLock.isHeld()) {
		 * return; } else { localWakeLock.setReferenceCounted(false);
		 * localWakeLock.release(); // 释放设备电源锁 } break; } }
		 */
		if (values != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
			System.out.println("its[0]:" + values[0]);

			// 经过测试，当手贴近距离感应器的时候its[0]返回值为0.0，当手离开时返回1.0
			if (values[0] < 8.0) {// 贴近手机

				System.out.println("hands up");
				Log.d("lt", "hands up in calling activity");
				if (localWakeLock.isHeld()) {
					return;
				} else {
					isWakelock = true;
					localWakeLock.acquire();// 申请设备电源锁
				}
			} else {// 远离手机
				System.out.println("hands moved");
				Log.d("lt", "hands moved in calling activity");
				if (localWakeLock.isHeld()) {
					return;
				} else {
					localWakeLock.setReferenceCounted(false);
					localWakeLock.release(); // 释放设备电源锁
					isWakelock = false;
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}
