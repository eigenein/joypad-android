package ninja.eigenein.joypad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

/**
 * Simulates a joypad.
 */
public class JoypadView extends View {

    private static final float SIN_30 = (float)Math.sin(Math.PI / 6.0);
    private static final float PI = (float)Math.PI;
    private static final float[] DIRECTIONS = {
            0f, PI / 4f, 2f * PI / 4f, 3f * PI / 4f, PI, 5f * PI / 4f, 6f * PI / 4f, 7f * PI / 4f};

    private final Paint outerPaint = new Paint();
    private final Paint innerPaint = new Paint();
    private final Paint moveablePaint = new Paint();
    private final Paint directionsPaint = new Paint();

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
        setupPaint(directionsPaint, Paint.Style.FILL, array.getColor(
                R.styleable.JoypadView_directionsColor,
                resources.getColor(R.color.joypad_grey_300)
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
        final float outerWidth = outerPaint.getStrokeWidth();

        // Draw outer arc.
        final float outerOffset = outerWidth / 2f;
        @SuppressLint("DrawAllocation") final RectF outerRect = new RectF(
                outerOffset, outerOffset, width - outerOffset, height - outerOffset);
        canvas.drawArc(outerRect, 0.0f, 360f, false, outerPaint);

        final float centerX = width / 2f;
        final float centerY = height / 2f;

        // Draw direction triangles.
        final float triangleHeight = outerWidth / 2f;
        final float offsetY = height / 2f - 3f * outerWidth / 4f;
        final float offsetX = SIN_30 * outerWidth / 2f;
        for (final float angle : DIRECTIONS) {
            // We'll rotate vertices of triangle.
            @SuppressLint("DrawAllocation") final Path path = new Path();
            final PointF startPoint = rotatePoint(0f, offsetY + triangleHeight, angle);
            path.moveTo(centerX + startPoint.x, centerY + startPoint.y);
            final PointF leftPoint = rotatePoint(-offsetX, offsetY, angle);
            path.lineTo(centerX + leftPoint.x, centerY + leftPoint.y);
            final PointF rightPoint = rotatePoint(+offsetX, offsetY, angle);
            path.lineTo(centerX + rightPoint.x, centerY + rightPoint.y);
            path.close();

            canvas.drawPath(path, directionsPaint);
        }

        // Draw inner circle.
        canvas.drawCircle(centerX, centerY, innerRadius, innerPaint);
    }

    private static void setupPaint(final Paint paint, final Paint.Style style, final int color) {
        paint.setAntiAlias(true);
        paint.setStyle(style);
        paint.setColor(color);
    }

    private static PointF rotatePoint(final float x, final float y, final double angle) {
        final float cos = (float)Math.cos(angle);
        final float sin = (float)Math.sin(angle);
        return new PointF(x * cos - y * sin, x * sin + y * cos);
    }
}
