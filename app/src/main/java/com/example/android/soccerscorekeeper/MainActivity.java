package com.example.android.soccerscorekeeper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity keeps track two soccer team.
 */
public class MainActivity extends AppCompatActivity {

    // Array for TextView score both teams
    public static String[] AviewTypeTeamA = {"scoreGolTeamA", "scoreFoulTeamA", "scoreYellowCardTteamA", "scoreRedCardTeamA"};
    public static String[] AviewTypeTeamB = {"scoreGolTeamB", "scoreFoulTeamB", "scoreYellowCardTteamB", "scoreRedCardTeamB"};
    // Array for store Goal score both teams
    public static int[] AscoreTeamA = {0, 0, 0, 0};
    public static int[] AscoreTeamB = {0, 0, 0, 0};
    // Array color button effect
    public static int[] AcolorAnim = {0x4DFFFFFF, 0x4DFFEB3B, 0x4DFF0000};

    MediaPlayer mp;

    Animation animation;
    ValueAnimator colorAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Player sound effect.
     */
    public void playerSound(String sound) {
        Resources res = getResources();
        Integer resIdSound = res.getIdentifier(sound, "raw", this.getPackageName());
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mp = MediaPlayer.create(this, resIdSound);
        mp.start();
    }

    /**
     * Increase the score, start animation, show toast and play right sound effect.
     */
    public void addScoreTeams(View v) {
        int scoreTeam;
        String viewType;
        String stringToast;
        String stringTeam;
        String strNameButton = getResources().getResourceEntryName(v.getId());
        String[] arString = strNameButton.split("_");
        int indexArray = Integer.valueOf(arString[2]);

        // Get which team it is, add +1 at the kind of scoring clicked then display it.
        if (arString[1].equals("teamA")) {
            AscoreTeamA[indexArray] = AscoreTeamA[indexArray] + 1;
            scoreTeam = AscoreTeamA[indexArray];
            viewType = AviewTypeTeamA[indexArray];
            stringTeam = getString(R.string.name_teamA);
        } else {
            AscoreTeamB[indexArray] = AscoreTeamB[indexArray] + 1;
            scoreTeam = AscoreTeamB[indexArray];
            viewType = AviewTypeTeamB[indexArray];
            stringTeam = getString(R.string.name_teamB);
        }
        displayScore(scoreTeam, viewType);

        // Which type of animation show
        if (arString[0].equals("gol")) {
            animButton(v);
        } else {
            animBackgroundButton(v, AcolorAnim[indexArray - 1]);
        }

        // Now is time for sound effect
        playerSound(arString[0]);

        // Get the right string and show it with toast.
        String nameStringToCheck = "toast_" + arString[0];
        int checkString = getResources().getIdentifier(nameStringToCheck, "string", getPackageName());
        stringToast = getString(checkString, stringTeam);
        toastMessage(stringToast);
    }

    /**
     * Resets Arrays score for both teams and display them.
     */
    public void resetScore(View v) {
        for (int i = 0; i < AscoreTeamA.length; i++) {
            AscoreTeamA[i] = 0;
            AscoreTeamB[i] = 0;
            displayScore(AscoreTeamA[i], AviewTypeTeamA[i]);
            displayScore(AscoreTeamB[i], AviewTypeTeamB[i]);
        }
        String stringToast = getString(R.string.toast_reset);
        toastMessage(stringToast);
        stopAudioAnimation();
    }
    /*
     * Stop all effects
     */

    public void stopAudioAnimation(){
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!animation.hasEnded()) {
                animation.setDuration(0);
                animation.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (colorAnim.isRunning() ) {
                colorAnim.setDuration(0);
                colorAnim.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * View the score of the clicked type of the related team..
     */
    public void displayScore(int score, String vType) {
        int resourceId = getResources().getIdentifier(vType, "id", getPackageName());
        TextView golView = findViewById(resourceId);
        golView.setText(String.valueOf(score));
    }

    /**
     * Show effect for Goal button
     */
    public void animButton(final View v) {
        animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(10);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                v.setClickable(false);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setClickable(true);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
         v.startAnimation(animation);
    }

    /**
     * Show effect for Others buttons.
     */
    public void animBackgroundButton(final View v, int colorStart) {
        stopAudioAnimation();
        int colorEnd = 0x00FFFFFF;
        colorAnim = ObjectAnimator.ofInt(v, "backgroundColor", colorStart, colorEnd);
        colorAnim.setDuration(500);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(10);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        //colorAnim.start();
        colorAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator colorAnim) {
                v.setClickable(false);
            }
            @Override
            public void onAnimationEnd(Animator colorAnim) {
                colorAnim.removeListener(this);
                colorAnim.setDuration(0);
                ((ValueAnimator) colorAnim).reverse();
                v.setBackgroundResource(R.drawable.btn_back);
                v.setClickable(true);
            }

        });
        colorAnim.start();
    }

    /*
    * Show toast message.
     */
    public void toastMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

}