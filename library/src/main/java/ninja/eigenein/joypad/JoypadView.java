package ninja.eigenein.joypad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

/**
 * Simulates a joypad.
 */
public class JoypadView extends View {

    private final Paint outerPaint = new Paint();
    private final Paint innerPaint = new Paint();
    private final Paint moveablePaint = new Paint();

    private float outerWidth;
    private float innerRadius;
    private float moveableRadius;

    public JoypadView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JoypadView, 0, 0);
        final Resources resources = context.getResources();

        setupPaint(outerPaint, Paint.Style.STROKE, array.getColor(
                R.styleable.JoypadView_outerColor,
                resources.getColor(R.color.joypad_grey_50)
        ));
        setupPaint(innerPaint, Paint.Style.FILL, array.getColor(
                R.styleable.JoypadView_innerColor,
                resources.getColor(R.color.joypad_grey_500)
        ));
        setupPaint(moveablePaint, Paint.Style.FILL, array.getColor(
                R.styleable.JoypadView_moveableColor,
                resources.getColor(R.color.joypad_grey_900)
        ));

        outerPaint.setStrokeWidth(array.getDimensionPixelSize(
                R.styleable.JoypadView_outerWidth,
                resources.getDimensionPixelSize(R.dimen.joypad_outer_width)
        ));
        innerRadius = array.getDimensionPixelSize(
                R.styleable.JoypadView_innerRadius,
                resources.getDimensionPixelSize(R.dimen.joypad_inner_radius)
        );
        moveableRadius = array.getDimensionPixelSize(
                R.styleable.JoypadView_moveableRadius,
                resources.getDimensionPixelSize(R.dimen.joypad_moveable_radius)
        );
    }

    protected void onDraw(@NonNull final Canvas canvas) {
        super.onDraw(canvas);

        final float width = getWidth();
        final float height = getHeight();

        // Draw outer arc.
        final float outerOffset = outerPaint.getStrokeWidth() / 2.0f;
        @SuppressLint("DrawAllocation") final RectF outerRect = new RectF(
                outerOffset, outerOffset, width - outerOffset, height - outerOffset);
        canvas.drawArc(outerRect, 0.0f, 360.0f, false, outerPaint);

        // Draw inner circle.
        canvas.drawCircle(width / 2.0f, height / 2.0f, innerRadius, innerPaint);
    }

    private static void setupPaint(final Paint paint, final Paint.Style style, final int color) {
        paint.setAntiAlias(true);
        paint.setStyle(style);
        paint.setColor(color);
    }
}
