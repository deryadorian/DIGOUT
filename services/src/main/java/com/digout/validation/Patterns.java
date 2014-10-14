package com.digout.validation;

import java.util.regex.Pattern;

import com.digout.utils.Matchers;

public final class Patterns {

    public static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Z0-9_\\-*]+[\\w\\.\\p{Space}_*'-]*$",
            Pattern.CASE_INSENSITIVE);
    public static final int PASSWORD_COMPLEXITY = 4;
    public static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=[-_a-zA-Z0-9]*?)\\S{" + PASSWORD_COMPLEXITY
            + ",}$");
    public static final Pattern FULLNAME_PATTERN = Pattern.compile("^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}");
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@(?:[A-Z0-9-]+\\.)+[A-Z]{2,4}$",
            Pattern.CASE_INSENSITIVE);
    public static final Pattern PHONE_PATTERN = Pattern.compile("(0)[0-9]{10}");
    public static final Pattern REGION_CITY_PATTERN = Pattern.compile("^(\\p{L}+)(-|\\s*)?(\\p{L}+)*$");
    public static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("\\d+");
    public static final Pattern ADDRESS_LINE_PATTERN = Pattern.compile("[\\p{L}\\p{Z}\\p{P}\\p{N}]{1,40}");
    public static final Pattern ADDRESS_LINE_PATTERN2 = Pattern.compile("[\\p{Alnum}/:'.,\\-\\s]{1,40}");
    public static final Pattern IBAN_PATTERN = Pattern.compile("^TR\\d{7}[0-9A-Z]{17}$");
    public static final Pattern CREDIT_CARD_NUMBER = Pattern.compile(""
            + "^(?:4[0-9]{12}(?:[0-9]{3})?          # Visa\n" + " |  5[1-5][0-9]{14}                  # MasterCard\n"
            + " |  3[47][0-9]{13}                   # American Express\n"
            + " |  3(?:0[0-5]|[68][0-9])[0-9]{11}   # Diners Club\n"
            + " |  6(?:011|5[0-9]{2})[0-9]{12}      # Discover\n" + " |  (?:2131|1800|35\\d{3})\\d{11}      # JCB\n"
            + ")$");
    public static final Pattern EXPIRATION_YEAR_MONTH_PATTERN = Pattern.compile("\\d{2}");
    public static final Pattern CVV_PATTERN = Pattern.compile("\\d{3,4}");

    private Patterns() {
    }

    public static void main(String[] args) {
    	System.out.println(Matchers.matches("ğffgGNRÜODĞğhnaiun3123-\\.,:'   de", ADDRESS_LINE_PATTERN));
    	System.out.println(Matchers.matches("HRGODIADFGDCVDCÜTCODO RКGĞFDOFSW", REGION_CITY_PATTERN));
	}
}
