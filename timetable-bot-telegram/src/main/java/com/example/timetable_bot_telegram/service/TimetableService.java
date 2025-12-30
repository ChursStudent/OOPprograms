package com.example.timetable_bot_telegram.service;

import com.example.timetable_bot_telegram.model.SemesterModel;
import com.example.timetable_bot_telegram.model.TimetableModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TimetableService {
    public static String GetTimetable(int week, int group, int daySec, boolean near, boolean oneday, int weekday) throws IOException, ParseException {
        String urlString = "https://digital.etu.ru/api/mobile/schedule?weekDay=&subjectType=&groupNumber="+group+"&joinWeeks=true";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder jsonString = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            jsonString.append(output);
        }
        String[] russianDays = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
        StringBuilder result;
        StringBuilder currentLessons = new StringBuilder();
        currentLessons.append(jsonString);
        String delimiter = "\"lessons\":";
        int index;
        String botMessage = "";
        int day = 0;
        int dayOfFirst = 0;
        int bracketCount = 0;
        boolean lessonFound = false;
        boolean dayFound = false;
        boolean firstInDay;
        boolean firstInWeek = true;
        Gson gson = new Gson();
        Type listType = new TypeToken<List<TimetableModel>>() {}.getType();
        List<TimetableModel> lessonDay;
        TimetableModel lessonFirstDay = new TimetableModel();
        while (day < 7 && !lessonFound && !dayFound) {
            firstInDay = true;
            result = new StringBuilder();
            index = currentLessons.toString().indexOf(delimiter);
            if (index == -1 && day == 0) {
                botMessage += "Неизвестная группа";
                break;
            }
            currentLessons = new StringBuilder(currentLessons.substring(index + delimiter.length()));
            for (int i = 0; i < currentLessons.length(); i++) {
                result.append(currentLessons.charAt(i));
                if (currentLessons.charAt(i) == '[') {
                    bracketCount++;
                }
                if (currentLessons.charAt(i) == ']') {
                    bracketCount--;
                }
                if (bracketCount == 0) {
                    break;
                }
            }
            lessonDay = gson.fromJson(result.toString(), listType);
            for (TimetableModel lessonSingle : lessonDay) {
                if (firstInWeek && lessonSingle.week != week) {
                    lessonFirstDay.name = lessonSingle.name;
                    lessonFirstDay.room = lessonSingle.room;
                    lessonFirstDay.start_time = lessonSingle.start_time;
                    lessonFirstDay.end_time = lessonSingle.end_time;
                    dayOfFirst = day;
                    firstInWeek = false;
                }
                if (lessonSingle.week == 3 || lessonSingle.week == week) {
                    if (near) {
                        if ((86400*weekday + daySec - 86400*day - lessonSingle.end_time_seconds) < 0) {
                            botMessage = "Следующая пара это " + lessonSingle.name
                                    + (lessonSingle.room.isEmpty()?"":" в ауд. " + lessonSingle.room)
                                    + " с " + lessonSingle.start_time + " до " + lessonSingle.end_time + " (" + russianDays[day] + ")\n";
                            lessonFound = true;
                            break;
                        }
                    }
                    else if (oneday && day == weekday) {
                        botMessage += "-" + lessonSingle.name
                                + (lessonSingle.room.isEmpty()?"":" в ауд. " + lessonSingle.room)
                                + " с " + lessonSingle.start_time + " до " + lessonSingle.end_time + "\n";
                        dayFound = true;
                    }
                    else {
                        if (!oneday) {
                            if (firstInDay) {
                                botMessage += russianDays[day] + ":\n-" + lessonSingle.name
                                        + (lessonSingle.room.isEmpty()?"":" в ауд. " + lessonSingle.room)
                                        + " с " + lessonSingle.start_time + " до " + lessonSingle.end_time + "\n";
                                firstInDay = false;
                            } else botMessage += "-" + lessonSingle.name
                                    + (lessonSingle.room.isEmpty()?"":" в ауд. " + lessonSingle.room)
                                    + " с " + lessonSingle.start_time + " до " + lessonSingle.end_time + "\n";
                        }
                    }
                }
            }
            day++;
        }
        if (botMessage.isEmpty() && near) {
            botMessage = "Следующая пара это " + lessonFirstDay.name
                    + (lessonFirstDay.room.isEmpty()?"":" в ауд. " + lessonFirstDay.room)
                    + " с " + lessonFirstDay.start_time + " до " + lessonFirstDay.end_time + " (" + russianDays[dayOfFirst] + ")\n";
        }
        if (botMessage.isEmpty()) {
            botMessage = "В этот день нет пар";
        }
        return botMessage;
    }

    public static int getWeekNumber() throws IOException {
        String urlString = "https://digital.etu.ru/api/mobile/semester";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder jsonString = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            jsonString.append(output);
        }
        Gson gson = new Gson();
        SemesterModel Sem = gson.fromJson(jsonString.toString(), SemesterModel.class);
        int year, month, day;
        year = Sem.year;
        month = 10*(Sem.startDate.charAt(5) - '0') + Sem.startDate.charAt(6) - '0';
        day = 10*(Sem.startDate.charAt(8) - '0') + Sem.startDate.charAt(9) - '0';
        LocalDate today = LocalDate.now();
        LocalDate dateStart = LocalDate.of(year, month, day);
        long daysBetween = ChronoUnit.DAYS.between(dateStart, today);
        return (int)(daysBetween / 7) % 2 + 1;
    }
}
