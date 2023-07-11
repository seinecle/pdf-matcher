/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.clementlevallois.pdfmatcher.controller;

import java.util.HashMap;
import java.util.HashSet;
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
    private String[] fields;
    private Boolean caseSensitive = false;
    private Set<String> setKeywordsFound;

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
        this.caseSensitive = caseSensitive;
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
        boolean termBetweenQuotationMarksStarted = false;
        StringBuilder termBetweenQuotationMarks = new StringBuilder();
        setKeywordsFound = new HashSet();
        Map<String, Boolean> mapLetters = new HashMap();
        boolean termEnclosedInQuotationMarks = false;
        for (String field : fields) {
            if (field.equals("OR")) {
                sb.append("||");
            } else if (field.equals("AND")) {
                sb.append("&&");
            } else if (field.equals(" ") && termBetweenQuotationMarksStarted) {
                termBetweenQuotationMarks.append(field);
            } else if (field.equals("NOT")) {
                sb.append("!");
            } else if (DELIMITERS.contains(field)) {
                sb.append(field);
            } else {
                if (field.startsWith("\"") && field.endsWith("\"")) {
                    field = field.replaceAll("\"", "");
                    termBetweenQuotationMarks.append(field);
                    field = termBetweenQuotationMarks.toString();
                    termBetweenQuotationMarksStarted = false;
                    termEnclosedInQuotationMarks = true;
                }
                if (field.startsWith("\"")) {
                    termBetweenQuotationMarks.append(field);
                    termBetweenQuotationMarksStarted = true;
                    continue;
                }
                if (field.endsWith("\"")) {
                    termBetweenQuotationMarks.append(field);
                    field = termBetweenQuotationMarks.toString();
                    termBetweenQuotationMarksStarted = false;
                    termEnclosedInQuotationMarks = true;
                }
                if (termBetweenQuotationMarksStarted) {
                    termBetweenQuotationMarks.append(field);
                    continue;
                }
                if (!termBetweenQuotationMarksStarted) {
                    String letter = ALPHABET.substring(count, (count + 1));
                    if (!caseSensitive) {
                        field = field.toLowerCase();
                    }
                    if (termEnclosedInQuotationMarks) {
                        String strippedField = field.replaceAll("\"", "");
                        int indexOfField = line.indexOf(strippedField);
                        if (indexOfField < 0) {
                            mapLetters.put(letter, Boolean.FALSE);
                        } else {
                            int indexCharBefore = indexOfField - 1;
                            int indexCharAfter = indexOfField + strippedField.length();
                            boolean conditionBeforeOK = false;
                            boolean conditionAfterOK = false;
                            if (indexCharBefore < 0 || (indexCharBefore >= 0 && !Character.isLetter(line.charAt(indexCharBefore)))) {
                                conditionBeforeOK = true;
                            }
                            Character charAfter = null;
                            if (indexCharAfter < line.length()) {
                                charAfter = line.charAt(indexCharAfter);
                            }

                            if (indexCharAfter >= line.length() || (indexCharAfter < line.length() && !Character.isLetter(charAfter))) {
                                conditionAfterOK = true;
                            }
                            if (conditionAfterOK && conditionBeforeOK) {
                                mapLetters.put(letter, Boolean.TRUE);
                                if (!sb.toString().endsWith("!(") & !sb.toString().endsWith("!") & !sb.toString().endsWith("! ")) {
                                    setKeywordsFound.add(strippedField);
                                }
                            } else {
                                mapLetters.put(letter, Boolean.FALSE);
                            }
                        }
                        termEnclosedInQuotationMarks = false;
                    } else if (line.contains(field)) {
                        mapLetters.put(letter, Boolean.TRUE);
                        if (!sb.toString().endsWith("!(") & !sb.toString().endsWith("!") & !sb.toString().endsWith("! ")) {
                            setKeywordsFound.add(field);
                        }
                    } else {
                        mapLetters.put(letter, Boolean.FALSE);
                    }
                    sb.append(letter);
                    count++;
                    termBetweenQuotationMarks = new StringBuilder();
                }
            }
        }
        sb.append(")");
        String rule = sb.toString();
        rule = rule.replace("! ", "!");
        Boolean matched = null;
        try {
            matched = ((Boolean) MVEL.eval(sb.toString(), mapLetters));
        } catch (Exception e) {
            System.out.println("error with rule: " + rule);
        }

        return matched;
    }

    public Set<String> getKeywordsFound() {
        return setKeywordsFound;
    }

}
