package com.example.facebookfriendstrial;

import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dbhelper.DBHelper;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
public class MainActivity extends Activity {

	private LoginButton loginBtn;
	private TextView userName;
	private UiLifecycleHelper uiHelper;
	private static DBHelper dbHelper;
	@SuppressWarnings("unused")
	private static SQLiteDatabase newDB;
	public static String[] linkStrings;
	private Button friendsListBtn;
	ListView listView;
	private boolean loggedIn = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_facebook);

		friendsListBtn = (Button) findViewById(R.id.friends_list);
		friendsListBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(loggedIn){
				Intent intent = new Intent(MainActivity.this, SecondActivity.class);
				startActivity(intent);
				}
				else
				{
					Toast.makeText(MainActivity.this, "Please Login.", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		userName = (TextView) findViewById(R.id.user_name);
		loginBtn = (LoginButton) findViewById(R.id.fb_login_button);
		
		loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
			@Override
			public void onUserInfoFetched(GraphUser user) {
				if (user != null) {
					userName.setText("Hello, " + user.getName());
					loggedIn = true;
				} else {
					userName.setText("You are not logged in");
				}
			}
		});
	}

	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) {
				Log.d("FacebookSampleActivity", "Facebook session opened");
				loggedIn = true;
			} else if (state.isClosed()) {
				Log.d("FacebookSampleActivity", "Facebook session closed");
			}
		}
	};
	public void onBackPressed() {
		
		super.onBackPressed();
		finish();
		
	};
	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();

		dbHelper = new DBHelper(getApplicationContext());
		
		Session session = Session.getActiveSession();
		
		if (session.isOpened()) {
			
			new Request(   session,  "/me/taggable_friends",   null,   HttpMethod.GET,   new Request.Callback() {
		        public void onCompleted(Response response) {
		            // handle the result 
		        	try {
			                	parseUserFromResponse(response);
		            	} catch ( Throwable t ) {
		                System.err.println( t );
		            }
		        }
		    }    ).executeAsync();
		}		
	}
	
	
	public static final void parseUserFromResponse( Response response ) {
		
		JSONObject currentJSonObject, jsonPicture, jsonData;
		String id, name, imageURL = null;
		int length=0, i, j=0;

		String ID = "id", NAME = "name";
		ContentValues values = new ContentValues();
		
		try {
	        GraphObject go  = response.getGraphObject();
	        JSONObject  jso = go.getInnerJSONObject();
	        JSONArray   arr = jso.getJSONArray( "data" );
	        length = arr.length();
	        
	        //dbHelper.openDataBase();
			newDB = dbHelper.getWritableDatabase();
	        linkStrings = new String[length];
			
	        for ( i = 0; i < length; i++ ){
	        	
  				currentJSonObject = arr.getJSONObject(i);
  				id     = currentJSonObject.getString("id");
	            name   = currentJSonObject.getString("name");
	            
		        jsonPicture = currentJSonObject.getJSONObject("picture");
		        jsonData = jsonPicture.getJSONObject("data");
	            imageURL = jsonData.getString("url");

	            linkStrings[j] = imageURL;
	            j++;
	            values.put(ID, id);  
	    	    values.put(NAME, name); 
	            dbHelper.insert(values);
	        }
	    //newDB.close();
		} catch ( Throwable t ) {
	        t.printStackTrace();
	    }
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
	}
}