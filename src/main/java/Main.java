import org.openqa.selenium.Dimension;
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
	
	public static final String StartDate = "6/1/2021";  // May 1, 2021
	public static final String EndDate = "8/15/2021";  // August 15, 2021
	
	public static void main(String[] args) throws FileNotFoundException {
		System.setProperty("webdriver.chrome.driver", "chromedriver");
		driver = new ChromeDriver();

		/*
		We change the width of the window so the date picker will give us all dates
		for a year ahead; so, we are not limited to only two months
		 */
		Dimension dimensions = new Dimension(800,1024);
		driver.manage().window().setSize(dimensions);
		
		FlightAutomation flightAutomation = new FlightAutomation(driver);
		flightAutomation.start();
		flightAutomation.pw.println("New Line");
		flightAutomation.pw.close();

//		driver.close();
	}
}
