import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.SQLException;
import java.util.*;

public class TestingFlightAutomation {

    WebDriver driver;
    FlightAutomation flightAuto;

    @Before
    public void setUp() throws SQLException {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();
        Dimension dimensions = new Dimension(800,1024);
        driver.manage().window().setSize(dimensions);
        flightAuto = new FlightAutomation(driver);
    }

    @Test
    public void testComposeUrl(){
        Date[] dates = DateHelper.getDateIntervals("09/10/2021", "09/20/2021", 6).get(0);
        String url = flightAuto.composeURL("Vladivostok", dates[0], dates[1]);
        Assert.assertTrue("Testing destination:",url.contains("Vladivostok"));
        Assert.assertTrue("Testing departure date:",url.contains("09%2F10%2F2021"));
        Assert.assertTrue("Testing return date:", url.contains("09%2F16%2F2021"));
    }

    //this test may not work properly, since prices for tickets change over time
    // date of test 04/18/2021
    @Test
    public void testGettingCheapestPriceOffPage(){
        String url = "https://www.expedia.com/Flights-Search?leg1=from%3AAtlanta%2CGA%2Cto%3ANew%20York%20%28NYC%20-%20All%20Airports%29%2Cdeparture%3A1%2F4%2F2022TANYT&mode=search&options=carrier%3A%2A%2Ccabinclass%3A%2Cmaxhops%3A1%2Cnopenalty%3AN&pageId=0&passengers=adults%3A1%2Cchildren%3A0%2Cinfantinlap%3AN&trip=oneway";
        flightAuto.getPage(url);
        double price = flightAuto.getCheapestPrice();
        Assert.assertEquals("Comparing cheapest price: ", 90.0, price, 2);
    }

    //this test may not work properly, since prices for tickets change over time
    // date of test 04/18/2021
    @Test
    public void testNoDirectFlightBehavior(){
        String url ="https://www.expedia.com/Flights-Search?leg1=from%3AAtlanta%2C%20GA%20%28ATL-Hartsfield-Jackson%20Atlanta%20Intl.%29%2Cto%3ARome%20%28ROM%20-%20All%20Airports%29%2Cdeparture%3A4%2F18%2F2021TANYT&mode=search&options=carrier%3A%2A%2Ccabinclass%3A%2Cmaxhops%3A1%2Cnopenalty%3AN&pageId=0&passengers=adults%3A1%2Cchildren%3A0%2Cinfantinlap%3AN&trip=oneway";
        flightAuto.getPage(url);
        int price = (int) flightAuto.getCheapestPrice();
        Assert.assertEquals("Testing behavior when no price is found: ",(int) Double.MAX_VALUE, price);
        String another_url = "https://www.expedia.com/Flights-Search?leg1=from%3AAtlanta%2C%20GA%20%28ATL-Hartsfield-Jackson%20Atlanta%20Intl.%29%2Cto%3ARome%20%28ROM%20-%20All%20Airports%29%2Cdeparture%3A5%2F24%2F2021TANYT&mode=search&options=carrier%3A%2A%2Ccabinclass%3A%2Cmaxhops%3A1%2Cnopenalty%3AN&pageId=0&passengers=adults%3A1%2Cchildren%3A0%2Cinfantinlap%3AN&trip=oneway";
        flightAuto.getPage(another_url);
        price = (int) flightAuto.getCheapestPrice();
        Assert.assertNotEquals("Testing that price will be different: ", (int) Double.MAX_VALUE, price);
    }

    @Test
    public void testCheapestFlightDatesSingle(){
        HashMap<String, Integer> date_price = new HashMap<>();
        date_price.put("05/01/2021", 1000);
        date_price.put("05/02/2021", 999);
        date_price.put("05/03/2021", 998);
        date_price.put("05/04/2021", 997);
        date_price.put("05/05/2021", 996);
        Map.Entry<Integer, List<String>> price_dates = flightAuto.getCheapestFlightDates(date_price);
        Assert.assertEquals("Testing correct date: ", "05/05/2021", price_dates.getValue().get(0));
        Assert.assertEquals("Testing correct price: ", Integer.valueOf(996), price_dates.getKey());
    }
    @Test
    public void testCheapestFlightDatesMultiple(){
        HashMap<String, Integer> date_price = new HashMap<>();
        date_price.put("05/01/2021", 1000);
        date_price.put("05/02/2021", 996);
        date_price.put("05/03/2021", 996);
        date_price.put("05/04/2021", 997);
        date_price.put("05/05/2021", 996);
        Map.Entry<Integer, List<String>> price_dates = flightAuto.getCheapestFlightDates(date_price);
        Assert.assertEquals("Testing the number of correct dates: ", 3,price_dates.getValue().size());
        Assert.assertTrue("Testing contains correct date: ",  price_dates.getValue().contains("05/05/2021"));
        Assert.assertTrue("Testing contains correct date: ",  price_dates.getValue().contains("05/02/2021"));
        Assert.assertTrue("Testing contains correct date: ",  price_dates.getValue().contains("05/03/2021"));
        Assert.assertEquals("Testing correct price: ", Integer.valueOf(996), price_dates.getKey());
    }

    /**
     * this method test whether or not thte AddingtoDB method works properly and adding the finding results to the database
     * @throws SQLException
     */
    @Test
    public void testAddingToDB() throws SQLException {
        int db_size_before = flightAuto.getDbSize();
        FlightAutomation.addRowToFlightTable("Yuryuzan"+ (int) (Math.random()*1000), "99/99/99",0);
        int db_size_after = flightAuto.getDbSize();
        Assert.assertNotEquals("Size changed: ", db_size_before, db_size_after);
        Assert.assertEquals("Size Increased by One: ", db_size_before+1, db_size_after);
    }
    @After
    public void cleanUp(){
        flightAuto.closeUp();
    }
}

