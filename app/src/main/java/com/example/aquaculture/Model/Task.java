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
    private String receiverName;
    private String uploaderName;
    //private String receiveToken;

    public Task(){}

    public Task(String date, String receiver, String status, String task, String time, String uploader, String receiverName, String uploaderName) {
        this.date = date;
        this.receiver = receiver;
        this.status = status;
        this.task = task;
        this.time = time;
        this.uploader = uploader;
        this.receiverName = receiverName;
        this.uploaderName = uploaderName;
    }

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

    public Task(String date, String receiver, String status, String task, String time){
        this.date = date;
        this.receiver = receiver;
        this.status = status;
        this.task = task;
        this.time = time;
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

    public void setDate(String date) {this.date = date;}

    public void setReceiver(String receiver) {this.receiver = receiver;}

    public void setStatus(String status) {this.status = status;}

    public void setTask(String task) {this.task = task;}

    public void setTime(String time) {this.time = time;}

    public void setUploader(String uploader) {this.uploader = uploader;}

    public String getReceiverName() {return receiverName;}

    public void setReceiverName(String receiverName) {this.receiverName = receiverName;}

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }
}
