package com.ivan.bot.util;

import com.ivan.bot.entity.Event;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
public class IcsFileGenerator {

    private static final DateTimeFormatter ICS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    /**
     * Generates an ICS file for a calendar event
     * @param event The event to convert to ICS format
     * @return File object pointing to the generated ICS file
     */
    public static File generateIcsFile(Event event) throws IOException {
        String icsContent = buildIcsContent(event);

        String fileName = "event_" + event.getId() + "_" + System.currentTimeMillis() + ".ics";
        File icsFile = new File(TEMP_DIR, fileName);

        try (FileWriter writer = new FileWriter(icsFile)) {
            writer.write(icsContent);
        }

        log.info("Generated ICS file: {}", icsFile.getAbsolutePath());
        return icsFile;
    }

    /**
     * Generates an ICS file for a calendar event with custom parameters
     * @param title Event title
     * @param startDateTime Event start time
     * @param endDateTime Event end time
     * @param chatId User's chat ID (used for unique ID generation)
     * @return File object pointing to the generated ICS file
     */
    public static File generateIcsFile(String title, LocalDateTime startDateTime, LocalDateTime endDateTime, Long chatId) throws IOException {
        String icsContent = buildIcsContent(title, startDateTime, endDateTime, chatId);

        String fileName = "event_" + chatId + "_" + System.currentTimeMillis() + ".ics";
        File icsFile = new File(TEMP_DIR, fileName);

        try (FileWriter writer = new FileWriter(icsFile)) {
            writer.write(icsContent);
        }

        log.info("Generated ICS file: {}", icsFile.getAbsolutePath());
        return icsFile;
    }

    private static String buildIcsContent(Event event) {
        return buildIcsContent(event.getTitle(), event.getStartDateTime(), event.getEndDateTime(), event.getChatId());
    }

    private static String buildIcsContent(String title, LocalDateTime startDateTime, LocalDateTime endDateTime, Long chatId) {
        String uid = UUID.randomUUID().toString() + "@virtualassistant.bot";
        String now = LocalDateTime.now().format(ICS_DATE_FORMAT);
        String start = startDateTime.format(ICS_DATE_FORMAT);
        String end = endDateTime.format(ICS_DATE_FORMAT);

        StringBuilder ics = new StringBuilder();
        ics.append("BEGIN:VCALENDAR\r\n");
        ics.append("VERSION:2.0\r\n");
        ics.append("PRODID:-//Virtual Assistant Bot//EN\r\n");
        ics.append("CALSCALE:GREGORIAN\r\n");
        ics.append("METHOD:PUBLISH\r\n");
        ics.append("BEGIN:VEVENT\r\n");
        ics.append("UID:").append(uid).append("\r\n");
        ics.append("DTSTAMP:").append(now).append("\r\n");
        ics.append("DTSTART:").append(start).append("\r\n");
        ics.append("DTEND:").append(end).append("\r\n");
        ics.append("SUMMARY:").append(escapeIcsText(title)).append("\r\n");
        ics.append("DESCRIPTION:Event created by Virtual Assistant Bot\r\n");
        ics.append("STATUS:CONFIRMED\r\n");
        ics.append("BEGIN:VALARM\r\n");
        ics.append("TRIGGER:-PT15M\r\n");
        ics.append("ACTION:DISPLAY\r\n");
        ics.append("DESCRIPTION:Reminder\r\n");
        ics.append("END:VALARM\r\n");
        ics.append("END:VEVENT\r\n");
        ics.append("END:VCALENDAR\r\n");

        return ics.toString();
    }

    /**
     * Escapes special characters for ICS format
     */
    private static String escapeIcsText(String text) {
        if (text == null) return "";
        return text
                .replace("\\", "\\\\")
                .replace(",", "\\,")
                .replace(";", "\\;")
                .replace("\n", "\\n");
    }
}


