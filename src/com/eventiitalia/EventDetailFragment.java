package com.eventiitalia;

import java.io.InputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eventiitalia.dummy.CityContent;

/**
 * A fragment representing a single uEvent detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailActivity}
 * on handsets.
 */
public class EventDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private CityContent.CityItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = CityContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;
	    LinearLayout llOfImage;
	    LinearLayout llOfText;
	    
	    public DownloadImageTask(ImageView bmImage,LinearLayout llOfImage,LinearLayout llOfText) {
	        this.bmImage=bmImage;
	    	this.llOfImage=llOfImage;
	        this.llOfText = llOfText;
	    }
	 
	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	         //   Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }
	 
	    protected void onPostExecute(Bitmap result) {
	        bmImage.setImageBitmap(result);
	        bmImage.getLayoutParams().width=200;
	        bmImage.getLayoutParams().height=200;
	        llOfImage.getLayoutParams().width=215;
	        llOfImage.getLayoutParams().height=215;
	    }
	}

    @Override
    public void onPause() {
        super.onPause();
        EventDialog.hide();
    }
	
	private class RestoreDBTask extends AsyncTask <Object, Void, String>
	{
	    private ArrayList<EventFeed> events;
	    private View rootView;
	    private LayoutInflater inflater;
	    private ViewGroup container;
	    private Bundle savedInstanceState;
		private CityContent.CityItem mItem;
		private boolean showDialog;

		public RestoreDBTask(boolean showDialog){
			super();
			this.showDialog=showDialog;
		}
		
	    @Override
	    protected void onPreExecute()
	    {
	    	if(this.showDialog)
                EventDialog.show(getActivity(), "Attendi", "sto scaricando le notizie");
	    }

	    @Override
	    protected void onPostExecute(String result) {	    	
	    	if(this.events!=null){

	    		//<SCROLLING AREA
	    		RelativeLayout relLayout=(RelativeLayout)rootView.findViewById(R.id.uevent_detail);
	    		
	    		LinearLayout scrollLayout=new LinearLayout(container.getContext());
	    		scrollLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    		ScrollView scrollView=new ScrollView(container.getContext());
	    		scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    		
	        	LinearLayout hLLayout=new LinearLayout(container.getContext());
	        	hLLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	        	hLLayout.setGravity(Gravity.LEFT|Gravity.TOP);
	        	hLLayout.setOrientation(LinearLayout.VERTICAL);
	    		
	    		relLayout.addView(scrollLayout);  
	    		scrollLayout.addView(scrollView);  
	    		scrollView.addView(hLLayout);  
	    		
	    		//SCROLLING AREA />
	    		
	        	
	        	for(int i=0;i<this.events.size();++i){        		
	        		LinearLayout wLL=new LinearLayout(container.getContext());
	        		wLL.setGravity(Gravity.LEFT|Gravity.TOP);  
	        		wLL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        		
	        		LinearLayout wLLdesc=new LinearLayout(container.getContext());  
	        		wLLdesc.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);  
	        		wLLdesc.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        		wLLdesc.setOrientation(LinearLayout.HORIZONTAL);
	        		
	        		TextView tvImage=null;
	        		LinearLayout wLImage=null;
	        		ImageView wImage=null;
	        		///////////////////////////////////////////////////////////
	        		if(!this.events.get(i).linkImage.equals("")){        			
		        		wImage=new ImageView(container.getContext()); 
		        		wLImage=new LinearLayout(container.getContext()); 	  
		        		wLImage.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);   
		        		wLImage.addView(wImage);  
	        		}
	
	        		tvImage = new TextView(container.getContext());
	        		tvImage.setText(this.events.get(i).title);
	        		tvImage.setText(Html.fromHtml(this.events.get(i).title+
												 "<br/><br/>"+
												 this.events.get(i).data+
												 "<br/><br/>"+
												 "<a href=\""+this.events.get(i).linkPage+"\">link</a>"));
	        		tvImage.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
	        		tvImage.setMovementMethod(LinkMovementMethod.getInstance());        		
	        		wLLdesc.addView(tvImage);        		
	        		///////////////////////////////////////////////////////////
	        		wLL.addView(wLImage);  
	        		wLL.addView(wLLdesc);  
	        		///////////////////////////////////////////////////////////
	        		// TASK LOAD IMAGE
	        		if(wImage!=null)
	        			new DownloadImageTask(wImage,wLImage,wLLdesc).execute(this.events.get(i).linkImage);           		
	        		///////////////////////////////////////////////////////////
	        		
	        		hLLayout.addView(wLL);
	        	}
	    	}
        	if(this.events==null || this.events.size()==0){ 
        		///////////////////////////////////////////////////////////   	  
	    		RelativeLayout relLayout=(RelativeLayout)rootView.findViewById(R.id.uevent_detail);
        		///////////////////////////////////////////////////////////   	        		
        		TextView showText=null;        		
        		showText = new TextView(container.getContext());
        	    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 
        	    																	 RelativeLayout.LayoutParams.MATCH_PARENT);
        	    showText.setLayoutParams(params);
        		showText.setText(Html.fromHtml("<p>Nessun evento presente per questa citta'</p>"));
        		showText.setTypeface(null, Typeface.BOLD);
        		showText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);        		
        		relLayout.addView(showText);
        		///////////////////////////////////////////////////////////   	  
        	}
	    	//close dialog
	    	if(showDialog){
                EventDialog.hide();
            }
	        //
	    }
	    
	    @Override
	    protected String doInBackground(Object... params)
	    {
	    	this.mItem=(CityContent.CityItem)params[0];
	    	
	    	if (this.mItem != null) {
	    		this.rootView=(View)params[1];
	    		this.inflater=(LayoutInflater)params[2];
	    		this.container=(ViewGroup)params[3];
	    		this.savedInstanceState=(Bundle)params[4];	    		
	    		this.events=EventUrls.getEvents(this.mItem.region,this.mItem.city);
	        }
	    	
	        return "";
	    }

	}
	
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    	// get root view
        View rootView = inflater.inflate(R.layout.fragment_uevent_detail, container, false); 
        // add events
        if(mItem!=null)
        	new RestoreDBTask(EventUrls.getNotCityEventsExist(mItem.region,mItem.city))
        	.execute(mItem,rootView,inflater,container,savedInstanceState);       
        /////////////////////////
        return rootView;
    }
}
