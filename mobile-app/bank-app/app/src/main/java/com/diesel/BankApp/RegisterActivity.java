package com.diesel.BankApp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class RegisterActivity extends AppCompatActivity {

    //Declaration EditTexts
    EditText editTextID;
    EditText editTextUsername;
    EditText editTextPassword;

    //Declaration TextInputLayout
    TextInputLayout textInputLayoutID;
    TextInputLayout textInputLayoutUsername;
    TextInputLayout textInputLayoutPassword;

    //Declaration Button
    Button buttonRegister;

    //Declaration SqliteHelper
    SqliteHelper sqliteHelper;

    //Declarando String
    private String textoSalida;
    private String clave = "Diesel";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sqliteHelper = new SqliteHelper(this);
        initTextViewLogin();
        initViews();
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    String ID = editTextID.getText().toString();
                    String Username = editTextUsername.getText().toString();
                    String Password = editTextPassword.getText().toString();

                    //Check in the database is there any user associated with  this id, username
                    if (!sqliteHelper.doesExist(ID, Username)) {

                        //Si no existe
                        try{
                            textoSalida = encriptar(Password, clave);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                        //does not exist now add new user to database
                        sqliteHelper.addUser(new User(ID, Username, "null", textoSalida, "null"));
                        Snackbar.make(buttonRegister, "User created successfully! Please Login ", Snackbar.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, Snackbar.LENGTH_LONG);
                    }else {

                        //Email exists with email input provided so show error user already exist
                        Snackbar.make(buttonRegister, "User already exists with same id or username ", Snackbar.LENGTH_LONG).show();
                    }


                }
            }
        });
    }

    private String encriptar(String datos, String password) throws Exception{
        SecretKeySpec secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        byte[] datosEncriptadosBytes = cipher.doFinal(datos.getBytes());
        String datosEncriptadosString = Base64.encodeToString(datosEncriptadosBytes, Base64.DEFAULT);
        return datosEncriptadosString;
    }

    private SecretKeySpec generateKey(String password) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }

    //this method used to set Login TextView click event
    private void initTextViewLogin() {
        TextView textViewLogin = (TextView) findViewById(R.id.textViewLogin);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //this method is used to connect XML views to its Objects
    private void initViews() {
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextID = (EditText) findViewById(R.id.editTextID);
        textInputLayoutUsername = (TextInputLayout) findViewById(R.id.textInputLayoutUsername);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutID = (TextInputLayout) findViewById(R.id.textInputLayoutID);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

    }

    //This method is used to validate input given by user
    public boolean validate() {
        boolean valid = false;

        //Get values from EditText fields
        String ID = editTextID.getText().toString();
        String Username = editTextUsername.getText().toString();
        String Password = editTextPassword.getText().toString();

        //Handling validation for id field
        if (ID.isEmpty()) {
            valid = false;
            textInputLayoutID.setError("Please enter valid id!");
            return valid;
        } else {
            if (ID.matches("\\d+")) {
                valid = true;
                textInputLayoutID.setError(null);
            } else {
                valid = false;
                textInputLayoutID.setError("ID must be numeric!");
                return valid;
            }

            if (ID.length() > 5) {
                valid = true;
                textInputLayoutID.setError(null);
            } else {
                valid = false;
                textInputLayoutID.setError("Id is too short!");
                return valid;
            }

        }

        //Handling validation for Username field
        if (Username.isEmpty()) {
            valid = false;
            textInputLayoutUsername.setError("Please enter valid username!");
            return valid;
        } else {
            if (Username.length() > 5) {
                valid = true;
                textInputLayoutUsername.setError(null);
            } else {
                valid = false;
                textInputLayoutUsername.setError("Username is too short!");
                return valid;
            }
        }

        //Handling validation for Password field
        if (Password.isEmpty()) {
            valid = false;
            textInputLayoutPassword.setError("Please enter valid password!");
            return valid;
        } else {
            if (Password.matches("\\d+")) {
                valid = true;
                textInputLayoutPassword.setError(null);
            } else {
                valid = false;
                textInputLayoutPassword.setError("Password must be numeric!");
                return valid;
            }

            if (Password.length() == 6) {
                valid = true;
                textInputLayoutPassword.setError(null);
            } else {
                valid = false;
                textInputLayoutPassword.setError("Password length must be 6!");
                return valid;
            }

        }


        return valid;
    }
}