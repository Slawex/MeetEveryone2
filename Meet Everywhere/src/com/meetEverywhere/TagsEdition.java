package com.meetEverywhere;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

/**
 *
 */
public class TagsEdition extends Activity {

	private ListView tagListView;
	private List<Contact> tags = new ArrayList<Contact>();
	private List<Contact> deletedTags = new ArrayList<Contact>();
	private ArrayAdapter<Contact> listAdapter;
	
	private DatabaseAdapter myDBAdapter;
	
	private List<Contact> getTagsFromDatabase() {
		List<Contact> list = new ArrayList<Contact>();
		
		Cursor dbCursor = myDBAdapter.getAllTags(DatabaseAdapter.TagType.USER);
		startManagingCursor(dbCursor);
		dbCursor.requery();
		  
		if(dbCursor.moveToFirst()) {
			do {
				int id = dbCursor.getInt(myDBAdapter.ID_COLUMN);
				String tag = dbCursor.getString(myDBAdapter.TAG_COLUMN);
				int checkedInt = dbCursor.getInt(myDBAdapter.ACTIVE_TAG_COLUMN);
				
//				list.add(new Contact(id, tag, checkedInt != 0, Contact.Status.SAVED, false));
		   } while (dbCursor.moveToNext());
		  }
		
		return list;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_edition_layout);
        
        myDBAdapter = new DatabaseAdapter(getApplicationContext());
        myDBAdapter.open();
        
        tagListView = (ListView)findViewById(R.id.tagsListView);
        tagListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        tags = getTagsFromDatabase();
        //listAdapter = new MyCustomAdapter(this, R.layout.content_info,(ArrayList<Contact>) tags);
        //listAdapter = new ArrayAdapter<Contact>(this,R.layout.content_info,tags);
        tagListView.setAdapter(listAdapter);
        putTagsIntoList(tags);
        
      
        
        registerForContextMenu(tagListView);
    }
    
    private void putTagsIntoList(List<Contact> tags2) {
        listAdapter = new MyCustomAdapter(this, R.layout.content_info,(ArrayList<Contact>) tags2);

         //listAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, tags);
         tagListView.setAdapter(listAdapter);
         //listAdapter.notifyDataSetChanged();
		
	}

	
    
    @Override
    protected void onDestroy() {
        myDBAdapter.close();
        super.onDestroy();
    }
    
    public void onBackPressed() {
    	boolean showDialog = false;
    	
    	for(Contact tag: tags)
/*    		if(tag.isEdited()){
    			showDialog = true;
    			break;
    		}
*/
    	if(!deletedTags.isEmpty())
    		showDialog = true;
    	
    	if(showDialog)
	    	new AlertDialog.Builder(this) 
	    	//set message, title, and icon
	    	.setTitle("Users tag edition") 
	    	.setMessage("Save changes before exit") 
	    	
	    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() { 
	    		public void onClick(DialogInterface dialog, int whichButton) { 
	    			saveAndExit();
	    		}
	    	})
	
	    	.setNegativeButton("No", new DialogInterface.OnClickListener() { 
	    		public void onClick(DialogInterface dialog, int whichButton) { 
	        	 finish();
	         } 
	    	})
	    	
	    	.show();
    	else
    		finish();
    }
    
	private void saveAndExit(){
    	for(Contact tag: tags)
/*    		if(tag.isEdited())
    			if(tag.getId() == -1)
    				myDBAdapter.insertTag(tag.getName(), tag.isSelected(), DatabaseAdapter.TagType.USER);
    			else
    				myDBAdapter.updateTag(tag.getId(), tag.getName(), tag.isSelected(), DatabaseAdapter.TagType.USER);

    	for(Contact tag: deletedTags)
    		myDBAdapter.deleteTag(tag.getId(), DatabaseAdapter.TagType.USER);
 */   	
    	finish();
	}
	
    
    public void backToMainMenu(View view){
    	finish();
    }
    
    public void addTags(View view){
    	EditText editText = (EditText) findViewById(R.id.tagsTextField);
    	String message = editText.getText().toString();
    	
    	if (TextUtils.isEmpty(message)) {
    		editText.setError(getString(R.string.error_tags_required));
    		editText.requestFocus();
    		return;
    	}
    	
    	String [] newTags = parseInput(message);
    	
    	for(String tag : newTags){
    		if(!TextUtils.isEmpty(tag) &&  !tagsListContains(tag))
    			tags.add(new Contact(tag));
    	}
    	
    	putTagsIntoList(tags);
    }
    
    private boolean tagsListContains(String tag) {
		
    	for(Contact contact : tags){
    		if(contact.getName().equals(tag))
    			return true;
    	}
    	
		return false;
	}

	public void saveTags(View view){
    	
    }
    
    private String [] parseInput(String message){
    	String [] tags=message.split(",");
    	for(int i=0;i<tags.length;i++){
    		tags[i]=tags[i].trim();
    	}
    	
    	
    	return tags;
    }
    
    public void deleteTags(View view){
    	List <Contact> newTags = new ArrayList<Contact>();
    	
    	for(Contact tag : tags){
    		if(!tag.isChecked())
    			newTags.add(tag);
    	}
    		
    	putTagsIntoList(newTags);
    }

  
}
