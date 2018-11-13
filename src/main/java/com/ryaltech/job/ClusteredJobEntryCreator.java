package com.ryaltech.job;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ClusteredJobEntryCreator {
	private static Logger logger = Logger.getLogger(ClusteredJobEntryCreator.class);
	@Autowired
	private DataSource ds;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createMissingEntry(String jobCode) {	
		new JdbcTemplate(ds).update("insert into JOB_DEFINITION(JOB_NAME_CD) values(?)", jobCode);		
	}

}
