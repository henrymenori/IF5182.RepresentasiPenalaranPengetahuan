package if5282.kamus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Dict {

    // ==== Constant ====

    private static final String DIRECTORY = "/storage/emulated/0/Documents";

    // ==== Properties ====

    public Tree[] trees;
    private String[][] table;

    // ==== Constructor ====

    public Dict() {
        trees = new Tree[4];

        for (int i = 0; i < 4; i++) {
            trees[i] = new Tree();
        }
    }

    // ==== Helper Method ====

    private int getTypeIndex(String s) {
        switch (s) {
            case "n":
                return 1;
            case "v":
                return 2;
            case "adv":
                return 3;
            case "adj":
                return 4;
            case "k":
                return 5;
            case "pre":
                return 6;
            case "pron":
                return 7;
            default:
                return 8;
        }
    }

    private String getTypeString(int idx) {
        switch (idx) {
            case 1:
                return "n";
            case 2:
                return "v";
            case 3:
                return "adv";
            case 4:
                return "adj";
            case 5:
                return "konj";
            case 6:
                return "pre";
            case 7:
                return "pron";
            default:
                return "unk";
        }
    }

    // ==== Main Function ====

    public void build(String dict_filename, String link_filename) {
        File dict_file = new File(DIRECTORY, dict_filename);
        File link_file = new File(DIRECTORY, link_filename);
        int word_count;
        String line, key, value;
        String[] words;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dict_file));

            word_count = Integer.parseInt(br.readLine());
            table = new String[word_count][4];

            for (int i = 0; i < word_count; i++) {
                line = br.readLine();

                if (line != null) {
                    words = line.split(",");

                    for (int j = 0; j < 4; j++) {
                        table[i][j] = words[j].toLowerCase().trim();
                        trees[j].insert(table[i][j], i + 1);
                    }

                    trees[0].insertType(table[i][0], getTypeIndex(words[4]));
                }
            }

            br.close();

            br = new BufferedReader(new FileReader(link_file));

            while ((line = br.readLine()) != null) {
                words = line.split(",");
                key = words[0].toLowerCase().trim();

                for (int i = 1; i < words.length; i++) {
                    value = words[i].toLowerCase().trim();
                    trees[0].insertLink(key, value);
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(String dict_filename) {
        File dict_file = new File(DIRECTORY, dict_filename);
        int word_count;
        String line;
        String[] words;

        trees[0].load("indonesia.txt");
        trees[1].load("jawa.txt");
        trees[2].load("sunda.txt");
        trees[3].load("padang.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(dict_file));

            word_count = Integer.parseInt(br.readLine());
            table = new String[word_count][4];

            for (int i = 0; i < word_count; i++) {
                line = br.readLine();

                if (line != null) {
                    words = line.split(",");

                    for (int j = 0; j < 4; j++) {
                        table[i][j] = words[j].toLowerCase().trim();
                    }
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        trees[0].save("indonesia.txt");
        trees[1].save("jawa.txt");
        trees[2].save("sunda.txt");
        trees[3].save("padang.txt");
    }

    public boolean validate(String s) {
        String[] words = s.split(" ");
        int[] sequence = new int[words.length];
        boolean valid = true;

        for (int i = 0; i < sequence.length - 1; i++) {
            boolean res = trees[0].searchLink(words[i], words[i + 1]);
            valid = valid && res;
        }

        return valid;
    }

    public StringBuilder validate2(String s) {
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        Parser parser = new Parser();

        for (int i = 0; i < words.length; i++) {
            sb.append(getTypeString(trees[0].searchType(words[i])));
            sb.append(' ');
        }

        try {
            sb.append('\n');
            parser.parse(sb.toString().trim());
            sb.append("valid");
        } catch (Exception e) {
            e.printStackTrace();
            sb.append("invalid");
        }

        return sb;
    }
}
