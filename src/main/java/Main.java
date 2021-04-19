import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
	
	public static WebDriver driver;
	public static final String URL = "https://www.expedia.com/";


	public static final List<String> Cities = new ArrayList<>(Arrays.asList(
//			"Cancun",
"New York"
//			"Las Vegas",
//			"Denver",
//			"Rome",
//			"Milan",
//			"Paris",
//			"Madrid",
//			"Amsterdam",
//			"Singapore"
	)) ;
	
	public static final String DepartureCity = "Atlanta";

	public static final String StartDate = "10/1/2021";  // May 1, 2021
	public static final String EndDate = "10/15/2021";  // August 15, 2021
	public static final int INTERVAL = 6;

	public static void main(String[] args) throws FileNotFoundException, SQLException {
		System.setProperty("webdriver.chrome.driver", "chromedriver");
		driver = new ChromeDriver();

		/*
		We change the width of the window so the date picker will give us all dates
		for a year ahead; so, we are not limited to only two months
		 */
		Dimension dimensions = new Dimension(800,1024);
		driver.manage().window().setSize(dimensions);


		FlightAutomation flightAutomation = new FlightAutomation(driver);
		flightAutomation.start(StartDate, EndDate, Cities, INTERVAL);

//		driver.close();
	}

}
