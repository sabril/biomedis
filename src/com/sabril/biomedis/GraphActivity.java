package com.sabril.biomedis;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GraphActivity extends Activity{
	//private Timer mTimer;
	private TextView timeLabel, healthLabel;
	private long mStartTime;
	private Handler mHandler;
	private Socket socket;
	private LinearLayout ecg_section;
	private Biomedis app;
	
	
	private boolean connected = false;
	
	Thread fst;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphs);
        app = (Biomedis)getApplicationContext();
        timeLabel = (TextView)findViewById(R.id.mTimeLabel);
        healthLabel = (TextView)findViewById(R.id.mHealthLabel);
        
        ecg_section = (LinearLayout)findViewById(R.id.ecg_graph);
        
        fst = new Thread(new ClientThread());
        
        mHandler = new Handler();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.start:
        	Toast.makeText(this, "Starting..", Toast.LENGTH_LONG).show();
    		mStartTime = System.currentTimeMillis();
    		mHandler.removeCallbacks(Timer_Tick);
    		mHandler.postDelayed(Timer_Tick, 100);
    		//mHandler.post(Listen_Server);
        	fst.start();
            return true;
        case R.id.stop:
        	Toast.makeText(this, "Stopping..", Toast.LENGTH_LONG).show();
        	mHandler.removeCallbacks(Timer_Tick);
        	//mHandler.removeCallbacks(Listen_Server);
        	fst.stop();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			final long start = mStartTime;
		    long millis = System.currentTimeMillis() - start;
		    int seconds = (int) (millis / 1000);
		    int minutes = seconds / 60;
		    seconds     = seconds % 60;
		    if (seconds < 10) {
		    	timeLabel.setText("" + minutes + ":0" + seconds);
		    } else {
		    	timeLabel.setText("" + minutes + ":" + seconds);            
		    }
		    mHandler.postDelayed(this, 1000);
		    
		}
	};
	
	private void updateLabel(String time_string){
		healthLabel.setText(time_string);
	}
	
	private Runnable Listen_Server = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
            try {
                InetAddress serverAddr = InetAddress.getByName(app.getIpAddress());
                Log.d("ClientActivity", "C: Connecting...");
                socket = new Socket(serverAddr, Integer.parseInt(app.getPortNumber()));
                connected = true;
                while (connected) {
                    try {
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.flush();
                        String output = dataInputStream.readLine();
                        String[] temp = output.split(" ");
                        Log.d("ClientActivity", "" +temp[2]);
                        //updateLabel(temp[2]);
                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
		}
	};
	
	public class ClientThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
            try {
                InetAddress serverAddr = InetAddress.getByName(app.getIpAddress());
                Log.d("ClientActivity", "C: Connecting...");
                socket = new Socket(serverAddr, Integer.parseInt(app.getPortNumber()));
                connected = true;
                while (connected) {
                    try {
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.flush();
                        String output = dataInputStream.readLine();
                        String[] temp = output.split(" ");
                        Log.d("ClientActivity", "" +temp[2]);
                        //updateLabel(temp[2]);
                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
		}
		
	}
}
