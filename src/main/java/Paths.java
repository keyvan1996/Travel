import java.util.ArrayList;
import java.util.Arrays;


public class Paths {
	public static final String FirstPriceResult = "//*[@id=\"app-layer-base\"]/div[2]/div[3]/div/section/main/ul/li[1]";
	public static final String PriceSpan = ".//div/div/div/div/div[2]/div/div/div/div[1]/section/span[2]";
	public static final String PriceSpanFewSeatsLeft = ".//div/div/div/div/div[2]/div/div/div/div[2]/section/span[2]";
	public static final String PriceSpanSpecialDeal = ".//div/div/div[2]/div/div/div/div[1]/section/span[2]";
	public static final ArrayList<String> PriceSpansLocations = new ArrayList<>(Arrays.asList(PriceSpan, PriceSpanFewSeatsLeft, PriceSpanSpecialDeal));

	public static final String CloseQSIButton = ".//div[2]/div/div[1]/button";
	public static final String QSIModal = "body > div.QSIWebResponsive";
}
