package com.nstudio.navigation.easy.view;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import com.nstudio.navigation.easy.FloatingViewService;
import com.nstudio.navigation.easy.common.SPFManager;

import static com.nstudio.navigation.easy.common.Link.GOOGLE_APP_PACKAGE_NAME;
import static com.nstudio.navigation.easy.common.Link.GOOGLE_PLAY_LINK;

/**
 * Created by daxia on 2017/4/26.
 */

public abstract class SoftKeyView {

    /*
     * View
     */
    protected View baseView;
    protected ImageButton imgBack, imgRecentApps, imgHome;
    protected FloatingViewService accessibilityService;
    /*
     *  Listener
     */
    private View.OnTouchListener baseViewTouchListener;
    private View.OnClickListener softKeyEventClickListener;
    private View.OnLongClickListener softKeyEventLongClickListener;

    /*
     * Configure
     */
    protected boolean stylusOnlyMode;
    protected boolean reverseFunctionButton;

    /*
     * Device value
     */
    protected int softkeyBarHeight;


    public SoftKeyView(FloatingViewService accessibilityService) {
        init(accessibilityService);
        loadConfigure();
        initBaseView();
        initImageButton();
        initBaseViewTheme();
        initTouchEvent();
        setSoftKeyEvent();

    }

    /*
     * The concrete method
     */



    /**
     * Link the base view & find the button view
     */
    abstract void initBaseView();

    /**
     * set the base view theme
     */
     abstract void initBaseViewTheme();
    /**
     * set the button
     */
    abstract void initImageButton();

    /**
     * Init Touch event for close the softkey bar
     */
    abstract void initTouchEvent();



    public abstract WindowManager.LayoutParams getLayoutParamsForLocation();


    private void init(FloatingViewService accessibilityService) {
        this.accessibilityService = accessibilityService;
    }

    /**
     * Get all configure from SPF.
     * It is also for refresh SPF or input new SPF.
     */
    public void refresh(){
        loadConfigure();
        initImageButton();
        initBaseViewTheme();
    }


    private void loadConfigure() {
        this.reverseFunctionButton = SPFManager.getReverseFunctionButton(accessibilityService);
        this.stylusOnlyMode = SPFManager.getStylusOnlyMode(accessibilityService);
    }

    private void setSoftKeyEvent() {
        softKeyEventClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add HapticFeedback
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                //Click event
                if (v.getId() == imgBack.getId()) {
                    if (reverseFunctionButton) {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                    } else {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                } else if (v.getId() == imgHome.getId()) {
                    accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);

                } else if (v.getId() == imgRecentApps.getId()) {
                    if (reverseFunctionButton) {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    } else {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                    }
                }

            }
        };
        softKeyEventLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == imgBack.getId()) {
                    if (reverseFunctionButton) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
                        }
                    }
                } else if (v.getId() == imgHome.getId()) {
                    Intent intent = accessibilityService.getPackageManager().getLaunchIntentForPackage(GOOGLE_APP_PACKAGE_NAME);
                    if (intent != null) {
                        // We found the activity now start the activity
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        accessibilityService.startActivity(intent);
                    } else {
                        // Bring user to the market or let them choose an app?
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.setData(Uri.parse(GOOGLE_PLAY_LINK + GOOGLE_APP_PACKAGE_NAME));
                        accessibilityService.startActivity(intent);
                    }

                } else if (v.getId() == imgRecentApps.getId()) {
                    if (!reverseFunctionButton) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
                        }
                    }
                }
                //Only trigger long click
                return true;
            }
        };

        //Set the click listener
        imgBack.setOnClickListener(softKeyEventClickListener);
        imgHome.setOnClickListener(softKeyEventClickListener);
        imgRecentApps.setOnClickListener(softKeyEventClickListener);

        //Set the long click listener
        imgBack.setOnLongClickListener(softKeyEventLongClickListener);
        imgHome.setOnLongClickListener(softKeyEventLongClickListener);
        imgRecentApps.setOnLongClickListener(softKeyEventLongClickListener);
    }


    /*
     * The  public  method
     */
    public View getBaseView() {
        return baseView;
    }


}
