package com.edgelesschat.smallvideo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.edgelesschat.ChatMainActivity;
import com.edgelesschat.R;
import com.edgelesschat.RotateImageActivity;
import com.edgelesschat.global.FeatureFunction;
import com.edgelesschat.global.GlobalParam;
import com.edgelesschat.map.BMapApiApp;
import com.edgelesschat.widget.CustomProgressDialog;

import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.util.DisplayMetrics;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class SmallVideoActivityUp extends Activity implements SurfaceHolder.Callback{

//	private Button start;// 开始录制按钮  
//    private Button stop;// 停止录制按钮  
	
	private int camaroType=0;//  1为图片  2为视频
	private int videotime=0;
	private String tempFile;
	private ImageView showpictrue;
	private VideoView showvideoview;
    private MediaRecorder mediarecorder;// 录制视频的类  
    private SurfaceView surfaceview;// 显示视频的控件  
    // 用来显示视频的一个接口，我靠不用还不行，也就是说用mediarecorder录制视频还得给个界面看  
    // 想偷偷录视频的同学可以考虑别的办法。。嗯需要实现这个接口的Callback接口  
    private SurfaceHolder surfaceHolder,show_surfaceHolder,holder; 
    private Camera camera;
    private RecordedButton rb_start;
    private int progress = 0;
    public boolean recordedOver = false;
	private int maxDuration = 10500;
	private ImageView mSubmit;
	private ImageView mBack;
	
	private Camera.Parameters parameters = null;
	Bundle bundle = null; // 声明一个Bundle对象，用来存储数据
	float previewRate = -1f;

	protected CustomProgressDialog mProgressDialog;
	
	private CameraSizeComparator sizeComparator = new CameraSizeComparator();
	
	private Handler mHandler =  new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	        
	        case GlobalParam.SHOW_PROGRESS_DIALOG:
	        	String str = (String)msg.obj;
	        	showProgressDialog(str);
	            break;
	        
	        case GlobalParam.HIDE_PROGRESS_DIALOG:
	        	hideProgressDialog();
	            break;
	       
	        default:
	            break;
	        }
	    
	    }
	};
	
	public  class CameraSizeComparator implements Comparator<Camera.Size>{
		public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			if(lhs.width == rhs.width){
				return 0;
			}
			else if(lhs.width > rhs.width){
				return 1;
			}
			else{
				return -1;
			}
		}

	}
	
	public boolean equalRate(Size s, float rate){
		float r = (float)(s.width)/(float)(s.height);
		if(Math.abs(r - rate) <= 0.03)
		{
			return true;
		}
		else{
			return false;
		}
	}
  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏  
        // 设置横屏显示  
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  
        // 设置竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
        // 选择支持半透明模式,在有surfaceview的activity中使用。  
        getWindow().setFormat(PixelFormat.TRANSLUCENT);  
        setContentView(R.layout.small_video_updata_activity_main);  
        init();  
    }  
  
    @Override
    	protected void onDestroy() {
    		super.onDestroy();
    		if (null != camera) {  
              camera.stopPreview();  
              camera.release();  
              camera = null;  
          }  
    		if (mediarecorder != null) {  
              // 停止录制  
              mediarecorder.stop();  
              // 释放资源  
              mediarecorder.release();  
              mediarecorder = null;  
          }  
    	}
    
    private void init() {
    	tempFile=getIntent().getStringExtra("tempPath");
    	Log.e("past", tempFile);
    	
    	showpictrue=(ImageView) this.findViewById(R.id.showpictrue);
    	showvideoview=(VideoView) this.findViewById(R.id.showvideoview);
        surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview); 
    	
        mSubmit = (ImageView) findViewById(R.id.small_video_submit);//确定
        mSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//发送视频
				ChatMainActivity.setvideotime(videotime);
				Message message = new Message();
				message.what = GlobalParam.SHOW_PROGRESS_DIALOG;
				message.obj = BMapApiApp.getInstance().getResources().getString(R.string.add_more_loading);
				mHandler.sendMessage(message);
				
//				tempFile = (new File(Environment.getExternalStorageDirectory(),"love.mp4")).getAbsolutePath();
//				File file=new File(Environment.getExternalStorageDirectory(),"love.mp4");//文件
				
//				String videoFile=tempFile+".mp4";
//				Log.e("path", videoFile);
				mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
				Intent intent = new Intent();
				if(camaroType==1){
					setResult(ChatMainActivity.REQUEST_GET_PHOTO, intent);
				}else if(camaroType==2){
					setResult(ChatMainActivity.REQUEST_GET_VIDEO, intent);
				}
				SmallVideoActivityUp.this.finish();
			}
		});
        
        mBack = (ImageView) findViewById(R.id.small_video_back);//返回
        mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSubmit.setVisibility(View.INVISIBLE);//确定
                mBack.setVisibility(View.INVISIBLE);//返回
                rb_start.setVisibility(View.VISIBLE);//点击的控件
                showvideoview.setVisibility(View.INVISIBLE);
                showpictrue.setVisibility(View.INVISIBLE);
                surfaceview.setVisibility(View.VISIBLE);
                
                camera.startPreview();//拍完照后，重新开始预览
                //考虑删除图片文件
			}
		});
        
         
        rb_start = (RecordedButton) findViewById(R.id.recordbutton);
        rb_start.setMax(maxDuration);
        
        rb_start.setOnGestureListener(new RecordedButton.OnGestureListener() {
            @Override
            public void onLongClick() {
//                mMediaRecorder.startRecord();
            	camaroType=2;
            	startRecord();
            	recordedOver = false;
                myHandler.sendEmptyMessageDelayed(0, 50);
            	
            }
            @Override
            public void onClick() {
            	camaroType=1;
            	parameters = camera.getParameters();
            	List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
                int index = getPictureSize(supportedPictureSizes);
                parameters.setPictureSize(supportedPictureSizes.get(index).width, supportedPictureSizes.get(index).height);
                camera.setParameters(parameters);
                camera.takePicture(null, null, new MyPictureCallback());

            }
            @Override
            public void onLift() {
            	videotime=(progress*10/maxDuration)+1;
            	stopRecord();
                recordedOver = true;
                progress = 0;
                rb_start.setProgress(progress);
                rb_start.closeButton();
//                videoFinish();
            }
            @Override
            public void onOver() {
            	videotime=(int)((float)progress/maxDuration)*10;
            	stopRecord();
                recordedOver = true;
                progress = 0;
                rb_start.setProgress(progress);
                rb_start.closeButton();
//                videoFinish();
            }
        });
        
        holder = surfaceview.getHolder();// 取得holder  
        holder.addCallback(this); // holder加入回调接口  
        // setType必须设置，要不出错.  
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
    }  
    
    /*获取手机相片的大小*/
    private int getPictureSize(List<Camera.Size> sizes) {
        // 屏幕的宽度
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
//        LogUtil.d(TAG, "screenWidth=" + screenWidth);
        int index = -1;


        for (int i = 0; i < sizes.size(); i++) {
            if (screenWidth == sizes.get(i).width) {
                index = i;
            }
        }
        // 当未找到与手机分辨率相等的数值,取列表中间的分辨率
        if (index == -1) {
            index = sizes.size() / 2;
        }


        return index;
    }

    
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(!recordedOver){
            	progress += 50;
                rb_start.setProgress(progress);
                myHandler.sendEmptyMessageDelayed(0, 50);
//                tv_hint.setVisibility(View.GONE);
            }
        }
    };
    
    void startRecord(){
    	mediarecorder = new MediaRecorder();// 创建mediarecorder对象  
        mediarecorder.setOnInfoListener(new OnInfoListener()
        {
			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				// TODO Auto-generated method stub
				if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) 
				{
					mediarecorder.stop();
					mediarecorder.release();  
					mediarecorder = null;
				}
			}
        });
        // 设置录制视频源为Camera(相机)  
        camera.unlock();
        mediarecorder.setCamera(camera);
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源 
        
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); 
          
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4  
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  
        
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 音频格式
        // 设置录制的视频编码h263 h264  
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        //////////////////////////////////////////////////////////////////
        mediarecorder.setVideoSize(640,480);  
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错  
        mediarecorder.setVideoFrameRate(24);
        mediarecorder.setMaxDuration(10000);  
        mediarecorder.setVideoEncodingBitRate(2*1024*1024);
        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface()); 
        mediarecorder.setOrientationHint(90);
        // 设置视频文件输出的路径  
        mediarecorder.setOutputFile(new File(tempFile+".mp4").getAbsolutePath());  
        try {  
            // 准备录制  
            mediarecorder.prepare();  
            // 开始录制  
            mediarecorder.start();  
        } catch (IllegalStateException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } 
    }
    
    void stopRecord(){
    	if (mediarecorder != null) {
    		try {
    			// 防止视频录制太短而崩溃
	    		mediarecorder.setOnErrorListener(null);
	    		mediarecorder.setOnInfoListener(null);  
	    		mediarecorder.setPreviewDisplay(null);
	            // 停止录制  
	            mediarecorder.stop();  
	    	    }catch (IllegalStateException e) {  
	                // TODO: handle exception  
	                Log.i("Exception", Log.getStackTraceString(e));  
	            }catch (RuntimeException e) {  
	                // TODO: handle exception  
	                Log.i("Exception", Log.getStackTraceString(e));  
	            }catch (Exception e) {  
	                // TODO: handle exception  
	                Log.i("Exception", Log.getStackTraceString(e));  
	            } 
    		if(videotime<=1){
    			Toast.makeText(this, "录制时间太短", Toast.LENGTH_SHORT).show();
    		}else{
            // 释放资源  
            mediarecorder.release();  
            mediarecorder = null;
            mSubmit.setVisibility(View.VISIBLE);//确定
            mBack.setVisibility(View.VISIBLE);//返回
            showpictrue.setVisibility(View.INVISIBLE);
            showvideoview.setVisibility(View.INVISIBLE);
            rb_start.setVisibility(View.INVISIBLE);//点击的控件
    
            showVideo();
    		}
        }  
    }
    
    //显示并播放录制的视频
    public void showVideo() {
    	surfaceview.setVisibility(View.INVISIBLE);
    	showvideoview.setVisibility(View.VISIBLE);
//    	File file =new File
    	Log.e("show", tempFile+".mp4");
    	showvideoview.setVideoPath( tempFile+".mp4");
    	showvideoview.setMediaController(null);
    	showvideoview.start();

    	showvideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {  
    		  
            @Override  
            public void onPrepared(MediaPlayer mp) {  
                mp.start();  
                mp.setLooping(true);  
  
            }  
        });  
  
    	showvideoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
  
                    @Override  
                    public void onCompletion(MediaPlayer mp) {  
//                    	showvideoview.setVideoURI(Uri.parse("file://"+new File(Environment.getExternalStorageDirectory(),"love.mp4").getAbsolutePath()));  
                    	showvideoview.setVideoPath( tempFile+".mp4");
                    	showvideoview.start();  
  
                    }  
                });  
    }
    
//    private void play() {
//    	
//        try {
//                File file=new File(Environment.getExternalStorageDirectory(),"love.mp4");//文件
//                Log.e("异常", "1");
//                mediaPlayer.reset();//重置为初始状态
//                Log.e("异常", "2");
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置音乐流的类型
//                Log.e("异常", "3");
//                mediaPlayer.setDisplay(surfaceview.getHolder());//设置video影片以surfaceviewholder播放
//                Log.e("异常", "4");
//                mediaPlayer.setDataSource(file.getAbsolutePath());//设置路径
//                Log.e("异常", "5");
//                mediaPlayer.prepare();//缓冲
//                Log.e("异常", "6");
//                mediaPlayer.start();//播放
//                Log.e("异常", "7");
//            } catch (Exception e) {
//            	Log.e("异常", "异常情况");
//                e.printStackTrace();
//            }
//    }
  
//    class TestVideoListener implements OnClickListener {  
//  
//        @Override  
//        public void onClick(View v) {  
//            if (v == start) {  
//                mediarecorder = new MediaRecorder();// 创建mediarecorder对象  
//                mediarecorder.setOnInfoListener(new OnInfoListener()
//                {
//					@Override
//					public void onInfo(MediaRecorder mr, int what, int extra) {
//						// TODO Auto-generated method stub
//						if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) 
//						{
//							mediarecorder.stop();
//							mediarecorder.release();  
//							mediarecorder = null;
//						}
//					}
//                	
//                });
//                // 设置录制视频源为Camera(相机)  
//                camera.unlock();
//                mediarecorder.setCamera(camera);
//                mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源 
//                
//                mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); 
//                  
//                // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4  
//                mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  
//                
//                mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 音频格式
//                // 设置录制的视频编码h263 h264  
//                mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
//                //////////////////////////////////////////////////////////////////
//                mediarecorder.setVideoSize(640,480);  
//                // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错  
//                mediarecorder.setVideoFrameRate(20);
//                mediarecorder.setMaxDuration(10000);  
//                mediarecorder.setVideoEncodingBitRate(1*1024*1024);
//                mediarecorder.setPreviewDisplay(surfaceHolder.getSurface()); 
//                mediarecorder.setOrientationHint(90);
//                // 设置视频文件输出的路径  
//                mediarecorder.setOutputFile("/sdcard/love.mp4");  
//                try {  
//                    // 准备录制  
//                    mediarecorder.prepare();  
//                    // 开始录制  
//                    mediarecorder.start();  
//                } catch (IllegalStateException e) {  
//                    // TODO Auto-generated catch block  
//                    e.printStackTrace();  
//                } catch (IOException e) {  
//                    // TODO Auto-generated catch block  
//                    e.printStackTrace();  
//                }  
//            }  
//            if (v == stop) {  
//                if (mediarecorder != null) {  
//                    // 停止录制  
//                    mediarecorder.stop();  
//                    // 释放资源  
//                    mediarecorder.release();  
//                    mediarecorder = null;  
//                }  
//            }  
//  
//        }
//  
//    }  
    
//    final class TouchListener implements OnTouchListener {  
//    	  
//        @Override  
//        public boolean onTouch(View v, MotionEvent event) {  
//            if (event.getAction() == KeyEvent.ACTION_DOWN) {  
//            	start.setText("已经按下按钮");  
//            }  
//            if (event.getAction() == KeyEvent.ACTION_UP) {  
//            	start.setText("按钮已经弹起");  
//            }  
//            return true;// 返回true的话，单击事件、长按事件不可以被触发  
//            // return false;  
//        }  
//    }
 // 预览
    private void initCamera() { 
    	parameters = camera.getParameters();//获取camera的parameter实例  
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();//获取所有支持的camera尺寸  
        Camera.Size optionSize = getOptimalPreviewSize(sizeList, surfaceview.getWidth(), surfaceview.getHeight());//获取一个最为适配的camera.size  
        parameters.setPreviewSize(optionSize.width,optionSize.height);//把camera.size赋值到parameters
        
        
//        List<Size> previewSizes = parameters.getSupportedPreviewSizes();
//        List<Size> pictureSizes = parameters.getSupportedPictureSizes(); 
//        
        previewRate = getScreenRate(this);
//      //设置PictureSize  
        Size pictureSize = getPropPictureSize(parameters.getSupportedPictureSizes(),previewRate, 1280); 
        parameters.setPictureSize(pictureSize.width, pictureSize.height); 
//        //设置PreviewSize
//        Size previewSize = getPropPreviewSize(parameters.getSupportedPreviewSizes(), previewRate, 1280);  
//        parameters.setPreviewSize(previewSize.width, previewSize.height);
        
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        // 闪光灯打开
//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);//把parameters设置给camera  
        camera.startPreview();
        camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
        camera.setDisplayOrientation(90);
        // 将预览旋转90度  
    }
    
    public static float getScreenRate(Context context){  
        Point P = getScreenMetrics(context);  
        float H = P.y;  
        float W = P.x;  
        return (W/H);  
   }
    
    /**
	 * 获取屏幕宽度和高度，单位为px
	 * @param context
	 * @return
	 */
	public static Point getScreenMetrics(Context context){
		DisplayMetrics dm =context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		Log.i("liaotian", "Screen---Width = " + w_screen + " Height = " + h_screen + " densityDpi = " + dm.densityDpi);
		return new Point(w_screen, h_screen);
	}
	
	public  Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth){  
	    Collections.sort(list, sizeComparator);   
	    int i = 0;  
	    for(Size s:list){  
	        if((s.width >= minWidth) && equalRate(s, th)){  
	            Log.i("liaotian", "PreviewSize:w = " + s.width + ",h = " + s.height);  
	            break;  
	        }  
	        i++;  
	    }  
	    if(i == list.size()){  
	        i = 0;//如果没找到，就选最小的size  
	    }  
	    return list.get(i);  
	}
	
	public Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth){  
	    Collections.sort(list, sizeComparator);  
	    int i = 0;  
	    for(Size s:list){ 
	        if((s.width >= minWidth) && equalRate(s, th)){  
	            Log.i("liaotian", "PictureSize : w = " + s.width + ",h = " + s.height);  
	            break;  
	        }  
	        i++;  
	    }  
	    if(i == list.size()){  
	        i = 0;//如果没找到，就选最小的size  
	    }  
	    return list.get(i);  
	}  

	// 预览变形处理
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {  
        final double ASPECT_TOLERANCE = 0.1;  
        double targetRatio = (double) w / h;  
        if (sizes == null) return null;  
  
        Size optimalSize = null;  
        double minDiff = Double.MAX_VALUE;  
  
        int targetHeight = h;  
  
        // Try to find an size match aspect ratio and size  
        for (Size size : sizes) {  
            double ratio = (double) size.width / size.height;  
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;  
            if (Math.abs(size.height - targetHeight) < minDiff) {  
                optimalSize = size;  
                minDiff = Math.abs(size.height - targetHeight);  
            }  
        }  
  
        // Cannot find the one match the aspect ratio, ignore the requirement  
        if (optimalSize == null) {  
            minDiff = Double.MAX_VALUE;  
            for (Size size : sizes) {  
                if (Math.abs(size.height - targetHeight) < minDiff) {  
                    optimalSize = size;  
                    minDiff = Math.abs(size.height - targetHeight);  
                }  
            }  
        }  
        return optimalSize;  
    }

  
    @Override  
    public void surfaceChanged(SurfaceHolder holder, int format, int width,  
            int height) {  
//         将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder  
    	
        surfaceHolder = holder;
        camera.autoFocus(new AutoFocusCallback() {
			
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				// TODO Auto-generated method stub
				initCamera();
				camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
			}
		});
    }  
  
    @Override  
    public void surfaceCreated(SurfaceHolder holder) {  
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder 
    	
//    	surfaceHolder = holder;
//    	Log.e("异常", "1");
//    	MediaPlayer mediaPlayer=new MediaPlayer();
//    	Log.e("异常", "2");
//    	mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//    	Log.e("异常", "3");
//    	mediaPlayer.setDisplay(holder);
////    	mediaPlayer.reset();
//    	Log.e("异常", "4");
//        //设置显示视频显示在SurfaceView上
//            try {
//            	File file=new File(Environment.getExternalStorageDirectory(),"love_1493012079801.mp4");//文件
//            	Log.e("异常", file.getAbsolutePath());
//            	mediaPlayer.setDataSource(file.getAbsolutePath());
//            	Log.e("异常", "6");
////            	mediaPlayer.prepare();
////            	mediaPlayer.start();
//            	Log.e("异常", "7");
//            } catch (Exception e) {
//            	Log.e("异常", "异常");
//                e.printStackTrace();
//            }
    	
    	Log.e("异常", "1");  
        surfaceHolder = holder;
        try {
        	if (camera != null) {
        		freeCameraResource();
        	}
        	  camera = Camera.open(); 
        	  parameters = camera.getParameters();
        	  parameters.setPictureFormat(PixelFormat.JPEG);
        	  parameters.set("jpeg-quality", 85);
        	  camera.setParameters(parameters);
              camera.setPreviewDisplay(surfaceHolder);
              camera.startPreview();
        } catch (Exception e) {  
            if (null != camera) {  
                camera.release();  
                camera = null;  
            }  
            e.printStackTrace();  
            Toast.makeText(SmallVideoActivityUp.this, "启动摄像头失败,请开启摄像头权限", Toast.LENGTH_SHORT).show();  
        } 
    }  
  
    @Override  
    public void surfaceDestroyed(SurfaceHolder holder) {  
        // surfaceDestroyed的时候同时对象设置为null  
//    	Log.e("释放资源", "释放资源");
//        surfaceview = null;  
//        surfaceHolder = null;  
//        mediarecorder = null;  
//        if (null != camera) {  
//            camera.stopPreview();  
//            camera.release();  
//            camera = null;  
//        }  
    } 
    
    /**
     * 释放摄像头资源
     */
    private void freeCameraResource() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.lock();
            camera.release();
            camera = null;
        }
    } 
    
    //照相
    static byte[] picData;
    File file;
    
    private final class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                bundle = new Bundle();
                //   bundle.putByteArray("bytes", data);//将图片字节数据保存在bundle中，实现数据交换
                picData = data;
//                saveToSDCard(data);
                file = new File(tempFile+".jpg");
                //建立输出字节流
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(picData);//用FileOutputStream 的write方法写入字节数组
                fos.close();//为了节省IO流的开销，需要关闭
                
                
                if (bundle == null) {
                    Toast.makeText(getApplicationContext(), "请先拍照",
                            Toast.LENGTH_SHORT).show();
                } else {
//                    Intent intent = new Intent(TakePicActivity.this, ShowPicActivity.class);
//
//                    //  intent.setClass(getApplicationContext(), ShowPicActivity.class);
//                    bundle.putBoolean("isRecord", isRecordState);
//                    bundle.putInt("isPosition", cameraPosition);
//                    bundle.putInt("isScreenConfigChange", isScreenConfigChange);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                	Toast.makeText(getApplicationContext(), "传对象", Toast.LENGTH_SHORT).show();
//                    if (camera != null) {
//                        freeCameraResource();
//                    }
                	//TODO 图片预览，点击取消删除图片文件
                    
                    mSubmit.setVisibility(View.VISIBLE);//确定
                    mBack.setVisibility(View.VISIBLE);//返回
                    showpictrue.setVisibility(View.VISIBLE);
                    showvideoview.setVisibility(View.INVISIBLE);
                    rb_start.setVisibility(View.INVISIBLE);//点击的控件
                    int id=getResourceByReflect(tempFile+".jpg");
                    if(id!=0){
                    	showpictrue.setBackgroundResource(id);
                    }else{
                    	Log.e("aa",id+"" );
                    }
                    
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getResourceByReflect(String imageName){  
        Class drawable  =  R.drawable.class;  
           Field field = null;  
           int r_id=0 ;  
           try {  
               field = drawable.getField(imageName);  
               r_id = field.getInt(field.getName());  
           } catch (Exception e) {  
           }  
           return r_id;  
    }  
    
    public void showProgressDialog(String msg,Context context){
		mProgressDialog = new CustomProgressDialog(this);
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();
	}
	public void showProgressDialog(String msg){
		mProgressDialog = new CustomProgressDialog(this);
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();
	}
	
	public void hideProgressDialog(){
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
    
}
