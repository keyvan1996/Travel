import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.sql.*;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.*;
import java.util.Date;


public class FlightAutomation {

    public static final String NON_STOP_FILTER = "[{\"numOfStopFilterValue\":{\"stopInfo\":{\"stopFilterOperation\":\"EQUAL\",\"numberOfStops\":0}}}]";
    private int price;
    private final WebDriver driver;
    public Wait<WebDriver> wait;
    //setting up the DB
    private static Connection connection;
    private static final String DB_URL = "jdbc:sqlite:flight";

    public FlightAutomation(WebDriver driver) throws SQLException {
        System.out.println("Starting chrome");
        this.driver = driver;
        wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(NoSuchElementException.class);
        connection = DriverManager.getConnection(DB_URL);
    }

    /**
     * Main method of the flight automation that launches the search tool
     * doesn't return anything but prints out to the console and adds records to the db
     * @param start_date String in format mm/dd/yyyy
     * @param end_date String in format mm/dd/yyyy
     * @param cities List<String>
     * @param interval number of nights intended to spend at the destination
     * @throws SQLException
     */
    public void start(String start_date, String end_date, List<String> cities,int interval) throws SQLException {
        ArrayList<Date[]> weeks = DateHelper.getDateIntervals(start_date, end_date, interval);
        for (String city : cities) {
            HashMap<String, Integer> prices = new HashMap<>();
            for (Date[] dayPair : weeks) {
                System.out.println("Searching for flights to " + city + " leaving " +
                        DateHelper.getStringFromDate(dayPair[0]) + " and returning on " +
                        DateHelper.getStringFromDate(dayPair[1])
                );
                /*
                Makes url with query params that specify locations, dates, non-stop filter
                 */
                String new_url = composeURL(city, dayPair[0], dayPair[1]);
                getPage(new_url);
                price = (int) getCheapestPrice();
                // in this case, there were no flights found that satisfy our parameters
                if (price != Integer.MAX_VALUE) {
                    prices.put(DateHelper.getStringFromDate(dayPair[0]), price);
                }
            }

            Map.Entry<Integer, List<String>> price_dates = getCheapestFlightDates(prices);
            if(price_dates.getKey()< Integer.MAX_VALUE){
                String text = "Lowest price flight ($" +price_dates.getKey() + ") to " + city + " departs on " +  price_dates.getValue().toString();
                System.out.println(text);
                addRowToFlightTable(city, price_dates.getValue().toString(), price_dates.getKey());
            }else{
                String text = "Couldn't find any flights from Atlanta to "+ city+" between "+ DateHelper.getStringFromDate(weeks.get(0)[0])+
                        " and " + DateHelper.getStringFromDate(weeks.get(weeks.size()-1)[1]);
                System.out.println(text);
            }

        }
    }

    /**
     * Makes a string of url with search params
     * @param destination city to go to
     * @param start date of outbound flight
     * @param end date of return flgiht
     * @return String url
     */
    public String composeURL(String destination, Date start, Date end) {
        String url = "";
        try {
            String end_date = DateHelper.getStringFromDate(end);
            String start_date = DateHelper.getStringFromDate(start);
            url = ("https://www.expedia.com/Flights-Search?filters=" +
                    URLEncoder.encode(NON_STOP_FILTER, "UTF-8") +
                    "&leg1=" +
                    URLEncoder.encode("from:Atlanta,GA,to:" + destination + ",departure:" + start_date + "TANYT", "UTF-8") +
                    "&leg2=" + URLEncoder.encode("from:" + destination + " ,to:Atlanta,GA,departure:" + end_date + "TANYT", "UTF-8") +
                    "&mode=search&options=" +
                    URLEncoder.encode("carrier:*,maxhops:1", "UTF-8") +
                    "&passengers=" +
                    URLEncoder.encode("adults:1,children:0", "UTF-8") +
                    "&sortOrder=INCREASING&sortType=PRICE&trip=roundtrip")
                    .replace("%26", "&").replace("+", "%20");
        } catch (java.io.UnsupportedEncodingException err) {
            System.out.println(err.getMessage());
        }
        return url;
    }

    /**
     * finds cheapest flight and all dates with such price
     * @param prices a hashmap with <Date, Price> entries
     * @return Map.Entry<Price, List of dates(string)>
     */
    public Map.Entry<Integer, List<String>> getCheapestFlightDates(HashMap<String, Integer> prices){
        HashMap<Integer, List<String>> price_dates = new HashMap<>();
        Integer least_price = Integer.MAX_VALUE;
        for(Map.Entry<String, Integer> date_price: prices.entrySet()){
            Integer new_key = date_price.getValue();
            String date_value = date_price.getKey();
            List<String> current_value = price_dates.getOrDefault(new_key, new ArrayList<>());
            current_value.add(date_value);
            price_dates.put(new_key, current_value);
            if(new_key < least_price){
                least_price = new_key;
            }
        }
        return new AbstractMap.SimpleEntry<>(least_price, price_dates.get(least_price));
    }

    /**
     * Adds a record to the db
     * @param city
     * @param leavingDate
     * @param price
     * @throws SQLException
     */
    public static void addRowToFlightTable(String city, String leavingDate, int price) throws SQLException {
        String sql = "insert into flight (city, departDate, price, timestamp ) values (?,?,?, CURRENT_TIMESTAMP)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, city);
            ps.setString(2, leavingDate);
            ps.setInt(3, price);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets current number of records in the db
     * @return
     */
    public int getDbSize(){
        String sql = "select count(*) from flight";
        int count = -1;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            rs.next();
            count = rs.getInt("count(*)");
            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * loads a page in the driver
     * @param url
     */
    public void getPage(String url){
        driver.get(url);
    }
    /**
     * Closes chrome window
     */
    public void closeUp(){
        driver.close();
    }

    /**
     * Handles logic when a dialog window pops up with survey request
     * this does not happen often but if it happens, it breaks the price fetching logic of one iteration
     */
    private void handleQSI() {
        try {
            WebElement QSImodal = driver.findElement(By.cssSelector(Paths.QSIModal));
            try{
                QSImodal.findElement(By.xpath(Paths.CloseQSIButton)).click();
            }catch(NoSuchElementException err){
                System.out.println("couldn't close the modal");
            }
        }
        catch(NoSuchElementException err){
        }
    }

    /**
     * this method finds the cheapest price from the non stop flight for the specified cities from the date given to the program
     * @return
     */
    public double getCheapestPrice() {
        double price = Double.MAX_VALUE;
        try {
            WebElement PriceLI = wait.until((driver) -> driver.findElement(By.xpath(Paths.FirstPriceResult)));
            boolean failedAttempt = false;
            for (String price_xpath : Paths.PriceSpansLocations) {
                try {
                    WebElement price_span = PriceLI.findElement(By.xpath(price_xpath));
                    price = Double.parseDouble(price_span.getText().replace("$", "").replace(",", ""));
                    if (failedAttempt) {
                        System.out.println("Finally got the price for you: " + price);
                    }
                    return price;
                } catch (NoSuchElementException err) {
                    failedAttempt = true;
                    System.out.println("Couldn't get the price. Maybe flight is in High Demand or there is a special deal.\nTrying again");
                }
            }
        } catch (Exception exception) {
            System.out.println("Something is not right. Maybe there are not non-stop flights. It is an exceptional situation.");
            System.out.println(exception.getMessage());
        }
        System.out.println("Couldn't get the price this time");
        return price;
    }
}