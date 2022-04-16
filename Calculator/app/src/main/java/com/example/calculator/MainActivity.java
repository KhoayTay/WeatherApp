package com.example.calculator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.SpannableStringBuilder;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.calculator.databinding.ActivityMainBinding;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private EditText display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        display = findViewById(R.id.display);
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getString(R.string.display).equals(display.getText().toString())) {
                    display.setText("");
                }
            }
        });

//        setSupportActionBar(binding.toolbar);
//
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void updateText(String addString) {
        String oldString = display.getText().toString();
        int cursorPosition = display.getSelectionStart();
        String leftString = oldString.substring(0, cursorPosition);
        String rightString = oldString.substring(cursorPosition);
        if (getString(R.string.display).equals(display.getText().toString())){
            display.setText(addString);
            display.setSelection(cursorPosition + 1);
        } else {
            display.setText(String.format("%s%s%s", leftString, addString, rightString));
            display.setSelection(cursorPosition + 1);
        }
    }

    public void num0Button(View view) {
        updateText("0");
    }

    public void num1Button(View view) {
        updateText("1");
    }

    public void num2Button(View view) {
        updateText("2");
    }

    public void num3Button(View view) {
        updateText("3");
    }

    public void num4Button(View view) {
        updateText("4");
    }

    public void num5Button(View view) {
        updateText("5");
    }

    public void num6Button(View view) {
        updateText("6");
    }

    public void num7Button(View view) {
        updateText("7");
    }

    public void num8Button(View view) {
        updateText("8");
    }

    public void num9Button(View view) {
        updateText("9");
    }

    public void pointButton(View view) {
        updateText(".");
    }

    public void addButton(View view) {
        updateText("+");
    }

    public void subtract0Button(View view) {
        updateText("-");
    }

    public void multiplyButton(View view) {
        updateText("×");
    }

    public void divideButton(View view) {
        updateText("÷");
    }

    public void equalButton(View view) {
        Double result = null;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");

        String readText = display.getText().toString();
        readText = readText.replaceAll("×", "*");
        readText = readText.replaceAll("÷", "/");

        try {
            result = (double)engine.eval(readText);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        if (result != null) {
            display.setText(String.valueOf(result.doubleValue()));
            display.setSelection(String.valueOf(result.doubleValue()).length());
        }
    }

    public void parenthesesButton(View view) {
        int cursorPosition = display.getSelectionStart();
        int openParentheses = 0;
        int closeParentheses = 0;
        int textLength = display.getText().length();

        for (int i = 0; i < cursorPosition; i++) {
            if (display.getText().toString().charAt(i) == '(') {
                openParentheses++;
            }
            if (display.getText().toString().charAt(i) == ')') {
                closeParentheses++;
            }
        }

        if (openParentheses == closeParentheses || display.getText().toString().charAt(textLength - 1) == '(') {
            updateText("(");
        } else if (openParentheses > closeParentheses || display.getText().toString().charAt(textLength - 1) != '(') {
            updateText(")");
        }
        display.setSelection(cursorPosition + 1);

    }

    public void negateButton(View view) {
        updateText("-");
    }

    public void deleteButton(View view) {
        int cursorPosition = display.getSelectionStart();
        int textLength = display.getText().length();
        if (cursorPosition != 0 && textLength != 0) {
            SpannableStringBuilder selection= (SpannableStringBuilder) display.getText();
            selection.replace(cursorPosition - 1, cursorPosition, "");
            display.setText(selection);
            display.setSelection(cursorPosition - 1);
        }
    }

    public void clearButton(View view) {
        display.setText("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}