package be.trewep.mobile_dev_adv_exam_august;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class endScreen extends Fragment {

    View v;
    TextView player;
    TextView score;
    gameViewModel gameViewModel;
    Button btn_play_again;
    int startScreen = 5;

    public endScreen() {
        // Required empty public constructor
        super(R.layout.fragment_number);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // set view
        v = inflater.inflate(R.layout.fragment_end_screen, container, false);
        gameViewModel = new ViewModelProvider(requireActivity()).get(gameViewModel.class);

        btn_play_again = v.findViewById(R.id.buttonPlayAgain);
        player = v.findViewById(R.id.textviewPlayerWinner);
        score = v.findViewById(R.id.textviewScore);

        if (gameViewModel.scorePlayer1 > gameViewModel.scorePlayer2){
            player.setText(gameViewModel.name_Player_1);
            score.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer1));
        }
        else{
            player.setText(gameViewModel.name_Player_2);
            score.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer2));
        }

        btn_play_again.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).setRound(startScreen);
        });
        return v;
    }
}