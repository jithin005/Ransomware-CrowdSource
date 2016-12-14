package com.amrita.shawn.ransomwarecrowd.app;

/**
 * Created by shawn on 12/8/16.
 */


public class Connection {
    private String ip;
    private String procName;

    public Connection(){

    }

    public Connection(String ip, String procName){

        this.ip = ip;
        this.procName = procName;
    }

    public String getIp(){
        return ip;
    }
    public String getProcName(){
        return procName;
    }
    public void setIp(String ip){
        this.ip = ip;
    }
    public void setProcName(String procName){
        this.procName = procName;
    }
}
