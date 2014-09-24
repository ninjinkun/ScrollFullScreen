package scrollfullscreen;

/**
 * Detect scroll and dispatch full screen event to listener
 */

public class ScrollDetector {
    /**
     * Interface definition for a callback to be invoke when full screen mode start/finish.
     */
    public interface OnFullScreenListener {
        /**
         * Callback method to be invoked when full screen mode is started
         */
        public void onFullScreenStarted();

        /**
         * Callback method to be invoked when full screen mode is inished
         */
        public void onFullScreenFinished();
    }

    private final OnFullScreenListener onFullScreenListener;
    private int accumulatedY;
    private ScrollDirection previousFiredDirection = ScrollDirection.NONE;
    private ScrollDirection previousDirection = ScrollDirection.NONE;
    private final int finishThresholdY;
    private final int startThresholdY;

    private static final int DEFAULT_UP_THRESHOLD_Y = 0;
    private static final int DEFAULT_DOWN_THRESHOLD_Y = 0;

    /**
     * Constructor with default threshold value
     * @param onFullScreenListener
     */
    public ScrollDetector(OnFullScreenListener onFullScreenListener) {
        this(onFullScreenListener, DEFAULT_UP_THRESHOLD_Y, DEFAULT_DOWN_THRESHOLD_Y);
    }

    /**
     * Constructor
     * @param onFullScreenListener
     * @param startThresholdY scroll distance until fire onFullScreenStarted
     * @param finishThresholdY scroll distance until fire onFullScreenFinished
     */

    public ScrollDetector(OnFullScreenListener onFullScreenListener, int startThresholdY, int finishThresholdY) {
        this.onFullScreenListener = onFullScreenListener;
        this.startThresholdY = startThresholdY;
        this.finishThresholdY = finishThresholdY;
    }

    /**
     * Get scroll distance and calculate to fire listener.
     * It is called by adapter or scroll view's subclass.
     * @param x
     * @param y
     * @param oldX
     * @param oldY
     */

    public void onScrollChanged(int x, int y, int oldX, int oldY) {
        int deltaY = oldY - y;
        accumulatedY += deltaY;
        ScrollDirection currentDirection = ScrollDirection.dectect(y, oldY);

        boolean isCurrentDirectionPreviousFired = previousFiredDirection == currentDirection;

        switch (currentDirection) {
            case UP: {
                boolean isOverThreshold = accumulatedY > finishThresholdY;
                if (isOverThreshold && !isCurrentDirectionPreviousFired) {
                    onFullScreenListener.onFullScreenFinished();
                    previousFiredDirection = currentDirection;
                    accumulatedY = deltaY;
                }
            }
            break;

            case DOWN: {
                boolean isOverThreshold = accumulatedY <= -startThresholdY;
                if (isOverThreshold && !isCurrentDirectionPreviousFired) {
                    onFullScreenListener.onFullScreenStarted();
                    previousFiredDirection = currentDirection;
                    accumulatedY = deltaY;
                }
            }
            break;

            case NONE:
                break;
        }


        if (currentDirection != ScrollDirection.NONE) {
            if (previousDirection != currentDirection) {
                accumulatedY = deltaY;
            }
            previousDirection = currentDirection;
        }
    }

    /**
     * Reset current states
     */
    public void reset() {
        accumulatedY = 0;
        previousFiredDirection = ScrollDirection.NONE;
        previousFiredDirection = ScrollDirection.NONE;
    }

    private enum ScrollDirection {
        NONE, UP, DOWN;

        public static ScrollDirection dectect(int currentOffsetY, int previousOffsetY) {
            return currentOffsetY < previousOffsetY ? UP :
                    currentOffsetY > previousOffsetY ? DOWN :
                            NONE;
        }
    }
}

