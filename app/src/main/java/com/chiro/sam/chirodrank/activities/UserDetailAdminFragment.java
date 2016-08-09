package com.chiro.sam.chirodrank.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.chiro.sam.chirodrank.R;
import com.chiro.sam.chirodrank.model.DatabaseHandler;
import com.chiro.sam.chirodrank.model.User;

import java.util.Locale;

public class UserDetailAdminFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private User mItem;

    private boolean spinnerPlus = true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserDetailAdminFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHandler handler = new DatabaseHandler(getContext());

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = handler.getUser(getArguments().getInt(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getName());
                appBarLayout.setBackgroundColor(Color.RED);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail_admin, container, false);

        if (mItem != null) {
            String formatted = String.format(Locale.ENGLISH, "€ %d.%02d", mItem.getBalance() / 100, mItem.getBalance() % 100);
            ((TextView) rootView.findViewById(R.id.text_admin_balance)).setText(formatted);

            final NumberPicker sign = (NumberPicker) rootView.findViewById(R.id.numberPickerSign);
            sign.setMinValue(0);
            sign.setMaxValue(1);

            NumberPicker.Formatter formatterSign = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    if (value == 0) {
                        return "+";
                    } else {
                        return "-";
                    }
                }
            };
            sign.setFormatter(formatterSign);

            NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    int temp = value * 10;
                    return "" + temp;
                }
            };

            final NumberPicker delta1 = (NumberPicker) rootView.findViewById(R.id.numberPickerDelta1);
            delta1.setMinValue(0);
            delta1.setMaxValue(1000);
            delta1.setWrapSelectorWheel(false);

            final NumberPicker delta2 = (NumberPicker) rootView.findViewById(R.id.numberPickerDelta2);
            delta2.setMinValue(0);
            delta2.setMaxValue(9); // div by 10 for formatter
            delta2.setFormatter(formatter);
            delta2.setWrapSelectorWheel(false);

            final NumberPicker newValue1 = (NumberPicker) rootView.findViewById(R.id.numberPickerNew1);
            newValue1.setMinValue(0);
            newValue1.setMaxValue(1000);
            newValue1.setValue(mItem.getBalance() / 100);
            newValue1.setWrapSelectorWheel(false);

            final NumberPicker newValue2 = (NumberPicker) rootView.findViewById(R.id.numberPickerNew2);
            newValue2.setMinValue(0);
            newValue2.setMaxValue(9); // div by 10 for formatter
            newValue2.setValue((mItem.getBalance() % 100) / 10);
            newValue2.setFormatter(formatter);
            newValue2.setWrapSelectorWheel(false);

            sign.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                    int newV = mItem.getBalance();
                    if (newVal == 0) {
                        newV += delta1.getValue() * 100 + delta2.getValue() * 10;
                    } else {
                        newV -= delta1.getValue() * 100 + delta2.getValue() * 10;
                    }
                    newValue1.setValue(newV / 100);
                    newValue2.setValue((newV % 100) / 10);
                }
            });

            delta1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                    if (sign.getValue() == 0) {
                        newValue1.setValue(newValue1.getValue() + newVal - oldVal);
                    } else {
                        newValue1.setValue(newValue1.getValue() - newVal + oldVal);
                    }
                }
            });

            delta2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                    int newV = mItem.getBalance();
                    if (sign.getValue() == 0) {
                        newV += delta1.getValue() * 100 + delta2.getValue() * 10;
                    } else {
                        newV -= delta1.getValue() * 100 + delta2.getValue() * 10;
                    }
                    newValue1.setValue(newV / 100);
                    newValue2.setValue((newV % 100) / 10);
                }
            });

            newValue1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                    int newV = newVal * 100 + newValue2.getValue() * 10;
                    int diff;
                    if (newV >= mItem.getBalance()) {
                        diff = newV - mItem.getBalance();
                        sign.setValue(0);
                    } else {
                        diff = mItem.getBalance() - newV;
                        sign.setValue(1);
                    }
                    delta1.setValue(diff / 100);
                    delta2.setValue((diff % 100) / 10);
                }
            });

            newValue2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                    int newV = newValue1.getValue() * 100 + newVal * 10;
                    int diff;
                    if (newV >= mItem.getBalance()) {
                        diff = newV - mItem.getBalance();
                        sign.setValue(0);
                    } else {
                        diff = mItem.getBalance() - newV;
                        sign.setValue(1);
                    }
                    delta1.setValue(diff / 100);
                    delta2.setValue((diff % 100) / 10);
                }
            });

        }
        return rootView;
    }
}
