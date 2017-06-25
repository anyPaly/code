package com.edgelesschat.DB;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	private SQLiteDatabase mDB = null;
	private static DBHelper mInstance = null;
	public static final String DataBaseName = "research.db";
	public static final int DataBaseVersion = 15;

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (null == mDB) {
			mDB = db;
		}
		db.execSQL(SessionTable.getCreateTableSQLString());
		db.execSQL(UserTable.getCreateTableSQLString());
		db.execSQL(MessageTable.getCreateTableSQLString());
		db.execSQL(GroupTable.getCreateTableSQLString());
		db.execSQL(RoomTable.getCreateTableSQLString());
		db.execSQL(InvertedMessageTable.getCreateTableSQLString());
		db.execSQL(BkgndTable.getCreateTableSQLString());
		db.execSQL(RoomUserTable.getCreateTableSQLString());
		db.execSQL(GroupRoomTable.getCreateTableSQLString());
		db.execSQL(AnimationTable.getCreateTableSQLString());
		db.execSQL(ChatBackgndTable.getCreateTableSQLString());
		db.execSQL(GifFavoriteTable.getCreateTableSQLString());
	}
	
	public synchronized static DBHelper getInstance(Context context){
		if (mInstance == null) {
			mInstance = new DBHelper(context, DataBaseName, null, DataBaseVersion);
		}
		
		return mInstance;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SessionTable.getDeleteTableSQLString());
		db.execSQL(UserTable.getDeleteTableSQLString());
		db.execSQL(MessageTable.getDeleteTableSQLString());
		db.execSQL(GroupTable.getDeleteTableSQLString());
		db.execSQL(RoomTable.getDeleteTableSQLString());
		db.execSQL(InvertedMessageTable.getDeleteTableSQLString());
		db.execSQL(BkgndTable.getDeleteTableSQLString());
		db.execSQL(RoomUserTable.getDeleteTableSQLString());
		db.execSQL(GroupRoomTable.getDeleteTableSQLString());
		db.execSQL(AnimationTable.getDeleteTableSQLString());
		db.execSQL(ChatBackgndTable.getDeleteTableSQLString());
		db.execSQL(GifFavoriteTable.getDeleteTableSQLString());
		onCreate(db);
	}
	
	@Override
	public synchronized void close() {
		if (mDB != null){
			mDB.close();
		}
		super.close();
	}
}
