package com.experiment.dsa1;

import com.experiment.dsa1.gmail.GmailServiceAndBuild;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import fi.iki.elonen.NanoHTTPD;
import jakarta.mail.MessagingException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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
public class Dsa1Application implements CommandLineRunner {

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
	public static String accessToken = null;
	public static String refreshToken = null;
	public static Long accessTokenExpiration;
	public static Date timeAtWhichAccessTokenGenerated;


	@Value("${oauth2.google.access_and_refresh_tokens_request_end_point}")
	private	String accessAndRefreshTokensUrl;

	@Value("${oauth2.google.refresh_token_request_end_point}")
	private String refreshTokenUrl;

//	private static final String CLIENT_ID = "998890755657-o4bsgukkf7u186ronht27hrv1nt5sg7r.apps.googleusercontent.com";
//	private static final String CLIENT_SECRET = "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";
//	private static final String redirectUri = "http://localhost";
//	private static final int authRedirectUriPort = 80;
//	private static final String authorizationEndPoint = "https://accounts.google.com/o/oauth2/v2/auth";
//	private static final String accessAndRefreshTokensUrl = "https://oauth2.googleapis.com/token";
//	private static final String refreshTokenUrl = "https://accounts.google.com/o/oauth2/token";


	public static void main(String[] args)  throws  Exception {
		SpringApplication.run(Dsa1Application.class, args);

    }

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Hello");
		//generate authorization code
//		getAuthCode();
		//generate access token and refresh token
//		getAccessAndRefreshTokens();

		System.out.println("--------");
		System.out.println("Authorization: " + authCode);
		System.out.println("--------");
		System.out.println("Access Token: " + accessToken);
		System.out.println("--------");
		System.out.println("Refresh Token: " + refreshToken);
		System.out.println("--------");

		//build the request with a valid access token
//		GCalendarService();
//		processCalendarEvents();
	}

}
