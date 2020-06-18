package com.arcsoft.arcfacedemo.MyView;

import java.util.Random;

//返回二维码
public class VerificationCode {
	private int num=4;
	public VerificationCode() {
		// TODO Auto-generated constructor stub
	}
	
	public VerificationCode(int num) {
		// TODO Auto-generated constructor stub
		this.num=num;
	}
	

	public String getCode() {
		 Random r=new Random();
	        char[]ch="0123456789abcdefghijklnmopqrsguvwhyzABCDEFGHIJKLNMOPQRSTUVWXYZ".toCharArray();
	        StringBuffer buffer=new StringBuffer();
	        for(int i=0;i<num;i++) {
	            int pos=r.nextInt(ch.length);
	            char c=ch[pos];
	            buffer.append(c);
	        }
		return buffer.toString();
	}
	
	
}
