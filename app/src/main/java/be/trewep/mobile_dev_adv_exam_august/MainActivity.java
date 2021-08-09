package be.trewep.mobile_dev_adv_exam_august;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    // Variables MainActivity
    private final letterFrag letter_frag = new letterFrag();
    private final numberFrag number_frag = new numberFrag();
    private final endScreen ending_frag = new endScreen();
    private final startScreen start_frag = new startScreen();
    private final newRound round_frag = new newRound();
    gameViewModel gameViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the correct layout for mainactivity, set the viewmodel --> gameViewModel class
        setContentView(R.layout.activity_main);
        gameViewModel = new ViewModelProvider(this).get(gameViewModel.class);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.playerFrag, start_frag)
                .commit();

        gameViewModel.gameStarted.observe(this, started -> {
            if (started.equals(true)) {
                // see witch round playing
                gameViewModel.getRound().observe(this, round -> {

                    if (round.equals(0)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.playerFrag, number_frag)
                                .commit();
                    } else if (round.equals(1)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.playerFrag, letter_frag)
                                .commit();
                    } else if (round.equals(2)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.playerFrag, number_frag)
                                .commit();
                    } else if (round.equals(3)){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.playerFrag, round_frag)
                                .commit();
                    } else if(round.equals(4)){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.playerFrag, ending_frag)
                                .commit();
                    } else if(round.equals(5)){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.playerFrag, start_frag)
                                .commit();
                    }
                });
            }
        });
    }

    public void setRound(int num){
        gameViewModel.setRound(num);
    }
}