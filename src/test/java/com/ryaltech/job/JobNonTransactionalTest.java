package com.ryaltech.job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JobNonTransactionalTest.class })

public class JobNonTransactionalTest extends BaseJobTest {

	@Bean
	MockClusteredJob getMockClusteredJob() {
		return new MockClusteredJob();
	}

	@Autowired
	MockClusteredJob job;

	@Test
	public void testNonTransactionalExecution() {
		try {
			job.execute(null);
			fail("expected to fail due to no transactions");
		} catch (RuntimeException ex) {
			assertEquals(
					"Transactions are not configured correctly. This prevents job: com.ryaltech.job.MockClusteredJob.execute from executing.",
					ex.getMessage());

		}

	}

}
