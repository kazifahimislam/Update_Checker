package com.example.updatechecker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.BuildConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkForUpdates(this);
    }

    private void checkForUpdates(Context context) {
        {
            int currentVersionCode = getAppVersionCode(this);

            DatabaseReference updateInfoRef = FirebaseDatabase.getInstance().getReference("update_info");

            updateInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer latestVersion = snapshot.child("latest_version").getValue(Integer.class);
                    String apkUrl = snapshot.child("apk_url").getValue(String.class);

                    if (latestVersion != null && latestVersion > currentVersionCode && apkUrl != null) {
                        showUpdateDialog(context, apkUrl);
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

    private int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1; // Error case
        }
    }

}