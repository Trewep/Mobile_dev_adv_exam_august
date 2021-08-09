package be.trewep.mobile_dev_adv_exam_august;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import be.bluebanana.zakisolver.NumberSolver;

import static android.content.ContentValues.TAG;


public class numberFrag extends Fragment {

    // Variables viewModel
    View v;
    gameViewModel gameViewModel;
    numberViewModel numberViewModel;

    // Variables lay out
    Button btn_Check;
    Button btn_High;
    Button btn_Low;
    TextView tv_results;
    TextView namePlayer1;
    TextView namePlayer2;
    TextView tv_player1;
    TextView tv_player2;
    EditText editText1;
    EditText editText2;
    ProgressBar pb;

    // Variables local use
    public GridLayout cardGridLayout;
    public MutableLiveData<Integer> number = new MutableLiveData<>();
    int num_player1;
    int num_player2;
    int targetNum;
    int checkActionToDo;
    int randomNum;
    int firstRound = 0;
    int secondRound = 1;
    int thirdRound = 2;
    int overview = 3;
    int endingScreen = 4;
    int randomNumLimit = 900; //Max rounds
    int DELAY = 1000;
    int PERIOD = 1000;
    String resultString;
    Timer t = new Timer();
    MutableLiveData<Integer> ronde;
    Random random = new Random();
    NumberSolver numSolver = new NumberSolver();
    ArrayList<String> solutions = new ArrayList<>();

    public numberFrag() {
        // Required empty public constructor
        super(R.layout.fragment_number);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //set view
        v = inflater.inflate(R.layout.fragment_number, container, false);
        number.setValue(0);

        // set cardlayout
        cardGridLayout = v.findViewById(R.id.gridlayout);
        gameViewModel = new ViewModelProvider(requireActivity()).get(gameViewModel.class);
        numberViewModel = new ViewModelProvider(requireActivity()).get(numberViewModel.class);
        editText1 = v.findViewById(R.id.edittextPlayer1);
        editText2 = v.findViewById(R.id.edittextPlayer2);
        pb = requireActivity().findViewById(R.id.progressBar);
        tv_results = v.findViewById(R.id.textviewResults);


        namePlayer1 = v.findViewById(R.id.textviewPlayer1);
        namePlayer2 = v.findViewById(R.id.textviewPlayer2);
        namePlayer1.setText(gameViewModel.name_Player_1);
        namePlayer2.setText(gameViewModel.name_Player_2);

        btn_Check = v.findViewById(R.id.buttonCheck);
        btn_High = v.findViewById(R.id.buttonHighNumber);
        btn_Low = v.findViewById(R.id.buttonLowNumber);

        // set the correct textviews and player scores
        tv_player1 = v.findViewById(R.id.textviewScorePlayer1);
        tv_player2 = v.findViewById(R.id.textviewScorePlayer2);
        tv_player1.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer1));
        tv_player2.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer2));


        // onclicklistener on buttons
        btn_Low.setOnClickListener(view -> numberViewModel.pickLowNumber());
        btn_High.setOnClickListener(view -> numberViewModel.pickHighNumber());
        numberViewModel.results.observe(getViewLifecycleOwner(), strings -> strings.forEach(string-> resultString += tv_results.getText() + "\n" + string));

        btn_Check.setOnClickListener(view -> {
            if(checkActionToDo == 0){
                // if one player his input is not a number, the other one wins
                if (editText1.getText().length() == 0 && editText2.getText().length() > 0){
                    gameViewModel.scorePlayer2++;
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player2_win), Toast.LENGTH_LONG).show());
                }

                else if (editText1.getText().length() > 0 && editText2.getText().length() == 0){
                    gameViewModel.scorePlayer1++;
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player1_win), Toast.LENGTH_LONG).show());
                }

                else if (editText1.getText().length() == 0 && editText2.getText().length() == 0){
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.no_winner), Toast.LENGTH_LONG).show());
                }

                else{
                    num_player1 = Integer.parseInt(String.valueOf(editText1.getText()));
                    num_player2 = Integer.parseInt(String.valueOf(editText2.getText()));
                    int result = gameViewModel.calculateDifference(num_player1, num_player2, targetNum);

                    // after filling in, check witch one is the winner
                    if (result == 0){
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player1_win), Toast.LENGTH_LONG).show());
                    }
                    else if (result == 1){
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player2_win), Toast.LENGTH_LONG).show());
                    }
                    else {
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.draw), Toast.LENGTH_LONG).show());
                    }
                }
                checkActionToDo++;
                btn_Check.setText(R.string.possible_solutions);
            }

            else if(checkActionToDo == 1){
                tv_results.setText(resultString);
                checkActionToDo++;
                btn_Check.setText(R.string.next_round);
            }
            else if(checkActionToDo == 2){
                // last game, set end screen
                ronde = gameViewModel.getRound();
                if (Objects.equals(ronde.getValue(), firstRound)){
                    ((MainActivity) requireActivity()).setRound(secondRound);
                }
                else if(ronde.getValue().equals(secondRound)){
                    ((MainActivity) requireActivity()).setRound(thirdRound);
                }
                else if(ronde.getValue().equals(thirdRound)) {
                    assert gameViewModel.numberOfGames.getValue() != null;
                    if(gameViewModel.amountOfRounds < gameViewModel.numberOfGames.getValue()){
                        ((MainActivity) requireActivity()).setRound(overview);
                        gameViewModel.amountOfRounds++;
                    }
                    else if(gameViewModel.amountOfRounds.equals(gameViewModel.numberOfGames.getValue())){
                        ((MainActivity) requireActivity()).setRound(endingScreen);
                    }
                }
            }
        });


        // observe the cards
        // if there are not 6 cards, draw a card
        // if the are 6 cards, draw a number and start timer
        numberViewModel.getNumbers().observe(getViewLifecycleOwner(), numberArray -> {
            if (numberArray.size() > 0 && numberArray.size() <= 6){
                View cardView = getLayoutInflater().inflate(R.layout.card_layout, cardGridLayout, false);
                TextView tv = cardView.findViewById(R.id.textviewCardText);
                tv.setText(String.valueOf(numberArray.get(numberArray.size()-1)));
                cardGridLayout.addView(cardView);
            }

            if (numberArray.size() == 6){
                TextView tv = v.findViewById(R.id.textviewRandom);
                //generate random number
                randomNum = random.nextInt(randomNumLimit)+100;
                targetNum = randomNum;
                tv.setText(String.format(Locale.ENGLISH, "Number to reach: %d", targetNum));
                startTimer(requireView());
                solve(numberArray, randomNum);
                btn_High.setVisibility(View.INVISIBLE);
                btn_Low.setVisibility(View.INVISIBLE);
            }
        });

        // update the progressbar sync with timer
        pb.setMax((gameViewModel.timerDuration / 1000)-1); // ms to s
        number.observe(requireActivity() , pb::setProgress);
        return v;
    }

    public void startTimer(View w) {
        // start a timer, update the timer every second
        long startTime = System.currentTimeMillis();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - startTime <= gameViewModel.timerDuration) {
                    assert number.getValue() != null;
                    number.postValue(number.getValue() + 1);
                }
                else {
                    btn_Check.getHandler().post(new Runnable() {
                        public void run() {
                            btn_Check.setVisibility(View.VISIBLE);
                        }
                    });
                    cancel();
                }
            }
        }, DELAY, PERIOD);
    }

    @Override
    public void onStart() {
        // make the 2 textfields empty at onstart
        super.onStart();
        btn_Check.setText("Check");
        btn_Check.setVisibility(View.INVISIBLE);
        btn_High.setVisibility(View.VISIBLE);
        btn_Low.setVisibility(View.VISIBLE);
        editText1.setText("");
        editText2.setText("");
        tv_results.setText("");
        checkActionToDo = 0;
        resultString = "Possible solutions were: ";
    }

    @Override
    public void onDestroyView() {
        // make the 2 textfields empty at ondestroyview
        numberViewModel numberViewModel = new ViewModelProvider(requireActivity()).get(numberViewModel.class);
        super.onDestroyView();
        numberViewModel.clearNumber();
        solutions.clear();
    }

    public void solve (ArrayList<Integer> numbers, int target) {
        // set up the solver
        numSolver.setInput(numbers, target, results -> {
            if (results.size() == 0) {
                Log.d(TAG, "solver: No solutions found.");
                return;
            }
            results.stream()
                    .limit(3)
                    .forEach(result -> {
                        solutions.add(result);
                    });
            numberViewModel.results.postValue(solutions);
        });

        // Start the solver
        new Thread(numSolver).start();
    }

}