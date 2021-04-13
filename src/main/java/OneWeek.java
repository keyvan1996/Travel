import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OneWeek {
	
	public static ArrayList<String[]> getOneWeekIntervals(String start, String end) {
		var format = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate = new Date();
		Date endDate = new Date();
		Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false);
		
		try {
			startDate = format.parse(start);
			endDate = format.parse(end);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		var weekList = new ArrayList<String[]>();
		currentDate = startDate;
		while (currentDate.before(endDate)) {
			cal.setTime(currentDate);
			var startWeek = cal.getTime();
			cal.add(Calendar.DATE, 6);
			var endWeek = cal.getTime();
			weekList.add(new String[] {format.format(startWeek), format.format(endWeek)});
			
			cal.add(Calendar.DATE, 1);
			currentDate = cal.getTime();
		}
		
		return weekList;
	}
}
