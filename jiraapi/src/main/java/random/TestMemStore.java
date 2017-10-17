package random;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestMemStore implements Runnable {
	
	int jobId; 
	TestMemStore(int jobId){
		this.jobId = jobId;
	}
	
	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(8);		
		for(int i =0 ;i<8;i++){
			executor.execute(new TestMemStore(i+1));
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
		 
		}
		
	}

	@Override
	public void run() {
		FileReader fileReader;		
		try {
			ClassLoader classLoader = TestMemStore.class.getClassLoader();
			File file = new File(classLoader.getResource("Job_"+jobId+".txt").getFile());
			fileReader = new FileReader(file);
			try (BufferedReader br = new BufferedReader(fileReader)) {
			    String line;
			    while ((line = br.readLine()) != null) {
			       MemStore.getInstanace().write(line.getBytes(), "job_"+jobId);
			    }
			} catch (IOException e) {
				e.printStackTrace();
			}
			MemStore.getInstanace().read("job_"+jobId);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
}
