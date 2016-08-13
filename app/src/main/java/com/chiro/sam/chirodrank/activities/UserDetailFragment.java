package com.chiro.sam.chirodrank.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chiro.sam.chirodrank.R;
import com.chiro.sam.chirodrank.model.DatabaseHandler;
import com.chiro.sam.chirodrank.model.User;

import java.util.Locale;

/**
 * A fragment representing a single User detail screen.
 * This fragment is either contained in a {@link UserListActivity}
 * in two-pane mode (on tablets) or a {@link UserDetailActivity}
 * on handsets.
 */
public class UserDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private User mItem;

    private int beerCount = 0, crateCount = 0, heavyCount = 0, chipsCount = 0;

    private TextView diffView, signView, balanceView;
    private Button orderButton;

    private DatabaseHandler handler;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserDetailFragment() {
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
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            balanceView = ((TextView) rootView.findViewById(R.id.user_detail_balance));
            String formatted = String.format(Locale.ENGLISH, "€ %d.%02d", mItem.getBalance()/100, mItem.getBalance()%100);
            balanceView.setText(formatted);
        }

        diffView = (TextView) rootView.findViewById(R.id.user_detail_diff);
        signView = (TextView) rootView.findViewById(R.id.user_detail_diff_signing);

        SideSpinner beer = (SideSpinner) rootView.findViewById(R.id.sidespinner_beer);
        beer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                beerCount = ((SideSpinner) view).getValue();
                updateDiff();
            }
        });
        SideSpinner crate = (SideSpinner) rootView.findViewById(R.id.sidespinner_crate);
        crate.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                crateCount = ((SideSpinner) view).getValue();
                updateDiff();
            }
        });
        SideSpinner heavy = (SideSpinner) rootView.findViewById(R.id.sidespinner_heavy);
        heavy.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                heavyCount = ((SideSpinner) view).getValue();
                updateDiff();
            }
        });
        SideSpinner chips = (SideSpinner) rootView.findViewById(R.id.sidespinner_chips);
        chips.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                chipsCount = ((SideSpinner) view).getValue();
                updateDiff();
            }
        });

        orderButton = (Button) rootView.findViewById(R.id.button_order);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int totalOrdered = chipsCount * 30 + crateCount * 1500 + heavyCount * 150 + beerCount * 100;
                if (totalOrdered <= mItem.getBalance()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Verify transaction");

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mItem.setBalance(mItem.getBalance() - totalOrdered);
                            handler.updateUser(mItem);
                            Context context = getContext();
                            Intent intent;
                            intent = new Intent(context, UserListActivity.class);

                            context.startActivity(intent);
                            Toast.makeText(getContext(), "Transaction complete", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                } else {
                    Toast.makeText(getContext(), "Balance to low", Toast.LENGTH_LONG).show();
                }

            }
        });

        return rootView;
    }

    public void updateDiff() {
        int totalOrdered = chipsCount * 30 + crateCount * 1500 + heavyCount * 150 + beerCount * 100;
        if (totalOrdered <= mItem.getBalance()) {
            signView.setTextColor(Color.GREEN);
            diffView.setTextColor(Color.GREEN);
            orderButton.setEnabled(true);

        } else {
            signView.setTextColor(Color.RED);
            diffView.setTextColor(Color.RED);
            orderButton.setEnabled(false);
        }
        diffView.setText(String.valueOf(totalOrdered / 100) + "." + String.valueOf(totalOrdered % 100));
    }
}
