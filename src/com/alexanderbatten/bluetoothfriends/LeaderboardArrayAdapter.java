package com.alexanderbatten.bluetoothfriends;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LeaderboardArrayAdapter extends ArrayAdapter<UserScore> {
	private List<UserScore> userScores;

	private TextView userName;
	private TextView deviceCount;
	
	public LeaderboardArrayAdapter(Context context, int textViewResourceId, List<UserScore> objects) {
		super(context, textViewResourceId, objects);
		this.userScores = objects;
	}
	
	public int getCount(){
		return this.userScores.size();
	}
	
	public UserScore getItem(int index){
		return this.userScores.get(index);
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.onlineleaderboard_row, parent, false);
        }
        
        UserScore userScore = getItem(position);
        
        userName = (TextView) row.findViewById(R.id.onlineleaderboard_userName);
        userName.setText(userScore.getUserName());
        
        deviceCount = (TextView) row.findViewById(R.id.onlineleaderboard_deviceCount);
        deviceCount.setText(Integer.toString(userScore.getDeviceCount()));
        
		return row;
	}
}
