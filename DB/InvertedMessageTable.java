package com.edgelesschat.DB;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.edgelesschat.Entity.MessageInfo;
import com.edgelesschat.global.ResearchCommon;
import com.edgelesschat.map.BMapApiApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class InvertedMessageTable {

    static final String NAME = "InvertedMessageTable";
    static final String MSG_field_id = "MSG_field_id";
    static final String MSG_field_msgId = "MSG_field_msgId";
    private SQLiteDatabase mDatabase;
    private static InvertedMessageTable sInvertedMessageTable;
    private Context mContext;

    static public InvertedMessageTable getInvertedMessageTable() {
        if (sInvertedMessageTable == null) {
            sInvertedMessageTable = new InvertedMessageTable(BMapApiApp.getInstance());
        }
        return sInvertedMessageTable;
    }

    public static String getCreateTableSQLString() {
        String sql = String.format("CREATE TABLE IF NOT EXISTS %s (%s text,%s text);", NAME, MSG_field_id,
                MSG_field_msgId);
        return sql;
    }

    public static String getDeleteTableSQLString() {
        return SqlHelper.formDeleteTableSqlString(NAME);
    }

    private InvertedMessageTable(Context c) {
        mContext = c;
    }

    public List<MessageInfo> query(String text, String fromId, String toId, int autoId, int type) {
        List<MessageInfo> list = new ArrayList<MessageInfo>();
        if (!TextUtils.isEmpty(text)) {
            List<Set<String>> mIdList = new ArrayList<Set<String>>();
            try {
                checkNull();
                for (int i = 0; i < text.length(); i++) {
                    String c = String.valueOf(text.charAt(i));

                    String searchSql = "SELECT " + MSG_field_msgId + " FROM " + NAME + " WHERE " + MSG_field_id + " = ? ";
                    Cursor cursor = mDatabase.rawQuery(searchSql, new String[]{c});
                    if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                        JSONObject jobj = new JSONObject(cursor.getString(0));
                        JSONArray array = jobj.optJSONArray("primarykey");
                        if (array != null && array.length() > 0) {
                            Set<String> recodeSet = new HashSet<String>();
                            for (int j = 0; j < array.length(); j++) {
                                recodeSet.add(array.get(j).toString());
                                mIdList.add(recodeSet);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mIdList.size() > 0) {
                Set<String> resultId = new HashSet<String>();
                for (int i = 0; i < mIdList.size(); i++) {
                    Set<String> set = mIdList.get(i);
                    if (i == 0) {
                        resultId.addAll(set);
                    } else {
                        resultId.retainAll(set);
                    }
                }

                if (resultId.size() > 0) {
                    MessageTable table = new MessageTable(mDatabase);
                    Iterator<String> iter = resultId.iterator();
                    try {
                        while (iter.hasNext()) {
                            String key = iter.next();
                            JSONObject jobj = new JSONObject(key);
                            String tag = jobj.optString("tag");
                            String toid = jobj.optString("toid");
                            String fromid = jobj.optString("fromid");
                            String loginid = jobj.optString("loginid");
                            //String id = jobj.optString("id");

                            if (TextUtils.equals(toid, toId) && TextUtils.equals(fromid, fromId)
                                    && TextUtils.equals(loginid, ResearchCommon.getUserId(BMapApiApp.getInstance()))) {
                                MessageInfo info = table.queryByPrimarykey(tag,toid,fromid,loginid);
                                if (info != null) {
                                    list.add(info);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        }
        return list;
    }

    public void updateInverted(MessageInfo message) {
        if (message == null || TextUtils.isEmpty(message.content)) {
            return;
        }
        checkNull();
        for (int i = 0; i < message.content.length(); i++) {
            String c = String.valueOf(message.content.charAt(i));
            updateChar(c, message);
        }
    }

    private void checkNull() {
        if (mDatabase == null) {
            mDatabase = DBHelper.getInstance(mContext).getWritableDatabase();
        }
    }

    private void updateChar(String c, MessageInfo message) {
        String searchSql = "SELECT " + MSG_field_msgId + " FROM " + NAME + " WHERE " + MSG_field_id + " = ? ";
        Cursor cursor = mDatabase.rawQuery(searchSql, new String[]{c});
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            // update
            String oginginKey = cursor.getString(0);
            String newkey = appendPrimaryKey(oginginKey, message);
            ContentValues values = new ContentValues();
            values.put(MSG_field_msgId, newkey);
            mDatabase.update(NAME, values, MSG_field_id + " = ? ",  new String[]{c});
        } else {
            // insert
            try {
                JSONArray array = new JSONArray();
                array.put(createPrimaryKey(message));
                String newkey = new JSONObject().put("primarykey", array).toString();
                ContentValues values = new ContentValues();
                values.put(MSG_field_id, c);
                values.put(MSG_field_msgId, newkey);
                mDatabase.insert(NAME, null, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Append a new message key into inverted message table
     * 
     * @param orginJson
     * @param message
     * @return
     */
    private String appendPrimaryKey(String orginJson, MessageInfo message) {
        try {
            String newkey = createPrimaryKey(message).toString();
            if (!orginJson.contains(newkey)) {
                JSONObject jobj = new JSONObject(orginJson);
                JSONArray array = jobj.optJSONArray("primarykey");
                array.put(newkey);
                return new JSONObject().put("primarykey", array).toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orginJson;
    }

    /**
     * Create message table primary key as json format for inverted message
     * table to store.
     * 
     * @return
     */
    private String createPrimaryKey(MessageInfo message) {
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("tag", message.tag);
            jobj.put("toid", message.toid);
            jobj.put("fromid", message.fromid);
            jobj.put("loginid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
            //jobj.put("id", message.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jobj.toString();
    }
}
