package com.utd.utsavdholakia.cloudfilesync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;

public class UploadActivity extends Activity {

    public String chosenFile = "";
    public String filePath = "";
    public int status = -1;
    public EditText editText;
    public int TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        setupUI(findViewById(R.id.parent_upload));
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(UploadActivity.this);
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
        getMenuInflater().inflate(R.menu.menu_upload, menu);
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

    public void uploadFileButtonPressed(View view) throws IOException, URISyntaxException {
        // TODO Auto-generated method stub
        Context context = getApplicationContext();
        editText = (EditText) findViewById(R.id.editText_file_selected);
        if(editText.getText() == null)
        {
            CharSequence text = "Please select a file!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else {
            chosenFile = editText.getText().toString();
            new communicateWithServer().execute();
        }
    }

    private class communicateWithServer extends AsyncTask <Void, Void, Void> {

        public Socket echoSocket;
        public PrintWriter out;
        public BufferedReader in;

        @Override
        protected Void doInBackground(Void... args) {

            status = -1;
            // params comes from the execute() call: params[0] is the url.
            try {
                echoSocket = SplashScreen.PrefetchData.getEchoSocket();
                //output stream of the socket
                out = new PrintWriter(echoSocket.getOutputStream(), true);
                //input stream of the socket
                in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

                out.println("Upload File");
                System.out.println("Client : Upload a file");
                //Log.e("Trace", "Client : Upload a file");
                if (in.readLine().equals("Send File name")) {
                    //Log.e("Trace", "Server : Send File Name");
                    System.out.println("Server : Send File Name");
                    out.println(chosenFile);
                    //Log.e("Trace", "Client :" + chosenFile);
                    System.out.println("Client :" + chosenFile);
                }
                filePath = filePath.concat("/" + chosenFile);
                File myFile = new File(filePath);
                BufferedReader fileBufferedReader = new BufferedReader(new FileReader(myFile));
                String line = in.readLine();
                if (line.equals("Send File")) {
                    while ((line = fileBufferedReader.readLine()) != null) {
                        out.println(line);
                    }
                    out.println("File Sent");
                    //Log.e("Trace", "Client : File Sent");
                    System.out.println("Client : File Sent");
                    if ((line = in.readLine()).equals("File Upload Success")) {
                        status = 1;
                        System.out.println("File uploaded successfully!!!");
                    } else if ((line = in.readLine()).equals("File Upload Error")) {
                        status = 0;
                        System.out.println("Error in uploading file!!!");
                    }
                    fileBufferedReader.close();
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



                if(status == 1) {
                    CharSequence text = "File uploaded successfully!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else if(status == 0) {
                    CharSequence text = "There was an error in uploading file!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {}}, TIME_OUT);

            finish();
        }
    }
    public void selectFileButtonPressed (View view)
    {
        int LOCAL_FILE_LIST = 1;
        Intent intent = new Intent(UploadActivity.this,LocalFileListActivity.class);
        startActivityForResult(intent, LOCAL_FILE_LIST);
    }
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        int LOCAL_FILE_LIST = 1;
        if (requestCode == LOCAL_FILE_LIST && resultCode == RESULT_OK) {
            //Intent getLocalFileintent = getIntent();
            //get the data sent back by intent
            filePath = data.getStringExtra("filePath");
            chosenFile = data.getStringExtra("fileName");

            //change edittext value based on the filename sent back
            EditText editText = (EditText) findViewById(R.id.editText_file_selected);
            editText.setText(chosenFile);
        }
    }
}
