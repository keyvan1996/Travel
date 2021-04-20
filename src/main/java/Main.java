import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
	
	public static WebDriver driver;
	public static final List<String> Cities = new ArrayList<>(Arrays.asList(
			"Cancun",
			"Las Vegas",
			"Denver",
			"Rome",
			"Milan",
			"Paris",
			"Madrid",
			"Amsterdam",
			"Singapore"
	)) ;
	public static final String StartDate = "05/1/2021";  // May 1, 2021
	public static final String EndDate = "08/15/2021";  // August 15, 2021
	public static final int INTERVAL = 6;

	public static void main(String[] args) throws SQLException {
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
	}

}
