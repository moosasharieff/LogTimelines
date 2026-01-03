package org.logtimelines;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimelinesCsvToIcs {

    private static final String CALENDAR_NAME = "Timelines Log";

    // Matches sample: 2025-12-29 09:14:44 [file:38]
    private static final DateTimeFormatter CSV_DATETIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static String formatIcsDateTime(LocalDateTime dt) {
        // Floating time (no timezone) – Calendar will treat it as local time.
        return dt.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
    }

    public static void convert(String INPUT_CSV, String OUTPUT_ICS) throws IOException {
        List<String[]> rows = readCsv(INPUT_CSV);
        List<String> icsLines = new ArrayList<>();

        icsLines.add("BEGIN:VCALENDAR");
        icsLines.add("VERSION:2.0");
        icsLines.add("PRODID:-//Timelines Export Java//EN");
        icsLines.add("X-WR-CALNAME:" + CALENDAR_NAME);

        int eventCount = 0;

        // Skip header: first row contains column names. [file:38]
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length < 6) {
                continue; // skip malformed lines
            }

            String timeline = row[0];
            String startStr = row[1];
            String endStr = row[2];
            String durationMinutes = row[3];
            String name = row[4];
            String notes = row[5];

            LocalDateTime start = LocalDateTime.parse(startStr, CSV_DATETIME);
            LocalDateTime end = LocalDateTime.parse(endStr, CSV_DATETIME);

            String dtStart = formatIcsDateTime(start);
            String dtEnd = formatIcsDateTime(end);

            String uid = (i) + "-" + start.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + "@timelines-java";

            String summary = (name != null && !name.isBlank()) ? name : timeline;

            StringBuilder descBuilder = new StringBuilder();
            descBuilder.append("Timeline: ").append(timeline).append("\\n");
            descBuilder.append("Duration (minutes): ").append(durationMinutes);
            if (notes != null && !notes.isBlank()) {
                descBuilder.append("\\nNotes: ").append(notes.replace("\n", " "));
            }
            String description = descBuilder.toString();

            icsLines.add("BEGIN:VEVENT");
            icsLines.add("UID:" + uid);
            icsLines.add("DTSTAMP:" + dtStart);
            icsLines.add("DTSTART:" + dtStart);
            icsLines.add("DTEND:" + dtEnd);
            icsLines.add("SUMMARY:" + escapeIcsText(summary));
            icsLines.add("DESCRIPTION:" + escapeIcsText(description));
            icsLines.add("END:VEVENT");

            eventCount++;
        }

        icsLines.add("END:VCALENDAR");

        writeLines(OUTPUT_ICS, icsLines);
        System.out.println("Wrote " + eventCount + " events to " + OUTPUT_ICS);
    }

    private static List<String[]> readCsv(String filename) throws IOException {
        List<String[]> rows = new ArrayList<>();
        Path path = Path.of(filename);

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);

                // Strip surrounding quotes if present
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = stripQuotes(parts[i].trim());
                }
                rows.add(parts);
            }
        }
        return rows;
    }

    private static String stripQuotes(String s) {
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private static String escapeIcsText(String s) {
        // Escape characters as per ICS text rules.
        return s
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,");
    }

    private static void writeLines(String filename, List<String> lines) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(filename, StandardCharsets.UTF_8))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }
}
