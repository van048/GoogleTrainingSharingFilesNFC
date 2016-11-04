package ben.cn.googletrainingsharingfilesnfc;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    @SuppressWarnings("FieldCanBeLocal")
    private NfcAdapter mNfcAdapter;
    // Flag to indicate that Android Beam is available
    @SuppressWarnings("unused")
    boolean mAndroidBeamAvailable = false;
    // List of URIs to provide to Android Beam
    private final Uri[] mFileUris = new Uri[10];
    // Instance that returns available files from this app
    @SuppressWarnings("FieldCanBeLocal")
    private FileUriCallback mFileUriCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        specifyFilesToSend();// TODO: 2016/11/4  

        PackageManager packageManager = getPackageManager();
        // NFC isn't available on the device
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            /*
             * Disable NFC features here.
             * For example, disable menu items or buttons that activate
             * NFC-related features
             */
            Toast.makeText(this, "NFC not supported.", Toast.LENGTH_SHORT).show();
            // Android Beam file transfer isn't supported
        } else if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // If Android Beam isn't available, don't continue.
            mAndroidBeamAvailable = false;
            /*
             * Disable Android Beam file transfer features here.
             */
            Toast.makeText(this, "Android Beam file transfer not supported.", Toast.LENGTH_SHORT).show();
            // Android Beam file transfer is available, continue
        } else {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            /*
             * Instantiate a new FileUriCallback to handle requests for
             * URIs
             */
            mFileUriCallback = new FileUriCallback();
            // Set the dynamic callback for URI requests.
            mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, this);
        }
    }

    /**
     * Callback that Android Beam file transfer calls to get
     * files to share
     */
    private class FileUriCallback implements
            NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {
        }

        /**
         * Create content URIs as needed to share with another device
         */
        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            return mFileUris;
        }
    }

    @SuppressLint("SetWorldReadable")
    private void specifyFilesToSend() {
        String transferFile = "transfer_image.jpg";
        File extDir = getExternalFilesDir(null);
        File requestFile = new File(extDir, transferFile);
        boolean setReadableResult = requestFile.setReadable(true, false);
        if (!setReadableResult) return;
        // Get a URI for the File and add it to the list of URIs
        Uri fileUri = Uri.fromFile(requestFile);
        if (fileUri != null) {
            mFileUris[0] = fileUri;// TODO: 2016/11/4  
        } else {
            Log.e("My Activity", "No File URI available for file.");
        }
    }
}
