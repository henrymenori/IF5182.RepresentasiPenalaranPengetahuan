package if5282.kamus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Tree {

    // ==== Constant ====

    private static final int INITIAL_SIZE = 250;
    private static final int VALUE_SIZE = 2;
    private static final int NULL_INDEX = 0;
    private static final char STOP_CHAR = '$';
    private static final String DIRECTORY = "/storage/emulated/0/Documents";

    // ==== Properties ====

    private byte[] data;

    // ==== Constructor ====

    public Tree() {
        data = new byte[INITIAL_SIZE];

        setExpn(1);
        setSize(INITIAL_SIZE);
        setCurr(4);
        setRoot(NULL_INDEX);
    }

    public Tree(int expn, int size) {
        data = new byte[size];

        setExpn(expn);
        setSize(size);
        setCurr(1 + 3 * expn);
        setRoot(NULL_INDEX);
    }

    // ==== Getter & Setter ====

    private int getExpn() {
        return getByteValue(0, 1);
    }

    private void setExpn(int expn) {
        setByteValue(0, 1, expn);
    }

    private int getSize() {
        return getByteValue(1, getExpn());
    }

    private void setSize(int size) {
        setByteValue(1, getExpn(), size);
    }

    private int getCurr() {
        int expn = getExpn();
        return getByteValue(1 + expn, expn);
    }

    private void setCurr(int curr) {
        int expn = getExpn();
        setByteValue(1 + expn, expn, curr);
    }

    private int getRoot() {
        int expn = getExpn();
        return getByteValue(1 + 2 * expn, expn);
    }

    private void setRoot(int root) {
        int expn = getExpn();
        setByteValue(1 + 2 * expn, expn, root);
    }

    public int getValue(int n_idx) {
        return getByteValue(n_idx, VALUE_SIZE);
    }

    private void setValue(int n_idx, int value) {
        setByteValue(n_idx, VALUE_SIZE, value);
    }

    private int getLeft(int n_idx) {
        return n_idx == NULL_INDEX ? getRoot() : getByteValue(n_idx + VALUE_SIZE, getExpn());
    }

    private void setLeft(int n_idx, int l_idx) {
        if (n_idx == NULL_INDEX)
            setRoot(l_idx);
        else
            setByteValue(n_idx + VALUE_SIZE, getExpn(), l_idx);
    }

    private int getRight(int n_idx) {
        int expn = getExpn();
        return n_idx == NULL_INDEX ? getRoot() : getByteValue(n_idx + VALUE_SIZE + expn, expn);
    }

    private void setRight(int n_idx, int r_idx) {
        int expn = getExpn();

        if (n_idx == NULL_INDEX)
            setRoot(r_idx);
        else
            setByteValue(n_idx + VALUE_SIZE + expn, expn, r_idx);
    }

    // ==== Basic Method ====

    private boolean isEmpty() {
        return getRoot() == NULL_INDEX;
    }

    private int getByteValue(int offset, int len) {
        int value = 0;

        for (int i = 0; i < len; i++) {
            value += (data[offset + i] & 0xff) << (i * 8);
        }

        return value;
    }

    private void setByteValue(int offset, int len, int value) {
        for (int i = 0; i < len; i++) {
            data[offset + i] = (byte) (value << ((3 - i) * 8) >> 24);
        }
    }

    private int getHeaderSize(int expn) {
        return 1 + 3 * expn;
    }

    private int getBlockSize(int expn) {
        return VALUE_SIZE + 2 * expn;
    }

    private int getMaxIndex(int expn) {
        return (0xffffffff >>> ((4 - expn) * 8));
    }

    private int getScaledIndex(int n_idx, int expn_a, int expn_b) {
        return (n_idx - getHeaderSize(expn_a)) / getBlockSize(expn_a) * getBlockSize(expn_b) + getHeaderSize(expn_b);
    }

    private int addNode(int value) {
        int n_idx = getCurr();

        setValue(n_idx, value);
        setLeft(n_idx, NULL_INDEX);
        setRight(n_idx, NULL_INDEX);
        setCurr(n_idx + getBlockSize(getExpn()));

        return n_idx;
    }

    private int addWord(String s, int s_idx, int d_idx) {
        int n_idx = addNode(s_idx < s.length() ? s.charAt(s_idx) : STOP_CHAR);
        int l_idx = s_idx < s.length() ? addWord(s, s_idx + 1, d_idx) : addNode(d_idx);

        setLeft(n_idx, l_idx);

        return n_idx;
    }

    // ==== Helper Method ====

    private boolean rescale(int expn_b) {
        int expn_a = getExpn();
        int curr_a = getCurr();
        int curr_b = getScaledIndex(curr_a, expn_a, expn_b);

        if (curr_b > getMaxIndex(expn_b)) {
            return false;
        } else {
            Tree t = new Tree(expn_b, curr_b);
            t.setCurr(curr_b);
            t.setRoot(getScaledIndex(getRoot(), expn_a, expn_b));

            int l_idx, r_idx;
            int i = getHeaderSize(expn_a);
            int j = getHeaderSize(expn_b);
            int bsize_a = getBlockSize(expn_a);
            int bsize_b = getBlockSize(expn_b);

            for (; i < curr_a; i += bsize_a) {
                t.setValue(j, getValue(i));

                l_idx = getLeft(i);
                t.setLeft(j, l_idx == NULL_INDEX ? NULL_INDEX : getScaledIndex(l_idx, expn_a, expn_b));

                r_idx = getRight(i);
                t.setRight(j, r_idx == NULL_INDEX ? NULL_INDEX : getScaledIndex(r_idx, expn_a, expn_b));

                j += bsize_b;
            }

            this.data = t.data;

            return true;
        }
    }

    private boolean resize(int size) {
        if (size < getCurr() || size > getMaxIndex(getExpn())) {
            return false;
        } else {
            byte[] t = new byte[size];
            System.arraycopy(data, 0, t, 0, getCurr());

            data = t;
            setSize(size);

            return true;
        }
    }

    private boolean insertHelper(String s, int s_idx, int p_idx, int n_idx, int d_idx) {
        char c = (char) getValue(n_idx);
        int l_idx, r_idx, t_idx, v_idx;

        if (s_idx < s.length()) {
            if (s.charAt(s_idx) < c) {
                t_idx = addWord(s, s_idx, d_idx);

                if (getLeft(p_idx) == n_idx)
                    setLeft(p_idx, t_idx);
                else
                    setRight(p_idx, t_idx);

                setRight(t_idx, n_idx);

                return true;
            } else if (s.charAt(s_idx) > c) {
                r_idx = getRight(n_idx);

                if (r_idx == NULL_INDEX) {
                    t_idx = addWord(s, s_idx, d_idx);
                    setRight(n_idx, t_idx);

                    return true;
                } else {
                    return insertHelper(s, s_idx, n_idx, r_idx, d_idx);
                }
            } else {
                l_idx = getLeft(n_idx);

                if (l_idx == NULL_INDEX) {
                    t_idx = addWord(s, s_idx + 1, d_idx);
                    setLeft(n_idx, t_idx);

                    return true;
                } else {
                    return insertHelper(s, s_idx + 1, n_idx, l_idx, d_idx);
                }
            }
        } else {
            if (c == STOP_CHAR) {
                return false;
            } else {
                t_idx = addNode(STOP_CHAR);
                setLeft(p_idx, t_idx);
                setRight(t_idx, n_idx);

                v_idx = addNode(d_idx);
                setLeft(t_idx, v_idx);

                return true;
            }
        }
    }

    private boolean insertLinkHelper(int p_idx, int n_idx, int d_idx) {
        int value = getValue(n_idx);
        int r_idx, t_idx;

        if (d_idx < value) {
            t_idx = addNode(d_idx);
            setRight(p_idx, t_idx);
            setRight(t_idx, n_idx);

            return true;
        } else if (d_idx > value) {
            r_idx = getRight(n_idx);

            if (r_idx == NULL_INDEX) {
                t_idx = addNode(d_idx);
                setRight(n_idx, t_idx);

                return true;
            } else {
                return insertLinkHelper(n_idx, r_idx, d_idx);
            }
        } else {
            return false;
        }
    }

    private int searchHelper(String s, int s_idx, int n_idx) {
        char c = (char) getValue(n_idx);
        int l_idx, r_idx;

        if (s_idx < s.length()) {
            if (s.charAt(s_idx) < c) {
                return NULL_INDEX;
            } else if (s.charAt(s_idx) > c) {
                r_idx = getRight(n_idx);
                return r_idx == NULL_INDEX ? NULL_INDEX : searchHelper(s, s_idx, r_idx);
            } else {
                l_idx = getLeft(n_idx);
                return l_idx == NULL_INDEX ? NULL_INDEX : searchHelper(s, s_idx + 1, l_idx);
            }
        } else {
            return c == STOP_CHAR ? getLeft(n_idx) : NULL_INDEX;
        }
    }

    private boolean searchLinkHelper(int n_idx, int d_idx) {
        int value = getValue(n_idx);
        int r_idx;

        if (d_idx < value) {
            return false;
        } else if (d_idx > value) {
            r_idx = getRight(n_idx);
            return r_idx != NULL_INDEX && searchLinkHelper(r_idx, d_idx);
        } else {
            return true;
        }
    }

    // ==== Main Function ====

    public boolean insert(String s, int d_idx) {
        int t_idx;

        if (isEmpty()) {
            t_idx = addWord(s, 0, d_idx);
            setRoot(t_idx);

            return true;
        } else {
            int size = getSize();
            int expn = getExpn();

            while (getCurr() + getBlockSize(expn) * (s.length() + 2) > size) {
                size *= 2;
            }

            if (size > getSize()) {
                if (size > getMaxIndex(expn)) {
                    rescale(expn + 1);
                }

                resize(size);
            }

            return insertHelper(s, 0, NULL_INDEX, getRoot(), d_idx);
        }
    }

    public boolean insertLink(String sa, String sb) {
        int n_idx = search(sa);
        int d_idx = search(sb);
        int r_idx, t_idx;

        if (n_idx == NULL_INDEX || d_idx == NULL_INDEX) {
            return false;
        } else {
            d_idx = getValue(d_idx);
            r_idx = getRight(n_idx);

            if (r_idx == NULL_INDEX) {
                t_idx = addNode(d_idx);
                setRight(n_idx, t_idx);

                return true;
            } else {
                return insertLinkHelper(n_idx, r_idx, d_idx);
            }
        }
    }

    public boolean insertType(String s, int type) {
        int n_idx = search(s);
        int l_idx;

        if (n_idx == NULL_INDEX) {
            return false;
        } else {
            l_idx = getLeft(n_idx);

            if (l_idx == NULL_INDEX) {
                l_idx = addNode(type);
                setLeft(n_idx, l_idx);

                return true;
            } else {
                return false;
            }
        }
    }

    public int search(String s) {
        return isEmpty() ? 0 : searchHelper(s, 0, getRoot());
    }

    public boolean searchLink(String sa, String sb) {
        int n_idx = search(sa);
        int d_idx = search(sb);
        int r_idx;

        if (n_idx == NULL_INDEX || d_idx == NULL_INDEX) {
            return false;
        } else {
            d_idx = getValue(d_idx);
            r_idx = getRight(n_idx);

            return r_idx != NULL_INDEX && searchLinkHelper(r_idx, d_idx);
        }
    }

    public int searchType(String s){
        int n_idx = search(s);
        int l_idx;

        if (n_idx == NULL_INDEX) {
            return 0;
        } else {
            l_idx = getLeft(n_idx);

            if (l_idx == NULL_INDEX) {
                return 0;
            } else {
                return getValue(l_idx);
            }
        }
    }

    public void load(String filename) {
        Tree t;
        File file = new File(DIRECTORY, filename);
        int expn, size;

        try {
            FileInputStream in = new FileInputStream(file);
            in.read(data, 0, 1);
            expn = getExpn();

            in.read(data, 1, expn * 3);
            size = getSize();

            t = new Tree(expn, size);
            t.setCurr(getCurr());
            t.setRoot(getRoot());
            in.read(t.data, 1 + getExpn() * 3, getCurr() - (1 + getExpn() * 3));
            data = t.data;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(String filename) {
        File file = new File(DIRECTORY, filename);

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(data, 0, getCurr());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder printMemory() {
        int l_idx, r_idx;
        StringBuilder sb = new StringBuilder();

        sb.append(String.format(" expn : %05d\r\n", getExpn()));
        sb.append(String.format(" size : %05d\r\n", getSize()));
        sb.append(String.format(" curr : %05d\r\n", getCurr()));
        sb.append(String.format(" root : %05d\r\n", getRoot()));
        sb.append("\r\n");
        sb.append(" index | v |   v   |   l   |   r   |\r\n");
        sb.append(" ------+---+-------+-------+-------+\r\n");

        for (int i = 1 + 3 * getExpn(); i < getCurr(); i += getBlockSize(getExpn())) {
            sb.append(String.format(" %05d | %c | %05d |", i, getValue(i), getValue(i)));

            l_idx = getLeft(i);
            sb.append(l_idx == NULL_INDEX ? "       |" : String.format(" %05d |", l_idx));

            r_idx = getRight(i);
            sb.append(r_idx == NULL_INDEX ? "       |" : String.format(" %05d |", r_idx));

            sb.append("\r\n");
        }

        return sb;
    }
}
