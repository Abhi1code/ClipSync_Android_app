package com.clipsync.clipsync_android.activity.adaptor;

public class Message_gs {

    private int id, status;
    private String name, msg, time;
    private boolean isself;

  public Message_gs(int id, String name, String msg, String time, int status, boolean isself){
      this.id = id;
      this.name = name;
      this.msg = msg;
      this.time = time;
      this.status = status;
      this.isself = isself;
  }

    public Message_gs(String name, String msg, String time, int status, boolean isself){
        this.name = name;
        this.msg = msg;
        this.time = time;
        this.status = status;
        this.isself = isself;
    }

    public Message_gs(String name, String msg, String time, boolean isself){
        this.name = name;
        this.msg = msg;
        this.time = time;
        this.isself = isself;
    }

    public int getId(){
      return id;
    }

    public int getStatus(){
      return status;
    }

    public String getName(){
      return name;
    }

    public String getMsg(){
      return msg;
    }

    public String getTime(){
      return time;
    }

    public boolean isSelf(){
      return isself;
    }
}
