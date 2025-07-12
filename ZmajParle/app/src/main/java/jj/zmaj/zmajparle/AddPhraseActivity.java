package jj.zmaj.zmajparle;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddPhraseActivity extends AppCompatActivity {

    private EditText inputPhrase, tagEdit;
    private Spinner typeSpinner;
    private CheckBox favoriteCheck;
    private Button savePhraseBtn;
    private PhraseDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phrase);

        inputPhrase = findViewById(R.id.inputPhrase);
        tagEdit = findViewById(R.id.tagEdit);
        typeSpinner = findViewById(R.id.typeSpinner);
        favoriteCheck = findViewById(R.id.favoriteCheck);
        savePhraseBtn = findViewById(R.id.savePhraseBtn);

        dbHelper = new PhraseDatabaseHelper(this);

        savePhraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputPhrase.getText().toString().trim();
                String type = typeSpinner.getSelectedItem().toString();
                String tags = tagEdit.getText().toString().trim();
                boolean favorite = favoriteCheck.isChecked();

                if (text.isEmpty()) {
                    Toast.makeText(AddPhraseActivity.this, "Enter a phrase or word!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Phrase phrase = new Phrase(text, type, tags, favorite);
                dbHelper.addPhrase(phrase);
                Toast.makeText(AddPhraseActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                finish(); // Close and go back to main
            }
        });
    }
}
