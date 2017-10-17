package random;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Set;



public class MemStore {
	String fileName = "abc.dat"; 
	RandomAccessFile accessFile;
	MappedByteBuffer outBuffer;
	JobMeta jobMeta ;
	private static volatile MemStore me;
	private MemStore(){
		try {
			accessFile = new RandomAccessFile(fileName,"rw");
			FileChannel fc = accessFile.getChannel();
			outBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, 1024*1024);
			jobMeta = new JobMeta();			
		} catch (IOException e) {
			
		}
	}
	
	public static  MemStore getInstanace(){
		if (me == null ){
			synchronized (MemStore.class) {
				if (null == me){
					me = new MemStore();
				}
			}
		}
		return me;
	}
	
	public  synchronized void write(byte[] data,String jobId){		
		int remaining = outBuffer.remaining();
		if(remaining <  data.length ) {
			flushMemFile();
		}		
		outBuffer.position(jobMeta.getLastFilePointer());
		int startIndex = outBuffer.position();		
		outBuffer.put(data);
		outBuffer.put("\n".getBytes());
		int endIndex = outBuffer.position();
		jobMeta.addMetaDetails(jobId,startIndex,endIndex);
		jobMeta.setLastFilePointer(outBuffer.position());
		
	}
	
	
	private void flushMemFile() {		
		Set<String> jobIds = jobMeta.getJobIds();		
		for(String jodId : jobIds){
			flushJob(jobMeta.getPageIndex(jodId));
			jobMeta.removeJob(jodId);
		}	
		jobMeta.setLastFilePointer(0);
	}
	
	private void flushJob(List<Integer[]> pageIndexs ){
		for(Integer []  position : pageIndexs){
			outBuffer.position(position[0]);			
			int length = position[1]-position[0];
			//System.out.println( "length "+ length +" offset "+position.getStartPosition());
			byte[] rawBytes = new byte[length];
			outBuffer.get(rawBytes, 0,length);
			System.out.println(new String(rawBytes));
			//position.getEndPosition();
		}
		
	}
	
	public void close() {
		try {
			accessFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void  read(String jobId) {
		flushJob(jobMeta.getPageIndex(jobId));		
	}
}
