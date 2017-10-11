package ninja.eigenein.joypad;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
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

    /**
     * Joypad listener.
     */
    public interface Listener {

        void onUp();

        /**
         * @param distance Distance from the center. Varies from 0 up to 1.
         * @param dx Horizontal offset. Varies from -1 up to 1.
         * @param dy Vertical offset. Varies from -1 up to 1.
         */
        void onMove(final float distance, final float dx, final float dy);
    }

    private static final float SIN_30 = 0.5f;

    /**
     * Pre-computed cos and sin for rotation.
     */
    private static final PointF[] ROTATION = {
            new PointF(+1f, +0f),
            new PointF(+0f, +1f),
            new PointF(-1f, +0f),
            new PointF(+0f, -1f),
            new PointF(+0.70710678f, +0.70710678f),
            new PointF(-0.70710678f, +0.70710678f),
            new PointF(-0.70710678f, -0.70710678f),
            new PointF(+0.70710678f, -0.70710678f),
    };

    /**
     * Used to draw the outer circle.
     */
    private final Paint outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * Used to draw the inner circle.
     */
    private final Paint innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * Used to draw the moveable circle.
     */
    private final Paint moveablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * Used to draw the direction triangles.
     */
    private final Paint directionsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final float innerRadius;
    private final float moveableRadius;

    private Listener listener;

    /**
     * Minimum of the view width and height.
     */
    private float minSizeDimension;
    private float centerPoint;

    /**
     * Maximum allowed moveable distance.
     */
    private float maxDistance;

    /**
     * Current moveable position.
     */
    private PointF moveablePoint;

    private ValueAnimator moveableAnimator;

    /**
     * Caches drawing static elements.
     */
    private Bitmap cachedBitmap;

    public JoypadView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JoypadView, 0, 0);
        final Resources resources = context.getResources();

        outerPaint.setStyle(Paint.Style.STROKE);
        //noinspection deprecation
        outerPaint.setColor(array.getColor(
                R.styleable.JoypadView_outer_color,
                resources.getColor(R.color.joypad_grey_50)
        ));
        outerPaint.setStrokeWidth(array.getDimensionPixelSize(
                R.styleable.JoypadView_outer_width,
                resources.getDimensionPixelSize(R.dimen.joypad_outer_width)
        ));

        innerPaint.setStyle(Paint.Style.FILL);
        //noinspection deprecation
        innerPaint.setColor(array.getColor(
                R.styleable.JoypadView_inner_color,
                resources.getColor(R.color.joypad_grey_500)
        ));

        moveablePaint.setStyle(Paint.Style.FILL);
        //noinspection deprecation
        moveablePaint.setColor(array.getColor(
                R.styleable.JoypadView_moveable_color,
                resources.getColor(R.color.joypad_grey_900)
        ));

        directionsPaint.setStyle(Paint.Style.FILL);
        //noinspection deprecation
        directionsPaint.setColor(array.getColor(
                R.styleable.JoypadView_directions_color,
                resources.getColor(R.color.joypad_grey_300)
        ));

        innerRadius = array.getDimensionPixelSize(
                R.styleable.JoypadView_inner_radius,
                resources.getDimensionPixelSize(R.dimen.joypad_inner_radius)
        );
        moveableRadius = array.getDimensionPixelSize(
                R.styleable.JoypadView_moveable_radius,
                resources.getDimensionPixelSize(R.dimen.joypad_moveable_radius)
        );
    }

    /**
     * Sets joypad listener.
     * @return this instance
     */
    public JoypadView setListener(final Listener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public boolean onTouchEvent(@NonNull final MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                fireUp();

                final PointF oldMoveablePoint = moveablePoint;
                moveableAnimator = ValueAnimator.ofFloat(0f, 1f);
                moveableAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(@NonNull final ValueAnimator animation) {
                        final float value = (float)moveableAnimator.getAnimatedValue();
                        moveablePoint = new PointF(
                                (1f - value) * oldMoveablePoint.x + value * centerPoint,
                                (1f - value) * oldMoveablePoint.y + value * centerPoint
                        );
                        postInvalidate();
                    }
                });
                moveableAnimator.start();
                return true;

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (moveableAnimator != null) {
                    moveableAnimator.cancel();
                    moveableAnimator = null;
                }

                final float offsetX = event.getX() - centerPoint;
                final float offsetY = event.getY() - centerPoint;
                final float distance = (float)Math.sqrt(offsetX * offsetX + offsetY * offsetY);

                if (distance < maxDistance) {
                    moveablePoint = new PointF(event.getX(), event.getY());
                    fireMove(distance / maxDistance, offsetX / maxDistance, -offsetY / maxDistance);
                } else {
                    moveablePoint = new PointF(
                            centerPoint + maxDistance * offsetX / distance,
                            centerPoint + maxDistance * offsetY / distance
                    );
                    fireMove(1f, offsetX / distance, -offsetY / distance);
                }

                postInvalidate();
                return true;

            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldWidth, final int oldHeight) {
        // Invalidate cache.
        cachedBitmap = null;

        minSizeDimension = Math.min(width, height);
        // Center X and Y values.
        centerPoint = minSizeDimension / 2f;
        // Maximum moveable circle distance from the center.
        maxDistance = centerPoint - moveableRadius;
        // Move the moveable circle to the center.
        moveablePoint = new PointF(centerPoint, centerPoint);
        fireUp();
    }

    @Override
    protected void onDraw(@NonNull final Canvas canvas) {
        super.onDraw(canvas);

        if ((cachedBitmap == null) || cachedBitmap.isRecycled()) {
            cachedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            drawStaticElements(new Canvas(cachedBitmap));
        }
        canvas.drawBitmap(cachedBitmap, 0f, 0f, null);
        canvas.drawCircle(moveablePoint.x, moveablePoint.y, moveableRadius, moveablePaint);
    }

    /**
     * Rotates the specified point around (0, 0).
     */
    private static PointF rotatePoint(final float x, final float y, final PointF rotation) {
        return new PointF(x * rotation.x - y * rotation.y, x * rotation.y + y * rotation.x);
    }

    /**
     * Draws static elements on the background picture in order to speed up {@see Joypad#onDraw}.
     */
    private void drawStaticElements(final Canvas canvas) {
        final float outerWidth = outerPaint.getStrokeWidth();

        // Draw outer arc.
        final float outerOffset = outerWidth / 2f;
        final RectF outerRect = new RectF(
                outerOffset, outerOffset,
                minSizeDimension - outerOffset, minSizeDimension - outerOffset
        );
        canvas.drawArc(outerRect, 0f, 360f, false, outerPaint);

        // Draw direction triangles.
        final float triangleHeight = 0.5f * outerWidth;
        final float offsetY = centerPoint - 0.75f * outerWidth;
        final float offsetX = SIN_30 * 0.5f * outerWidth;

        for (final PointF rotation : ROTATION) {
            // We'll rotate vertices of triangle.
            final Path path = new Path();
            final PointF startPoint = rotatePoint(0f, offsetY + triangleHeight, rotation);
            path.moveTo(centerPoint + startPoint.x, centerPoint + startPoint.y);
            final PointF leftPoint = rotatePoint(-offsetX, offsetY, rotation);
            path.lineTo(centerPoint + leftPoint.x, centerPoint + leftPoint.y);
            final PointF rightPoint = rotatePoint(+offsetX, offsetY, rotation);
            path.lineTo(centerPoint + rightPoint.x, centerPoint + rightPoint.y);
            path.close();

            canvas.drawPath(path, directionsPaint);
        }

        // Draw inner circle.
        canvas.drawCircle(centerPoint, centerPoint, innerRadius, innerPaint);
    }

    /**
     * Fires onUp event.
     * @see Listener#onUp()
     */
    private void fireUp() {
        final Listener listener = this.listener;
        if (listener != null) {
            listener.onUp();
        }
    }

    /**
     * Fires onMove event.
     * @see Listener#onMove(float, float, float)
     */
    private void fireMove(final float distance, final float dx, final float dy) {
        final Listener listener = this.listener;
        if (listener != null) {
            listener.onMove(distance, dx, dy);
        }
    }
}
