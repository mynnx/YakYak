package mynnx.yakyak;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.android.auth.CognitoCredentialsProvider;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import java.io.File;
import java.util.Date;

/**
 * Created by mynnx on 7/24/14.
 */
public class AmazonUploadAsync extends AsyncTask<File, Integer, String> {
    private CognitoCredentialsProvider provider;

    public AmazonUploadAsync(CognitoCredentialsProvider provider) { this.provider = provider; }

    @Override
    protected String doInBackground(File... file) {
        String filename = new Long(new Date().getTime()).toString() + ".3gp";

        TransferManager transferManager = new TransferManager(this.provider);
        Upload upload = transferManager.upload(
                "YakYak-audio",
                filename,
                file[0]);
        return "Hooray!";
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("yakyak", "Uploaded!");
    }
}
