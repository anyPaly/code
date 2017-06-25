package com.edgelesschat.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edgelesschat.Entity.Login;
import com.edgelesschat.Entity.Room;
import com.edgelesschat.Entity.User;
import com.edgelesschat.global.ResearchCommon;
import com.edgelesschat.map.BMapApiApp;

public class GroupRoomTable {

	public static final String TABLE_NAME = "GroupRoomTable";//数据表的名称
	/**
	 * fromId, sendTime, unreadCount, currentUser, primary key(fromId, currentUser
	 */
	public static final String COLUMN_ROOM_ID = "roomid";	
	public static final String COLUMN_ROOM_NAME = "roomname";//群昵称
	public static final String COLUMN_CREATE_USER_ID = "uid";
	public static final String COLUMN_IS_OWNER = "isOwner";
	public static final String COLUMN_LOGIN_ID = "loginid";
	public static final String COLUMN_GROUP_NICK_NAME = "group_nick_name";//用户所在群的昵称
	public static final String COLUMN_IS_PUBLISH_GROUP = "is_publish_group";//是否公开群
	public static final String COLUMN_IS_GET_GROUP_MSG = "is_get_group_msg";//是否接受群消息
	public static final String COLUMN_IS_SHOW_NICKNAME = "is_show_nickname";//是否显示群昵称
	//新添加字段
	public static final String COLUMN_ROOM_COUNT = "count";
	public static final String COLUMN_CREATE_CREATOR = "creator";
//	public static final String COLUMN_IS_JOIN = "is_jion";
	public static final String COLUMN_ROOM_ROLE = "role";
	
	public static final String COLUMN_CREATETIME = "createtime";

	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String PRIMARY_KEY_TYPE = "primary key(";

	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;

	public GroupRoomTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}

	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_ROOM_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ROOM_NAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_CREATE_USER_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_IS_OWNER, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);

			columnNameAndType.put(COLUMN_GROUP_NICK_NAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_IS_PUBLISH_GROUP, COLUMN_INTEGER_TYPE);//是否公开群
			columnNameAndType.put(COLUMN_IS_GET_GROUP_MSG, COLUMN_INTEGER_TYPE);//是否接受群消息
			columnNameAndType.put(COLUMN_IS_SHOW_NICKNAME, COLUMN_INTEGER_TYPE);
			
			//新添加字段
			columnNameAndType.put(COLUMN_ROOM_COUNT, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_CREATE_CREATOR, COLUMN_TEXT_TYPE);
//			columnNameAndType.put(COLUMN_IS_JOIN, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_ROOM_ROLE, COLUMN_INTEGER_TYPE);
			
			columnNameAndType.put(COLUMN_CREATETIME, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_ROOM_ID + "," + COLUMN_LOGIN_ID + ")";

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

	public void insert(List<Room> rooms) {
		List<Room> roomList = new ArrayList<Room>();
		roomList.addAll(rooms);
		for (Room room : roomList) {
			ContentValues allPromotionInfoValues = new ContentValues();

			allPromotionInfoValues.put(COLUMN_ROOM_ID, room.groupId);
			allPromotionInfoValues.put(COLUMN_ROOM_NAME, room.groupName);
			allPromotionInfoValues.put(COLUMN_CREATE_USER_ID, room.uid);
			allPromotionInfoValues.put(COLUMN_IS_OWNER, room.isOwner);
			allPromotionInfoValues.put(COLUMN_LOGIN_ID, ResearchCommon.getUserId(BMapApiApp.getInstance()));
			allPromotionInfoValues.put(COLUMN_GROUP_NICK_NAME, room.groupnickname);
			allPromotionInfoValues.put(COLUMN_IS_GET_GROUP_MSG, room.isgetmsg);//是否接受群消息
			allPromotionInfoValues.put(COLUMN_CREATETIME, room.createTime);
			//新添加字段
			
			allPromotionInfoValues.put(COLUMN_ROOM_COUNT, room.groupCount);
			allPromotionInfoValues.put(COLUMN_CREATE_CREATOR, room.creatAuth);
//			allPromotionInfoValues.put(COLUMN_IS_JOIN, room.isjoin);
			allPromotionInfoValues.put(COLUMN_ROOM_ROLE, room.role);
			
			delete(room.groupId);
			try {
				mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			} catch (SQLiteConstraintException e) {
				e.printStackTrace();
			}		
		}						
	}

	public void insert(Room room) {

		ContentValues allPromotionInfoValues = new ContentValues();

		allPromotionInfoValues.put(COLUMN_ROOM_ID, room.groupId);
		allPromotionInfoValues.put(COLUMN_ROOM_NAME, room.groupName);
		allPromotionInfoValues.put(COLUMN_CREATE_USER_ID, room.uid);
		allPromotionInfoValues.put(COLUMN_IS_OWNER, room.isOwner);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID, ResearchCommon.getUserId(BMapApiApp.getInstance()));
		allPromotionInfoValues.put(COLUMN_GROUP_NICK_NAME, room.groupnickname);
		allPromotionInfoValues.put(COLUMN_IS_GET_GROUP_MSG, room.isgetmsg);//是否接受群消息
		allPromotionInfoValues.put(COLUMN_CREATETIME, room.createTime);
		allPromotionInfoValues.put(COLUMN_IS_SHOW_NICKNAME, room.isShowNickname);
		
		//新添加字段
		allPromotionInfoValues.put(COLUMN_ROOM_COUNT, room.groupCount);
		allPromotionInfoValues.put(COLUMN_CREATE_CREATOR, room.creatAuth);
//		allPromotionInfoValues.put(COLUMN_IS_JOIN, room.isjoin);
		allPromotionInfoValues.put(COLUMN_ROOM_ROLE, room.role);

		delete(room.groupId);
		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}		
	}

	public boolean insert(String roomId, int isCreator,String gropName,String groupNickName,
			int ispublish,int isgetMsg,int isShowNicName,String creatAuth, int groupCount, int isjoin, int role) {
		ContentValues allPromotionInfoValues = new ContentValues();

		allPromotionInfoValues.put(COLUMN_ROOM_ID, roomId);
		allPromotionInfoValues.put(COLUMN_IS_OWNER, isCreator);
		allPromotionInfoValues.put(COLUMN_ROOM_NAME, gropName);
		allPromotionInfoValues.put(COLUMN_GROUP_NICK_NAME,groupNickName);
		allPromotionInfoValues.put(COLUMN_IS_PUBLISH_GROUP,ispublish);//是否公开群
		allPromotionInfoValues.put(COLUMN_IS_GET_GROUP_MSG, isgetMsg);//是否接受群消息
		allPromotionInfoValues.put(COLUMN_IS_SHOW_NICKNAME, isShowNicName);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID, ResearchCommon.getUserId(BMapApiApp.getInstance()));
		
		//新添加字段
		allPromotionInfoValues.put(COLUMN_ROOM_COUNT, groupCount);
		allPromotionInfoValues.put(COLUMN_CREATE_CREATOR, creatAuth);
//		allPromotionInfoValues.put(COLUMN_IS_JOIN, isjoin);
		allPromotionInfoValues.put(COLUMN_ROOM_ROLE, role);

		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}			
		return false;
	}

	public boolean update(Room room) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_ROOM_NAME, room.groupName);
		allPromotionInfoValues.put(COLUMN_GROUP_NICK_NAME, room.groupnickname);
		allPromotionInfoValues.put(COLUMN_IS_GET_GROUP_MSG, room.isgetmsg);//是否接受群消息
		allPromotionInfoValues.put(COLUMN_IS_SHOW_NICKNAME, room.isShowNickname);

		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_ROOM_ID + " = '" + room.groupId 
					+ "' AND " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}			

		return false;
	}

	public boolean updatePublish(int isPublish,String roomId) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_IS_PUBLISH_GROUP, isPublish);//是否公开群

		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_ROOM_ID + " = '" + roomId 
					+ "' AND " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}			

		return false;
	}
	public boolean updateIsGetMsg(int isGetMsg,String roomId) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_IS_GET_GROUP_MSG, isGetMsg);//是否接受群消息

		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_ROOM_ID + " = '" + roomId 
					+ "' AND " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}			

		return false;
	}

	public boolean delete(String roomId) {
		try {
			mDBStore.delete(TABLE_NAME, COLUMN_ROOM_ID + " = '" + roomId + "' AND " + 
					COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean delete() {
		try {
			mDBStore.delete(TABLE_NAME, COLUMN_LOGIN_ID + "='"
					+ ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public Room query(String roomId){
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
					COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + 
					"' AND " + COLUMN_ROOM_ID + "='" + roomId + "'" , null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}				

				int indexRoomId = cursor.getColumnIndex(COLUMN_ROOM_ID);
				int indexRoomName = cursor.getColumnIndex(COLUMN_ROOM_NAME);
				int indexUid = cursor.getColumnIndex(COLUMN_CREATE_USER_ID);
				int indexIsOwner = cursor.getColumnIndex(COLUMN_IS_OWNER);

				int indexGroupNickName = cursor.getColumnIndex(COLUMN_GROUP_NICK_NAME);
				int indexIsGetGroupMsg = cursor.getColumnIndex(COLUMN_IS_GET_GROUP_MSG);
				int indexIsShowNickName = cursor.getColumnIndex(COLUMN_IS_SHOW_NICKNAME);
				
				//新添加字段
				int indexRoomCount = cursor.getColumnIndex(COLUMN_ROOM_COUNT);
				int indexCreateCreator = cursor.getColumnIndex(COLUMN_CREATE_CREATOR);
//				int indexIsJion = cursor.getColumnIndex(COLUMN_IS_JOIN);
				int indexRoomRole = cursor.getColumnIndex(COLUMN_ROOM_ROLE);

				int indexCreateTime = cursor.getColumnIndex(COLUMN_CREATETIME);
				

				Room room = new Room();
				room.groupId = cursor.getString(indexRoomId);
				room.groupName = cursor.getString(indexRoomName);
				room.uid = cursor.getString(indexUid);
				room.isOwner = cursor.getInt(indexIsOwner);
				room.isgetmsg = cursor.getInt(indexIsGetGroupMsg);
				room.isShowNickname = cursor.getInt(indexIsShowNickName);
				room.groupnickname = cursor.getString(indexGroupNickName);
				room.createTime = cursor.getLong(indexCreateTime);
				//新添加字段
				room.groupCount = cursor.getInt(indexRoomCount);
				room.creatAuth = cursor.getString(indexCreateCreator);
//				room.isjoin = cursor.getInt(indexIsJion);
				room.role = cursor.getInt(indexRoomRole);
				return room;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public List<Room> query() {
		List<Room> allInfo = new ArrayList<Room>();
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " 
					+ COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexRoomId = cursor.getColumnIndex(COLUMN_ROOM_ID);
				int indexRoomName = cursor.getColumnIndex(COLUMN_ROOM_NAME);
				int indexUid = cursor.getColumnIndex(COLUMN_CREATE_USER_ID);
				int indexIsOwner = cursor.getColumnIndex(COLUMN_IS_OWNER);

				int indexGroupNickName = cursor.getColumnIndex(COLUMN_GROUP_NICK_NAME);
				int indexIsGetGroupMsg = cursor.getColumnIndex(COLUMN_IS_GET_GROUP_MSG);
				int indexIsShowNickName = cursor.getColumnIndex(COLUMN_IS_SHOW_NICKNAME);
				
				//新添加字段
				int indexRoomCount = cursor.getColumnIndex(COLUMN_ROOM_COUNT);
				int indexCreateCreator = cursor.getColumnIndex(COLUMN_CREATE_CREATOR);
//				int indexIsJion = cursor.getColumnIndex(COLUMN_IS_JOIN);
				int indexRoomRole = cursor.getColumnIndex(COLUMN_ROOM_ROLE);
				
				int indexCreateTime = cursor.getColumnIndex(COLUMN_CREATETIME);


				do {
					Room room = new Room();
					room.groupId = cursor.getString(indexRoomId);
					room.groupName = cursor.getString(indexRoomName);
					room.uid = cursor.getString(indexUid);
					room.isOwner = cursor.getInt(indexIsOwner);
					room.groupnickname = cursor.getString(indexGroupNickName);
					room.isgetmsg = cursor.getInt(indexIsGetGroupMsg);
					room.isShowNickname = cursor.getInt(indexIsShowNickName);
					room.createTime = cursor.getLong(indexCreateTime);
					
					//新添加字段
					room.groupCount = cursor.getInt(indexRoomCount);
					room.creatAuth = cursor.getString(indexCreateCreator);
//					room.isjoin = cursor.getInt(indexIsJion);
					room.role = cursor.getInt(indexRoomRole);
					RoomUserTable table = new RoomUserTable(mDBStore);
					List<User> mUsers = new ArrayList<>();
					mUsers = table.query(room.groupId);
					room.mUserList = new ArrayList<>();
					if (mUsers != null) {
						for (int i = 0; i < mUsers.size(); i++) {
							User user = mUsers.get(i);
							Login login = new Login();
							login.uid = user.getUserId();
							login.headsmall = user.getHeadSmall();
							login.name = user.getName();
							if (user.getGroupId()!=null) {
								login.groupId = Integer.parseInt(user.getGroupId());
							}
							room.mUserList.add(login);
						}
					}
					Log.e("liaotian", "aaaaa"+room.groupCount);
					allInfo.add(room);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("lt", e.toString());
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return allInfo;
	}
}
