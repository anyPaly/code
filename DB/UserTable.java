package com.edgelesschat.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.edgelesschat.Entity.Group;
import com.edgelesschat.Entity.Login;
import com.edgelesschat.Entity.MainSearchEntity;
import com.edgelesschat.Entity.OrderMenuItem;
import com.edgelesschat.Entity.Picture;
import com.edgelesschat.global.ResearchCommon;
import com.edgelesschat.map.BMapApiApp;
import com.edgelesschat.org.json.JSONArray;

public class UserTable {

	public static final String TABLE_NAME = "UserTable";//数据表的名称

	public static final String COLUMN_UID = "uid"; //用户id
	public static final String COLUMN_LOGIN_ID = "loginId";
	public static final String COLUMN_SORT = "sort";
	public static final String COLUMN_PHONE = "phone";
	public static final String COLUMN_HEAD_SMALL = "headSmall";
	public static final String COLUMN_HEAD_LARGE = "headLarge";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_FAUTH1 = "fauth1";//0-看 1-不看 当前用户是否看另个用户的朋友圈
	public static final String COLUMN_FAUTH2 = "fauth2";//0-看 1-不看 当前用户是否看另个用户的朋友圈
	
	/**
	 * //是否接消息 0-不接收 1-接收
	 */
	public static final String COLUMN_ISGETMSG = "isGetMsg";

	
	public static final String COLUMN_GENDER = "gender";
	public static final String COLUMN_SIGN = "sign";
	public static final String COLUMN_USER_PIC="userPic";
	


	public static final String COLUMN_PROVINCEID = "provinceid"; 

	public static final String COLUMN_CITYID = "cityid"; 
	
	public static final String COLUMN_REMARK = "remark"; 
	public static final String COLUMN_NICKNAME = "nickName"; 
	
	public static final String COLUMN_CREATE_TIME = "createTime"; 


	public static final String COLUMN_IS_FRIEND = "isFriend";
	public static final String COLUMN_GROUP_ID = "groupId"; 
	public static final String COLUMN_USER_TYPE ="user_type";
	public static final String COLUMN_USER_NAME_TYPE ="user_name_type";

	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String PRIMARY_KEY_TYPE = "primary key(";

	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;

	public UserTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}

	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_UID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_SORT , COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_PHONE , COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_HEAD_SMALL , COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_HEAD_LARGE , COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_NAME , COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_USER_PIC, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_FAUTH1, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_FAUTH2, COLUMN_INTEGER_TYPE);
			
			/*//订阅信息+++
			columnNameAndType.put(COLUMN_FEATURES , COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_AUTH , COLUMN_TEXT_TYPE);*/
			columnNameAndType.put(COLUMN_ISGETMSG , COLUMN_INTEGER_TYPE);
			/*columnNameAndType.put(COLUMN_ORDERMENU, COLUMN_TEXT_TYPE);*/
			//订阅号信息---
			
			
			columnNameAndType.put(COLUMN_GENDER , COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_SIGN, COLUMN_TEXT_TYPE);

			columnNameAndType.put(COLUMN_PROVINCEID , COLUMN_TEXT_TYPE);

			columnNameAndType.put(COLUMN_CITYID  , COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_CREATE_TIME   , COLUMN_TEXT_TYPE);
			
			columnNameAndType.put(COLUMN_REMARK   , COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_NICKNAME   , COLUMN_TEXT_TYPE);
			

			columnNameAndType.put(COLUMN_IS_FRIEND, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_GROUP_ID, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_USER_TYPE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_USER_NAME_TYPE, COLUMN_INTEGER_TYPE);
			
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_UID + "," + COLUMN_LOGIN_ID + ")";

			mSQLCreateWeiboInfoTable = SqlHelper.formCreateTableSqlString(TABLE_NAME, columnNameAndType, primary_key);
		}
		return mSQLCreateWeiboInfoTable;

	}

	public static String getDeleteTableSQLString() {
		if (null == mSQLDeleteWeiboInfoTable) {
			mSQLDeleteWeiboInfoTable = SqlHelper.formDeleteTableSqlString(TABLE_NAME);
		}  
		return mSQLDeleteWeiboInfoTable;
	}

	public void insert(List<Login> users, int groupId) {
		List<Login> userList = new ArrayList<Login>();
		userList.addAll(users);
		for (Login user : userList) {
			if(user.uid == null || user.uid.equals("")){
				continue;
			}
			ContentValues allPromotionInfoValues = new ContentValues();

			allPromotionInfoValues.put(COLUMN_UID, user.uid);
			allPromotionInfoValues.put(COLUMN_LOGIN_ID, ResearchCommon.getUserId(BMapApiApp.getInstance()));

			allPromotionInfoValues.put(COLUMN_SORT , user.sort);
			allPromotionInfoValues.put(COLUMN_PHONE , user.phone);
			allPromotionInfoValues.put(COLUMN_HEAD_SMALL , user.headsmall);
			allPromotionInfoValues.put(COLUMN_HEAD_LARGE , user.headlarge);
			allPromotionInfoValues.put(COLUMN_NAME , user.name);
			allPromotionInfoValues.put(COLUMN_NICKNAME   , user.nickname);
			allPromotionInfoValues.put(COLUMN_USER_PIC, user.userPic);
			allPromotionInfoValues.put(COLUMN_FAUTH1, user.fauth1);
			allPromotionInfoValues.put(COLUMN_FAUTH2, user.fauth2);
			
			/*//订阅信息+++
			allPromotionInfoValues.put(COLUMN_FEATURES , user.features);
			allPromotionInfoValues.put(COLUMN_AUTH , user.auth);
			allPromotionInfoValues.put(COLUMN_ORDERMENU,user.menuString);
			//订阅号信息---
*/			
			allPromotionInfoValues.put(COLUMN_GENDER , user.gender);
			allPromotionInfoValues.put(COLUMN_SIGN, user.sign);

			allPromotionInfoValues.put(COLUMN_PROVINCEID , user.provinceid);

			allPromotionInfoValues.put(COLUMN_CITYID  , user.cityid);
			allPromotionInfoValues.put(COLUMN_CREATE_TIME   , user.createtime);



			allPromotionInfoValues.put(COLUMN_IS_FRIEND, user.isfriend);
			allPromotionInfoValues.put(COLUMN_GROUP_ID, groupId);
			
			allPromotionInfoValues.put(COLUMN_REMARK, user.remark);
			allPromotionInfoValues.put(COLUMN_USER_TYPE, user.userType);
			allPromotionInfoValues.put(COLUMN_USER_NAME_TYPE, user.nameType);
			allPromotionInfoValues.put(COLUMN_ISGETMSG , user.isGetMsg);
			

			delete(user);
			try {
				mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			} catch (SQLiteConstraintException e) {
				e.printStackTrace();
			}
		}						
	}

	
	public void update(Login user) {
		if(user.uid == null || user.uid.equals("")){
			return;
		}
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_SORT , user.sort);
		allPromotionInfoValues.put(COLUMN_PHONE , user.phone);
		allPromotionInfoValues.put(COLUMN_HEAD_SMALL , user.headsmall);
		allPromotionInfoValues.put(COLUMN_HEAD_LARGE , user.headlarge);
		allPromotionInfoValues.put(COLUMN_NAME , user.name);
		allPromotionInfoValues.put(COLUMN_USER_PIC, user.userPic);
		allPromotionInfoValues.put(COLUMN_FAUTH1, user.fauth1);
		allPromotionInfoValues.put(COLUMN_FAUTH2, user.fauth2);
		
	/*	//订阅信息+++
		allPromotionInfoValues.put(COLUMN_FEATURES , user.features);
		allPromotionInfoValues.put(COLUMN_AUTH , user.auth);*/
		allPromotionInfoValues.put(COLUMN_ISGETMSG , user.isGetMsg);
	/*	allPromotionInfoValues.put(COLUMN_ORDERMENU,user.menuString);
		//订阅号信息---
		*/
		allPromotionInfoValues.put(COLUMN_GENDER , user.gender);
		allPromotionInfoValues.put(COLUMN_SIGN, user.sign);

		allPromotionInfoValues.put(COLUMN_PROVINCEID , user.provinceid);

		allPromotionInfoValues.put(COLUMN_CITYID  , user.cityid);
		allPromotionInfoValues.put(COLUMN_CREATE_TIME   , user.createtime);



		allPromotionInfoValues.put(COLUMN_IS_FRIEND, user.isfriend);
		allPromotionInfoValues.put(COLUMN_GROUP_ID, user.groupId);
		
		allPromotionInfoValues.put(COLUMN_REMARK, user.remark);
		allPromotionInfoValues.put(COLUMN_NICKNAME   , user.nickname);
		allPromotionInfoValues.put(COLUMN_USER_TYPE, user.userType);
		allPromotionInfoValues.put(COLUMN_USER_NAME_TYPE, user.nameType);

		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_UID + " = '"
					+ user.uid + "' AND " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}						
	}

	
	
	public void updateIsGetMsg(String uid,int isGetMsg) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_ISGETMSG , isGetMsg);

		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_UID +
					" = '" + uid + "' AND " + COLUMN_LOGIN_ID + "='" + 
					ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}						
	}
	
	public void update(int groupId) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_GROUP_ID, 0);

		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_GROUP_ID +
					" = " + groupId + " AND " + COLUMN_LOGIN_ID + "='" + 
					ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}						
	}

	public void insert(Login user, int groupId) {
		if(user.uid == null || user.uid.equals("")){
			return;
		}
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_UID, user.uid);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID, ResearchCommon.getUserId(BMapApiApp.getInstance()));

		allPromotionInfoValues.put(COLUMN_SORT , user.sort);
		allPromotionInfoValues.put(COLUMN_PHONE , user.phone);
		allPromotionInfoValues.put(COLUMN_HEAD_SMALL , user.headsmall);
		allPromotionInfoValues.put(COLUMN_HEAD_LARGE , user.headlarge);
		allPromotionInfoValues.put(COLUMN_NAME , user.name);
		allPromotionInfoValues.put(COLUMN_USER_PIC, user.userPic);
		allPromotionInfoValues.put(COLUMN_FAUTH1, user.fauth1);
		allPromotionInfoValues.put(COLUMN_FAUTH2, user.fauth2);
	/*	
		//订阅信息+++
		allPromotionInfoValues.put(COLUMN_FEATURES , user.features);
		allPromotionInfoValues.put(COLUMN_AUTH , user.auth);*/
		allPromotionInfoValues.put(COLUMN_ISGETMSG , user.isGetMsg);
	/*	allPromotionInfoValues.put(COLUMN_ORDERMENU,user.menuString);
		//订阅号信息---
*/		
		allPromotionInfoValues.put(COLUMN_GENDER , user.gender);
		allPromotionInfoValues.put(COLUMN_SIGN, user.sign);

		allPromotionInfoValues.put(COLUMN_PROVINCEID , user.provinceid);

		allPromotionInfoValues.put(COLUMN_CITYID  , user.cityid);
		allPromotionInfoValues.put(COLUMN_CREATE_TIME   , user.createtime);



		allPromotionInfoValues.put(COLUMN_IS_FRIEND, user.isfriend);
		allPromotionInfoValues.put(COLUMN_GROUP_ID, groupId);
		
		allPromotionInfoValues.put(COLUMN_REMARK, user.remark);
		allPromotionInfoValues.put(COLUMN_NICKNAME   , user.nickname);
		allPromotionInfoValues.put(COLUMN_USER_TYPE, user.userType);
		allPromotionInfoValues.put(COLUMN_USER_NAME_TYPE, user.nameType);
		

		delete(user);
		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}					
	}

	public void delete(Login user) {
		if(user.uid == null){
			return;
		}
		mDBStore.delete(TABLE_NAME, COLUMN_UID + "='" + user.uid + "' AND " +
				COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
	}

	public Login query(String uid){
		Login user = new Login();
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + 
					" WHERE " + COLUMN_UID + "='" + uid + "' AND " + COLUMN_LOGIN_ID + "='" 
					+ ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexUId = cursor.getColumnIndex(COLUMN_UID);
				int indexSort = cursor.getColumnIndex(COLUMN_SORT);
				int indexPhone = cursor.getColumnIndex(COLUMN_PHONE);
				int indexHeadSmall = cursor.getColumnIndex(COLUMN_HEAD_SMALL);
				int indexHeadLarge = cursor.getColumnIndex(COLUMN_HEAD_LARGE);
				int indexName = cursor.getColumnIndex(COLUMN_NAME);
				int indexUserPic = cursor.getColumnIndex(COLUMN_USER_PIC);
				int indexFauth1 = cursor.getColumnIndex(COLUMN_FAUTH1);
				int indexFauth2 = cursor.getColumnIndex(COLUMN_FAUTH2);
				
			/*	//订阅信息+++
				int indexFeatures = cursor.getColumnIndex(COLUMN_FEATURES);
				int indexAuth = cursor.getColumnIndex(COLUMN_AUTH);
				
				int indexOrderMenuString = cursor.getColumnIndex(COLUMN_ORDERMENU);
				//订阅号信息---
*/				
				int indexGender = cursor.getColumnIndex(COLUMN_GENDER);
				int indexSign = cursor.getColumnIndex(COLUMN_SIGN);
						
				
				int indexProvinceid = cursor.getColumnIndex(COLUMN_PROVINCEID);
				int indexCreateTime = cursor.getColumnIndex(COLUMN_CREATE_TIME);

				int indexIsFriend = cursor.getColumnIndex(COLUMN_IS_FRIEND);
				int indexGroupId = cursor.getColumnIndex(COLUMN_GROUP_ID);
				
			
				
				int indexRemark = cursor.getColumnIndex(COLUMN_REMARK);
				int indexNickname = cursor.getColumnIndex(COLUMN_NICKNAME); 
				
				int indexUserNameType = cursor.getColumnIndex(COLUMN_USER_NAME_TYPE);
				int indexUserType = cursor.getColumnIndex(COLUMN_USER_TYPE);
				int indexIsGetMsg = cursor.getColumnIndex(COLUMN_ISGETMSG);

				user.uid = cursor.getString(indexUId);
				user.sort = cursor.getString(indexSort);
				user.phone = cursor.getString(indexPhone);
				user.headsmall = cursor.getString(indexHeadSmall);
				user.headlarge = cursor.getString(indexHeadLarge);
				user.name = cursor.getString(indexName);
				user.fauth1 = cursor.getInt(indexFauth1);
				user.fauth2 = cursor.getInt(indexFauth2);
				user.userPic = cursor.getString(indexUserPic);
				if(user.userPic!=null && !user.userPic.equals("")){
					JSONArray array = new JSONArray(user.userPic);
					if(array!=null && array.length()>0){
						user.picList = new ArrayList<Picture>();
						for (int i = 0; i < array.length(); i++) {
							user.picList.add(Picture.getInfo(array.getString(i)));
						}
					}
				}
				
			/*	//订阅号信息+++
				user.features = cursor.getString(indexFeatures);
				user.auth = cursor.getString(indexAuth);*/
				user.isGetMsg = cursor.getInt(indexIsGetMsg);
				/*user.menuString = cursor.getString(indexOrderMenuString);
				
				if(user.menuString!=null && !user.menuString.equals("")){
					JSONArray array = new JSONArray(user.menuString);
					if(array!=null && array.length()>0){
						user.menuList = new ArrayList<OrderMenuItem>();
						for (int i = 0; i < array.length(); i++) {
							user.menuList.add(new OrderMenuItem(array.getJSONObject(i)));
						}
					}
				}
				//订阅号信息---
*/				
				user.gender = cursor.getInt(indexGender);
				user.sign = cursor.getString(indexSign);
				
				user.provinceid = cursor.getString(indexProvinceid);
				user.createtime = cursor.getLong(indexCreateTime);

				user.isfriend = cursor.getInt(indexIsFriend);
				user.groupId = cursor.getInt(indexGroupId);
				user.remark = cursor.getString(indexRemark);
				user.nickname = cursor.getString(indexNickname);
				user.userType = cursor.getInt(indexUserType);
				user.nameType = cursor.getInt(indexUserNameType);

				GroupTable table = new GroupTable(mDBStore);
				Group group = table.query(user.groupId);
				if(group != null){
					user.groupName = group.teamName;
				}
				return user;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public Login query(int groupId, String groupName){
		Login user = new Login();
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + 
					" WHERE " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) 
					+ "' AND " + COLUMN_GROUP_ID + "=" + groupId, null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexUId = cursor.getColumnIndex(COLUMN_UID);
				int indexSort = cursor.getColumnIndex(COLUMN_SORT);
				int indexPhone = cursor.getColumnIndex(COLUMN_PHONE);
				int indexHeadSmall = cursor.getColumnIndex(COLUMN_HEAD_SMALL);
				int indexHeadLarge = cursor.getColumnIndex(COLUMN_HEAD_LARGE);
				int indexName = cursor.getColumnIndex(COLUMN_NAME);
				int indexUserPic = cursor.getColumnIndex(COLUMN_USER_PIC);
				int indexFauth1 = cursor.getColumnIndex(COLUMN_FAUTH1);
				int indexFauth2 = cursor.getColumnIndex(COLUMN_FAUTH2);
				
			/*	//订阅信息+++
				int indexFeatures = cursor.getColumnIndex(COLUMN_FEATURES);
				int indexAuth = cursor.getColumnIndex(COLUMN_AUTH);*/
				int indexIsGetMsg = cursor.getColumnIndex(COLUMN_ISGETMSG);
				/*int indexOrderMenuString = cursor.getColumnIndex(COLUMN_ORDERMENU);*/
				//订阅号信息---
				
				int indexGender = cursor.getColumnIndex(COLUMN_GENDER);
				int indexSign = cursor.getColumnIndex(COLUMN_SIGN);
				
				
				int indexProvinceid = cursor.getColumnIndex(COLUMN_PROVINCEID);
				int indexCreateTime = cursor.getColumnIndex(COLUMN_CREATE_TIME);

				int indexIsFriend = cursor.getColumnIndex(COLUMN_IS_FRIEND);
				int indexGroupId = cursor.getColumnIndex(COLUMN_GROUP_ID);
				
				int indexRemark = cursor.getColumnIndex(COLUMN_REMARK);
				int indexNickname = cursor.getColumnIndex(COLUMN_NICKNAME); 
				
				int indexUserNameType = cursor.getColumnIndex(COLUMN_USER_NAME_TYPE);
				int indexUserType = cursor.getColumnIndex(COLUMN_USER_TYPE);

				user.uid = cursor.getString(indexUId);
				user.sort = cursor.getString(indexSort);
				user.phone = cursor.getString(indexPhone);
				user.headsmall = cursor.getString(indexHeadSmall);
				user.headlarge = cursor.getString(indexHeadLarge);
				user.name = cursor.getString(indexName);
				user.fauth1 = cursor.getInt(indexFauth1);
				user.fauth2 = cursor.getInt(indexFauth2);
				user.userPic = cursor.getString(indexUserPic);
				if(user.userPic!=null && !user.userPic.equals("")){
					JSONArray array = new JSONArray(user.userPic);
					if(array!=null && array.length()>0){
						user.picList = new ArrayList<Picture>();
						for (int i = 0; i < array.length(); i++) {
							user.picList.add(Picture.getInfo(array.getString(i)));
						}
					}
				}
				
		
				user.isGetMsg = cursor.getInt(indexIsGetMsg);
				
				
				
				user.gender = cursor.getInt(indexGender);
				user.sign = cursor.getString(indexSign);
				
				user.provinceid = cursor.getString(indexProvinceid);
				user.createtime = cursor.getLong(indexCreateTime);

				user.isfriend = cursor.getInt(indexIsFriend);
				user.groupId = cursor.getInt(indexGroupId);
				user.remark = cursor.getString(indexRemark);
				user.nickname = cursor.getString(indexNickname);
				user.userType = cursor.getInt(indexUserType);
				user.nameType = cursor.getInt(indexUserNameType);

				GroupTable table = new GroupTable(mDBStore);
				Group group = table.query(user.groupId);
				if(group != null){
					user.groupName = group.teamName;
				}
				return user;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
		
	}
	
	/**
	 * 根据昵称检索用户
	 * @param groupId
	 * @param groupName
	 * @return
	 */
	public List<MainSearchEntity> queryListByNickName(String nickname) {
		List<MainSearchEntity> allInfo = new ArrayList<MainSearchEntity>();
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + 
					" WHERE " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) 
					+ "' AND ("+COLUMN_NICKNAME+" like '%"+nickname+"%' or "+ COLUMN_REMARK +" like '%"+nickname+"%')"
					+" AND "+COLUMN_USER_TYPE+"!=1", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexUId = cursor.getColumnIndex(COLUMN_UID);
				int indexSort = cursor.getColumnIndex(COLUMN_SORT);
				int indexHeadSmall = cursor.getColumnIndex(COLUMN_HEAD_SMALL);
				int indexNickname = cursor.getColumnIndex(COLUMN_NICKNAME); 
				int indexRemarkName = cursor.getColumnIndex(COLUMN_REMARK);

				do {
					MainSearchEntity user = new MainSearchEntity("通讯录", 100,cursor.getString(indexNickname),
							"", cursor.getString(indexHeadSmall), 0, 1,cursor.getString(indexUId),nickname,
							cursor.getString(indexRemarkName));
					allInfo.add(user);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return allInfo;
	}
	
	
	public List<Login> queryList(int groupId, String groupName) {
		List<Login> allInfo = new ArrayList<Login>();
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + 
					" WHERE " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) 
					+ "' AND " + COLUMN_GROUP_ID + "=" + groupId
					+" AND "+COLUMN_USER_TYPE+"!=1", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexUId = cursor.getColumnIndex(COLUMN_UID);
				int indexSort = cursor.getColumnIndex(COLUMN_SORT);
				int indexPhone = cursor.getColumnIndex(COLUMN_PHONE);
				int indexHeadSmall = cursor.getColumnIndex(COLUMN_HEAD_SMALL);
				int indexHeadLarge = cursor.getColumnIndex(COLUMN_HEAD_LARGE);
				int indexName = cursor.getColumnIndex(COLUMN_NAME);
				int indexUserPic = cursor.getColumnIndex(COLUMN_USER_PIC);
				int indexFauth1 = cursor.getColumnIndex(COLUMN_FAUTH1);
				int indexFauth2 = cursor.getColumnIndex(COLUMN_FAUTH2);
				
				
				int indexIsGetMsg = cursor.getColumnIndex(COLUMN_ISGETMSG);
			
				
				int indexGender = cursor.getColumnIndex(COLUMN_GENDER);
				int indexSign = cursor.getColumnIndex(COLUMN_SIGN);
				
				int indexProvinceid = cursor.getColumnIndex(COLUMN_PROVINCEID);
				int indexCreateTime = cursor.getColumnIndex(COLUMN_CREATE_TIME);

				int indexIsFriend = cursor.getColumnIndex(COLUMN_IS_FRIEND);
				int indexGroupId = cursor.getColumnIndex(COLUMN_GROUP_ID);
				
				int indexRemark = cursor.getColumnIndex(COLUMN_REMARK);
				int indexNickname = cursor.getColumnIndex(COLUMN_NICKNAME); 
				int indexUserNameType = cursor.getColumnIndex(COLUMN_USER_NAME_TYPE);
				int indexUserType = cursor.getColumnIndex(COLUMN_USER_TYPE);

				do {
					Login user = new Login();
					user.uid = cursor.getString(indexUId);
					user.sort = cursor.getString(indexSort);
					user.phone = cursor.getString(indexPhone);
					user.headsmall = cursor.getString(indexHeadSmall);
					user.headlarge = cursor.getString(indexHeadLarge);
					user.name = cursor.getString(indexName);
					user.fauth1 = cursor.getInt(indexFauth1);
					user.fauth2 = cursor.getInt(indexFauth2);
					user.userPic = cursor.getString(indexUserPic);
					if(user.userPic!=null && !user.userPic.equals("")){
						JSONArray array = new JSONArray(user.userPic);
						if(array!=null && array.length()>0){
							user.picList = new ArrayList<Picture>();
							for (int i = 0; i < array.length(); i++) {
								user.picList.add(Picture.getInfo(array.getString(i)));
							}
						}
					}
					
					
				
					user.isGetMsg = cursor.getInt(indexIsGetMsg);
				
					
					
					user.gender = cursor.getInt(indexGender);
					user.sign = cursor.getString(indexSign);
					
					user.provinceid = cursor.getString(indexProvinceid);
					user.createtime = cursor.getLong(indexCreateTime);

					user.isfriend = cursor.getInt(indexIsFriend);
					user.groupId = cursor.getInt(indexGroupId);
					
					user.remark = cursor.getString(indexRemark);
					user.nickname = cursor.getString(indexNickname);
					user.userType = cursor.getInt(indexUserType);
					user.nameType = cursor.getInt(indexUserNameType);

					allInfo.add(user);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return allInfo;
	}
	
	/**
	 * 根据类型查询用户
	 * @param type; 0-学员 1-非学员 2-订阅号 3-服务号
	 */
	
	public List<Login> queryList(int type) {
		List<Login> allInfo = new ArrayList<Login>();
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + 
					" WHERE " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) 
					+ "' ", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexUId = cursor.getColumnIndex(COLUMN_UID);
				int indexSort = cursor.getColumnIndex(COLUMN_SORT);
				int indexPhone = cursor.getColumnIndex(COLUMN_PHONE);
				int indexHeadSmall = cursor.getColumnIndex(COLUMN_HEAD_SMALL);
				int indexHeadLarge = cursor.getColumnIndex(COLUMN_HEAD_LARGE);
				int indexName = cursor.getColumnIndex(COLUMN_NAME);
				int indexUserPic = cursor.getColumnIndex(COLUMN_USER_PIC);
				int indexFauth1 = cursor.getColumnIndex(COLUMN_FAUTH1);
				int indexFauth2 = cursor.getColumnIndex(COLUMN_FAUTH2);
				
			
				int indexIsGetMsg = cursor.getColumnIndex(COLUMN_ISGETMSG);
				
				
				int indexGender = cursor.getColumnIndex(COLUMN_GENDER);
				int indexSign = cursor.getColumnIndex(COLUMN_SIGN);
				
				int indexProvinceid = cursor.getColumnIndex(COLUMN_PROVINCEID);
				int indexCreateTime = cursor.getColumnIndex(COLUMN_CREATE_TIME);

				int indexIsFriend = cursor.getColumnIndex(COLUMN_IS_FRIEND);
				int indexGroupId = cursor.getColumnIndex(COLUMN_GROUP_ID);
				
				int indexRemark = cursor.getColumnIndex(COLUMN_REMARK);
				int indexNickname = cursor.getColumnIndex(COLUMN_NICKNAME); 
				int indexUserNameType = cursor.getColumnIndex(COLUMN_USER_NAME_TYPE);
				int indexUserType = cursor.getColumnIndex(COLUMN_USER_TYPE);

				do {
					Login user = new Login();
					user.uid = cursor.getString(indexUId);
					user.sort = cursor.getString(indexSort);
					user.phone = cursor.getString(indexPhone);
					user.headsmall = cursor.getString(indexHeadSmall);
					user.headlarge = cursor.getString(indexHeadLarge);
					user.name = cursor.getString(indexName);
					user.fauth1 = cursor.getInt(indexFauth1);
					user.fauth2 = cursor.getInt(indexFauth2);
					user.userPic = cursor.getString(indexUserPic);
					if(user.userPic!=null && !user.userPic.equals("")){
						JSONArray array = new JSONArray(user.userPic);
						if(array!=null && array.length()>0){
							user.picList = new ArrayList<Picture>();
							for (int i = 0; i < array.length(); i++) {
								user.picList.add(Picture.getInfo(array.getString(i)));
							}
						}
					}
					
			
					user.isGetMsg = cursor.getInt(indexIsGetMsg);
				
					
					user.gender = cursor.getInt(indexGender);
					user.sign = cursor.getString(indexSign);
					
					user.provinceid = cursor.getString(indexProvinceid);
					user.createtime = cursor.getLong(indexCreateTime);

					user.isfriend = cursor.getInt(indexIsFriend);
					user.groupId = cursor.getInt(indexGroupId);
					
					user.remark = cursor.getString(indexRemark);
					user.nickname = cursor.getString(indexNickname);
					user.userType = cursor.getInt(indexUserType);
					user.nameType = cursor.getInt(indexUserNameType);

					allInfo.add(user);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return allInfo;
	}

}
