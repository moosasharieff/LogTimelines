# LogTimelines

LogTimelines is a small Java CLI that converts **Timelines** (iPhone / Apple Watch) CSV exports into `.ics` calendar files that can be imported into any calendar application that supports the iCalendar (`.ics`) format (for example, Apple Calendar / iCloud Calendar, Google Calendar, Outlook, etc.). [file:38]

## Prerequisites

- Java 21+
- Maven 3+

## Usage

1. Export a **detailed** CSV from Timelines and place it under `CSVLogDirectory/`, e.g.: export_week1_2026_detailed.csv

   ```text
   timeline,start date,end date,duration (minutes),name,notes
   "Test",2025-12-29 09:14:44,2025-12-29 13:43:48,269.0677,"Full Stack Prep",""
   ```

2. Build and run:

   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="org.logtimelines.Main"
   ```

   By default `Main` converts:

   ```java
   String inputCsv = "CSVLogDirectory/export_week1_2026_detailed.csv";
   String outputIcs = "ICSCalenderEvents/export_week1_2026_detailed.ics";
   ```

3. Import the generated `.ics` file into your calendar application (for example, Apple Calendar / iCloud Calendar, Google Calendar, Outlook).

## Project layout

- `src/main/java/org/logtimelines/Main.java` – CLI entry point (configures input/output paths).
- `src/main/java/org/logtimelines/TimelinesCsvToIcs.java` – CSV → ICS conversion logic.
- `CSVLogDirectory/` – sample Timelines CSV input.
- `ICSCalenderEvents/` – generated ICS files (output).