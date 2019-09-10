package com.nstudio.navigation.easy.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.Editable;
import android.util.Log;
import android.view.*;
import android.view.accessibility.AccessibilityEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.nstudio.navigation.easy.MainActivity;
import com.nstudio.navigation.easy.R;
import com.nstudio.navigation.easy.app.AppSettings;


public class FloatingViewService extends AccessibilityService {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private LinearLayout expandedView;
    private ImageView imgApps;
    private ImageView imgCloseApp;
    private ImageView imgMic;
    @SuppressLint("StaticFieldLeak")
    private static FloatingViewService sSharedInstance;
    private Context context;
    private boolean allowTraceEvent = false;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            action(view);
        }
    };

    public FloatingViewService() {

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            //Check the focused view is from Edittext object
            Class<?> clazz = Class.forName(event.getClassName().toString());
            if ((Editable.class.isAssignableFrom(clazz) || EditText.class.isAssignableFrom(clazz))
                   ) {
               // softKeyBar.getBaseView().setVisibility(View.GONE);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //do nothing
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        sSharedInstance = null;
        return false;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        sSharedInstance = this;
        updateServiceInfo(allowTraceEvent);
        updateView();
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("tag","create");

        context = FloatingViewService.this;

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


        imgMic = mFloatingView.findViewById(R.id.imgMic);
        imgApps = mFloatingView.findViewById(R.id.imgApps);
        ImageView imgExit = mFloatingView.findViewById(R.id.imgExit);
        ImageView imgBack = mFloatingView.findViewById(R.id.imgBack);
        ImageView imgHome = mFloatingView.findViewById(R.id.imgHome);
        ImageView imgRecent = mFloatingView.findViewById(R.id.imgRecent);
        imgCloseApp = mFloatingView.findViewById(R.id.imgCloseApp);
        ImageView imgHideButtons = mFloatingView.findViewById(R.id.close_button);

        imgExit.setOnClickListener(clickListener);
        imgBack.setOnClickListener(clickListener);
        imgHome.setOnClickListener(clickListener);
        imgRecent.setOnClickListener(clickListener);
        imgHideButtons.setOnClickListener(clickListener);
        imgMic.setOnClickListener(clickListener);
        imgApps.setOnClickListener(clickListener);
        imgCloseApp.setOnClickListener(clickListener);

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

    private void updateServiceInfo(boolean allowTraceEvent) {
        AccessibilityServiceInfo info = getServiceInfo();
        if (allowTraceEvent) {
            info.eventTypes = AccessibilityEvent.TYPE_VIEW_FOCUSED;
        } else {
            info.eventTypes = 0;
        }
        setServiceInfo(info);
    }

    public void updateView() {
        AppSettings settings = new AppSettings(context);
        if (settings.isShowMic()){
            imgMic.setVisibility(View.VISIBLE);
        }else {
            imgMic.setVisibility(View.GONE);
        }

        if (settings.isAppDrawer()){
            imgApps.setVisibility(View.VISIBLE);
        }else {
            imgApps.setVisibility(View.GONE);
        }
        if (settings.isCloseApp()){
            imgCloseApp.setVisibility(View.VISIBLE);
        }else {
            imgCloseApp.setVisibility(View.GONE);
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

    public void action(View view) {
        Log.e("access","id "+view.getId());
        switch (view.getId()){

            case R.id.imgApps:
                Log.e("access","imgApps");
                break;
            case R.id.imgBack:
                Log.e("access","imgBack");
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case R.id.imgCloseApp:
                Log.e("access","imgCloseApp");
                break;
            case R.id.imgExit:
                Log.e("access","imgExit");
                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                stopSelf();
                break;
            case R.id.imgHome:
                Log.e("access","imgHome");
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                break;
            case R.id.imgMic:
                Log.e("access","imgMic");

                break;
            case R.id.imgRecent:
                Log.e("access","imgRecent");
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
            case R.id.close_button:
                Log.e("access","close_button");

                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);

                break;
        }
    }

    public static FloatingViewService getsSharedInstance(){
        return sSharedInstance;
    }

}
