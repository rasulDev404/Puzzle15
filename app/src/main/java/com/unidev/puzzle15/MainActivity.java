package com.unidev.puzzle15;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    boolean isStarted;
    ViewGroup group;
    Boolean firstGame = true;
    private TextView finish;
    private TextView reset;
    private TextView textStep;
    private Button[][] buttons;
    private ArrayList<Integer> numbers;
    private Coordinate emptyCoordinate;
    private int stepCount;
    private TextView title_best;
    private int bestStep = 0;
    private MediaPlayer mediaGame;
    private MediaPlayer mediaBtn;
    private SharedPreferences preferences;
    private Button winFinish;
    private Button winRestart;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadView();
        loadButtons();
        preferences = getSharedPreferences("app_data", Context.MODE_PRIVATE);
        editor = preferences.edit();
        isStarted = preferences.getBoolean("is_playing", false);
        if (preferences.getBoolean("music_game", true)) {
            mediaGame.start();
        }
        if (savedInstanceState == null) {
            if (isStarted) {
                stepCount = preferences.getInt("step", 0);
                bestStep = preferences.getInt("bestStep", 0);
                String numbersStr = preferences.getString("numbers", "1#2#3#4#5#6#7#8#9#10#11#12#13#14#15#n");
                String[] numbersList = numbersStr.split("#");
                ArrayList<String> numbers = new ArrayList<>();
                Log.d("TTT", "onCreate: " + Arrays.toString(numbersList));
                for (int i = 0; i < numbersList.length; i++) {
                    numbers.add(numbersList[i]);
                }
                textStep.setText(String.valueOf(stepCount));
                loadSaveNumbers(numbers);
            } else {
                mediaBtn.start();
                initNumbers();
                loadNumbersToButtons();
            }

        }

    }

    private void loadView() {
        title_best = findViewById(R.id.title_best_step);
        finish = findViewById(R.id.btn_start);
        reset = findViewById(R.id.btn_reset);
        textStep = findViewById(R.id.title_step);
        group = findViewById(R.id.groupButtons);
        reset.setOnClickListener(view -> onClickReset());
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mediaGame = MediaPlayer.create(this, R.raw.sound_game);
        mediaGame.setLooping(true);
        mediaBtn = MediaPlayer.create(this, R.raw.sound_btn);
    }


    private void loadButtons() {
        int count = group.getChildCount();
        int size = (int) Math.sqrt(count);
        buttons = new Button[size][size];
        for (int i = 0; i < count; i++) {
            View view = group.getChildAt(i);
            Button button = (Button) view;
            button.setOnClickListener(this::onButtonClick);
            int y = i / size;
            int x = i % size;
            buttons[y][x] = button;
            button.setTag(new Coordinate(x, y));
        }
    }

    private void initNumbers() {
        numbers = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            numbers.add(i);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        stepCount = savedInstanceState.getInt("score", 0);
        bestStep = savedInstanceState.getInt("bestStep", 0);
        isStarted = savedInstanceState.getBoolean("is_started");
        List<String> numberList = savedInstanceState.getStringArrayList("numbers");
        textStep.setText(String.valueOf(stepCount));
        loadSaveNumbers(numberList);

    }

    private void loadSaveNumbers(List<String> numberList) {
        numbers = new ArrayList<>();
        for (int i = 0; i < numberList.size(); i++) {
            if (numberList.get(i).equals("n")) {
                emptyCoordinate = new Coordinate(i % 4, i / 4);
                buttons[i / 4][i % 4].setText("");
            } else {
                buttons[i / 4][i % 4].setText(numberList.get(i));
                numbers.add(Integer.valueOf(numberList.get(i)));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("score", stepCount);
        outState.putInt("bestStep", bestStep);
        outState.putBoolean("is_started", isStarted);
        ArrayList<String> numbers = new ArrayList<>();
        for (int i = 0; i < group.getChildCount(); i++) {
            numbers.add(((Button) group.getChildAt(i)).getText().toString());
        }
        outState.putStringArrayList("numbers", numbers);
        super.onSaveInstanceState(outState);
    }

    private void loadNumbersToButtons() {
        shuffle();
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons.length; j++) {
                int index = i * 4 + j;
                if (index < 15) {
                    buttons[i][j].setText(String.valueOf(numbers.get(index)));
                }
            }
        }
        buttons[3][3].setText("");
        stepCount = 0;
        emptyCoordinate = new Coordinate(3, 3);
        isStarted = true;
//        timer.setBase(SystemClock.elapsedRealtime());
//        timer.start();

    }

    private void onButtonClick(View view) {
//        timer.start();
        Button button = (Button) view;
        Coordinate c = (Coordinate) button.getTag();
        int eY = emptyCoordinate.getY();
        int eX = emptyCoordinate.getX();
        int dY = Math.abs(c.getY() - eY);
        int dX = Math.abs(c.getX() - eX);
        if (dX + dY == 1) {
            stepCount++;
            textStep.setText(String.valueOf(stepCount));
            buttons[eY][eX].setText(button.getText());
            button.setText("");
            emptyCoordinate = c;
            if (isWin()) {
                showWinDialog();
            }
        }

    }
    private void showWinDialog(){
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_win,null);
        TextView userStepCount = view.findViewById(R.id.stepCount);
        winFinish = view.findViewById(R.id.dialog_btn_finish);
        winRestart = view.findViewById(R.id.dialog_btn_restart);
        if (stepCount < preferences.getInt("bestStep",0)) {
            bestStep = stepCount;
            title_best.setText(String.valueOf(bestStep));
        }
        dialog.setContentView(view);
        dialog.setCancelable(false);
        winRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                onClickReset();
            }
        });
        winFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    private boolean isWin() {
        if (!(emptyCoordinate.getX() == 3 && emptyCoordinate.getY() == 3)) return false;
        for (int i = 0; i < 15; i++) {
            String s = buttons[i / 4][i % 4].getText().toString();
            if (!s.equals(String.valueOf(i + 1))) return false;
        }
        return true;
    }

    private void onClickReset() {

        loadNumbersToButtons();
    }

    private void shuffle() {
        numbers.remove(Integer.valueOf(0));
        Collections.shuffle(numbers);
        if (isSolvable(numbers)) {
        } else {
            shuffle();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(isStarted){
//            timer.setBase(SystemClock.elapsedRealtime()+pauseTime);
//            timer.start();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //pauseTime = SystemClock.elapsedRealtime()-timer.getBase();
        editor.putInt("step", stepCount);
        editor.putInt("bestStep", bestStep);
        StringBuilder numberList = new StringBuilder();
        for (int i = 0; i < group.getChildCount() - 1; i++) {
            if (((Button) group.getChildAt(i)).getText().toString().equals("")) {
                numberList.append("n");
            }
            numberList.append(((Button) group.getChildAt(i)).getText().toString() + "#");
        }
        if (((Button) group.getChildAt(group.getChildCount() - 1)).getText().toString().equals("")) {
            numberList.append("n#");
        }
        numberList.append(((Button) group.getChildAt(15)).getText().toString());
        editor.putString("numbers", numberList.toString());
        editor.putBoolean("first_game", true);
        editor.putBoolean("is_playing", true);
//        editor.putBoolean("music_game",false);
        editor.putBoolean("music_btn", true);
        editor.apply();
//        timer.stop();
        mediaGame.stop();
        mediaBtn.stop();
    }

    public boolean isSolvable(ArrayList<Integer> puzzle) {
        numbers.add(0);
        int parity = 0;
        int gridWidth = (int) Math.sqrt(puzzle.size());
        int row = 0; // the current row we are on
        int blankRow = 0; // the row with the blank tile

        for (int i = 0; i < puzzle.size(); i++) {
            if (i % gridWidth == 0) { // advance to next row
                row++;
            }
            if (puzzle.get(i) == 0) { // the blank tile
                blankRow = row; // save the row on which encountered
                continue;
            }
            for (int j = i + 1; j < puzzle.size(); j++) {
                if (puzzle.get(i) > puzzle.get(j) && puzzle.get(j) != 0) {
                    parity++;
                }
            }
        }

        if (gridWidth % 2 == 0) { // even grid
            if (blankRow % 2 == 0) { // blank on odd row; counting from bottom
                return parity % 2 == 0;
            } else { // blank on even row; counting from bottom
                return parity % 2 != 0;
            }
        } else { // odd grid
            return parity % 2 == 0;
        }
    }
}