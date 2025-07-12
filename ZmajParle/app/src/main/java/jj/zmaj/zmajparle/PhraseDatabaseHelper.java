package jj.zmaj.zmajparle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class PhraseDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "zmajparle.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "phrases";

    public void deletePhrase(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }


    public PhraseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "text TEXT," +
                        "type TEXT," +
                        "tags TEXT," +
                        "favorite INTEGER" +
                        ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addPhrase(Phrase phrase) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("text", phrase.getText());
        values.put("type", phrase.getType());
        values.put("tags", phrase.getTags());
        values.put("favorite", phrase.isFavorite() ? 1 : 0);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<Phrase> getAllPhrases() {
        List<Phrase> phraseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "favorite DESC, id DESC");
        if (cursor.moveToFirst()) {
            do {
                Phrase phrase = new Phrase(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("text")),
                        cursor.getString(cursor.getColumnIndexOrThrow("type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tags")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1
                );
                phraseList.add(phrase);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return phraseList;
    }

    public List<Phrase> getPhrasesByType(String type) {
        List<Phrase> phraseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "type = ?", new String[]{type}, null, null, "favorite DESC, id DESC");
        if (cursor.moveToFirst()) {
            do {
                Phrase phrase = new Phrase(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("text")),
                        cursor.getString(cursor.getColumnIndexOrThrow("type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tags")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1
                );
                phraseList.add(phrase);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return phraseList;
    }

    public void updateFavorite(int id, boolean favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("favorite", favorite ? 1 : 0);
        db.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Phrase> getPhrasesByTag(String tag) {
        List<Phrase> phraseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "tags LIKE ?", new String[]{"%"+tag+"%"}, null, null, "favorite DESC, id DESC");
        if (cursor.moveToFirst()) {
            do {
                Phrase phrase = new Phrase(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("text")),
                        cursor.getString(cursor.getColumnIndexOrThrow("type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tags")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1
                );
                phraseList.add(phrase);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return phraseList;
    }

    public List<Phrase> getPhrasesByTypeAndTag(String type, String tag) {
        List<Phrase> phraseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "type = ?";
        String[] selectionArgs;
        if (tag.isEmpty()) {
            selectionArgs = new String[]{type};
        } else {
            selection += " AND tags LIKE ?";
            selectionArgs = new String[]{type, "%" + tag + "%"};
        }
        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, "favorite DESC, id DESC");
        if (cursor.moveToFirst()) {
            do {
                Phrase phrase = new Phrase(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("text")),
                        cursor.getString(cursor.getColumnIndexOrThrow("type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tags")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1
                );
                phraseList.add(phrase);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return phraseList;
    }


}
