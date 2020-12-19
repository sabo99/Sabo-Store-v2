package com.sabo.sabostorev2.Common;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScaleCenterItemLayoutManager extends LinearLayoutManager {
    public ScaleCenterItemLayoutManager(Context context) {
        super(context);
    }

    public ScaleCenterItemLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        lp.width = getWidth() / 3;
        return true;
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        scaleMiddleItem();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
        if (getOrientation() == RecyclerView.HORIZONTAL) {

            scaleMiddleItem();
            return scrolled;
        } else
            return 0;
    }

    private void scaleMiddleItem() {
        float mid = getWidth() / 2.0f;
        float d1 = 0.9f * mid;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float childMid = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2.0f;
            float d = Math.min(d1, Math.abs(mid - childMid));
            float scale = 1f - 0.15f * d / d1;
            child.setScaleX(scale);
            child.setScaleY(scale);
        }
    }
}
