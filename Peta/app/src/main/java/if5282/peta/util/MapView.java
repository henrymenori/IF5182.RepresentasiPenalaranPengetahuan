package if5282.peta.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MapView extends View {

    // ==== Property ====

    private Paint paint;
    private Tree tree;
    private int width;
    private int height;

    // ==== Constructor ====

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
    }

    // ==== Getter & Setter ====

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    // ==== Override Method ====

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        tree.drawMap(canvas, width, height, paint);
    }
}
