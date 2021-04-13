import org.openqa.selenium.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

	public FlightAutomation(WebDriver driver) throws FileNotFoundException {
		System.out.println("Starting chrome");
		this.driver = driver;
	}

	public void start() {
		var weeks = OneWeek.getOneWeekIntervals(Main.StartDate, Main.EndDate);

		for (var city : Main.Cities) {
			var prices = new HashMap<String, Integer>();
			for (var dayPair : weeks) {
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
			var a = driver.findElement(By.xpath("//*[@id=\"location-field-leg1-origin-menu\"]/div[1]/button"));
			a.click();
			var departLoc = driver.findElement(By.xpath(Paths.DepartureAirportDropdown));
			clearField(departLoc);
			departLoc.sendKeys(Main.DepartureCity);
			var aa = driver.findElement(By.xpath("//*[@id=\"location-field-leg1-origin-menu\"]/div[2]/ul/li[1]/button"));
			aa.click();
			var b = driver.findElement(By.cssSelector("#location-field-leg1-destination-menu > div.uitk-field.has-floatedLabel-label.has-icon.has-no-placeholder > button"));
			b.click();
			var arriveLoc = driver.findElement(By.xpath(Paths.ArrivalAirportDropdown));
			clearField(arriveLoc);
			arriveLoc.sendKeys(arrival);
			var bb = driver.findElement(By.xpath("//*[@id=\"location-field-leg1-destination-menu\"]/div[2]/ul/li[1]/button"));
			bb.click();
		} catch (StaleElementReferenceException e) {
			System.out.println(e);
		}
	}

	private void setDates(String start, String end) {
		try {
			var a = driver.findElement(By.xpath("//*[@id=\"d1-btn\"]"));
			a.click();
			var departDate = driver.findElement(By.id(Paths.DepartureDateBox));
			clearField(departDate);
			departDate.sendKeys(start);
			var arriveDate = driver.findElement(By.xpath(Paths.ReturningDateBox));
			clearField(arriveDate);
			arriveDate.sendKeys(end);
		} catch (StaleElementReferenceException e) {
			System.out.println(e);

		}
	}

	private void clearField(WebElement element) {
		try {
			element.click();
			element.clear();
			element.sendKeys(Keys.CONTROL, "a", Keys.BACK_SPACE);
		} catch (StaleElementReferenceException e) {
			System.out.println(e);
		}
	}
}