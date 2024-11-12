package com.example.updatechecker.update;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateChecker {
    public static void checkForUpdates(Context context) {
        {
            int currentVersionCode = getAppVersionCode(context);



            DatabaseReference updateInfoRef = FirebaseDatabase.getInstance().getReference("update_info");
            FirebaseApp.initializeApp(context);


            updateInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Integer latestVersion = snapshot.child("latest_version").getValue(Integer.class);
                    String apkUrl = snapshot.child("apk_url").getValue(String.class);

                    if (latestVersion != null && latestVersion > currentVersionCode && apkUrl != null) {
                        Toast.makeText(context, "New update available", Toast.LENGTH_SHORT).show();
                        new UpdateChecker().showUpdateDialog(context, apkUrl);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Failed to check for updates", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    // Method to show the update dialog


    // Method to initiate APK download and installation


    private void downloadAndInstallApk (String apkUrl, Context context){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl));
        context.startActivity(intent);
    }
    private void showUpdateDialog (Context context, String apkUrl){
        new AlertDialog.Builder(context)
                .setTitle("Update Available")
                .setMessage("A new version of the app is available. Do you want to update?")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadAndInstallApk(apkUrl, context);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1; // Error case
        }
    }
}
