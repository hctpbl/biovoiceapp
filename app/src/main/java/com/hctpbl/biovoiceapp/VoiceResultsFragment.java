package com.hctpbl.biovoiceapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;

import java.util.ArrayList;

/**
 * Shows the result of a user verification request
 */
public class VoiceResultsFragment extends Fragment {

    private String mUsername;
    private float mThreshold;
    private float mResult;

    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private TextView mTextTextView;
    private BarGraph mResultsChart;
    private Button mGoHomeButton;
    private Button mGoEnrollButton;

    /**
     * Creates an instance of this fragment with all necessary data
     * @param username The username of the verified user
     * @param threshold General threshold of the system
     * @param result Value of the verification
     * @return
     */
    public static VoiceResultsFragment newInstance(String username, float threshold, float result) {
        Bundle args = new Bundle();
        args.putString(VoiceRecordingFragment.EXTRA_VOICEREC_USERNAME, username);
        args.putFloat(VoiceRecordingFragment.EXTRA_VOICEREC_THRESHOLD, threshold);
        args.putFloat(VoiceRecordingFragment.EXTRA_VOICEREC_RESULT, result);
        VoiceResultsFragment fragment = new VoiceResultsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_voice_results, container, false);

        mUsername = getArguments().getString(VoiceRecordingFragment.EXTRA_VOICEREC_USERNAME);
        mThreshold = getArguments().getFloat(VoiceRecordingFragment.EXTRA_VOICEREC_THRESHOLD);
        mResult = getArguments().getFloat(VoiceRecordingFragment.EXTRA_VOICEREC_RESULT);

        mTitleTextView = (TextView)v.findViewById(R.id.resverif_title);
        mTitleTextView.setText(String.format(getResources().getString(R.string.resverif_title), mUsername));

        mSubtitleTextView = (TextView)v.findViewById(R.id.resverif_subtitle);
        mTextTextView = (TextView)v.findViewById(R.id.resverif_text);

        if (mResult >= mThreshold) {
            mSubtitleTextView.setText(R.string.resverif_subtitle_positive);
            mSubtitleTextView.setTextColor(Color.GREEN);
            mTextTextView.setText(R.string.resverif_text_positive);
        }

        mResultsChart = (BarGraph)v.findViewById(R.id.resverif_results_chart);
        setResultsChartValues();

        mGoHomeButton = (Button)v.findViewById(R.id.resverif_gohome_button);
        mGoHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToMain();
            }
        });

        mGoEnrollButton = (Button)v.findViewById(R.id.resverif_govoicerec_button);
        mGoEnrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        return v;
    }

    /**
     * Go back to main screen
     */
    private void goBackToMain() {
        Intent i = new Intent(getActivity(), BioVoiceAppActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    /**
     * Sets the values and colors for the chars
     */
    private void setResultsChartValues() {

        Bar thresholdBar = new Bar();
        thresholdBar.setColor(Color.BLUE);
        thresholdBar.setName(getResources().getString(R.string.voicerec_threshold));
        thresholdBar.setValue(mThreshold);

        Bar resultsBar = new Bar();
        int resultsBarColor;
        if (mResult >= mThreshold) {
            resultsBarColor = Color.GREEN;
        } else {
            resultsBarColor = Color.RED;
        }
        resultsBar.setColor(resultsBarColor);
        resultsBar.setName(getResources().getString(R.string.voicerec_result));
        resultsBar.setValue(mResult);

        ArrayList<Bar> points = new ArrayList<Bar>();
        points.add(thresholdBar);
        points.add(resultsBar);
        mResultsChart.setBars(points);
    }
}
