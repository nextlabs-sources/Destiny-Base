/*
 * Created on Sep 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.framework.utils;

import java.util.regex.Pattern;

/**
 * Various utilities related to password management
 * 
 * @author sasha
 */

public final class PasswordUtils {
    public static final int DEFAULT_PASSWORD_MIN_LENGTH = 7;
    public static final int DEFAULT_PASSWORD_MAX_LENGTH = 12;
    public static final boolean DEFAULT_PASSWORD_NUMBER_REQUIRED = true;
    public static final boolean DEFAULT_PASSWORD_NON_WORD_REQUIRED = true;

    /**
     * Just because you can do everything in a regex, doesn't mean you *should*
     *
     * This checks to see if the password has 
     *   at least one number
     *   at least one lowercase letter
     *   at least one uppercase letter
     *   at least one non-alphanumeric character
     *   and is between 10 and 128 characters
     *   and contains no more than 2 identical consecutive characters
     *
     * The OWSAP requirements actually specify just three out of the first four and the last two
     */
    private static final String PASSWORD_PATTERN_REGEX = "^(?:(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]))(?!.*(.)\\1{2,})[A-Za-z0-9!~`<>,;:_=?*+#.'\\\\\"&%()\\|\\[\\]\\{\\}\\-\\$\\^\\@\\/]{10,128}";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_PATTERN_REGEX);
    
    /**
     * Validates the supplied password according to the OWASP rules as described
     * in PASSWORD_PATTERN
     * 
     * @param password  the password to verify
     * @return true if the password satisfies all the rules, false otherwise
     */
    public static final boolean isValidPasswordDefault(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validates supplied password according to the specified options and rules.
     * Rules that are always enforced are: password can never be null, and it
     * must contain at least one alphabet character [a-zA-Z]. Additional rules
     * include minimum and maximum length requirements, requiring at least one
     * numeric character, and requiring at least one non-alphanumeric character
     * 
     * @param password
     *            the password to verify
     * @param minLength
     *            minimum required length, specify 0 if no minimum required
     * @param maxLength
     *            maximum supported length, specify Integer.MAX_VALUE if no
     *            maximum is enforced
     * @param numberRequired
     *            if true then password will be checked for presense of at least
     *            one numeric character
     * @param nonWordRequired
     *            if true then password will be checked for presense of at least
     *            one non-alphanumeric character, such as $
     * @return true if the password satisfies all the rules, false otherwise
     */
    public static final boolean isValidPassword(String password, int minLength, int maxLength, boolean numberRequired, boolean nonWordRequired) {
        if (password == null) {
            return false;
        }

        int length = password.length();
        if (length < minLength || length > maxLength) {
            return false;
        }

        if (!password.matches(".*[a-zA-Z]+.*")) {
            return false;
        }

        if (numberRequired) {
            if (!password.matches(".*\\d+.*")) {
                return false;
            }
        }

        if (nonWordRequired) {
            if (!password.matches(".*\\W+.*")) {
                return false;
            }
        }
        return true;
    }

}
