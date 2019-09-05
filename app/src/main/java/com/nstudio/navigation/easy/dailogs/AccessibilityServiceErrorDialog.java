package com.nstudio.navigation.easy.dailogs;

import android.app.Dialog;
import android.content.Intent;
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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * Created by daxia on 2016/8/27.
 */
public class AccessibilityServiceErrorDialog extends DialogFragment implements View.OnClickListener {


private Button But_go_to_accessibility;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        return dialog;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.dialog_accessibility_service_error, container);
        But_go_to_accessibility =  rootView.findViewById(R.id.But_go_to_accessibility);
        But_go_to_accessibility.setOnClickListener(this);
        return rootView;
    }



    private void gotoSettingPage() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_go_to_accessibility:
                gotoSettingPage();
                break;
        }
        this.dismiss();
    }


}
