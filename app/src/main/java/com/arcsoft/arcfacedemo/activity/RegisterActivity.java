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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;

import com.arcsoft.arcfacedemo.Service.BackstageSockectServices;
import com.arcsoft.arcfacedemo.Tool.Data.JsonNotice;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.Data.ServerData;
import com.arcsoft.arcfacedemo.Tool.Data.Staff;
import com.arcsoft.arcfacedemo.Tool.Json.Modify.JsonDataModify;
import com.arcsoft.arcfacedemo.Tool.SQL.Analysis.DataAnalysis;
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
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class RegisterActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener, RadioGroup.OnCheckedChangeListener{
    private static final String TAG = "RegisterActivity";
    private static final int MAX_DETECT_NUM = 10; //最大检测数量

    /**
     * 当FR成功，活体未成功时，FR等待活体的时间
     */
    private static final int WAIT_LIVENESS_INTERVAL = 100;
    private CameraHelper cameraHelper; //初始化相机
    private DrawHelper drawHelper; //初始化画图类
    private Camera.Size previewSize;  //相机的像素
    private Handler handler;
    private EditText e_name;//账号
    private EditText e_num;//姓名
    private EditText e_sex;
    private EditText e_zhiwei;//密码

    private BackstageSockectServices bss;//Service的操作对象

    private RadioGroup radioGroup;//容器

    private Toolbar toolbar;//

    private String en_name;//账号
    private String en_num;//姓名
    private String en_sex;//性别
    private String en_zhiwei;//密码

    // 注册界面
    private int regist = 2;

    private Staff oldsSaff=new Staff();





    /**
     * 优先打开的摄像头
     */
    private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT; //打开前置摄像头
    private FaceEngine faceEngine;
    private FaceHelper faceHelper;
    private List<CompareResult> compareResultList; // 对比结果列表

    /**
     * 注册人脸状态码，准备注册
     */
    private static final int REGISTER_STATUS_READY = 0;
    /**
     * 注册人脸状态码，注册中
     */
    private static final int REGISTER_STATUS_PROCESSING = 1;
    /**
     * 注册人脸状态码，注册结束（无论成功失败）
     */
    private static final int REGISTER_STATUS_DONE = 2;

    private int registerStatus = REGISTER_STATUS_DONE;

    private int afCode = -1;
    private ConcurrentHashMap<Integer, Integer> requestFeatureStatusMap = new ConcurrentHashMap<>();//hashMap
    private ConcurrentHashMap<Integer, Integer> livenessMap = new ConcurrentHashMap<>();//活体检测 hashMap
    private CompositeDisposable getFeatureDelayedDisposables = new CompositeDisposable();//组合包
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
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA, //摄像头
            Manifest.permission.READ_PHONE_STATE //动态获取权限
    };
    private int recognize=2;
    private boolean state;



    /**
     * 获取Services的对象
     */
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bss=((BackstageSockectServices.MyServicesBinder)service).getBinder();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //标题导航栏
        toolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);//ActionBar更换为toobar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //保持亮屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //开启服务
        Intent intentService=new Intent(RegisterActivity.this,BackstageSockectServices.class);
        final SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
        intentService.putExtra(OverallSituation.USER_IP,sp.getString(OverallSituation.USER_IP,""));
        intentService.putExtra(OverallSituation.USER_PORT,sp.getString(OverallSituation.USER_PORT,""));
        bindService(intentService,connection,BIND_AUTO_CREATE);

        //判断版本大于19的时候
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            //View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 控制NavigationBar的隐藏于显示。
            //View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  包
            // 含了View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 和 View.SYSTEM_UI_FLAG_FULLSCREEN
            getWindow().setAttributes(attributes);
        }

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 300:
                        oldsSaff=(Staff) msg.obj;
                        break;
                }
            }
        };


        e_name = findViewById(R.id.E_name);
        e_num = findViewById(R.id.E_num);
        e_zhiwei = findViewById(R.id.E_zhiwei);

//        SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
        e_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //文本改变前



            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //文本改变时


            }

            @Override
            public void afterTextChanged(Editable s) {
                //文本改变后，一般使用此方法
                DataAnalysis analysis=new DataAnalysis(handler);
                analysis.staffData(sp.getString(OverallSituation.USER_IP,""),s.toString().trim());
                //Toast.makeText(RegisterActivity.this,s.toString(),Toast.LENGTH_SHORT).show();

            }
        });


        radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(this);

        // Activity启动后就锁定为启动时的方向
        //getResources().getConfiguration().orientation 获取屏幕状态
        //锁定横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        //本地人脸库初始化 初始faceserver对象  并初始化引擎
        FaceServer.getInstance().init(this, recognize);

        previewView = findViewById(R.id.texture_preview);
        //在布局结束后才做初始化操作
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        //初始化控件
        faceRectView = findViewById(R.id.face_rect_view);

        compareResultList = new ArrayList<>();

        DisplayMetrics dm = getResources().getDisplayMetrics(); //获取分辨率
        int spanCount = (int) (dm.widthPixels / (getResources().getDisplayMetrics().density * 100 + 0.5f));
    }
    /**
     * 在{@link #previewView}第一次布局完成后，去除该监听，并且进行引擎和相机的初始化
     */
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

        //关闭sockect
        bss.setThreadStart(false);
        unbindService(connection);
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
//                    Log.i(TAG, "onPreview: fr end = " + System.currentTimeMillis() + " trackId = " + requestId);
                    //活体检测通过，搜索特征
                    if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.ALIVE) {
                        searchFace(faceFeature, requestId);
                    }
                    //活体检测未出结果，延迟100ms再执行该函数
                    else if (livenessMap.get(requestId) != null && livenessMap.get(requestId) == LivenessInfo.UNKNOWN) {
                        //MILLISECONDS milliseconds
                        //订阅时间

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
                        .currentTrackId(ConfigUtil.getTrackId(RegisterActivity.this.getApplicationContext()))
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
                    for (int i = 0; i < facePreviewInfoList.size(); i++)
                    {
                        Log.i("MSG","人物矩形： "+facePreviewInfoList.get(i).getFaceInfo().getRect()) ;
                        String name = faceHelper.getName(facePreviewInfoList.get(i).getTrackId());
                        drawInfoList.add(new DrawInfo(facePreviewInfoList.get(i).getFaceInfo().getRect(), GenderInfo.UNKNOWN, AgeInfo.UNKNOWN_AGE, LivenessInfo.UNKNOWN,
                                name == null ? "没有注册人脸信息" : name));//画图列表
                    }
                    drawHelper.draw(faceRectView, drawInfoList);
                }
              /*  Log.i("***************",registerStatus+"");
                Log.i("***************",facePreviewInfoList.size()+"");
                Log.i("***************",facePreviewInfoList.size()+"");*/
                if (registerStatus == REGISTER_STATUS_READY && facePreviewInfoList != null && facePreviewInfoList.size() > 0) {
                    registerStatus = REGISTER_STATUS_PROCESSING;
                    Observable.create(new ObservableOnSubscribe<Integer>() {
                        @Override
                        public void subscribe(ObservableEmitter<Integer> emitter) {
                            //nv21 对nv12对象字节数组进行克隆
                            if(!TextUtils.isEmpty(e_name.getText().toString().trim())  &&  !TextUtils.isEmpty(e_num.getText().toString().trim()) &&
                                    !TextUtils.isEmpty(e_zhiwei.getText().toString().trim()) ) {

                                en_name = e_name.getText().toString().trim();
                                en_num = e_num.getText().toString().trim();
                                en_zhiwei = e_zhiwei.getText().toString().trim();
                                //    Context context, byte[] nv21, int width, int height, String name,String num,String zhiwei,String sex
                                int j = FaceServer.getInstance().register(
                                        RegisterActivity.this, nv21.clone(),previewSize.width, previewSize.height, en_name, en_num, en_zhiwei,en_sex,false);

                                emitter.onNext(j);
                            }else
                            {
                                emitter.onNext(4);
                            }
                        }
                    }).subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Integer>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }
                                @Override
                                public void onNext(Integer flag) {
                                    Log.i("*****","返回码:"+flag);
                                    switch(flag)
                                    {
                                        case 1:
                                            Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                            registerStatus = REGISTER_STATUS_DONE;
                                            JsonNotice jsonNotice=new JsonNotice();
                                            jsonNotice.setFace(1);
                                            bss.socketOutput(new JsonDataModify().jsonNoticeToString(jsonNotice),(byte) 0x01);
                                            break;
                                        case 2:

                                            Toast.makeText(RegisterActivity.this, "已经注册，无须再次注册！", Toast.LENGTH_SHORT).show();
                                            registerStatus = REGISTER_STATUS_DONE;
                                            break;
                                        case 3:

                                            Toast.makeText(RegisterActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
                                            registerStatus = REGISTER_STATUS_DONE;
                                            break;
                                        case 4:

                                            Toast.makeText(RegisterActivity.this, "资料没有完善完整！", Toast.LENGTH_SHORT).show();
                                            registerStatus = REGISTER_STATUS_DONE;
                                            break;
                                        default:
                                            registerStatus = REGISTER_STATUS_DONE;
                                            break;
                                    }

                                }
                                @Override
                                public void onError(Throwable e) {


                                    Toast.makeText(RegisterActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
                                    registerStatus = REGISTER_STATUS_DONE;
                                }
                                @Override
                                public void onComplete() {
                                }
                            });
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
     *
     * @param facePreviewInfoList 人脸和trackId列表
     */
    private void clearLeftFace(List<FacePreviewInfo> facePreviewInfoList) {
        Set<Integer> keySet = requestFeatureStatusMap.keySet();
        if (compareResultList != null) {
            for (int i = compareResultList.size() - 1; i >= 0; i--) {
                if (!keySet.contains(compareResultList.get(i).getTrackId())) {
                    compareResultList.remove(i);

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
                        if (compareResult == null || compareResult.getUser_Name()== null) {
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

                                }
                                //添加显示人员时，保存其trackId
                                compareResult.setTrackId(requestId);
                                compareResultList.add(compareResult);

                            }
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.SUCCEED);
                            faceHelper.addName(requestId, compareResult.getUser_Name());

                        } else {
                            requestFeatureStatusMap.put(requestId, RequestFeatureStatus.FAILED);
                            faceHelper.addName(requestId, "没有注册人脸信息" + requestId);
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


    /**
     * 将准备注册的状态置为{@link #REGISTER_STATUS_READY}
     *
     * @param view 注册按钮
     */
    public void register(View view) {
        //判断账号，密码，姓名是否正确
        //String userna=oldsSaff.getUserName();

        if(oldsSaff!=null
                &&oldsSaff.getUserName().equals(e_name.getText().toString().trim())
                &&oldsSaff.getUserPassWord().equals(e_zhiwei.getText().toString().trim())
                &&oldsSaff.getName().equals(e_num.getText().toString().trim())){
            if (registerStatus == REGISTER_STATUS_DONE) {
                registerStatus = REGISTER_STATUS_READY;
            }
        }else{
            Toast.makeText(RegisterActivity.this,"账号或者密码不正确！",Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        switch (radioGroup.getCheckedRadioButtonId())
        {
            case R.id.radio_boy :
                en_sex = "男";
                break;
            case R.id.radio_girl :
                en_sex = "女";
                break;
        }

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
//    @Override
//    protected void onStop() {
//        super.onStop();
//        startActivity(new Intent(RegisterActivity.this,ControlActivity.class));
//    }
}
