package mynnx.yakyak;

import android.os.AsyncTask;

import com.amazonaws.android.auth.CognitoCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;

import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class AmazonDownloadAsync extends AsyncTask<Void, Integer, String> {
    private CognitoCredentialsProvider provider;

    public AmazonDownloadAsync(CognitoCredentialsProvider provider) {
        this.provider = provider;
    }

    @Override
    protected String doInBackground(Void... params) {
        // Gets the list of objects in S3 and return the download URL of the most recent object.

        TransferManager transferManager = new TransferManager(this.provider);
        AmazonS3Client mClient = new AmazonS3Client(this.provider);
        List<S3ObjectSummary> summaries = mClient.listObjects("YakYak-audio").getObjectSummaries();
        String mKey = summaries.get(summaries.size() - 1).getKey();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        URL url = mClient.generatePresignedUrl("YakYak-audio", mKey, calendar.getTime());
        return url.toString();
    }
}
