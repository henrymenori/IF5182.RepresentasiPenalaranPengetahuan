package if5282.peta.util;

import java.nio.ByteBuffer;

public class Tree {

    // ==== Constant ====

    private static final int INITIAL_SIZE = 250;
    private static final int NULL_INDEX = 0;

    // ==== Property ====

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
        return getByteValueInt(0, 1);
    }

    private void setExpn(int expn) {
        setByteValueInt(0, 1, expn);
    }

    private int getSize() {
        return getByteValueInt(1, getExpn());
    }

    private void setSize(int size) {
        setByteValueInt(1, getExpn(), size);
    }

    private int getCurr() {
        int expn = getExpn();
        return getByteValueInt(1 + expn, expn);
    }

    private void setCurr(int curr) {
        int expn = getExpn();
        setByteValueInt(1 + expn, expn, curr);
    }

    private int getRoot() {
        int expn = getExpn();
        return getByteValueInt(1 + 2 * expn, expn);
    }

    private void setRoot(int root) {
        int expn = getExpn();
        setByteValueInt(1 + 2 * expn, expn, root);
    }

    private double getValueX(int n_idx) {
        return getByteValueDouble(n_idx);
    }

    private void setValueX(int n_idx, double value) {
        setByteValueDouble(n_idx, value);
    }

    private double getValueY(int n_idx) {
        return getByteValueDouble(n_idx + 8);
    }

    private void setValueY(int n_idx, double value) {
        setByteValueDouble(n_idx + 8, value);
    }

    private int getBranchCount(int n_idx) {
        return getByteValueInt(n_idx + 16, 1);
    }

    private void setBranchCount(int n_idx, int value) {
        setByteValueInt(n_idx + 16, 1, value);
    }

    private int getBranchIndex(int n_idx, int order) {
        int expn = getExpn();
        return getByteValueInt(n_idx + 17 + 2 * order * expn, expn);
    }

    private void setBranchIndex(int n_idx, int order, int b_idx) {
        int expn = getExpn();
        setByteValueInt(n_idx + 17 + 2 * order * expn, expn, b_idx);
    }

    private int getBranchName(int n_idx, int order) {
        int expn = getExpn();
        return getByteValueInt(n_idx + 17 + (2 * order + 1) * expn, expn);
    }

    private void setBranchName(int n_idx, int order, int value) {
        int expn = getExpn();
        setByteValueInt(n_idx + 17 + (2 * order + 1) * expn, expn, value);
    }

    // ==== Basic Method ====

    private int getByteValueInt(int offset, int len) {
        int value = 0;

        for (int i = 0; i < len; i++) {
            value += (data[offset + i] & 0xff) << (i * 8);
        }

        return value;
    }

    private void setByteValueInt(int offset, int len, int value) {
        for (int i = 0; i < len; i++) {
            data[offset + i] = (byte) (value << ((3 - i) * 8) >> 24);
        }
    }

    private double getByteValueDouble(int offset) {
        byte[] bytes = new byte[8];

        System.arraycopy(data, offset, bytes, 0, 8);

        return ByteBuffer.wrap(bytes).getDouble();
    }

    private void setByteValueDouble(int offset, double value) {
        byte[] bytes = new byte[8];

        ByteBuffer.wrap(bytes).putDouble(value);
        System.arraycopy(bytes, 0, data, offset, 8);
    }

    private int getHeaderSize(int expn) {
        return 1 + 3 * expn;
    }

    private int getBlockSize(int expn) {
        return 17 + 10 * expn;
    }

    private int getMaxIndex(int expn) {
        return (0xffffffff >>> ((4 - expn) * 8));
    }

    private int getScaledIndex(int n_idx, int expn_a, int expn_b) {
        return (n_idx - getHeaderSize(expn_a)) / getBlockSize(expn_a) * getBlockSize(expn_b) + getHeaderSize(expn_b);
    }

    // ==== Helper Method ====

    private boolean isEmpty() {
        return getRoot() == NULL_INDEX;
    }

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

            int b_idx;
            int i = getHeaderSize(expn_a);
            int j = getHeaderSize(expn_b);
            int bsize_a = getBlockSize(expn_a);
            int bsize_b = getBlockSize(expn_b);

            for (; i < curr_a; i += bsize_a) {
                t.setValueX(j, getValueX(i));
                t.setValueY(j, getValueY(i));

                for (int k = 0; k < 5; k++) {
                    b_idx = getBranchIndex(i, k);
                    t.setBranchIndex(j, k, b_idx == NULL_INDEX ? NULL_INDEX : getScaledIndex(b_idx, expn_a, expn_b));
                }

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

    private int addNode(double valX, double valY, int branch_count, int[] names, int[] idxs) {
        int n_idx = getCurr();
        int expn = getExpn();
        int header_size = getHeaderSize(expn);
        int block_size = getBlockSize(expn);

        setValueX(n_idx, valX);
        setValueY(n_idx, valY);
        setBranchCount(n_idx, branch_count);

        for (int i = 0; i < names.length; i++) {
            setBranchName(n_idx, i, names[i] - 1);
            setBranchIndex(n_idx, i, header_size + (idxs[i] - 1) * block_size);
        }

        setCurr(n_idx + block_size);

        return n_idx;
    }

    // ==== Main Method ====

    public boolean insert(double valX, double valY, int branch_count, int[] names, int[] idxs) {
        int t_idx;

        if (isEmpty()) {
            t_idx = addNode(valX, valY, branch_count, names, idxs);
            setRoot(t_idx);

            return true;
        } else {
            int size = getSize();
            int expn = getExpn();

            while (getCurr() + getBlockSize(expn) > size) {
                size *= 2;
            }

            if (size > getSize()) {
                if (size > getMaxIndex(expn)) {
                    rescale(expn + 1);
                }

                resize(size);
            }

            addNode(valX, valY, branch_count, names, idxs);

            return true;
        }
    }

    public StringBuilder printMemory(String[] names) {
        StringBuilder sb = new StringBuilder();
        int expn = getExpn();

        sb.append(String.format(" expn : %05d\r\n", getExpn()));
        sb.append(String.format(" size : %05d\r\n", getSize()));
        sb.append(String.format(" curr : %05d\r\n", getCurr()));
        sb.append(String.format(" root : %05d\r\n", getRoot()));
        sb.append("\r\n");

        for (int i = getHeaderSize(expn); i < getCurr(); i += getBlockSize(expn)) {
            sb.append(String.format(" %05d | %.6f | %.6f | %d\r\n", i, getValueX(i), getValueY(i), getBranchCount(i)));

            for (int j = 0; j < getBranchCount(i); j++) {
                sb.append(String.format("\t%05d - %s\r\n", getBranchIndex(i, j), names[getBranchName(i, j)]));
            }
        }

        return sb;
    }
}
