package com.rgk.factory;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CheckPwdActivity extends Activity {

	private EditText pwdEditText;
	private Button confirmBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.check_pwd_layout);
		
		pwdEditText = (EditText) findViewById(R.id.passwordEd);
		confirmBtn = (Button) findViewById(R.id.citConfirm);
		
		confirmBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                   if(pwdEditText.getText().toString().equals("1234")) {
                	/*
                	PackageManager packageManager = getPackageManager();
                	Intent intent = packageManager.getLaunchIntentForPackage("com.rgk.factory");
                	startActivity(intent);
                        */
                        Intent intent=new Intent();
                        intent.setClass(CheckPwdActivity.this,MainActivity.class);
                        startActivity(intent);
                	finish();
                   } else {
                	Toast.makeText(CheckPwdActivity.this, "√‹¬Î¥ÌŒÛ£¨«Î÷ÿ–¬ ‰»Î£°", Toast.LENGTH_SHORT).show();
                	pwdEditText.getText().clear();
                   }
                }   
            });
		
	  }	
}
