package com.tcn.sdk.springdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tcn.sdk.springdemo.Model.PointsModel;
import com.tcn.sdk.springdemo.Model.UserDetails;
import com.tcn.sdk.springdemo.Model.UserName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SelectionActivity extends AppCompatActivity {

    public ArrayList<UserDetails> list;
    SweetAlertDialog pd;
    EditText et;
    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    SweetAlertDialog pDialog;
    String barcodetext = "";
    SweetAlertDialog sweetAlertDialog;
    Boolean isuserpaying = false;
    Boolean threadintrupt = false;
    Boolean oncreate = false;
    wait30 w30;
    PointsModel mypoints;
    UserName myuser;
    UserDetails mydetl;
    int qrid = 0;
    private final Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                // TODO: do what you need here
                try {

                    qrid = Integer.parseInt(et.getText().toString().replaceAll("[^a-zA-Z0-9\\.\\-]", "").replace("?", ""));
                    new JsonTask2().execute("https://memberappapi.azurewebsites.net/Api/Login/" + qrid);


                    System.out.println("the code written : " + et.getText());
                    pDialog.setTitle("Processing..");

                } catch (Exception ex) {


                }
            }
        }
    };
    CountDownTimer cd;
    Handler handler = new Handler();
    private Button skip;
    private Button qr;
    private Button member;
    private Button back;

    void showsweetalerttimeout(final CountDownTimer[] ct) {
        sweetAlertDialog = new SweetAlertDialog(SelectionActivity.this, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText("Press Anywhere on screen to Continue");
        sweetAlertDialog.setContentText("This session will end in 10");
        sweetAlertDialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                ct[0].cancel();
                sweetAlertDialog.dismissWithAnimation();
            }
        });

        sweetAlertDialog.setCancelButton("Close", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                threadintrupt = true;
                ct[0].cancel();
                sweetAlertDialog.dismissWithAnimation();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sweetAlertDialog.dismissWithAnimation();
                ct[0].cancel();
            }
        });
        sweetAlertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        skip = findViewById(R.id.skipbtn);
        qr = findViewById(R.id.qrbtn);
        back = findViewById(R.id.backbtn);
        member = findViewById(R.id.btnmember);

        w30 = new wait30();
        w30.start();
        oncreate = true;

        list = new ArrayList<UserDetails>();
        readqr();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pay = new Intent(SelectionActivity.this, SelectPaymentActivity.class);
                pay.putExtra("login", false);
                startActivity(pay);
            }
        });
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //get data from account table
                // new JsonTask().execute("https://memberappapi.azurewebsites.net/Api/Points?userID=2");
                if (!chkinternet()) {
                    new SweetAlertDialog(SelectionActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("No internet")
                            .setContentText("Please contact careline")
                            .show();
                } else {
                    showylogindialog();
                }


            }
        });
        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pay = new Intent(SelectionActivity.this, DownloadAppActivity.class);
                startActivity(pay);
            }
        });
    }

    boolean chkinternet() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void readqr() {

        et = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(80, 80, 80, 80);
        lp.gravity = Gravity.CENTER;

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(input_finish_checker);
                // pDialog.setTitle("Please Wait verifying code..");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);

                } else {

                }

            }
        });


    }

    void showylogindialog() {

        final LinearLayout linearLayout = new LinearLayout(this);

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Create EditText
        final ImageView editText = new ImageView(this);
        editText.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
        editText.setPadding(20, 20, 20, 20);
        linearLayout.addView(editText);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        pDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    barcodetext += (char) event.getUnicodeChar();
                    System.out.println("kesa  :" + barcodetext);
                    et.setText(barcodetext);
                }

                return false;
            }
        });
        pDialog.setConfirmButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
        pDialog.setTitleText("SHOW YOUR LOGIN QR TO PROCEED.");
        barcodetext = "";
        if (cd != null) cd.cancel();
        cd = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (pDialog.getTitleText().equals("SHOW YOUR LOGIN QR TO PROCEED.")) {
                    //  pDialog.setContentText("This dialog will close in " + millisUntilFinished / 1000 + " seconds");
                } else {
                    // pDialog.setContentText("loading...");
                    this.cancel();
                }

            }

            public void onFinish() {
                pDialog.dismissWithAnimation();
            }

        }.start();
        pDialog.setCustomView(linearLayout);
        pDialog.show();//showing the dialog


    }

    public void showerrdialog() {

        pDialog.dismissWithAnimation();
        pd.dismissWithAnimation();
        final SweetAlertDialog sd = new SweetAlertDialog(SelectionActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("No Record Found!")
                .setContentText("Invalid QR Code.");
        sd.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sd.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        threadintrupt = true;
        isuserpaying = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        threadintrupt = false;
        isuserpaying = false;
        if (!oncreate) {
            new wait30().start();
        } else {

            oncreate = false;

        }


    }

    public class wait30 extends Thread {
        public wait30() {
        }

        public void run() {

            super.run();

            while (!threadintrupt) {

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final CountDownTimer[] ct = new CountDownTimer[1];
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            ct[0] = new CountDownTimer(10000, 1000) {
                                public void onTick(long millisUntilFinished) {

                                    if (!isuserpaying) {
                                        if (millisUntilFinished > 0) {
                                            sweetAlertDialog.setContentText("This session will end in " + millisUntilFinished / 1000);
                                        } else {
                                            threadintrupt = true;
                                            try {
                                                sweetAlertDialog.dismissWithAnimation();
                                            } catch (Exception ex) {
                                            }
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                       /* Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
*/
                                            ct[0].cancel();
                                        }
                                    }
                                }

                                public void onFinish() {

                                    try {
                                        sweetAlertDialog.dismissWithAnimation();
                                    } catch (Exception ex) {
                                    }
                                    threadintrupt = true;
                                    ct[0].cancel();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            };

                            if (!isuserpaying) {
                                showsweetalerttimeout(ct);
                                ct[0].start();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        showsweetalerttimeout(ct);
//
//                    }
//                });

            }
        }
    }
    //jason task end here

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new SweetAlertDialog(SelectionActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pd.setContentText("Processing");
            pd.show();
        }


        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {


                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                    System.out.println("Response  :" + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            System.out.println("result for get points  :" + result);

            if (result != null) {
                System.out.println(result);
                System.out.println("id " + qrid);
                Gson gson = new Gson();
                Type type = new TypeToken<List<PointsModel>>() {
                }.getType();
                ArrayList<PointsModel> pointsModels = gson.fromJson(result, type);
                // new JsonTask2().execute("https://memberappapi.azurewebsites.net/Api/Login/"+qrid);
                mypoints = pointsModels.get(0);
                mydetl = new UserDetails(mypoints, myuser);

                String username = "Welcome " + mydetl.getFirstName() + " " + mydetl.getLastName() + ". You have " + mydetl.getPoints();
                pDialog.dismissWithAnimation();
                pd.dismissWithAnimation();
                new SweetAlertDialog(SelectionActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setContentText(username)
                        .setTitleText("Successfully Loged in")
                        .setConfirmButton("Proceed to pay", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                Intent it = new Intent(SelectionActivity.this, SelectPaymentActivity.class);
                                //To pass:
                                it.putExtra("fname", mydetl.getFirstName());
                                it.putExtra("lname", mydetl.getLastName());
                                it.putExtra("points", mydetl.getPoints());
                                it.putExtra("uid", mydetl.getUserID());
                                it.putExtra("pid", mydetl.getId());
                                it.putExtra("ustatus", mydetl.getUserStatus());
                                it.putExtra("expdate", mydetl.getExpireDate());
                                it.putExtra("login", true);

                                sweetAlertDialog.dismissWithAnimation();
                                startActivity(it);


                            }
                        }).show();

            } else
                showerrdialog();
        }
    }

    private class JsonTask2 extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new SweetAlertDialog(SelectionActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pd.setContentText("Processing");
            pd.show();
        }


        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {


                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                    System.out.println("Response  :" + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }

            System.out.println("result for get userdetails  :" + result);

            if (result != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<UserName>() {
                }.getType();

                UserName details = gson.fromJson(result, type);

                myuser = details;
                new JsonTask().execute("https://memberappapi.azurewebsites.net/Api/Points?userID=" + myuser.getUserID());
            } else
                showerrdialog();

        }
    }
}
