package com.alexwbaule.flexprofile.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.utils.CustomGenerateViewId;

import java.util.List;

public class SeekbarWithIntervals extends LinearLayout implements OnSeekBarChangeListener, View.OnClickListener {
    private RelativeLayout RelativeLayout = null;
    private SeekBar Seekbar = null;
    private TextView textView = null;
    private Context context;
    private int MarkerSize = 0;

    private int WidthMeasureSpec = 0;
    private int HeightMeasureSpec = 0;

    public SeekbarWithIntervals(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(context).inflate(R.layout.seekbar_with_intervals, this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            alignIntervals();

            // We've changed the intervals layout, we need to refresh.
            RelativeLayout.measure(WidthMeasureSpec, HeightMeasureSpec);
            RelativeLayout.layout(RelativeLayout.getLeft(), RelativeLayout.getTop(), RelativeLayout.getRight(), RelativeLayout.getBottom());
        }
    }

    private void alignIntervals() {
        int widthOfSeekbarThumb = getSeekbarThumbWidth();
        int thumbOffset = widthOfSeekbarThumb / 2;
        int eachStep = MarkerSize == 0 ? 0 : getSeekbar().getWidth() / MarkerSize;

        alignFirstInterval(thumbOffset);
        alignIntervalsInBetween(eachStep);
        alignLastInterval(eachStep);
    }

    private int getSeekbarThumbWidth() {
        int seekbar_padding = getSeekbar().getPaddingLeft();
        int thumb_size = getSeekbar().getThumbOffset();

        return seekbar_padding - thumb_size;
    }

    private void alignFirstInterval(int offset) {
        if (getRelativeLayout().getChildAt(0) != null) {
            TextView firstInterval = (TextView) getRelativeLayout().getChildAt(0);
            firstInterval.setPadding(offset, 0, 0, 0);
        }
    }

    private void alignIntervalsInBetween(int maximumWidthOfEachInterval) {
        // Don't align the first or last interval.
        for (int index = 1; index < (getRelativeLayout().getChildCount() - 1); index++) {
            TextView textViewInterval = (TextView) getRelativeLayout().getChildAt(index);
            TextView lastText = (TextView) getRelativeLayout().getChildAt(index - 1);
            int widthOfLastText = lastText.getWidth();
            // This works out how much left padding is needed to center the current interval.
            int leftPadding = Math.round(maximumWidthOfEachInterval - widthOfLastText - (getSeekbar().getThumbOffset() / 2));
            textViewInterval.setPadding(leftPadding, 0, 0, 0);
        }
    }

    private void alignLastInterval(int maximumWidthOfEachInterval) {
        int lastIndex = getRelativeLayout().getChildCount() - 1;
        if (getRelativeLayout().getChildAt(lastIndex) != null) {
            TextView lastInterval = (TextView) getRelativeLayout().getChildAt(lastIndex);
            int widthOfText = lastInterval.getWidth();

            int leftPadding = Math.round(maximumWidthOfEachInterval - widthOfText - (getSeekbar().getThumbOffset() / 2));
            lastInterval.setPadding(leftPadding, 0, 0, 0);
        }
    }

    protected synchronized void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        WidthMeasureSpec = widthMeasureSpec;
        HeightMeasureSpec = heightMeasureSpec;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getProgress() {
        return getSeekbar().getProgress();
    }

    public void setProgress(int progress) {
        getSeekbar().setProgress(progress);
    }

    public void setIntervals(List<String> intervals) {
        displayIntervals(intervals);
        getSeekbar().setMax(100);
        MarkerSize = (intervals.size() - 1);
    }

    private void displayIntervals(List<String> intervals) {
        int idOfPreviousInterval = 0;
        int inicial = 0;

        if (getRelativeLayout().getChildCount() == 0) {
            for (String interval : intervals) {
                TextView textViewInterval = createInterval(interval);
                alignTextViewToRightOfPreviousInterval(textViewInterval, idOfPreviousInterval);
                idOfPreviousInterval = textViewInterval.getId();
                textViewInterval.setTag(inicial);
                getRelativeLayout().addView(textViewInterval);
                inicial += 25;
            }
        }
    }

    private TextView createInterval(String interval) {
        View textBoxView = LayoutInflater.from(getContext())
                .inflate(R.layout.seekbar_with_intervals_labels, null);

        TextView textView = textBoxView
                .findViewById(R.id.textViewInterval);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setId(View.generateViewId());
        } else {
            textView.setId(CustomGenerateViewId.customGenerateViewId());
        }
        textView.setText(interval);
        textBoxView.setOnClickListener(this);

        return textView;
    }

    private void alignTextViewToRightOfPreviousInterval(TextView textView, int idOfPreviousInterval) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (idOfPreviousInterval > 0) {
            params.addRule(android.widget.RelativeLayout.RIGHT_OF, idOfPreviousInterval);
        }

        textView.setLayoutParams(params);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        getSeekbar().setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    private RelativeLayout getRelativeLayout() {
        if (RelativeLayout == null) {
            RelativeLayout = findViewById(R.id.intervals);
        }

        return RelativeLayout;
    }

    private SeekBar getSeekbar() {
        if (Seekbar == null) {
            Seekbar = findViewById(R.id.seekbar);
            Seekbar.setOnSeekBarChangeListener(this);
        }

        return Seekbar;
    }

    private TextView getTextView() {
        if (textView == null) {
            textView = findViewById(R.id.seek_bar_value);
        }
        return textView;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        getTextView().setText(context.getString(R.string.percent, progress, context.getString(R.string.percent_sign)));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        int value = (int) v.getTag();
        getSeekbar().setProgress(value);
    }
}