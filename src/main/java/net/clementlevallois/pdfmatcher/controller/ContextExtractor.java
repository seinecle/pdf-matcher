/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.clementlevallois.pdfmatcher.controller;

/**
 *
 * @author LEVALLOIS
 */
public class ContextExtractor {

    public static void main(String args[]) {
        String line = "parties, who are authorized to participate in the inspection described in this Statement of Work (“Authorized Personnel”) in ";
        String word = "Authorized Personnel";
        String extract = ContextExtractor.extract(word, line, 5);
        System.out.println("extract: " + extract);
    }

    public static String extract(String word, String line, int lengthContext) {
        System.out.println("word: "+ word);
        System.out.println("line: "+ line);
        
        String wordWithOriginalCase = word;
        word = word.toLowerCase();
        
        String leftPart = line.substring(0, line.toLowerCase().indexOf(word));
        String rightPart = line.substring(line.toLowerCase().indexOf(word) + word.length(), line.length());

        String appendixLeft = "";
        if (!leftPart.endsWith(" ") & !leftPart.isBlank() & leftPart.contains(" ")) {
            int index = leftPart.lastIndexOf(" ");
            appendixLeft = leftPart.substring(index+ 1, leftPart.length());
            leftPart = leftPart.substring(0, index);
        }

        String appendixRight = "";
        if (!rightPart.startsWith(" ")&& !rightPart.isBlank() & rightPart.contains(" ")) {
            int index = rightPart.indexOf(" ");
            appendixRight = rightPart.substring(0, index);
            rightPart = rightPart.substring(index, rightPart.length());
        }
        String rightPartTrimmed = rightPart.trim();
        String leftPartTrimmed = leftPart.trim();

        String[] termsOnTheLeft = leftPartTrimmed.split("\\s");
        String[] termsOnTheRight = rightPartTrimmed.split("\\s");
        if (!leftPart.contains(" ")){
            termsOnTheLeft = new String[0];
        }
        if (!rightPart.contains(" ")){
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
}
