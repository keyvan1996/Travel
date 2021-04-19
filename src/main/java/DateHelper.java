import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {
	
	public static ArrayList<Date[]> getDateIntervals(String start, String end, int interval) {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate = new Date();
		Date endDate = new Date();
		Date currentDate;
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false);
		try {
			startDate = format.parse(start);

			endDate = calculateEndDateWithInterval(format.parse(end), interval);

			cal.setTime(startDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ArrayList<Date[]> weekList = new ArrayList<>();
		currentDate = startDate;
		while (!currentDate.after(endDate)) {
			Calendar local_calendar = Calendar.getInstance();
			local_calendar.setTime(currentDate);
			Date startWeek = local_calendar.getTime();
			local_calendar.add(Calendar.DATE, interval);
			Date endWeek = local_calendar.getTime();
			weekList.add(new Date[] {(startWeek), (endWeek)});
			cal.add(Calendar.DATE, 1);
			currentDate = cal.getTime();
		}
		return weekList;
	}

	private static Date calculateEndDateWithInterval(Date endDate, int interval){
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		cal.add(Calendar.DATE, -interval);
		return cal.getTime();
	}
	public static String getStringFromDate(Date date){
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		return format.format(date);
	}
}
