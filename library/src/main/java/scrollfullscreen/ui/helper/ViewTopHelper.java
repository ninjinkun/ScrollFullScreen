package scrollfullscreen.ui.helper;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.lang.ref.WeakReference;

/**
 * Adjust view's top offset for show/hide ActionBar.
 */

public class ViewTopHelper {
    private WeakReference<View> target;
    private int from;
    private int currentTargetOffsetTop;
    private int actionBarShownTop = 0;
    private int actionBarHiddenTop = 0;

    /**
     * Constructor
     * @param target
     * @param actionBarShownTop Top height on ActionBar shown
     * @param actionBarHiddenTop Top height on ActionBar hidden
     */
    public ViewTopHelper(View target, int actionBarShownTop, int actionBarHiddenTop) {
        this.target = new WeakReference<View>(target);
        this.actionBarShownTop = actionBarShownTop;
        this.actionBarHiddenTop = actionBarHiddenTop;
    }

    private static int calculateOffsetTop(View target, int from, int originalOffsetTop, float interpolatedTime) {
        int targetTop = 0;
        if (from != originalOffsetTop) {
            targetTop = (from + (int)((originalOffsetTop - from) * interpolatedTime));
        }
        int offset = targetTop - target.getTop();
        final int currentTop = target.getTop();
        if (offset + currentTop < 0) {
            offset = 0 - currentTop;
        }
        return offset;
    }

    private final Animation moveToActionBarHiddenAnimation = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            View targetView = target.get();
            if (targetView != null) {
                int offset = calculateOffsetTop(targetView, from, actionBarHiddenTop, interpolatedTime);
                setTransrationY(offset);
            }
        }
    };

    private final Animation moveToActinoBarShownAnimation = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            View targetView = target.get();
            if (targetView != null) {
                int offset = calculateOffsetTop(targetView, from, actionBarShownTop, interpolatedTime);
                setTransrationY(offset);
            }
        }
    };


    /**
     * Adjust offset to ActionBar shown mode
     * It's to be called when activity is initialized
     */
    public void setOffsetToActionBarShownPostision() {
        View targetView = target.get();
        if (targetView != null) {
            int offset = calculateOffsetTop(targetView, from, actionBarShownTop, 1f);
            setTransrationY(offset);
        }
    }

    /**
     * Adjust offset to ActionBar hidden mode
     */
    public void setOffsetToActionBarHiddenPostision() {
        View targetView = target.get();
        if (targetView != null) {
            int offset = calculateOffsetTop(targetView, from, actionBarHiddenTop, 1f);
            setTransrationY(offset);
        }
    }

    /**
     * Adjust offset to ActionBar shown mode with animation
     * @param listener handle animation start/end
     * @param duration animation duration
     */

    public void animateOffsetToActionBarShownPosition(final Animation.AnimationListener listener, int duration) {
        applyAnimationToTarget(currentTargetOffsetTop, moveToActinoBarShownAnimation, listener, duration);
    }

    /**
     * Adjust offset to ActionBar hidden mode with animation
     * @param listener handle animation start/end
     * @param duration animation duration
     */
    public void animateOffsetToActionBarHiddenPosition(final Animation.AnimationListener listener, int duration) {
        applyAnimationToTarget(currentTargetOffsetTop, moveToActionBarHiddenAnimation,  listener, duration);
    }

    private void applyAnimationToTarget(int from, final Animation animation, Animation.AnimationListener listener, int duration) {
        this.from = from;
        animation.reset();
        animation.setDuration(duration);
        animation.setAnimationListener(listener);
        target.get().startAnimation(animation);
    }

    /**
     * Set view's transrationY
     * @param offset
     */
    private void setTransrationY(int offset) {
        target.get().setTranslationY(offset);
        currentTargetOffsetTop = offset;
    }
}
