package if5282.kamus.util;

public class Stemmer {

    public static StringBuilder getType(String s) {

        StringBuilder sb = new StringBuilder();

        if ((s.startsWith("per") || s.startsWith("peng") || s.startsWith("ke")) && s.endsWith("an")) {
            sb.append("n");
        } else if (s.startsWith("ber") && s.endsWith("an")) {
            sb.append("v");
        } else if (s.startsWith("meng") || s.startsWith("men") || s.startsWith("mem") || s.startsWith("meny") || s.startsWith("me")) {
            sb.append("v");
        } else if (s.startsWith("peng") || s.startsWith("pen") || s.startsWith("pem") || s.startsWith("peny") || s.startsWith("pe")) {
            sb.append("n");
        } else if (s.startsWith("ber") || s.startsWith("be") || s.startsWith("per") || s.startsWith("di") || s.startsWith("ter")) {
            sb.append("v");
        } else if (s.startsWith("se")) {
            sb.append("n");
        } else if (s.startsWith("ke")) {
            sb.append("v");
        } else if (s.endsWith("an") || s.endsWith("in") || s.endsWith("wan") || s.endsWith("wati")) {
            sb.append("n");
        } else if (s.endsWith("if") || s.endsWith("ik") || s.endsWith("is") || s.endsWith("er") || s.endsWith("wi")) {
            sb.append("adj");
        }

        return sb;
    }

}
