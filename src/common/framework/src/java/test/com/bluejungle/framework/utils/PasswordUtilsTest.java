/*
 * Created on Sep 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.framework.utils;

import com.bluejungle.framework.utils.PasswordUtils;

import junit.framework.TestCase;

/**
 * @author sasha
 */

public class PasswordUtilsTest extends TestCase {

    /**
     * Constructor for PasswordUtilsTest.
     * 
     * @param arg0
     */
    public PasswordUtilsTest(String arg0) {
        super(arg0);
    }

    public final void testIsValidPasswordDefault() {
        assertTrue("testIsValidPasswordDefault - Valid password should succceed", PasswordUtils.isValidPasswordDefault("F1!456789a"));
        assertTrue("testIsValidPasswordDefault - Valid password should succceed", PasswordUtils.isValidPasswordDefault("F1!456789abcdefghijklmnopqrstuvwxyzF1!456789abcdefghijklmnopqrstuvwxyzF1!456789abcdefghijklmnopqrstuvwxyzF1!456789abcdefghijklm"));
        assertTrue("testIsValidPasswordDefault - Valid password should succceed", PasswordUtils.isValidPasswordDefault("#fFfA234567890"));
        assertTrue("testIsValidPasswordDefault - Valid password should succceed", PasswordUtils.isValidPasswordDefault("F1!456789abcdefghijklmnopqrstuvwxyz"));
        assertTrue("testIsValidPasswordDefault - Valid password should succceed", PasswordUtils.isValidPasswordDefault("f1!456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertTrue("testIsValidPasswordDefault - Valid password should succceed", PasswordUtils.isValidPasswordDefault("Fa1234567!@#$%^&*()-_+={}[]|:;\"'<>,.?/~`"));
        assertTrue("testIsValidPasswordDefault - Valid password should succceed", PasswordUtils.isValidPasswordDefault("##fA2345678"));
        assertTrue("testIsValidPasswordDefault - Valid password should succceed", PasswordUtils.isValidPasswordDefault("#ffA2345678"));
        assertTrue("testIsValidPasswordDefault - Valid password should succceed", PasswordUtils.isValidPasswordDefault("#fAA2345678"));
        assertTrue("testIsValidPasswordDefault - Valid password should succceed", PasswordUtils.isValidPasswordDefault("#fA22345678"));

        
        assertFalse("testIsValidPasswordDefault - Password smaller than minimum length should fail", PasswordUtils.isValidPasswordDefault("f1"));
        assertFalse("testIsValidPasswordDefault - Long passwords should fail", PasswordUtils.isValidPasswordDefault("F1!456789abcdefghijklmnopqrstuvwxyzF1!456789abcdefghijklmnopqrstuvwxyzF1!456789abcdefghijklmnopqrstuvwxyzF1!456789abcdefghijklmnopqrstuvwxyzF1!456789abcdefghijklmnopqrstuvwxyzF1!456789abcdefghijklmnopqrstuvwxyzF1!456789abcdefghijklmnopqrstuvwxyzF1!456789abcdefghijklmnopqrstuvwxyz")); 
        assertFalse("testIsValidPasswordDefault - Password without number should fail", PasswordUtils.isValidPasswordDefault("Fg!mabcde"));
        assertFalse("testIsValidPasswordDefault - Password without uppercase should fail", PasswordUtils.isValidPasswordDefault("fg!1mabcde"));
        assertFalse("testIsValidPasswordDefault - Password without lowercase should fail", PasswordUtils.isValidPasswordDefault("FG!1MABCDE"));
        assertFalse("testIsValidPasswordDefault - Password without special characters should fail", PasswordUtils.isValidPasswordDefault("fA2345678"));
        assertFalse("testIsValidPasswordDefault - Password with more than two identical consecutive characters should fail", PasswordUtils.isValidPasswordDefault("@@@fA2345678"));
        assertFalse("testIsValidPasswordDefault - Password with more than two identical consecutive characters should fail", PasswordUtils.isValidPasswordDefault("@fffA2345678"));
        assertFalse("testIsValidPasswordDefault - Password with more than two identical consecutive characters should fail", PasswordUtils.isValidPasswordDefault("@fAAA2345678"));
        assertFalse("testIsValidPasswordDefault - Password with more than two identical consecutive characters should fail", PasswordUtils.isValidPasswordDefault("#fA234567888"));
    }

    public final void testIsValidPassword() {
        // Test with no resrictions
        assertTrue("no restrictions should pass", PasswordUtils.isValidPassword("foo", 0, Integer.MAX_VALUE, false, false));

        // Test with null
        assertFalse("null password should fail", PasswordUtils.isValidPassword(null, 0, Integer.MAX_VALUE, false, false));

        // Test minimum length
        assertTrue("minimum length should pass", PasswordUtils.isValidPassword("foobar123", 8, Integer.MAX_VALUE, false, false));
        assertFalse("minimum length should fail", PasswordUtils.isValidPassword("foobar", 8, Integer.MAX_VALUE, false, false));

        // Test maximum length
        assertTrue("maximum length thould pass", PasswordUtils.isValidPassword("foobar", 0, 32, false, false));
        assertFalse("maximum length should fail", PasswordUtils.isValidPassword("foobar123", 0, 8, false, false));

        // Test numbers required
        assertTrue("numbers required should pass", PasswordUtils.isValidPassword("1fooBar", 0, Integer.MAX_VALUE, true, false));
        assertTrue("numbers required should pass", PasswordUtils.isValidPassword("foo1Bar", 0, Integer.MAX_VALUE, true, false));
        assertTrue("numbers required should pass", PasswordUtils.isValidPassword("fooBar1", 0, Integer.MAX_VALUE, true, false));
        assertTrue("numbers required should pass", PasswordUtils.isValidPassword("12foo34Bar56", 0, Integer.MAX_VALUE, true, false));
        assertFalse("numbers required should fail", PasswordUtils.isValidPassword("fooBar", 0, Integer.MAX_VALUE, true, false));
        assertFalse("just numbers should fail", PasswordUtils.isValidPassword("12345678", 0, Integer.MAX_VALUE, true, false));

        // Test non-word characters required
        assertTrue("non-word characters required should pass", PasswordUtils.isValidPassword("&fooBar", 0, Integer.MAX_VALUE, false, true));
        assertTrue("non-word characters required should pass", PasswordUtils.isValidPassword("foo%Bar", 0, Integer.MAX_VALUE, false, true));
        assertTrue("non-word characters required should pass", PasswordUtils.isValidPassword("fooBar^", 0, Integer.MAX_VALUE, false, true));
        assertTrue("non-word characters required should pass", PasswordUtils.isValidPassword("`~f!@o#$o%^3&*4()B-_a=+r[{5]}6|\\", 0, Integer.MAX_VALUE, true, false));
        assertFalse("non-word characters required should fail", PasswordUtils.isValidPassword("fooBar123", 0, Integer.MAX_VALUE, false, true));
        assertFalse("just non-word characters should fail", PasswordUtils.isValidPassword("`~@#$%^&*()_-+={[}]\\|;:'\",<>.?/", 0, Integer.MAX_VALUE, false, true));
    }

}
