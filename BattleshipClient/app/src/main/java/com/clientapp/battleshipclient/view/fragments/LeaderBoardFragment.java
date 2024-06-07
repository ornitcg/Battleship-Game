package com.clientapp.battleshipclient.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.clientapp.battleshipclient.R;
import com.clientapp.battleshipclient.data.User;

import java.util.ArrayList;

public class LeaderBoardFragment extends Fragment {

    private ArrayList<User> bestUsersList; // Replace with your data type
    private LinearLayout parentLayout; // The parent layout to hold CardViews


    public static LeaderBoardFragment newInstance(ArrayList<User> usersList) {
        LeaderBoardFragment fragment = new LeaderBoardFragment();
        Bundle args = new Bundle();
        args.putSerializable("users_list", usersList);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leader_board, container, false); // Replace with your fragment layout
        parentLayout = view.findViewById(R.id.cardsLinearLayoutId); // Replace with the ID of your parent layout
        if (getArguments() != null) {
            bestUsersList = (ArrayList<User>) getArguments().getSerializable("users_list");
        }
        if (bestUsersList == null) {
            bestUsersList = new ArrayList<>(); // Initialize to avoid null pointer exception
        }
        Log.d("myDEBUG LeaderBoardFragment", "onCreateView: " + bestUsersList.toString());
        addCardViews();
        return view;
    }




    /*
    *  Creates and Add a new CardView for each item in the list
    * */
    private void addCardViews() {
        if (bestUsersList == null) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (User user : bestUsersList) { // Replace with your data type and variable
            CardView cardView = (CardView) inflater.inflate(R.layout.card_view_leader, parentLayout, false);
            TextView userName = cardView.findViewById(R.id.playerNameId);
            TextView userScore = cardView.findViewById(R.id.bestScoreId);

            userName.setText(user.getName()); // Set data to the TextView
            userScore.setText(String.valueOf(user.getScore())); // Set data to the TextView

            parentLayout.addView(cardView);
        }
    }

//    public void setDataList(List<User> dataList) {
//        this.bestUsersList = dataList;
//    }

}

