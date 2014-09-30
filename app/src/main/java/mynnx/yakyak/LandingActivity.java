package mynnx.yakyak;

import android.app.ListActivity;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.amazonaws.android.auth.CognitoCredentialsProvider;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import mynnx.yakyak.models.Yak;


public class LandingActivity extends ListActivity {

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private CognitoCredentialsProvider awsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AmazonLoginAsync amazonLogin = new AmazonLoginAsync(getApplicationContext());
        amazonLogin.execute();
        try {
            this.awsProvider = amazonLogin.get();
        } catch (ExecutionException e) {
            Log.d("yakyak", "Login failed: " + e);      // TODO: handle failure to AWS login
            this.awsProvider = null;
        } catch (InterruptedException e) {
            Log.d("yakyak", "Login interrupted: " + e);
            this.awsProvider = null;
        }

        setContentView(R.layout.card_list);

        ArrayList<Yak> yaks = new ArrayList<Yak>();
        Resources res = getResources();

        // TODO: populate these from a server
        yaks.add(new Yak("Quack like a duck", 12, R.color.yellow));
        yaks.add(new Yak("Best grandma impression", 39, R.color.purple));
        yaks.add(new Yak("Sing the alphabet backwards", 13, R.color.blue));
        yaks.add(new Yak("Lowest note you can sing", 44, R.color.red));

        CardListAdapter yakItemArrayAdapter= new CardListAdapter(this, yaks);
        setListAdapter(yakItemArrayAdapter);
    }

    public String getTempFileName () {
        String mFileDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        String mFileName = mFileDirectory + "audiorecordtest.3gp";
        return mFileName;
    }

    public String getTempFileDirectory () {
        String mFileDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        return mFileDirectory;
    }

    public CognitoCredentialsProvider getAWSProvider() {
        return awsProvider;
    }
}
