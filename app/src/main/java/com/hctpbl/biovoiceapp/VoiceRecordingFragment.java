package com.hctpbl.biovoiceapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.hctpbl.biovoiceapp.api.APIErrorDialog;
import com.hctpbl.biovoiceapp.api.BioVoiceAPI;
import com.hctpbl.biovoiceapp.api.model.VoiceAccessResponse;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import at.markushi.ui.CircleButton;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

public class VoiceRecordingFragment extends Fragment{

    private static final String TAG = "VoiceRecordingFragment";

    private static final String DIALOG_ERROR = "error_dialog_voice_recording";

    private static final String CONTENT_TYPE = "audio/x-m4a";

    private boolean mEnrolled;
    private String mUsername;

    private TextView mVoiceRecTitleTextView;
    private TextView mVoiceRecTextTextView;
    private CircleButton mVoiceRecRecordButton;
    private Chronometer mVoiceRecChronometer;
    private Button mVoiceRecEnroll;
    private Button mVoiceRecVerify;

    private String mFileName;
    private MediaRecorder mRecorder;
    private boolean mCanRecord = true;

    public static VoiceRecordingFragment newInstance(String username, boolean enrolled) {
        Bundle args = new Bundle();
        args.putSerializable(VoiceAccessFragment.EXTRA_VOICEACC_USERNAME, username);
        args.putSerializable(VoiceAccessFragment.EXTRA_VOICEACC_ENROLLED, enrolled);
        VoiceRecordingFragment fragment = new VoiceRecordingFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_voice_recording,
                container, false);

        if (!checkExternalStorageAvailable()) {
            APIErrorDialog.getErrorDialog(APIErrorDialog.ERROR_NO_STORAGE).
                    show(getFragmentManager(), DIALOG_ERROR);
        }

        mUsername = getArguments().getString(VoiceAccessFragment.EXTRA_VOICEACC_USERNAME);
        mEnrolled = getArguments().getBoolean(VoiceAccessFragment.EXTRA_VOICEACC_ENROLLED);

        mFileName =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mUsername + ".m4a";
        Log.i(TAG, "Audio file name: " + mFileName);

        mVoiceRecTitleTextView = (TextView)v.findViewById(R.id.voicerec_title_text_view);
        mVoiceRecTitleTextView.setText(String.format(
                getResources().getString(R.string.voicerec_title), mUsername));

        mVoiceRecTextTextView = (TextView)v.findViewById(R.id.voicerec_text_text_view);
        if (mEnrolled) {
            mVoiceRecTextTextView.setText(R.string.voicerec_text_enrolled);
        }

        mVoiceRecRecordButton = (CircleButton)v.findViewById(R.id.voicerec_record_button);
        mVoiceRecRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record();
            }
        });

        mVoiceRecChronometer = (Chronometer)v.findViewById(R.id.voicerec_chronometer);

        mVoiceRecEnroll = (Button)v.findViewById(R.id.voicerec_enroll_button);
        mVoiceRecEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    VoiceAccessResponse response = new EnrollUser().execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        mVoiceRecVerify = (Button)v.findViewById(R.id.voicerec_verify_button);
        mVoiceRecVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    VoiceAccessResponse response = new VerifyUser().execute().get();
                    Log.i(TAG, String.valueOf(response.getResult()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        recordStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recordStop();
    }

    private void record() {
        if (mCanRecord) {
            recordStart();
        } else {
            recordStop();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        mRecorder.start();
    }

    private boolean checkExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }


    private void recordStart() {
        startRecording();
        Log.i(TAG, "Recording started");
        mCanRecord = false;
        mVoiceRecChronometer.setVisibility(View.VISIBLE);
        mVoiceRecChronometer.setBase(SystemClock.elapsedRealtime());
        mVoiceRecChronometer.start();
        mVoiceRecRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_microphone_muted));
        mVoiceRecRecordButton.setColor(Color.argb(200, 227, 69, 53));
        mVoiceRecEnroll.setVisibility(View.GONE);
        mVoiceRecVerify.setVisibility(View.GONE);
    }

    private void recordStop() {
        stopRecording();
        Log.i(TAG, "Recording stopped");
        mCanRecord = true;
        mVoiceRecChronometer.stop();
        mVoiceRecRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_microphone));
        mVoiceRecRecordButton.setColor(Color.argb(200, 150, 204, 158));
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        mVoiceRecEnroll.setVisibility(View.VISIBLE);
        if (mEnrolled) {
            mVoiceRecEnroll.setText(R.string.voicerec_reenroll);
            mVoiceRecVerify.setVisibility(View.VISIBLE);
        }
        Log.i(TAG, "Recording successful");
        /*if (mRecordingThread != null) {
            mRecordingThread.stopRecording();
            mRecordingThread = null;
        }*/
    }

    private class EnrollUser extends AsyncTask<Void, Void, VoiceAccessResponse> {

        @Override
        protected VoiceAccessResponse doInBackground(Void... params) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String apiURLstr = sp.getString(SettingsActivity.KEY_PREF_API_URL, "");
            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(apiURLstr)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            BioVoiceAPI api = adapter.create(BioVoiceAPI.class);
            VoiceAccessResponse response;
            try {
                File rawAudioFile = new File(mFileName);
                TypedFile typedAudioFile = new TypedFile(CONTENT_TYPE, rawAudioFile);
                response = api.enrollUser(mUsername, typedAudioFile);
                return response;
            } catch (RetrofitError re) {
                /*if (re.isNetworkError()) {
                    //mErrorMessageId = APIErrorDialog.ERROR_IO;
                } else if (re.getResponse().getStatus() == 400) {
                    response = (VoiceAccessResponse) re.getBodyAs(VoiceAccessResponse.class);
                    return response;
                }*/
                re.printStackTrace();
            }
            return null;
        }
    }

    private class VerifyUser extends AsyncTask<Void, Void, VoiceAccessResponse> {

        @Override
        protected VoiceAccessResponse doInBackground(Void... voids) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String apiURLstr = sp.getString(SettingsActivity.KEY_PREF_API_URL, "");
            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(apiURLstr)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            BioVoiceAPI api = adapter.create(BioVoiceAPI.class);
            VoiceAccessResponse response;
            try {
                File rawAudioFile = new File(mFileName);
                TypedFile typedAudioFile = new TypedFile(CONTENT_TYPE, rawAudioFile);
                response = api.testUser(mUsername, typedAudioFile);
                return response;
            } catch (RetrofitError re) {
                /*if (re.isNetworkError()) {
                    //mErrorMessageId = APIErrorDialog.ERROR_IO;
                } else if (re.getResponse().getStatus() == 400) {
                    response = (VoiceAccessResponse) re.getBodyAs(VoiceAccessResponse.class);
                    return response;
                }*/
                re.printStackTrace();
            }
            return null;
        }
    }
}
