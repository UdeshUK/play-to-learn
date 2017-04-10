package com.avoid.playtolearn.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.avoid.playtolearn.common.Font;
import com.avoid.playtolearn.util.FontCache;

public class LabelTextView extends android.support.v7.widget.AppCompatTextView {
    public LabelTextView(Context context) {
        super(context);
        applyCustomShape(context);
        applyCustomFont(context);
    }

    public LabelTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomShape(context);
        applyCustomFont(context);
    }

    public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomShape(context);
        applyCustomFont(context);
    }

    private void applyCustomShape(Context context) {
//        setBackgroundColor(Color.WHITE);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface(Font.ACTION_MAN_BOLD_FONT, context);
        setTypeface(customFont);

        setTextColor(Color.WHITE);
    }
}
