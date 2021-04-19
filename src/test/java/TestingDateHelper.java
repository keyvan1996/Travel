import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TestingDateHelper {
    @Test
    public void testOneWeekIntervalBeginning(){
        ArrayList<Date[]> dates =  DateHelper.getDateIntervals("05/01/2021", "05/07/2021", 6);
        Assert.assertEquals("Comparing start date month: ",Calendar.MAY , dates.get(0)[0].getMonth());
        Assert.assertEquals("Comparing start date day: ",  1, dates.get(0)[0].getDate());
        Assert.assertEquals("Comparing start date year: ",  2021, 1900+dates.get(0)[0].getYear());
        Assert.assertEquals("Comparing end date month: ",Calendar.MAY , dates.get(0)[1].getMonth());
        Assert.assertEquals("Comparing end date day: ",  7, dates.get(0)[1].getDate());
        Assert.assertEquals("Comparing start date year: ",  2021, 1900+dates.get(0)[1].getYear());
    }
    @Test
    public void testOneWeekIntervalEnd(){
        ArrayList<Date[]> dates =  DateHelper.getDateIntervals("05/01/2021", "08/15/2021", 6);
        Assert.assertEquals("Comparing last interval start date month: ",Calendar.AUGUST , dates.get(dates.size()-1)[0].getMonth());
        Assert.assertEquals("Comparing last interval start date day: ",  9, dates.get(dates.size()-1)[0].getDate());
        Assert.assertEquals("Comparing last interval start date year: ",  2021, 1900+dates.get(dates.size()-1)[0].getYear());
        Assert.assertEquals("Comparing last interval end date month: ",Calendar.AUGUST , dates.get(dates.size()-1)[1].getMonth());
        Assert.assertEquals("Comparing last interval end date day: ",  15, dates.get(dates.size()-1)[1].getDate());
        Assert.assertEquals("Comparing last interval start date year: ",  2021, 1900+dates.get(dates.size()-1)[1].getYear());
    }
    @Test
    public void testOneWeekIntervalMonthChange(){
        ArrayList<Date[]> dates =  DateHelper.getDateIntervals("05/31/2021", "08/15/2021", 6);
        Assert.assertEquals("Comparing month change start date month: ",Calendar.MAY , dates.get(0)[0].getMonth());
        Assert.assertEquals("Comparing month change start date day: ",  31, dates.get(0)[0].getDate());
        Assert.assertEquals("Comparing month change start date year: ",  2021, 1900+dates.get(0)[0].getYear());
        Assert.assertEquals("Comparing month change end date month: ",Calendar.JUNE , dates.get(0)[1].getMonth());
        Assert.assertEquals("Comparing month change end date day: ",  6, dates.get(0)[1].getDate());
        Assert.assertEquals("Comparing month change start date year: ",  2021, 1900+dates.get(0)[1].getYear());
    }

    @Test
    public void testDateToStringParse(){
        Date date = new Date(1618797229992L);

        String date_string = DateHelper.getStringFromDate(date);
        Assert.assertEquals("Comparing 04/18/21: ", "04/18/2021", date_string);
    }
}
