package com.sabril.biomedis;

import android.app.Application;

public class Biomedis extends Application{
	private String ip_address="192.168.3.254", port_number="5000";
	
	public String getIpAddress(){
		return this.ip_address;
	}
	
	public void setIpAddress(String ip){
		this.ip_address = ip;
	}
	
	public String getPortNumber(){
		return this.port_number;
	}
	
	public void setPortNumber(String port){
		this.port_number = port;
	}
}
