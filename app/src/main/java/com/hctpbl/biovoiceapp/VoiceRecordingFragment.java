package com.hctpbl.biovoiceapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hctpbl.biovoiceapp.api.APIErrorDialog;
import com.hctpbl.biovoiceapp.api.BioVoiceAPI;
import com.hctpbl.biovoiceapp.api.CountingTypedFile;
import com.hctpbl.biovoiceapp.api.ProgressListener;
import com.hctpbl.biovoiceapp.api.model.VoiceAccessResponse;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import at.markushi.ui.CircleButton;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

public class VoiceRecordingFragment extends Fragment implements VoiceAccessResponseReceiver {

    private static final String TAG = "VoiceRecordingFragment";

    private static final String DIALOG_ERROR = "error_dialog_voice_recording";
    private static final String DIALOG_ENROLLMENT = "enrollment_dialog_voice_recording";

    public static final String EXTRA_VOICEREC_USERNAME = "com.hctpbl.biovoiceapp.voicerec_username";
    public static final String EXTRA_VOICEREC_RESULT = "com.hctpbl.biovoiceapp.voicerec_result";
    public static final String EXTRA_VOICEREC_THRESHOLD = "com.hctpbl.biovoiceapp.voicerec_threshold";

    private static final String CONTENT_TYPE = "audio/x-m4a";

    private boolean mEnrolled;
    private String mUsername;

    private TextView mVoiceRecTitleTextView;
    private TextView mVoiceRecTextTextView;
    private CircleButton mVoiceRecRecordButton;
    private Chronometer mVoiceRecChronometer;
    private ProgressBar mProgressBar;
    private TextView mUploadProgressTextView;
    private Button mVoiceRecEnroll;
    private Button mVoiceRecVerify;
    private LinearLayout mButtonsBottom;

    private String mFileName;
    private MediaRecorder mRecorder;
    private boolean mCanRecord = true;

    private PowerManager.WakeLock mWakeLock;

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

        setRetainInstance(true);

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
        mProgressBar = (ProgressBar)v.findViewById(R.id.voicerec_progress_bar);
        mUploadProgressTextView = (TextView)v.findViewById(R.id.voicerec_upload_progress_text_view);

        mVoiceRecEnroll = (Button)v.findViewById(R.id.voicerec_enroll_button);
        mVoiceRecEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                mUploadProgressTextView.setVisibility(View.VISIBLE);
                mButtonsBottom.setVisibility(View.GONE);
                VoiceAccessResponseReceiver fragment = (VoiceAccessResponseReceiver)
                        getFragmentManager().findFragmentById(R.id.container_voice_recognition);
                new EnrollUser(fragment).execute();
            }
        });

        mVoiceRecVerify = (Button)v.findViewById(R.id.voicerec_verify_button);
        mVoiceRecVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                mUploadProgressTextView.setVisibility(View.VISIBLE);
                mButtonsBottom.setVisibility(View.GONE);
                VoiceAccessResponseReceiver fragment = (VoiceAccessResponseReceiver)
                        getFragmentManager().findFragmentById(R.id.container_voice_recognition);
                new VerifyUser(fragment).execute();
            }
        });

        mButtonsBottom = (LinearLayout)v.findViewById(R.id.voicerec_buttons_bottom);

        PowerManager powerManager = (PowerManager)getActivity().getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

        return v;
    }

    private void showResultsPage(float threshold, float result) {
        VoiceResultsFragment fragment = VoiceResultsFragment.newInstance(mUsername, threshold, result);

        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container_voice_recognition, fragment)
                .commit();
    }

    private void showEnrolledUserLayout() {
        mVoiceRecTextTextView.setText(R.string.voicerec_text_enrolled);
        mButtonsBottom.setVisibility(View.GONE);
        mEnrolled = true;
        mVoiceRecChronometer.setBase(SystemClock.elapsedRealtime());
        new EnrollmentSuccessDialog().show(getFragmentManager(), DIALOG_ENROLLMENT);
    }

    @Override
    public void onResume() {
        super.onResume();
        mWakeLock.acquire();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWakeLock.release();
        recordStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recordStop();
    }

    private void removeAudioFile() {
        File audioFile = new File(mFileName);
        audioFile.delete();
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
        mButtonsBottom.setVisibility(View.VISIBLE);
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
        mVoiceRecEnroll.setVisibility(View.VISIBLE);
        if (mEnrolled) {
            mVoiceRecEnroll.setText(R.string.voicerec_reenroll);
            mVoiceRecVerify.setVisibility(View.VISIBLE);
        }
    }

    private class EnrollUser extends AsyncTask<Void, Integer, VoiceAccessResponse> {

        VoiceAccessResponseReceiver mResponseReceiver;
        long mTotalSize;

        public EnrollUser(VoiceAccessResponseReceiver receiver) {
            this.mResponseReceiver = receiver;
        }

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
                mTotalSize = rawAudioFile.length();
                ProgressListener listener = new ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) mTotalSize) * 100));
                    }
                };
                CountingTypedFile typedAudioFile = new CountingTypedFile(CONTENT_TYPE, rawAudioFile, listener);
                response = api.enrollUser(mUsername, typedAudioFile);
                return response;
            } catch (RetrofitError re) {
                if (re.isNetworkError()) {
                    //mErrorMessageId = APIErrorDialog.ERROR_IO;
                } else if (re.getResponse().getStatus() == 400) {
                    response = (VoiceAccessResponse) re.getBodyAs(VoiceAccessResponse.class);
                    return response;
                }
                re.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(TAG, "Uploaded: " + String.valueOf(values[0]) + "%");
            setUploadProgress(values[0]);
        }

        @Override
        protected void onPostExecute(VoiceAccessResponse voiceAccessResponse) {
            mResponseReceiver.getVoiceAccessResponse(voiceAccessResponse);
        }
    }

    private class VerifyUser extends AsyncTask<Void, Integer, VoiceAccessResponse> {

        VoiceAccessResponseReceiver mResponseReceiver;
        long mTotalSize;

        public VerifyUser(VoiceAccessResponseReceiver receiver) {
            this.mResponseReceiver = receiver;
        }

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
                mTotalSize = rawAudioFile.length();
                ProgressListener listener = new ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) mTotalSize) * 100));
                    }
                };
                CountingTypedFile typedAudioFile = new CountingTypedFile(CONTENT_TYPE, rawAudioFile, listener);
                response = api.testUser(mUsername, typedAudioFile);
                return response;
            } catch (RetrofitError re) {
                if (re.isNetworkError()) {
                    //mErrorMessageId = APIErrorDialog.ERROR_IO;
                } else if (re.getResponse().getStatus() == 400) {
                    response = (VoiceAccessResponse) re.getBodyAs(VoiceAccessResponse.class);
                    return response;
                }
                re.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(TAG, "Uploaded: " + String.valueOf(values[0]) + "%");
            setUploadProgress(values[0]);
        }

        @Override
        protected void onPostExecute(VoiceAccessResponse voiceAccessResponse) {
            mResponseReceiver.getVoiceAccessResponse(voiceAccessResponse);
        }
    }

    private void setUploadProgress(int progress) {
        mUploadProgressTextView.setText(String.format(
                        getResources().getString(R.string.voicerec_upload_progress),
                        String.valueOf(progress)));
        mProgressBar.setProgress(progress);
    }

    public void getVoiceAccessResponse(VoiceAccessResponse response) {
        mProgressBar.setVisibility(View.GONE);
        mUploadProgressTextView.setVisibility(View.GONE);
        if (!response.isError()) {
            if (response.getAction().equals(VoiceAccessResponse.ACTION_ENROLL)) {
                showEnrolledUserLayout();
            } else {
                showResultsPage(response.getThreshold(), response.getResult());
            }
        } else {
            Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "Threshold: " + String.valueOf(response.getThreshold()));
        Log.d(TAG, "Result: " + String.valueOf(response.getResult()));
        removeAudioFile();
    }
}
