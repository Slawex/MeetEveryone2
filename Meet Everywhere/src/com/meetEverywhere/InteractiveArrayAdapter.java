package com.meetEverywhere;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Klasa s³u¿¹ca do wykreowania listy znalezionych kontaktów (jako ArrayAdapter).
 * Rozwa¿yæ zmianê -> zwyk³y listview + listenery zamiast adaptera.
 */
public class InteractiveArrayAdapter extends ArrayAdapter<Contact> {

  private final List<Contact> list;
  private final Activity context;
  private final InputMethodManager imm;
  private List<Contact> listenersMap = new ArrayList<Contact>();
  
  public InteractiveArrayAdapter(Activity context, List<Contact> list, InputMethodManager imm) {
    super(context, R.layout.tags_rows_layout, list);
    this.context = context;
    this.list = list;
    this.imm = imm;
  }

  static class ViewHolder {
    protected TextView text;
    protected EditText tmpText;
    protected CheckBox checkbox;
    protected Button saveButton;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = null;
    if (convertView == null) {
      LayoutInflater inflator = context.getLayoutInflater();
      view = inflator.inflate(R.layout.tags_rows_layout, null);
      final ViewHolder viewHolder = new ViewHolder();
      viewHolder.text = (TextView) view.findViewById(R.id.label);
      viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
      viewHolder.checkbox
          .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            //@Override
            public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
              Contact element = (Contact) viewHolder.checkbox
                  .getTag();
   //           element.setSelected(buttonView.isChecked());

            }
          });
      
      viewHolder.tmpText = (EditText) view.findViewById(R.id.editText1);
      viewHolder.saveButton = (Button) view.findViewById(R.id.button1);
      
      view.setTag(viewHolder);
    } else {
      view = convertView;
    }
    
    Contact element = list.get(position);
    ViewHolder holder = (ViewHolder) view.getTag();
    holder.checkbox.setTag(element);
    holder.text.setText(element.getName());

    
	int pos = listenersMap.size();
	holder.tmpText.setId(pos);
	holder.saveButton.setId(pos);
	listenersMap.add(element);

	//we need to update adapter once we finish with editing
	holder.tmpText.setOnFocusChangeListener(new OnFocusChangeListener() {
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus){
				final int position = v.getId();
	//			listenersMap.get(position).setTmpText(((EditText) v).getText().toString());
			}
		}
	});
	/*
	if(element.isInEdition()){
		holder.text.setVisibility(View.GONE);
		holder.tmpText.setVisibility(View.VISIBLE);
		holder.saveButton.setVisibility(View.VISIBLE);
		element.setToken(holder.tmpText.getWindowToken());
		
		holder.saveButton.setOnClickListener(new OnClickListener() {
	        //@Override
	        public void onClick(View arg0) {
        		Contact element = listenersMap.get(arg0.getId());
        		element.setStatus(Contact.Status.EDITED);
	        	imm.hideSoftInputFromWindow(element.getToken(), 0);
	        	notifyDataSetChanged();
	        }
	    });
		
		
		holder.saveButton.setOnFocusChangeListener (new OnFocusChangeListener() {
	        //@Override
	        public void onFocusChange(View arg0, boolean hasFocus) {
	        	if(hasFocus){
	        		Contact element = listenersMap.get(arg0.getId());
	        		element.setStatus(Contact.Status.EDITED);
		        	imm.hideSoftInputFromWindow(element.getToken(), 0);
		        	notifyDataSetChanged();
	        	}
	        }
	    });
		
		if(element.isGiveFocus()){
			//element.setGiveFocus(false);
			//holder.tmpText.requestFocus();
			//imm.toggleSoftInputFromWindow(element.getToken(), 0, InputMethodManager.SHOW_IMPLICIT);
		}
	} else {
		holder.text.setVisibility(View.VISIBLE);
		holder.tmpText.setVisibility(View.GONE);
		holder.saveButton.setVisibility(View.GONE);
	}
	*/
    return view;
  }
} 