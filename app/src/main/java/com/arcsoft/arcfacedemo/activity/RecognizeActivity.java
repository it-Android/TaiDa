package com.arcsoft.arcfacedemo.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.Dialog.TipsDialog;
import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Service.BackstageSockectServices;
import com.arcsoft.arcfacedemo.Tool.Data.Attendance;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.SQL.Analysis.DataAnalysis;
import com.arcsoft.arcfacedemo.Tool.SQL.Modify.DataModify;
import com.arcsoft.arcfacedemo.Tool.System.SignIn;
import com.arcsoft.arcfacedemo.Tool.System.Utilities;
import com.arcsoft.arcfacedemo.faceserver.CompareResult;
import com.arcsoft.arcfacedemo.faceserver.FaceServer;
import com.arcsoft.arcfacedemo.model.DrawInfo;
import com.arcsoft.arcfacedemo.model.FacePreviewInfo;
import com.arcsoft.arcfacedemo.util.ConfigUtil;
import com.arcsoft.arcfacedemo.util.DrawHelper;
import com.arcsoft.arcfacedemo.util.camera.CameraHelper;
import com.arcsoft.arcfacedemo.util.camera.CameraListener;
import com.arcsoft.arcfacedemo.util.face.FaceHelper;
import com.arcsoft.arcfacedemo.util.face.FaceListener;
import com.arcsoft.arcfacedemo.util.face.RequestFeatureStatus;
import com.arcsoft.arcfacedemo.widget.FaceRectView;
import com.arcsoft.arcfacedemo.widget.ShowFaceInfoAdapter;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class RecognizeActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "RecognizeActivity";
    private static final int MAX_DETECT_NUM = 30; //最大检测数量

    /**
     * 当FR成功，活体未成功时，FR等待活体的时间
     */
    private static final int WAIT_LIVENESS_INTERVAL = 100;
    private CameraHelper cameraHelper; //初始化相机
    private DrawHelper drawHelper; //初始化画图类
    private Camera.Size previewSize;  //相机的像素
    private Toolbar toolbar;

    private String uName=null;
    /**
     * 优先打开的摄像头
     */

    private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT; //打开前置摄像头

    private FaceEngine faceEngine;
    private FaceHelper faceHelper;

    private List<CompareResult> compareResultList; // 对比结果列表

    private ShowFaceInfoAdapter adapter;

    private int afCode = -1;
    private ConcurrentHashMap<Integer, Integer> requestFeatureStatusMap = new ConcurrentHashMap<>();//hashMap
    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();//活体检测 hashMap
    private CompositeDisposable getFeatureDelayedDisposables = new CompositeDisposable();//组合包

    //判断当前页面
    private int recognize = 1;   //识别为1 注册为2

    //SystemClock.sleep(millis)
    //是一个类似于Thread.sleep(millis)的实用方法，但是它忽略InterruptedException异常。使用该函数产生的延迟如果你不使用Thread.interrupt()，因为它会保存线程的中断状态。
    /**
     * 相机预览显示的控件，可为SurfaceView或TextureView
     */
    private View previewView;
    /**
     * 绘制人脸框的控件  绘画人物边框类
     */
    private FaceRectView faceRectView;

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001; //权限
    private static final float SIMILAR_THRESHOLD = 0.8F; //similar threshold  相似度
    /**
     * 所需的所有权限信息
     */
    private TextView textView_pring;




    private Handler handler;//消息
    private Attendance oldAtt;//old消息
    private TipsDialog tipsDialog;//提示框

    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA, //摄像头
            Manifest.permission.READ_PHONE_STATE //动态获取权限
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        //标题导航栏
        toolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);//ActionBar更换为toobar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //保持亮屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();//初始化考勤数据
        //判断版本大于19的时候
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            //View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 控制NavigationBar的隐藏于显示。
            //View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  包
            // 含了View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 和 View.SYSTEM_UI_FLAG_FULLSCREEN
            getWindow().setAttributes(attributes);
            // Activity启动后就锁定为启动时的方向
            //getResources().getConfiguration().orientation 获取屏幕状态
            //锁定屏幕横屏方向
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            //本地人脸库初始化 初始faceserver对象  并初始化引擎

            FaceServer.getInstance().init(this, recognize);




            //   byte bytedata[]=FaceServer.getInstance().getbyte();
//
//           List<FaceRegisterInfo> b=FaceServer.getInstance().a();
//           String dataFace="";
//
//
//            for(FaceRegisterInfo fr:b){
//                dataFace +="姓名："+fr.getName();
//                dataFace +="工号："+fr.getNum();
//                dataFace +="性别："+fr.getSex();
//
//                dataFace +="\n";
//            }


          /*  try {
                textView_pring.setText(new String(bytedata,"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/




            previewView = findViewById(R.id.texture_preview);

            //在布局结束后才做初始化操作
            previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
            //初始化控件
            faceRectView = findViewById(R.id.face_rect_view);

            RecyclerView recyclerShowFaceInfo = findViewById(R.id.recycler_view_person); //recycler布局
            compareResultList = new ArrayList<>();
            adapter = new ShowFaceInfoAdapter(compareResultList, this);  //小框框
            recyclerShowFaceInfo.setAdapter(adapter);
            DisplayMetrics dm = getResources().getDisplayMetrics(); //获取分辨率
            int spanCount = (int) (dm.widthPixels / (getResources().getDisplayMetrics().density * 200 ));
            recyclerShowFaceInfo.setLayoutManager(new GridLayoutManager(this, spanCount));
            recyclerShowFaceInfo.setItemAnimator(new DefaultItemAnimator());

        }else{
            Toast.makeText(this,"Android版本过低！",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGlobalLayout() {
        previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            // 判断权限问题
            // 三个权限
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initEngine();
            initCamera();
        }
    }

    /**
     * 初始化引擎
     */
    private void initEngine() {
        faceEngine = new FaceEngine();
        //视频流初始化 引擎
        afCode = faceEngine.init(this, FaceEngine.ASF_DETECT_MODE_VIDEO, FaceEngine.
                        ASF_OP_0_ONLY,
                12, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_LIVENESS);
        //添加功能人脸识别 人脸检测 活体检测
        VersionInfo versionInfo = new VersionInfo(); // 多余
        faceEngine.getVersion(versionInfo); //多余
        Log.i(TAG, "initEngine:  init: " + afCode + "  version:" + versionInfo); //多余
        if (afCode != ErrorInfo.MOK) {
            Toast.makeText(this, getString(R.string.init_failed, afCode), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 销毁引擎
     */
    private void unInitEngine() {

        if (afCode == ErrorInfo.MOK) {
            afCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + afCode);
        }
    }

    @Override
    protected void onDestroy() {

        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }

        //faceHelper中可能会有FR耗时操作仍在执行，加锁防止crash
        if (faceHelper != null) {
            synchronized (faceHelper) {
                unInitEngine();
            }
            ConfigUtil.setTrackId(this, faceHelper.getCurrentTrackId());
            faceHelper.release();
        } else {
            unInitEngine();
        }
        if (getFeatureDelayedDisposables != null) {
            getFeatureDelayedDisposables.dispose();
            getFeatureDelayedDisposables.clear();
        }
        FaceServer.getInstance().unInit();



        super.onDestroy();
    }


    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }
    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //实现接口回调 faceListener
        final FaceListener faceListener = new FaceListener() {
            @Override
            public void onFail(Exception e) {
                Log.e(TAG, "onFail: " + e.getMessage());
            }

            //请求FR的回调
            @Override
            public void onFaceFeatureInfoGet(@Nullable final FaceFeature faceFeature, final Integer requestId) {
                //FR成功
                if (faceFeature != null) {
                    Log.i(TAG, "onPreview: fr end = " + System.currentTimeMillis() + " trackId = " + requestId);
                    //活体检测通过，搜索特征
                    if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.ALIVE) {
                        searchFace(faceFeature, requestId);
                    }
                    //活体检测未出结果，延迟100ms再执行该函数
                    else if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.UNKNOWN) {
                        //MILLISECONDS milliseconds
                        //订阅时间
                        getFeatureDelayedDisposables.add(Observable.timer(WAIT_LIVENESS_INTERVAL, TimeUnit.MILLISECONDS)
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) {
                                        onFaceFeatureInfoGet(faceFeature, requestId);
                                    }
                                }));
                    }
                    //活体检测失败
                    else {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.NOT_ALIVE);
                    }

                }
                //FR 失败
                else {
                    requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                }
            }

        };

        //  CameraHelper 调用接口回调 cameraListener.onCameraOpened(mCamera, mCameraId, displayOrientation, isMirror);
        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                previewSize = camera.getParameters().getPreviewSize();
                drawHelper = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, isMirror);

                faceHelper = new FaceHelper.Builder()
                        .faceEngine(faceEngine) //传入引擎
                        .frThreadNum(MAX_DETECT_NUM)
                        .previewSize(previewSize)
                        .faceListener(faceListener)
                        .currentTrackId(ConfigUtil.getTrackId(RecognizeActivity.this.getApplicationContext()))
                        .build();
            }


            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }

                List<FacePreviewInfo> facePreviewInfoList = faceHelper.onPreviewFrame(nv21); //人体活体检测 检测到



                if (facePreviewInfoList != null && faceRectView != null && drawHelper != null) {
                    List<DrawInfo> drawInfoList = new ArrayList<>();
                    for (int i = 0; i < facePreviewInfoList.size(); i++) {
                        Log.i("MSG","人物矩形： "+facePreviewInfoList.get(i).getFaceInfo().getRect()) ;
                        String name = faceHelper.getName(facePreviewInfoList.get(i).getTrackId());
                        drawInfoList.add(new DrawInfo(facePreviewInfoList.get(i).getFaceInfo().getRect(), GenderInfo.UNKNOWN, AgeInfo.UNKNOWN_AGE, LivenessInfo.UNKNOWN,
                                name == null ? "没有注册人脸信息" : name));//画图列表
                    }
                    drawHelper.draw(faceRectView, drawInfoList);



                }

                clearLeftFace(facePreviewInfoList); //清除人脸

                if (facePreviewInfoList != null && facePreviewInfoList.size() > 0 && previewSize != null) {

                    for (int i = 0; i < facePreviewInfoList.size(); i++) {

                        livenessMap.put(facePreviewInfoList.get(i).getTrackId(), facePreviewInfoList.get(i).getLivenessInfo().getLiveness());

                        /**
                         * 对于每个人脸，若状态为空或者为失败，则请求FR（可根据需要添加其他判断以限制FR次数），
                         * FR回传的人脸特征结果在{@link FaceListener#onFaceFeatureInfoGet(FaceFeature, Integer)}中回传
                         */
                        if (requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == null
                                || requestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId()) == RequestFeatureStatus.FAILED) {
                            Log.i("MSGa","线程1");
                            requestFeatureStatusMap.put(facePreviewInfoList.get(i).getTrackId(), RequestFeatureStatus.SEARCHING);
                            faceHelper.requestFaceFeature(nv21, facePreviewInfoList.get(i).getFaceInfo(), previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, facePreviewInfoList.get(i).getTrackId());
//                            Log.i(TAG, "onPreview: fr start = " + System.currentTimeMillis() + " trackId = " + facePreviewInfoList.get(i).getTrackId());
                        }
                    }
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                //初始化相机
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation); //90度 270 0 180
                }
                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };

        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(),previewView.getMeasuredHeight()))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(cameraID != null ? cameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
    }

//权限检查  回调不执行问题

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                initEngine();
                initCamera();
                if (cameraHelper != null) {
                    cameraHelper.start();
                }
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 删除已经离开的人脸
     * @param facePreviewInfoList 人脸和trackId列表
     */

    private void clearLeftFace(List<FacePreviewInfo> facePreviewInfoList) {
        Set<Integer> keySet = requestFeatureStatusMap.keySet();
        if (compareResultList != null) {
            for (int i = compareResultList.size() - 1; i >= 0; i--) {
                if (!keySet.contains(compareResultList.get(i).getTrackId())) {
                    compareResultList.remove(i);
                    adapter.notifyItemRemoved(i);
                }
            }
        }
        if (facePreviewInfoList == null || facePreviewInfoList.size() == 0) {
            requestFeatureStatusMap.clear();
            livenessMap.clear();
            return;
        }

        for (Integer integer : keySet) {
            boolean contained = false;
            for (FacePreviewInfo facePreviewInfo : facePreviewInfoList) {
                if (facePreviewInfo.getTrackId() == integer) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                requestFeatureStatusMap.remove(integer);
                livenessMap.remove(integer);
            }
        }
    }



    private void searchFace(final FaceFeature frFace, final Integer requestId) {
        Observable
                .create(new ObservableOnSubscribe<CompareResult>() {
                    @Override
                    public void subscribe(ObservableEmitter<CompareResult> emitter) {
//                        Log.i(TAG, "subscribe: fr search start = " + System.currentTimeMillis() + " trackId = " + requestId);
                        //在特征库进行搜索  如果比对成功就发送事件
                        CompareResult compareResult = FaceServer.getInstance().getTopOfFaceLib(frFace);
                        //compareResult 返回有两个参数 人脸的名字和对比最大相似度
//                        Log.i(TAG, "subscribe: fr search end = " + System.currentTimeMillis() + " trackId = " + requestId);
                        if (compareResult == null) {
                            emitter.onError(null);//跳出去
                        } else {
                            emitter.onNext(compareResult); //发送CompareResult的对象
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CompareResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CompareResult compareResult) {
                        if (compareResult == null || compareResult.getUser_Name() == null) {
                            //如果侦察的对象返回是空， 或者获取到名字为空
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED); //failed 失败
                            faceHelper.addName(requestId, "搜索不到面部" + requestId);
                            return;
                        }

//                        Log.i(TAG, "onNext: fr search get result  = " + System.currentTimeMillis() + " trackId = " + requestId + "  similar = " + compareResult.getSimilar());
                        //如果识别结果相似度大于0.8
                        if (compareResult.getSimilar() > SIMILAR_THRESHOLD) {
                            boolean isAdded = false;
                            if (compareResultList == null) {
                                requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                                faceHelper.addName(requestId, "没有注册人脸信息");
                                return;
                            }
                            for (CompareResult compareResult1 : compareResultList) {
                                if (compareResult1.getTrackId() == requestId) {
                                    isAdded = true;

                                    break;
                                }
                            }
                            if (!isAdded) {
                                //对于多人脸搜索，假如最大显示数量为 MAX_DETECT_NUM 且有新的人脸进入，则以队列的形式移除
                                if (compareResultList.size() >= MAX_DETECT_NUM) {
                                    compareResultList.remove(0);
                                    adapter.notifyItemRemoved(0);
                                }
                                //添加显示人员时，保存其trackId
                                compareResult.setTrackId(requestId);
                                compareResultList.add(compareResult);
                                adapter.notifyItemInserted(compareResultList.size() - 1);
                            }
                            if(compareResultList.get(0).getTimeCompare() == 1){

                                SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
                                uName=compareResult.getUser_Name();
                                DataAnalysis analysis=new DataAnalysis(handler);
                                analysis.getAttendance(sp.getString(OverallSituation.USER_IP,""),compareResult.getUser_Name(),Utilities.getDate());

                            }else if(compareResultList.get(0).getTimeCompare() == 2)
                            {
                                Toast.makeText(RecognizeActivity.this,"已经考勤成功！！", Toast.LENGTH_SHORT).show();
                            }else
                            {
                                Toast.makeText(RecognizeActivity.this,"登录失败！！", Toast.LENGTH_SHORT).show();
                            }
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                            faceHelper.addName(requestId, compareResult.getUser_Name());

                        } else {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            faceHelper.addName(requestId, "没有注册人脸信息");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //考勤时间段
    private void faceattendance(final CompareResult compareResult){

        /**
         * /允许打卡时间
         * 上午 上班：7：00 （70000） ~  8：00 （80000） 下班 ：    12：00（120000）~ 12:45（124500）
         * 下午 上班：12：45（124500） ~ 13：30（133000）  下班：  17：30（173000） ~ 18：15（181500）
         * 晚上 上班：18：15（181500） ~ 19：00（190000）  下班 ： 21：00（210000） ~ 21：30（213000）
         */
        final SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
        final DataModify dataModify=new DataModify(handler);

        final Map<String,Object> map= SignIn.getSignIn(Utilities.getTimeNumbers(),oldAtt);//获取信息
        final Attendance newAtt=(Attendance) map.get("attendance");
        Boolean normal=(Boolean) map.get("normal");
        String reason=(String)map.get("reason");

        if(normal){
            //String aa=compareResult.getUser_Name();
            dataModify.setAttendance(sp.getString(OverallSituation.USER_IP,""),uName,Utilities.getDate(),newAtt);//写入考勤记录
            //Toast.makeText(this,"考勤成功！",Toast.LENGTH_SHORT).show();
        }else{
            if(!reason.equals("系统异常!"))
            dataModify.setAttendance(sp.getString(OverallSituation.USER_IP,""),uName,Utilities.getDate(),newAtt);//写入考勤记录

        }
    }







    private void init(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 19://获取单个员工的某天的记录
                        oldAtt=(Attendance) msg.obj;
                        faceattendance(null);
                        break;
                    case 888:
                        if((Boolean)msg.obj){
                            Toast.makeText(RecognizeActivity.this,"考勤成功！", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(RecognizeActivity.this,"考勤失败！", Toast.LENGTH_SHORT).show();
                        }
                        break;

                }

            }
        };



    }


}
