package if5282.peta.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Dict {

    // ==== Constant ====

    private static final String DIRECTORY = "/storage/emulated/0/Documents";

    // ==== Properties ====

    private String[] names;
    public Tree map;

    // ==== Constructor ====

    public Dict() {
        map = new Tree(4, 1000);
    }

    // ==== Main Function ====

    public void build(String intersection_filename, String street_filename) {
        File intr_file = new File(DIRECTORY, intersection_filename);
        File strt_file = new File(DIRECTORY, street_filename);
        int intr_count, strt_count;
        String line;
        String[] values;

        double valX, valY;
        int branch_count;
        int[] names = new int[5];
        int[] idxs = new int[5];


        try {
            BufferedReader br = new BufferedReader(new FileReader(intr_file));

            intr_count = Integer.parseInt(br.readLine());

            for (int i = 0; i < intr_count; i++) {
                line = br.readLine();

                if (line != null) {
                    values = line.split(" ");

                    valX = Double.parseDouble(values[0]);
                    valY = Double.parseDouble(values[1]);
                    branch_count = (values.length - 2) / 2;

                    for (int j = 0; j < branch_count; j++) {
                        idxs[j] = Integer.parseInt(values[2 + j * 2]);
                        names[j] = Integer.parseInt(values[3 + j * 2]);
                    }

                    map.insert(valX, valY, branch_count, names, idxs);
                }
            }

            br.close();

            br = new BufferedReader(new FileReader(strt_file));

            strt_count = Integer.parseInt(br.readLine());
            this.names = new String[strt_count];

            for (int i = 0; i < strt_count; i++) {
                line = br.readLine();

                if (line != null) {
                    this.names[i] = line;
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder print() {
        return map.printMemory(names);
    }
}
