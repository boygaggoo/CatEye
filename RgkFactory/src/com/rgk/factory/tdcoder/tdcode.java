package com.rgk.factory.tdcoder;


import com.rgk.factory.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class tdcode extends Activity implements View.OnClickListener{
	
    private View mPass;
	private View mFail;
	public static String TAG = "Tdcode";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tdcoderesult);
		InitButtons();
		SetOnClickListener();
	}
	
	private void SetOnClickListener() {
		mPass.setOnClickListener(this);
		mPass.setOnClickListener(this);
		mFail.setOnClickListener(this);
	}

	private void InitButtons() {
		mPass = findViewById(R.id.vibrator_pass);
		mFail = findViewById(R.id.vibrator_fail);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		   case R.id.vibrator_pass:
			   SendBroadcast("pass");
			   break;
		   case R.id.vibrator_fail:
			   SendBroadcast("fail");
			   break;
		}
	}
	
	public void SendBroadcast(String result){
		/*
		ResultHandle.SaveResultToSystem(result, TAG);
		sendBroadcast(new Intent(Config.ItemOnClick));
		sendBroadcast(new Intent(Config.ACTION_START_AUTO_TEST).putExtra("test_item", TAG));
		Cancelvibrate();
		finish();
		*/
	}

}
