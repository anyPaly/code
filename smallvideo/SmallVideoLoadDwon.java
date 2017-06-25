package com.edgelesschat.smallvideo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.edgelesschat.animation.AnimationMyStickers;
import com.edgelesschat.global.MD5;

public class SmallVideoLoadDwon {

	
	
	public void downLoadFile(String strUrl){
		
		
		
	}
	
	public void downloadfile(String strURL)
	{
        try 
        {
        	String fileName = new MD5().getMD5ofStr(strURL);
    		fileName += ".mp4";
    		//String strFilePath = strCacheDir + fileName;
            // 创建一个Http连接
            downLoadFromUrl(strURL,fileName,AnimationMyStickers.BASE_PATH+"videocache"+File.separator);
        } 
        catch (Exception e) 
        {
              e.printStackTrace();
        }
	}
	
	public void  downLoadFromUrl(String urlStr, String fileName,String savePath) throws IOException
	{  
        URL url = new URL(urlStr);    
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
                //设置超时间为3秒  
        conn.setConnectTimeout(100*1000);  
        //防止屏蔽程序抓取而返回403错误  
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
  
        //得到输入流  
        InputStream inputStream = conn.getInputStream();    
        //获取自己数组  
        byte[] getData = readInputStreamFromBuffer(inputStream);      
  
        //文件保存位置  
        File saveDir = new File(savePath);  
        if(!saveDir.exists())
        {  
            saveDir.mkdir();  
        }  
        File file = new File(savePath+fileName); 
        
        FileOutputStream fos = new FileOutputStream(file);       
        fos.write(getData);   
        if(fos!=null)
        {  
            fos.close();    
        }  
    } 

	public static  byte[] readInputStreamFromBuffer(InputStream inputStream) throws IOException 
	{    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
        while((len = inputStream.read(buffer)) != -1) 
        { 
            bos.write(buffer, 0, len);    
        }
        bos.close();
        return bos.toByteArray();    
	}  
	
}
