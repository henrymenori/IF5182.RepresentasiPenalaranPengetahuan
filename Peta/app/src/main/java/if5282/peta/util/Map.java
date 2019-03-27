package if5282.peta.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Map {

    // ==== Constant ====

    private static final String DIRECTORY = "/storage/emulated/0/Documents";

    // ==== Properties ====

    public ArrayList<String> names;
    public Tree tree;

    // ==== Constructor ====

    public Map() {
        tree = new Tree(4, 100000);
        names = new ArrayList<>();
    }

    // ==== Main Function ====

    public void build(String intersection_filename, String link_filename) {
        File intr_file = new File(DIRECTORY, intersection_filename);
        File link_file = new File(DIRECTORY, link_filename);
        String line;
        String[] values;
        int idx;

        try {
            BufferedReader br = new BufferedReader(new FileReader(intr_file));

            while ((line = br.readLine()) != null) {
                values = line.split(" ");

                tree.insertIntersection(Double.parseDouble(values[1]), Double.parseDouble(values[0]));
            }

            br.close();

            br = new BufferedReader(new FileReader(link_file));

            while ((line = br.readLine()) != null) {
                values = line.split(" ");

                if (!names.contains(values[2])) {
                    names.add(values[2]);
                }

                idx = names.indexOf(values[2]);

                tree.insertLink(Integer.parseInt(values[0]), Integer.parseInt(values[1]), false, idx);
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void build(String raw_filename) {
        File raw_file = new File(DIRECTORY, raw_filename);
        String line;
        String[] values;
        int count, name, n_idx = 0, t_idx;

        try {
            BufferedReader br = new BufferedReader(new FileReader(raw_file));

            while ((line = br.readLine()) != null) {
                values = line.split(",");

                tree.insertIntersection(Double.parseDouble(values[1]), Double.parseDouble(values[0]));
            }

            br.close();

            br = new BufferedReader(new FileReader(raw_file));

            while ((line = br.readLine()) != null) {
                values = line.split(",");
                count = Integer.parseInt(values[2]);

                for (int i = 0; i < count; i++) {
                    t_idx = Integer.parseInt(values[i * 2 + 3]);

                    if (t_idx > n_idx /*&& t_idx <= 62*/) {
                        if (!names.contains(values[i * 2 + 4])) {
                            names.add(values[i * 2 + 4]);
                        }

                        name = names.indexOf(values[i * 2 + 4]);
                        tree.insertLink(n_idx, t_idx, false, name);
                    }
                }

                n_idx++;
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
