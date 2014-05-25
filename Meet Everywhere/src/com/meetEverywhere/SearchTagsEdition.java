package com.meetEverywhere;

import android.content.Intent;
import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;

public class SearchTagsEdition extends TagsView {

	public void searchTags(View view) {
		Intent intent = new Intent(this, FoundTagsActivity.class);
    	intent.putStringArrayListExtra("tags", getTagsAsStrings());
    	startActivity(intent);
		
	}

	@Override
	public int getLayout() {
		return R.layout.search_tags_edition_layout;
	}

}
