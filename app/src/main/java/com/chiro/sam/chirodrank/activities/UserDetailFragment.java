package com.chiro.sam.chirodrank.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

    private DatabaseHandler handler;

    private SideSpinner beer, crate, heavy, chips;

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
        beer = (SideSpinner) this.getActivity().findViewById(R.id.sidespinner_beer);
        crate = (SideSpinner) this.getActivity().findViewById(R.id.sidespinner_crate);
        heavy = (SideSpinner) this.getActivity().findViewById(R.id.sidespinner_heavy);
        chips = (SideSpinner) this.getActivity().findViewById(R.id.sidespinner_chips);

        Button orderButton = (Button) this.getActivity().findViewById(R.id.button_order);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order(beer.getValue(), crate.getValue(), heavy.getValue(), chips.getValue());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            String formatted = String.format(Locale.ENGLISH, "€ %d.%02d", mItem.getBalance()/100, mItem.getBalance()%100);
            ((TextView) rootView.findViewById(R.id.user_detail)).setText(formatted);
        }

        return rootView;
    }

    private void order(int beer, int crate, int heavy, int chips) {

    }
}
