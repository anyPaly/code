package com.edgelesschat.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.edgelesschat.Entity.MainSearchEntity;
import com.edgelesschat.Entity.MessageInfo;
import com.edgelesschat.Entity.MessageType;
import com.edgelesschat.Entity.NotifiyVo;
import com.edgelesschat.Entity.Session;
import com.edgelesschat.append.ChatSecure;
import com.edgelesschat.global.GlobleType;
import com.edgelesschat.global.ResearchCommon;
import com.edgelesschat.map.BMapApiApp;
import com.edgelesschat.utils.Logger;

public class MessageTable {

    public static final String TABLE_NAME = "MessageTable";// 数据表的名称
    private static final String TAG = TABLE_NAME;
    /**
     * content, fromId, toId, sessionId, pullTime, sendTime, voiceTime, type,
     * readState, sendState, sendType, currentUser, primary key(fromId, toId,
     * sendTime, currentUser)
     */
    public static final String COLUMN_FROM_UID = "fromId";
    public static final String COLUMN_FROM_NAME = "fromName";
    public static final String COLUMN_FROM_HEAD = "fromHead";
    public static final String COLUMN_TO_ID = "toId";
    public static final String COLUMN_TO_NAME = "toName";
    public static final String COLUMN_TO_HEAD = "toHead";
    public static final String COLUMN_LOGIN_ID = "loginId";
    public static final String COLUMN_ID = "messageID";
    public static final String COLUMN_TAG = "messageTag";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_IMAGE_URLS = "imageUrlS";
    public static final String COLUMN_IMAGE_URLL = "imageUrlL";
    public static final String COLUMN_VOICE_URL = "voiceUrl";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_MESSAGE_TYPE = "msgtype";
    public static final String COLUMN_IMAGE_STRING = "imageString";
    public static final String COLUMN_IMAGE_WIDTH = "imageWith";
    public static final String COLUMN_IMAGE_HEIGHT = "imageHeight";
    public static final String COLUMN_SEND_TIME = "sendTime";
    public static final String COLUMN_VOICE_STRING = "voiceString";
    public static final String COLUMN_VOICE_TIME = "voiceTime";
    public static final String COLUMN_READ_STATE = "readState";
    public static final String COLUMN_SEND_STATE = "sendState";
    public static final String COLUMN_IS_READ_VOICE = "readVoiceState";
    public static final String COLUMN_SAMPLE_RATE = "sampleRate";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_SYSTEM_MESSAGE = "system_message";
    public static final String COLUMN_VIDEO_STRING = "videoString";
    public static final String COLUMN_INTEGER_TYPE = "integer";
    public static final String COLUMN_TEXT_TYPE = "text";
    public static final String PRIMARY_KEY_TYPE = "primary key(";

    private SQLiteDatabase mDBStore;

    private static String mSQLCreateWeiboInfoTable = null;
    private static String mSQLDeleteWeiboInfoTable = null;

    public MessageTable(SQLiteDatabase sqlLiteDatabase) {
        mDBStore = sqlLiteDatabase;
    }

    public static String getCreateTableSQLString() {
        if (null == mSQLCreateWeiboInfoTable) {

            HashMap<String, String> columnNameAndType = new HashMap<String, String>();
            columnNameAndType.put(COLUMN_FROM_UID, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_FROM_NAME, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_FROM_HEAD, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_TO_ID, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_TO_NAME, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_TO_HEAD, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_ID, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_TAG, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_CONTENT, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_IMAGE_URLS, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_IMAGE_URLL, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_VOICE_URL, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_LAT, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_LNG, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_ADDRESS, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_TYPE, COLUMN_INTEGER_TYPE);
            columnNameAndType.put(COLUMN_MESSAGE_TYPE, COLUMN_INTEGER_TYPE);
            columnNameAndType.put(COLUMN_IMAGE_WIDTH, COLUMN_INTEGER_TYPE);
            columnNameAndType.put(COLUMN_IMAGE_HEIGHT, COLUMN_INTEGER_TYPE);
            columnNameAndType.put(COLUMN_SEND_TIME, COLUMN_INTEGER_TYPE);
            columnNameAndType.put(COLUMN_VOICE_TIME, COLUMN_INTEGER_TYPE);
            columnNameAndType.put(COLUMN_READ_STATE, COLUMN_INTEGER_TYPE);
            columnNameAndType.put(COLUMN_SEND_STATE, COLUMN_INTEGER_TYPE);
            columnNameAndType.put(COLUMN_IS_READ_VOICE, COLUMN_INTEGER_TYPE);
            columnNameAndType.put(COLUMN_SAMPLE_RATE, COLUMN_INTEGER_TYPE);
            columnNameAndType.put(COLUMN_SYSTEM_MESSAGE, COLUMN_INTEGER_TYPE);
            columnNameAndType.put(COLUMN_IMAGE_STRING, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_VOICE_STRING, COLUMN_TEXT_TYPE);
            columnNameAndType.put(COLUMN_VIDEO_STRING, COLUMN_TEXT_TYPE);
            
            String primary_key = PRIMARY_KEY_TYPE + COLUMN_FROM_UID + "," + COLUMN_TO_ID + "," + COLUMN_LOGIN_ID + ","
                    + COLUMN_TAG + ")";

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

    public void insert(List<MessageInfo> messages) {
        List<MessageInfo> messageList = new ArrayList<MessageInfo>();
        messageList.addAll(messages);
        for (MessageInfo message : messageList) {
            ContentValues allPromotionInfoValues = new ContentValues();

            allPromotionInfoValues.put(COLUMN_FROM_UID, message.fromid);
            allPromotionInfoValues.put(COLUMN_FROM_NAME, message.fromname);
            allPromotionInfoValues.put(COLUMN_FROM_HEAD, message.fromurl);
            allPromotionInfoValues.put(COLUMN_LOGIN_ID, ResearchCommon.getUserId(BMapApiApp.getInstance()));
            allPromotionInfoValues.put(COLUMN_TO_ID, message.toid);
            allPromotionInfoValues.put(COLUMN_TO_NAME, message.toname);
            allPromotionInfoValues.put(COLUMN_TO_HEAD, message.tourl);
            allPromotionInfoValues.put(COLUMN_ID, message.id);
            allPromotionInfoValues.put(COLUMN_TAG, message.tag);
            allPromotionInfoValues.put(COLUMN_CONTENT, message.content);
            allPromotionInfoValues.put(COLUMN_IMAGE_URLS, message.imgUrlS);
            allPromotionInfoValues.put(COLUMN_IMAGE_URLL, message.imgUrlL);
            allPromotionInfoValues.put(COLUMN_VOICE_URL, message.voiceUrl);
            allPromotionInfoValues.put(COLUMN_TYPE, message.typechat);
            allPromotionInfoValues.put(COLUMN_MESSAGE_TYPE, message.typefile);
            allPromotionInfoValues.put(COLUMN_IMAGE_WIDTH, message.imgWidth);
            allPromotionInfoValues.put(COLUMN_IMAGE_HEIGHT, message.imgHeight);
            allPromotionInfoValues.put(COLUMN_SEND_TIME, message.time);
            allPromotionInfoValues.put(COLUMN_VOICE_TIME, message.voicetime);
            allPromotionInfoValues.put(COLUMN_READ_STATE, message.readState);
            allPromotionInfoValues.put(COLUMN_SEND_STATE, message.sendState);
            allPromotionInfoValues.put(COLUMN_IS_READ_VOICE, message.isReadVoice);
            allPromotionInfoValues.put(COLUMN_SAMPLE_RATE, message.sampleRate);
            allPromotionInfoValues.put(COLUMN_LAT, String.valueOf(message.mLat));
            allPromotionInfoValues.put(COLUMN_LNG, String.valueOf(message.mLng));
            allPromotionInfoValues.put(COLUMN_ADDRESS, message.mAddress);
            allPromotionInfoValues.put(COLUMN_SYSTEM_MESSAGE, message.systeMessage);
            allPromotionInfoValues.put(COLUMN_IMAGE_STRING, message.imageString);
            allPromotionInfoValues.put(COLUMN_VOICE_STRING, message.voiceString);
            allPromotionInfoValues.put(COLUMN_VIDEO_STRING, message.videoUrl);
            try {
                mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }
        }
    }

    public void insert(MessageInfo message) {

        if (message.mSystemInfo != null) {
            // insert system information
            message.tag = message.mSystemInfo.getContent() + "[" + System.currentTimeMillis() + "]";
            message.toid = message.mSystemInfo.roomID;
            message.toname = message.mSystemInfo.roomName;
            message.fromname = NotifiyVo.SYSUSR;
            message.fromid = NotifiyVo.SYSUSR;
            message.content = message.mSystemInfo.getContent();
            message.readState = 1;// default has read.
            String queryMaxMessageIdSql = "SELECT max(messageID) FROM " + TABLE_NAME;
            Cursor c = mDBStore.rawQuery(queryMaxMessageIdSql, null);
            if (c != null && c.moveToFirst()) {
                try {
                    String s = c.getString(0);
                    message.id = String.valueOf(Integer.parseInt(s) + 1);
                    Logger.d(TAG, "query max message id:" + c + ", well add 1 : " + message.id);
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e(TAG, e.getMessage(), e.fillInStackTrace());
                    return;
                }
            }
        }
        Cursor cursor = null;

        ContentValues allPromotionInfoValues = new ContentValues();

        allPromotionInfoValues.put(COLUMN_FROM_UID, message.fromid);
        allPromotionInfoValues.put(COLUMN_FROM_NAME, message.fromname);
        allPromotionInfoValues.put(COLUMN_FROM_HEAD, message.fromurl);
        allPromotionInfoValues.put(COLUMN_LOGIN_ID, ResearchCommon.getUserId(BMapApiApp.getInstance()));
        allPromotionInfoValues.put(COLUMN_TO_ID, message.toid);
        allPromotionInfoValues.put(COLUMN_TO_NAME, message.toname);
        allPromotionInfoValues.put(COLUMN_TO_HEAD, message.tourl);
        allPromotionInfoValues.put(COLUMN_ID, message.id);
        allPromotionInfoValues.put(COLUMN_TAG, message.tag);
        if (message.bEncrypted) {
            allPromotionInfoValues.put(COLUMN_CONTENT, message.strEncrypttData);
        } else {
            allPromotionInfoValues.put(COLUMN_CONTENT, message.content);
        }

        allPromotionInfoValues.put(COLUMN_IMAGE_URLS, message.imgUrlS);
        allPromotionInfoValues.put(COLUMN_IMAGE_URLL, message.imgUrlL);
        allPromotionInfoValues.put(COLUMN_VOICE_URL, message.voiceUrl);
        allPromotionInfoValues.put(COLUMN_TYPE, message.typechat);
        allPromotionInfoValues.put(COLUMN_MESSAGE_TYPE, message.typefile);
        allPromotionInfoValues.put(COLUMN_IMAGE_WIDTH, message.imgWidth);
        allPromotionInfoValues.put(COLUMN_IMAGE_HEIGHT, message.imgHeight);
        allPromotionInfoValues.put(COLUMN_SEND_TIME, message.time);
        allPromotionInfoValues.put(COLUMN_VOICE_TIME, message.voicetime);
        allPromotionInfoValues.put(COLUMN_READ_STATE, message.readState);
        allPromotionInfoValues.put(COLUMN_SEND_STATE, message.sendState);
        allPromotionInfoValues.put(COLUMN_IS_READ_VOICE, message.isReadVoice);
        allPromotionInfoValues.put(COLUMN_SAMPLE_RATE, message.sampleRate);
        allPromotionInfoValues.put(COLUMN_LAT, String.valueOf(message.mLat));
        allPromotionInfoValues.put(COLUMN_LNG, String.valueOf(message.mLng));
        allPromotionInfoValues.put(COLUMN_ADDRESS, message.mAddress);
        allPromotionInfoValues.put(COLUMN_SYSTEM_MESSAGE, message.systeMessage);
        allPromotionInfoValues.put(COLUMN_IMAGE_STRING, message.imageString);
        allPromotionInfoValues.put(COLUMN_VOICE_STRING, message.voiceString);
        allPromotionInfoValues.put(COLUMN_VIDEO_STRING, message.videoUrl);
        try {
            mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
            // update InvertedMessageTable
            InvertedMessageTable.getInvertedMessageTable().updateInverted(message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    public void delete(MessageInfo message) {
        mDBStore.delete(TABLE_NAME, COLUMN_TAG + "='" + message.tag + "' AND " + COLUMN_LOGIN_ID + "='"
                + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
    }
    
    public void deleteFromId(String strMsgId) 
    {
        mDBStore.delete(TABLE_NAME, COLUMN_ID + "='" + strMsgId + "' AND " + COLUMN_LOGIN_ID + "='"
                + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
    }
    
    public boolean delete(String toId, int type) {
        try {
            if (type == 300) {
                mDBStore.delete(
                        TABLE_NAME, COLUMN_TO_ID + "='" + toId + "'" + " AND " + COLUMN_TYPE + "=300" + " AND "
                                + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'",
                        null);
            } else if (type == 500) {
                mDBStore.delete(
                        TABLE_NAME, COLUMN_TO_ID + "='" + toId + "'" + " AND " + COLUMN_TYPE + "=500" + " AND "
                                + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'",
                        null);
            } else {
                mDBStore.delete(TABLE_NAME,
                        (COLUMN_FROM_UID + "='" + toId + "' or " + COLUMN_TO_ID + "='" + toId + "'") + " AND "
                                + COLUMN_TYPE + "=100" + " AND " + COLUMN_LOGIN_ID + "='"
                                + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'",
                        null);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateMessage(MessageInfo message) {
        ContentValues allPromotionInfoValues = new ContentValues();
        allPromotionInfoValues.put(COLUMN_ID, message.id);
        allPromotionInfoValues.put(COLUMN_IMAGE_URLS, message.imgUrlS);
        allPromotionInfoValues.put(COLUMN_IMAGE_URLL, message.imgUrlL);
        allPromotionInfoValues.put(COLUMN_VOICE_URL, message.voiceUrl);
        allPromotionInfoValues.put(COLUMN_IMAGE_WIDTH, message.imgWidth);
        allPromotionInfoValues.put(COLUMN_IMAGE_HEIGHT, message.imgHeight);
        allPromotionInfoValues.put(COLUMN_SEND_TIME, message.time);
        allPromotionInfoValues.put(COLUMN_VOICE_TIME, message.voicetime);
        allPromotionInfoValues.put(COLUMN_SEND_STATE, message.sendState);
        allPromotionInfoValues.put(COLUMN_READ_STATE, message.readState);
        allPromotionInfoValues.put(COLUMN_IS_READ_VOICE, message.isReadVoice);
        allPromotionInfoValues.put(COLUMN_SYSTEM_MESSAGE, message.systeMessage);
        allPromotionInfoValues.put(COLUMN_IMAGE_STRING, message.imageString);
        allPromotionInfoValues.put(COLUMN_VOICE_STRING, message.voiceString);
        allPromotionInfoValues.put(COLUMN_VIDEO_STRING, message.videoUrl);
        try {
            mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_TAG + "='" + message.tag + "' AND "
                    + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
            return true;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }

        return false;
    }

    /** Update head image url in db */
    public int updateHead(MessageInfo message) {
        ContentValues allPromotionInfoValues = new ContentValues();
        allPromotionInfoValues.put(COLUMN_FROM_HEAD, message.fromurl);
        allPromotionInfoValues.put(COLUMN_TO_HEAD, message.tourl);
        try {
            String loginId = ResearchCommon.getUserId(BMapApiApp.getInstance());
            String where = String.format(" %s='%s' AND %s='%s' AND %s='%s' ", COLUMN_FROM_UID, message.fromid,
                    COLUMN_LOGIN_ID, loginId, COLUMN_TO_ID, message.toid);
            return mDBStore.update(TABLE_NAME, allPromotionInfoValues, where, null);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean update(MessageInfo message) {
        ContentValues allPromotionInfoValues = new ContentValues();
        allPromotionInfoValues.put(COLUMN_SEND_STATE, message.sendState);
        allPromotionInfoValues.put(COLUMN_READ_STATE, message.readState);
        allPromotionInfoValues.put(COLUMN_IS_READ_VOICE, message.isReadVoice);

        try {
            mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_TAG + "='" + message.tag + "' AND "
                    + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
            return true;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateVoiceContent(String tag, String content) {

        try {
            String sql = "UPDATE " + TABLE_NAME + " SET " + COLUMN_CONTENT + "='" + content + "' WHERE " + COLUMN_TAG
                    + "='" + tag + "' AND " + COLUMN_TYPE + "=100 AND " + COLUMN_LOGIN_ID + "='"
                    + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'";
            mDBStore.execSQL(sql);
            return true;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateReadState(String id, int type) {

        try {
            String sql = "";
            if (type == 100) {
                sql = "UPDATE " + TABLE_NAME + " SET " + COLUMN_READ_STATE + "=1 WHERE "
                        + (COLUMN_FROM_UID + "='" + id + "' OR " + COLUMN_TO_ID + "='" + id) + "' AND " + COLUMN_TYPE
                        + "=100 AND " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance())
                        + "'";
            } else if (type == 300) {
                sql = "UPDATE " + TABLE_NAME + " SET " + COLUMN_READ_STATE + "=1 WHERE " + COLUMN_TO_ID + "='" + id
                        + "' AND " + COLUMN_TYPE + "=300 AND " + COLUMN_LOGIN_ID + "='"
                        + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'";
            } else if (type == GlobleType.MEETING_CHAT) {
                sql = "UPDATE " + TABLE_NAME + " SET " + COLUMN_READ_STATE + "=1 WHERE " + COLUMN_TO_ID + "='" + id
                        + "' AND " + COLUMN_TYPE + "=500 AND " + COLUMN_LOGIN_ID + "='"
                        + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'";
            }
            mDBStore.execSQL(sql);
            return true;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePrivateReadState(String id) {

        try {
            String sql = "UPDATE " + TABLE_NAME + " SET " + COLUMN_READ_STATE + "=1 WHERE "
                    + (COLUMN_FROM_UID + "='" + id + "' OR " + COLUMN_TO_ID + "='" + id) + "' AND " + COLUMN_TYPE
                    + "=100 AND " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'";
            mDBStore.execSQL(sql);
            return true;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }

        return false;
    }

    public MessageInfo query(String tag) {
        MessageInfo message = new MessageInfo();
        Cursor cursor = null;
        try {
            String querySql = "";
            querySql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TAG + "='" + tag + "'";
            cursor = mDBStore.rawQuery(querySql, null);
            if (cursor != null) {

                if (!cursor.moveToFirst()) {
                    return null;
                }

                int indexFromId = cursor.getColumnIndex(COLUMN_FROM_UID);
                int indexFromName = cursor.getColumnIndex(COLUMN_FROM_NAME);
                int indexFromHead = cursor.getColumnIndex(COLUMN_FROM_HEAD);
                int indexToID = cursor.getColumnIndex(COLUMN_TO_ID);
                int indexToName = cursor.getColumnIndex(COLUMN_TO_NAME);
                int indexToHead = cursor.getColumnIndex(COLUMN_TO_HEAD);
                int indexMessageId = cursor.getColumnIndex(COLUMN_ID);
                int indexMessageTag = cursor.getColumnIndex(COLUMN_TAG);
                int indexContent = cursor.getColumnIndex(COLUMN_CONTENT);
                int indexImgUrls = cursor.getColumnIndex(COLUMN_IMAGE_URLS);
                int indexImgUrlL = cursor.getColumnIndex(COLUMN_IMAGE_URLL);
                int indexVoiceUrl = cursor.getColumnIndex(COLUMN_VOICE_URL);
                int indexType = cursor.getColumnIndex(COLUMN_TYPE);
                int indexMessageType = cursor.getColumnIndex(COLUMN_MESSAGE_TYPE);
                int indexImgWidth = cursor.getColumnIndex(COLUMN_IMAGE_WIDTH);
                int indexImgHeight = cursor.getColumnIndex(COLUMN_IMAGE_HEIGHT);
                int indexSendTime = cursor.getColumnIndex(COLUMN_SEND_TIME);
                int indexVoiceTime = cursor.getColumnIndex(COLUMN_VOICE_TIME);
                int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
                int indexSendState = cursor.getColumnIndex(COLUMN_SEND_STATE);
                int indexVoiceReadState = cursor.getColumnIndex(COLUMN_IS_READ_VOICE);
                int indexLatID = cursor.getColumnIndex(COLUMN_LAT);
                int indexLngID = cursor.getColumnIndex(COLUMN_LNG);
                int indexAddressID = cursor.getColumnIndex(COLUMN_ADDRESS);
                int indexSystemMessage = cursor.getColumnIndex(COLUMN_SYSTEM_MESSAGE);

                int indexVoiceString = cursor.getColumnIndex(COLUMN_VOICE_STRING);
                int indexImageString = cursor.getColumnIndex(COLUMN_IMAGE_STRING);
                int indexVideoString = cursor.getColumnIndex(COLUMN_VIDEO_STRING);
                message.fromid = cursor.getString(indexFromId);
                message.fromname = cursor.getString(indexFromName);
                message.fromurl = cursor.getString(indexFromHead);
                message.toid = cursor.getString(indexToID);
                message.toname = cursor.getString(indexToName);
                message.tourl = cursor.getString(indexToHead);
                message.id = cursor.getString(indexMessageId);
                message.tag = cursor.getString(indexMessageTag);
                message.content = cursor.getString(indexContent);
                message.imgUrlS = cursor.getString(indexImgUrls);
                message.imgUrlL = cursor.getString(indexImgUrlL);
                message.voiceUrl = cursor.getString(indexVoiceUrl);
                message.typechat = cursor.getInt(indexType);
                message.typefile = cursor.getInt(indexMessageType);
                message.imgWidth = cursor.getInt(indexImgWidth);
                message.imgHeight = cursor.getInt(indexImgHeight);
                message.mLat = Double.parseDouble(cursor.getString(indexLatID));
                message.mLng = Double.parseDouble(cursor.getString(indexLngID));
                message.mAddress = cursor.getString(indexAddressID);
                message.systeMessage = cursor.getInt(indexSystemMessage);

                message.time = cursor.getLong(indexSendTime);
                message.voicetime = cursor.getInt(indexVoiceTime);
                message.readState = cursor.getInt(indexReadState);
                message.sendState = cursor.getInt(indexSendState);
                message.isReadVoice = cursor.getInt(indexVoiceReadState);
                message.voiceString = cursor.getString(indexVoiceString);
                message.imageString = cursor.getString(indexImageString);
                message.videoUrl = cursor.getString(indexVideoString);
                
                String strPassword = ChatSecure.getDecryptPassword(message.fromid);
                String strMessage = ChatSecure.decrypt(message.content, strPassword);
                if ((strMessage != null) && !strMessage.equals("")) {
                    message.content = strMessage;
                }

                return message;
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

    public MessageInfo queryByID(String id) {
        MessageInfo message = new MessageInfo();
        Cursor cursor = null;
        try {
            String querySql = "";
            querySql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "='" + id + "'";
            cursor = mDBStore.rawQuery(querySql, null);
            if (cursor != null) {

                if (!cursor.moveToFirst()) {
                    return null;
                }

                int indexFromId = cursor.getColumnIndex(COLUMN_FROM_UID);
                int indexFromName = cursor.getColumnIndex(COLUMN_FROM_NAME);
                int indexFromHead = cursor.getColumnIndex(COLUMN_FROM_HEAD);
                int indexToID = cursor.getColumnIndex(COLUMN_TO_ID);
                int indexToName = cursor.getColumnIndex(COLUMN_TO_NAME);
                int indexToHead = cursor.getColumnIndex(COLUMN_TO_HEAD);
                int indexMessageId = cursor.getColumnIndex(COLUMN_ID);
                int indexMessageTag = cursor.getColumnIndex(COLUMN_TAG);
                int indexContent = cursor.getColumnIndex(COLUMN_CONTENT);
                int indexImgUrls = cursor.getColumnIndex(COLUMN_IMAGE_URLS);
                int indexImgUrlL = cursor.getColumnIndex(COLUMN_IMAGE_URLL);
                int indexVoiceUrl = cursor.getColumnIndex(COLUMN_VOICE_URL);
                int indexType = cursor.getColumnIndex(COLUMN_TYPE);
                int indexMessageType = cursor.getColumnIndex(COLUMN_MESSAGE_TYPE);
                int indexImgWidth = cursor.getColumnIndex(COLUMN_IMAGE_WIDTH);
                int indexImgHeight = cursor.getColumnIndex(COLUMN_IMAGE_HEIGHT);
                int indexSendTime = cursor.getColumnIndex(COLUMN_SEND_TIME);
                int indexVoiceTime = cursor.getColumnIndex(COLUMN_VOICE_TIME);
                int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
                int indexSendState = cursor.getColumnIndex(COLUMN_SEND_STATE);
                int indexVoiceReadState = cursor.getColumnIndex(COLUMN_IS_READ_VOICE);
                int indexLatID = cursor.getColumnIndex(COLUMN_LAT);
                int indexLngID = cursor.getColumnIndex(COLUMN_LNG);
                int indexAddressID = cursor.getColumnIndex(COLUMN_ADDRESS);
                int indexSystemMessage = cursor.getColumnIndex(COLUMN_SYSTEM_MESSAGE);
                int indexVoiceString = cursor.getColumnIndex(COLUMN_VOICE_STRING);
                int indexImageString = cursor.getColumnIndex(COLUMN_IMAGE_STRING);
                int indexVideoString = cursor.getColumnIndex(COLUMN_VIDEO_STRING);
                
                message.fromid = cursor.getString(indexFromId);
                message.fromname = cursor.getString(indexFromName);
                message.fromurl = cursor.getString(indexFromHead);
                message.toid = cursor.getString(indexToID);
                message.toname = cursor.getString(indexToName);
                message.tourl = cursor.getString(indexToHead);
                message.id = cursor.getString(indexMessageId);
                message.tag = cursor.getString(indexMessageTag);
                message.content = cursor.getString(indexContent);
                message.imgUrlS = cursor.getString(indexImgUrls);
                message.imgUrlL = cursor.getString(indexImgUrlL);
                message.voiceUrl = cursor.getString(indexVoiceUrl);
                message.typechat = cursor.getInt(indexType);
                message.typefile = cursor.getInt(indexMessageType);
                message.imgWidth = cursor.getInt(indexImgWidth);
                message.imgHeight = cursor.getInt(indexImgHeight);
                message.mLat = Double.parseDouble(cursor.getString(indexLatID));
                message.mLng = Double.parseDouble(cursor.getString(indexLngID));
                message.mAddress = cursor.getString(indexAddressID);
                message.systeMessage = cursor.getInt(indexSystemMessage);

                message.time = cursor.getLong(indexSendTime);
                message.voicetime = cursor.getInt(indexVoiceTime);
                message.readState = cursor.getInt(indexReadState);
                message.sendState = cursor.getInt(indexSendState);
                message.isReadVoice = cursor.getInt(indexVoiceReadState);
                message.voiceString = cursor.getString(indexVoiceString);
                message.imageString = cursor.getString(indexImageString);
                message.videoUrl = cursor.getString(indexVideoString);
                
                String strPassword = ChatSecure.getDecryptPassword(message.fromid);
                String strMessage = ChatSecure.decrypt(message.content, strPassword);
                if ((strMessage != null) && !strMessage.equals("")) {
                    message.content = strMessage;
                }

                return message;
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

    /**
     * 根据内容查找
     * 
     * @param toId
     * @param autoID
     * @param type
     * @return
     */
    public List<MainSearchEntity> queryByContent(String searchContent, String toId, int type) {
        List<MainSearchEntity> allInfo = new ArrayList<MainSearchEntity>();
        Cursor cursor = null;
        try {
            String querySql = "";
            if (type == 0) {// 查询所有的数据（包括单聊和群聊）
                querySql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='"
                        + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'" + " AND " + COLUMN_CONTENT
                        + " like '%" + searchContent + "%' " + " AND " + COLUMN_MESSAGE_TYPE + " =" + MessageType.TEXT;
            } else {
                if (type == 100 && (toId != null && !toId.equals(""))) {
                    querySql = "SELECT * FROM " + TABLE_NAME + " WHERE (" + COLUMN_FROM_UID + "='" + toId + "' or "
                            + COLUMN_TO_ID + "='" + toId + "')" + " AND " + COLUMN_TYPE + "=" + type + " AND "
                            + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'"
                            + " AND " + COLUMN_CONTENT + " like '%" + searchContent + "%' " + " AND "
                            + COLUMN_MESSAGE_TYPE + " =" + MessageType.TEXT;
                } else if (type == 300 && (toId != null && !toId.equals(""))) {
                    querySql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TO_ID + "='" + toId + "'" + " AND "
                            + COLUMN_TYPE + "=" + type + " AND " + COLUMN_LOGIN_ID + "='"
                            + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'" + " AND " + COLUMN_CONTENT
                            + " like '%" + searchContent + "%' " + " AND " + COLUMN_MESSAGE_TYPE + " ="
                            + MessageType.TEXT;
                }
            }

            cursor = mDBStore.rawQuery(querySql, null);
            if (cursor != null) {

                if (!cursor.moveToFirst()) {
                    return null;
                }

                int indexFromId = cursor.getColumnIndex(COLUMN_FROM_UID);
                int indexFromName = cursor.getColumnIndex(COLUMN_FROM_NAME);
                int indexFromHead = cursor.getColumnIndex(COLUMN_FROM_HEAD);
                int indexToID = cursor.getColumnIndex(COLUMN_TO_ID);
                int indexToName = cursor.getColumnIndex(COLUMN_TO_NAME);
                int indexToHead = cursor.getColumnIndex(COLUMN_TO_HEAD);
                int indexMessageId = cursor.getColumnIndex(COLUMN_ID);
                int indexMessageTag = cursor.getColumnIndex(COLUMN_TAG);
                int indexContent = cursor.getColumnIndex(COLUMN_CONTENT);
                int indexImgUrls = cursor.getColumnIndex(COLUMN_IMAGE_URLS);
                int indexImgUrlL = cursor.getColumnIndex(COLUMN_IMAGE_URLL);
                int indexVoiceUrl = cursor.getColumnIndex(COLUMN_VOICE_URL);
                int indexType = cursor.getColumnIndex(COLUMN_TYPE);
                int indexMessageType = cursor.getColumnIndex(COLUMN_MESSAGE_TYPE);
                int indexImgWidth = cursor.getColumnIndex(COLUMN_IMAGE_WIDTH);
                int indexImgHeight = cursor.getColumnIndex(COLUMN_IMAGE_HEIGHT);
                int indexSendTime = cursor.getColumnIndex(COLUMN_SEND_TIME);
                int indexVoiceTime = cursor.getColumnIndex(COLUMN_VOICE_TIME);
                int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
                int indexSendState = cursor.getColumnIndex(COLUMN_SEND_STATE);
                int indexVoiceReadState = cursor.getColumnIndex(COLUMN_IS_READ_VOICE);
                int indexLatID = cursor.getColumnIndex(COLUMN_LAT);
                int indexLngID = cursor.getColumnIndex(COLUMN_LNG);
                int indexAddressID = cursor.getColumnIndex(COLUMN_ADDRESS);
                int indexSystemMessage = cursor.getColumnIndex(COLUMN_SYSTEM_MESSAGE);
                int indexVoiceString = cursor.getColumnIndex(COLUMN_VOICE_STRING);
                int indexImageString = cursor.getColumnIndex(COLUMN_IMAGE_STRING);
                int indexVideoString = cursor.getColumnIndex(COLUMN_VIDEO_STRING);
                do {
                    int typeChat = cursor.getInt(indexType);
                    String nickname = cursor.getString(indexToName);
                    String headUrl = cursor.getString(indexToHead);
                    String uid = cursor.getString(indexToID);
                    if (typeChat == 100) {

                    } else if (typeChat == 300) {
                        SessionTable sesTab = new SessionTable(mDBStore);
                        Session session = sesTab.query(cursor.getString(indexToID), 300);
                        if (session != null) {
                            nickname = session.name;
                            headUrl = session.heading;
                            uid = session.getFromId();
                        }
                    }

                    MainSearchEntity message = new MainSearchEntity("聊天记录", typeChat, nickname,
                            cursor.getString(indexContent), headUrl, cursor.getLong(indexSendTime), 2, uid,
                            searchContent, "");
                    /*
                     * message.fromid = cursor.getString(indexFromId);
                     * message.fromname = cursor.getString(indexFromName);
                     * message.fromurl = cursor.getString(indexFromHead);
                     * message.toid = cursor.getString(indexToID);
                     * message.toname = cursor.getString(indexToName);
                     * message.tourl = cursor.getString(indexToHead); message.id
                     * = cursor.getString(indexMessageId); message.tag =
                     * cursor.getString(indexMessageTag); message.content =
                     * cursor.getString(indexContent); message.imgUrlS =
                     * cursor.getString(indexImgUrls); message.imgUrlL =
                     * cursor.getString(indexImgUrlL); message.voiceUrl =
                     * cursor.getString(indexVoiceUrl); message.typefile=
                     * cursor.getInt(indexMessageType); message.imgWidth =
                     * cursor.getInt(indexImgWidth); message.imgHeight =
                     * cursor.getInt(indexImgHeight); message.mLat =
                     * Double.parseDouble(cursor.getString(indexLatID));
                     * message.mLng =
                     * Double.parseDouble(cursor.getString(indexLngID));
                     * message.mAddress = cursor.getString(indexAddressID);
                     * 
                     * message.time = cursor.getLong(indexSendTime);
                     * message.voicetime = cursor.getInt(indexVoiceTime);
                     * message.readState = cursor.getInt(indexReadState);
                     * message.sendState = cursor.getInt(indexSendState);
                     * message.isReadVoice = cursor.getInt(indexVoiceReadState);
                     * message.systeMessage = cursor.getInt(indexSystemMessage);
                     * message.voiceString = cursor.getString(indexVoiceString);
                     * message.imageString = cursor.getString(indexImageString);
                     */

                    allInfo.add(0, message);
                } while (cursor.moveToNext());

                return allInfo;
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

    public MessageInfo queryByPrimarykey(String tag, String toid, String fromid, String loginid) {
        String sql = String.format(
                "select content,msgtype,fromId from MessageTable where messageTag = '%s' and toId='%s' and fromId='%s' and loginId='%s'",
                tag, toid, fromid, loginid);
       Cursor cursor = mDBStore.rawQuery(sql, null);
       if (cursor!= null && cursor.getCount() > 0 && cursor.moveToFirst()) {
           MessageInfo info = new MessageInfo(); 
           info.content = cursor.getString(0);
           info.typefile = cursor.getInt(1);
           info.fromid = cursor.getString(2);
           return info;
       }
       return null;
    }

    public List<MessageInfo> query(String toId, int autoID, int type) {
        if (TextUtils.isEmpty(toId)) {
            return null;
        }
        List<MessageInfo> allInfo = new ArrayList<MessageInfo>();
        Cursor cursor = null;
        try {

            String querySql = "";
            String sysusr = " or (fromId='" + NotifiyVo.SYSUSR + "' and toId='" + toId + "')";
            if (type == 100) {
                if (autoID == -1) {
                    querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE ((" + COLUMN_FROM_UID + "='" + toId
                            + "' or " + COLUMN_TO_ID + "='" + toId + "')" + " AND " + COLUMN_LOGIN_ID + "='"
                            + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'" + " AND " + COLUMN_TYPE + "="
                            + type + ")" + sysusr + " ORDER BY rowid" + " DESC LIMIT 0," + ResearchCommon.LOAD_SIZE;
                } else {
                    querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE ((" + COLUMN_FROM_UID + "='" + toId
                            + "' or " + COLUMN_TO_ID + "='" + toId + "')" + " AND " + COLUMN_LOGIN_ID + "='"
                            + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'" + " AND " + " rowid<" + autoID
                            + " AND " + COLUMN_TYPE + "=" + type + ") " + sysusr + " ORDER BY rowid" + " DESC LIMIT 0,"
                            + ResearchCommon.LOAD_SIZE;
                }

            } else {
                if (autoID == -1) {
                    querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE (" + COLUMN_TO_ID + "='" + toId + "' AND "
                            + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'"
                            + " AND " + COLUMN_TYPE + "=" + type + ")" + sysusr + " ORDER BY messageID"
                            + " DESC LIMIT 0," + ResearchCommon.LOAD_SIZE;
                } else {
                    querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE (" + COLUMN_TO_ID + "='" + toId + "' AND "
                            + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'"
                            + " AND " + " rowid<" + autoID + " AND " + COLUMN_TYPE + "=" + type + ")" + sysusr
                            + " ORDER BY rowid" + " DESC LIMIT 0," + ResearchCommon.LOAD_SIZE;
                }

            }
//            Log.e("querySqlAAAA:" , querySql);
            cursor = mDBStore.rawQuery(querySql, null);
            if (cursor != null) {

                if (!cursor.moveToFirst()) {
                    return null;
                }

                int indexRowId = cursor.getColumnIndex("rowid");
                int indexFromId = cursor.getColumnIndex(COLUMN_FROM_UID);
                int indexFromName = cursor.getColumnIndex(COLUMN_FROM_NAME);
                int indexFromHead = cursor.getColumnIndex(COLUMN_FROM_HEAD);
                int indexToID = cursor.getColumnIndex(COLUMN_TO_ID);
                int indexToName = cursor.getColumnIndex(COLUMN_TO_NAME);
                int indexToHead = cursor.getColumnIndex(COLUMN_TO_HEAD);
                int indexMessageId = cursor.getColumnIndex(COLUMN_ID);
                int indexMessageTag = cursor.getColumnIndex(COLUMN_TAG);
                int indexContent = cursor.getColumnIndex(COLUMN_CONTENT);
                int indexImgUrls = cursor.getColumnIndex(COLUMN_IMAGE_URLS);
                int indexImgUrlL = cursor.getColumnIndex(COLUMN_IMAGE_URLL);
                int indexVoiceUrl = cursor.getColumnIndex(COLUMN_VOICE_URL);
                int indexType = cursor.getColumnIndex(COLUMN_TYPE);
                int indexMessageType = cursor.getColumnIndex(COLUMN_MESSAGE_TYPE);
                int indexImgWidth = cursor.getColumnIndex(COLUMN_IMAGE_WIDTH);
                int indexImgHeight = cursor.getColumnIndex(COLUMN_IMAGE_HEIGHT);
                int indexSendTime = cursor.getColumnIndex(COLUMN_SEND_TIME);
                int indexVoiceTime = cursor.getColumnIndex(COLUMN_VOICE_TIME);
                int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
                int indexSendState = cursor.getColumnIndex(COLUMN_SEND_STATE);
                int indexVoiceReadState = cursor.getColumnIndex(COLUMN_IS_READ_VOICE);
                int indexLatID = cursor.getColumnIndex(COLUMN_LAT);
                int indexLngID = cursor.getColumnIndex(COLUMN_LNG);
                int indexAddressID = cursor.getColumnIndex(COLUMN_ADDRESS);
                int indexSystemMessage = cursor.getColumnIndex(COLUMN_SYSTEM_MESSAGE);
                int indexVoiceString = cursor.getColumnIndex(COLUMN_VOICE_STRING);
                int indexImageString = cursor.getColumnIndex(COLUMN_IMAGE_STRING);
                int indexVideoString = cursor.getColumnIndex(COLUMN_VIDEO_STRING);
                do {
                    MessageInfo message = new MessageInfo();
                    message.fromid = cursor.getString(indexFromId);
                    message.fromname = cursor.getString(indexFromName);
                    message.fromurl = cursor.getString(indexFromHead);
                    message.toid = cursor.getString(indexToID);
                    message.toname = cursor.getString(indexToName);
                    message.tourl = cursor.getString(indexToHead);
                    message.id = cursor.getString(indexMessageId);
                    message.tag = cursor.getString(indexMessageTag);
                    message.content = cursor.getString(indexContent);
                    message.imgUrlS = cursor.getString(indexImgUrls);
                    message.imgUrlL = cursor.getString(indexImgUrlL);
                    message.voiceUrl = cursor.getString(indexVoiceUrl);
                    message.typechat = cursor.getInt(indexType);
                    message.typefile = cursor.getInt(indexMessageType);
                    message.imgWidth = cursor.getInt(indexImgWidth);
                    message.imgHeight = cursor.getInt(indexImgHeight);
                    message.mLat = Double.parseDouble(cursor.getString(indexLatID));
                    message.mLng = Double.parseDouble(cursor.getString(indexLngID));
                    message.mAddress = cursor.getString(indexAddressID);

                    message.time = cursor.getLong(indexSendTime);
                    message.voicetime = cursor.getInt(indexVoiceTime);
                    message.readState = cursor.getInt(indexReadState);
                    message.sendState = cursor.getInt(indexSendState);
                    message.isReadVoice = cursor.getInt(indexVoiceReadState);
                    message.systeMessage = cursor.getInt(indexSystemMessage);
                    message.voiceString = cursor.getString(indexVoiceString);
                    message.imageString = cursor.getString(indexImageString);
                    message.videoUrl = cursor.getString(indexVideoString);
                    
                    message.auto_id = cursor.getInt(indexRowId);

                    String strPassword = ChatSecure.getDecryptPassword(message.fromid);
                    String strMessage = ChatSecure.decrypt(message.content, strPassword);
                    if ((strMessage != null) && !strMessage.equals("")) {
                        message.content = strMessage;
                    }

                    if (NotifiyVo.SYSUSR.equals(message.fromid)) {
                        message.mSystemInfo = new NotifiyVo();
                        message.mSystemInfo.setContent(message.content);
                    }
                    Logger.d(TAG, "query fromid:" + message.fromid);
                    allInfo.add(0, message);
                } while (cursor.moveToNext());

                return allInfo;
            }
        } catch (Exception e) {
            // e.printStackTrace();
            Logger.e(TAG, e.getMessage(), e.fillInStackTrace());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public int queryUnreadCountByID(String id, int type) {
        Cursor cursor = null;
        try {

            String querySql = "";
            if (type == 100) {
                querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE " + COLUMN_FROM_UID + "='" + id + "' AND "
                        + COLUMN_TYPE + "=100 AND " + COLUMN_READ_STATE + "=0" + " AND " + COLUMN_LOGIN_ID + "='"
                        + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'" + " ORDER BY " + COLUMN_SEND_TIME
                        + " DESC ";
            } else {
                querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE " + COLUMN_TO_ID + "='" + id + "' AND "
                        + COLUMN_TYPE + "=type AND " + COLUMN_READ_STATE + "=0" + " AND " + COLUMN_LOGIN_ID + "='"
                        + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'" + " ORDER BY " + COLUMN_SEND_TIME
                        + " DESC ";
            }

            cursor = mDBStore.rawQuery(querySql, null);
            if (cursor != null) {

                if (!cursor.moveToFirst()) {
                    return 0;
                }

                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public int queryUnreadCount() {
        Cursor cursor = null;
        try {

            String querySql = "";
            querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE " + COLUMN_READ_STATE + "=0" + " AND "
                    + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'";

            cursor = mDBStore.rawQuery(querySql, null);
            if (cursor != null) {

                if (!cursor.moveToFirst()) {
                    return 0;
                }

                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public boolean isTopMessage(int nMesageID, int toId) {

        Cursor cursor = null;
        String querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE (" + COLUMN_FROM_UID + "='" + toId + "' or "
                + COLUMN_TO_ID + "='" + toId + "')" + " AND " + COLUMN_LOGIN_ID + "='"
                + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'" + " AND messageID > " + nMesageID
                + " ORDER BY messageID" + " DESC LIMIT 0, 1";

        cursor = mDBStore.rawQuery(querySql, null);
        if (cursor != null) {
            if (!cursor.moveToFirst()) {
                return true;
            }
        }
        return false;
    }

    public boolean isMessageExist(int nMesageID, int toId) {

        Cursor cursor = null;
        String querySql = "SELECT count(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_FROM_UID + "='" + toId + "'"
                + " AND " + COLUMN_LOGIN_ID + "='" + ResearchCommon.getUserId(BMapApiApp.getInstance()) + "'"
                + " AND messageID = " + nMesageID;

        cursor = mDBStore.rawQuery(querySql, null);
        if (cursor != null) {
            if (!cursor.moveToFirst()) {
                return false;
            }
            long result = cursor.getLong(0);
            if (result > 0) {
                return true;
            }
        }
        return false;
    }

}
