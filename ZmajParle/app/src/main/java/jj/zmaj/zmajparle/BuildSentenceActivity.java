package jj.zmaj.zmajparle;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.widget.EditText;



public class BuildSentenceActivity extends AppCompatActivity {

    private Spinner subjectSpinner, verbSpinner, objectSpinner, modifierSpinner;
    private Button buildBtn, playAudioBtn;
    private TextView resultText;
    private PhraseDatabaseHelper dbHelper;
    private TextToSpeech tts;

    private String sentence = "";
    private EditText searchSubjectEdit, searchVerbEdit, searchObjectEdit, searchModifierEdit;
    private TextView translationText;
    private Handler handler = new Handler(Looper.getMainLooper());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_sentence);

        searchSubjectEdit = findViewById(R.id.searchSubjectEdit);
        searchVerbEdit = findViewById(R.id.searchVerbEdit);
        searchObjectEdit = findViewById(R.id.searchObjectEdit);
        searchModifierEdit = findViewById(R.id.searchModifierEdit);

        searchSubjectEdit.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSpinner(subjectSpinner, dbHelper.getPhrasesByTypeAndTag("Subject", s.toString().trim()));
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        searchVerbEdit.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSpinner(verbSpinner, dbHelper.getPhrasesByTypeAndTag("Verb", s.toString().trim()));
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        searchObjectEdit.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSpinner(objectSpinner, dbHelper.getPhrasesByTypeAndTag("Object", s.toString().trim()));
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        searchModifierEdit.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSpinner(modifierSpinner, dbHelper.getPhrasesByTypeAndTag("Modifier", s.toString().trim()));
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });


        subjectSpinner = findViewById(R.id.subjectSpinner);
        verbSpinner = findViewById(R.id.verbSpinner);
        objectSpinner = findViewById(R.id.objectSpinner);
        modifierSpinner = findViewById(R.id.modifierSpinner);
        buildBtn = findViewById(R.id.buildBtn);
        playAudioBtn = findViewById(R.id.playAudioBtn);
        resultText = findViewById(R.id.resultText);

        translationText = findViewById(R.id.translationText);


        dbHelper = new PhraseDatabaseHelper(this);

        setupSpinners();

        buildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = (String) subjectSpinner.getSelectedItem();
                String verb = (String) verbSpinner.getSelectedItem();
                String object = (String) objectSpinner.getSelectedItem();
                String modifier = (String) modifierSpinner.getSelectedItem();

                StringBuilder sb = new StringBuilder();
                if (!subject.equals("-")) sb.append(subject).append(" ");
                if (!verb.equals("-")) sb.append(verb).append(" ");
                if (!object.equals("-")) sb.append(object).append(" ");
                if (!modifier.equals("-")) sb.append(modifier);

                sentence = sb.toString().replaceAll("\\s+", " ").trim();
                resultText.setText(sentence.isEmpty() ? "—" : sentence);
                if (!sentence.isEmpty()) {
                    translationText.setText("Translating...");
                    translateSentence(sentence);
                } else {
                    translationText.setText("");
                }

            }
        });


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.FRENCH);
                }
            }
        });

        playAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sentence.isEmpty()) {
                    tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
    }

    private void setupSpinners() {
        setSpinner(subjectSpinner, dbHelper.getPhrasesByTypeAndTag("Subject", ""));
        setSpinner(verbSpinner, dbHelper.getPhrasesByTypeAndTag("Verb", ""));
        setSpinner(objectSpinner, dbHelper.getPhrasesByTypeAndTag("Object", ""));
        setSpinner(modifierSpinner, dbHelper.getPhrasesByTypeAndTag("Modifier", ""));

    }

    private void setSpinner(Spinner spinner, List<Phrase> phraseList) {
        List<String> items = new ArrayList<>();
        items.add("-"); // for "none"
        for (Phrase p : phraseList) {
            items.add(p.getText());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void translateSentence(final String frenchSentence) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://libretranslate.com/translate");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoOutput(true);
                    String data = "q=" + java.net.URLEncoder.encode(frenchSentence, "UTF-8") +
                            "&source=fr&target=en";
                    OutputStream os = conn.getOutputStream();
                    os.write(data.getBytes());
                    os.flush();
                    os.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    in.close();

                    // Extract the translation from the JSON result (very simple way)
                    String json = sb.toString();
                    String result = "";
                    int i = json.indexOf("\"translatedText\":\"");
                    if (i != -1) {
                        int start = i + 18;
                        int end = json.indexOf("\"", start);
                        if (end > start) {
                            result = json.substring(start, end);
                        }
                    }
                    final String translation = result.isEmpty() ? "[No translation]" : result;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            translationText.setText(translation);
                        }
                    });
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            translationText.setText("[Translation error]");
                        }
                    });
                }
            }
        }).start();
    }

}
