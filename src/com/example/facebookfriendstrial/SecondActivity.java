package com.example.facebookfriendstrial;

import java.util.ArrayList;
import com.example.dbhelper.DBHelper;
import com.example.imageView.LazyAdapter;
import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class SecondActivity extends ListActivity {
	private ArrayList<String> results = new ArrayList<String>();
	private static DBHelper dbHelper;
	private String tableName = DBHelper.tableName;
	private static SQLiteDatabase newDB;
	ImageView imageView;
	ListView list;
    LazyAdapter adapter;
    int count, i=0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dbHelper = new DBHelper(getApplicationContext());
        list=(ListView)findViewById(android.R.id.list);
        
        Button b=(Button)findViewById(android.R.id.button1);
        b.setOnClickListener(listener);
        	openAndQueryDatabase();
    }
    
    public OnClickListener listener=new OnClickListener(){
        @Override
        public void onClick(View arg0) {
            adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }
    };

	private void openAndQueryDatabase() {
		try {			
			newDB = dbHelper.getWritableDatabase();
			Cursor c = newDB.rawQuery("SELECT distinct id, name FROM " + tableName , null);
			
	    	if (c != null ) {
	    		if  (c.moveToFirst()) {
	    			do {
	    				String name = c.getString(c.getColumnIndex("NAME"));
	    				results.add("Name: " + name);
	    			}while (c.moveToNext());
	    		}
	    	}
	    	
	        if(adapter==null){
	        	adapter=new LazyAdapter(this, MainActivity.linkStrings, results);
	        }
	        list.setAdapter(adapter);
	        
		} catch (SQLiteException se ) {
        	Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } finally {
        	if (newDB != null) 
        		newDB.execSQL("DELETE FROM " + tableName);
        		newDB.close();
        }
	}
}
