package random;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JobMeta implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6807997019084776552L;
	
	private Map<String,List<Integer[]>> jobMeta = new HashMap<>();	
	private int lastFilePointer =0;
	
	
	/**
	 * 
	 * @param jobId
	 * @param startIndex
	 * @param endIndex
	 */
	public void addMetaDetails(String jobId, int startIndex, int endIndex) {
		List<Integer[]>  job ; 
		if(jobMeta.containsKey(jobId)){
			job = jobMeta.get(jobId);			
		}else{
			job =  new ArrayList<>();	
			jobMeta.put(jobId, job);
		}
		addPosition(job, startIndex, endIndex);		
	}

	/**
	 * 
	 * @param startPosition
	 * @param endPosition
	 */
	public void addPosition(List<Integer[]> positions, int startPosition, int endPosition) {
		if (!positions.isEmpty()) {
			Integer[] position = positions.get(positions.size() - 1);
			if (position[1] == startPosition) {
				position[1] = endPosition;
				return;
			}
		}
		Integer[] position = { startPosition, endPosition };
		positions.add(position);

	}


	public int getLastFilePointer() {
		return lastFilePointer;
	}


	public void setLastFilePointer(int lastFilePointer) {
		this.lastFilePointer = lastFilePointer;
	}
	
	public Set<String> getJobIds(){
		return jobMeta.keySet();		
	}
	
		
	/**
	 * 
	 * @param jodId
	 */
	public void removeJob(String jodId) {
		jobMeta.remove(jodId);		
	}

	public List<Integer[]> getPageIndex(String jodId) {		
		return jobMeta.get(jodId);
	}
	

}
