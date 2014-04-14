package at.modalog.cordova.plugin.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import android.webkit.ConsoleMessage;

public class CordovaChromeClientLog extends CordovaChromeClient
{
	// change your path on the sdcard here
	private String logDir = ".at.modalog.cordova.plugin.log"; // prepending a dot "." makes the directory invisible
	private String logFileName = "CordovaChromeClient.log";
	private File logFile;
	private double logFileMaxSizeInKiloBytes = 512;
    private String TAG = "CordovaLog";
    
	public static CordovaChromeClient makeChromeClient( CordovaActivity cordova, CordovaWebView webView )
	{
		return new CordovaChromeClientLog(cordova, webView);
	}
	
    /**
     * Constructor.
     *
     * @param cordova
     */
    public CordovaChromeClientLog(CordovaInterface cordova)
    {
        super( cordova );
        init();
    }

    /**
     * Constructor.
     * 
     * @param ctx
     * @param app
     */
    public CordovaChromeClientLog(CordovaInterface ctx, CordovaWebView app) {
        super( ctx, app );
        init();
    }
    
    public void init()
    {
        // make log dir
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/" + this.logDir + "/");
        if( !dir.exists() )
        {
        	dir.mkdirs();
        }
        
        // create .nomedia file to avoid indexing log files
        File noMediaFile = new File(dir.getAbsolutePath() + "/", ".nomedia");
        if( !noMediaFile.exists() ) 
        {
        	try
        	{
        		noMediaFile.createNewFile();
	        }
			catch(Exception e){}
        }
        
        // create log file if necessary
        this.logFile = new File(dir.getAbsolutePath() + "/", this.logFileName);
        if( !this.logFile.exists() )
        {
        	try
        	{
        		this.logFile.createNewFile();
	        }
			catch(Exception e)
			{
				e.printStackTrace();
				LOG.e( TAG, "Java error: " + e.getMessage() );
			}
        }
    }
    
    // console.log in api level 7: http://developer.android.com/guide/developing/debug-tasks.html
    // Expect this to not compile in a future Android release!
    @SuppressWarnings("deprecation")
    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID)
    {
        //This is only for Android 2.1
        if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.ECLAIR_MR1)
        {
            // save log into file
        	Formatter f = new Formatter();
        	String msg = f.format("%s: Line %d : %s", sourceID, lineNumber, message).toString(); 
        	this.appendToLog(msg );
            
            super.onConsoleMessage(message, lineNumber, sourceID);
        }
    }

    @TargetApi(8)
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage)
    {
        if (consoleMessage.message() != null)
        {
            // save log into file
        	Formatter f = new Formatter();
        	String msg = f.format("%s: Line %d : %s", consoleMessage.sourceId(), consoleMessage.lineNumber(), consoleMessage.message()).toString(); 
        	this.appendToLog(msg );
        }
         return super.onConsoleMessage(consoleMessage);
    }
    
    public void appendToLog( String msg )
    {
    	FileWriter fw = null;
    	
    	// shorten file content if it´s too big
    	if( this.logFile.length() / 1024 > this.logFileMaxSizeInKiloBytes )
    	{
    		String cache = "";
    		String line;
    		BufferedReader r = null;
    	    try
    	    {
    	    	r = new BufferedReader(new FileReader(this.logFile));
    	    	r.skip((long)(this.logFile.length() * 0.3));
			  	do
			  	{
				  line = r.readLine();
				  if( line != null )
				  {
					  cache += "\n" + line;
				  }
			    }
			    while(line != null);
    	      
				try
				{
					fw = new FileWriter( this.logFile, false );
			        fw.write( cache );
			        fw.flush();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					LOG.e( TAG, "Java error: " + e.getMessage() ); 
				}
				finally
				{
					try
					{
						if(fw != null)
						{
							fw.close();
						}
					}
					catch(Exception e){}
				}
    	    }
    	    catch(Exception e)
    	    {
    	    	e.printStackTrace();
				LOG.e( TAG, "Java error: " + e.getMessage() );
    	    }
    	    finally
    	    {
    	    	try
    	    	{
    	    		if( r != null)
    	    		{
    	    			r.close();
    	    		}
	    	    }
				catch(Exception e){}
    	    }
    	}
    	
        // save log into file
		try
		{
			fw = new FileWriter( this.logFile, true );
			// add Date & Time to msg
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
			msg = sdf.format(new Date()) + ":  " + msg;
			fw.append( "\n" + msg );
	        fw.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LOG.e( TAG, "Java error: " + e.getMessage() ); 
		}
		finally
		{
			try
			{
				if(fw != null)
				{
					fw.close();
				}
			}
			catch(Exception e){}
		}
    }
}
