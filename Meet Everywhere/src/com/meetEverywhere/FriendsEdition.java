package com.meetEverywhere;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FriendsEdition extends Activity {

	private final int PICK_CONTACT = 1;

	private Cursor dbCursor;
	private DatabaseAdapter myDBAdapter;

	private List<String> contacts = new ArrayList<String>();
	private List<Contact> fullContacts = new ArrayList<Contact>();
	private ArrayAdapter<String> adapter;
	private int initialSetContactsNum = 0;

	private ListView listView;

	/** Called when the activity is first created. */

	private String findNumber(String id, String name) {
		ContentResolver cr = getContentResolver();
		Cursor pCur = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
				new String[] { id }, null);

		while (pCur.moveToNext())
			return pCur
					.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

		pCur.close();

		return null;
	}

	private void getContacts() {
		dbCursor = myDBAdapter.getAllContacts();
		startManagingCursor(dbCursor);
		dbCursor.requery();

		if (dbCursor.moveToFirst()) {
			do {
				int id = dbCursor.getInt(myDBAdapter.ID_COLUMN);
				String name = dbCursor.getString(myDBAdapter.NAME_COLUMN);
				String num = dbCursor.getString(myDBAdapter.NUM_COLUMN);

				fullContacts.add(new Contact(name));
				contacts.add(name + " (" + num + ")");
			} while (dbCursor.moveToNext());
		}

		initialSetContactsNum = contacts.size();

		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		if (cur.getCount() > 0)
			while (cur.moveToNext()) {
				String id = cur.getString(cur
						.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				if (Integer
						.parseInt(cur.getString(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					String num = findNumber(id, name);
					// Contact newContact = new Contact(name, num);
					Contact newContact = new Contact(name);

					if (!fullContacts.contains(newContact)) {
						fullContacts.add(newContact);
						contacts.add(name + " (" + num + ")");
					}
				}
			}
		cur.close();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends_edition_layout);

		myDBAdapter = new DatabaseAdapter(getApplicationContext());
		myDBAdapter.open();

		getContacts();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, contacts);

		// Getting the reference to the listview object of the layout
		listView = (ListView) findViewById(R.id.listview);

		// Setting adapter to the listview
		listView.setAdapter(adapter);
		for (int i = 0; i < initialSetContactsNum; i++)
			listView.setItemChecked(i, true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.friends_edition, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.find:
			Intent intent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, PICK_CONTACT);
			break;
		case R.id.save:
			SparseBooleanArray positions = listView.getCheckedItemPositions();
			for (int i = 0; i < fullContacts.size(); i++) {
				Contact c = fullContacts.get(i);
				/*
				 * if(positions.get(i)){ if(c.getId() < 0)
				 * myDBAdapter.insertContact(c.getName(), c.getNumber()); } else
				 * if(c.getId() >= 0) myDBAdapter.deleteContact(c.getId());
				 */
			}

			finish();
			break;
		case R.id.cancel:
			finish();
			break;
		case R.id.selectAll:
			for (int i = 0; i < contacts.size(); i++)
				listView.setItemChecked(i, true);
			break;
		case R.id.deleteAll:
			for (int i = 0; i < contacts.size(); i++)
				listView.setItemChecked(i, false);
			break;
		case R.id.order:
			SparseBooleanArray checkedPos = listView.getCheckedItemPositions();
			int lastChecked = -1;
			for (int i = 0; i < contacts.size(); i++)
				if (checkedPos.get(i)) {
					lastChecked++;
					if (lastChecked != i) {
						String tmpC = contacts.get(lastChecked);
						contacts.remove(lastChecked);
						contacts.add(lastChecked, contacts.get(i - 1));
						contacts.remove(i);
						contacts.add(i, tmpC);

						Contact tmpC2 = fullContacts.get(lastChecked);
						fullContacts.remove(lastChecked);
						fullContacts.add(lastChecked, fullContacts.get(i - 1));
						fullContacts.remove(i);
						fullContacts.add(i, tmpC2);
					}
				}

			adapter.notifyDataSetChanged();

			for (int i = 0; i <= lastChecked; i++)
				listView.setItemChecked(i, true);
			for (int i = lastChecked + 1; i < contacts.size(); i++)
				listView.setItemChecked(i, false);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (PICK_CONTACT):
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				ContentResolver cr = getContentResolver();
				Cursor c = cr.query(contactData, null, null, null, null);
				if (c.moveToFirst()) {
					String id = c.getString(c
							.getColumnIndex(ContactsContract.Contacts._ID));
					String name = c
							.getString(c
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

					String hasPhoneNumber = c
							.getString(c
									.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
					if (Integer.parseInt(hasPhoneNumber) > 0) {
						String num = findNumber(id, name);
						Contact newContact = new Contact(name);
						int contactNum = fullContacts.indexOf(newContact);
						if (contactNum == -1) {
							contactNum = contacts.size();
							contacts.add(name + " (" + num + ")");
							fullContacts.add(newContact);
						}

						listView.setItemChecked(contactNum, true);
						listView.setSelection(contactNum);
					} else
						Toast.makeText(getApplicationContext(),
								"Ten kontakt nie zawiera numeru",
								Toast.LENGTH_LONG).show();
				}
				c.close();
			}
			break;
		}
	}
}
