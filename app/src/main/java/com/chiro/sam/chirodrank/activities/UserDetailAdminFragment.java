package com.chiro.sam.chirodrank.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chiro.sam.chirodrank.R;
import com.chiro.sam.chirodrank.model.DatabaseHandler;
import com.chiro.sam.chirodrank.model.User;

import java.lang.reflect.Field;
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

    private DatabaseHandler handler;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserDetailAdminFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new DatabaseHandler(getContext());

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
        final View rootView = inflater.inflate(R.layout.user_detail_admin, container, false);

        if (mItem != null) {
            String formatted = String.format(Locale.ENGLISH, "€ %d.%02d", mItem.getBalance() / 100, mItem.getBalance() % 100);
            final TextView oldBalance = (TextView) rootView.findViewById(R.id.text_admin_balance);
            oldBalance.setText(formatted);

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

            // work around to fix display bug
            Field f = null;
            try {
                f = NumberPicker.class.getDeclaredField("mInputText");
                f.setAccessible(true);
                EditText inputText = (EditText) f.get(sign);
                inputText.setFilters(new InputFilter[0]);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


            final NumberPicker delta1 = (NumberPicker) rootView.findViewById(R.id.numberPickerDelta1);
            delta1.setMinValue(0);
            delta1.setMaxValue(1000);
            delta1.setWrapSelectorWheel(false);

            final NumberPicker delta2 = (NumberPicker) rootView.findViewById(R.id.numberPickerDelta2);
            delta2.setMinValue(0);
            delta2.setMaxValue(99);
            delta2.setWrapSelectorWheel(false);

            final NumberPicker newValue1 = (NumberPicker) rootView.findViewById(R.id.numberPickerNew1);
            newValue1.setMinValue(0);
            newValue1.setMaxValue(1000);
            newValue1.setValue(mItem.getBalance() / 100);
            newValue1.setWrapSelectorWheel(false);

            final NumberPicker newValue2 = (NumberPicker) rootView.findViewById(R.id.numberPickerNew2);
            newValue2.setMinValue(0);
            newValue2.setMaxValue(99);
            newValue2.setValue((mItem.getBalance() % 100));
            newValue2.setWrapSelectorWheel(false);

            sign.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                    int newV = mItem.getBalance();
                    if (newVal == 0) {
                        newV += delta1.getValue() * 100 + delta2.getValue();
                    } else {
                        newV -= delta1.getValue() * 100 + delta2.getValue();
                    }
                    newValue1.setValue(newV / 100);
                    newValue2.setValue(newV % 100);
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
                        newV += delta1.getValue() * 100 + delta2.getValue();
                    } else {
                        newV -= delta1.getValue() * 100 + delta2.getValue();
                    }
                    newValue1.setValue(newV / 100);
                    newValue2.setValue(newV % 100);
                }
            });

            newValue1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                    int newV = newVal * 100 + newValue2.getValue();
                    int diff;
                    if (newV >= mItem.getBalance()) {
                        diff = newV - mItem.getBalance();
                        sign.setValue(0);
                    } else {
                        diff = mItem.getBalance() - newV;
                        sign.setValue(1);
                    }
                    delta1.setValue(diff / 100);
                    delta2.setValue(diff % 100);
                }
            });

            newValue2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                    int newV = newValue1.getValue() * 100 + newVal;
                    int diff;
                    if (newV >= mItem.getBalance()) {
                        diff = newV - mItem.getBalance();
                        sign.setValue(0);
                    } else {
                        diff = mItem.getBalance() - newV;
                        sign.setValue(1);
                    }
                    delta1.setValue(diff / 100);
                    delta2.setValue(diff % 100);
                }
            });

            Button changeBalanceButton = (Button) rootView.findViewById(R.id.button_change_balance);
            changeBalanceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItem.setBalance(newValue1.getValue() * 100 + newValue2.getValue());
                    handler.updateUser(mItem);
                    String formatted = String.format(Locale.ENGLISH, "€ %d.%02d", mItem.getBalance() / 100, mItem.getBalance() % 100);
                    oldBalance.setText(formatted);
                }
            });

            Button deleteUserButton = (Button) rootView.findViewById(R.id.button_delete_user);
            deleteUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Delete user ?");

                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.deleteUser(mItem);
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });
        }
        return rootView;
    }
}
