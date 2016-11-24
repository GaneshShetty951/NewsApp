package com.example.ganeshshetty.newsapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;


public class InputActivity extends Activity implements DatePickerDialog.OnDateSetListener {
    EditText text_user_name,text_email;
    TextView date_view,text_interest;
    Button btn_pick_date,btn_interest,btn_submit;
    Spinner spin_birth,spin_residence,spin_language,spin_religion;

    private int year;
    private int month;
    private int day;
    static final int DATE_PICKER_ID = 1111;
    private static final String DEFAULT_LOCAL = "India";
    static final int REQUEST_CODE = 0;

    ArrayList<String> religion_list;
    ArrayAdapter<String> adapter_lang;
    ArrayList<String> countries;
    ArrayList<String> languages;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        findViewById();

        //creatting database.
        createDatabase();

        // Get current date by calender
        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        // Show current date

        date_view.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        // Button listener to show date picker dialog
        btn_pick_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // On button click show datepicker dialog
                showDialog(DATE_PICKER_ID);

            }

        });

        //Button Listener to show list of interest
        btn_interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(InputActivity.this,InterestActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        addCountryList();
        //preparing Adapter for spinner
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, countries);

        //Adding data to birth spinner
        spin_birth.setAdapter(adapter);
        spin_birth.setSelection(adapter.getPosition(DEFAULT_LOCAL));

        //Adding data to residence spinner
        spin_residence.setAdapter(adapter);
        spin_residence.setSelection(adapter.getPosition(DEFAULT_LOCAL));

        //creating list of language
        createLanguageList();

        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        adapter_lang = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, languages);

        //add language list to spinner
        spin_language.setAdapter(adapter_lang);

        //initializing Religion list.
        initializeReligionList();
        ArrayAdapter<String> adapter_religion = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, religion_list);
        spin_religion.setAdapter(adapter_religion);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertIntoDB();
            }
        });
    }

    private void createLanguageList() {
        Locale[] locale = Locale.getAvailableLocales();
        String lang;
        for( Locale loc : locale ){
            lang = loc.getDisplayLanguage();
            if( lang.length() > 0 && !languages.contains(lang) ){
                languages.add( lang );
            }
        }
    }

    private void addCountryList() {
        Locale[] locale = Locale.getAvailableLocales();
        countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
    }

    private void initializeReligionList() {
        religion_list =new ArrayList<String>();
        religion_list.add("African Traditional & Diasporic");
        religion_list.add("Agnostic");
        religion_list.add("Atheist");
        religion_list.add("Baha'i");
        religion_list.add("Buddhism");
        religion_list.add("Cao Dai");
        religion_list.add("Chinese traditional religion");
        religion_list.add("Christianity");
        religion_list.add("Hinduism");
        religion_list.add("Islam");
        religion_list.add("Jainism");
        religion_list.add("Juche");
        religion_list.add("Judaism");
        religion_list.add("Neo-Paganism");
        religion_list.add("Nonreligious");
        religion_list.add("Rastafarianism");
        religion_list.add("Secular");
        religion_list.add("Shinto");
        religion_list.add("Sikhism");
        religion_list.add("Spiritism");
        religion_list.add("Tenrikyo");
        religion_list.add("Unitarian-Universalism");
        religion_list.add("Zoroastrianism");
        religion_list.add("primal-indigenous");
        religion_list.add("Other");

    }

    private void findViewById() {
        text_user_name=(EditText)findViewById(R.id.user_name);
        text_email=(EditText)findViewById(R.id.mail_id);

        text_interest=(TextView)findViewById(R.id.text_interest);
        date_view=(TextView)findViewById(R.id.dob);

        btn_pick_date=(Button)findViewById(R.id.dobpick);
        btn_interest=(Button)findViewById(R.id.select_interest);
        btn_submit=(Button)findViewById(R.id.btn_submit);

        spin_birth=(Spinner)findViewById(R.id.spinner_birth);
        spin_residence=(Spinner)findViewById(R.id.spinner_residence);
        spin_language=(Spinner)findViewById(R.id.spinner_language);
        spin_religion=(Spinner)findViewById(R.id.spinner_religion);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle=data.getExtras();
        String[] interest_list=bundle.getStringArray("selectedItems");
        text_interest.setText("");
        for(String str:interest_list){
            text_interest.setText(text_interest.getText()+"\n"+str);
        }
        text_interest.setText(text_interest.getText()+"\n\n");
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            // Show selected date
            date_view.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

        }
    };

    protected void createDatabase(){
        db=openOrCreateDatabase("NewsUserDB", Context.MODE_PRIVATE, null);
        db.execSQL("drop table IF EXISTS Users");
        db.execSQL("CREATE TABLE IF NOT EXISTS Users(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "user_name VARCHAR," +
                "email VARCHAR," +
                "dob VARCHAR," +
                "interest VARCHAR," +
                "countrybirth VARCHAR," +
                "countryresid VARCHAR," +
                "language VARCHAR," +
                "Religion VARCHAR);");
    }

    protected void insertIntoDB(){
        String name = text_user_name.getText().toString().trim();
        String email = text_email.getText().toString().trim();
        String dob=date_view.getText().toString().trim();
        String interest=text_interest.getText().toString().trim();
        String country_b=spin_birth.getSelectedItem().toString().trim();
        String country_r=spin_residence.getSelectedItem().toString().trim();
        String language=spin_language.getSelectedItem().toString().trim();
        String religion=spin_religion.getSelectedItem().toString().trim();
        if(name.equals("") || email.equals("")||dob.equals("")||interest.equals("")||country_b.equals("")||country_r.equals("")||language.equals("")||religion.equals("")){
            Toast.makeText(getApplicationContext(),"Please fill all fields", Toast.LENGTH_LONG).show();
            return;
        }

        String query = "INSERT INTO Users (user_name,email,dob,interest,countrybirth,countryresid,language,Religion) " +
                "VALUES('"+name+"', '"+email+"','"+dob+"','"+interest+"','"+country_b+"','"+country_r+"','"+language+"','"+religion+"');";
        db.execSQL(query);
        Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }
}
