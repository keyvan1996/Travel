import java.util.ArrayList;
import java.util.Arrays;

public class Paths {
//	public static final String DepartureAirportDropdown = "//*[@id=\"location-field-leg1-origin\"]";
	public static final String DepartureAirportDropdown = "//*[@id=\"app-layer-location-field-leg1-origin-ta-dialog\"]/div[2]/div/div[1]/section/div/input";
//	public static final String ArrivalAirportDropdown = "//*[@id=\"location-field-leg1-destination\"]";
	public static final String ArrivalAirportDropdown = "//*[@id=\"app-layer-location-field-leg1-destination-ta-dialog\"]/div[2]/div/div[1]/section/div/input";
	//*[@id="wizard-flight-tab-roundtrip"]/div[2]/div[1]/div/div[2]/div/div/div
	//*[@id="location-field-leg1-origin-input"]

//	public static final String DepartureDateBox = "//*[@id=\"flight-departing-hp-flight\"]";
//	public static final String ReturningDateBox = "//*[@id=\"flight-returning-hp-flight\"]";
 	public static final String DepartureDateBox = "//*[@id=\"wizard-flight-tab-roundtrip\"]/div[2]/div[2]/div/div/div[1]";
	public static final String ReturningDateBox = "//*[@id=\"d2-btn\"]";
	
	public static final String FlightButton = "//*[@id=\"uitk-tabs-button-container\"]/li[2]/a";

//	public static final String SearchButton = "//*[@id=\"gcw-flights-form-hp-flight\"]/div[7]/label/button";
	public static final String SearchButton = "//*[@id=\"wizard-flight-pwa-1\"]/div[3]/div[2]/button";

	public static final String PriceResultsList = "//*[@id=\"app-layer-base\"]/div[2]/div[3]/div/section/main/ul";
	public static final String FirstPriceResult = "//*[@id=\"app-layer-base\"]/div[2]/div[3]/div/section/main/ul/li[1]";
	public static final String PriceSpan = ".//div/div/div/div/div[2]/div/div/div/div[1]/section/span[2]";
	public static final String PriceSpanFewSeatsLeft = ".//div/div/div/div/div[2]/div/div/div/div[2]/section/span[2]";
	public static final String PriceSpanSpecialDeal = ".//div/div/div[2]/div/div/div/div[1]/section/span[2]";
	public static final ArrayList<String> PriceSpansLocations = new ArrayList<>(Arrays.asList(PriceSpan, PriceSpanFewSeatsLeft, PriceSpanSpecialDeal));

	public static final String CloseQSIButton = ".//div[2]/div/div[1]/button";
	public static final String QSIModal = "body > div.QSIWebResponsive";
}
