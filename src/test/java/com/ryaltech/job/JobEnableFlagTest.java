package com.ryaltech.job;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JobEnableFlagTest.class })
@EnableTransactionManagement
public class JobEnableFlagTest extends BaseJobTest implements Callback{
	
	
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
		//purely to create an entry to contend on
		counter = 0;
		job.execute(null);		
	}

	@Autowired
	MockClusteredJob job;
	private volatile int counter;
	
	public void call() {
		counter ++;		
	}
	
	@Test
	public void testEnabled(){
		
		job.execute(this);		
		assertEquals(1, counter);
	}
	

	@Test	
	public void testDisabled(){
		try{
			new JdbcTemplate(ds).execute("UPDATE JOB_DEFINITION set ENABLED_FLG='N'");
			job.execute(this);
			assertEquals(0, counter);
		}finally{
			new JdbcTemplate(ds).execute("UPDATE JOB_DEFINITION set ENABLED_FLG='Y'");
			
		}
	}

}
