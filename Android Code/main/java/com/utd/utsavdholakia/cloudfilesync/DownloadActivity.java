package com.utd.utsavdholakia.cloudfilesync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;

public class DownloadActivity extends Activity {

    public String chosenFile = "";
    public int status = -2; //status = 0 : initial,failure,error;  1 : success; -1: wrong file name
    public EditText editText;
    public long TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setupUI(findViewById(R.id.parent_download));
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(DownloadActivity.this);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_download, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void downloadFileButtonPressed(View view) throws IOException, URISyntaxException {
        // TODO Auto-generated method stub
        Context context = getApplicationContext();
        editText = (EditText) findViewById(R.id.editText_selected_file_name);
        if(editText.getText() == null)
        {
            CharSequence text = "Please enter a filename!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else{
            chosenFile = editText.getText().toString();
            new communicateWithServer().execute();
        }
    }

    private class communicateWithServer extends AsyncTask<Void, Void, Void> {
        public Socket echoSocket;
        public PrintWriter out;
        public BufferedReader in;
        @Override
        protected Void doInBackground(Void... args) {
            // params comes from the execute() call: params[0] is the url.
            status = -2;
            try {
                echoSocket = SplashScreen.PrefetchData.getEchoSocket();
                //output stream of the socket
                out = new PrintWriter(echoSocket.getOutputStream(),true);
                //input stream of the socket
                in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

                //String filePath = "/Users/utsavdholakia/OneDrive/Educational/UTD/Advacned Computer Networks/Team Project/CloudFolder/";
                System.out.println("DownloadActivity chosenfile : " + chosenFile);

                out.println("Download File");
                System.out.println("Client : Download a file");
                if(in.readLine().equals("Send File Name"))
                {
                    System.out.println("Server :Send File Name");
                    out.println(chosenFile);
                    System.out.println("Client :" + chosenFile);
                }
                    String filePath = Environment.getExternalStorageDirectory() + "/CloudFolder/" + chosenFile;
                    File myFile = new File(filePath);
                    System.out.println("DownloadActivity FilePath : " + filePath);
                    //System.out.println("DownloadActivity chosenfile : " + chosenFile);
                    BufferedWriter fileBufferedWriter = new BufferedWriter(new FileWriter(myFile));
                    String line = in.readLine();
                    if(line.equals("File Found"))
                    {
                        line = in.readLine();
                        while(!line.equals("File Sent"))
                        {
                            fileBufferedWriter.write(line);
                            line = in.readLine();
                        }
                        System.out.println("Server :" + line);
                        if(line.equals("File Sent"))
                        {
                            out.println("File Download Success");
                            System.out.println("File downloaded successfully!!!");
                            status = 1;
                        }
                        else
                        {
                            out.println("File Download Error");
                            System.out.println("Error in downloading file!!!");
                            status = 0;
                        }
                        fileBufferedWriter.close();
                    }
                    else if(line.equals("Wrong File Name"))
                    {
                        System.out.println("Server :" + line);
                        status = -1;
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
       }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            Context context = getApplicationContext();


                if(status == 1) {       //file download successful
                    CharSequence text = "File downloaded successfully!";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else if(status == 0) {  //file downloading error
                    CharSequence text = "There was an error in downloading a file!";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else if(status == -1) { //file name wrong - server throws this error
                    CharSequence text = "Wrong file name! File not found on cloud!";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {}}, TIME_OUT);
            finish();
        }
    }
    public void showCloudFilesButtonPressed (View view)
    {
        int CLOUD_FILE_LIST = 1;
        Intent intent = new Intent(DownloadActivity.this,CloudFileListActivity.class);
        startActivityForResult(intent, CLOUD_FILE_LIST);
        /*Intent intent = new Intent(DownloadActivity.this,CloudFileListActivity.class);
        startActivity(intent);*/
    }
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data)
    {   super.onActivityResult(requestCode, resultCode, data);
        int CLOUD_FILE_LIST = 1;
        if (requestCode == CLOUD_FILE_LIST && resultCode == RESULT_OK) {
            Intent getCloudFileintent = getIntent();
            //get the data sent back by intent
            chosenFile = data.getStringExtra("fileName");

            //change edittext value based on the filename sent back
            EditText editText = (EditText) findViewById(R.id.editText_selected_file_name);
            editText.setText(chosenFile);
        }
    }


}
