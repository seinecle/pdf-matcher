/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.clementlevallois.pdfmatcher.tests;

import java.util.List;
import java.util.TreeMap;
import net.clementlevallois.functions.model.Occurrence;
import net.clementlevallois.pdfmatcher.controller.PdfMatcher;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author LEVALLOIS
 */
public class FunctionTest {

    @Test
    public void testEntirePdfMatchingWithWordContext() throws InterruptedException {

        TreeMap<Integer, Integer> pagesAndStartingLines = new TreeMap();
        pagesAndStartingLines.put(1, 1);

        TreeMap<Integer, String> lines = new TreeMap();
        lines.put(1, "such a beautiful island Java is");
        lines.put(2, "Python is such a beautiful island");
        lines.put(3, "Scala is such a beautiful island");
        lines.put(4, "R is such a beautifulzz island");
        lines.put(5, "Perl is such a beautiful island");
        lines.put(6, "Rust is such a beautiful island");

        String searchedTerm = "Java OR python OR Perl AND NOT \"Java is\"";

        Integer nbWords = 5;
        Integer nbLines = 2;
        Boolean caseSensitive = true;
        String startOfPage = "début de la page";
        String endOfPage = "fin de la page";

        PdfMatcher matcher = new PdfMatcher();
        List<Occurrence> occurrences = matcher.analyze(pagesAndStartingLines, searchedTerm, lines, nbWords, nbLines, caseSensitive, startOfPage, endOfPage);
        StringBuilder sb = new StringBuilder();
        System.out.println("TEST 1");
        for (Occurrence occ : occurrences) {
            System.out.println(occ.getContext());
            sb.append(occ.getContext());
            sb.append("\n");
            System.out.println("-------------");
            sb.append("-------------");
            sb.append("\n");
        }

        String expectedOutcome = """
                                début de la page
                                such a beautiful island <strong>Java</strong> is
                                Python is such a beautiful island
                                Scala is such a beautiful island
                                -------------
                                Scala is such a beautiful island
                                R is such a beautifulzz island
                                <strong>Perl</strong> is such a beautiful island
                                Rust is such a beautiful island
                                fin de la page
                                -------------                                
                                """;

        Assert.assertTrue(sb.toString().equals(expectedOutcome));

        searchedTerm = "(Java OR python OR Perl) AND NOT \"Java is\"";
        occurrences = matcher.analyze(pagesAndStartingLines, searchedTerm, lines, nbWords, nbLines, caseSensitive, startOfPage, endOfPage);
        sb = new StringBuilder();
        System.out.println("TEST 2");
        for (Occurrence occ : occurrences) {
            System.out.println(occ.getContext());
            sb.append(occ.getContext());
            sb.append("\n");
            System.out.println("-------------");
            sb.append("-------------");
            sb.append("\n");
        }

        expectedOutcome = """
                                Scala is such a beautiful island
                                R is such a beautifulzz island
                                <strong>Perl</strong> is such a beautiful island
                                Rust is such a beautiful island
                                fin de la page
                                -------------                                
                                """;
        Assert.assertTrue(sb.toString().equals(expectedOutcome));

        searchedTerm = """
                       "such a beautiful"
                       """;
        nbLines = 0;
        occurrences = matcher.analyze(pagesAndStartingLines, searchedTerm, lines, nbWords, nbLines, caseSensitive, startOfPage, endOfPage);
        sb = new StringBuilder();
        System.out.println("TEST 3");
        for (Occurrence occ : occurrences) {
            System.out.println(occ.getContext());
            sb.append(occ.getContext());
            sb.append("\n");
            System.out.println("-------------");
            sb.append("-------------");
            sb.append("\n");
        }

        expectedOutcome
                = """
  <strong>such a beautiful</strong> island Java is
  -------------
  Python is <strong>such a beautiful</strong> island
  -------------
  Scala is <strong>such a beautiful</strong> island
  -------------
  Perl is <strong>such a beautiful</strong> island
  -------------
  Rust is <strong>such a beautiful</strong> island
  -------------
  """;
        Assert.assertTrue(sb.toString().equals(expectedOutcome));
    }
}
