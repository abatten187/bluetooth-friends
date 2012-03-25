package com.alexanderbatten.bluetoothfriends;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EncounterArrayAdapter extends ArrayAdapter<Encounter> {
	private static final String TAG = "EncounterArrayAdapter";
	
	private Context context;
	private List<Encounter> encounters;

	private TextView tvStartTime;
	private TextView tvFinishTime;
	private ImageView ivIcon;
	
	public EncounterArrayAdapter(Context context, int textViewResourceId, List<Encounter> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.encounters = objects;
	}
	
	public int getCount(){
		return this.encounters.size();
	}
	
	public Encounter getItem(int index){
		return this.encounters.get(index);
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.encounterlist_row, parent, false);
        }
        
        Encounter encounter = getItem(position);
        
        tvStartTime = (TextView) row.findViewById(R.id.encounterlistrow_starttime);
        tvFinishTime = (TextView) row.findViewById(R.id.encounterlistrow_finishtime);
        
        SimpleDateFormat format = new SimpleDateFormat("MMM d H:mm");
        tvStartTime.setText(format.format(encounter.getStarttime()));
        tvFinishTime.setText(format.format(encounter.getFinishtime()));

        try {
        	Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().getAssets().open("images/pin.png"));
            ivIcon = (ImageView) row.findViewById(R.id.encounterlistrow_icon);
        	ivIcon.setImageBitmap(bitmap);
        } catch (IOException e) {
        	e.printStackTrace();
        }
       
		return row;
	}
}
