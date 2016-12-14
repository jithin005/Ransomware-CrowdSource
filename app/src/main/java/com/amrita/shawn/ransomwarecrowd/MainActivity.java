package com.amrita.shawn.ransomwarecrowd;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.amrita.shawn.ransomwarecrowd.app.Config;
import com.amrita.shawn.ransomwarecrowd.app.Connection;
import com.amrita.shawn.ransomwarecrowd.app.Utilities;
import com.amrita.shawn.ransomwarecrowd.service.Monitoring;
import com.amrita.shawn.ransomwarecrowd.util.CustomListAdapter;
import com.amrita.shawn.ransomwarecrowd.util.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {



    private List<Connection> conList = new ArrayList<Connection>();
    Integer j=0;
    String PID = "";
    Integer uid = 0;
    String UID = "";

    ActivityManager activitymanager;
    List<ActivityManager.RunningAppProcessInfo> RAP ;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private CustomListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(MainActivity.this);
        final ListView listView =  (ListView) findViewById(R.id.listview);

        Intent serviceIntent = new Intent(getApplicationContext(), Monitoring.class);
        startService(serviceIntent);

        adapter = new CustomListAdapter(this, conList);
        listView.setAdapter(adapter);
        PackageManager pm = getApplicationContext().getPackageManager();


        activitymanager = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        RAP = activitymanager.getRunningAppProcesses();

        for(ActivityManager.RunningAppProcessInfo processInfo : RAP){
            j = processInfo.pid;
            PID = j.toString();
            Log.d("vannu",PID);
        }

        Utilities util = new Utilities();
        List<String> stdout = Shell.SH.run("ps");
        List<String> packages = new ArrayList<>();

        // Get a list of all installed apps on the device.
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        int j = 0;
        for (String line : stdout) {
            if(j!=0) {

                String[] arr = line.split("\\s+");
                String pID = arr[1].split(":")[0];
                String ppID = arr[2].split(":")[0];
                String processName = arr[arr.length - 1].split(":")[0];
                packages.add(processName);
                Log.d("vannu", processName);

                for (ApplicationInfo app : apps) {
                    String appName = app.loadLabel(pm).toString();
                    if (app.processName.equals(processName)) {
                        uid = app.uid;
                        UID = uid.toString();
                    }
                }

                String appName;
                ArrayList<String> ipadd;
                Log.d(PID, UID);
                ipadd = util.getPIDConnections(PID,UID);
                Integer inj = ipadd.size();
                Log.d("Checking", inj.toString()+":"+processName);
                if(ipadd.size()!=0) {
                    for (int i = 0; i < ipadd.size(); i++) {

                        Connection connection = new Connection();
                        connection.setIp(ipadd.get(i));
                        try {
                            appName = (String) pm.getApplicationLabel(pm.getApplicationInfo(processName, PackageManager.GET_META_DATA));
                        }catch (Exception e){
                            appName = processName;
                        }

                        connection.setProcName(appName);
                        conList.add(connection);


                    }
                }
                ipadd.clear();

            }
            else
                j++;
        }

        adapter.notifyDataSetChanged();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Delete the app: " + message, Toast.LENGTH_LONG).show();


                }
            }
        };

        displayFirebaseRegId();



    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("TAG", "Firebase reg id: " + regId);

    }
    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
