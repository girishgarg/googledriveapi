package googledriveapi;
import googledriveapi.Quickstart;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.http.GenericUrl;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import java.io.*;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class Quickstart {
	 /** Application name. */
    private static final String APPLICATION_NAME =
        "Drive API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/drive-java-quickstart92");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY,DriveScopes.DRIVE_FILE,DriveScopes.DRIVE_APPDATA,DriveScopes.DRIVE,DriveScopes.DRIVE_PHOTOS_READONLY,DriveScopes.DRIVE_METADATA,DriveScopes.DRIVE_SCRIPTS);

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
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            Quickstart.class.getResourceAsStream("/client_secret.json");
        boolean found = Quickstart.class.getResourceAsStream("/client_secret.json")!=null;
        System.out.println(found);
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        System.out.println(credential.getAccessToken());
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
        Drive service = getDriveService();

        // Print the names and IDs for up to 10 files.
       
        FileList result = service.files().list()
             .setPageSize(10)
             .setFields("nextPageToken, files(id, name)")
             .execute();
        List<File> files = result.getFiles();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
            //	System.out.println(file.get(file.getId()));
                System.out.printf("%s (%s) [%s]\n", file.getName(), file.getId(),file.getWebViewLink());
               // System.out.println(file.getContentHints());
               // System.out.println(file.getPermissions());
               // System.out.println(file.getWebContentLink());
               // System.out.println(file.getWritersCanShare());
            	
            }
            System.out.println(service.files().get("0B3L3Vyq1vaxwX3ZqODB1TEhGNUpMcjMyY25DTlBFWF9PUDdR").executeMediaAsInputStream());
        }
        
        //to upload a file https://developers.google.com/drive/v3/web/manage-uploads
       File fileMetadata = new File();
        fileMetadata.setName("photo.jpg");
        java.io.File filePath = new java.io.File("F:/images/104ND750/DSC_6359.JPG");
        FileContent mediaContent = new FileContent("image/jpeg", filePath);
        File file = service.files().create(fileMetadata, mediaContent)
            .setFields("id")
            .execute();
        System.out.println("File ID: " + file.getId());
        
        //downlaod a file https://developers.google.com/drive/v3/web/manage-downloads
        /*String fileId = "0B6OtIpAL6oa6amUzMkpuMXFzekU";
        OutputStream outputStream = new ByteArrayOutputStream();
        service.files().get(fileId).setAlt("media")
            .executeAndDownloadTo(outputStream);
        System.out.println(outputStream);*/
      
       
        
        //to delete a file https://developers.google.com/drive/v2/reference/files/delete
        
        
        
        //download google doc format
        String fileId = "1umsQ6dcVb6dJ_zrjZA1g9lJ6QBS7YPbYOfE3zuaSUjI";
        OutputStream outputStream = new ByteArrayOutputStream();
        service.files().export(fileId, "application/pdf")
            .executeMediaAndDownloadTo(outputStream);
        String test = new String(((ByteArrayOutputStream) outputStream).toByteArray());
        System.out.println(test);
       

    }
    
    
}	
