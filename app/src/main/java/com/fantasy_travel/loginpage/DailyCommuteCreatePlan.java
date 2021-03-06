package com.fantasy_travel.loginpage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;

import static android.app.TimePickerDialog.*;


public class DailyCommuteCreatePlan extends AppCompatActivity {

    private Spinner spinner1, spinner2;
    public LatLng startLatLng, destLatLng;
    private Button save_btn, cancel_btn;
    EditText chooseTime;
    private int current_hour, current_minute;
    private String amPm;
    EditText planName;
    String plan_name1;
    String start_loc_name, dest_loc_name, time, mode, sex;
    public String start[], dest[];
    private String apiKey = "AIzaSyAj76-gXJ5gvXrjp12YlLA90xEokK7O234";
    private String username = "";
    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_commute_create_plan);
        mDrawerLayout = findViewById(R.id.Map_Drawer);
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                });
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        int id = menuItem.getItemId();

                        switch (id){
                            case R.id.nav_account:
                                Toast.makeText(getApplicationContext(),"Account",Toast.LENGTH_SHORT).show();
                                Intent intent_Account = new Intent( DailyCommuteCreatePlan.this, AccountActivity.class);
                                startActivity(intent_Account);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_Daily_Commute:
                                Intent intent_DC = new Intent( DailyCommuteCreatePlan.this, DailyCommuteViewPlan.class);
                                startActivity(intent_DC);
                                Toast.makeText(getApplicationContext(),"DailyCommute",Toast.LENGTH_SHORT).show();
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_Find_Fellow_Traveller:
                                Intent intent_FFT = new Intent( DailyCommuteCreatePlan.this, Maps.class);
                                startActivity(intent_FFT);
                                Toast.makeText(getApplicationContext(),"FindFellowTraveller",Toast.LENGTH_SHORT).show();
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_Setting:
                                Toast.makeText(getApplicationContext(),"Setting",Toast.LENGTH_SHORT).show();
                                Intent intent_Setting = new Intent( DailyCommuteCreatePlan.this, AccountActivity.class);
                                startActivity(intent_Setting);
                                break;
                            case R.id.nav_LogOut:
                                Toast.makeText(getApplicationContext(),"LouOut",Toast.LENGTH_SHORT).show();
                                finish();
                        }
                        return true;
                    }
                });


        Places.initialize(getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(this);

        SharedPreferences preferences1 =
                getSharedPreferences("com.myOTP.FantasyTravel", Context.MODE_PRIVATE);
        username = preferences1.getString("emailID",username);

        if(!Places.isInitialized())
        {
            Places.initialize(getApplicationContext(), "AIzaSyAj76-gXJ5gvXrjp12YlLA90xEokK7O234");
        }

        planName = (EditText) findViewById(R.id.Plan_Name);
        plan_name1 = planName.getText().toString();
        Log.d("Backend","planName"+plan_name1);

        final TextView startingTextView = findViewById(R.id.starting_location_autocomplete_text);
        final TextView destinationTextView = findViewById(R.id.destination_location_autocomplete_text);

        AutocompleteSupportFragment autocompleteFragment1 = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.starting_location_autocomplete);
        AutocompleteSupportFragment autocompleteFragment2 = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.destination_location_autocomplete);

        /*AutocompleteFilter filter= new AutocompleteFilter.Builder()
                .setCountry("IR")
                .build();
        autocompleteFragment1.setFilter(filter);
        autocompleteFragment2.setFilter(filter);

        */

        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
                startingTextView.setText(place.getName());
                start_loc_name = startingTextView.getText().toString();
                Log.d("backend","lat"+start_loc_name );
                startLatLng = place.getLatLng();
                start = startLatLng.toString().replace("lat/lng: ","").replace("(","").replace(")","").split(",");
                Log.d("backend","lat"+start[0] );
                Log.d("backend","long"+start[1] );
                Log.d("Backend","start_LatLng"+startLatLng);
            }

            @Override
            public void onError(@NonNull Status status) {
                startingTextView.setText(status.toString());
            }
        });

        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destinationTextView.setText(place.getName());
                dest_loc_name = destinationTextView.getText().toString();
                destLatLng = place.getLatLng();
                dest = destLatLng.toString().replace("lat/lng: ","").replace("(","").replace(")","").split(",");
                Log.d("backend","lat"+dest[0] );
                Log.d("backend","long"+dest[1] );
                Log.d("Backend","dest_LatLng"+destLatLng);
            }

            @Override
            public void onError(@NonNull Status status) {
                destinationTextView.setText(status.toString());
            }
        });

        chooseTime = (EditText) findViewById(R.id.editTime);
        chooseTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                current_hour = calendar.get(Calendar.HOUR_OF_DAY);
                current_minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(DailyCommuteCreatePlan.this, new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        chooseTime.setText(String.format("%02d:%02d", hourOfDay, minute) + amPm);
                        time = chooseTime.getText().toString();
                    }
                }, current_hour, current_minute, false);
                timePickerDialog.setTitle("Pick a time");
                timePickerDialog.show();
                }
            });

        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
    }

    public void addListenerOnSpinnerItemSelection()
    {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());


        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        save_btn = (Button) findViewById(R.id.plan_save_btn);
        cancel_btn = (Button) findViewById(R.id.plan_cancel_btn);


        //preview selections
        save_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(DailyCommuteCreatePlan.this, "Plan : " + planName.getText().toString() +"is Saved.",Toast.LENGTH_SHORT).show();
                new Call_Save().execute();

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent notificationIntent = new Intent(DailyCommuteCreatePlan.this, AlarmReceiver.class);
                PendingIntent broadcast = PendingIntent.getBroadcast(DailyCommuteCreatePlan.this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, 30);
                time.replaceAll("AM","").replaceAll("PM","");
                String[] chooseTime1=time.split(":");
                Log.d("Backend", "min in mobile" + String.valueOf(Calendar.MINUTE));
                Log.d("Backend", "sec in mobile" + String.valueOf(Calendar.SECOND));
                Log.d("Backend", "hour in mobile" + String.valueOf(Calendar.HOUR));
               // cal.add(Calendar.MINUTE, Integer.parseInt(chooseTime1[1].substring(0,chooseTime1[1].length()-2)));
              //  cal.add(Calendar.MINUTE, Integer.parseInt(chooseTime1[1].substring(0,chooseTime1[1].length()-2)));
              //  cal.add(Calendar.HOUR, Integer.parseInt(chooseTime1[0]));
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                Intent intent = new Intent(DailyCommuteCreatePlan.this, DailyCommuteViewPlan.class);
                startActivity(intent);
            }
        });

        cancel_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(DailyCommuteCreatePlan.this, DailyCommuteViewPlan.class);
                startActivity(intent);
            }
        });
    }

private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

            public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
                mode = spinner1.getSelectedItem().toString();
                sex = spinner2.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
    }

    class Call_Save extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.d("Backend", "insideCall");
            try {

                String URL1 = Misc.Url1+"/InsertMyPlan?startLat="+start[0]+"&endLat="+start[1]+"&endLong="+dest[1]+"&startLong="+dest[0]+"&id="+username+"&preferedSex="+sex+"&preferedMode="+mode+"&name="+planName.getText().toString()+"&startLoc="+start_loc_name+"&endLoc="+dest_loc_name+"&time="+time;
                Log.d("Backend",URL1);
                URL url = new URL(URL1);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                // conn.setRequestProperty("Accept", "application/json");
                Log.d("Backend", "request posted successfully");
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                //  Log.d("Output from Server .... \n","new");
                while ((output = br.readLine()) != null) {
                    Log.d("Backend", output);


                    if(output.contains("200"))
                    {  Log.d("Backend", "Contains 200");
                        Intent intent = new Intent( DailyCommuteCreatePlan.this, DailyCommuteViewPlan.class);
                        startActivity(intent);
                    }
                    else if(output.contains("500"))
                    {  Log.d("Backend", "Contains 500");
                        DailyCommuteCreatePlan.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(DailyCommuteCreatePlan.this, "Account Not Active", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                    else
                    { Log.d("Backend", "Does not contain Contains 200");
                        //Toast.makeText(LoginActivity.this,"Invalid Creadentials",Toast.LENGTH_SHORT);


                        DailyCommuteCreatePlan.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(DailyCommuteCreatePlan.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }

                conn.disconnect();

            } catch (Exception e) {


                Log.d("Backend", "exception in HTTP");
                e.printStackTrace();

            }

            return null;
        }
    }
}
