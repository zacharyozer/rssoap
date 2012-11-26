package com.rssoap.control;
/**
 * FeedChecker is a thread that checks what feeds need to get updated every thirty 
 * seconds and then tells the controller to update them.
 * @author pgroudas
 *
 */
/*
 * Depends on Controller
 */
public class FeedChecker extends Thread {
    /**
     * Constructs a new FeedChecker
     *
     */
	FeedChecker() {
		//makes this thread a daemon thread so it closes when the Controller quits
		setDaemon(true);
    }
	/**
	 * Invoked to start the thread.  This thread checks calls updateFeeds() on the controller 
	 * every 30 seconds.
	 */
    public void run() {
		try{
			//wait for 10 seconds so as not to slow down the start up time of the application
			sleep(10000);
			while(true){
				//sleep for thirty seconds
				sleep(30000);
				//update appropriate feeds
				Controller.getApp().updateFeeds();
			}
		}
		catch(InterruptedException e){
			//do nothing
		}
	}
}