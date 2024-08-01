package com.experiment.dsa1;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import fi.iki.elonen.NanoHTTPD;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;
import java.awt.Desktop;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@SpringBootApplication
public class Dsa1Application {

/*
Step 1: get Auth code by pasting the following in the browser

https://accounts.google.com/o/oauth2/v2/auth?
scope=openid&profile&email&
access_type=offline&
include_granted_scopes=true&
response_type=code&
state=state_parameter_passthrough_value&
redirect_uri=http://localhost&
client_id=998890755657-o4bsgukkf7u186ronht27hrv1nt5sg7r.apps.googleusercontent.com


Step2: Get the access token, refresh token, expires in, token id by using the following curl command from terminal

curl \
--request POST \
--data "code=4/0AcvDMrBdMZYq0P4Isj9RVxvA3K-J5ALCPUaBPma9m7ioE6ZJrin776nvdx-mpbyRg0UaIA&client_id=998890755657-o4bsgukkf7u186ronht27hrv1nt5sg7r.apps.googleusercontent.com&client_secret=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&redirect_uri=http://localhost&grant_type=authorization_code" \https://oauth2.googleapis.com/token


Step3: Get the refreshed access token using the refresh token

curl \
--request POST \
--data "client_id=998890755657-o4bsgukkf7u186ronht27hrv1nt5sg7r.apps.googleusercontent.com&client_secret=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&refresh_token=1//04uWx4euyIrMTCgYIARAAGAQSNwF-L9Ir-gdmcnVklYjIOLDCOMgre3uu2hLShzNbcj-TpHnY27CSpn2ZSOL7nwxsmMF5vtEARmk&grant_type=refresh_token" \https://accounts.google.com/o/oauth2/token
*/

	private static String authCode = null;
	private static String accessToken = null;
	private static String refreshToken = null;
	private static Long accessTokenExpiration ;
	private static Date timeAtWhichAccessTokenGenerated;
	private static final String CLIENT_ID = "998890755657-o4bsgukkf7u186ronht27hrv1nt5sg7r.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "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";
	private static final String redirectUri = "http://localhost";
	private static final int authRedirectUriPort = 80;
	private static final String authorizationEndPoint = "https://accounts.google.com/o/oauth2/v2/auth";
	private static final String accessAndRefreshTokensUrl = "https://oauth2.googleapis.com/token";
	private static final String refreshTokenUrl = "https://accounts.google.com/o/oauth2/token";

	static Calendar service = null;

	public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, GeneralSecurityException {
		SpringApplication.run(Dsa1Application.class, args);
		System.out.println("Hello");

		//generate authorization code
		getAuthCode();

		//generate access token and refresh token
		getAccessAndRefreshTokens();

		System.out.println("--------");
		System.out.println("Authorization: " + authCode);
		System.out.println("--------");
		System.out.println("Access Token: " + accessToken);
		System.out.println("--------");
		System.out.println("Refresh Token: " + refreshToken);
		System.out.println("--------");

		//build the request with a valid access token
		GCalendarService();

		processCalendarEvents();

    }

	public static void processCalendarEvents() throws IOException {
/*
		String pageToken = null;
		do {
			CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
			List<CalendarListEntry> items = calendarList.getItems();

			for (CalendarListEntry calendarListEntry : items) {
				System.out.println(calendarListEntry.getSummary());
			}
			pageToken = calendarList.getNextPageToken();
		} while (pageToken != null);
		*/
		Events events = service.events()
				.list("primary")
				.setMaxResults(100)
				.setTimeMin(new DateTime(System.currentTimeMillis()))
//				.setOrderBy("startTime")
				.execute();

		List<Event> items = events.getItems();
		if(items.isEmpty()){
			System.out.println("No upcoming events found");
		}else{
			System.out.println("Upcoming events: ");
			for(Event event : items){
//				DateTime start = event.getStart().getDateTime();
//				if(start == null){
//					start = event.getStart().getDate();
//				}else{
				if(event.size() > 4){
					System.out.println("Event summary: " + event.getSummary() + " , attendees: "+ event.getAttendees());
					if(event.getAttendees().size() > 2){
						for(EventAttendee att: event.getAttendees()){
							System.out.println("Id: " + att.getId() + ", email: "+ att.getEmail());
						}

					}
				}

			}
		}
	}

	public static void GCalendarService() throws IOException, GeneralSecurityException{

		System.out.println("AT Exp: "+ accessTokenExpiration);
		long initial = timeAtWhichAccessTokenGenerated.getTime();
		GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, new Date((initial + accessTokenExpiration)*1000) ));
//		GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, null ));

		HttpRequestInitializer httpRequestInitializer = new HttpCredentialsAdapter(credentials);
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

		//creating gcalendar service
		service = new Calendar.Builder(HTTP_TRANSPORT,
				new JacksonFactory(),
				httpRequestInitializer)
				.setApplicationName("project2898ad")
				.build();
	}
	public static class OAuth2Server extends NanoHTTPD {
		private final BlockingQueue<String> urlQueue;
		public OAuth2Server(int port, BlockingQueue<String> urlQueue) {
			super(port);
			this.urlQueue = urlQueue;
		}

		@Override
		public Response serve(IHTTPSession session) {
			if ("/".equals(session.getUri())) {
				String fullUrl = session.getUri() + "?" + session.getQueryParameterString();
//				System.out.println("Captured URL: " + fullUrl);

				urlQueue.offer(fullUrl);
				return newFixedLengthResponse("Authorization successful. You can close this window.");
			}
			return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
		}
	}
	private static void getAuthCode() throws IOException, URISyntaxException, InterruptedException {

		BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(1);
		OAuth2Server server = new OAuth2Server(authRedirectUriPort, urlQueue);
		server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);

		String authorizationUrl = String.format(
				"%s?scope=%s&access_type=%s&include_granted_scopes=%s&response_type=%s&state=%s&redirect_uri=%s&client_id=%s",
				authorizationEndPoint,
				"https://www.googleapis.com/auth/calendar",
				"offline",
				"true",
				"code",
				"state_parameter_passthrough_value",
				redirectUri,
				CLIENT_ID
		);

		if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
		//	windows only
			Desktop.getDesktop().browse(new URI(authorizationUrl));
		//	below for only ubuntu
		//	runtime.exec("/usr/bin/firefox -new-window" + authorizationUrl);
		}else{
			//mac only
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("open " + authorizationUrl);
		}

		String capturedUrl = urlQueue.take();
//		System.out.println("auth redirected url: " + capturedUrl);
		String[] temp = capturedUrl.split("&");

		authCode = temp[1].split("=")[1];
	}
	private static void getAccessAndRefreshTokens() throws IOException {

		Map<String, Object> params = new LinkedHashMap<>();
		params.put("code", authCode);
		params.put("client_id", CLIENT_ID);
		params.put("client_secret", CLIENT_SECRET);
		params.put("redirect_uri", redirectUri);
		params.put("grant_type", "authorization_code");

		StringBuilder postData = new StringBuilder();

		for(Map.Entry<String, Object> param : params.entrySet()){

			if(!postData.isEmpty()){
				postData.append('&');
			}
			postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
			postData.append('=');
			if(Objects.equals(param.getKey(), "redirect_uri")){
				postData.append((String) param.getValue());
			}else{
				postData.append(URLEncoder.encode((String) param.getValue(), StandardCharsets.UTF_8));
			}
		}

		byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

		try {
			URL url = new URL(accessAndRefreshTokensUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.getOutputStream().write(postDataBytes);
			System.out.println(connection);

			if (connection.getResponseCode() >= 400) {
				InputStream errorStream = connection.getErrorStream();
				if (errorStream != null) {
					BufferedReader reader2 = new BufferedReader(new InputStreamReader(errorStream));
					StringBuilder buffer = new StringBuilder();
					String line;
					while ((line = reader2.readLine()) != null) {
						buffer.append(line);
					}
					reader2.close();

					// Process the error message
					String errorMessage = buffer.toString();
					System.out.println("Error message: " + errorMessage);
				}
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder buffer = new StringBuilder();

				for(String line = reader.readLine(); line!=null; line = reader.readLine()){
					buffer.append(line);
				}

				JSONObject json = new JSONObject(buffer.toString());
//				System.out.println(json);
				accessToken = json.getString("access_token");
				refreshToken = json.getString("refresh_token");
				accessTokenExpiration = (long)json.getInt("expires_in");
				timeAtWhichAccessTokenGenerated = new Date();

			}
		}catch (Exception exception){
			System.out.println(exception.getMessage());
		}

	}
	private static String getRefreshedAccessToken() {

		try {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("grant_type", "refresh_token");
			params.put("client_id", CLIENT_ID);
			params.put("client_secret", CLIENT_SECRET);
			params.put("refresh_token",
					refreshToken);

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (!postData.isEmpty()) {
					postData.append('&');
				}
				postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), StandardCharsets.UTF_8));
			}
			byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

			URL url = new URL(refreshTokenUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.getOutputStream().write(postDataBytes);

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuilder buffer = new StringBuilder();
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				buffer.append(line);
			}

			JSONObject json = new JSONObject(buffer.toString());
			System.out.println(json);

			accessToken = json.getString("access_token");

			return accessToken;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


}
