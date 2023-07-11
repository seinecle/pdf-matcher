/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.clementlevallois.pdfmatcher.controller;

import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author LEVALLOIS
 */
public class ContextExtractor {

    public static String extractSurroundingWords(Set<String> termsFound, String searchTerm, String line, int lengthContext, boolean caseSensitive) {

        if (searchTerm.contains(" OR ") || searchTerm.contains(" AND ") || searchTerm.contains(" NOT(") || searchTerm.contains("\"")) {
            if (termsFound != null && !termsFound.isEmpty()) {
                String tempResult;
                String lineWithCaseSensitivityDealtWith = caseSensitive ? line : line.toLowerCase();
                for (String oneSearchTerm : termsFound) {
                    int startIndexOfSearchTerm = lineWithCaseSensitivityDealtWith.indexOf(oneSearchTerm);
                    int endIndexOfSearchTerm = startIndexOfSearchTerm + oneSearchTerm.length();
                    tempResult = line.substring(0, startIndexOfSearchTerm);
                    tempResult = tempResult + "<strong>" + line.substring(startIndexOfSearchTerm, endIndexOfSearchTerm);
                    tempResult = tempResult + "</strong>";
                    tempResult = tempResult + line.substring(endIndexOfSearchTerm, line.length());
                    line = tempResult;
                }
                return line;
            }
        }

        String wordWithOriginalCase = searchTerm;
        searchTerm = searchTerm.toLowerCase();

        String leftPart = line.substring(0, line.toLowerCase().indexOf(searchTerm));
        String rightPart = line.substring(line.toLowerCase().indexOf(searchTerm) + searchTerm.length(), line.length());

        String appendixLeft = "";
        if (!leftPart.endsWith(" ") & !leftPart.isBlank() & leftPart.contains(" ")) {
            int index = leftPart.lastIndexOf(" ");
            appendixLeft = leftPart.substring(index + 1, leftPart.length());
            leftPart = leftPart.substring(0, index);
        }

        String appendixRight = "";
        if (!rightPart.startsWith(" ") && !rightPart.isBlank() & rightPart.contains(" ")) {
            int index = rightPart.indexOf(" ");
            appendixRight = rightPart.substring(0, index);
            rightPart = rightPart.substring(index, rightPart.length());
        }
        String rightPartTrimmed = rightPart.trim();
        String leftPartTrimmed = leftPart.trim();

        String[] termsOnTheLeft = leftPartTrimmed.split("\\s");
        String[] termsOnTheRight = rightPartTrimmed.split("\\s");
        if (!leftPart.contains(" ")) {
            termsOnTheLeft = new String[0];
        }
        if (!rightPart.contains(" ")) {
            termsOnTheRight = new String[0];
        }
        int beforeWords = Math.min(termsOnTheLeft.length, lengthContext);
        int afterWords = Math.min(termsOnTheRight.length, lengthContext);

        StringBuilder contextToReturn = new StringBuilder();

        for (int i = beforeWords; i > 0; i--) {
            contextToReturn.append(" ");
            contextToReturn.append(termsOnTheLeft[termsOnTheLeft.length - i]);
        }
        contextToReturn.append(" ");
        contextToReturn.append(appendixLeft);
        contextToReturn.append(wordWithOriginalCase);
        contextToReturn.append(appendixRight);
        for (int i = 0; i < afterWords; i++) {
            contextToReturn.append(" ");
            contextToReturn.append(termsOnTheRight[i]);
        }
        return contextToReturn.toString().trim();
    }

    public static String extractSurroundingLines(Set<String> termsFound, String searchTerm, String line, LinkedBlockingQueue<String> preLines, List<String> postLines, boolean caseSensitive, String startOfPage, String endOfPage) {

        if (searchTerm.contains(" OR ") || searchTerm.contains(" AND ") || searchTerm.contains(" NOT(")) {
            if (termsFound != null && !termsFound.isEmpty()) {
                String tempResult;
                String lineWithCaseSensitivityDealtWith = caseSensitive ? line : line.toLowerCase();
                for (String oneSearchTerm : termsFound) {
                    int startIndexOfSearchTerm = lineWithCaseSensitivityDealtWith.indexOf(oneSearchTerm);
                    int endIndexOfSearchTerm = startIndexOfSearchTerm + oneSearchTerm.length();
                    if (startIndexOfSearchTerm == -1){
                        System.out.println("error in pdf match with surrounding lines: ");
                        System.out.println("searchTerm: "+ searchTerm);
                        System.out.println("line: "+ line);
                        System.out.println("one search term: "+ oneSearchTerm);
                    }
                    tempResult = line.substring(0, startIndexOfSearchTerm);
                    tempResult = tempResult + "<strong>" + line.substring(startIndexOfSearchTerm, endIndexOfSearchTerm);
                    tempResult = tempResult + "</strong>";
                    tempResult = tempResult + line.substring(endIndexOfSearchTerm, line.length());
                    line = tempResult;
                }
            }
        } else {
            searchTerm = caseSensitive ? searchTerm : searchTerm.toLowerCase();
            String tempResult;
            String lineWithCaseSensitivityDealtWith = caseSensitive ? line : line.toLowerCase();
            int startIndexOfSearchTerm = lineWithCaseSensitivityDealtWith.indexOf(searchTerm);
            int endIndexOfSearchTerm = startIndexOfSearchTerm + searchTerm.length();
            tempResult = line.substring(0, startIndexOfSearchTerm);
            tempResult = tempResult + "<strong>" + line.substring(startIndexOfSearchTerm, endIndexOfSearchTerm);
            tempResult = tempResult + "</strong>";
            tempResult = tempResult + line.substring(endIndexOfSearchTerm, line.length());
            line = tempResult;
        }

        StringBuilder contextToReturn = new StringBuilder();

        if (preLines.isEmpty()) {
            contextToReturn.append(startOfPage);
            contextToReturn.append("\n");
        } else {
            for (String preLine : preLines) {
                contextToReturn.append(preLine);
                contextToReturn.append("\n");
            }
        }

        contextToReturn.append(line);
        contextToReturn.append("\n");
        if (postLines.isEmpty()) {
            contextToReturn.append(endOfPage);
            contextToReturn.append("\n");
        } else {
            for (String postLine : postLines) {
                contextToReturn.append(postLine);
                contextToReturn.append("\n");
            }
        }
        return contextToReturn.toString().trim();
    }
}
