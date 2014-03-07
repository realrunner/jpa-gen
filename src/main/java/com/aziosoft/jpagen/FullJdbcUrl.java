package com.aziosoft.jpagen;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mnelson on 3/6/14.
 *
 */
public class FullJdbcUrl {
    String username;
    String password;
    String url;

    public FullJdbcUrl(String username, String password, String url) {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    final static Pattern p = Pattern.compile("([^/]*)/(.*)@(.*)");

    public static FullJdbcUrl parse(String url) throws ParseException {
        Matcher matcher = p.matcher(url);
        if(!matcher.matches()) {
            throw new ParseException("Invalid JDBC URL " + url, 0);
        }
        return new FullJdbcUrl(matcher.group(1), matcher.group(2), matcher.group(3));
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }
}
