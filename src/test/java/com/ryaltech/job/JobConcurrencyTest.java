package com.ryaltech.job;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JobConcurrencyTest.class })
@EnableTransactionManagement
public class JobConcurrencyTest extends BaseJobTest implements Callback{
	private static Logger logger = Logger.getLogger(JobConcurrencyTest.class);
	
	@Bean
	PlatformTransactionManager txManager(){
		return new DataSourceTransactionManager(ds);
		
	}
	@Bean
	MockClusteredJob getMockClusteredJob() {
		return new MockClusteredJob();
	}
	@Before
	public void setUp1(){
		counter = 0;
	}

	@Autowired
	MockClusteredJob job;
	private volatile int counter;
	
	public void call() {
		logger.debug("call");
		System.out.println("Counter increased in object "+hashCode());
		
		counter ++;
		
		if(counter == 1){
			Thread concurrentThread = 
			new Thread(){
				public void run() {job.execute(JobConcurrencyTest.this);};
			};
			concurrentThread.start();
			try{
				concurrentThread.join();			
			}catch(Exception ex){}
		}
		
	}
	
	@Test
	public void testConcurrencyWithEntryPrecreate(){
		logger.debug("testConcurrencyWithEntryPrecreate");
		//purely to create an entry to contend on
		job.execute(null);
		job.execute(this);		
		assertEquals(1, counter);
	}
	

	@Test	
	public void testConcurrencyWithoutEntryPrecreate(){
		logger.debug("testConcurrencyWithoutEntryPrecreate");		
		job.execute(this);		
		assertEquals(1, counter);
		counter=0;
		job.execute(this);		
		assertEquals(1, counter);
	}

}
