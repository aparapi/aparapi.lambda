package com.amd.aparapi;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HSAILValidator {
    static class Instruction {
        int lineNumber;
        Label label;
        String content;
        String tailComment;
        boolean special = false;
        static Pattern tailCommentPattern = Pattern.compile("^ *(.*) *; *//(.*)");
        static Pattern noTailCommentPattern = Pattern.compile("^ *(.*) *; *");

        Instruction(int _lineNumber, String _content, Label _label) {
            lineNumber = _lineNumber;
            Matcher matcher = tailCommentPattern.matcher(_content);
            if (matcher.matches()) {
                content = matcher.group(1);
                tailComment = matcher.group(2);
            } else {
                matcher = noTailCommentPattern.matcher(_content);
                if (matcher.matches()) {
                    content = matcher.group(1);
                    tailComment = null;

                } else {
                    content = _content.trim();
                    special = true;
                }

            }
            label = _label;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (label != null) {
                sb.append(label.name).append(":\n");
            }
            sb.append("   " + content);
            return (sb.toString());
        }
    }

    static class Label {
        String name;

        Label(String _name) {
            name = _name;
        }
    }

    static String labelRegexCapture = "(\\@L[a-zA-Z0-9_]+)";

    static class LineMatcher {
        Pattern pattern;
        Matcher matcher;

        String getGroup(int group) {
            return (matcher.group(group));
        }

        boolean matches(String line) {
            Matcher lineMatcher = pattern.matcher(line);
            if (lineMatcher.matches()) {
                matcher = lineMatcher;

            } else {
                matcher = null;

            }
            return (matcher != null);
        }

        LineMatcher(Pattern _pattern) {
            pattern = _pattern;
        }
    }

    static LineMatcher labelMatcher = new LineMatcher(Pattern.compile("^ *" + labelRegexCapture + ": *"));
    static LineMatcher whiteSpaceMatcher = new LineMatcher(Pattern.compile("^ *//(.*)"));
    static LineMatcher multiLineStartMatcher = new LineMatcher(Pattern.compile("^ */\\*(.*)"));
    static LineMatcher multiLineEndMatcher = new LineMatcher(Pattern.compile("^ *\\*/(.*)"));

    public static void main(String[] _args) throws IOException {
        String fileName = "C:\\Users\\user1\\aparapi\\branches\\lambda\\sindexof.hsail";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

        List<String> input = new ArrayList<String>();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            input.add(line);
        }
        br.close();
        Label label = null;
        boolean multiLine = false;
        int lineNumber = 0;
        List<Instruction> instructions = new ArrayList<Instruction>();
        for (String line : input) {

            if (multiLine) {
                if (multiLineEndMatcher.matches(line)) {
                    multiLine = false;
                } else {
                    // skip
                }
            } else {
                if (whiteSpaceMatcher.matches(line)) {
                    // System.out.println(line+" = "+whiteSpaceMatcher.getGroup(1));
                } else if (multiLineStartMatcher.matches(line)) {
                    multiLine = true;
                } else if (labelMatcher.matches(line)) {
                    // System.out.println(line);
                    label = new Label(labelMatcher.getGroup(1));
                } else {
                    if (!line.trim().equals("")) {
                        instructions.add(new Instruction(lineNumber, line, label));
                        label = null;
                    }

                }

            }
            lineNumber++;
        }
        for (Instruction i : instructions) {
            System.out.println(i);
        }


    }

}
