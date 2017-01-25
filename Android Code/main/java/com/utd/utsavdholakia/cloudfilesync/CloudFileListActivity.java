package com.utd.utsavdholakia.cloudfilesync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class CloudFileListActivity extends Activity {

    public String chosenFile = "";
    ListView listView;
    public String []remoteFileName;
    public static ArrayList<String> cloudFileList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_file_list);
        listView = (ListView) findViewById(R.id.cloudFileListView);
        showCloudFiles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cloud_file_list, menu);
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

    public void showCloudFiles (){
        new communicateWithServer().execute();

    }

    private class communicateWithServer extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... args) {
            // params comes from the execute() call: params[0] is the url.
            //status = 0;
            try {
                Socket echoSocket = SplashScreen.PrefetchData.getEchoSocket();
                //output stream of the socket
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),true);
                //input stream of the socket
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

                String remoteFileList = "";
                out.println("Send File List");
                System.out.println("Client : Send File List");

                remoteFileList = in.readLine();	//server sent file list in a string
                out.println("File list received");	//reply back with a message
                System.out.println("Client : File list received");
                //System.out.println("Server :" + remoteFileList);	//server file list printing without formatting first
                remoteFileName = remoteFileList.split(";");	//get filenames from the string
                System.out.println("Server file list");
                cloudFileList.clear();
                for(int i = 0; i < remoteFileName.length ; i++)			//print all file names
                {
                    cloudFileList.add(remoteFileName[i]);
                    System.out.println("[" + i + 1 + "] :" + cloudFileList.get(i));
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

            populateListView();
        }
    }

    public void populateListView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, cloudFileList);
        listView.setAdapter(adapter);
        System.out.println("CloudFileList : showListView before onclicklistener");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;
                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);
                chosenFile = itemValue;
                System.out.println("CloudFileList : Position =" + position);
                System.out.println("CloudFileList : chosenFile" + chosenFile);
                Intent intent = getIntent();
                intent.putExtra("fileName", chosenFile);
                setResult(RESULT_OK, intent);
            }
        });

    }

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_file_list);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.cloudFileListView);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, cloudFileList);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;
                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);
                chosenFile = itemValue;
                System.out.println("CloudFileList : Position =" + position);
            }
        });
        System.out.println("CloudFileList : chosenFile" + chosenFile);
        Intent intent = getIntent();
        intent.putExtra("fileName",chosenFile);
        setResult(RESULT_OK, intent);
        finish();
    }*/
}
