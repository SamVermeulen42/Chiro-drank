package com.chiro.sam.chirodrank.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chiro.sam.chirodrank.R;

public class SideSpinner extends LinearLayout {

    private int value;

    private Button mPreviousButton;
    private Button mNextButton;
    private TextView countView;

    public SideSpinner(Context context) {
        super(context);
        initializeViews(context);
    }

    public SideSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public SideSpinner(Context context,
                       AttributeSet attrs,
                       int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sidespinner_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        countView = (TextView) this.findViewById(R.id.sidespinner_view_current_value);

        mPreviousButton = (Button) this
                .findViewById(R.id.sidespinner_view_previous);
        mPreviousButton
                .setBackgroundResource(android.R.drawable.ic_media_previous);
        mPreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                value--;
                if (value == 0) {
                    mPreviousButton.setVisibility(INVISIBLE);
                } else {
                    mPreviousButton.setVisibility(VISIBLE);
                }
                countView.setText(String.valueOf(value));
            }
        });


        mNextButton = (Button)this
                .findViewById(R.id.sidespinner_view_next);
        mNextButton
                .setBackgroundResource(android.R.drawable.ic_media_next);
        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                value++;
                countView.setText(String.valueOf(value));
            }
        });
    }

    public int getValue() {
        return value;
    }
}
