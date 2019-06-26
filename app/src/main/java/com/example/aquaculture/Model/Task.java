package com.example.aquaculture.Model;

import com.google.firebase.database.PropertyName;

public class Task {
    //private String TaskId;
    private String date;
    private String receiver;
    private String status;
    private String task;
    private String time;
    private String uploader;
    //private String receiveToken;

    public Task(){}

    public Task(String date, String receiver, String status, String task, String time, String uploader){
        //this.TaskId = TaskId;
        this.date = date;
        this.receiver = receiver;
        this.status = status;
        this.task = task;
        this.time = time;
        this.uploader = uploader;
        //this.receiveToken = receiveToken;
    }
    //@PropertyName("TaskId")
    //public String getTaskId() {
      //  return TaskId;
    //}
    //@PropertyName("receiveToken")
    //public String getReceiveToken() {
     //   return receiveToken;
    //}
    @PropertyName("date")
    public String getDate() {
        return date;
    }
    @PropertyName("receiver")
    public String getReceiver() {
        return receiver;
    }
    @PropertyName("status")
    public String getStatus() {
        return status;
    }
    @PropertyName("task")
    public String getTask() {
        return task;
    }
    @PropertyName("time")
    public String getTime() {
        return time;
    }
    @PropertyName("uploader")
    public String getUploader() {
        return uploader;
    }

}
