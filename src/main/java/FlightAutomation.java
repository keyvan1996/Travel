import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


public class FlightAutomation {
    File priceFile = new File("PriceFile.txt");

    PrintWriter pw = new PrintWriter(priceFile);

    public ArrayList<Integer> priceListNewOrleans = new ArrayList<>();
    public ArrayList<Integer> priceListLasVegas = new ArrayList<>();
    public ArrayList<Integer> priceListDenver = new ArrayList<>();
    public ArrayList<Integer> priceListRome = new ArrayList<>();
    public ArrayList<Integer> priceListMilan = new ArrayList<>();
    public ArrayList<Integer> priceListParis = new ArrayList<>();
    public ArrayList<Integer> priceListMadrid = new ArrayList<>();
    public ArrayList<Integer> priceListAmsterdam = new ArrayList<>();
    public ArrayList<Integer> priceListSingapore = new ArrayList<>();

    private int price;
    private WebDriver driver;
    public Wait<WebDriver> wait;

    public FlightAutomation(WebDriver driver) throws FileNotFoundException {
        System.out.println("Starting chrome");
        this.driver = driver;
        wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(NoSuchElementException.class);

    }

    public void start() {
        ArrayList<Date[]> weeks = OneWeek.getOneWeekIntervals(Main.StartDate, Main.EndDate);
        for (Date[] week : weeks) {
            System.out.println(week[0].toString());
            System.out.println(week[1].toString());
        }
        for (String city : Main.Cities) {
            HashMap<String, Integer> prices = new HashMap<>();
            for (Date[] dayPair : weeks) {
                System.out.println("Searching for flights to " + city + " leaving " + dayPair[0] + " and returning on " + dayPair[1]);
                driver.get(Main.URL);

                /*
                Makes url with query params that specify locations, dates, non-stop filter
                 */
                String new_url = composeURL(city, dayPair[0], dayPair[1]);
                driver.get(new_url);

                /*
                used for Selenium GUI date, location selection
                 */
//                goToFlightPage();
//                setLocations(city);
//                setDates(dayPair[0], dayPair[1]);
//                driver.findElement(By.xpath(Paths.SearchButton)).click();

                driver.findElement(By.xpath(Paths.SearchButton)).click();
                price = (int) getCheapestPrice();
                // in this case, there were no flights found that satisfy our parameters
                if (price != Integer.MAX_VALUE) {
                    prices.put(OneWeek.getStringFromDate(dayPair[0]), price);
                }
            }
            System.out.println(prices.toString());
            Optional<Map.Entry<String, Integer>> lowestPair = prices.entrySet().stream().min((x, y) -> Integer.compare(x.getValue(), y.getValue()));
            if(lowestPair.isPresent()){
                String text = "Lowest price flight ($" + lowestPair.get().getValue() + ") to " + city + " departs on " + lowestPair.get().getKey();
                System.out.println(text);
                pw.println(text);
                addRowToFlightTable(city, lowestPair.get().getKey(), price);
            }else{
                String text = "Couldn't find any flights from Atlanta to "+ city+" between "+OneWeek.getStringFromDate(weeks.get(0)[0])+
                        " and " + OneWeek.getStringFromDate(weeks.get(weeks.size()-1)[1]);
                System.out.println(text);
                pw.println(text);
            }

        }
        pw.close();
    }

    private void goToFlightPage() {
        try {
            WebElement flightsButton = driver.findElement(By.xpath(Paths.FlightButton));
            flightsButton.click();
        } catch (StaleElementReferenceException e) {
            System.out.println(e);
        }catch(ElementClickInterceptedException err){
            System.out.println("perhaps QSI modal window was found");
            handleQSI();
        }
    }

    private void setLocations(String arrival) {
        try {
//			WebElement a = driver.findElement(By.xpath("//*[@id=\"location-field-leg1-origin-menu\"]/div[1]/button"));
            WebElement departureInputButton = driver.findElement(By.xpath("//*[@id=\"wizard-flight-tab-roundtrip\"]/div[2]/div[1]/div/div[1]/div/div/div/button"));
            departureInputButton.click();

            WebElement departLoc = driver.findElement(By.xpath(Paths.DepartureAirportDropdown));
            clearField(departLoc);
            departLoc.sendKeys(Main.DepartureCity);
            departLoc.sendKeys(Keys.ENTER);
//			WebElement aa = driver.findElement(By.xpath("//*[@id=\"location-field-leg1-origin-menu\"]/div[2]/ul/li[1]/button"));
//			aa.click();
//			WebElement arrivalInputButton = driver.findElement(By.cssSelector("#location-field-leg1-destination-menu > div.uitk-field.has-floatedLabel-label.has-icon.has-no-placeholder > button"));
            WebElement arrivalInputButton = driver.findElement(By.xpath("//*[@id=\"wizard-flight-tab-roundtrip\"]/div[2]/div[1]/div/div[2]/div/div/div/button"));
            arrivalInputButton.click();
            WebElement arriveLoc = driver.findElement(By.xpath(Paths.ArrivalAirportDropdown));
            clearField(arriveLoc);
            arriveLoc.sendKeys(arrival);
            arriveLoc.sendKeys(Keys.ENTER);
//			WebElement bb = driver.findElement(By.xpath("//*[@id=\"location-field-leg1-destination-menu\"]/div[2]/ul/li[1]/button"));
//			bb.click();
        } catch (StaleElementReferenceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setDates(Date start, Date end) {
        try {
            WebElement a = driver.findElement(By.xpath("//*[@id=\"d1-btn\"]"));
            a.click();
            WebElement departDate = driver.findElement(By.xpath(Paths.DepartureDateBox));
            selectDate(start);
            selectDate(end);

            WebElement done = driver.findElement(By.xpath("//*[@id=\"app-layer-datepicker-flights-departure-arrival-start\"]/div[2]/div/div/div[3]/button"));
            done.click();

//			clearField(departDate);
//			departDate.sendKeys(start.toString());
//			WebElement arriveDate = driver.findElement(By.xpath(Paths.ReturningDateBox));
//			clearField(arriveDate);
//			arriveDate.sendKeys(end.toString());
        } catch (StaleElementReferenceException e) {
            System.out.println(e.getMessage());
            driver.close();

        }
    }

    private void clearField(WebElement element) {
        try {
            element.click();
            element.clear();
            element.sendKeys(Keys.CONTROL, "a", Keys.BACK_SPACE);
        } catch (StaleElementReferenceException e) {
            System.out.println(e.getMessage());
        }
    }

    private double getCheapestPrice() {
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

    private void selectDate(Date date) {
        LocalDate date_local = LocalDate.from(date.toInstant().atZone(ZoneId.systemDefault())).atStartOfDay().toLocalDate();
        String departMonth = date_local.getMonth().toString();
        String departYear = String.valueOf(date_local.getYear());
        int departDay = date_local.getDayOfMonth();
        String depart_month_year = departMonth + " " + departYear;
        //locate all boxes with all months
        List<WebElement> all_months = driver.findElement(By.xpath("//*[@id=\"app-layer-datepicker-flights-departure-arrival-start\"]/div[2]/div/div/div[2]/div")).findElements(By.xpath("./div"));
        for (WebElement month_element : all_months) {
            // get the month and year value of the current month_element
            String month_name_year = month_element.findElement(By.xpath("./*[@class='uitk-date-picker-month-name uitk-type-medium']")).getText().toUpperCase();
            //check if the month and year match the date we specified
            if (depart_month_year.equals(month_name_year)) {

                // locate all table cells in the current month
                // it usually would be equals to number of days in that month + 1 for an empty space
                List<WebElement> dates_elements = month_element.findElements(By.xpath(".//td"));

                //find the button that matches our date
                for (WebElement date_element : dates_elements) {
                    try {
                        WebElement button_date = date_element.findElement(By.xpath(".//button"));

                        int day = Integer.parseInt(button_date.getAttribute("data-day"));
                        if (day == departDay) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button_date);
                            button_date.click();

                        }
                    } catch (NoSuchElementException err) {
//                        System.out.println(err.getMessage());
                    }

                }

            }
        }
    }

    public String composeURL(String destination, Date start, Date end) {
        String url = "";
        try {
            String end_date = OneWeek.getStringFromDate(end);
            String start_date = OneWeek.getStringFromDate(start);
            url = ("https://www.expedia.com/Flights-Search?filters=" +
                    URLEncoder.encode(Main.NON_STOP_FILTER, "UTF-8") +
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
    //setting up the DB
    private static Connection connection;
    private static final String DB_URL = "jdbc:sqlite:flight";


    public static void addRowToFlightTable(String city, String leavingDate, int price) throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        String sql = "insert into flight (city, departDate, price) values (?,?,?)";
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

}