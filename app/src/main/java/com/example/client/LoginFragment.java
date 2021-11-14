package com.example.client;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.util.concurrent.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
    private EditText serverHost = null;
    private EditText serverPort = null;
    private EditText username = null;
    private EditText password = null;
    private EditText firstName = null;
    private EditText lastName = null;
    private EditText email = null;
    private RadioGroup genderGroup = null;
    private RadioButton maleChoice = null;
    private Button loginButton = null;
    private Button registerButton = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        serverHost = view.findViewById(R.id.hostField);
        serverPort = view.findViewById(R.id.portField);
        username = view.findViewById(R.id.usernameField);
        password = view.findViewById(R.id.passwordField);
        firstName = view.findViewById(R.id.firstNameField);
        lastName = view.findViewById(R.id.lastNameField);
        email = view.findViewById(R.id.emailField);
        genderGroup = view.findViewById(R.id.genderOption);
        maleChoice = view.findViewById(R.id.maleOption);


        loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login login = new Login(username.getText().toString(), password.getText().toString());
                login.checkLogin();
                webCall(login.getWebRequest(), serverHost.getText().toString(), serverPort.getText().toString(), "login");
            }
        });

        registerButton = view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register register = new Register(username.getText().toString(), password.getText().toString(),
                        firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(),
                        (maleChoice.isChecked()) ? "m" : "f");
                register.checkRegister();
                webCall(register.getWebRequest(), serverHost.getText().toString(), serverPort.getText().toString(), "register");
             }
        });

        registerButton.setClickable(false);
        registerButton.setBackgroundColor(Color.LTGRAY);
        registerButton.setTextColor(Color.WHITE);
        loginButton.setClickable(false);
        loginButton.setBackgroundColor(Color.LTGRAY);
        loginButton.setTextColor(Color.WHITE);


        serverHost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtons("login");
                updateButtons("register");
            }
        });

        serverPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtons("login");
                updateButtons("register");
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtons("login");
                updateButtons("register");
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtons("login");
                updateButtons("register");
            }
        });

        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtons("register");
            }
        });

        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtons("register");
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtons("register");
            }
        });

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateButtons("register");
            }
        });

        return view;
    }


    private void updateButtons(String loginType) {
        if (loginType.matches("login")) {
            if (serverHost.getText().toString().matches("") || serverPort.getText().toString().matches("") ||
                    username.getText().toString().matches("") || password.getText().toString().matches("")) {
                loginButton.setClickable(false);
                loginButton.setBackgroundColor(Color.LTGRAY);
                loginButton.setTextColor(Color.WHITE);
            }
            else {
                loginButton.setClickable(true);
                loginButton.setBackgroundColor(Color.GRAY);
                loginButton.setTextColor(Color.BLACK);
            }
        }
        else {
            if (serverHost.getText().toString().matches("") || serverPort.getText().toString().matches("") ||
                    username.getText().toString().matches("") || password.getText().toString().matches("") ||
                    firstName.getText().toString().matches("") || lastName.getText().toString().matches("") ||
                    email.getText().toString().matches("") || (genderGroup.getCheckedRadioButtonId() == -1)) {
                registerButton.setClickable(false);
                registerButton.setBackgroundColor(Color.LTGRAY);
                registerButton.setTextColor(Color.WHITE);
            }
            else {
                registerButton.setClickable(true);
                registerButton.setBackgroundColor(Color.GRAY);
                registerButton.setTextColor(Color.BLACK);
            }
        }
    }

    public void webCall(String json, String host, String port, String requestType) {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                String firstName = bundle.getString("first-name", "");
                String lastName = bundle.getString("last-name", "");
                if (firstName.matches("LOGIN FAILED") && lastName.matches("LOGIN FAILED")) {
                    Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Login Successful!" , Toast.LENGTH_SHORT).show();
                    loginSuccess();
                }
            }
        };
        LoginTask loginTask = new LoginTask(json, host, port, requestType,  handler);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(loginTask);
    }

    private void loginSuccess() {
        ((MainActivity) getActivity()).setLoggedIn(true);
    }

}
