package be.trewep.mobile_dev_adv_exam_august;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class gameViewModel extends ViewModel {
    // needed variables for game
    public String name_Player_1;
    public String name_Player_2;
    public Integer gameNum = 0;
    public Integer amountOfRounds = 1;
    public MutableLiveData<Integer> round;
    public MutableLiveData<Integer> numberOfGames = new MutableLiveData<>(1); //amound games
    public MutableLiveData<Boolean> gameStarted= new MutableLiveData<>(false);
    public int scorePlayer1 = 0;
    public int scorePlayer2 = 0;
    public int timerDuration = 30000; // timer duration in milliseconds
    public int player1Difference;
    public int player2Difference;
    public int player1Wins = 0;
    public int player2Wins = 1;
    public int draw = 2;

    public MutableLiveData<Integer> getRound() {
        // get the current round
        if (round == null) {
            round = new MutableLiveData<Integer> ();
            round.postValue(gameNum);
        }
        return round;
    }

    public int calculateDifference(int num1, int num2, int target){
        // check winner witch is closer
        player1Difference = Math.abs(num1- target);
        player2Difference = Math.abs(num2- target);
        if (player1Difference < player2Difference){
            scorePlayer1++;
            return player1Wins;
        }

        if (player1Difference > player2Difference){
            scorePlayer2++;
            return player2Wins;
        }
        return draw;
    }

    // add score
    public void winPlayer1(){
        scorePlayer1++;
    }

    public void winPlayer2(){
        scorePlayer2++;
    }

    // change round
    public void setRound(int num){
        round.postValue(num);
        getRound();
    }

    public void setGame(int num){
        amountOfRounds = num;
    }
}
