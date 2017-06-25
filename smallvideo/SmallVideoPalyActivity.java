package com.edgelesschat.smallvideo;

import java.io.File;
import java.io.IOException;

import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.edgelesschat.R;
import com.edgelesschat.animation.AnimationMyStickers;
import com.edgelesschat.global.MD5;
import com.edgelesschat.smallvideo.camara.Log;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class SmallVideoPalyActivity extends Activity{

	private String path;
	private int sendstate;
	private String videoUrl;
	private int type;//0是自己   1是别人
	private SurfaceView show_surfaceview;
	private SurfaceHolder surfaceHolder;
	private LinearLayout show_loadlayout;
	private FrameLayout framelayout;
//	private ProgressBar show_progressbar;
	private MediaPlayer player;
	private String basepath;
	private String loadpath;
//	private static int GUIUPDATEIDENTIFIER=0;
	
	Handler myHandler = new Handler() {  
		public void handleMessage(Message msg) { 
			switch (msg.what) {   
			case 0: 
				showVideo(loadpath); //有效果
				showToast();
				break; 
				}   
		}   
	};  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.small_video_play_activity);
		init();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(player!=null){
			if (player.isPlaying()) {
				player.stop();
	        }
			player.release();
		}
	}
	
	private void showToast(){
//		Toast.makeText(this,"缓存完成", Toast.LENGTH_SHORT).show();
	}
	
	private void init() {
		
		basepath=AnimationMyStickers.BASE_PATH+"videocache"+File.separator;
		framelayout=(FrameLayout)findViewById(R.id.framelayout);
		show_surfaceview=(SurfaceView)findViewById(R.id.show_surfaceview);
		show_loadlayout=(LinearLayout)findViewById(R.id.show_loadlayout);
//		show_progressbar=(ProgressBar)findViewById(R.id.show_progressbar);
		
		framelayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();//点击消失
			}
		});
		
		videoUrl = getIntent().getStringExtra("videoUrl");
		type=getIntent().getIntExtra("type", 1);
		sendstate=getIntent().getIntExtra("sendstate", 1);
		
		if (sendstate!=1) {
			path = getIntent().getStringExtra("videoPath");
			showVideo(path);
		} else {
			loadVideo(videoUrl);
		}
	}

	@SuppressWarnings("deprecation")
	private void showVideo(final String videoPath){
		
		show_loadlayout.setVisibility(View.GONE);
		show_surfaceview.setVisibility(View.VISIBLE);
		
		surfaceHolder = show_surfaceview.getHolder();
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);//AudioManager.STREAM_MUSIC 需要声音的参数  本类不需要
                player.setDisplay(surfaceHolder);
                try {
                    player.setDataSource(videoPath);
                    player.prepare();
                    player.start();
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							//循环播放
							mp.seekTo(0);
                            mp.start();
						}
					});
                } catch (IOException e) {
                    e.printStackTrace();
                }
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
			}
		});
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		
//		show_videoview.setVideoPath(videoPath);
//		show_videoview.setMediaController(null);
//		show_videoview.start();
	}
	
	private void loadVideo(final String videoUrl){
		String fileName = new MD5().getMD5ofStr(videoUrl);
		Log.e("name", fileName);
		fileName += ".mp4";
		basepath = AnimationMyStickers.BASE_PATH+"videocache"+File.separator;
		loadpath = basepath + fileName;
		File file = new File(loadpath);
		// /storage/emulated/0/EdgelessChat/cache/7443339B9A335C86F6EE1C49C502B050.cache
		// /storage/emulated/0/EdgelessChat/cache/F07D90D77AB5CE980158FFC27D33FC45.mp4
		
		if(!file.exists()){
			//未缓存
			File saveDir = new File(basepath);  
	        if(!saveDir.exists())
	        {  
	            saveDir.mkdir();  
	        } 
			loadSaveVideo(videoUrl,loadpath);
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					new SmallVideoLoadDwon().downloadfile(videoUrl);
//					//通知更新
//					Message message = new Message();   
//                    message.what = 0;   
//                    myHandler.sendMessage(message); 
//				}
//			}).start();
		}else{
			//已经缓存
			showVideo(loadpath);
		}
		
	}
	
	private void loadSaveVideo(String strUrl,final String filePath){
	
//		String savePath=AnimationMyStickers.BASE_PATH+"videocache"+File.separator;
//		String fileName= new MD5().getMD5ofStr(strUrl) + ".mp4";
//        File file = new File(savePath+fileName); 
		
		RequestParams requestParams = new RequestParams(strUrl);
		requestParams.setSaveFilePath(filePath);
		x.http().get(requestParams, new CommonCallback<File>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				File file =new File(filePath);
				file.delete();
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				
			}

			@Override
			public void onFinished() {
				showVideo(loadpath); 
			}

			@Override
			public void onSuccess(File arg0) {
				
				show_loadlayout.setVisibility(View.GONE);
				show_surfaceview.setVisibility(View.VISIBLE);
			}
			
		});
	}
	
	
}
