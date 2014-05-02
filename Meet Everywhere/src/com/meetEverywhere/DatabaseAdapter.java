package com.meetEverywhere;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * 
 * Klasa, która bêdzie zbêdna, zostawiam j¹ jako wzór dla metod DAO w przysz³oœci.
 *
 */
public class DatabaseAdapter {

	public static enum TagType{USER, SEARCH};
	
    private final String DB_NAME = "database.db";
    private final String TAGS_TABLE = "Tags";
    private final String SEARCH_TAGS_TABLE = "SearchTags";
    private final String FRIENDS_TABLE = "Friends";
	
    private final int DB_VERSION = 4;
    
    private static final String KEY_ID = "_id";
    private static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public final int ID_COLUMN = 0;
    
    private final String KEY_TAG = "tag";
    private final String TAG_OPTIONS = "TEXT NOT NULL";
    public final int TAG_COLUMN = 1;
    private final String KEY_ACTIVE_TAG = "active_tag";
    private final String ACTIVE_TAG_OPTION = "INTEGER NOT NULL";
    public final int ACTIVE_TAG_COLUMN = 2;
    
    private final String KEY_NAME = "name";
    private final String NAME_OPTIONS = "TEXT NOT NULL";
    public final int NAME_COLUMN = 1;
    private final String KEY_NUM = "num";
    private final String NUM_OPTIONS = "TEXT NOT NULL";
    public final int NUM_COLUMN = 2;
    
    private final String TAGS_TABLE_CREATE = "create table " +
    		TAGS_TABLE + " (" +
    		KEY_ID + " " + ID_OPTIONS + ", " +
    		KEY_TAG + " " + TAG_OPTIONS + ", " +
    		KEY_ACTIVE_TAG + " " + ACTIVE_TAG_OPTION + ");";
    
    private final String SEARCH_TAGS_TABLE_CREATE = "create table " +
    		SEARCH_TAGS_TABLE + " (" +
    		KEY_ID + " " + ID_OPTIONS + ", " +
    		KEY_TAG + " " + TAG_OPTIONS + ", " +
    		KEY_ACTIVE_TAG + " " + ACTIVE_TAG_OPTION + ");";
    
    private final String FRIENDS_TABLE_CREATE = "create table " +
    		FRIENDS_TABLE + " (" +
    		KEY_ID + " " + ID_OPTIONS + ", " +
    		KEY_NAME + " " + NAME_OPTIONS + ", " +
    		KEY_NUM + " " + NUM_OPTIONS + ");";
    
    //Klasa helpera do otwierania i aktualizacji bazy danych
    private class DatabaseHelper extends SQLiteOpenHelper {
    
        public DatabaseHelper(Context context, String name,
                CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
    
        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(TAGS_TABLE_CREATE);
            _db.execSQL(SEARCH_TAGS_TABLE_CREATE);
            _db.execSQL(FRIENDS_TABLE_CREATE);
        }
    
        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVer, int newVer) {
            _db.execSQL("DROP TABLE IF EXISTS " + TAGS_TABLE);
            _db.execSQL("DROP TABLE IF EXISTS " + SEARCH_TAGS_TABLE);
            _db.execSQL("DROP TABLE IF EXISTS " + FRIENDS_TABLE);
            onCreate(_db);
        }
    }
    
    //Zmienna do przechowywania bazy danych
    private SQLiteDatabase db;
    
    //Kontekst aplikacji korzystaj¹cej z bazy
    private final Context context;
    
    //Helper od otwierania i aktualizacji bazy danych
    private DatabaseHelper myDatabaseHelper;
    
    public DatabaseAdapter(Context _context) {
        context = _context;
        myDatabaseHelper = new DatabaseHelper(context, 
                DB_NAME, null, DB_VERSION);
    }
    
    //Otwieramy po³¹czenie z baz¹ danych
    public DatabaseAdapter open() {
        db = myDatabaseHelper.getWritableDatabase();
        return this;
    }
    
    //Zamykamy po³¹czenie z baz¹ danych
    public void close(){
        db.close();
    }
    
    private int translateBoolToInt(boolean arg){
    	if(arg)
    		return 1;
    	else
    		return 0;
    }
    
    //Metoda dodaj¹ca nowy wiersz danych do bazy
    public long insertTag(String tag, boolean checked, TagType tagType) {
        //Tworzymy obiekt nowego "wiersza"
        ContentValues newValues = new ContentValues();
        //Wype³niamy wszystkie pola wiersza
        newValues.put(KEY_TAG, tag);
        newValues.put(KEY_ACTIVE_TAG, translateBoolToInt(checked));
        //Wstawiamy wiersz do bazy
        
        String table;
        if(tagType.equals(TagType.USER))
        	table = TAGS_TABLE;
        else
        	table = SEARCH_TAGS_TABLE;
        
        return db.insert(table, null, newValues);
    }
    
    //Metoda aktualizuj¹ca konkretny wiersz
    public boolean updateTag(long id, String tag, boolean checked, TagType tagType) {
        //Warunek wstawiany do klauzuli WHERE
        String where = KEY_ID + "=" + id;
        //Tak samo jak przy metodzie insert
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_TAG, tag);
        updateValues.put(KEY_ACTIVE_TAG, translateBoolToInt(checked));
        //Aktualizujemy dane wiersza zgodnego ze zmienn¹ where
        
        String table;
        if(tagType.equals(TagType.USER))
        	table = TAGS_TABLE;
        else
        	table = SEARCH_TAGS_TABLE;
        
        return db.update(table, updateValues, where, null) > 0;
    }
    
    public boolean deleteTag(long id, TagType tagType) {
        String where = KEY_ID + "=" + id;
        
        String table;
        if(tagType.equals(TagType.USER))
        	table = TAGS_TABLE;
        else
        	table = SEARCH_TAGS_TABLE;
        
        return db.delete(table, where , null) > 0;
    }
    
    public void deleteAllTags(TagType tagType) {
        String table;
        if(tagType.equals(TagType.USER))
        	table = TAGS_TABLE;
        else
        	table = SEARCH_TAGS_TABLE;
        
        db.delete(table, "1", null);
    }
    
    public Cursor getAllTags(TagType tagType) {
        String[] columns = {KEY_ID, KEY_TAG, KEY_ACTIVE_TAG};
        
        String table;
        if(tagType.equals(TagType.USER))
        	table = TAGS_TABLE;
        else
        	table = SEARCH_TAGS_TABLE;
        
        return db.query(table, columns,
                null, null, null, null, null);
    }

    //Metoda dodaj¹ca nowy wiersz danych do bazy
    public long insertContact(String name, String number) {
        //Tworzymy obiekt nowego "wiersza"
        ContentValues newValues = new ContentValues();
        //Wype³niamy wszystkie pola wiersza
        newValues.put(KEY_NAME, name);
        newValues.put(KEY_NUM, number);
        //Wstawiamy wiersz do bazy
        return db.insert(FRIENDS_TABLE, null, newValues);
    }
    
    //Metoda aktualizuj¹ca konkretny wiersz
    public boolean updateContact(long id, String name, String number) {
        //Warunek wstawiany do klauzuli WHERE
        String where = KEY_ID + "=" + id;
        //Tak samo jak przy metodzie insert
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_NAME, name);
        updateValues.put(KEY_NUM, number);
        //Aktualizujemy dane wiersza zgodnego ze zmienn¹ where
        return db.update(FRIENDS_TABLE, updateValues, where, null) > 0;
    }
    
    public boolean deleteContact(long id) {
        String where = KEY_ID + "=" + id;
        return db.delete(FRIENDS_TABLE, where , null) > 0;
    }
    
    public void deleteAllContacts() {
        db.delete(FRIENDS_TABLE, "1", null);
    }
    
    public Cursor getAllContacts() {
        String[] columns = {KEY_ID, KEY_NAME, KEY_NUM};
        return db.query(FRIENDS_TABLE, columns,
                null, null, null, null, null);
    }
}
