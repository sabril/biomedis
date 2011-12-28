package com.sabril.biomedis;


import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CommClient {
	private Handler mHandler;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    //public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
    public CommClient(Context context, Handler handler){
        mState = STATE_NONE;
        mHandler = handler;
    }
    
    public synchronized int getState() {
        return mState;
    }
    
    private synchronized void setState(int state) {
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(GraphActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }
    
    public synchronized void start() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        
        setState(STATE_NONE);
    }
    
    public synchronized void connect(Biomedis app) {

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(app);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
    
    public synchronized void connected(Socket socket) {
    	// Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(GraphActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString("Aaaa", "AAAA");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }
    
    public synchronized void stop() {
    	if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        setState(STATE_NONE);
    }
    
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }
    
    private void connectionLost() {
        setState(STATE_NONE);
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(GraphActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(GraphActivity.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
    
    private void connectionFailed() {
        setState(STATE_NONE);
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(GraphActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(GraphActivity.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
    
    private class ConnectThread extends Thread{
    	private final Socket mSocket;
    	private final SocketAddress mAddr;
    	private final DataInputStream inputStream;
    	private final OutputStream out;
    	public ConnectThread(Biomedis app){
    		
    		SocketAddress tmp = null;
    		Socket tmpSock = null;
    		DataInputStream tmpIn = null;
    		OutputStream tmpOut = null;
    		try{
    			tmpSock = new Socket(InetAddress.getByName(app.getIpAddress()), Integer.parseInt(app.getPortNumber()));
    			tmpIn = new DataInputStream(tmpSock.getInputStream());
    			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(tmpSock.getOutputStream())), true);
                out.flush();
				tmpOut = tmpSock.getOutputStream();
    		}catch(IOException e){
    			Log.e("ClientActivity", "C: Error", e);
    		}
    		mAddr = tmp;
    		mSocket = tmpSock;
    		inputStream = tmpIn;
    		out = tmpOut;
    	}
    	
    	public void run(){
    		setName("ConnectThread");
            // Make a connection to the BluetoothSocket
            
                byte[] buffer = new byte[1024];
                int bytes;
                String output;
                // Keep listening to the InputStream while connected
                while (true) {
                    try {
                        // Read from the InputStream
                        bytes = inputStream.read(buffer);
                        output = inputStream.readLine();
                        mHandler.obtainMessage(GraphActivity.MESSAGE_READ, bytes, -1, output).sendToTarget();
                    } catch (IOException e) {
                    	e.printStackTrace();
                        //
                        connectionLost();
                        break;
                    }
                }
                // This is a blocking call and will only return on a  successful connection or an exception
                //mSocket.connect(mAddr);
            
            // Reset the ConnectThread because we're done
            synchronized (CommClient.this) {
                mConnectThread = null;
            }
            // Start the connected thread
            //connected(mSocket);
    	}
    	
    	public void cancel(){
    		try {
				mSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    private class ConnectedThread extends Thread{
    	private final Socket mSocket;
    	private final DataInputStream inputStream;
    	private final OutputStream out;
    	public ConnectedThread(Socket socket){
    		mSocket = socket;
    		DataInputStream tmpIn = null;
    		OutputStream tmpOut = null;
    		
    		try {
				tmpIn = (DataInputStream) socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		inputStream = tmpIn;
    		out = tmpOut;
    		
    	}
    	
    	public void run(){
            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = inputStream.read(buffer);
                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(GraphActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                	e.printStackTrace();
                    //
                    connectionLost();
                    break;
                }
            }
    	}
    	
        public void write(byte[] buffer) {
            try {
                out.write(buffer);
                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(GraphActivity.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                //
            }
        }
    	
        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                //
            }
        }
    }
}
