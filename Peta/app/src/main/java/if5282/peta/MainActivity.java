package if5282.peta;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import if5282.peta.util.Map;
import if5282.peta.util.MapView;
import if5282.peta.util.PathView;
import if5282.peta.util.Tree;

public class MainActivity extends AppCompatActivity {

    private Tree tree;
    private ArrayList<String> names;
    private PathView pathView;
    private EditText editTextSrc, editTextDst;
    private TextView debug;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        Map map = new Map();
        //map.build("intersection2.txt", "link.txt");
        map.build("raw2.txt");

        tree = map.tree;
        names = map.names;

        MapView mapView = findViewById(R.id.mapView);
        mapView.setTree(tree);

        pathView = findViewById(R.id.pathView);
        pathView.setTree(tree);

        editTextSrc = findViewById(R.id.editTextSrc);
        editTextDst = findViewById(R.id.editTextDst);
        debug = findViewById(R.id.debug);

        Button buttonInitialize = findViewById(R.id.buttonInitialize);
        buttonInitialize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tree.initNavigation(Integer.parseInt(editTextSrc.getText().toString()), Integer.parseInt(editTextDst.getText().toString()));
                state = 0;
                pathView.invalidate();
                printStatus();
            }
        });

        Button buttonStep = findViewById(R.id.buttonStep);
        buttonStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 0) {
                    state = tree.navigateStep();
                    pathView.invalidate();
                }
                printStatus();
            }
        });

        Button buttonNavigate = findViewById(R.id.buttonNavigate);
        buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while (state == 0) {
                    state = tree.navigateStep();
                }
                pathView.invalidate();
                printStatus();
            }
        });

        state = 0;
    }

    private void printStatus() {
        if (state == -1) {
            debug.setText("Path not found");
        } else if (state == 1) {
            debug.setText("Path found");
        } else {
            debug.setText("Searching...");
        }
    }
}
