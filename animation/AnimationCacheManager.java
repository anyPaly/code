package com.edgelesschat.animation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

import com.edgelesschat.global.MD5;

public class AnimationCacheManager 
{
	
	public static final String strCacheDir = AnimationMyStickers.BASE_PATH+"cache"+File.separator;
	public static FileInputStream getStream(String url)
	{
		String fileName = new MD5().getMD5ofStr(url);
		fileName += ".cache";
		String strFilePath = strCacheDir + fileName;
		if(isFileExist(strFilePath))
		{
			try 
			{
				FileInputStream stream;
				stream = new FileInputStream(new File(strFilePath));
				return stream;
			} 
			catch (Exception e) {
//				Log.e("error", e.getMessage().toString());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void saveCache(String strurl, InputStream inputStream) throws IOException
	{
		String fileName = new MD5().getMD5ofStr(strurl);
		fileName += ".gif";
		String strFilePath = strCacheDir + fileName;
		FileOutputStream fos;
		File file = new File(strCacheDir);
		if(!file.exists())
		{
			file.mkdirs();
		}
		try 
		{
			File myFile = new File(strFilePath);
			
			myFile.createNewFile();
			fos = new FileOutputStream(strFilePath);
			int len = 0;
			byte[] buf = new byte[1024];
			while((len = inputStream.read(buf)) != -1)
			{
				fos.write(buf, 0, len);
			}
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static boolean isFileExist(String strPath)
	{
		File file = new File(strPath);
		if(file.exists() && !file.isDirectory())
		{
			return true;
		}else{
			return false;
		}
	}
}
