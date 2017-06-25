package com.edgelesschat.animation;

import java.io.Serializable;

import com.edgelesschat.org.json.JSONException;
import com.edgelesschat.org.json.JSONObject;


public class AnimationViewPagerCover implements Serializable {
	private static final long serialVersionUID = 1L;
	public String gif_cover;

	public AnimationViewPagerCover() {
		super();
	}

	public AnimationViewPagerCover(JSONObject json) {
		super();
		if (json == null) {
			return;
		}
		try {
			if (!json.isNull("gif_cover")) {
				gif_cover = json.getString("gif_cover");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
