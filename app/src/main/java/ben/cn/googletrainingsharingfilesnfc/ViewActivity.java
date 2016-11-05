package ben.cn.googletrainingsharingfilesnfc;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

public class ViewActivity extends AppCompatActivity {
    // A File object containing the path to the transferred files
    private File mParentPath;
    // Incoming Intent
    @SuppressWarnings("FieldCanBeLocal")
    private Intent mIntent;

    /*
         * Called from onNewIntent() for a SINGLE_TOP Activity??
         * or onCreate() for a new Activity. For onNewIntent(),
         * remember to call setIntent() to store the most
         * current Intent??
         *
         */
    private void handleViewIntent() {
        // Get the Intent action
        mIntent = getIntent();
        String action = mIntent.getAction();
        /*
         * For ACTION_VIEW, the Activity is being asked to display data.
         * Get the URI.
         */
        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            // Get the URI from the Intent
            Uri beamUri = mIntent.getData();
            /*
             * Test for the type of URI, by getting its scheme value
             */
            if (TextUtils.equals(beamUri.getScheme(), "file")) {
                mParentPath = handleFileUri(beamUri);
            } else if (TextUtils.equals(
                    beamUri.getScheme(), "content")) {
                mParentPath = handleContentUri(beamUri);
            }
        }
        if (mParentPath != null) {
            Toast.makeText(this, mParentPath.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }
    }

    private File handleContentUri(Uri beamUri) {
        // Position of the filename in the query Cursor
        int filenameIndex;
        // File object for the filename
        File copiedFile;
        // The filename stored in MediaStore
        String fileName;
        // Test the authority of the URI
        if (!TextUtils.equals(beamUri.getAuthority(), MediaStore.AUTHORITY)) {
            /*
             * Handle content URIs for other content providers
             */
            Toast.makeText(this, "Not media-related.", Toast.LENGTH_SHORT).show();
            // For a MediaStore content URI
        } else {
            // Get the column that contains the file name
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor pathCursor =
                    getContentResolver().query(beamUri, projection,
                            null, null, null);
            // Check for a valid cursor
            if (pathCursor != null &&
                    pathCursor.moveToFirst()) {
                // Get the column index in the Cursor
                filenameIndex = pathCursor.getColumnIndex(
                        MediaStore.MediaColumns.DATA);
                // Get the full file name including path??
                fileName = pathCursor.getString(filenameIndex);
                // Create a File object for the filename
                copiedFile = new File(fileName);

                pathCursor.close();

                // Return the parent directory of the file
                return new File(copiedFile.getParent());
            } else {
                // The query didn't work; return null
                return null;
            }
        }
        return null;
    }

    private File handleFileUri(Uri beamUri) {
        // Get the path part of the URI
        String fileName = beamUri.getPath();
        // Create a File object for this filename
        File copiedFile = new File(fileName);
        // Get a string containing the file's parent directory??
        return new File(copiedFile.getParent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        handleViewIntent(); // non SINGLE_TOP
    }
}
