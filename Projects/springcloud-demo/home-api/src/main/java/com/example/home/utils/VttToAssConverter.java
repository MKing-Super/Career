package com.example.home.utils;

import cn.hutool.core.date.DateUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VttToAssConverter {
    static String name = "Shameless";//名称
    static String season = "S11";//季
    static int n = 20;//集数

    static String VTT = ".vtt";
    static String ASS = ".ass";
    static String VTT_URL = "E:/Videos/"+name+" "+season+"/vtt/";
    static String ASS_URL = "E:/Videos/"+name+" "+season+"/ass/";
    static String MIDDLE = null;
    static List<String> MIDDLE_ARR = new ArrayList<>();

    public static void main(String[] args) {
        for (int i = 1 ; i <= n ; i++){
            if (i < 10){
                MIDDLE_ARR.add(name + " " + season + "E0" + i + "(both)");
                MIDDLE_ARR.add(name + " " + season + "E0" + i + "(Chinese)");
                MIDDLE_ARR.add(name + " " + season + "E0" + i + "(English)");
            }else {
                MIDDLE_ARR.add(name + " " + season + "E" + i + "(both)");
                MIDDLE_ARR.add(name + " " + season + "E" + i + "(Chinese)");
                MIDDLE_ARR.add(name + " " + season + "E" + i + "(English)");
            }
        }

        for (String str : MIDDLE_ARR){
            MIDDLE = str;
            String inputFilePath = VTT_URL + MIDDLE + VTT;  // 输入VTT文件路径
            String outputFilePath = ASS_URL + MIDDLE + ASS;; // 输出ASS文件路径
            try {
                convertVttToAss(inputFilePath, outputFilePath);
                System.out.println(str + "转换完成！");
            } catch (IOException e) {
                System.err.println("转换过程中出错: " + e.getMessage());
            }
        }

    }

    public static void convertVttToAss(String inputPath, String outputPath) throws IOException {
        List<String> vttLines = readFile(inputPath);
        AssScriptInfo scriptInfo = new AssScriptInfo();
        List<AssEvent> events = parseVttToAss(vttLines);
        writeAssFile(scriptInfo, events, outputPath);
    }

    private static List<String> readFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        }
        return lines;
    }

    private static List<AssEvent> parseVttToAss(List<String> vttLines) {
        List<AssEvent> events = new ArrayList<>();
        Pattern timePattern = Pattern.compile("(\\d{2}:\\d{2}:\\d{2})\\.(\\d{3}) --> (\\d{2}:\\d{2}:\\d{2})\\.(\\d{3})");

        int i = 0;
        while (i < vttLines.size()) {
            String line = vttLines.get(i);

            // 跳过VTT文件头和注释
            if (line.startsWith("WEBVTT") || line.startsWith("NOTE") || line.isEmpty()) {
                i++;
                continue;
            }

            // 匹配时间行
            Matcher matcher = timePattern.matcher(line);
            if (matcher.matches()) {
                String startTime = matcher.group(1) + "." + matcher.group(2);
                String endTime = matcher.group(3) + "." + matcher.group(4);

                // 获取下一行作为字幕文本
                i++;
                StringBuilder text = new StringBuilder();
                while (i < vttLines.size() && !vttLines.get(i).matches("\\d{2}:\\d{2}:\\d{2}\\.\\d{3} --> \\d{2}:\\d{2}:\\d{2}\\.\\d{3}")) {
                    if (!vttLines.get(i).isEmpty()) {
                        if (text.length() > 0) {
                            text.append("\\N"); // ASS中的换行符
                        }
                        text.append(vttLines.get(i));
                    }
                    i++;
                }

                events.add(new AssEvent(startTime, endTime, text.toString()));
            } else {
                i++;
            }
        }

        return events;
    }

    private static void writeAssFile(AssScriptInfo scriptInfo, List<AssEvent> events, String outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            String nowFormat = DateUtil.format(new Date(), "yyyy-MM-dd");
            // 写入ASS文件头
            writer.write("[Script Info]\n");
            writer.write(";SrtEdit "+nowFormat+"\n");
            writer.write(";Copyright(C) \n\n");

            writer.write("Title: " + MIDDLE + "\n");
            writer.write("Original Script:\n");
            writer.write("Original Translation: \n");
            writer.write("Original Timing: \n");
            writer.write("Original Editing:\n");
            writer.write("Script Updated By: \n");
            writer.write("Update Details: \n");
            writer.write("ScriptType: v4.00+\n");
            writer.write("Collisions: Reverse\n");
            writer.write("PlayResX: " + 0 + "\n");
            writer.write("PlayResY: " + 0 + "\n");
            writer.write("Timer: 100.0000\n");
            writer.write("Synch Point: 1\n");
            writer.write("WrapStyle: 0\n");
            writer.write("ScaledBorderAndShadow: no\n\n");

            // 写入V4样式（简单样式）
            writer.write("[V4+ Styles]\n");
            writer.write("Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\n");
            writer.write("Style: Default,文泉驿微米黑,16,&H00FFFFFF,&HF0000000,&H00000000,&HF0000000,-1,0,0,0,100,100,0,0,1,1,0,2,5,5,2,134\n\n");

            // 写入事件部分
            writer.write("[Events]\n");
            writer.write("Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n");

            for (AssEvent event : events) {
                String text = event.getText();
                String[] split = text.split("\\\\N");
                String t = "\\N{\\fncronos Pro Subhead\\fs10\\1c&H0080FF&}";
                String t2 = "{\\r}";
                if (split.length > 1){
                    text = split[0] + t + split[1] + t2;
                }else {
                    text = split[0] + t + split[0] + t2;
                }

                writer.write(String.format("Dialogue: 0,%s,%s,Default,NTP,0,0,0,,%s\n",
                        convertVttTimeToAss(event.getStartTime()),
                        convertVttTimeToAss(event.getEndTime()),
                        text));
            }
        }
    }

    // 将VTT时间格式(HH:MM:SS.mmm)转换为ASS时间格式(H:MM:SS.cc)
    private static String convertVttTimeToAss(String vttTime) {
        String[] parts = vttTime.split(":|\\.");
        if (parts.length == 4) {
            String hours = parts[0];
            String minutes = parts[1];
            String seconds = parts[2];
            String millis = parts[3];

            // ASS时间格式：H:MM:SS.cc (百分之一秒)
            return String.format("%s:%s:%s.%s",
                    hours.substring(0,1),
                    minutes,
                    seconds,
                    millis.substring(0, 2)); // 取前两位毫秒作为百分之一秒
        }
        return "0:00:00.00"; // 默认值
    }

    // ASS脚本信息类
    static class AssScriptInfo {
        private String title = "Converted Subtitle";
        private int playResX = 384;
        private int playResY = 288;

        // getters
        public String getTitle() { return title; }
        public int getPlayResX() { return playResX; }
        public int getPlayResY() { return playResY; }
    }

    // ASS事件类
    static class AssEvent {
        private final String startTime;
        private final String endTime;
        private final String text;

        public AssEvent(String startTime, String endTime, String text) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.text = text;
        }

        // getters
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public String getText() { return text; }
    }

}
