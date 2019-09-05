package com.nstudio.navigation.easy;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.*;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.nstudio.navigation.easy.common.AppSettings;

public class FloatingViewServiceOld extends AccessibilityService implements View.OnClickListener{

    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private LinearLayout expandedView;

    private static FloatingViewServiceOld sSharedInstance;

    private Context context;

    public FloatingViewServiceOld() {


    }

    public static FloatingViewServiceOld getsSharedInstance(){
        return sSharedInstance;
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }
    @Override
    public boolean onUnbind(Intent intent) {
        sSharedInstance = null;
        return false;
    }


    @SuppressLint("InflateParams")
    @Override
    public void onCreate() {
        super.onCreate();



        context = FloatingViewServiceOld.this;

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (mWindowManager != null) {
            mWindowManager.addView(mFloatingView, params);
        }

        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        expandedView = mFloatingView.findViewById(R.id.expanded_container);

        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        sSharedInstance = this;

        updateView();
    }

    public void updateView() {
        ImageView imgApps,imgCloseApp,imgExit,imgBack,imgHome,imgRecent,imgMic, imgHideButtons;

        imgMic = mFloatingView.findViewById(R.id.imgMic);
        imgApps = mFloatingView.findViewById(R.id.imgApps);
        imgExit = mFloatingView.findViewById(R.id.imgExit);
        imgBack = mFloatingView.findViewById(R.id.imgBack);
        imgHome = mFloatingView.findViewById(R.id.imgHome);
        imgRecent = mFloatingView.findViewById(R.id.imgRecent);
        imgCloseApp = mFloatingView.findViewById(R.id.imgCloseApp);
        imgHideButtons = mFloatingView.findViewById(R.id.close_button);



        imgExit.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgHome.setOnClickListener(this);
        imgRecent.setOnClickListener(this);
        imgHideButtons.setOnClickListener(this);

        AppSettings settings = new AppSettings(context);


        if (settings.isShowMic()){
            imgMic.setVisibility(View.VISIBLE);
            imgMic.setOnClickListener(this);
        }
        if (settings.isAppDrawer()){
            imgApps.setVisibility(View.VISIBLE);
            imgApps.setOnClickListener(this);
        }
        if (settings.isCloseApp()){
            imgCloseApp.setVisibility(View.VISIBLE);
            imgCloseApp.setOnClickListener(this);
        }

        if (settings.getOrientation()==AppSettings.HORIZONTAL){
            expandedView.setOrientation(LinearLayout.HORIZONTAL);
        }else {
            expandedView.setOrientation(LinearLayout.VERTICAL);
        }
    }

    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.imgApps:
                break;
            case R.id.imgBack:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case R.id.imgCloseApp:
                break;
            case R.id.imgExit:
                Intent intent = new Intent(FloatingViewServiceOld.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                stopSelf();
                break;
            case R.id.imgHome:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                break;
            case R.id.imgMic:

                break;
            case R.id.imgRecent:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
            case R.id.close_button:

                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);

                break;
        }
    }
}
