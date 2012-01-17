public class UpdateThread implements Runnable{
	private boolean running = false;
	private Thread thread;
	private SearchIds ids;

	public UpdateThread(SearchIds ids){
		this.ids = ids;
	}
	
	public void run() {
		while (running) {
			try {
				Thread.sleep(SearchIds.autoUpdateInterval);
			} catch (InterruptedException IE) {
				if(running){ //Only send message if running
					SearchIds.log.warning("[SearchIds] An Error occured in UpdateThread.");
				}
			}
			if(running){ //Make sure we don't update if not running
				ids.updateData(SearchIds.updateSource);
			}
		}
	}

	public void start() {
		running = true;
		thread = new Thread(this);
	}

	public void stop() {
		running = false;
		thread.interrupt();
		thread = null;
	}
}