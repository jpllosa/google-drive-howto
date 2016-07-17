package example;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

/**
 * 
 * @author Joel Patrick Llosa
 *
 */
public class ReadFile {

	// to run: java username appName
	public static void main(String[] args) throws Exception {
		// read and write scope
		List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE_READONLY, DriveScopes.DRIVE_FILE);

		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		// credentials will be in the credentials directory
		// once you have stored credential, no need to log in
		FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(new java.io.File("credentials"));

		// Load client secrets.
		GoogleClientSecrets googleClientSecrets = GoogleClientSecrets.load(jsonFactory,
				new InputStreamReader(new FileInputStream(new java.io.File("credentials/client_id.json"))));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory,
				googleClientSecrets, SCOPES).setDataStoreFactory(fileDataStoreFactory).setAccessType("offline").build();

		// authorize(userId)
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(args[0]);
		// Credentials saved to credentialsFolder

		Drive driveService = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName(args[1])
				.build();

		FileList result = driveService.files().list().execute();
		List<File> files = result.getItems();
		String fileId = null;
		for (File file : files) {
            if (file.getTitle().equals("howToSave.txt")) {
                fileId = file.getId();
                break;
            }
        }
		
		if (fileId != null) {
			InputStream is = driveService.files().get(fileId).executeMediaAsInputStream();
			int data;
			while ((data = is.read()) >= 0) {
				System.out.print((char)data);
			}
			System.out.println("end of file reached");
			is.close();
		} else {
			System.out.println("file not found...");
		}
		
		httpTransport.shutdown();
	}
}
