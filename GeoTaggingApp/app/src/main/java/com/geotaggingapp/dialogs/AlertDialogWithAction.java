/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.geotaggingapp.dialogs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.geotaggingapp.MainActivity;
import com.geotaggingapp.R;


public class AlertDialogWithAction {

    private Context mContext;
    private AlertDialog.Builder mDialogBuilder;

    public AlertDialogWithAction(Context context) {
        this.mContext = context;
        mDialogBuilder = new AlertDialog.Builder(mContext);
    }


    /**
     * Method to show alert dialog on respective permission result.
     * @param permissionType Permission type for which manifest permission will be requested.
     * @param requestCode Request code for respective permission.
     */
    public void showDialogForAccessPermission(final String permissionType, final int requestCode) {

        String message = "";
        switch (permissionType) {
            case Manifest.permission.CAMERA:
                message = mContext.getResources().getString(R.string.allow_camera_permission);
            break;
        }

        mDialogBuilder.setMessage(message);

        mDialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) mContext,
                                new String[]{Manifest.permission.CAMERA}, MainActivity.PERMISSION_CAMERA);
                }
        });
        mDialogBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel), null);

        mDialogBuilder.create().show();
    }
}
