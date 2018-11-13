package com.ryaltech.job;

import com.ryaltech.job.ClusteredJob;

public class MockClusteredJob {
	
	
	
	@ClusteredJob
	public void execute(Callback callback){		
		if(callback != null )callback.call();
	}

}
