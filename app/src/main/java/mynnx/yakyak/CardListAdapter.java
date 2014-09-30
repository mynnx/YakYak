package mynnx.yakyak;

import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.android.auth.CognitoCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mynnx.yakyak.models.Yak;

public class CardListAdapter extends ArrayAdapter<Yak> {
    private LandingActivity parentActivity;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private boolean isRecording;
    private boolean isPlaying;
    private String mFileName;


    public CardListAdapter(LandingActivity activity, List<Yak> items){
        super(activity, R.layout.yak_card_view, items);
        parentActivity = activity;
        mFileName = parentActivity.getTempFileName();
        isRecording = false;    // TODO figure out if globals are the best way to do this
        isPlaying = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = parentActivity.getWindow().getLayoutInflater();
        View view = inflater.inflate(R.layout.yak_card_view, parent, false);

        Yak yak = getItem(position);

        // Set the prompt text
        TextView header = (TextView) view.findViewById(R.id.prompt);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Rambla-Regular.ttf");
        header.setTypeface(font);
        header.setText(yak.getPrompt());

        // Set the Yak's background
        view.setBackgroundColor(getContext().getResources().getColor(yak.getColor()));

        // Set up event listeners for play and pause
        // TODO these should be tied to individual Yaks; each should operate in isolation
        ImageButton recordButton = (ImageButton) view.findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    stopRecording(view);
                } else {
                    startRecording(view);
                }
            }
        });

        ImageButton playButton = (ImageButton) view.findViewById(R.id.playButton);
        playButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mFileName = parentActivity.getTempFileName();
                if (isPlaying) {
                    stopPlaying(view);
                } else {
                    startPlaying(view);
                }
            }
        });

        return view;
    }

    private void startRecording(View view) {
        mediaRecorder = new MediaRecorder();

        // Set the audio format and encoder
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // Setup the output location
        // TODO can we record into a buffer instead of a temporary file?
        mediaRecorder.setOutputFile(mFileName);

        // Start the recording
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Toast.makeText(parentActivity, "Playback failed.", Toast.LENGTH_LONG);
        }
        mediaRecorder.start();
        isRecording = true;
    }

    private void stopRecording(View view) {
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();

        CognitoCredentialsProvider awsProvider = parentActivity.getAWSProvider();
        AmazonUploadAsync uploadTask = new AmazonUploadAsync(awsProvider);
        File toUpload = new File(mFileName);
        uploadTask.execute(toUpload);
        isRecording = false;
    }

    private void stopPlaying(View view) {
        mediaPlayer.stop();
        isPlaying = false;
    }

    private void startPlaying(View view) {
        AmazonDownloadAsync downloadTask = new AmazonDownloadAsync(parentActivity.getAWSProvider());
        downloadTask.execute();

        String urlToPlay = "";
        try {
            urlToPlay = downloadTask.get();
        } catch (ExecutionException e) {
            Log.d("yakyak", "Downloading failed: " + e);
        } catch (InterruptedException e) {
            Log.d("yakyak", "Download was interrupted: " + e);
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                mp.release();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d("yakyak", "Playing media failed because: " + what + ", " + extra);
                isPlaying = false;
                return false;
            }
        });

        try {
            mediaPlayer.setDataSource(urlToPlay);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.d("yakyak", "Playing media failed because" + e.getMessage());
        }
    }
}
