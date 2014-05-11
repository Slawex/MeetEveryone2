package com.meetEverywhere;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public abstract class TagsView extends Activity {

	private ListView tagListView;
	private List<Contact> tags = new ArrayList<Contact>();
	private List<Contact> deletedTags = new ArrayList<Contact>();
	private ArrayAdapter<Contact> listAdapter;

	private DatabaseAdapter myDBAdapter;

	private List<Contact> getTagsFromDatabase() {
		List<Contact> list = new ArrayList<Contact>();

		Cursor dbCursor = myDBAdapter
				.getAllTags(DatabaseAdapter.TagType.SEARCH);
		startManagingCursor(dbCursor);
		dbCursor.requery();

		if (dbCursor.moveToFirst()) {
			do {
				int id = dbCursor.getInt(myDBAdapter.ID_COLUMN);
				String tag = dbCursor.getString(myDBAdapter.TAG_COLUMN);
				int checkedInt = dbCursor.getInt(myDBAdapter.ACTIVE_TAG_COLUMN);

				// list.add(new Contact(id, tag, checkedInt != 0,
				// Contact.Status.SAVED, false));
			} while (dbCursor.moveToNext());
		}

		return list;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_tags_edition_layout);

		myDBAdapter = new DatabaseAdapter(getApplicationContext());
		myDBAdapter.open();

		tagListView = (ListView) findViewById(R.id.tagsListView);
		tagListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		tags = getTagsFromDatabase();
		// listAdapter = new MyCustomAdapter(this,
		// R.layout.content_info,(ArrayList<Contact>) tags);
		// listAdapter = new
		// ArrayAdapter<Contact>(this,R.layout.content_info,tags);
		tagListView.setAdapter(listAdapter);
		
		tagListView.setOnItemClickListener(new OnItemClickListener() {
			   public void onItemClick(AdapterView<?> parent, View view,
			     int position, long id) {
			    // When clicked, show a toast with the TextView text
				   LinearLayout row = (LinearLayout) parent.getChildAt(position);
				   CheckBox cb = (CheckBox) row.findViewById(R.id.checkBox1);
				   cb.toggle();
				   ((MyCustomAdapter) listAdapter).changePosition(position);
			   }
			  });
		
		putTagsIntoList(tags);

		registerForContextMenu(tagListView);
	}

	@Override
	protected void onDestroy() {
		Toast.makeText(getApplicationContext(), "Czy zapisaæ?",
				Toast.LENGTH_LONG).show();

		myDBAdapter.close();
		super.onDestroy();
	}

	public void backToMainMenu(View view) {
		finish();
	}

	public void addTags(View view) {
		EditText editText = (EditText) findViewById(R.id.tagsTextField);
		String message = editText.getText().toString();

		if (TextUtils.isEmpty(message)) {
			editText.setError(getString(R.string.error_tags_required));
			editText.requestFocus();
			return;
		}

		String[] newTags = parseInput(message);

		for(String string : newTags){
			Log.d("parsowanie",string);
		}
		
		for (String tag : newTags) {
			if (!TextUtils.isEmpty(tag) && !tagsListContains(tag))
				tags.add(new Contact(tag));
		}

		putTagsIntoList(tags);
	}

	private void putTagsIntoList(List<Contact> tags2) {
		listAdapter = new MyCustomAdapter(this, R.layout.content_info,
				(ArrayList<Contact>) tags2);

		// listAdapter = new ArrayAdapter<String>(getApplicationContext(),
		// android.R.layout.simple_list_item_1, tags);
		tagListView.setAdapter(listAdapter);
		// listAdapter.notifyDataSetChanged();

	}

	private boolean tagsListContains(String tag) {

		for (Contact contact : tags) {
			if (contact.getName().equals(tag))
				return true;
		}

		return false;
	}

	public abstract void saveTags(View view);

	private String[] parseInput(String message) {
		String[] tags = message.split(",");
		for (int i = 0; i < tags.length; i++) {
			tags[i] = tags[i].trim();
		}

		return tags;
	}

	public void deleteTags(View view) {
		List<Contact> newTags = new ArrayList<Contact>();

		for (Contact tag : tags) {
			if (!tag.isChecked())
				newTags.add(tag);
		}

		tags = newTags;

		putTagsIntoList(newTags);
	}

}
