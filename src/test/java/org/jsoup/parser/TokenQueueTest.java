package org.jsoup.parser;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Token queue tests.
 */
public class TokenQueueTest {
    @Test public void chompBalanced() {
        TokenQueue tq = new TokenQueue(":contains(one (two) three) four");
        String pre = tq.consumeTo("(");
        String guts = tq.chompBalanced('(', ')');
        String remainder = tq.remainder();

        assertEquals(":contains", pre);
        assertEquals("one (two) three", guts);
        assertEquals(" four", remainder);
    }
    
    @Test public void chompEscapedBalanced() {
        TokenQueue tq = new TokenQueue(":contains(one (two) \\( \\) \\) three) four");
        String pre = tq.consumeTo("(");
        String guts = tq.chompBalanced('(', ')');
        String remainder = tq.remainder();

        assertEquals(":contains", pre);
        assertEquals("one (two) \\( \\) \\) three", guts);
        assertEquals("one (two) ( ) ) three", TokenQueue.unescape(guts));
        assertEquals(" four", remainder);
    }

    @Test public void chompBalancedMatchesAsMuchAsPossible() {
        TokenQueue tq = new TokenQueue("unbalanced(something(or another)) else");
        tq.consumeTo("(");
        String match = tq.chompBalanced('(', ')');
        assertEquals("something(or another)", match);
    }
    
    @Test public void unescape() {
        assertEquals("one ( ) \\", TokenQueue.unescape("one \\( \\) \\\\"));
    }
    
    @Test public void chompToIgnoreCase() {
        String t = "<textarea>one < two </TEXTarea>";
        TokenQueue tq = new TokenQueue(t);
        String data = tq.chompToIgnoreCase("</textarea");
        assertEquals("<textarea>one < two ", data);
        
        tq = new TokenQueue("<textarea> one two < three </oops>");
        data = tq.chompToIgnoreCase("</textarea");
        assertEquals("<textarea> one two < three </oops>", data);
    }

    @Test public void addFirst() {
        TokenQueue tq = new TokenQueue("One Two");
        tq.consumeWord();
        tq.addFirst("Three");
        assertEquals("Three Two", tq.remainder());
    }
    
    
    @Test 
    public void consumeToIgnoreSecondCallTest(){
		String t = "<textarea>one < two </TEXTarea> third </TEXTarea>";
		TokenQueue tq = new TokenQueue(t);
		String data = tq.chompToIgnoreCase("</textarea>");
		assertEquals("<textarea>one < two ", data);
		
		data = tq.chompToIgnoreCase("</textarea>");
		assertEquals(" third ", data);
    }


    @Test 
    public void consumeToIgnoreCaseEdgeCase() {
        // Test when the target string is at the beginning of the input
        String t = "<textarea>one < two </TEXTarea>";
        TokenQueue tq = new TokenQueue(t);
        String data = tq.chompToIgnoreCase("<textarea");
        assertEquals("", data);
        
        // Test when the target string is not present
        tq = new TokenQueue("No match here");
        data = tq.chompToIgnoreCase("<textarea");
        assertEquals("No match here", data);
    }

    @Test 
    public void consumeToIgnoreCaseWithMultipleMatches() {
        // Test when there are multiple occurrences of the target string
        String t = "<textarea>one < two </TEXTarea> <textarea>three < four </TEXTarea>";
        TokenQueue tq = new TokenQueue(t);
        String data = tq.chompToIgnoreCase("</textarea>");
        assertEquals("<textarea>one < two ", data);
        
        // Consume the second occurrence
        data = tq.chompToIgnoreCase("</textarea>");
        assertEquals(" <textarea>three < four ", data);
    }
    
    @Test
    public void testMatchesStartTag() {
        TokenQueue queue = new TokenQueue("<div>");
        assertTrue(queue.matchesStartTag());

        queue.consume();
        assertFalse(queue.matchesStartTag());
    }


    @Test
    public void testConsumeAttributeKey() {
        TokenQueue tq1 = new TokenQueue("class=\"example\"");
        String attributeKey1 = tq1.consumeAttributeKey();
        assertEquals("class", attributeKey1);
    }

    @Test
    public void testPeek() {
        TokenQueue tq1 = new TokenQueue("example");
        char peekedChar1 = tq1.peek();
        assertEquals('e', peekedChar1);

        TokenQueue tq2 = new TokenQueue("");
        char peekedChar2 = tq2.peek();
        assertEquals((char) 0, peekedChar2);

        TokenQueue tq7 = new TokenQueue("\\<div>");
        char peekedChar7 = tq7.peek();
        assertEquals('\\', peekedChar7);
    }

    @Test
    public void testAddFirstCharacter() {
        TokenQueue tq1 = new TokenQueue("");
        tq1.addFirst('a');
        assertEquals("a", tq1.remainder());
    }

    @Test
    public void testMatchesCS() {
        TokenQueue tq1 = new TokenQueue("");
        assertTrue(tq1.matchesCS(""));

        TokenQueue tq3 = new TokenQueue("def");
        assertTrue(tq3.matchesCS("d"));

        TokenQueue tq4 = new TokenQueue("hello world");
        assertTrue(tq4.matchesCS("hello"));
    }
}
