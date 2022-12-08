package com.vahan.mitra_playstore.utils;

public interface DrawableClickListener {

    enum DrawablePosition {TOP, BOTTOM, LEFT, RIGHT}

    void onClick(DrawablePosition target);
}