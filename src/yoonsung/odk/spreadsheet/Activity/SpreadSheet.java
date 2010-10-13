package yoonsung.odk.spreadsheet.Activity;

import java.util.ArrayList;
import java.util.Arrays;

import yoonsung.odk.spreadsheet.R;
import yoonsung.odk.spreadsheet.DataStructure.Table;
import yoonsung.odk.spreadsheet.Database.Data;
import yoonsung.odk.spreadsheet.Database.TableProperty;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Main acitivity that displays the spread sheet.
 * 
 * 
 * @Author : YoonSung Hong (hys235@cs.washington.edu)
 */
public class SpreadSheet extends Activity {
	
	// Menu Ids
	private static final int LOAD_NEW_FILE = 0;
	private static final int COLUMN_MANAGER_ID = Menu.FIRST;
	private static final int SAVE_NEW_ID = COLUMN_MANAGER_ID + 1;
	private static final int SELECT_COLUMN = SAVE_NEW_ID + 1;
	private static final int SEND_SMS_ROW = SELECT_COLUMN + 1;
	private static final int HISTORY_IN = SEND_SMS_ROW + 1;
	
	// Data structure for table/spread sheet
	private Data data;
	
	// Last-touch by the user
	private Table currentTable;
	private int currentCellLoc;
		
	// Refresh data and draw a table on screen.
	public void init() {
		// Data strucutre will represent the table/spread sheet
		this.data = new Data();
		
		// Get table data
		this.currentTable = data.getTable();
		    	
    	// Draw the table
    	noIndexFill(currentTable);
	}
	
    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_layout);
       
        // Initialize
        init();
        
        // Enter button
        Button enter = (Button)findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
        	// READ INPUT FROM EDITTEXT AND UPDATE CORRESPONDING CELL IN
        	// THE TABLE AND EXCEL OBJECT DATA STRUCTURE.
			@Override
			public void onClick(View v) {
				
				// Get the input on EditText
				EditText edit = (EditText)findViewById(R.id.edit_box);
				String currentStr = edit.getText().toString();
			
				// Update the original cell's content
				TextView currentCell = (TextView)findViewById(currentCellLoc);
				currentCell.setText(currentStr);
				
				// Update changes in DB
			}
		});
     
    }
    
    /* Get back to the main activity */
    @Override
    public void onResume() {
    	super.onResume();
    	
    	// Refresh data table & re-draw
    	init();
    }
    
   
    // Create a cell with this value.
    private TextView createCell(String str) {
    	// Create a cell
    	TextView cell = new TextView(this);
    	
    	// Cell configurations
    	cell.setBackgroundColor(getResources().getColor(R.color.Avanda));
    	cell.setText(str);
    	cell.setTextColor(getResources().getColor(R.color.black));
    	cell.setPadding(5, 5, 5, 5);
    	cell.setClickable(true);
    	
    	// Reaction when the cell is clicked by users
        cell.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Get the content of the cell
				TextView tv = (TextView) v;
				CharSequence selected_text = tv.getText();
				
				// TEMP
				TextView led = (TextView)findViewById(R.id.led);
				led.setText(Integer.toString(tv.getId()));
			
				// Register current cell location
				currentCellLoc = tv.getId();
				
				// Allow users to edit on this cell
				EditText box = (EditText)findViewById(R.id.edit_box);
				box.setText(selected_text);
				
			}
		});
 
        // Menu options that ecah cell will have.
        cell.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// Update current cell location
				TextView tv = (TextView) v;
				currentCellLoc = tv.getId();
				
				// Options on this cell
				menu.add(0, SELECT_COLUMN, 0, "Select This Column");
				menu.add(0, SEND_SMS_ROW, 0, "Send SMS On This Row");
				menu.add(0, HISTORY_IN, 0, "History in");
			}
		});      
       
        return cell;
    }
   
    // Create an index cell that will be placed on left-most column.
    private TextView createIndexCell(String str) {
    	// Create a cell
    	TextView cell = new TextView(this);
    	cell.setText(str);
    	
    	// Configurations
    	cell.setBackgroundColor(getResources().getColor(R.color.Beige));
    	cell.setTextColor(getResources().getColor(R.color.black));
    	cell.setPadding(5, 5, 5, 5);
    	cell.setClickable(true);
    	
    	// When any index cell is long clicked, remove the index column.
    	cell.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// U
				currentTable = data.getTable();
				noIndexFill(currentTable);
				return true;
			}
		});
    		
    	return cell;
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
    	
    	switch(item.getItemId()) {
	    case SELECT_COLUMN:	// Set this column as an index column
			// Get index column content
			currentTable = data.getTable();
			ArrayList<String> indexCol = currentTable.getCol(currentTable.getColNum(currentCellLoc));
			
			// Header
			ArrayList<String> header = new ArrayList<String>(Arrays.asList(currentTable.getColName(currentTable.getColNum(currentCellLoc))));
			
			// Create an index table
			Table indexTable = new Table(1, indexCol.size(),  null, header, indexCol, null);

			// Fill index table and data table
			withIndexFill(currentTable, indexTable);
			return true;
	    case SEND_SMS_ROW: // Send SMS on this row
	    	// Get the current row
	    	ArrayList row = currentTable.getRow(currentTable.getRowNum(currentCellLoc)-1);
	    	
	    	// Encode the row information into a string
	    	String content = "";
	    	for (int i = 0; i < row.size(); i++) {
	    		content += row.get(i) + " ";
	    	}
	    	
	    	//SmsSender sender = new SmsSender();
	    	//sender.sendSMS("2062614018", content);
	    	sendSMS("2062614018", content);
	    	return true;
	    case HISTORY_IN: // Draw new table on this history
	    	String selectedColName = currentTable.getColName(currentTable.getColNum(currentCellLoc - currentTable.getWidth()));
	    	String selectedValue = currentTable.getCellValue(currentCellLoc - currentTable.getWidth());
	    	Log.e("checkpoint", selectedColName + " " + selectedValue);
	    	currentTable = data.getTable(selectedColName, selectedValue);
	    	noIndexFill(currentTable);
	    	return true;
	    }
	    return super.onContextItemSelected(item);
	}
    
    // CREATE OPTION MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, COLUMN_MANAGER_ID, 0, "Column Manager");
        menu.add(0, SAVE_NEW_ID, 1, "Save");
        return true;
    }
    
    // HANDLE OPTION MENU
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        
    	// HANDLES DIFFERENT MENU OPTIONS
    	switch(item.getItemId()) {
        // OPEN NEW FILE THROUGH A FILE MANGER
        case COLUMN_MANAGER_ID:
        	Intent i = new Intent(this, ColumnManager.class); 	
        	//i.putStringArrayListExtra("colOrder", currentTableProperty.getColOrder());
        	startActivityForResult(i, LOAD_NEW_FILE);
        	return true;
        // SAVE CURRENTLY LOEADED FILE TO THE ORIGINAL PATH.
        case SAVE_NEW_ID:
            return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        /*
        // MESSAGES FROM FILE MANAGER
        Bundle extras = intent.getExtras();

        // HANLDLES LOAIDING A NEW FILE.
        switch(requestCode) {
        // CALL BACK FOR LOAD_NEW_FILE.
        case LOAD_NEW_FILE:
        	// Load table from DB
        	DBIO dbio = new DBIO();
        	Table table = dbio.DBToTable(1);
        	
        	// Debug
        	Log.d("TABLE", table.getTableData().toString());
        	
        	// Draw the table
        	noIndexFill(table);  	
        	break;
        }
        */
    }

    
    private void noIndexFill(Table table) {
    	// EMPTY THE TABLES
    	TableRow mainLayout = (TableRow)findViewById(R.id.mainLayout);
    	mainLayout.removeAllViews();
    	
    	HorizontalScrollView scrollView = new HorizontalScrollView(this);
    	
    	TableLayout tableLayout = fill(false, table);
    	
    	scrollView.addView(tableLayout);
    	//mainLayout.addView(scrollView, mainLayout.getWidth(), LayoutParams.FILL_PARENT);
    	mainLayout.addView(scrollView, LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
    }
 
    private void withIndexFill(Table data, Table index) {
    	// SET INDEX TABLE EMPTY / INVISIBLE 
    	TableRow mainLayout = (TableRow)findViewById(R.id.mainLayout);
    	mainLayout.removeAllViews();
        
    	// INDEX COLUMN
    	HorizontalScrollView indexScrollView = new HorizontalScrollView(this);
    	TableLayout indexTable = fill(true, index);
    	indexScrollView.addView(indexTable);
    	mainLayout.addView(indexScrollView, 80, LayoutParams.FILL_PARENT); 
    	Log.e("index width", "" + indexTable.getWidth());
    	
    	// DATA COLUMN
    	HorizontalScrollView dataScrollView = new HorizontalScrollView(this);
    	TableLayout dataTable = fill(false, data);
    	dataScrollView.addView(dataTable);
    	mainLayout.addView(dataScrollView, 
    						mainLayout.getWidth() - 80, 
    						LayoutParams.FILL_PARENT);
    	mainLayout.setWeightSum(1.0f);
    }

    private TableLayout fill(boolean isIndex, Table table) {		
    	TableLayout tableLayout = new TableLayout(this);
    	tableLayout.setBackgroundColor(getResources().getColor(R.color.black)); // added
    	
    	// Header
    	TableRow header = new TableRow(this);
    	int i = 0;
    	for (String colName : table.getHeader()) {
    		TextView tv;
    		if (isIndex) {
        		tv = createIndexCell(colName);
        		tv.setBackgroundColor(getResources().getColor(R.color.header_index));
        	} else {
        		tv = createCell(colName);
        		tv.setBackgroundColor(getResources().getColor(R.color.header_data));
        		tv.setId(i);
        		i++;
        	}
    		
    		LinearLayout headerLl = new LinearLayout(this);
        	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        		     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        	layoutParams.setMargins(1, 1, 1, 1);
        	headerLl.addView(tv, layoutParams);
    		
	        header.addView(headerLl);
    	}
    	// ADD THE ROW TO THE TABLELAYOUT
        tableLayout.addView(header, new TableLayout.LayoutParams(
        					LayoutParams.FILL_PARENT,
        					LayoutParams.WRAP_CONTENT));
    	
    	// Table Data   	
    	for (int r = 0; r < table.getHeight(); r++) {
    		TableRow row = new TableRow(this);
    		Log.d("Row", table.getRow(r).toString());
    		for (int c = 0; c < table.getWidth(); c++) {
    			TextView tv;
	        	if (isIndex) {
	        		tv = createIndexCell((String)table.getRow(r).get(c));
	        	} else {
	        		tv = createCell((String)table.getRow(r).get(c));
	        		tv.setId((r+1)*table.getWidth() + c);
	        	}

	        	LinearLayout dataLl = new LinearLayout(this);
	        	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
	        		     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

	        	layoutParams.setMargins(1, 1, 1, 1);
	        	dataLl.addView(tv, layoutParams);
	        	
		        row.addView(dataLl);
    		}
    		
    		// ADD THE ROW TO THE TABLELAYOUT
	        tableLayout.addView(row, new TableLayout.LayoutParams(
	        					LayoutParams.FILL_PARENT,
	        					LayoutParams.WRAP_CONTENT));
    	}
    	
        return tableLayout;
    }
    
  //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message)
    {        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
 
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
 
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));
 
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        
 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);        
    }
    
}