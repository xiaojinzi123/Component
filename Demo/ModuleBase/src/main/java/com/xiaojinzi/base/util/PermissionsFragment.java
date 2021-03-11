package com.xiaojinzi.base.util;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

public class PermissionsFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSIONS = 0x001234;
    private static final int REQUEST_CODE_SETTING = 0x0012345;
    private PermissionsCallback mCallback;
    private String[] mPermissions;

    public static PermissionsFragment getInstance() {
        return new PermissionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String[] permissions, PermissionsCallback callback) {
        mPermissions = permissions;
        mCallback = callback;
        requestPermissions(mPermissions, REQUEST_CODE_PERMISSIONS);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CODE_PERMISSIONS) return;
        boolean isAllGranted = true;
        for (int i = 0, size = permissions.length; i < size; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                log("onRequestPermissionsResult: " + permissions[i] + " is Granted");
            } else {
                log("onRequestPermissionsResult: " + permissions[i] + " is Denied");
                isAllGranted = false;
            }
        }
        if (isAllGranted) {
            mCallback.onResult(true);
        } else {
            showPermissionDeniedDialog();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isGranted(String permission) {
        return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isRevoked(String permission) {
        return getActivity().getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showPermissionDeniedDialog() {
        //启动当前App的系统设置界面
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("帮助")
                .setMessage("当前应用缺少必要权限")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.onResult(false);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 启动当前App的系统设置界面
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                        startActivityForResult(intent, REQUEST_CODE_SETTING);
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_SETTING) return;
        // 从设置页面回来时, 重新检测一次申请的权限是否都已经授权
        boolean isAllGranted = true;
        for (String permission : mPermissions) {
            if (!isGranted(permission)) {
                isAllGranted = false;
                break;
            }
        }
        mCallback.onResult(isAllGranted);
    }

    void log(String message) {
        Log.i(PermissionsUtil.TAG, message);
    }
}