package be.trewep.mobile_dev_adv_exam_august;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import be.bluebanana.zakisolver.LetterSolver;

import static android.content.ContentValues.TAG;


public class letterFrag extends Fragment {

    // variables
    private static final int PERIOD = 1000;
    private static final int DELAY = 1000;
    private final int maxLetters = 9;
    public boolean result1;
    public boolean result2;
    public GridLayout cardGridLayout;
    public MutableLiveData<Integer> letter = new MutableLiveData<>();
    boolean resultPlayer1;
    boolean resultPlayer2;
    int checkActionToDo = 0;
    int firstRound = 0;
    int secondRound = 1;
    int thirdRound = 2;

    View v;
    letterViewModel letterViewModel;
    gameViewModel gameViewModel;
    LetterSolver letSolver = new LetterSolver();

    String text1;
    String text2;
    String resultString = "Possible solutions were:";
    TextView tv_results;
    EditText editText1;
    EditText editText2;
    Button btn_Check;
    Button btn_consonant;
    Button btn_vowel;
    ProgressBar pb;

    Timer t = new Timer();
    MutableLiveData<Integer> ronde;
    ArrayList<String> solutions = new ArrayList<>();

    public letterFrag() {
        super(R.layout.fragment_letter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // set view
        v = inflater.inflate(R.layout.fragment_letter, container, false);
        gameViewModel = new ViewModelProvider(requireActivity()).get(gameViewModel.class);
        letterViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(letterViewModel.class);
        letter.setValue(0);
        editText1 = v.findViewById(R.id.edittextPlayer1);
        editText2 = v.findViewById(R.id.edittextPlayer2);
        letSolver.loadDictionary(requireActivity(), R.raw.dictionary);
        tv_results = v.findViewById(R.id.textviewResults);
        tv_results.setText("");
        btn_Check = v.findViewById(R.id.buttonCheck);
        btn_Check.setVisibility(View.INVISIBLE);
        btn_consonant = v.findViewById(R.id.buttonConsonant);
        btn_vowel = v.findViewById(R.id.buttonVowel);
        pb = requireActivity().findViewById(R.id.progressBar);

        TextView namePlayer1 = v.findViewById(R.id.textviewPlayer1);
        TextView namePlayer2 = v.findViewById(R.id.textviewPlayer2);
        namePlayer1.setText(gameViewModel.name_Player_1);
        namePlayer2.setText(gameViewModel.name_Player_2);

        //set the correct textviews and player scores
        cardGridLayout = v.findViewById(R.id.gridlayout);
        TextView tv_player1 = v.findViewById(R.id.textviewScorePlayer1);
        TextView tv_player2 = v.findViewById(R.id.textviewScorePlayer2);
        tv_player1.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer1));
        tv_player2.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer2));

        // onclicklistener on buttons
        btn_vowel.setOnClickListener(view -> letterViewModel.pickVowel());
        btn_consonant.setOnClickListener(view -> letterViewModel.pickConsonant());
        letterViewModel.results.observe(getViewLifecycleOwner(), strings -> {
            StringBuilder tempString = new StringBuilder();
            resultString += "\n";
            for (int i=0; i < strings.size()-1; i++){
                tempString.append(strings.get(i)).append(", ");
            }
            resultString += tempString + strings.get(strings.size()-1);
        });


        btn_Check.setOnClickListener(view -> {
            if (checkActionToDo == 0){
                text1 = String.valueOf(editText1.getText());
                text2 = String.valueOf(editText2.getText());

                // check the given word to see if it exists and only with letters from the cards
                resultPlayer1 = letterViewModel.checkText(text1, result1);
                resultPlayer2 = letterViewModel.checkText(text2, result2);

                // continue only if both players have a valid solution
                if (resultPlayer1 && resultPlayer2){
                    if (text1.length() == text2.length()){
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.draw), Toast.LENGTH_SHORT).show());
                        gameViewModel.winPlayer1();
                        gameViewModel.winPlayer2();
                    }

                    else if (text1.length() > text2.length()){
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player1_win), Toast.LENGTH_SHORT).show());
                        gameViewModel.winPlayer1();
                    }
                    else{
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player2_win), Toast.LENGTH_SHORT).show());
                        gameViewModel.winPlayer2();
                    }
                }

                // if one player his input is not a word, the other one wins
                else if (resultPlayer1 && !resultPlayer2){
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player1_win), Toast.LENGTH_SHORT).show());
                    gameViewModel.winPlayer1();
                }

                else if (!resultPlayer1 && resultPlayer2){
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player2_win), Toast.LENGTH_SHORT).show());
                    gameViewModel.winPlayer2();
                }

                // no points = no correct answer
                else{
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.no_winner), Toast.LENGTH_SHORT).show());
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
                gameViewModel.setGame(gameViewModel.amountOfRounds++);
                ronde = gameViewModel.getRound();
                if (Objects.requireNonNull(ronde.getValue()).equals(firstRound)){
                    ((MainActivity) requireActivity()).setRound(secondRound);
                }
                else if(ronde.getValue().equals(secondRound)){
                    ((MainActivity) requireActivity()).setRound(thirdRound);
                }
                else {
                    ((MainActivity) requireActivity()).setRound(firstRound);
                }
            }
        });

        // check if the letterArray had 9 letters
        // if not so --> new card
        // if so --> start a timer
        letterViewModel.getLetters().observe(getViewLifecycleOwner(), letterArray -> {
            if (letterArray.size() > 0 && letterArray.size() <= maxLetters){
                View cardView = getLayoutInflater().inflate(R.layout.card_layout, cardGridLayout, false);
                TextView tv = cardView.findViewById(R.id.textviewCardText);
                tv.setText(String.valueOf(letterArray.get(letterArray.size()-1)));
                cardGridLayout.addView(cardView);
            }

            if (letterArray.size() == maxLetters){
                btn_consonant.setVisibility(View.INVISIBLE);
                btn_vowel.setVisibility(View.INVISIBLE);
                startTimer();
                solve(letterArray);
            }
        });

        // set progressbar
        pb.setMax((gameViewModel.timerDuration/1000) -1); //ms to s
        letter.observe(requireActivity() , pb::setProgress);
        return v;
    }


    public void startTimer() {
        // start timer
        long startTime = System.currentTimeMillis();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - startTime <= gameViewModel.timerDuration) {
                    assert letter.getValue() != null;
                    letter.postValue(letter.getValue() + 1);
                } else {

                    //lambda function instead of new runnable
                    btn_Check.getHandler().post(() -> btn_Check.setVisibility(View.VISIBLE));
                    cancel();
                }
            }
        }, DELAY, PERIOD);
    }


    @Override
    public void onStart() {
        // delete value of textfields
        super.onStart();
        editText1.setText("");
        editText2.setText("");
        tv_results.setText("");
        checkActionToDo = 0;
        resultString = "Possible solutions were: ";
        btn_consonant.setVisibility(View.VISIBLE);
        btn_vowel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        // delete solution
        super.onDestroyView();
        solutions.clear();
        letterViewModel.clearLetter();
    }

    public void solve (ArrayList<Character> letters) {
        // set up solver
        letSolver.setInput(letters, results -> {
            if (results.size() == 0) {
                Log.d(TAG, "solve: No solutions found.");
                return;
            }
            results.stream()
                    .limit(3)
                    .forEach(result -> solutions.add(result));
            letterViewModel.results.postValue(solutions);
        });

        // Start the solver
        new Thread(letSolver).start();
    }
}