package at.jku.se.prse.team3;

import com.dropbox.core.*;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CommitInfo;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;

public class CloudBackup {

    private static final String CLIENT_IDENTIFIER = "wk400vxjbjtryt0";

    //Custom exception for Dropbox upload errors.

    public static class CloudBackupException extends RuntimeException {
        /**
         * Constructs a new CloudBackupException with the specified detail message and cause.
         *
         * @param message The detail message (which is saved for later retrieval by the getMessage() method).
         * @param cause   The cause (which is saved for later retrieval by the getCause() method).
         */
        public CloudBackupException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class DropboxUploadException extends RuntimeException {
        /**
         * Constructs a new DropboxUploadException with the specified detail message and cause.
         *
         * @param message The detail message (which is saved for later retrieval by the getMessage() method).
         * @param cause   The cause (which is saved for later retrieval by the getCause() method).
         */
        public DropboxUploadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    //Custom exception for file upload errors.

    public static class FileUploadException extends DbxException {
        /**
         * Constructs a new FileUploadException with the specified detail message and cause.
         *
         * @param message The detail message (which is saved for later retrieval by the getMessage() method).
         * @param cause   The cause (which is saved for later retrieval by the getCause() method).
         */
        public FileUploadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exportiert Datein in ein Dropbox Cloud Ordner. Issue 35.
     *
     * @param localPath Pfad wo lokale Daten lokal gespeichert sind.
     * @param cloudPath Cloudpfad wo die Daten gespeichert werden.
     * @param accessToken Access Token um Transaktion zu authorisieren, jede 4 Stunden wird neue gebraucht.
     */
    public static void uploadDB(String localPath, String cloudPath, String accessToken) throws FileUploadException {
        try (InputStream input = new FileInputStream(localPath)) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder(CLIENT_IDENTIFIER).build();
            DbxClientV2 client = new DbxClientV2(config, accessToken);

            CommitInfo commitInfo = CommitInfo.newBuilder(cloudPath)
                    .withMode(WriteMode.OVERWRITE)
                    .build();

            FileMetadata metadata = client.files().uploadBuilder(cloudPath)
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(input);

        } catch (IOException | UploadErrorException e) {
            throw new DropboxUploadException("Error uploading to Dropbox",e);
        } catch (DbxException e) {
            throw new FileUploadException("Dropbox error",e);
        }
    }

}