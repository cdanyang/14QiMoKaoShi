import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class LazyComputer {

	final int WORKER_PORT = 12345;

	private Queue<Double> list;
	private int pendingResult = 0;
	private final int numWorkers;
	private final Thread workerPool[];

	public LazyComputer(int numElements, int numWorkers) {
		this.numWorkers = numWorkers;
		workerPool = new Thread[numWorkers];

		for (int i = 0; i < numWorkers; i++) {
			workerPool[i] = new Thread(new WorkerRunnable(WORKER_PORT + i));
		}

		// Initialize list of numbers
		list = new LinkedList<Double>();
		Random rand = new Random();
		for (int i = 0; i < numElements; i++) {
			list.add(rand.nextDouble());
		}
	}

	public synchronized void add() {

	}

	public void start() {
		// Connect to worker thread
	}

	private class WorkerRunnable implements Runnable {

		final int workerPort;

		WorkerRunnable(int port) {
			this.workerPort = port;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
