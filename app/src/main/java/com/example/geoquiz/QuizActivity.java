package com.example.geoquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    public static final String KEY_IS_CHEATER = "cheater";
    public static final String KEY_ENABLE_NEXT_BUTTON = "nextButton";
    public static final String KEY_HINT = "hint";

    public static final int REQUEST_CODE_CHEAT = 0;

    @BindView(R.id.question_text_view)
    TextView mQuestionTextView;

    @BindView(R.id.true_button)
    Button mTrueButton;

    @BindView(R.id.false_button)
    Button mFalseButton;

    @BindView(R.id.next_button)
    Button mNextButton;

    @BindView(R.id.cheat_button)
    Button mCheatButton;

    @BindView(R.id.hint_text_view)
    TextView mHintTextView;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    private boolean mEnableNextButton = false;
    private int mHint = 3;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER, false);
            mEnableNextButton = savedInstanceState.getBoolean(KEY_ENABLE_NEXT_BUTTON, false);
            mHint = savedInstanceState.getInt(KEY_HINT, 0);
        }

        mHintTextView.setText(String.valueOf(mHint));

        mQuestionTextView.setOnClickListener(v -> {
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
            updateQuestion();});

        mTrueButton.setOnClickListener(v -> {
            checkAnswer(true);
            enableNextButton(!mEnableNextButton);
        });

        mFalseButton.setOnClickListener(v -> {
            checkAnswer(false);
            enableNextButton(!mEnableNextButton);
        });

        enableNextButton(mEnableNextButton);
        mNextButton.setOnClickListener(v -> {
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
            mIsCheater = false;
            updateQuestion();
            enableNextButton(!mEnableNextButton);
        });

        isHintButtonEnable();
        mCheatButton.setOnClickListener(v -> {
            mHint--;
            isHintButtonEnable();
            mHintTextView.setText(String.valueOf(mHint));
            Log.d(TAG, "Hint:" + mHint);
            boolean answerIsTRue = mQuestionBank[mCurrentIndex].isAnswerTrue();
            Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTRue);
            startActivityForResult(intent, REQUEST_CODE_CHEAT);
        });

        updateQuestion();
    }

    private void isHintButtonEnable() {
        if (mHint <= 0) {
            mCheatButton.setEnabled(false);
        }
    }

    private void enableNextButton(boolean enable) {
        mNextButton.setEnabled(enable);
        mEnableNextButton = enable;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSavedInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_IS_CHEATER, mIsCheater);
        savedInstanceState.putBoolean(KEY_ENABLE_NEXT_BUTTON, mEnableNextButton);
        savedInstanceState.putInt(KEY_HINT, mHint);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId;

        if(mIsCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
                    .show();
    }
}
