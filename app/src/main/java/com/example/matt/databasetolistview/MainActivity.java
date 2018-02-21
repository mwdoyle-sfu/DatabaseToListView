package com.example.matt.databasetolistview;

        import android.database.Cursor;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ListView;
        import android.widget.SimpleCursorAdapter;
        import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    int[] imageIDs = {
            R.drawable.bug,
            R.drawable.down,
            R.drawable.fish,
            R.drawable.heart,
            R.drawable.help,
            R.drawable.lightning,
            R.drawable.star,
            R.drawable.up,
    };

    int nextImageIndex = 0;

    DBAdapter myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openDB();
        populateListViewFromDB();
        registerListClickCallBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }

    private void openDB() {
        myDb = new DBAdapter(this);
        myDb.open();
    }

    private void closeDB() {
        myDb.close();
    }


    private void displayText(String message) {
        Log.i("MyApp",message);
    }

    public void onClick_AddRecord(View v) {
        displayText("Clicked add record");
        int imageId = imageIDs[nextImageIndex];
        nextImageIndex = (nextImageIndex + 1) % imageIDs.length;

        // Add it to the DB and re-draw the ListView
        myDb.insertRow("Jenny" + nextImageIndex,imageId, "Green");
        populateListViewFromDB();
    }

    public void onClick_ClearAll(View v) {
        displayText("Clicked clear all");
        myDb.deleteAll();
        populateListViewFromDB();
    }


    private void populateListViewFromDB() {
        Cursor cursor = myDb.getAllRows();

        // Allow activity to manage lifetime of the cursor
        // DEPRECATED! Runs on the UI thread, OK for small/short queries
        startManagingCursor(cursor);

        // Setup mapping from cursor to view fields
        String[] fromFeildNames = new String[]
                {DBAdapter.KEY_NAME, DBAdapter.KEY_STUDENTNUM, DBAdapter.KEY_FAVCOLOUR, DBAdapter.KEY_STUDENTNUM};
        int[] toViewIds = new int[]
                {R.id.item_name,    R.id.item_icon,     R.id.item_favcolor,     R.id.item_studentnum};

        // Create an adapter to map columns of the Db onto elements in the UI
        SimpleCursorAdapter myCursorAdapter =
                new SimpleCursorAdapter(
                        this,           // Context
                        R.layout.item_layout,   // Row layout template
                        cursor,                 // cursor (set of DB records to map)
                        fromFeildNames,         // DB Column
                        toViewIds               // View IDs to put information in
                );

        // Set the adapter for the list view
        ListView myList = (ListView) findViewById(R.id.listViewFromDB);
        myList.setAdapter(myCursorAdapter);
    }

    private void registerListClickCallBack() {
        ListView myList = (ListView) findViewById(R.id.listViewFromDB);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long idInDB) {

                updateItemForId(idInDB);
                displayToastForId(idInDB);
            }
        });
    }

    private void updateItemForId(long idInDB) {
        Cursor cursor = myDb.getRow(idInDB);
        if (cursor.moveToFirst()) {
            long idDB = cursor.getLong(DBAdapter.COL_ROWID);
            String name = cursor.getString(DBAdapter.COL_NAME);
            int studentNum = cursor.getInt(DBAdapter.COL_STUDENTNUM);
            String favColour = cursor.getString(DBAdapter.COL_FAVCOLOUR);

            favColour += "!";
            myDb.updateRow(idInDB, name, studentNum, favColour);
        }
        cursor.close();
        populateListViewFromDB();
    }


    // Display an entire record set to the screen
    private void displayToastForId(long idInDB) {
        Cursor cursor = myDb.getRow(idInDB);
        if (cursor.moveToFirst()) {
            long idDB = cursor.getLong(DBAdapter.COL_ROWID);
            String name = cursor.getString(DBAdapter.COL_NAME);
            int studentNum = cursor.getInt(DBAdapter.COL_STUDENTNUM);
            String favColour = cursor.getString(DBAdapter.COL_FAVCOLOUR);

            String message = "ID: " + idDB + "\n"
                    + "Name: " + name + "\n"
                    + "Std#: " + studentNum + "\n"
                    + "FavColour: " + favColour;
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
        cursor.close();
    }
}
