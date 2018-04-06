package com.bestdealfinance.bdfpartner.dialog;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestdealfinance.bdfpartner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by rbq on 11/2/16.
 */

public class SpeechToTextDialog extends DialogFragment implements View.OnClickListener {

    private List<AutoCompleteTextView> etGroupList;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private EditText editTextAppFill, editTextSpeech;
    private int currentProcessingEditTextForSpeech = -1;
    private ArrayList<String> resultStringArrayList;
    private SpeechRecognizer recognizer;
    private Intent recognizeIntent;
    private ImageView speechMic;
    private TextView speechMicTextView, speechDialogTitle, noFieldForSpeech;
    private Button btnCancel, btnNext;
    private HashMap<String, String> etHashMapFieldUi;
    private HashMap<String, String> etListItemMap;
    private List<String> fieldUiId;

    public SpeechToTextDialog()
    {

    }

    public void setData(List<AutoCompleteTextView> etGroupList, List<String> fieldUiId) {
        this.etGroupList = etGroupList;
        this.fieldUiId = fieldUiId;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.dialog_speech_to_text, null);
        //getDialog().setTitle(getString(R.string.bank_information));
        setCancelable(false);

        initializeUI(inflaterView);
        initializeSpeechComponents();
        setTitleAndDataForSpeech();

        return inflaterView;
    }

    private void setTitleAndDataForSpeech() {

        editTextSpeech.setText("");
        currentProcessingEditTextForSpeech++;

        if (currentProcessingEditTextForSpeech >= etGroupList.size()) {
            dismiss();
            Toast.makeText(getActivity(), "No more fields left for speech", Toast.LENGTH_SHORT).show();
            return;
        }

        editTextAppFill = etGroupList.get(currentProcessingEditTextForSpeech);

        for (; currentProcessingEditTextForSpeech < etGroupList.size(); currentProcessingEditTextForSpeech++) {
            if (!checkIfEditTextIsValidForSpeech(currentProcessingEditTextForSpeech)) {
                editTextAppFill = etGroupList.get(currentProcessingEditTextForSpeech);
                speechDialogTitle.setText(editTextAppFill.getHint().toString());
                editTextSpeech.setText(editTextAppFill.getText());
                if (fieldUiId.get(currentProcessingEditTextForSpeech).equals("iwc")) {
                    editTextSpeech.setHint("In numbers");
                    editTextSpeech.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    editTextSpeech.setHint("In alphabets");
                    editTextSpeech.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                break;
            }
        }

        if (currentProcessingEditTextForSpeech >= etGroupList.size()) {
            noFieldForSpeech.setVisibility(View.VISIBLE);
            editTextSpeech.setVisibility(View.GONE);
            speechDialogTitle.setVisibility(View.GONE);
            speechMic.setVisibility(View.GONE);
            speechMicTextView.setVisibility(View.GONE);
            return;
        }
    }


    private boolean checkIfEditTextIsValidForSpeech(int currentProcessingEditTextForSpeech) {
        String fieldUi = fieldUiId.get(currentProcessingEditTextForSpeech);
        boolean nextItem = true;

        switch (fieldUi) {
            case "static-dd": {
                break;
            }

            case "iwc": {
                nextItem = false;
                break;
            }

            case "durationym": {
                break;
            }

            case "durationy": {
                break;
            }

            case "mobile": {
                nextItem = false;
                break;
            }

            case "email": {
                nextItem = false;
                break;
            }

            case "alpha": {
                nextItem = false;
                break;
            }
            case "numeric": {
                nextItem = false;
                break;
            }

            case "alphanum": {
                nextItem = false;
                break;
            }

            case "textbox": {
                nextItem = false;
                break;
            }
            case "pan": {
                nextItem = false;
                break;
            }

            case "dob": {
                break;
            }

            case "date-dmy": {
                break;
            }
            default: {
                nextItem = false;
                break;
            }
        }
        return nextItem;
    }

    private void initializeUI(View inflaterView) {
        speechMic = (ImageView) inflaterView.findViewById(R.id.speech_mic);
        btnCancel = (Button) inflaterView.findViewById(R.id.speech_btn_cancel);
        btnNext = (Button) inflaterView.findViewById(R.id.speech_btn_next);
        speechMicTextView = (TextView) inflaterView.findViewById(R.id.speech_mic_text);
        speechDialogTitle = (TextView) inflaterView.findViewById(R.id.speech_dialog_title);
        noFieldForSpeech = (TextView) inflaterView.findViewById(R.id.speech_no_field_for_speech);
        editTextSpeech = (EditText) inflaterView.findViewById(R.id.speech_edit_text);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (editTextSpeech.getText() != null && !editTextSpeech.getText().toString().isEmpty() && editTextAppFill.getText() != editTextSpeech.getText()) {
                    if (fieldUiId.get(currentProcessingEditTextForSpeech).equals("iwc")) {
                        if (isNumeric(editTextSpeech.getText().toString().trim())){
                            editTextAppFill.setText(editTextSpeech.getText().toString().trim());
                        }
                        else{
//                            adb connect 10.10.11.126:5556
                            editTextAppFill.setText("");
                            Toast.makeText(getActivity(), "Numeric Only", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        editTextAppFill.setText(editTextSpeech.getText());

                    }
                }

                setTitleAndDataForSpeech();
            }
        });

        speechMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizer.startListening(recognizeIntent);
            }
        });
    }

    private void initializeSpeechComponents() {

        recognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());

        recognizeIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,3000);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                viewChanger(false, "Listening...");
                speechMic.setImageResource(R.drawable.ic_close);
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
                viewChanger(true, "Loading...");
            }

            @Override
            public void onError(int error) {
                viewChanger(true, "Error Occurred.");
            }

            @Override
            public void onResults(Bundle results) {

                speechMic.setImageResource(R.drawable.ic_microphone);
                resultStringArrayList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (resultStringArrayList != null) {
                    String newValue = resultStringArrayList.get(0);
                    editTextAppFill.setText(newValue);
                    editTextSpeech.setText(newValue);
                    viewChanger(true, "Tap to speak again");
                } else {
                    viewChanger(true, "Couldn't recognize, Please try again.");
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        };

        //register the intent with the recognizer
        recognizer.setRecognitionListener(listener);

    }

    private void viewChanger(boolean isEnabled, String s) {
        speechMic.setEnabled(isEnabled);
        speechMicTextView.setText(s);

    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDisplayMetrics().widthPixels;
        getDialog().getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {

        if (currentProcessingEditTextForSpeech >= etGroupList.size()) {
            Toast.makeText(getActivity(), "Nopes..", Toast.LENGTH_SHORT).show();
            return;
        }

        editTextAppFill = etGroupList.get(currentProcessingEditTextForSpeech);

        //tts.speak(editTextAppFill.getHint(), TextToSpeech.QUEUE_ADD, null, "DEFAULT");


        try {
            //startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == getActivity().RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //txtSpeechInput.setText(result.get(0));
                    editTextAppFill.setText(result.get(currentProcessingEditTextForSpeech++));
                    /*for (int i = 0; i < etGroupList.size(); i++) {
                        editText = etGroupList.get(i);
                        String s = editText.getHint().toString();
                        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    }*/
                    /*if (currentProcessingEditTextForSpeech < etGroupList.size()) {
                        promptSpeechInput();
                    }*/

                }
                break;
            }

        }
    }
    public boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }
}
