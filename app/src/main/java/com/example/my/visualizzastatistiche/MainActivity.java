package com.example.my.visualizzastatistiche;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.my.visualizzastatistiche.adapter.DirectoriesRecyclerAdapter;
import com.example.my.visualizzastatistiche.models.Directory;
import com.example.my.visualizzastatistiche.util.DataBaseHelper;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DirectoriesRecyclerAdapter.OnDirectoryListener{

    private static final String TAG = "MainActivity";

    //UI components
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //vars
    private ArrayList<Directory> mDirectory = new ArrayList<>();
    private DirectoriesRecyclerAdapter mDirectoriesRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = findViewById(R.id.swipeContainer);
        initRecyclerView();

        openFtpConnection();
        setListeners();

        //Check if we already have a database directory & sqlite database created, otherwise we create it
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        try {
            dataBaseHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        setSupportActionBar((Toolbar) findViewById(R.id.directories_toolbar));
        setTitle("Directories");
    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mDirectoriesRecyclerAdapter = new DirectoriesRecyclerAdapter(mDirectory, this);
        mRecyclerView.setAdapter(mDirectoriesRecyclerAdapter);
    }
    private void openFtpConnection(){
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String url = myPrefs.getString("ftp_server",null);

        if (url != null)
        {
            //Connect to the FTP server with an AsyncTask(is Async but fires a progress Dialog to wait until its finished)
            new FtpListTask().execute(url);
        } else {
            Toast.makeText(getApplicationContext(), "ERRORE: Impostazioni mancanti!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }
    private void setListeners(){
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDirectory.clear();
                openFtpConnection();
            }
        });
    }

    @Override
    public void onDirectoryClick(int position) {
        Log.d(TAG, "onDirectoryClick: Clicked position " + position);

        Intent intent = new Intent(this,DirectoryActivity.class);
        intent.putExtra("selected_directory", mDirectory.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.app_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FtpListTask extends AsyncTask<String, Void, FTPFile[]> {
        private ProgressDialog pd;
        String username = null, password = null;
        @Override
        protected void onPreExecute() {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            username = myPrefs.getString("username",null);
            password = myPrefs.getString("password",null);
            if (username != null && password != null )
            {
                //Creo un nuovo process dialog mentre il mio AsyncTask sta girando
                pd = new ProgressDialog( MainActivity.this);
                pd.setTitle("Download...");
                pd.setMessage("Download della lista statistiche..");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            } else {
                Toast.makeText(getApplicationContext(), "ERRORE: Impostazioni mancanti!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected FTPFile[] doInBackground(String... strings) {
            FTPClient ftpClient = new FTPClient();

            try {
                String url = strings[0];
                ftpClient.connect(InetAddress.getByName(url));
                ftpClient.login(username, password);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                FTPFile[] ftpDirs = ftpClient.listDirectories("statistiche");
                for (int i = 2; i < ftpDirs.length; i++) {

                    Directory directory = new Directory();
                    directory.setName(ftpDirs[i].getName());

                    mDirectory.add(directory);
                }
                ftpClient.disconnect();
                return ftpDirs;
            } catch (Exception ex) {
                Log.e(TAG, "onCreate: error on FTP CONNECT", ex);
                System.out.println(ex.getMessage() + " " + ex.getStackTrace());
                Toast.makeText(getApplicationContext(), "ERRORE: Impostazioni FTP sbagliate!", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(FTPFile[] ftpFiles) {
            //avvertiamo l'adapter che abbiamo cambiato dei dati sulla lista
            mDirectoriesRecyclerAdapter.notifyDataSetChanged();
            //Nascondo il processDialog
            pd.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
