package if5282.kamus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import if5282.kamus.util.Dict;
import if5282.kamus.util.Parser;
import if5282.kamus.util.Stemmer;
import if5282.kamus.util.Tree;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText editText;
    private TextView textView, debug;
    private Spinner spinner;
    private Dict dict;
    private StringBuilder sb;
    private int language;
    private String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        debug = findViewById(R.id.debug);
        spinner = findViewById(R.id.spinner);
        input = "";

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input = s.toString();
                sb = new StringBuilder();
                sb.append(dict.validate(input) ? "valid" : "invalid");
                sb.append('\n');
                sb.append(dict.validate2(input));
                sb.append('\n');
                sb.append(dict.translate(input, language));
                textView.setText(sb);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item
                , new String[]{"Indonesia", "Jawa", "Sunda", "Minang"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        dict = new Dict();
        dict.load("dict3.txt");
    }

    public void build(View view) {
        dict.build("dict4.txt", "link2.txt");
        dict.save();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        language = position;
        sb = new StringBuilder();
        sb.append(dict.validate(input) ? "valid" : "invalid");
        sb.append('\n');
        sb.append(dict.validate2(input));
        sb.append('\n');
        sb.append(dict.translate(input, language));
        textView.setText(sb);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
