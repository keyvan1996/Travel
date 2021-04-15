import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.SimpleFormatter;

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

	public FlightAutomation(WebDriver driver) throws FileNotFoundException {
		System.out.println("Starting chrome");
		this.driver = driver;
	}

	public void start() {
		ArrayList<Date[]> weeks = OneWeek.getOneWeekIntervals(Main.StartDate, Main.EndDate);

		for (String city : Main.Cities) {
			HashMap<String, Integer> prices = new HashMap<>();
			for (Date[] dayPair : weeks) {
				System.out.println("Searching for flights to " + city + " leaving " + dayPair[0] + " and returning on " + dayPair[1]);
				driver.get(Main.URL);

				goToFlightPage();
				setLocations(city);
				setDates(dayPair[0], dayPair[1]);

				driver.findElement(By.xpath(Paths.SearchButton)).click();
			}

			var lowestPair = prices.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue)).get();
			System.out.println("Lowest price flight departs on " + lowestPair.getKey());
			pw.println("Lowest price flight departs on " + lowestPair.getKey());
		}
		pw.close();
	}

	private void goToFlightPage() {
		try {
			WebElement flightsButton = driver.findElement(By.xpath(Paths.FlightButton));
			flightsButton.click();
		} catch (StaleElementReferenceException e) {
			System.out.println(e);
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

	private void selectDate(Date date){
		LocalDate date_local = LocalDate.from(date.toInstant().atZone(ZoneId.systemDefault())).atStartOfDay().toLocalDate();
		String departMonth = date_local.getMonth().toString();
		String departYear = String.valueOf(date_local.getYear());
		int departDay = date_local.getDayOfMonth();
		String depart_month_year = departMonth + " " +departYear;
		//locate all boxes with all months
		List<WebElement> all_months =  driver.findElement(By.xpath("//*[@id=\"app-layer-datepicker-flights-departure-arrival-start\"]/div[2]/div/div/div[2]/div")).findElements(By.xpath("./div"));
		for(WebElement month_element: all_months){
			// get the month and year value of the current month_element
			String month_name_year = month_element.findElement(By.xpath("./*[@class='uitk-date-picker-month-name uitk-type-medium']")).getText().toUpperCase();
			//check if the month and year match the date we specified
			if (depart_month_year.equals( month_name_year)){
				System.out.println(true);
				// locate all table cells in the current month
				// it usually would be eaqual to number of days in that month + 1 for an empty space
				List<WebElement> dates_elements = month_element.findElements(By.xpath(".//td"));
				System.out.println(dates_elements.size());
				//find the button that matches our date
				for(WebElement date_element : dates_elements){
					try {
						WebElement button_date = date_element.findElement(By.xpath(".//button"));

						int day = Integer.parseInt(button_date.getAttribute("data-day"));
						if(day == departDay){
							((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button_date);
							button_date.click();

						}
					}catch(NoSuchElementException err){
						System.out.println(err.getMessage());
					}

				}

			}
		}
	}
}