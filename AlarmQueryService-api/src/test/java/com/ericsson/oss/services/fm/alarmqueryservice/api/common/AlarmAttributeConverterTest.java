package com.ericsson.oss.services.fm.alarmqueryservice.api.common;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AlarmAttributeConverterTest {

    @Test
    public void testGetLongAttributeValue() {
        Long result = AlarmAttributeConverter.getLongAttributeValue("probableCause", 234L);
        Assert.assertEquals(result, new Long(234));
        result = AlarmAttributeConverter.getLongAttributeValue("probableCause", null);
        Assert.assertEquals(result, new Long(0));
    }

    @Test
    public void testGetDateAttributeValue() {
        // test null attribute
        Assert.assertNull(AlarmAttributeConverter.getDateAttributeValue("probableCause", null));

        // test date is different from String
        Assert.assertNull(AlarmAttributeConverter.getDateAttributeValue("probableCause", 234L));

        // test date is a correct string
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date result = AlarmAttributeConverter.getDateAttributeValue("probableCause", "2021-03-17T11:47:34.234Z");
        Assert.assertEquals("2021-03-17T11:47:34.234Z", simpleDateFormat.format(result));
        Assert.assertNotNull(result);

        //Input date without milliseconds
        result = AlarmAttributeConverter.getDateAttributeValue("probableCause", "2021-03-17T11:47:34Z");
        Assert.assertEquals("2021-03-17T11:47:34.000Z", simpleDateFormat.format(result));
        Assert.assertNotNull(result);

        //Test incorrect date
        Assert.assertNull(AlarmAttributeConverter.getDateAttributeValue("probableCause", "2021-03-17T11:47.0Z"));

        // test date is a date
        Assert.assertNotNull(AlarmAttributeConverter.getDateAttributeValue("probableCause", new Date()));
    }
}
