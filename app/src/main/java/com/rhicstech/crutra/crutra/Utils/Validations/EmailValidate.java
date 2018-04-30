package com.rhicstech.crutra.crutra.Utils.Validations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rhicstechii on 06/02/2018.
 */

public class EmailValidate {
    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public EmailValidate() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    /**
     * Validate hex with regular expression
     *
     * @param hex
     *            hex for validation
     * @return true valid hex, false invalid hex
     */
    public boolean validate(final String hex) {

        matcher = pattern.matcher(hex);
        return matcher.matches();

    }

    public boolean passwordMatch(String p1,String p2){
        if(p1.contentEquals(p2)){
            return true;
        }
        else{
            return false;
        }
    }
}