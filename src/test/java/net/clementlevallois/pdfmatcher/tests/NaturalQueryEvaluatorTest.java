package net.clementlevallois.pdfmatcher.tests;


import net.clementlevallois.pdfmatcher.controller.NaturalQueryEvaluator;
import org.junit.Assert;
import org.junit.Test;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author LEVALLOIS
 */
public class NaturalQueryEvaluatorTest {
    
    
    @Test
    public void testPdfmatcher() {
        String query = """
                       "java" OR (python AND programming) AND NOT("monty python")
                       """;
        Boolean caseSensitive = false; 
        NaturalQueryEvaluator ev = new NaturalQueryEvaluator();
        ev.setQueryInNaturalLanguage(query, caseSensitive);
        Boolean matched1 = ev.evaluate("test with Python programming");
        Boolean matched2 = ev.evaluate("test with monthy python");

        Assert.assertTrue(matched1);
        Assert.assertFalse(matched2);

        query = """
                       "java OR (python AND programming) AND NOT("monty python")
                       """;
        caseSensitive = false; 
        ev = new NaturalQueryEvaluator();
        ev.setQueryInNaturalLanguage(query, caseSensitive);
        matched1 = ev.evaluate("test with Python programming");
        Assert.assertNull(matched1);

        
    }

}
