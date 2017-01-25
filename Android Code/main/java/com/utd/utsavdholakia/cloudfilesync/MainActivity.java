package com.utd.utsavdholakia.cloudfilesync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
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
        if (id == R.id.action_exit) {
            new communicateWithServer().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class communicateWithServer extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... args) {

            // params comes from the execute() call: params[0] is the url.
            try{
                Socket echoSocket = SplashScreen.PrefetchData.getEchoSocket();
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),true);
                //input stream of the socket
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

                out.println("Close Connection");
                System.out.println("Client : Close Connection");
                if(in.readLine().equals("Communication done!! Ciao...")) {
                    finish();
                }
                else
                {
                    Context context = getApplicationContext();
                    CharSequence text = "There was an error closing the application!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    System.exit(0);
                }

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
            public void uploadButtonPressed(View view)
    {
        //Do something in response to press button event
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
    }
    public void downloadButtonPressed(View view)
    {
        //Do something in response to press button event
        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
    }
    public void showCloudFileListButtonPressed(View view)
    {
        //Do something in response to press button event
        Intent intent = new Intent(this, CloudFileListActivity.class);
        startActivity(intent);
    }
    public void showLocalFileListButtonPressed(View view)
    {
        //Do something in response to press button event
        Intent intent = new Intent(this, LocalFileListActivity.class);
        startActivity(intent);

    }

}
