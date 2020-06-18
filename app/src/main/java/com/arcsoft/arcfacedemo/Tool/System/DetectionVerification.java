package com.arcsoft.arcfacedemo.Tool.System;


//数字验证
public class DetectionVerification {
    public DetectionVerification() {

    }

    public Boolean isNumber(String str,String s){

        String []aStr;
        aStr=str.split(s);
        for(String data:aStr){

            for(char ch: data.toCharArray()){
                if(!(ch>='0'&&ch<='9')){
                    return false;
                }
            }
        }
        return true;
    }
    public Boolean isNumber(String str){
        for(char ch: str.toCharArray()){
            if(!(ch>='0'&&ch<='9')){
                return false;
            }
        }
        return true;
    }
    public Integer isIp(String str){
        if(isNumber(str.trim())){//纯数字
            return 0;
        }
        if(!isNumber(str.trim(),"\\.")){//不全部是数字与点
            return 1;
        }

        String []aStr;
        aStr=str.trim().split("\\.");
        for(String data:aStr){
            if(data.equals("")||data.length()>3||aStr.length!=4||str.substring(str.length()-1, str.length()).equals(".")){//是否为IP格式
                return 2;
            }

            int num=Integer.parseInt(data);
            if(num<0||num>255) {//判断IP是否为0~255
                return 3;
            }

        }
        return -1;
    }

}
