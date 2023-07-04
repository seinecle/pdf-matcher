/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.clementlevallois.pdfmatcher.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.mvel2.MVEL;

/**
 *
 * @author LEVALLOIS
 */
public class NaturalQueryEvaluator {

    private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    private static final Set<String> DELIMITERS = Set.of(" ", "(", ")");
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String[] fields;
    Boolean caseSensitive = false;

    public static void main(String args[]) {
    }

    public void setQueryInNaturalLanguage(String naturalQuery, Boolean caseSensitive) {
        naturalQuery = naturalQuery.replaceAll("\\R", "");
        long countDoubleQuotes = naturalQuery.codePoints().filter(ch -> ch == '"').count();
        long countOpeningBracket = naturalQuery.codePoints().filter(ch -> ch == '(').count();
        long countClosingBracket = naturalQuery.codePoints().filter(ch -> ch == ')').count();
        if ((countDoubleQuotes % 2) != 0) {
            return;
        }
        if (countOpeningBracket != countClosingBracket) {
            return;
        }
        fields = naturalQuery.split(String.format(WITH_DELIMITER, "[ ()]"));

    }

    public Boolean evaluate(String line) {
        if (fields == null) {
            return null;
        }
        int count = 0;
        line = line.replaceAll("\\R", "");
        if (!caseSensitive) {
            line = line.toLowerCase();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        boolean termStarted = false;
        StringBuilder term = new StringBuilder();
        Map<String, Boolean> map = new HashMap();
        for (String field : fields) {
            if (field.equals("OR")) {
                sb.append("||");
            } else if (field.equals("AND")) {
                sb.append("&&");
            } else if (field.equals(" ") && termStarted) {
                term.append(field);
            } else if (field.equals("NOT")) {
                sb.append("!");
            } else if (DELIMITERS.contains(field)) {
                sb.append(field);
            } else {
                if (field.startsWith("\"") && field.endsWith("\"")) {
                    field = field.replaceAll("\"", "");
                    term.append(field);
                    field = term.toString();
                    termStarted = false;
                }
                if (field.startsWith("\"")) {
                    field = field.replaceAll("\"", "");
                    term.append(field);
                    termStarted = true;
                }
                if (field.endsWith("\"")) {
                    field = field.replaceAll("\"", "");
                    term.append(field);
                    field = term.toString();
                    termStarted = false;
                }
                if (!termStarted) {
                    String letter = ALPHABET.substring(count, (count + 1));
                    if (!caseSensitive) {
                        field = field.toLowerCase();
                    }
                    if (line.contains(field)) {
                        map.put(letter, Boolean.TRUE);
                    } else {
                        map.put(letter, Boolean.FALSE);
                    }
                    sb.append(letter);
                    count++;
                    term = new StringBuilder();
                }
            }
        }
        sb.append(")");
        String rule = sb.toString();
        Boolean matched = null;
        try {
            matched = ((Boolean) MVEL.eval(sb.toString(), map));
        } catch (Exception e) {
            System.out.println("error with rule: " + rule);
        }

        return matched;
    }

}
