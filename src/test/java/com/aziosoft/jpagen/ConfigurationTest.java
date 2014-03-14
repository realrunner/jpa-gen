package com.aziosoft.jpagen;

import com.aziosoft.jpagen.config.Configuration;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mnelson on 3/5/14.
 *
 */
public class ConfigurationTest {

    @Test
    public void testConfigurationDeserializationSuccess() throws IOException {
        InputStream s = ConfigurationTest.class.getResourceAsStream("/test-1.json");
        Configuration config = Configuration.fromStream(s);
        Assert.assertEquals("com.aziosoft.auctoweb.model", config.getPackageName());
        Assert.assertEquals(true, config.getExcludePrefixInClassNames());
        Assert.assertEquals(2, config.getCustomDataTypes().size());
        Assert.assertEquals(3, config.getFieldTypes().size());
        Assert.assertEquals(7, config.getCustomAnnotations().size());
        Assert.assertEquals(3, config.getRelationships().size());
        Assert.assertEquals(1, config.getClassNames().size());
    }
}
