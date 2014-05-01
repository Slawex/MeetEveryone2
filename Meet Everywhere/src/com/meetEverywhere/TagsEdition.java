package com.meetEverywhere;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

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
        tags = getTagsFromDatabase();
        listAdapter = new InteractiveArrayAdapter(this, tags, (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE));
        //listAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, tags);
        tagListView.setAdapter(listAdapter);
        
        /*tagListView.setOnItemClickListener(new OnItemClickListener() {

            //@Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                    long id) {
            	view.findViewById(R.id.editText1).requestFocusFromTouch();
            	view.findViewById(R.id.editText1).requestFocus();
            }
        });*/
        
        registerForContextMenu(tagListView);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tags_edition, menu);
        return true;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tags_edition_context_menu, menu);
        
        menu.setHeaderTitle("Menu title :)");
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
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.addTag:
//        	tags.add(new Contact());
        	listAdapter.notifyDataSetChanged();
            break;
        case R.id.save:
        	saveAndExit();
        	break;
        case R.id.cancel:
        	finish();
        	break;
        case R.id.deleteAll:
        	for(Contact tag: tags)
/*        		if(tag.getId() != -1)
        			deletedTags.add(tag);
*/            	
        	tags.clear();
        	listAdapter.notifyDataSetChanged();
        	break;
        case R.id.copy:
        	List<Contact> list = new ArrayList<Contact>();
        	
    		Cursor dbCursor = myDBAdapter.getAllTags(DatabaseAdapter.TagType.SEARCH);
    		startManagingCursor(dbCursor);
    		dbCursor.requery();
    		  
    		if(dbCursor.moveToFirst()) {
    			do {
    				int id = dbCursor.getInt(myDBAdapter.ID_COLUMN);
    				String tag = dbCursor.getString(myDBAdapter.TAG_COLUMN);
    				int checkedInt = dbCursor.getInt(myDBAdapter.ACTIVE_TAG_COLUMN);
    				
    				list.add(new Contact(tag));
    		   } while (dbCursor.moveToNext());
    		  }
        	
        	tags.addAll(list);
        	listAdapter.notifyDataSetChanged();
    		
        	break;
        default:
            break;
        }
        return true;
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	Contact element = tags.get(((AdapterContextMenuInfo)item.getMenuInfo()).position);
    	
        switch (item.getItemId()) {
        case R.id.checkTag:
 //       	element.setSelected(!element.isSelected());
            break;
            
        case R.id.deleteTag:
/*        	if(element.getId() != -1)
        		deletedTags.add(element);
*/        	tags.remove(element);
            break;
            
        case R.id.editTag:
/*        	element.setStatus(Contact.Status.IN_EDITION);
        	element.setGiveFocus(true);
 */           break;
            
        default:
            break;
        }
        
        listAdapter.notifyDataSetChanged();
        return true;
    }
    
}
