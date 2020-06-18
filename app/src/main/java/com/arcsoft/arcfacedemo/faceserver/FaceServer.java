package com.arcsoft.arcfacedemo.faceserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import android.util.Log;


import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.Data.Staff;
import com.arcsoft.arcfacedemo.Tool.SQL.Modify.DataModify;
import com.arcsoft.arcfacedemo.Tool.System.Utilities;
import com.arcsoft.arcfacedemo.model.FaceRegisterInfo;
import com.arcsoft.arcfacedemo.util.ImageUtil;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人脸库操作类，包含注册和搜索
 */
public class FaceServer {

    private static final String TAG = "FaceServer";
    public static final String IMG_SUFFIX = ".jpg";
    private static FaceEngine faceEngine = null;
    private static FaceServer faceServer = null;

    private static List<FaceRegisterInfo> faceRegisterInfoList; //人脸特征

    private static List<TimeInfo> TimeList; //人脸特征

    public static String ROOT_PATH;

    public static final String SAVE_IMG_DIR = "register" + File.separator + "imgs";
    private static final String SAVE_FEATURE_DIR = "register" + File.separator + "features";



    private static  int  Success = 1;
    private static  int  Angine_Success = 2;
    private static  int  lose = 3;

    public static TimeHelp timeHelp;


    Map<String,Object> map=new HashMap<>();

    //当前模式
    public static int mode = 0;
    /**
     * 是否正在搜索人脸，保证搜索操作单线程进行
     */
    private boolean isProcessing = false;
    /**
     *
     * */


    //单例模式
    public static FaceServer getInstance() {
        if (faceServer == null) {
            synchronized (FaceServer.class) {
                if (faceServer == null) {
                    faceServer = new FaceServer();
                }
            }
        }
        return faceServer;
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     *
     *
     * @return 是否初始化成功
     *
     *
     */
    public boolean init(Context context, int i) {
        synchronized (this) {
            if (faceEngine == null && context != null) {

                faceRegisterInfoList = new ArrayList<>(); //初始化人脸特征

                TimeList = new ArrayList<>();//初始化TimeList

                faceEngine = new FaceEngine();
                mode = i;
                int engineCode = faceEngine.init(context, FaceEngine.ASF_DETECT_MODE_IMAGE, FaceEngine.ASF_OP_0_ONLY, 16, 4, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);
                if (engineCode == ErrorInfo.MOK) {
                    Log.e(TAG, "i*****" + engineCode+"*****");
                    initFaceList(context);
                    timeHelp = new TimeHelp("0:00--23:00");

                    return true;
                } else {
                    faceEngine = null;
                    Log.e(TAG, "init: failed! code = " + engineCode);
                    return false;
                }
            }
            return false;
        }




    }

    /**
     * 销毁
     */
    public void unInit() {
        synchronized (this) {
            if (faceRegisterInfoList != null) {
                faceRegisterInfoList.clear();
                TimeList.clear();
                TimeList = null;
                faceRegisterInfoList = null;
            }
            if (faceEngine != null) {
                faceEngine.unInit();
                faceEngine = null;
            }
        }
    }

    /**
     * 初始化人脸特征数据以及人脸特征数据对应的注册图
     *
     * @param context 上下文对象
     */
    private void initFaceList(Context context) {

        synchronized (this) {
            if (ROOT_PATH == null) {

                //获取当前安装包的绝对路径
                ROOT_PATH = context.getFilesDir().getAbsolutePath();



            }




            File featureDir = new File(ROOT_PATH + File.separator + SAVE_FEATURE_DIR);

            //File.separator 跨平台 斜杠
            if (!featureDir.exists() || !featureDir.isDirectory()) {
                return;
            }
            File[] featureFiles = featureDir.listFiles();
            if (featureFiles == null || featureFiles.length == 0) {
                return;
            }
         /*   faceRegisterInfoList = new ArrayList<>(); //初始化人脸特征//数据库拿数据
            TimeList = new ArrayList<>();*/
            for (File featureFile : featureFiles) {
                try {



                    //存储代码
                    String name = "";       //注册工号
                    String num = "";        //注册姓名
                    String zhiwei = "";     //注册密码
                    String sex = "";        //注册性别
                    String time = "";       //注册时间
                    boolean state  = false;


                    FileInputStream fis = new FileInputStream(featureFile);
                    BufferedReader bf = new BufferedReader(new InputStreamReader(fis)) ;//转为字符流

                    String str = null;

                    StringBuffer stringBuffer  = new StringBuffer();

                    while((str = bf.readLine()) != null){
                        stringBuffer.append(str);
                    }
                    fis.close();
                    bf.close();
                    String s_data = stringBuffer.toString().trim();
                    byte [] feature_1 = new byte[1032];
                    String[] s1 = s_data.split("@");

                    name = s1[1];

                    num = s1[2];

                    sex = s1[3];

                    zhiwei = s1[4];

                    state = Boolean.parseBoolean(s1[5]);

                    time = s1[6].trim();

                    feature_1 = HexStringbyte(s1[7]);





                    //    faceRegisterInfoList.add(new FaceRegisterInfo(feature, featureFile.getName()));
                    faceRegisterInfoList.add(new FaceRegisterInfo(feature_1,name,num,sex,zhiwei,time,state));
                    //   Log.i("MSG******************"+"!")
                    TimeList.add(new TimeInfo(name, Boolean.parseBoolean(s1[4]),null));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public int getFaceNumber(Context context) {
        synchronized (this) {
            if (context == null) {
                return 0;
            }
            if (ROOT_PATH == null) {
                ROOT_PATH = context.getFilesDir().getAbsolutePath();
            }

            File featureFileDir = new File(ROOT_PATH + File.separator + SAVE_FEATURE_DIR);

            int featureCount = 0;
            if (featureFileDir.exists() && featureFileDir.isDirectory()) {
                String[] featureFiles = featureFileDir.list();
                featureCount = featureFiles == null ? 0 : featureFiles.length;
            }
            int imageCount = 0;
            File imgFileDir = new File(ROOT_PATH + File.separator + SAVE_IMG_DIR);
            if (imgFileDir.exists() && imgFileDir.isDirectory()) {
                String[] imageFiles = imgFileDir.list();
                imageCount = imageFiles == null ? 0 : imageFiles.length;
            }
            return featureCount > imageCount ? imageCount : featureCount;
        }
    }

    public int clearAllFaces(Context context) {
        synchronized (this) {
            if (context == null) {
                return 0;
            }
            if (ROOT_PATH == null) {
                ROOT_PATH = context.getFilesDir().getAbsolutePath();
            }
            if (faceRegisterInfoList != null) {
                faceRegisterInfoList.clear();
            }
            File featureFileDir = new File(ROOT_PATH + File.separator + SAVE_FEATURE_DIR);
            int deletedFeatureCount = 0;
            if (featureFileDir.exists() && featureFileDir.isDirectory()) {
                File[] featureFiles = featureFileDir.listFiles();
                if (featureFiles != null && featureFiles.length > 0) {
                    for (File featureFile : featureFiles) {
                        if (featureFile.delete()) {
                            deletedFeatureCount++;
                        }
                    }
                }
            }
            int deletedImageCount = 0;
            File imgFileDir = new File(ROOT_PATH + File.separator + SAVE_IMG_DIR);
            if (imgFileDir.exists() && imgFileDir.isDirectory()) {
                File[] imgFiles = imgFileDir.listFiles();
                if (imgFiles != null && imgFiles.length > 0) {
                    for (File imgFile : imgFiles) {
                        if (imgFile.delete()) {
                            deletedImageCount++;
                        }
                    }
                }
            }
            return deletedFeatureCount > deletedImageCount ? deletedImageCount : deletedFeatureCount;
        }
    }

    /**
     * 注册人脸
     *
     * @param context 上下文对象
     * @param nv21    NV21数据
     * @param width   NV21宽度
     * @param height  NV21高度
     * @param name    保存的名字，可为空
     * @return 是否注册成功
     */
    public int register(final Context context, byte[] nv21, int width, int height, String name, String num, String zhiwei, String sex, boolean state) {
        Staff staff=new Staff();
        //人脸对比
        synchronized (this) {
            if (faceEngine == null || context == null || nv21 == null || width % 4 != 0 || nv21.length != width * height * 3 / 2) {
                return lose;
            }

            if (ROOT_PATH == null) {
                ROOT_PATH = context.getFilesDir().getAbsolutePath();
            }
            boolean dirExists = true;


            //特征存储的文件夹
            File featureDir = new File(ROOT_PATH + File.separator + SAVE_FEATURE_DIR);



            if (!featureDir.exists()) {
                dirExists = featureDir.mkdirs();
            }
            //判断是否获取成功
            if (!dirExists) {
                return lose;
            }
            //图片存储的文件夹
            File imgDir = new File(ROOT_PATH + File.separator + SAVE_IMG_DIR);




            if (!imgDir.exists()) {
                dirExists = imgDir.mkdirs();
            }
            if (!dirExists) {
                return lose;
            }
            //1.人脸检测
            List<FaceInfo> faceInfoList = new ArrayList<>();
            int code = faceEngine.detectFaces(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList); //截获图片 获取人脸
            if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                FaceFeature faceFeature = new FaceFeature();


                Log.i("*****","2");
                //2.特征提取 并检测是否相同 如果相同则已存在 注册失败
                code = faceEngine.extractFaceFeature(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList.get(0), faceFeature);
                Log.i("*****",code+"");

                CompareResult cr = getTopOfFaceLib(faceFeature);
                if(cr == null)
                {
                    Log.i("MSG","对象没有注册");
                }else
                {
                    return Angine_Success;
                }

                try {
                    //3.保存注册结果（注册图、特征数据）
                    if (code == ErrorInfo.MOK) {
                        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
                        //为了美观，扩大rect截取注册图
                        Rect cropRect = getBestRect(width, height, faceInfoList.get(0).getRect());
                        if (cropRect == null) {
                            return lose;
                        }

                        File file = new File(imgDir + File.separator + name + IMG_SUFFIX);


                        FileOutputStream fosImage = new FileOutputStream(file);




                        yuvImage.compressToJpeg(cropRect, 100, fosImage);





                        fosImage.close();






                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());


                        //将图片转换成二进制流
                        ByteArrayOutputStream bo=new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bo);

                        byte []faceImageData=bo.toByteArray();//人脸照片




                        //判断人脸旋转角度，若不为0度则旋转注册图
                        boolean needAdjust = false;
                        if (bitmap != null) {
                            switch (faceInfoList.get(0).getOrient()) {
                                case FaceEngine.ASF_OC_0:
                                    break;
                                case FaceEngine.ASF_OC_90:
                                    bitmap = ImageUtil.getRotateBitmap(bitmap, 90);
                                    needAdjust = true;
                                    break;
                                case FaceEngine.ASF_OC_180:
                                    bitmap = ImageUtil.getRotateBitmap(bitmap, 180);
                                    needAdjust = true;
                                    break;
                                case FaceEngine.ASF_OC_270:
                                    bitmap = ImageUtil.getRotateBitmap(bitmap, 270);
                                    needAdjust = true;
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (needAdjust) {
                            fosImage = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fosImage);
                            fosImage.close();
                        }
                        //转换成二进制流
                        FileInputStream fosInput = new FileInputStream(file.getAbsolutePath());
                        ByteArrayOutputStream outputStream =  new ByteArrayOutputStream();

                        byte[] buffer = new byte[2048];

                        int len = 0;

                        while ((len = fosInput.read(buffer)) != -1) {
                            outputStream.write(buffer,0, len);
                        }
                        outputStream.close();
                        fosInput.close();

                        byte  [] data_picture = outputStream.toByteArray();

                        outputStream.flush();

                        Log.i("MSG","data_picture:"+ Arrays.toString(data_picture));


                        //存储代码
                        StringBuffer sb = new StringBuffer();

                        sb.append("@"+name);
                        sb.append("@"+num);
                        sb.append("@"+sex);
                        sb.append("@"+zhiwei);
                        sb.append("@"+state);
                        sb.append("@"+timeHelp.getCurrentTime().trim());
                        sb.append("@"+bytes2HexString(faceFeature.getFeatureData()));

                        byte[] b_Data = sb.toString().getBytes();




                        FileOutputStream fosFeature = new FileOutputStream(featureDir + File.separator + name);



//                        String faceData=Utilities.bytes2HexString(b_Data);

                        // writeFile(context,"6544654sf4s4",Utilities.HexStringbyte(faceData));



                        DataModify dm=new DataModify(null);
                        staff.setUserName(name);
                        staff.setName(num);
                        staff.setUserPassWord(zhiwei);
                        staff.setUserSex(sex);
                        staff.setFace(Utilities.bytes2HexString(b_Data));//存人脸模板
//                        staff.setAge(0);
//
//
//                        staff.setPhone("10086");
                        staff.setFaceImage("0x"+Utilities.bytes2HexString(faceImageData));//存入人脸图片
//                        staff.setUserJob("小弟");
//                        staff.setE_Mail("123459@qq.com");
//
                        SharedPreferences sp=context.getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,Context.MODE_PRIVATE);

                        dm.setfase(sp.getString(OverallSituation.USER_IP,"192.168.1.6"),name,b_Data,faceImageData,sex);

                        //dm.setfase("192.168.1.3","123","");


                        fosFeature.write(b_Data);
                        fosFeature.close();





                        //内存中的数据同步
                        if (faceRegisterInfoList == null) {
                            faceRegisterInfoList = new ArrayList<>();
                        }
                        if (TimeList == null) {
                            faceRegisterInfoList = new ArrayList<>();
                        }

                        timeHelp.getCurrentTime();
                        faceRegisterInfoList.add(new FaceRegisterInfo(faceFeature.getFeatureData(),name,num,sex,zhiwei,timeHelp.getCurrentTime(),false));



                        TimeList.add(new TimeInfo(num,false,null));
                        return Success;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return lose;
        }


    }

    private void writeFile(Context context,String fileName,byte[] bytes) {

        if(bytes==null)return;

        if (ROOT_PATH == null) {
            //获取当前安装包的绝对路径
            ROOT_PATH = context.getFilesDir().getAbsolutePath();
        }
        //特征存储的文件夹
        File featureFileDir = new File(ROOT_PATH + File.separator + SAVE_FEATURE_DIR+File.separator+fileName);

        try {
            FileOutputStream out = new FileOutputStream(featureFileDir);//指定写到哪个路径中
            FileChannel fileChannel = out.getChannel();
            fileChannel.write(ByteBuffer.wrap(bytes)); //将字节流写入文件中
            fileChannel.force(true);//强制刷新
            fileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 在特征库中搜索
     *
     * @param faceFeature 传入特征数据
     * @return 比对结果
     */
    public CompareResult getTopOfFaceLib(FaceFeature faceFeature) {
        //判断其他都不为空的前提下
        if (faceEngine == null || isProcessing || faceFeature == null || faceRegisterInfoList == null || faceRegisterInfoList.size() == 0) {
            return null;
        }
        //初始化 人脸特征
        FaceFeature tempFaceFeature = new FaceFeature();
        //初始化 人脸相似器
        FaceSimilar faceSimilar = new FaceSimilar();
        float maxSimilar = 0;
        int maxSimilarIndex = -1;
        isProcessing = true; //判断是否进行单线程 人脸识别
        for (int i = 0; i < faceRegisterInfoList.size(); i++) {
            tempFaceFeature.setFeatureData(faceRegisterInfoList.get(i).getFeatureData());
            //通过方法存放在faceSimilar 里面
            faceEngine.compareFaceFeature(faceFeature, tempFaceFeature, faceSimilar);
            if (faceSimilar.getScore() > maxSimilar) {  //如果获取到的相似度大于0 就进行筛选出最大可能性的图片
                maxSimilar = faceSimilar.getScore();
                maxSimilarIndex = i;
            }
        }

        if (maxSimilarIndex != -1) {
            //    public CompareResult(String userName, float similar,  String user_num ,  String user_stateTime ,String user_state ) {
            isProcessing = false;
            //判断签到条件
            if (mode == 1)
            {
                int temp = timeHelp.CompareTime(TimeList.get(maxSimilarIndex).isState());
                if(temp == 1 || temp == 2)
                {
                    if(temp == 1)
                    {

                        TimeList.set(maxSimilarIndex, new TimeInfo(faceRegisterInfoList.get(maxSimilarIndex).getName(),true,timeHelp.getCurrentTime()));//修改对应的元素

                    }
                    //修改当前faceRegistInfoList信息
                    return new CompareResult(faceRegisterInfoList.get(maxSimilarIndex).getName(), maxSimilar,faceRegisterInfoList.get(maxSimilarIndex).getNum()
                            ,timeHelp.getCurrentTime().trim(), TimeList.get(maxSimilarIndex).isState(),temp,TimeList.get(maxSimilarIndex).getTime());
                }
            }

            //返回对比数据
            return new CompareResult(faceRegisterInfoList.get(maxSimilarIndex).getName(), maxSimilar,faceRegisterInfoList.get(maxSimilarIndex).getNum()
                    ,timeHelp.getCurrentTime().trim(), faceRegisterInfoList.get(maxSimilarIndex).isState(),0,TimeList.get(maxSimilarIndex).getTime());
        }


        return null;
    }

    /**
     * 将图像中需要截取的Rect向外扩张一倍，若扩张一倍会溢出，则扩张到边界，若Rect已溢出，则收缩到边界
     *
     * @param width   图像宽度
     * @param height  图像高度
     * @param srcRect 原Rect
     * @return 调整后的Rect
     */
    private static Rect getBestRect(int width, int height, Rect srcRect) {

        if (srcRect == null) {
            return null;
        }
        Rect rect = new Rect(srcRect);
        //1.原rect边界已溢出宽高的情况
        int maxOverFlow = 0;
        int tempOverFlow = 0;
        if (rect.left < 0) {
            maxOverFlow = -rect.left;
        }
        if (rect.top < 0) {
            tempOverFlow = -rect.top;
            if (tempOverFlow > maxOverFlow) {
                maxOverFlow = tempOverFlow;
            }
        }
        if (rect.right > width) {
            tempOverFlow = rect.right - width;
            if (tempOverFlow > maxOverFlow) {
                maxOverFlow = tempOverFlow;
            }
        }
        if (rect.bottom > height) {
            tempOverFlow = rect.bottom - height;
            if (tempOverFlow > maxOverFlow) {
                maxOverFlow = tempOverFlow;
            }
        }
        if (maxOverFlow != 0) {
            rect.left += maxOverFlow;
            rect.top += maxOverFlow;
            rect.right -= maxOverFlow;
            rect.bottom -= maxOverFlow;
            return rect;
        }
        //2.原rect边界未溢出宽高的情况
        int padding = rect.height() / 2;
        //若以此padding扩张rect会溢出，取最大padding为四个边距的最小值
        if (!(rect.left - padding > 0 && rect.right + padding < width && rect.top - padding > 0 && rect.bottom + padding < height)) {
            padding = Math.min(Math.min(Math.min(rect.left, width - rect.right), height - rect.bottom), rect.top);
        }

        rect.left -= padding;
        rect.top -= padding;
        rect.right += padding;
        rect.bottom += padding;
        return rect;
    }

    //字节数组转换成十六进制
    public static String bytes2HexString(byte[] data) {
        if (data == null) {
            throw new NullPointerException();
        }
        StringBuilder buffer = new StringBuilder();
        for (byte b : data) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                buffer.append('0');
            }
            buffer.append(hex);
        }
        return buffer.toString();
    }
    //十六进制转换成字节数组
    public static byte[] HexStringbyte(String s)
    {
        byte [] b  = new byte[s.length()/2];
        if(s.length()>0)
        {
            for(int i = 0 ; i < s.length()/2 ; i++)
            {
                Log.i("MSG",i+"");
                int i1 = Integer.parseInt(s.substring(i*2,i*2+2),16);
                byte i2 = (byte)(i1&0xff);
                Log.i("MSG",i2+"");
                b[i] = i2;
            }
        }
        return   b;
    }










    public List<FaceRegisterInfo> a(){
        return faceRegisterInfoList;
    };
}
