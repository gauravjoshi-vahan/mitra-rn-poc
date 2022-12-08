package com.vahan.mitra_playstore.utils;

import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.vahan.mitra_playstore.R;

public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;
    private final FragmentActivity context;

    public ZoomOutPageTransformer(FragmentActivity requireActivity) {
        this.context = requireActivity;
    }

    public void transformPage(View page, float position) {
        float nextItemVisiblePx = context.getResources().getDimension(R.dimen.viewpager_next_item_visible);
        float currentItemHorizontalMarginPx = context.getResources().getDimension(R.dimen.viewpager_current_item_horizontal_margin);
        float pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx;
        page.setTranslationX(-pageTranslationX * position);
        // Next line scales the item's height. You can remove it if you don't want this effect
        page.setScaleY(1 - (0.25f * Math.abs(position)));
    }

}
