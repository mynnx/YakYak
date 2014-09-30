package mynnx.yakyak;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.amazonaws.android.auth.CognitoCredentialsProvider;

/**
 * Created by mynnx on 7/24/14.
 */
public class AmazonLoginAsync extends AsyncTask<Void, Void, CognitoCredentialsProvider> {

    private Context context;

    public AmazonLoginAsync(Context context) {
        this.context = context;
    }

    @Override
    protected CognitoCredentialsProvider doInBackground(Void... args) {
        CognitoCredentialsProvider cognitoProvider = new CognitoCredentialsProvider(
                this.context, // get the context for the current activity
                "",
                "",
                "",
                ""
        );
        cognitoProvider.getIdentityId();
        return cognitoProvider;
    }

    @Override
    protected void onPostExecute(CognitoCredentialsProvider provider) {
        Toast.makeText(this.context, "Logged into AWS!", Toast.LENGTH_SHORT).show();
    }
}
