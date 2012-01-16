public class UpdateThread implements Runnable{
	private boolean running = false;
	private Thread thread;
	private SearchIds ids;

	public UpdateThread(SearchIds ids){
		this.ids = ids;
	}

	public void run() {
		while (this.running) {
			try {
				Thread.sleep(SearchIds.autoUpdateInterval * 1000);
			} catch (InterruptedException localInterruptedException) {
			}
			this.ids.updateData();
		}
	}

	public void start() {
		this.running = true;
		this.thread = new Thread(this);
		this.thread.start();
	}

	public void stop() {
		this.running = false;
		this.thread.interrupt();
	}
}