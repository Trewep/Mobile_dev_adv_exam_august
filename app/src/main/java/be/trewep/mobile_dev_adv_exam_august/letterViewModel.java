package be.trewep.mobile_dev_adv_exam_august;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class letterViewModel extends AndroidViewModel {
    // variables
    public MutableLiveData<ArrayList<Character>> letterArray;
    InputStream is;
    int randomLetter = 26; // choose a number thats equals to the alphabet
    int numberToAdd = 97; // number of lowercase a in ASCII table
    int minWordLength = 2; // minimum chars of a word
    MutableLiveData<ArrayList<String>> results = new MutableLiveData<>();

    public letterViewModel(@NonNull Application application) {
        super(application);
    }

    // check if the letterarray is not empty and return
    public MutableLiveData<ArrayList<Character>> getLetters(){
        if (letterArray == null){
            letterArray = new MutableLiveData<>();
            letterArray.setValue(new ArrayList<>());
        }
        return letterArray;
    }

    // pick a random letter in the ascii table
    public char pickALetter() {
        Random random = new Random();
        int ascii = random.nextInt(randomLetter) + numberToAdd; // lowercase 'a'
        return (char)ascii;
    }

    // vowel?
    public boolean isVowel (char c) {
        char[] vowels = {'a', 'e', 'i', 'o', 'u'};

        for (char v: vowels) {
            if (v == c) return true;
        }
        return false;
    }

    // consonant?
    public boolean isConsonant (char c) {
        return !isVowel(c);
    }


    // add vowel to letterarray
    public void pickVowel() {
        ArrayList<Character> list = getLetters().getValue();
        assert list != null;
        if (list.size() < 9){
            char c;
            do {
                c = pickALetter();
            } while (!isVowel(c));

            list.add(c);
            letterArray.setValue(list);
        }
    }

    // add consonant to letterarray
    public void pickConsonant() {
        ArrayList<Character> list = getLetters().getValue();
        assert list != null;
        if (list.size() < 9){
            char c;
            do {
                c = pickALetter();
            } while (!isConsonant(c));
            list.add(c);
            letterArray.setValue(list);
        }
    }

    // delete letterarray
    public void clearLetter(){
        ArrayList<Character> list = getLetters().getValue();
        assert list != null;
        list.clear();
        letterArray.setValue(list);
    }

    //check string to make sure it exists, and can be formed with the characters from the letterarray
    public boolean checkText(String userText, boolean res) {
        try {
            // a word must contani at least 2 letters, if not it isn't a word
            if (userText.length() < minWordLength) {
                res = false;
            }
            else{
                //use wordlist to see how many letters from the letterarray are in the given string
                char [] wordArray = userText.toCharArray();
                ArrayList<Character> tempList = letterArray.getValue();

                ArrayList<Character> wordList = new ArrayList<>();
                for (char c:wordArray){
                    wordList.add(c);
                }
                assert tempList != null;
                wordList.retainAll(tempList);

                //use the correct file to check the string for realness (depending on how long it is)
                int fileToOpen = getApplication().getResources().getIdentifier("raw/filter" + String.valueOf(userText.length()), null, getApplication().getApplicationContext().getPackageName());

                is = this.getApplication().getApplicationContext().getResources().openRawResource(fileToOpen);
                // if the string contains letters not from the letterarray, it isn't valid
                if (wordList.size() < userText.length()){
                    //new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplication().getApplicationContext(), getApplication().getApplicationContext().getResources().getString(R.string.invalid), Toast.LENGTH_SHORT).show());
                    res = false;
                }

                // check the file with words for the string to see if it exists
                byte[] buffer = new byte[is.available()];
                while (is.read(buffer) != -1){
                    String jsontext = new String(buffer);
                    res = jsontext.contains(userText);
                }
            }

        } catch (Exception e) {
            Log.e("TAG", "" + e.toString());
        }
        return res;
    }
}