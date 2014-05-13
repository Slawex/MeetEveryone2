package com.meetEverywhere;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class ErrorDialog {
	public static Dialog createDialog(Context context, List<ValidationError> errorMessages) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.error_dialog);
		StringBuilder builder = new StringBuilder();
		for(ValidationError error : errorMessages) {
			builder.append(context.getResources().getString(error.getMessageKey())).append("/n");
		}
		((TextView)dialog.findViewById(R.id.ErrorDialog_errorList)).setText(builder.toString());
		((TextView)dialog.findViewById(R.id.ErrorDialog_OKButton)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog.dismiss();
			}
			
		});
		return dialog;
	}
}
