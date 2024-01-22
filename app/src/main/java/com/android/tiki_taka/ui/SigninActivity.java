package com.android.tiki_taka.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.utils.ValidatorSingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SigninActivity extends AppCompatActivity {
    TextInputLayout emailInputLayout;
    TextInputEditText emailEditText;
    TextInputLayout passInputLayout;
    TextInputEditText passEditText;
    ImageView signInButton;
    TextView forgotText;
    TextView deleteAccountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        emailInputLayout = findViewById(R.id.textInputLayout);
        emailEditText = findViewById(R.id.이메일);
        passInputLayout = findViewById(R.id.textInputLayout2);
        passEditText = findViewById(R.id.비밀번호);
        signInButton = findViewById(R.id.imageView5);
        forgotText = findViewById(R.id.textView8);
        deleteAccountText = findViewById(R.id.textView9);

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ValidatorSingleton.getInstance().isValidEmail(s.toString())) {
                    emailInputLayout.setError("이메일 형식이 아닙니다");
                } else {
                    emailInputLayout.setError(null); // 오류 메시지 제거
                }

            }

        });
        passEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ValidatorSingleton.getInstance().isValidPassword(s.toString())) {
                    passInputLayout.setError("비밀번호는 8자 이상 20자 이하, 영문과 숫자를 혼합하여 사용해야 합니다.");
                } else {
                    passInputLayout.setError(null);
                }
            }


        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });

    }
}