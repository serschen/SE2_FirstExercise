package com.example.se2_firstexercise;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnSubmit;
    EditText txtNumber;
    TextView txtResponse;
    Button btnAscii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllViews();
        registrateEventHandlers();
    }

    private void getAllViews(){
        btnSubmit = findViewById(R.id.btnSubmit);
        txtNumber = findViewById(R.id.txtNumber);
        txtResponse = findViewById(R.id.txtResponse);
        btnAscii = findViewById(R.id.btnAscii);
    }

    private void registrateEventHandlers(){
        btnSubmit.setOnClickListener(this);
        btnAscii.setOnClickListener(this);
    }

    private void contactServer(int matrikel){
        new Thread(new Runnable(){
            public void run(){
            Socket client;
            PrintWriter out;
            BufferedReader in;

            try{
                client = new Socket("se2-isys.aau.at", 53212);
                out = new PrintWriter(client.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                out.println(matrikel);
                final String msg = in.readLine();

                if(msg != null){
                    setTxtResponse(msg);
                }
                client.close();
                } catch(UnknownHostException e) {
                    setTxtResponse(e.toString());

                } catch(IOException e) {
                    setTxtResponse(e.toString());
                }
            }
        }).start();
    }

    public void setTxtResponse(String msg){
        if(msg != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtResponse.setText(msg);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnSubmit){
            String number = txtNumber.getText().toString();
            if(!number.equals("")){
                try {
                    int matrikel = Integer.parseInt(String.valueOf(number));
                    contactServer(matrikel);
                }
                catch(Exception e){
                    txtResponse.setText(e.toString());
                }
            }
            else{
                txtResponse.setText("Bitte die Matrikelnummer eingeben");
            }
        }else if(v == btnAscii){
            String number = txtNumber.getText().toString();
            StringBuilder newNumber = new StringBuilder();

            for(int i=0; i < number.length(); i++){
                if(i%2 != 0 && number.charAt(i) != '0'){
                    newNumber.append((char)(number.charAt(i) + 48));
                }
                else{
                    newNumber.append(number.charAt(i));
                }
            }

            txtResponse.setText(newNumber.toString());
        }
    }
}