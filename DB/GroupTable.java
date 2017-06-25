package com.edgelesschat.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edgelesschat.Entity.Group;
import com.edgelesschat.Entity.Login;
import com.edgelesschat.Entity.MainSearchEntity;
import com.edgelesschat.global.ResearchCommon;
import com.edgelesschat.map.BMapApiApp;

public class GroupTable {

	public static final String TABLE_NAME = "GroupTable";//数据表的名称
	public static final String COLUMN_GROUP_ID = "groupId";
	public static final String COLUMN_LOGIN_ID = "loginId";
	public static final String COLUMN_GROUP_NAME = "groupname";
	
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String PRIMARY_KEY_TYPE = "primary key(";
	
	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;
	
	public GroupTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}
	
	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_GROUP_ID, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_GROUP_NAME, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_GROUP_ID + "," + COLUMN_LOGIN_ID + ")";

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
	
	public void insert(List<Group> groups) {
		List<Group> groupList = new ArrayList<Group>();
		groupList.addAll(groups);
		for (Group group : groupList) {
			ContentValues allPromotionInfoValues = new ContentValues();
			
			allPromotionInfoValues.put(COLUMN_GROUP_ID, group.id);
			allPromotionInfoValues.put(COLUMN_LOGIN_ID, ResearchCommon.getUserId(BMapApiApp.getInstance()));
			allPromotionInfoValues.put(COLUMN_GROUP_NAME, group.teamName);
			if(group.mUserList != null && group.mUserList.size() != 0){
				UserTable table = new UserTable(mDBStore);
				table.insert(group.mUserList, group.id);
			}
		
			delete(group.id);
			try {
				mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			} catch (SQLiteConstraintException e) {
				e.printStackTrace();
			}
		}						
	}
	
	public boolean insert(Group group) {
		ContentValues allPromotionInfoValues = new ContentValues();
		
		allPromotionInfoValues.put(COLUMN_GROUP_ID, group.id);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID, ResearchCommon.getUserId(BMapApiApp.getInstance()));
		allPromotionInfoValues.put(COLUMN_GROUP_NAME, group.teamName);
		
		if(group.mUserList != null && group.mUserList.size()>0){
			UserTable table = new UserTable(mDBStore);
			table.insert(group.mUserList,group.id);
		}
		
		delete(group.id);
		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}			
		return false;
	}
	
	public boolean update(Group group) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_GROUP_NAME, group.teamName);
		
		if(group.mUserList!= null && group.mUserList.size()>0){
			UserTable table = new UserTable(mDBStore);
			table.insert(group.mUserList,group.id);
		}
		
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_GROUP_ID + " = '" + group.id + "' AND " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}			
		
		return false;
	}
	
	public boolean delete(int groupId) {
		try {
			mDBStore.delete(TABLE_NAME, COLUMN_GROUP_ID + " = " + groupId + " AND " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public Group query(int groupId){
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME 
					+ " WHERE " + COLUMN_GROUP_ID + " = " + groupId + " AND "
					+ COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			if (cursor != null) {
				
				if (!cursor.moveToFirst()) {
					return null;
				}
				
				int indexGroupId = cursor.getColumnIndex(COLUMN_GROUP_ID);
				int indexGroupName = cursor.getColumnIndex(COLUMN_GROUP_NAME);
				
				Group group = new Group();
				group.id = cursor.getInt(indexGroupId);
				group.teamName = cursor.getString(indexGroupName);
				
				UserTable table = new UserTable(mDBStore);
				String  name = "";
				if(group.id >= 0){
					name = group.teamName;
				}
				group.mUserList = table.queryList(group.id, name);
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
	
	
	public List<Group> queryStarUser(){
		List<Group> allInfo = new ArrayList<Group>();
		Cursor cursor = null;
		try {
			String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='" 
		+ ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'";
			Log.e("query", sql);
			cursor = mDBStore.rawQuery(sql, null);
			if (cursor != null) {
				
				if (!cursor.moveToFirst()) {
					return null;
				}
				
				int indexGroupId = cursor.getColumnIndex(COLUMN_GROUP_ID);
				int indexGroupName = cursor.getColumnIndex(COLUMN_GROUP_NAME);
				
				do {
					Group group = new Group();
					group.id = cursor.getInt(indexGroupId);
					group.teamName = cursor.getString(indexGroupName);
					UserTable table = new UserTable(mDBStore);
					group.mUserList = table.queryList(group.id, group.teamName);
					allInfo.add(group);
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
	
	
	public List<Group> query() {
		List<Group> allInfo = new ArrayList<Group>();
		Cursor cursor = null;
		try {
			String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='" 
		+ ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'";
			Log.e("query", sql);
			cursor = mDBStore.rawQuery(sql, null);
			if (cursor != null) {
				
				if (!cursor.moveToFirst()) {
					return null;
				}
				
				int indexGroupId = cursor.getColumnIndex(COLUMN_GROUP_ID);
				int indexGroupName = cursor.getColumnIndex(COLUMN_GROUP_NAME);
				
				do {
					Group group = new Group();
					group.id = cursor.getInt(indexGroupId);
					group.teamName = cursor.getString(indexGroupName);
					UserTable table = new UserTable(mDBStore);
					group.mUserList = table.queryList(group.id, group.teamName);
					if(group.mUserList!=null && group.mUserList.size()>0){
						for (int i = 0; i < group.mUserList.size(); i++) {
							Login login = group.mUserList.get(i);
							if(login.userType == 2){
								Login starLogin = new Login();
								starLogin.uid = login.uid;
								starLogin.nickname = login.nickname;
								starLogin.remark = login.remark;
								starLogin.headsmall = login.headsmall;
								starLogin.sign = login.sign;
								starLogin.sort = "☆";
								starLogin.sortName = "星标朋友";
								group.mStarList = new ArrayList<Login>();
								group.mStarList.add(starLogin);
								break;
							}
						}
					}
					allInfo.add(group);
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
	
	public List<Group> queryGroup(int initId) {
		List<Group> allInfo = new ArrayList<Group>();
		Cursor cursor = null;
		try {
			String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "' AND " + COLUMN_GROUP_ID + ">=" + initId ;
			Log.e("query", sql);
			cursor = mDBStore.rawQuery(sql, null);
			if (cursor != null) {
				
				if (!cursor.moveToFirst()) {
					return null;
				}
				
				int indexGroupId = cursor.getColumnIndex(COLUMN_GROUP_ID);
				int indexGroupName = cursor.getColumnIndex(COLUMN_GROUP_NAME);
				
				do {
					Group group = new Group();
					group.id = cursor.getInt(indexGroupId);
					group.teamName = cursor.getString(indexGroupName);
					
					allInfo.add(group);
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
