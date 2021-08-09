package be.trewep.mobile_dev_adv_exam_august;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class newRound extends Fragment {

    int firstround = 0;
    Button btn_next;
    TextView tv_name_player1;
    TextView tv_name_player2;
    TextView tv_score_player1;
    TextView tv_score_player2;
    gameViewModel gameViewModel;


    public newRound() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_round, container, false);
        btn_next = v.findViewById(R.id.buttonNextRound);
        tv_name_player1 = v.findViewById(R.id.textviewNamePlayer1);
        tv_name_player2 = v.findViewById(R.id.textviewNamePlayer2);
        tv_score_player1 = v.findViewById(R.id.textviewPointsPlayer1);
        tv_score_player2 = v.findViewById(R.id.textviewPointsPlayer2);
        gameViewModel = new ViewModelProvider(requireActivity()).get(gameViewModel.class);

        tv_name_player1.setText(gameViewModel.name_Player_1);
        tv_name_player2.setText(gameViewModel.name_Player_2);
        tv_score_player1.setText(String.valueOf(gameViewModel.scorePlayer1));
        tv_score_player2.setText(String.valueOf(gameViewModel.scorePlayer2));

        btn_next.setOnClickListener(view ->{
            ((MainActivity) requireActivity()).setRound(firstround);
        });

        return v;
    }
}