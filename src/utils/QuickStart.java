package utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class QuickStart {
	/** Application name. */
	private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".credentials/calendar-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials at
	 * ~/.credentials/calendar-java-quickstart
	 */
	static com.google.api.services.calendar.Calendar calendarService = null;
	static boolean logIn = false;
	
	
	
	private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR	);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
		// Load client secrets.

		InputStream in = new FileInputStream("client_secret.json");
		// QuickStart.class.getResourceAsStream(TEST);

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Calendar client service.
	 * 
	 * @return an authorized Calendar client service
	 * @throws IOException
	 */
	public static com.google.api.services.calendar.Calendar getCalendarService() throws IOException {
		Credential credential = authorize();
		return new com.google.api.services.calendar.Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}
	public static void logIn() throws IOException{
		calendarService = getCalendarService();
		DateTime now = new DateTime(System.currentTimeMillis());
		System.out.println(getCalendarService().calendarList());
		Events events = calendarService.events().list("primary").setMaxResults(10).setTimeMin(now).setOrderBy("startTime")
				.setSingleEvents(true).execute();
		List<Event> items = events.getItems();
		if (items.size() == 0) {
			System.out.println("No upcoming events found.");
		} else {
			System.out.println("Upcoming events");
			for (Event event : items) {
				DateTime start = event.getStart().getDateTime();
				if (start == null) {
					start = event.getStart().getDate();
				}
				System.out.printf("%s (%s)\n", event.getSummary(), start);
			}
		}
		if(calendarService != null){
			logIn = true;
		}
	}
	public static boolean checkLogIn(){
		
		return logIn;
	}
	public static void addEvent(String summary, Date startDate, Date endDate) throws IOException{
		Event eventTest = new Event().setSummary(summary);
		DateTime startDateTime = new DateTime(startDate);
		EventDateTime startTest = new EventDateTime()
		    .setDateTime(startDateTime)
		    .setTimeZone("America/Los_Angeles");
		eventTest.setStart(startTest);

		DateTime endDateTime = new DateTime(endDate);
		EventDateTime end = new EventDateTime()
		    .setDateTime(endDateTime)
		    .setTimeZone("America/Los_Angeles");
		eventTest.setEnd(end);

		String calendarId = "primary";
		eventTest = calendarService.events().insert(calendarId, eventTest).execute();
		System.out.printf("Event created: %s\n", eventTest.getHtmlLink());
	}

//	public static void main(String[] args) throws IOException {
//		// Build a new authorized API client service.
//		// Note: Do not confuse this class with the
//		// com.google.api.services.calendar.model.Calendar class.
//		com.google.api.services.calendar.Calendar gCalService = getCalendarService();
//
//		
//		// List the next 10 events from the primary calendar.
//		DateTime now = new DateTime(System.currentTimeMillis());
//		System.out.println(getCalendarService().calendarList());
//		Events events = gCalService.events().list("primary").setMaxResults(10).setTimeMin(now).setOrderBy("startTime")
//				.setSingleEvents(true).execute();
//		List<Event> items = events.getItems();
//		if (items.size() == 0) {
//			System.out.println("No upcoming events found.");
//		} else {
//			System.out.println("Upcoming events");
//			for (Event event : items) {
//				DateTime start = event.getStart().getDateTime();
//				if (start == null) {
//					start = event.getStart().getDate();
//				}
//				System.out.printf("%s (%s)\n", event.getSummary(), start);
//			}
//		}
//		//TEEST==========
//		Event eventTest = new Event().setSummary("Google I/O 2015").setLocation("800 Howard St., San Francisco, CA 94103")
//				.setDescription("A chance to hear more about Google's developer products.");
//		DateTime startDateTime = new DateTime("2017-05-28T09:00:00-07:00");
//		EventDateTime startTest = new EventDateTime()
//		    .setDateTime(startDateTime)
//		    .setTimeZone("America/Los_Angeles");
//		eventTest.setStart(startTest);
//
//		DateTime endDateTime = new DateTime("2017-05-28T17:00:00-07:00");
//		EventDateTime end = new EventDateTime()
//		    .setDateTime(endDateTime)
//		    .setTimeZone("America/Los_Angeles");
//		eventTest.setEnd(end);
//
//		String calendarId = "primary";
//		eventTest = gCalService.events().insert(calendarId, eventTest).execute();
//		System.out.printf("Event created: %s\n", eventTest.getHtmlLink());
//		//TEEST==========
//	}

}