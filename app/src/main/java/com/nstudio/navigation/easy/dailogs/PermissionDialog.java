package com.nstudio.navigation.easy.dailogs;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.nstudio.navigation.easy.R;


/**
 * Created by daxia on 2016/8/27.
 */
public class PermissionDialog extends DialogFragment implements View.OnClickListener {


    private boolean systemAlertPermission, accessibilityPermission;
    private Button But_intent_system_alert, But_intent_accessibility;

    public static PermissionDialog newInstance(boolean systemAlertPermission, boolean accessibilityPermission) {
        Bundle args = new Bundle();
        PermissionDialog fragment = new PermissionDialog();
        args.putBoolean("systemAlertPermission", systemAlertPermission);
        args.putBoolean("accessibilityPermission", accessibilityPermission);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        systemAlertPermission = getArguments().getBoolean("systemAlertPermission", false);
        accessibilityPermission = getArguments().getBoolean("accessibilityPermission", false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.dialog_permission, container);
        But_intent_system_alert =  rootView.findViewById(R.id.But_intent_system_alert);
        But_intent_system_alert.setOnClickListener(this);
        But_intent_accessibility =  rootView.findViewById(R.id.But_intent_accessibility);
        But_intent_accessibility.setOnClickListener(this);
        initButton();
        return rootView;
    }


    private void initButton() {
        if (!systemAlertPermission && !accessibilityPermission) {
            //Use layout default value
        } else if (systemAlertPermission && !accessibilityPermission) {
            But_intent_system_alert.setText(getString(R.string.Permission_allowed));
            But_intent_system_alert.setEnabled(false);
            But_intent_accessibility.setText(getString(R.string.Permission_goto_page));
            But_intent_accessibility.setEnabled(true);
        } else if (!systemAlertPermission && accessibilityPermission) {
            //User change the Permission without this dialog
            But_intent_system_alert.setText(getString(R.string.Permission_allow_system_alert_first_and_restart_service));
            But_intent_system_alert.setEnabled(true);
        }
    }

    private void gotoSettingPage() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    private void gotoDrawOverlaysPage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getActivity().getPackageName()));
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_intent_system_alert:
                gotoDrawOverlaysPage();
                break;
            case R.id.But_intent_accessibility:
                gotoSettingPage();
                break;
        }
        this.dismiss();
    }


}
