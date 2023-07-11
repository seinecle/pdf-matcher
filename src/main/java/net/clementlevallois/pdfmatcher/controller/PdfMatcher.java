/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package net.clementlevallois.pdfmatcher.controller;

import net.clementlevallois.functions.model.Occurrence;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author LEVALLOIS
 */
public class PdfMatcher {

    public List<Occurrence> analyze(TreeMap<Integer, Integer> pagesAndStartingLine, String searchedTerm, Map<Integer, String> lines, Integer nbWords, Integer nbLines, boolean caseSensitive, String startOfPage, String endOfPage) throws InterruptedException {

        boolean contextWillBeLines = nbLines != null && nbLines > 0;

        List<Occurrence> occurrences = new ArrayList();

        NaturalQueryEvaluator ev = new NaturalQueryEvaluator();
        ev.setQueryInNaturalLanguage(searchedTerm, caseSensitive);

        LinkedBlockingQueue preLines = new LinkedBlockingQueue(1);
        if (contextWillBeLines) {
            preLines = new LinkedBlockingQueue(nbLines);
        }
        List<String> postLines = new ArrayList();

        Set<Map.Entry<Integer, String>> entrySet = lines.entrySet();

        for (Map.Entry<Integer, String> entry : entrySet) {
            int lineNumber = entry.getKey();
            String line = entry.getValue();
            if (contextWillBeLines) {
                if (preLines.remainingCapacity() == 0) {
                    preLines.poll();
                }
                if (lines.containsKey(lineNumber - 1)) {
                    preLines.add(lines.get(lineNumber - 1));
                }
                postLines = new ArrayList(nbLines);
                for (int i = 1; i <= nbLines; i++) {
                    if (lines.containsKey(lineNumber + i)) {
                        postLines.add(lines.get(lineNumber + i));
                    }
                }
            }
            Boolean matched = ev.evaluate(line);
            if (matched == null || !matched) {
                continue;
            }

            if (preLines.remainingCapacity() > 0) {
                preLines.put(startOfPage);
            }
            if (postLines.size() < nbLines) {
                postLines.add(endOfPage);
            }

            Occurrence occ = new Occurrence();
            String context = "";

            if (contextWillBeLines) {
                context = ContextExtractor.extractSurroundingLines(ev.getKeywordsFound(), searchedTerm, line, preLines, postLines, caseSensitive, startOfPage, endOfPage);
            } else {
                context = ContextExtractor.extractSurroundingWords(ev.getKeywordsFound(), searchedTerm, line, nbWords, caseSensitive);
            }
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
