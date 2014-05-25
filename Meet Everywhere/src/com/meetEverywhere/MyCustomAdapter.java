package com.meetEverywhere;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MyCustomAdapter extends ArrayAdapter<Tag> {
 
  private ArrayList<Tag> countryList;
  private Activity activity;
 
  public MyCustomAdapter(Context context, int textViewResourceId,
    ArrayList<Tag> contactList) {
   super(context, textViewResourceId, contactList);
   this.activity=(Activity) context;
   this.countryList = new ArrayList<Tag>();
   this.countryList.addAll(contactList); 
  }
 
  private class ViewHolder {
   TextView code;
   CheckBox name;
  }
 
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
 
   ViewHolder holder = null;
   Log.v("ConvertView", String.valueOf(position));
 
   if (convertView == null) {
   LayoutInflater vi = (LayoutInflater)activity.getSystemService(
     Context.LAYOUT_INFLATER_SERVICE);
   convertView = vi.inflate(R.layout.content_info, null);
 
   holder = new ViewHolder();
   holder.code = (TextView) convertView.findViewById(R.id.tag_name);
   holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
   convertView.setTag(holder);
 
    holder.name.setOnClickListener( new View.OnClickListener() { 
     public void onClick(View v) { 
      CheckBox cb = (CheckBox) v ; 
      Tag country = (Tag) cb.getTag(); 
      /*Toast.makeText(activity.getApplicationContext(),
       "Clicked on Checkbox: " + cb.getText() +
       " is " + cb.isChecked(),
       Toast.LENGTH_LONG).show();*/
      if(country!=null)
    	  country.setChecked(cb.isChecked());
     } 
    }); 
   }
   else {
    holder = (ViewHolder) convertView.getTag();
   }
 
   if(countryList.size()>0){
	   Tag country = countryList.get(position);
	   holder.code.setText(country.getName());
	   holder.name.setChecked(country.isChecked());
	   holder.name.setTag(country);
   }
 
   return convertView;
 
  }
 
 }