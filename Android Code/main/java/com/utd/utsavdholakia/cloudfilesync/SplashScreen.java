package com.utd.utsavdholakia.cloudfilesync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new PrefetchData().execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
    /**
     * Async Task to make http call
     */
    public static class PrefetchData extends AsyncTask<Void, Void, Void> {

        public static Socket echoSocket;

        public Socket createSocket (Socket echoSocket) throws UnknownHostException,IOException
        {
            echoSocket = new Socket("ec2-52-24-184-199.us-west-2.compute.amazonaws.com", 4444);
            return echoSocket;
        }
        public static Socket getEchoSocket()
        {
            return echoSocket;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls

        }
        @Override
        protected Void doInBackground(Void... arg0) {

            try{
                echoSocket = createSocket(echoSocket);
                //output stream of the socket
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),true);
                //input stream of the socket
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

                System.out.println("Starting synchronization with server...");
                out.println("Sync");
                System.out.println("Client : Sync");
                String inputCommand = in.readLine();
                if(inputCommand.equals("Sync ACK"))
                {
                    System.out.println("Server :" + inputCommand);
                    out.println("ACK");
                    System.out.println("Client : ACK");
                    //return 1;
                }
                else
                {
                    System.out.println("Server :" + inputCommand);
                    out.println("Close connection");
                    System.out.println("Client : Close Connection");
                    System.out.println("Server :" + inputCommand);
                    //return -1;
                }
            }
            catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
            }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
