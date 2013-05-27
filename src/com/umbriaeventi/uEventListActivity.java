package com.umbriaeventi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.umbriaeventi.dummy.DummyContent;
import android.net.Uri;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.SearchView;


/**
 * An activity representing a list of uEvents. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link uEventDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link uEventListFragment} and the item details
 * (if present) is a {@link uEventDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link uEventListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class uEventListActivity extends FragmentActivity
        implements uEventListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    static private String[] cities=null;
    
    public void showDialogError(){
    	
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	    dialog.setIcon(R.drawable.ic_launcher);
	    dialog.setTitle("Errore");
	    dialog.setMessage("Errore di conessione, non vi sono citt� con eventi");
	    dialog.setPositiveButton("chiudi", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            dialog.dismiss();
            finish();
            System.exit(0);
        }
       });
	    AlertDialog alert=dialog.create();
	    alert.show();
    	
    }
    
    public boolean addCity(){
    	//
    	DummyContent.clear();
    	//download cities
    	if(cities==null) cities=uEventUrls.getCities();    	
    	//no cities, exit from app
    	if(cities==null) return false;    	
    	//add cities
    	for(int i=0;i<cities.length;++i){
    		DummyContent.addItem(
    				new DummyContent.DummyItem(
    						Integer.toString(i),cities[i]
    						));
    	}
    	return true;
    }
    
    private void setLayout(){

    	//set layout
	    setContentView(R.layout.activity_uevent_list);
	
	    if (findViewById(R.id.uevent_detail_container) != null) {
	        // The detail container view will be present only in the
	        // large-screen layouts (res/values-large and
	        // res/values-sw600dp). If this view is present, then the
	        // activity should be in two-pane mode.
	        mTwoPane = true;
	
	        // In two-pane mode, list items should be given the
	        // 'activated' state when touched.
	        ((uEventListFragment) getSupportFragmentManager()
	                .findFragmentById(R.id.uevent_list))
	                .setActivateOnItemClick(true);
	    }
	    //set serch menu events
	    SearchView srchCity=(SearchView) findViewById(R.id.searchCity);
	    srchCity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
            	searchCity(newText);
                return true;
            }
        });
    }
    
    private void searchCity(String name){
    	uEventListFragment uelf=((uEventListFragment) getSupportFragmentManager() .findFragmentById(R.id.uevent_list));
    	uelf.setFilterList(name);
    }
    
    private class DownloadCitiesTask extends AsyncTask<String, Void, Object> {
    	
    	 boolean connesionExt=true;
    	
        protected Object doInBackground(String... args) {
        	long start = System.currentTimeMillis();
            //add cities
            connesionExt=addCity();
            //get end time
        	long end = System.currentTimeMillis();
        	//calc screen time
        	long timepass=start-end;
        	//sleep
        	if(timepass<1000){
				try {
					Thread.sleep(1000-timepass);
				}
				catch (InterruptedException e) {
					Log.e("task error",e.toString());
				}
        	}
        	//
            return null;
        }

        protected void onPostExecute(Object result) {
        	//set layout
        	setLayout();
            //show dialog errors
            if(!connesionExt) 
            	showDialogError();
	        //force selection first item   
            if(mTwoPane){
		        Thread thread = new Thread(){        	
		        	public void run(){
						try {
							Thread.sleep(100);//lol...
			                onItemSelected("0");
						}
						catch (InterruptedException e) {
							Log.e("task error",e.toString());
						}     		
		        	}
		        };
		        thread.start();
            }
        }
   }    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {    
    	//connection policy 
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy);	
        //save state
        super.onCreate(savedInstanceState);
        //only at start
        if(cities==null){
	        //set splash screen
	        setContentView(R.layout.activity_splash_screen);
	        //create interface
	        new DownloadCitiesTask().execute();
        }
        else{
        	//no download
        	addCity();
        	//set layout
        	setLayout();
        } 	
    }

    /**
     * Callback method from {@link uEventListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(uEventDetailFragment.ARG_ITEM_ID, id);
            uEventDetailFragment fragment = new uEventDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.uevent_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, uEventDetailActivity.class);
            detailIntent.putExtra(uEventDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
