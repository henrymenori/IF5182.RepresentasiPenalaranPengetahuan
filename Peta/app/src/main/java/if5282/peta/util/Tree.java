package if5282.peta.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Stack;

public class Tree {

    // ==== Constant ====

    private static final int INITIAL_SIZE = 250;
    private static final int MAX_BRANCH = 5;
    private static final int MAX_EXPN = 4;
    private static final int NULL_INDEX = 0;

    // ==== Property ====

    private byte[] data;
    private double xmin;
    private double xmax;
    private double ymin;
    private double ymax;
    private Stack<Integer> path;
    private Stack<Integer> stop;
    private int s_idx;
    private int d_idx;

    // ==== Constructor ====

    public Tree() {
        data = new byte[INITIAL_SIZE];

        setExpn(1);
        setSize(INITIAL_SIZE);
        setCurr(4);
        setRoot(NULL_INDEX);

        path = new Stack<>();
        stop = new Stack<>();
        xmin = xmax = ymin = ymax = 0;
    }

    public Tree(int expn, int size) {
        data = new byte[size];

        setExpn(expn);
        setSize(size);
        setCurr(1 + 3 * expn);
        setRoot(NULL_INDEX);

        path = new Stack<>();
        stop = new Stack<>();
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
        if (expn_b > MAX_EXPN) {
            return false;
        }

        int expn_a = getExpn();
        int curr_a = getCurr();
        int curr_b = getScaledIndex(curr_a, expn_a, expn_b);

        if (curr_b > getMaxIndex(expn_b)) {
            return false;
        }

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

    private void addNode(double valX, double valY) {
        int n_idx = getCurr();

        setValueX(n_idx, valX);
        setValueY(n_idx, valY);
        setBranchCount(n_idx, 0);
        setCurr(n_idx + getBlockSize(getExpn()));

        if (valX < xmin || xmin == 0) xmin = valX;
        if (valX > xmax || xmax == 0) xmax = valX;
        if (valY < ymin || ymin == 0) ymin = valY;
        if (valY > ymax || ymax == 0) ymax = valY;
    }

    // ==== Main Method ====

    public boolean insertIntersection(double valX, double valY) {
        int expn = getExpn();

        if (isEmpty()) {
            addNode(valX, valY);
            setRoot(getHeaderSize(expn));

            return true;
        } else {
            int size = getSize();

            if (getCurr() + getBlockSize(expn) > size) {
                size *= 2;

                if (size > getMaxIndex(expn) && !rescale(expn + 1)) {
                    return false;
                }

                if (!resize(size)) {
                    return false;
                }
            }

            addNode(valX, valY);

            return true;
        }
    }

    public boolean insertLink(int a, int b, boolean directed, int name) {
        int expn = getExpn();
        int header_size = getHeaderSize(expn);
        int block_size = getBlockSize(expn);
        int a_idx = header_size + a * block_size;
        int b_idx = header_size + b * block_size;
        int a_count = getBranchCount(a_idx);
        int b_count = getBranchCount(b_idx);

        if (!(a_count < MAX_BRANCH)) {
            return false;
        }

        setBranchIndex(a_idx, a_count, b_idx);
        setBranchName(a_idx, a_count, name);
        setBranchCount(a_idx, a_count + 1);

        if (!directed) {
            if (!(b_count < MAX_BRANCH)) {
                return false;
            }

            setBranchIndex(b_idx, b_count, a_idx);
            setBranchName(b_idx, b_count, name);
            setBranchCount(b_idx, b_count + 1);
        }

        return true;
    }

    // ==== Print Method ====

    public StringBuilder printMemory(ArrayList<String> names) {
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
                sb.append(String.format("\t%05d - %s\r\n", getBranchIndex(i, j), names.get(getBranchName(i, j))));
            }
        }

        return sb;
    }

    // ==== Navigation Method ====

    public void initNavigation(int src, int dst) {
        path.clear();
        stop.clear();

        s_idx = getHeaderSize(getExpn()) + src * getBlockSize(getExpn());
        d_idx = getHeaderSize(getExpn()) + dst * getBlockSize(getExpn());

        path.push(s_idx);
    }

    public int navigateStep() {
        double distance = Double.MAX_VALUE;
        int n_idx = path.peek();
        int m_idx = NULL_INDEX;
        int t_idx;

        if (n_idx != d_idx) {
            for (int i = 0; i < getBranchCount(n_idx); i++) {
                t_idx = getBranchIndex(n_idx, i);

                if (!path.contains(t_idx) && !stop.contains(t_idx) && distance > getDistance(t_idx, d_idx)) {
                    distance = getDistance(t_idx, d_idx);
                    m_idx = t_idx;
                }
            }

            if (m_idx == NULL_INDEX) {
                if (n_idx == s_idx) {
                    return -1;
                } else {
                    stop.push(n_idx);
                    path.pop();
                    return 0;
                }
            } else {
                path.push(m_idx);
                return 0;
            }
        } else {
            return 1;
        }
    }

    // ==== Drawing Method ====

    public void drawMap(Canvas canvas, int width, int height, Paint paint) {
        double w_ratio = (double) width / (xmax - xmin) * 0.9;
        double h_ratio = (double) height / (ymax - ymin) * 0.9;
        double w_offset = width * 0.05;
        double h_offset = height * 0.05;
        float ax, ay, bx, by;
        int count = 0;

        for (int i = getHeaderSize(getExpn()); i < getCurr(); i += getBlockSize(getExpn())) {
            ax = (float) ((getValueX(i) - xmin) * w_ratio + w_offset);
            ay = (float) (height - (getValueY(i) - ymin) * h_ratio - h_offset);

            for (int j = 0; j < getBranchCount(i); j++) {
                bx = (float) ((getValueX(getBranchIndex(i, j)) - xmin) * w_ratio + w_offset);
                by = (float) (height - (getValueY(getBranchIndex(i, j)) - ymin) * h_ratio - h_offset);

                canvas.drawLine(ax, ay, bx, by, paint);
            }

            if (getBranchCount(i) > 0) {
                canvas.drawCircle(ax, ay, 5, paint);
                canvas.drawText("" + count, ax, ay - 10, paint);
            }

            count++;
        }
    }

    public void drawPath(Canvas canvas, int width, int height, Paint paint) {
        double w_ratio = (double) width / (xmax - xmin) * 0.9;
        double h_ratio = (double) height / (ymax - ymin) * 0.9;
        double w_offset = width * 0.05;
        double h_offset = height * 0.05;
        float ax, ay, bx, by;

        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(3);

        for (int i = 0; i < path.size() - 1; i++) {
            ax = (float) ((getValueX(path.elementAt(i)) - xmin) * w_ratio + w_offset);
            ay = (float) (height - (getValueY(path.elementAt(i)) - ymin) * h_ratio - h_offset);
            bx = (float) ((getValueX(path.elementAt(i + 1)) - xmin) * w_ratio + w_offset);
            by = (float) (height - (getValueY(path.elementAt(i + 1)) - ymin) * h_ratio - h_offset);

            canvas.drawLine(ax, ay, bx, by, paint);
        }
    }

    // ==== Other Function ====

    private double getDistance(int a_idx, int b_idx) {
        double ax = getValueX(a_idx);
        double ay = getValueY(a_idx);
        double bx = getValueX(b_idx);
        double by = getValueY(b_idx);

        return Math.sqrt(Math.pow(ax - bx, 2) + Math.pow(ay - by, 2));
    }
}
