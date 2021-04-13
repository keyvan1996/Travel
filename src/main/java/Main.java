import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileNotFoundException;

public class Main {
	
	public static WebDriver driver;
	public static final String URL = "https://www.expedia.com/";
	
	public static final String[] Cities = new String[] {
		"New Orleans",
		"Las Vegas",
		"Denver",
		"Rome",
		"Milan",
		"Paris",
		"Madrid",
		"Amsterdam",
		"Singapore"
	};
	
	public static final String DepartureCity = "Atlanta";
	
	public static final String StartDate = "8/1/2021";  // May 1, 2021
	public static final String EndDate = "9/15/2021";  // August 15, 2021
	
	public static void main(String[] args) throws FileNotFoundException {
		System.setProperty("webdriver.chrome.driver", "chromedriver");
		driver = new ChromeDriver();
		
		var flightAutomation = new FlightAutomation(driver);
		flightAutomation.start();
		flightAutomation.pw.println("New Line");
		flightAutomation.pw.close();

//		driver.close();
	}
}
