package if5282.kamus.util;

public class Parser {

    // ==== Properties ====

    public String[] tokens;
    public int curr;

    // ==== Non Terminal Function ====

    private void accept(String token) throws Exception {
        if (!token.equals(tokens[curr]))
            throw new Exception("invalid grammar");
        curr++;
    }

    private void kalimat() throws Exception {
        s();
        p();
        if (curr < tokens.length)
            kalimat_a();

        if (curr < tokens.length)
            throw new Exception("invalid grammar");
    }

    private void kalimat_a() throws Exception {
        switch (tokens[curr]) {
            case "adj":
                accept("adj");
                break;
            case "konj":
                accept("konj");
                s();
                p();
                o();
                break;
            case "pre":
                k();
                if (curr < tokens.length)
                    kalimat_c();
                break;
            case "v":
                p();
                if (curr < tokens.length)
                    kalimat_d();
                break;
            case "pron":
                accept("pron");
                p();
                if (curr < tokens.length)
                    k();
                break;
            default:
                accept("n");
                if (curr < tokens.length)
                    kalimat_b();
                break;
        }
    }

    private void kalimat_b() throws Exception {
        switch (tokens[curr]) {
            case "adj":
                accept("adj");
                break;
            case "pre":
                k();
                break;
            case "konj":
                accept("konj");
                s();
                p();
                if (curr < tokens.length)
                    o();
                break;
            case "pron":
                accept("pron");
                p();
                o();
                break;
            case "n":
                accept("n");
                if (curr < tokens.length)
                    kalimat_f();
                break;
            default:
                p();
                if (curr < tokens.length)
                    k();
                break;
        }
    }

    private void kalimat_c() throws Exception {
        accept("konj");
        if (curr < tokens.length)
            kalimat_e();
    }

    private void kalimat_d() throws Exception {
        switch (tokens[curr]) {
            case "adj":
                accept("adj");
                break;
            default:
                accept("n");
                if (curr < tokens.length)
                    k();
                break;
        }
    }

    private void kalimat_e() throws Exception {
        switch (tokens[curr]) {
            case "n":
                s();
                p();
                if (curr < tokens.length)
                    k();
                break;
            default:
                p();
                pel();
                break;
        }
    }

    private void kalimat_f() throws Exception {
        p();
        o();
    }

    private void s() throws Exception {
        switch (tokens[curr]) {
            case "n":
                accept("n");
                break;
            default:
                accept("pron");
                break;
        }
    }

    private void p() throws Exception {
        accept("v");
        if (curr < tokens.length && tokens[curr].equals("adv"))
            accept("adv");
    }

    private void o() throws Exception {
        accept("n");
    }

    private void k() throws Exception {
        accept("pre");
        accept("n");
    }

    private void pel() throws Exception {
        switch (tokens[curr]) {
            case "n":
                accept("n");
                break;
            default:
                accept("adj");
                break;
        }
    }

    public void parse(String s) throws Exception {
        tokens = s.split(" ");
        curr = 0;
        kalimat();
    }
}
