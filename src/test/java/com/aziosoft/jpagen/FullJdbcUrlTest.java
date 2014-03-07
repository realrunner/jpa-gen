package com.aziosoft.jpagen;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;

/**
 * Created by mnelson on 3/6/14.
 *
 */
public class FullJdbcUrlTest {

    @Test
    public void testParse() throws ParseException {
        FullJdbcUrl url = FullJdbcUrl.parse("mike/nelson@jdbc:mysql://localhost/emp");
        Assert.assertEquals("mike", url.getUsername());
        Assert.assertEquals("nelson", url.getPassword());
        Assert.assertEquals("jdbc:mysql://localhost/emp", url.getUrl());
    }

    @Test
    public void testParseHarder() throws ParseException {
        FullJdbcUrl url = FullJdbcUrl.parse("mike_nelson/some crazy password with @@ %as / +++ -@jdbc:mysql://localhost:8080/emp");
        Assert.assertEquals("mike_nelson", url.getUsername());
        Assert.assertEquals("some crazy password with @@ %as / +++ -", url.getPassword());
        Assert.assertEquals("jdbc:mysql://localhost:8080/emp", url.getUrl());
    }
}
