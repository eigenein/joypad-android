package ninja.eigenein.joypad;

/**
 * Represents relative power for both wheels.
 */
@SuppressWarnings("WeakerAccess")
public class WheelsPower {
    private final static double PI_2 = Math.PI / 2.0;

    private final float left;
    private final float right;

    /**
     * Converts offsets to a car wheels power.
     */
    public static WheelsPower wheelsPower(final float distance, final float dx, final float dy) {
        final double angle = Math.atan2(dy, dx);
        return new WheelsPower(wheelPower(angle) * distance, wheelPower(angle - PI_2) * distance);
    }

    private static float wheelPower(final double angle) {
        if ((angle >= 0.0) && (angle <= PI_2)) {
            return 1.0f;
        }
        if ((angle >= -Math.PI) && (angle <= -PI_2)) {
            return -1.0f;
        }
        if ((angle >= -PI_2) && (angle <= 0.0)) {
            return (float)Math.cos(2.0 * angle);
        }
        return -(float)Math.cos(angle * 2.0);
    }

    public WheelsPower(final float left, final float right) {
        this.left = left;
        this.right = right;
    }

    public float getLeft() {
        return left;
    }

    public float getRight() {
        return right;
    }
}
