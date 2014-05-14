import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WorkerComputer implements Runnable {
	final static int WORKER_PORT = 12345;

	private final int port;

	public WorkerComputer(int port) {
		this.port = port;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException(
					"\nUsage: java WorkerComputer <port> (>= 10000) "
							+ "or \njava WorkerComputer <numer_of_worers> (<=10)");
		}
		int arg = Integer.parseInt(args[0]);
		if (arg <= 10) {
			// It's number of workers
			Thread workerPool[] = new Thread[arg];
			for (int i = 0; i < arg; i++) {
				workerPool[i] = new Thread(new WorkerComputer(WORKER_PORT+i));
				workerPool[i].start();
			}
			for (int i = 0; i < arg; i++) { 
				try {
					workerPool[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (arg >= 10000) {
			WorkerComputer worker = new WorkerComputer(arg);
			worker.run();
		} else {
			throw new IllegalArgumentException(
					"\nUsage: java WorkerComputer <port> (>= 10000) "
							+ "or \njava WorkerComputer <numer_of_worers> (<=10)");
		}
	}

	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(port);
			Socket conn = server.accept();
			System.out.println("Connected by "
					+ conn.getRemoteSocketAddress().toString());
			ObjectOutputStream oout = new ObjectOutputStream(
					conn.getOutputStream());
			ObjectInputStream oin = new ObjectInputStream(conn.getInputStream());
			while (true) {
				ComputeUnit unit = (ComputeUnit) oin.readObject();
				double sum = 0;
				for (int i = 0; i < unit.numbersToCompute.size(); i++) {
					sum += unit.numbersToCompute.get(i);
				}
				unit.setSum(sum);
				unit.numbersToCompute.clear();
				oout.writeObject(unit);
				oout.flush();
			}
		} catch (Exception e) {
			return;
		}

	}
}
