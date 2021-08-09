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
import android.widget.EditText;
import android.widget.NumberPicker;

import org.jetbrains.annotations.NotNull;

public class startScreen extends Fragment {
    // ↓ variables for layout elements
    NumberPicker np;
    gameViewModel gamestate_viewmodel;
    Button btn_ready;
    EditText et_player1;
    EditText et_player2;

    // ↓ variables for locally used functions
    int gamesToPlay;

    public startScreen() {
        // Required empty public constructor
        super(R.layout.fragment_start_screen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_start_screen, container, false);
        gamestate_viewmodel = new ViewModelProvider(requireActivity()).get(gameViewModel.class);
        btn_ready = v.findViewById(R.id.buttonReady);
        et_player1 = v.findViewById(R.id.edittextNamePlayer1);
        et_player2 = v.findViewById(R.id.edittextNamePlayer2);

        np = v.findViewById(R.id.numberPickerRound);
        np.setEnabled(true);
        np.setMaxValue(10);
        np.setMinValue(1);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        np.setOnValueChangedListener((picker, oldVal, newVal) -> {
            gamesToPlay =  picker.getValue();
            gamestate_viewmodel.numberOfGames.postValue(gamesToPlay);
        });

        btn_ready.setOnClickListener(v -> {
            gamestate_viewmodel.name_Player_1 = et_player1.getText().toString();
            gamestate_viewmodel.name_Player_2 = et_player2.getText().toString();

            if(et_player1.getText().toString().equals("")){
                gamestate_viewmodel.name_Player_1 = "Joris";
            }
            if(et_player2.getText().toString().equals("")){
                gamestate_viewmodel.name_Player_2 = "Koen";
            }

            gamestate_viewmodel.gameStarted.setValue(true);
        });

    }
}