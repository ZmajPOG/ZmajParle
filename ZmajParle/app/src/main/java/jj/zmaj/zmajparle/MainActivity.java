package jj.zmaj.zmajparle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private PhraseDatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private PhraseAdapter adapter;
    private List<Phrase> phraseList;
    private Button addPhraseBtn, buildSentenceBtn;
    private EditText tagSearchEdit;
    private String lastTagSearch = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new PhraseDatabaseHelper(this);

        recyclerView = findViewById(R.id.phrasesRecyclerView);
        addPhraseBtn = findViewById(R.id.addPhraseBtn);
        buildSentenceBtn = findViewById(R.id.buildSentenceBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        phraseList = dbHelper.getAllPhrases();
        adapter = new PhraseAdapter(this, phraseList, new PhraseAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(Phrase phrase) {
                dbHelper.updateFavorite(phrase.getId(), !phrase.isFavorite());
                loadPhrases(); // Refresh after favorite toggle
            }
        });
        recyclerView.setAdapter(adapter);

        adapter.setOnItemLongClickListener(new PhraseAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final Phrase phrase) {
                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete phrase?")
                        .setMessage("Are you sure you want to delete:\n\n" + phrase.getText())
                        .setPositiveButton("Delete", (dialog, which) -> {
                            dbHelper.deletePhrase(phrase.getId());
                            loadPhrases();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });


        addPhraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddPhraseActivity.class);
                startActivity(i);
            }
        });

        buildSentenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BuildSentenceActivity.class);
                startActivity(i);
            }
        });

        tagSearchEdit = findViewById(R.id.tagSearchEdit);

        tagSearchEdit.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastTagSearch = s.toString().trim();
                loadPhrases();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPhrases();
    }

    private void loadPhrases() {
        if (lastTagSearch.isEmpty()) {
            phraseList = dbHelper.getAllPhrases();
        } else {
            phraseList = dbHelper.getPhrasesByTag(lastTagSearch);
        }
        adapter.setPhraseList(phraseList);
    }

}
