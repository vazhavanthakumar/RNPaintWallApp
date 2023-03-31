package com.wallpaintapp.paintwall;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class WallPaintModuleActivity extends ReactContextBaseJavaModule {

    public WallPaintModuleActivity(ReactApplicationContext context) {
        super(context);
    }

    @NonNull
    @Override
    public String getName() {
        return "OpenCVAndroidModule";
    }

    @ReactMethod
    public void openWallPaintActivity() {
        try {
            Toast.makeText(getCurrentActivity(), "Native android component launched", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getCurrentActivity(), PaintWallMainActivity.class);
            getCurrentActivity().startActivity(intent);
        } catch (NullPointerException e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }
    }

}
