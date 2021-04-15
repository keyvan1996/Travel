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
		
		try {
			startDate = format.parse(start);
			endDate = format.parse(end);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ArrayList<Date[]> weekList = new ArrayList<>();
		currentDate = startDate;
		while (currentDate.before(endDate)) {
			cal.setTime(currentDate);
			Date startWeek = cal.getTime();
			cal.add(Calendar.DATE, 6);
			Date endWeek = cal.getTime();
//			weekList.add(new String[] {format.format(startWeek), format.format(endWeek)});
			weekList.add(new Date[] {(startWeek), (endWeek)});

			cal.add(Calendar.DATE, 1);
			currentDate = cal.getTime();
		}
		
		return weekList;
	}
}
