package com.example.mahasiswa;

public class ExamSchedule {
    private String Classes;
    private String Date;
    private String Time;
    private String Room;

    public ExamSchedule() {
    }

    public ExamSchedule(String classes, String date, String time, String room) {
        Classes = classes;
        Date = date;
        Time = time;
        Room = room;
    }

    public String getClasses() {
        return Classes;
    }

    public void setClasses(String classes) {
        Classes = classes;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }
}
