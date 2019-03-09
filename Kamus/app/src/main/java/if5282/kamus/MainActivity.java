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
import android.widget.EditText;
import android.widget.TextView;

import if5282.kamus.util.Dict;
import if5282.kamus.util.Parser;
import if5282.kamus.util.Tree;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private TextView textView, debug;
    private Dict dict;
    private StringBuilder sb;

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

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sb = new StringBuilder();
                sb.append(dict.validate(s.toString()) ? "valid" : "invalid");
                sb.append('\n');
                sb.append(dict.validate2(s.toString()));
                textView.setText(sb);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dict = new Dict();
        dict.load("dict4.txt");
    }

    public void build(View view) {
        dict.build("dict4.txt", "link2.txt");
        dict.save();
    }
}
