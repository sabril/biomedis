package com.sabril.biomedis;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnClickListener {
	private EditText ip_addr; 
	private EditText port_num;
	private Biomedis app;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        ip_addr = (EditText)findViewById(R.id.editText1);
        port_num = (EditText)findViewById(R.id.editText2);
        
        Button save_button = (Button)findViewById(R.id.button1);
        save_button.setOnClickListener(this);
        
        app = (Biomedis)getApplicationContext();
        
        // set default values
        ip_addr.setText(app.getIpAddress());
        port_num.setText(app.getPortNumber());
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		app.setIpAddress(ip_addr.getText().toString());
		app.setPortNumber(port_num.getText().toString());
		Toast.makeText(this, "Configuration successfully saved", Toast.LENGTH_LONG).show();
	}
}
