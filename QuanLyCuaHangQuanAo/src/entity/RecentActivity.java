package entity;


import java.security.Timestamp;
import java.util.concurrent.TimeUnit;

public class RecentActivity {
	private String type;
	private String id;
	private String description;
	private java.sql.Timestamp activityDate;
	private String iconPath;

	// Constructor and getters

	public String getTimeAgo() {
		// Calculate time difference between now and activity date
		long diffInMillies = System.currentTimeMillis() - activityDate.getTime();
		long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillies);

		if (diffMinutes < 60) {
			return diffMinutes + " phút trước";
		} else if (diffMinutes < 1440) { // less than 24 hours
			long hours = diffMinutes / 60;
			return hours + " giờ trước";
		} else {
			long days = diffMinutes / 1440;
			return days + " ngày trước";
		}
	}
}