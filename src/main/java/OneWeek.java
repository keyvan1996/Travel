import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OneWeek {
	
	public static ArrayList<Date[]> getOneWeekIntervals(String start, String end) {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate = new Date();
		Date endDate = new Date();
		Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false);
		int interval = 6;
		
		try {
			startDate = format.parse(start);
			endDate = format.parse(end);
			cal.setTime(endDate);
			cal.add(Calendar.DATE, -interval);
			endDate = cal.getTime();
			cal.setTime(startDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ArrayList<Date[]> weekList = new ArrayList<>();
		currentDate = startDate;
		while (currentDate.before(endDate)) {
			Calendar local_calendar = Calendar.getInstance();
			local_calendar.setTime(currentDate);
			Date startWeek = local_calendar.getTime();
			local_calendar.add(Calendar.DATE, interval);
			Date endWeek = local_calendar.getTime();
//			weekList.add(new String[] {format.format(startWeek), format.format(endWeek)});
			weekList.add(new Date[] {(startWeek), (endWeek)});

			cal.add(Calendar.DATE, 1);
			currentDate = cal.getTime();
		}
		
		return weekList;
	}
}
