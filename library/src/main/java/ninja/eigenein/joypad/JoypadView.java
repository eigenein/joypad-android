package ninja.eigenein.joypad;

import android.animation.ValueAnimator;
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
import android.view.MotionEvent;
import android.view.View;

/**
 * Simulates a joypad.
 */
public class JoypadView extends View {

    private static final float SIN_30 = (float)Math.sin(Math.PI / 6.0);

    /**
     * Pre-computed cos and sin for rotation.
     */
    private static final PointF[] ROTATION = {
            new PointF(1f, 0f),
            new PointF((float)Math.cos(0.25 * Math.PI), (float)Math.sin(0.25 * Math.PI)),
            new PointF(0f, 1f),
            new PointF((float)Math.cos(0.75 * Math.PI), (float)Math.sin(0.75 * Math.PI)),
            new PointF(-1f, 0f),
            new PointF((float)Math.cos(1.25 * Math.PI), (float)Math.sin(1.25 * Math.PI)),
            new PointF(0f, -1f),
            new PointF((float)Math.cos(1.75 * Math.PI), (float)Math.sin(1.75 * Math.PI)),
    };

    /**
     * Used to draw the outer circle.
     */
    private final Paint outerPaint = new Paint();
    /**
     * Used to draw the inner circle.
     */
    private final Paint innerPaint = new Paint();
    /**
     * Used to draw the moveable circle.
     */
    private final Paint moveablePaint = new Paint();
    /**
     * Used to draw the direction triangles.
     */
    private final Paint directionsPaint = new Paint();

    private float innerRadius;
    private float moveableRadius;

    /**
     * Minimum of the view width and height.
     */
    private float minSizeDimension;

    /**
     * Maximum allowed moveable distance.
     */
    private float maxDistance;

    private float moveableX;
    private float moveableY;

    private boolean animationCancelled = true;

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

    @Override
    public boolean onTouchEvent(@NonNull final MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                // Animate moving back to the center.
                final float oldMoveableX = moveableX;
                final float oldMoveableY = moveableY;
                final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(@NonNull final ValueAnimator animation) {
                        if (animationCancelled) {
                            animator.cancel();
                            return;
                        }
                        final float value = (float)animator.getAnimatedValue();
                        moveableX = (1f - value) * oldMoveableX + value * minSizeDimension / 2f;
                        moveableY = (1f - value) * oldMoveableY + value * minSizeDimension / 2f;
                        postInvalidate();
                    }
                });
                animationCancelled = false;
                animator.start();
                return true;

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // Cancel animation if any.
                animationCancelled = true;

                // Compute distance from the center.
                final float offsetX = event.getX() - minSizeDimension / 2f;
                final float offsetY = event.getY() - minSizeDimension / 2f;
                final float distance = (float)Math.sqrt(offsetX * offsetX + offsetY * offsetY);

                if (distance < maxDistance) {
                    // Touched inside the outer circle.
                    moveableX = event.getX();
                    moveableY = event.getY();
                } else {
                    // Touched outside the outer circle.
                    moveableX = minSizeDimension / 2f + maxDistance * offsetX / distance;
                    moveableY = minSizeDimension / 2f + maxDistance * offsetY / distance;
                }

                postInvalidate();
                return true;

            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldWidth, final int oldHeight) {
        minSizeDimension = Math.min(width, height);
        maxDistance = minSizeDimension / 2f - moveableRadius;
        moveableX = minSizeDimension / 2f;
        moveableY = minSizeDimension / 2f;
    }

    @Override
    protected void onDraw(@NonNull final Canvas canvas) {
        super.onDraw(canvas);

        final float outerWidth = outerPaint.getStrokeWidth();

        // Draw outer arc.
        final float outerOffset = outerWidth / 2f;
        @SuppressLint("DrawAllocation") final RectF outerRect = new RectF(
                outerOffset, outerOffset, minSizeDimension - outerOffset, minSizeDimension - outerOffset);
        canvas.drawArc(outerRect, 0.0f, 360f, false, outerPaint);

        final float center = minSizeDimension / 2f;

        // Draw direction triangles.
        final float triangleHeight = outerWidth / 2f;
        final float offsetY = minSizeDimension / 2f - 3f * outerWidth / 4f;
        final float offsetX = SIN_30 * outerWidth / 2f;
        for (final PointF rotation : ROTATION) {
            // We'll rotate vertices of triangle.
            @SuppressLint("DrawAllocation") final Path path = new Path();
            final PointF startPoint = rotatePoint(0f, offsetY + triangleHeight, rotation);
            path.moveTo(center + startPoint.x, center + startPoint.y);
            final PointF leftPoint = rotatePoint(-offsetX, offsetY, rotation);
            path.lineTo(center + leftPoint.x, center + leftPoint.y);
            final PointF rightPoint = rotatePoint(+offsetX, offsetY, rotation);
            path.lineTo(center + rightPoint.x, center + rightPoint.y);
            path.close();

            canvas.drawPath(path, directionsPaint);
        }

        // Draw inner circle.
        canvas.drawCircle(center, center, innerRadius, innerPaint);

        // Draw moveable.
        canvas.drawCircle(moveableX, moveableY, moveableRadius, moveablePaint);
    }

    private static void setupPaint(final Paint paint, final Paint.Style style, final int color) {
        paint.setAntiAlias(true);
        paint.setStyle(style);
        paint.setColor(color);
    }

    private static PointF rotatePoint(final float x, final float y, final PointF rotation) {
        return new PointF(x * rotation.x - y * rotation.y, x * rotation.y + y * rotation.x);
    }
}
