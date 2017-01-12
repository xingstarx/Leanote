
package com.leanote.android.weidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class TabView extends LinearLayoutCompat {

    private static final int[] ATTRS = new int[] {
            android.R.attr.selectableItemBackground
    };

    private ImageView mImageView;
    private TextView mTextView;

    public TabView(@NonNull Context context) {
        this(context, null);
    }

    public TabView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.actionBarTabStyle);
    }

    public TabView(@NonNull Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarTabTextStyle, outValue, true);

        int txtstyle = outValue.data;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, ATTRS);

        int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

        mImageView = new ImageView(context);
        mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        mImageView.setScaleType(ScaleType.CENTER_INSIDE);

        mTextView = new TextView(context);
        mTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setCompoundDrawablePadding(pad);
        mTextView.setTextAppearance(context, txtstyle);
        mTextView.setTextColor(Color.WHITE);

        Drawable backgroundDrawable = typedArray.getDrawable(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setBackground(backgroundDrawable);
        }
        typedArray.recycle();
        this.addView(mImageView);
        this.addView(mTextView);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setIcon(int resId) {
        setIcon(getResources().getDrawable(resId));
    }

    public void setIcon(@Nullable Drawable icon) {
        mImageView.setVisibility(icon != null ? View.VISIBLE : View.GONE);
        mImageView.setImageDrawable(icon);
    }

    public void setTextColor(int color) {
        mTextView.setTextColor(color);
    }

    public void setText(int resId, int ico) {
        setText(getContext().getString(resId), ico);
    }

    public void setText(CharSequence text, int ico) {
        mTextView.setText(text);
        mTextView.setCompoundDrawablesWithIntrinsicBounds(ico, 0, 0, 0);
    }
}