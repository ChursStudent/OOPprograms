package com.example.timetable_bot_telegram.service;

import com.example.timetable_bot_telegram.config.BotConfig;
import com.google.gson.JsonSyntaxException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Scanner;
import static com.example.timetable_bot_telegram.service.TimetableService.getWeekNumber;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String timetable = "";
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            System.out.println("Получено сообщение: " + messageText + " от " + chatId);
            String msgWord;
            Calendar calendar = Calendar.getInstance();
            int msgGroup = 0, msgWeek = 0;
            int msgDaySec = LocalTime.now().toSecondOfDay();
            int msgWeekday = calendar.get(Calendar.DAY_OF_WEEK) - 2;
            if (msgWeekday == -1) {
                msgWeekday = 6;
            }
            boolean msgNear = false, msgOneday = false;
            Scanner scanner = new Scanner(messageText);
            msgWord = scanner.next();
            boolean wrongInput = false;
            switch (msgWord) {
                case "/start":
                    sendMessage(chatId, "Используйте следующие команды для расписания:\n" +
                            "near_lesson группа - ближайшее занятие для указанной группы;\n" +
                            "DAY неделя группа - расписание занятий в указанный день (monday, tuesday, ...);\n" +
                            "tomorrow группа - расписание на следующий день;\n" +
                            "all неделя группа - расписание на всю неделю;\n" +
                            "week, чтобы узнать чётность текущей недели.");
                    break;
                default: {
                    switch (msgWord) {
                        case "near_lesson":
                            if (scanner.hasNextInt()) {
                                msgGroup = scanner.nextInt();
                            } else wrongInput = true;
                            try {
                                msgWeek = getWeekNumber();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            msgNear = true;
                            msgOneday = false;
                            msgWeekday = msgWeekday;
                            break;
                        case "monday":
                            if (scanner.hasNextInt()) {
                                msgWeek = scanner.nextInt();
                            } else wrongInput = true;
                            if (scanner.hasNextInt()) {
                                msgGroup = scanner.nextInt();
                            } else wrongInput = true;
                            msgNear = false;
                            msgOneday = true;
                            msgWeekday = 0;
                            break;
                        case "tuesday":
                            if (scanner.hasNextInt()) {
                                msgWeek = scanner.nextInt();
                            } else wrongInput = true;
                            if (scanner.hasNextInt()) {
                                msgGroup = scanner.nextInt();
                            } else wrongInput = true;
                            msgNear = false;
                            msgOneday = true;
                            msgWeekday = 1;
                            break;
                        case "wednesday":
                            if (scanner.hasNextInt()) {
                                msgWeek = scanner.nextInt();
                            } else wrongInput = true;
                            if (scanner.hasNextInt()) {
                                msgGroup = scanner.nextInt();
                            } else wrongInput = true;
                            msgNear = false;
                            msgOneday = true;
                            msgWeekday = 2;
                            break;
                        case "thursday":
                            if (scanner.hasNextInt()) {
                                msgWeek = scanner.nextInt();
                            } else wrongInput = true;
                            if (scanner.hasNextInt()) {
                                msgGroup = scanner.nextInt();
                            } else wrongInput = true;
                            msgNear = false;
                            msgOneday = true;
                            msgWeekday = 3;
                            break;
                        case "friday":
                            if (scanner.hasNextInt()) {
                                msgWeek = scanner.nextInt();
                            } else wrongInput = true;
                            if (scanner.hasNextInt()) {
                                msgGroup = scanner.nextInt();
                            } else wrongInput = true;
                            msgNear = false;
                            msgOneday = true;
                            msgWeekday = 4;
                            break;
                        case "saturday":
                            if (scanner.hasNextInt()) {
                                msgWeek = scanner.nextInt();
                            } else wrongInput = true;
                            if (scanner.hasNextInt()) {
                                msgGroup = scanner.nextInt();
                            } else wrongInput = true;
                            msgNear = false;
                            msgOneday = true;
                            msgWeekday = 5;
                            break;
                        case "sunday":
                            if (scanner.hasNextInt()) {
                                msgWeek = scanner.nextInt();
                            } else wrongInput = true;
                            if (scanner.hasNextInt()) {
                                msgGroup = scanner.nextInt();
                            } else wrongInput = true;
                            msgNear = false;
                            msgOneday = true;
                            msgWeekday = 6;
                            break;
                        case "tomorrow":
                            if (scanner.hasNextInt()) {
                                msgGroup = scanner.nextInt();
                            } else wrongInput = true;
                            try {
                                msgWeek = getWeekNumber();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            msgNear = false;
                            msgOneday = true;
                            msgWeekday = (msgWeekday + 1) % 7;
                            if (msgWeekday == 0) {
                                msgWeek = (msgWeek==1) ? 2 : 1;
                            }
                            break;
                        case "all":
                            if (scanner.hasNextInt()) {
                                msgWeek = scanner.nextInt();
                            } else wrongInput = true;
                            if (scanner.hasNextInt()) {
                                msgGroup = scanner.nextInt();
                            } else wrongInput = true;
                            msgNear = false;
                            msgOneday = false;
                            msgWeekday = 0;
                            break;
                        case "week":
                            try {
                                if (getWeekNumber() == 1) sendMessage(chatId, "Неделя нечётная");
                                else sendMessage(chatId, "Неделя чётная");
                                return;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        default:
                            wrongInput = true;
                            break;
                    }
                    if (!(msgWeek == 1 || msgWeek == 2)) {
                        wrongInput = true;
                    }
                    if (!wrongInput) {
                        try {
                            timetable = TimetableService.GetTimetable(msgWeek, msgGroup, msgDaySec, msgNear, msgOneday, msgWeekday);

                        } catch (IOException e) {
                            sendMessage(chatId, "Неверные данные");
                        } catch (JsonSyntaxException e) {
                            throw new RuntimeException("Unable to parse json");
                        } catch (ParseException e) {
                            throw new RuntimeException("Unable to parse data");
                        }
                        sendMessage(chatId, timetable);
                    } else
                        sendMessage(chatId, "Введена неизвестная команда");
                    break;
                }
            }
        }
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Сообщение не отправилось");
        }
    }
}