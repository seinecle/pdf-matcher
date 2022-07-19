/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package net.clementlevallois.pdfmatcher.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import net.clementlevallois.utils.TextCleaningOps;

/**
 *
 * @author LEVALLOIS
 */
public class PdfMatcher {

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    public List<Occurrence> analyze(TreeMap<Integer, Integer> pagesAndStartingLine, String searchedTerm, Map<Integer, String> lines, int lengthContext) {

        List<Occurrence> occurrences = new ArrayList();

        searchedTerm = TextCleaningOps.doAllCleaningOps(searchedTerm);
        searchedTerm = searchedTerm.toLowerCase();

        Set<Map.Entry<Integer, String>> entrySet = lines.entrySet();

        for (Map.Entry<Integer, String> entry : entrySet) {
            int lineNumber = entry.getKey();
            String line = entry.getValue();

            if (!line.toLowerCase().contains(searchedTerm)) {
                continue;
            }

            Occurrence occ = new Occurrence();
            String context = ContextExtractor.extract(searchedTerm, line, lengthContext);
            occ.setContext(context);

            int pageOccurrence = 1;

            NavigableSet<Integer> navigableKeySet = pagesAndStartingLine.navigableKeySet();

            Iterator<Integer> it = navigableKeySet.iterator();
            while (it.hasNext()) {
                int page = it.next();
                int pageStartingLine = pagesAndStartingLine.get(page);
                if (pageStartingLine < lineNumber & it.hasNext()) {
                    continue;
                }
                if (pageStartingLine > lineNumber) {
                    pageOccurrence = page - 1;
                    break;
                    
                } else {
                    pageOccurrence = page;
                    break;
                }
            }
            occ.setPage(pageOccurrence);
            occurrences.add(occ);
        }
        return occurrences;

    }
}
