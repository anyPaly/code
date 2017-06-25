package com.edgelesschat.animation;

import java.io.Serializable;

import com.edgelesschat.org.json.JSONException;
import com.edgelesschat.org.json.JSONObject;

public class Animation implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String id;//gif  id（12345）
	public String gif_name;//中文
	public String gif_desc;//描述
	public String gif_cover;//封面url
	public String gif_location;//下载zip地址
	public String gif_path;//
	public long createtime;//创建时间
	public int gif_count;//数目
	public int gif_class;//只有1
	public String gif_header;//英文名
	public String gif_folder = "";//文件在本机地址
	

	public String getGif_folder() {
		return gif_folder;
	}

	public void setGif_folder(String gif_folder) {
		this.gif_folder = gif_folder;
	}

	public String getGif_header() {
		return gif_header;
	}

	public void setGif_header(String gif_header) {
		this.gif_header = gif_header;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGif_name() {
		return gif_name;
	}

	public void setGif_name(String gif_name) {
		this.gif_name = gif_name;
	}

	public String getGif_desc() {
		return gif_desc;
	}

	public void setGif_desc(String gif_desc) {
		this.gif_desc = gif_desc;
	}

	public String getGif_cover() {
		return gif_cover;
	}

	public void setGif_cover(String gif_cover) {
		this.gif_cover = gif_cover;
	}

	public String getGif_location() {
		return gif_location;
	}

	public void setGif_location(String gif_location) {
		this.gif_location = gif_location;
	}

	public String getGif_path() {
		return gif_path;
	}

	public void setGif_path(String gif_path) {
		this.gif_path = gif_path;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public int getGif_count() {
		return gif_count;
	}

	public void setGif_count(int gif_count) {
		this.gif_count = gif_count;
	}

	public int getGif_class() {
		return gif_class;
	}

	public void setGif_class(int gif_class) {
		this.gif_class = gif_class;
	}

	public Animation() {
		super();
	}

	public Animation(JSONObject json) {
		super();
		if (json == null) {
			return;
		}
		try {
			if (!json.isNull("gifid")) {
				id = json.getString("gifid");
			}
			if (!json.isNull("gif_header")) {
				gif_header = json.getString("gif_header");
			}
			if (!json.isNull("gif_name")) {
				gif_name = json.getString("gif_name");
			}
			if (!json.isNull("gif_desc")) {
				gif_desc = json.getString("gif_desc");
			}
			if (!json.isNull("gif_cover")) {
				gif_cover = json.getString("gif_cover");
			}
			if (!json.isNull("gif_location")) {
				gif_location = json.getString("gif_location");
			}
			if (!json.isNull("createtime")) {
				createtime = json.getLong("createtime");
			}
			if (!json.isNull("gif_count")) {
				gif_count = json.getInt("gif_count");
			}
			if (!json.isNull("gif_class")) {
				gif_class = json.getInt("gif_class");
			}
			if (!json.isNull("gif_path")) {
				gif_path = json.getString("gif_path");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
