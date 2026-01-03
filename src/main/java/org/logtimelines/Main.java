package org.logtimelines;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {

        String inputCsv = "CSVLogDirectory/export_week1_2026_detailed.csv";
        String outputIcs = "ICSCalenderEvents/export_week1_2026_detailed.ics";

        TimelinesCsvToIcs.convert(inputCsv, outputIcs);
    }
}