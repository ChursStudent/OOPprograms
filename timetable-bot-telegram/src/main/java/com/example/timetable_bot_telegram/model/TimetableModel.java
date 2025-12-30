package com.example.timetable_bot_telegram.model;

import lombok.Data;

@Data
public class TimetableModel {
    public String teacher;
    public String second_teacher;
    public String subjectType;
    public Integer week;
    public String name = "no";
    public String start_time;
    public String end_time;
    public Integer start_time_seconds;
    public Integer end_time_seconds;
    public String room;
}