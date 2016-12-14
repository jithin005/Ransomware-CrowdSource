package com.amrita.shawn.ransomwarecrowd.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.amrita.shawn.ransomwarecrowd.app.Connection;
import com.amrita.shawn.ransomwarecrowd.app.Utilities;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class Monitoring extends Service {


    ArrayList<Utilities.Connection> con;
    private List<Connection> conList = new ArrayList<Connection>();
    String pid = "";
    Integer j=0;
    String PID = "";
    Integer uid = 0;
    String UID = "";
    FileWriter writer;
    int serverResponseCode;

    Integer k=0;

    ActivityManager activitymanager;
    Context context;
    List<ActivityManager.RunningAppProcessInfo> RAP ;


    public Monitoring() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        new Thread()
        {
            public void run() {
                while(true){



                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ipaddress"+k.toString()+".txt";
                    File ipFile = new File(path);
                    try {
                            writer = new FileWriter(ipFile);
                    }catch(Exception e){

                    }






                    PackageManager pm = getApplicationContext().getPackageManager();
                    activitymanager = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

                    RAP = activitymanager.getRunningAppProcesses();

                    for(ActivityManager.RunningAppProcessInfo processInfo : RAP){
                        j = processInfo.pid;
                        PID = j.toString();
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
                            String processName = arr[arr.length - 1].split(":")[0];
                            packages.add(processName);

                            for (ApplicationInfo app : apps) {
                                if (app.processName.equals(processName)) {
                                    uid = app.uid;
                                    UID = uid.toString();
                                }
                            }

                            String appName;
                            ArrayList<String> ipadd;
                            Log.d(PID, UID);
                            ipadd = util.getPIDConnections(PID,UID);
                           // Log.d("Checking", inj.toString()+":"+processName);
                            if(ipadd.size()!=0) {
                                for (int i = 0; i < ipadd.size(); i++) {

                                    Connection connection = new Connection();
                                    connection.setIp(ipadd.get(i));
                                    try {
                                        appName = (String) pm.getApplicationLabel(pm.getApplicationInfo(processName, PackageManager.GET_META_DATA));
                                    }catch (Exception e){
                                        appName = processName;
                                    }
                                    try {
                                        writer.append(ipadd.get(i) + " : " + appName+"\n");
                                        writer.flush();
                                    }catch (Exception e){

                                    }

                                }
                            }


                        }
                        else
                            j++;
                    }
                    try {
                        writer.close();
                        Log.d("File name:", path);
                        uploadFile(path, "ipaddress"+k.toString()+".txt");
                        ipFile.delete();
                        k++;





                        Thread.sleep(120000);
                    }catch (Exception e){

                    }






                }
            }
        }.start();


        return START_STICKY;
    }





    public int uploadFile(String sourceFileUri, String name) {


        String fileName = name;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String SERVER_URL = "http://192.168.0.124:1234/ransom/ip.php";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
            return 0;
        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(SERVER_URL);
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);
                Log.i("checking","Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd );
                dos.writeBytes(lineEnd);
                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);
                if(serverResponseCode == 200){

                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return serverResponseCode;

        } // End else block
    }

}
