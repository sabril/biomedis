package com.sabril.biomedis;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GraphActivity extends Activity{
	private Biomedis app;
	//private Timer mTimer;
	private TextView timeLabel, healthLabel;
//	private long mStartTime;
	private Socket socket;
	private LinearLayout ecg_section;
	public WaveformView mWaveform = null;
	private CommClient mCommClient = null;
	
    public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String TOAST = "toast";
	
	// bt-uart constants
    private static final int MAX_SAMPLES = 640;
    private static final int  MAX_LEVEL	= 240;
    private static final int  DATA_START = (MAX_LEVEL + 1);
    private static final int  DATA_END = (MAX_LEVEL + 2);
    
    private static final byte  REQ_DATA = 0x00;
    private static final byte  ADJ_HORIZONTAL = 0x01;
    private static final byte  ADJ_VERTICAL = 0x02;
    private static final byte  ADJ_POSITION = 0x03;

    private static final byte  CHANNEL1 = 0x01;
    private static final byte  CHANNEL2 = 0x02;
    
    // Run/Pause status
    private boolean bReady = false;
    // receive data 
    private int[] ch1_data = new int[MAX_SAMPLES/2];
	private int[] ch2_data = new int[MAX_SAMPLES/2];
    private int dataIndex=0, dataIndex1=0, dataIndex2=0;
	private boolean bDataAvailable=false;
//	
//	
//	private boolean connected = false;
	
	//Thread fst;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphs);
        app = (Biomedis)getApplicationContext();
        timeLabel = (TextView)findViewById(R.id.mTimeLabel);
        healthLabel = (TextView)findViewById(R.id.mHealthLabel);
        ecg_section = (LinearLayout)findViewById(R.id.ecg_graph);

    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	Toast.makeText(this, "Start..", Toast.LENGTH_LONG).show();
    	
    	mCommClient = new CommClient(this, mHandler);
    	mWaveform = (WaveformView)findViewById(R.id.waveformView1);
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
        	mCommClient.connect(app);
            return true;
        case R.id.stop:
        	mCommClient.stop();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private final Handler mHandler = new Handler(){
    	@Override
        public void handleMessage(Message msg){
    		switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				
				break;
			case MESSAGE_READ:
				String read = (String) msg.obj;
				int data_length = msg.arg1;
				Log.v("ClientActivity", read);
				
//				for(int x=0; x< data_length; x++){
//					
//				}
				break;
			case MESSAGE_WRITE:
				break;
			case MESSAGE_DEVICE_NAME:
				Toast.makeText(getApplicationContext(), "Connected to: "
                        + app.getIpAddress(), Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                        Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
    	}
    };
    
//	private Runnable Timer_Tick = new Runnable() {
//		public void run() {
//			final long start = mStartTime;
//		    long millis = System.currentTimeMillis() - start;
//		    int seconds = (int) (millis / 1000);
//		    int minutes = seconds / 60;
//		    seconds     = seconds % 60;
//		    if (seconds < 10) {
//		    	timeLabel.setText("" + minutes + ":0" + seconds);
//		    } else {
//		    	timeLabel.setText("" + minutes + ":" + seconds);            
//		    }
//		    mHandler.postDelayed(this, 1000);
//		    
//		}
//	};
//	
//	private void updateLabel(String time_string){
//		healthLabel.setText(time_string);
//	}
//	
//	private Runnable Listen_Server = new Runnable() {
//		
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//            try {
//                InetAddress serverAddr = InetAddress.getByName(app.getIpAddress());
//                Log.d("ClientActivity", "C: Connecting...");
//                socket = new Socket(serverAddr, Integer.parseInt(app.getPortNumber()));
//                connected = true;
//                while (connected) {
//                    try {
//                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
//                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
//                        out.flush();
//                        String output = dataInputStream.readLine();
//                        String[] temp = output.split(" ");
//                        Log.d("ClientActivity", "" +temp[2]);
//                        //updateLabel(temp[2]);
//                    } catch (Exception e) {
//                        Log.e("ClientActivity", "S: Error", e);
//                    }
//                }
//                socket.close();
//                Log.d("ClientActivity", "C: Closed.");
//            } catch (Exception e) {
//                Log.e("ClientActivity", "C: Error", e);
//                connected = false;
//            }
//		}
//	};
//	
//	public class ClientThread implements Runnable{
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//            try {
//                InetAddress serverAddr = InetAddress.getByName(app.getIpAddress());
//                Log.d("ClientActivity", "C: Connecting...");
//                socket = new Socket(serverAddr, Integer.parseInt(app.getPortNumber()));
//                connected = true;
//                while (connected) {
//                    try {
//                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
//                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
//                        out.flush();
//                        String output = dataInputStream.readLine();
//                        String[] temp = output.split(" ");
//                        Log.d("ClientActivity", "" +temp[2]);
//                        //updateLabel(temp[2]);
//                    } catch (Exception e) {
//                        Log.e("ClientActivity", "S: Error", e);
//                    }
//                }
//                socket.close();
//                Log.d("ClientActivity", "C: Closed.");
//            } catch (Exception e) {
//                Log.e("ClientActivity", "C: Error", e);
//                connected = false;
//            }
//		}
//		
//	}
}
