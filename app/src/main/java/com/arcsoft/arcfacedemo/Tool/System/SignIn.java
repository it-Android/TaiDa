package com.arcsoft.arcfacedemo.Tool.System;

import com.arcsoft.arcfacedemo.Tool.Data.Attendance;

import java.util.HashMap;
import java.util.Map;

public class SignIn {
    public final static Map<String,Object> getSignIn(String time,Attendance oldAtt){
        int []amDate={70000,80000,120000,124559};
        int []pmDate={124600,133000,173000,181559};
        int []emDate={181600,190000,210000,213000};

        boolean allow=true;


        Map<String,Object> map=new HashMap<>();
        Attendance attendance=new Attendance();//考勤时间记录
        Boolean normal=true;//是否正常考勤
        String reason="";//原因
        time=time.replace(":","");





        try{
            int nowTime=Integer.valueOf(time);
            if(nowTime>0&&nowTime<amDate[0]){
                normal=false;
                reason="不在考勤时间";
                attendance.setAmAttendance("(过早考勤)"+Utilities.getTime());
                map.put("attendance","");
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }


            if(nowTime>=amDate[0]&&nowTime<=amDate[1]){//早上上班考勤
                normal=true;
                reason="在正常考勤时间内";
                attendance.setAmAttendance(""+Utilities.getTime());
                map.put("attendance",attendance);
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>amDate[1]&&nowTime<amDate[2]){//在上班期间考勤
                normal=false;
                if(oldAtt.getAmAttendance()==null||oldAtt.getAmAttendance().equals("")){//迟到
                    reason="考勤迟到，考勤时间为："+Utilities.getTime();
                    attendance.setAmAttendance("(迟到)"+Utilities.getTime());
                    map.put("attendance",attendance);
                    map.put("normal",normal);
                    map.put("reason",reason);
                    return map;
                }//早退
                reason="早退，考勤时间为："+Utilities.getTime();
                attendance.setAmNoAttendance("(早退)"+Utilities.getTime());
                map.put("attendance",attendance);
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>=amDate[2]&&nowTime<=amDate[3]){//上午下班考勤

                if(oldAtt.getAmNoAttendance()==null||oldAtt.getAmNoAttendance().equals("")){
                    normal=true;
                    reason="在正常考勤时间内";
                    attendance.setAmNoAttendance(""+Utilities.getTime());
                    map.put("attendance",attendance);
                    map.put("normal",normal);
                    map.put("reason",reason);
                    return map;
                }
                normal=false;
                reason="已有考勤记录，考勤记录为："+oldAtt.getAmNoAttendance();
                attendance.setAmNoAttendance("");
                map.put("attendance",attendance);
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>=pmDate[0]&&nowTime<=pmDate[1]){//下午上班考勤
                normal=true;
                reason="在正常考勤时间内";
                attendance.setPmAttendance(""+Utilities.getTime());
                map.put("attendance",attendance);
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>pmDate[1]&&nowTime<pmDate[2]){//在上班期间考勤
                normal=false;
                if(oldAtt.getPmAttendance()==null||oldAtt.getPmAttendance().equals("")){//迟到
                    reason="考勤迟到，考勤时间为："+Utilities.getTime();
                    attendance.setPmAttendance("(迟到)"+Utilities.getTime());
                    map.put("attendance",attendance);
                    map.put("normal",normal);
                    map.put("reason",reason);
                    return map;
                }//早退
                reason="早退，考勤时间为："+Utilities.getTime();
                attendance.setPmNoAttendance("(早退)"+Utilities.getTime());
                map.put("attendance",attendance);
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>=pmDate[2]&&nowTime<=pmDate[3]){//下午下班考勤

                if(oldAtt.getPmNoAttendance()==null||oldAtt.getPmNoAttendance().equals("")){
                    normal=true;
                    reason="在正常考勤时间内";
                    attendance.setPmNoAttendance(""+Utilities.getTime());
                    map.put("attendance",attendance);
                    map.put("normal",normal);
                    map.put("reason",reason);
                    return map;
                }
                normal=false;
                reason="已有考勤记录，考勤记录为："+oldAtt.getPmNoAttendance();
                attendance.setPmNoAttendance("");
                map.put("attendance",attendance);
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>=emDate[0]&&nowTime<=emDate[1]){//晚上上班考勤
                normal=true;
                reason="在正常考勤时间内";
                attendance.setNiAttendance(""+Utilities.getTime());
                map.put("attendance",attendance);
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }
            if(nowTime>emDate[1]&&nowTime<emDate[2]){//在上班期间考勤
                normal=false;
                if(oldAtt.getNiAttendance()==null||oldAtt.getNiAttendance().equals("")){//迟到
                    reason="考勤迟到，考勤时间为："+Utilities.getTime();
                    attendance.setNiNoAttendance("(迟到)"+Utilities.getTime());
                    map.put("attendance",attendance);
                    map.put("normal",normal);
                    map.put("reason",reason);
                    return map;
                }//早退
                reason="您属于早退，考勤时间为："+Utilities.getTime();
                attendance.setNiNoAttendance("(早退)"+Utilities.getTime());
                map.put("attendance",attendance);
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }


            if(nowTime>=emDate[2]&&nowTime<=emDate[3]){//晚上下班考勤
                if(oldAtt.getNiNoAttendance()==null||oldAtt.getNiNoAttendance().equals("")){
                    normal=true;
                    reason="在正常考勤时间内";
                    attendance.setNiNoAttendance(""+Utilities.getTime());
                    map.put("attendance",attendance);
                    map.put("normal",normal);
                    map.put("reason",reason);
                    return map;
                }
                normal=false;
                reason="已有考勤记录，考勤记录为："+oldAtt.getNiNoAttendance();
                attendance.setNiNoAttendance("");
                map.put("attendance",attendance);
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>emDate[3]){//晚上 最晚打卡时间的23：59：59
                normal=false;
                allow=false;
                reason="超过夜晚最晚打卡时间：21：30 将不给予考勤记录！";
                attendance.setNiNoAttendance("(不在考勤时间)"+Utilities.getTime());
                map.put("attendance",attendance);
                map.put("normal",normal);
                map.put("reason",reason);
                map.put("allow",allow);
                return map;
            }

        }catch (Exception e){}
        allow=false;
        map.put("attendance",attendance);
        map.put("normal",false);
        map.put("reason","考勤系统异常");
        map.put("allow",allow);
        return map;
    }
    public final static Map<String,Object> getSignIn(String time){
        int []amDate={70000,80000,120000,124559};
        int []pmDate={124600,133000,173000,181559};
        int []emDate={181600,190000,210000,213000};
        String aaa="非正常考勤时间，是否继续！";
        time=time.replace(":","");

        Map<String,Object> map=new HashMap<>();
        Boolean normal=true;//是否正常考勤
        String reason="";//原因
        try{
            int nowTime=Integer.valueOf(time);
            if(nowTime<amDate[0]){
                normal=false;
                reason=aaa;//原因
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>=amDate[0]&&nowTime<=amDate[1]){
                normal=true;
                reason="正常考勤!";//原因
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>amDate[1]&&nowTime<amDate[2]){
                normal=false;
                reason=aaa;//原因
                map.put("normal",normal);
                map.put("reason",reason);
                return map;

            }


            if(nowTime>=amDate[2]&&nowTime<=amDate[3]){
                normal=true;
                reason="正常考勤!";//原因
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }


            if(nowTime>=pmDate[0]&&nowTime<=pmDate[1]){
                normal=true;
                reason="正常考勤!";//原因
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }


            if(nowTime>pmDate[1]&&nowTime<pmDate[2]){
                normal=false;
                reason=aaa;//原因
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }


            if(nowTime>=pmDate[2]&&nowTime<=pmDate[3]){
                normal=true;
                reason="正常考勤!";//原因
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>=emDate[0]&&nowTime<=emDate[1]){
                normal=true;
                reason="正常考勤!";//原因
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>emDate[1]&&nowTime<emDate[2]){
                normal=false;
                reason=aaa;//原因
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>=emDate[2]&&nowTime<=emDate[3]){
                normal=true;
                reason="正常考勤!";//原因
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }

            if(nowTime>emDate[3]){
                normal=false;
                reason=aaa;//原因
                map.put("normal",normal);
                map.put("reason",reason);
                return map;
            }
        }catch (Exception e){}
        normal=false;
        reason="系统异常!";//原因
        map.put("normal",normal);
        map.put("reason",reason);
        return map;
    }

}
