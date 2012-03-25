package com.alexanderbatten.bluetoothfriends;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;


public class OnlineLeaderboard extends ListActivity {
	private static final String TAG = "OnlineLeaderboard";
	
	public static OnlineLeaderboard ACTIVE_INSTANCE = null;
	private List<UserScore> leaderboard;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		ACTIVE_INSTANCE = this;
		leaderboard = new ArrayList<UserScore>();
		setContentView(R.layout.onlineleaderboard_view);
		
		this.setTitle(this.getString(R.string.app_name) +" > Online Leaderboard");

		new DownloadOnlineLeaderboard(this).execute();
	}
	
	private class DownloadOnlineLeaderboard extends AsyncTask<Void,Void,Void>{
		ProgressDialog pd;

		private Context context;
		
		public DownloadOnlineLeaderboard(Context context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i(TAG, "onPreExecute");
			
			pd = ProgressDialog.show(ACTIVE_INSTANCE, "Please wait..", "Downloading current leaderboard", false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.i(TAG, "doInBackground");
			String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			
			URI uri;
			try {
				uri = new URI(
						"http",
						"alexanderbatten.com",
						"/sandbox/bluetoothfriends/getLeaderboard.php",
						"deviceID="+ android_id,
						null);
						Log.v(TAG, uri.toASCIIString());
						
						HttpClient client = new DefaultHttpClient();
						HttpConnectionParams.setConnectionTimeout(client.getParams(), 30000);
						HttpPost post = new HttpPost(uri);
						HttpResponse response = client.execute(post);
						
		    			if (response != null){
		    				InputStream in = response.getEntity().getContent();
		    				String a = convertStreamToString(in);		
		    				Log.v(TAG, "Server response.. "+ a);
		    				
							JSONObject json = new JSONObject(a);
							int count = 1;
							
							while(json.has("place_"+ count)){
								JSONObject place = json.getJSONObject("place_"+ count);
								Log.v(TAG, place.getString("userName") +" has "+ place.getInt("deviceCount"));
								leaderboard.add(new UserScore(place.getString("userName"), place.getInt("deviceCount")));
								count++;
							}
						
		    			}
						
							
			} catch (Exception e) {
				Log.e(TAG, "URI ERROR!");
				e.printStackTrace();
			} 
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.i(TAG, "onPostExecute");
			pd.dismiss();
			//TODO: Handle data and transfer to view
			
			displayList();
		}
	}
	
	private void displayList(){
		LeaderboardArrayAdapter adapter = new LeaderboardArrayAdapter(getApplicationContext(), R.layout.devicelist_row, leaderboard);
        this.setListAdapter(adapter);
	}
	
    private String convertStreamToString(InputStream is) {

	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
}
