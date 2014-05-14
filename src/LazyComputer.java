import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

public class LazyComputer {

	final static int WORKER_PORT = 12345;

	final int NUMBER_TO_COMPUTE = 10;

	private Queue<Double> list;
	private boolean pendingResult[];
	private final List<Thread> workerPool;
	private int workerIdx = 0;

	public LazyComputer(int numElements, int numWorkers) {
		workerPool = new ArrayList<Thread>(numWorkers);
		pendingResult = new boolean[numWorkers];
		Arrays.fill(pendingResult, false);

		// Initialize list of numbers
		list = new LinkedList<Double>();
		Random rand = new Random();
		for (int i = 0; i < numElements; i++) {
			double num = rand.nextDouble();
			list.add(num);
		}

	}

	public void addWorker(InetAddress workerHost, int workerPort) {
		workerPool.add(new Thread(new WorkerRunnable(workerHost, workerPort,
				workerIdx++)));
	}

	public synchronized void addNumber(double number) {
		list.add(number);
	}

	public synchronized List<Double> getNumbersToCompute() {
		List<Double> numberToCompute = new ArrayList<Double>();
		if (list.size() > 1) {
			int i = 0;
			while (i < NUMBER_TO_COMPUTE && !list.isEmpty()) {
				numberToCompute.add(list.poll());
				i++;
			}
		} else if (list.size() == 1) {
			numberToCompute.add(list.peek());
		}
		return numberToCompute;
	}

	private synchronized boolean hasPending() {
		for (int i = 0; i < pendingResult.length; i++) {
			if (pendingResult[i])
				return true;
		}
		return false;
	}

	private synchronized void setPending(int workerId, boolean pending) {
		pendingResult[workerId] = pending;
	}

	public void start() {
		// Start worker thread
		for (int i = 0; i < workerPool.size(); i++) {
			workerPool.get(i).start();
		}
		for (int i = 0; i < workerPool.size(); i++) {
			try {
				workerPool.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("The sum of the list is: " + list.peek());
	}

	private class WorkerRunnable implements Runnable {

		final int workerPort;
		final InetAddress workerAddr;
		Socket workerSocket;
		int workerId;

		WorkerRunnable(InetAddress host, int port, int index) {
			this.workerPort = port;
			this.workerAddr = host;
			this.workerId = index;
		}

		@Override
		public void run() {
			try {
				workerSocket = new Socket(this.workerAddr, this.workerPort);
				ObjectOutputStream oout = new ObjectOutputStream(
						workerSocket.getOutputStream());
				ObjectInputStream oin = new ObjectInputStream(
						workerSocket.getInputStream());
				//System.out.println("Connnected to "
				//		+ workerSocket.getRemoteSocketAddress().toString());
				while (true) {
					ComputeUnit unit = new ComputeUnit();
					List<Double> numbers = getNumbersToCompute();
					boolean hasPending = hasPending();
					if (numbers.size() == 1 && !hasPending) {
						break;
					} else if (numbers.size() == 1 && hasPending) {
						continue;
					} else if (numbers.size() == 0) {
						continue;
					}
					unit.addNumbersToCompute(numbers);
					setPending(this.workerId, true);
					oout.writeObject(unit);
					oout.flush();
					ComputeUnit resultUnit = (ComputeUnit) oin.readObject();
					addNumber(resultUnit.sum);
					setPending(this.workerId, false);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 2 || args.length > 3) {
			throw new IllegalArgumentException(
					"\nUsage: LazyComputer number_of_double number_of_workers <worker_host>");
		}
		int numElements = Integer.parseInt(args[0]);
		int numWorkers = Integer.parseInt(args[1]);
		LazyComputer lazyOne = new LazyComputer(numElements, numWorkers);
		if (args.length == 2) {
			Scanner scanner = new Scanner(System.in);
			for (int i = 0; i < numWorkers; i++) {
				System.out.print("Input host address of worker " + (i + 1)
						+ ": ");
				String host = scanner.nextLine();
				System.out.print("Input port of worker " + (i + 1) + ": ");
				int port = scanner.nextInt();
				scanner.nextLine();
				lazyOne.addWorker(InetAddress.getByName(host), port);
			}
		} else if (args.length == 3) {
			InetAddress host = InetAddress.getByName(args[2]);
			for (int i = 0; i < numWorkers; i++) {
				lazyOne.addWorker(host, WORKER_PORT + i);
			}
		}
		lazyOne.start();
	}
}
