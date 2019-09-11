package com.example.my.visualizzastatistiche;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.visualizzastatistiche.adapter.BookRecyclerAdapter;
import com.example.my.visualizzastatistiche.models.Book;
import com.example.my.visualizzastatistiche.models.Directory;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DirectoryActivity extends AppCompatActivity implements BookRecyclerAdapter.OnBookListener {
    private static final String TAG = "DirectoryActivity";

    //UI
    private TextView mViewTitle;
    private ImageButton mBackArrow;
    private EditText mDataIniziale;
    private EditText mDataFinale;
    private Button mSearch;
    private TextView mLibriConContantiLabel;
    private TextView mLibriConBuoniLabel;
    private TextView mSpesaConBuoni;
    private TextView mSpesaConContanti;
    private RecyclerView mRecyclerViewLibri;

    //vars
    private Directory mInitialDirectory;
    private Calendar myCalendar;
    private DatePickerDialog dpd;
    private ArrayList<Book> mBooks = new ArrayList<>();
    private BookRecyclerAdapter mBooksRecyclerAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        mViewTitle = findViewById(R.id.directory_text_title);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mDataIniziale = findViewById(R.id.dataInizialeText);
        mDataFinale = findViewById(R.id.dataFinaleText);
        mSearch = findViewById(R.id.btnRicercaStatistiche);
        mLibriConContantiLabel = findViewById(R.id.libriConContantiLabel);
        mLibriConBuoniLabel = findViewById(R.id.libriConBuoniLabel);
        mSpesaConBuoni = findViewById(R.id.spesaConBuoniLabel);
        mSpesaConContanti = findViewById(R.id.spesaConContantiLabel);
        mRecyclerViewLibri = findViewById(R.id.recyclerViewLibri);


        if(getIntent().hasExtra("selected_directory")){
            mInitialDirectory = getIntent().getParcelableExtra("selected_directory");
            setNewDirectoryProperties();

            new FtpDatabaseDownloadTask().execute(mInitialDirectory.getName());
            Log.d(TAG, "onCreate: " + mInitialDirectory.toString());
        }
        //Initialize the recycler View
        initRecyclerView();
        //specify the listeners to use
        setListeners();
        //Set the initial dates for the pickers;
        setInitialDates();
    }


    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewLibri.setLayoutManager(linearLayoutManager);
        mBooksRecyclerAdapter = new BookRecyclerAdapter(mBooks, this);
        mRecyclerViewLibri.setAdapter(mBooksRecyclerAdapter);
    }

    private void clearViews(){
        mLibriConBuoniLabel.setText("0");
        mSpesaConBuoni.setText("0.00" + "€");
        mLibriConContantiLabel.setText("0");
        mSpesaConContanti.setText("0.00" + "€");
        mBooks.clear();
    }

    private void setInitialDates(){
        //Get the current date and format it, then set it as the text to the pickers
        myCalendar = Calendar.getInstance();
        int day = myCalendar.get(Calendar.DAY_OF_MONTH);
        int month = myCalendar.get(Calendar.MONTH);
        int year = myCalendar.get(Calendar.YEAR);
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
        myCalendar.set(year, month,day);
        mDataIniziale.setText(sdf.format(myCalendar.getTime()));
        mDataFinale.setText(sdf.format(myCalendar.getTime()));
    }

    //Sets the listeners for this class
    private void setListeners(){

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //closes the current activity and returns to activity that launched this one
                finish();
            }
        });

        mDataIniziale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataIniziale.setShowSoftInputOnFocus(false);
                myCalendar = Calendar.getInstance();
                int day = myCalendar.get(Calendar.DAY_OF_MONTH);
                int month = myCalendar.get(Calendar.MONTH);
                int year = myCalendar.get(Calendar.YEAR);

                dpd = new DatePickerDialog(DirectoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(year, month,dayOfMonth);
                        String myFormat = "dd/MM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);

                        mDataIniziale.setText(sdf.format(myCalendar.getTime()));
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        mDataFinale.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mDataFinale.setShowSoftInputOnFocus(false);
                myCalendar = Calendar.getInstance();
                int day = myCalendar.get(Calendar.DAY_OF_MONTH);
                int month = myCalendar.get(Calendar.MONTH);
                int year = myCalendar.get(Calendar.YEAR);

                dpd = new DatePickerDialog(DirectoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(year, month, dayOfMonth);
                        String myFormat = "dd/MM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);

                        mDataFinale.setText(sdf.format(myCalendar.getTime()));
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked search button");
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // Make sure user insert date into edittext in this format.

                Date dataIniziale, dataFinale;
                int quantitaContanti = 0, quantitaBuoni = 0;
                Integer lastVenditaID = null;
                BigDecimal costoBuoni = new BigDecimal(0), costoContanti = new BigDecimal(0);
                try {
                    clearViews();
                    //set the dates from the pickers
                    dataIniziale = formatter.parse(mDataIniziale.getText().toString());
                    dataFinale = formatter.parse(mDataFinale.getText().toString());

                    //set the initial date in a calendar to be able to add days
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dataIniziale);
                    SQLiteDatabase db = openOrCreateDatabase("inventario.db", MODE_PRIVATE, null);


                    if(dataIniziale.compareTo(dataFinale) > 0){
                        Toast.makeText(getApplicationContext(), "ERRORE: Data Iniziale > Data Finale", Toast.LENGTH_LONG).show();
                    } else {
                        while (dataIniziale.compareTo(dataFinale) < 0 || dataIniziale.compareTo(dataFinale) == 0) {
                            //SELECTION query
                            try (Cursor searchResultSet = db.rawQuery("SELECT vendita.id, vendita.costo, vendita.metodo, inventario.nome, inventario.autore, inventario.casa, inventario.anno, inventario.indice FROM vendita, statistiche, inventario WHERE data='" + formatter.format(dataIniziale) + "' AND vendita.id = statistiche.venditaID AND inventario.id = statistiche.libriID", null)) {
                                if(searchResultSet.moveToFirst()) {
                                    while(!searchResultSet.isAfterLast()) {
                                        Book libro = new Book();
                                        libro.setDate(dataIniziale);
                                        libro.setName(searchResultSet.getString(3));
                                        libro.setVenditaID(searchResultSet.getInt(0));
                                        libro.setAuthor(searchResultSet.getString(4));
                                        libro.setEditor(searchResultSet.getString(5));
                                        libro.setYear(searchResultSet.getString(6));
                                        libro.setIndex(searchResultSet.getString(7));
                                        if (searchResultSet.getString(2).equals("B")) {
                                            libro.setMetodo("B");
                                            quantitaBuoni++;
                                            if(lastVenditaID == null || !(searchResultSet.getInt(0) == lastVenditaID))
                                            {
                                                costoBuoni = costoBuoni.add(new BigDecimal(searchResultSet.getString(1).replaceAll(",", ".")));
                                                lastVenditaID = searchResultSet.getInt(0);
                                            }
                                        } else {
                                            libro.setMetodo("C");
                                            quantitaContanti++;
                                            if(lastVenditaID == null || !(searchResultSet.getInt(0) == lastVenditaID))
                                            {
                                                costoContanti = costoContanti.add(new BigDecimal(searchResultSet.getString(1).replaceAll(",", ".")));
                                                lastVenditaID = searchResultSet.getInt(0);
                                            }
                                        }
                                        mBooks.add(libro);
                                        searchResultSet.moveToNext();
                                    }
                                }
                            }
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            dataIniziale = calendar.getTime();
                        }
                    Toast.makeText(getApplicationContext(), "Ricerca Completata", Toast.LENGTH_SHORT).show();
                    }
                    //Update the labels
                    mLibriConContantiLabel.setText(String.format(Locale.getDefault(), "%d", quantitaContanti));
                    if(costoContanti.compareTo(BigDecimal.ZERO) != 0) {
                        mSpesaConContanti.setText(costoContanti.toString() + "€");
                    }
                    mLibriConBuoniLabel.setText(String.format(Locale.getDefault(), "%d", quantitaBuoni));
                    if(costoBuoni.compareTo(BigDecimal.ZERO) != 0) {
                        mSpesaConBuoni.setText(costoBuoni.toString() + "€");
                    }
                    // close the connections we don't need
                    db.close();
                    mBooksRecyclerAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("setOnClickListener: ", e.toString());
                }
            }
        });
    }

    //Displays the directory name in the toolbar by grabbing the directory object passed with the Intent bundle
    private void setNewDirectoryProperties(){
        mViewTitle.setText(mInitialDirectory.getName());
    }

    @Override
    public void onBookClick(int position) {

    }

    //This AsyncTask downloads the remote database File and overwrites the local one everytime a different activity is selected
    //@param machineName: specifies the selected option in the list, and is passed in the execute method
    public class FtpDatabaseDownloadTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog pd;
        String username = null, password = null, url = null;
        @Override
        protected void onPreExecute() {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(DirectoryActivity.this);
            username = myPrefs.getString("username",null);
            password = myPrefs.getString("password",null);
            url = myPrefs.getString("ftp_server",null);
            if (username != null && password != null )
            {
                //Creo un nuovo process dialog mentre il mio AsyncTask sta girando
                pd = new ProgressDialog( DirectoryActivity.this);
                pd.setTitle("Download...");
                pd.setMessage("Download del file statistiche..");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            } else {
                Toast.makeText(getApplicationContext(), "ERRORE: Impostazioni mancanti!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            FTPClient ftpClient = new FTPClient();
            try {
                String machineName = strings[0];
                ftpClient.connect(InetAddress.getByName(url));
                ftpClient.login(username, password);
                //System.out.println("status :: " + ftpClient.getStatus());
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                //We create the outputstream where to write the file to
                File localDatabase = new File("/data/data/com.example.my.visualizzastatistiche/databases/inventario.db");
                try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localDatabase))) {
                    //Change the FTP folder to the selected machine on the list
                    ftpClient.changeWorkingDirectory("/statistiche/" + machineName);
                    //Retrieve the file from the FTP server
                    ftpClient.retrieveFile("statistiche.sqlite", outputStream);
                }
                //Close the FTP connection
                ftpClient.logout();
                ftpClient.disconnect();
                return true;
            } catch (Exception ex) {
                Log.e(TAG, "onCreate DirectoryActivity: error on FTP DOWNLOAD", ex);
                System.out.println(ex.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean ftpFiles) {
            //Dismiss the progress Dialog once we're done with this task
            pd.dismiss();
        }
    }
}
