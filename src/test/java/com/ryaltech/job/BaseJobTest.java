package com.ryaltech.job;

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;


@EnableAspectJAutoProxy
@ComponentScan({ "com.ryaltech.job" })
@Configuration
public abstract class BaseJobTest {
	private static Logger logger = Logger.getLogger(BaseJobTest.class);
	@Before
	public void setUp() throws Exception {
		logger.debug("setUp");
		ds.getConnection().prepareStatement("runscript from 'classpath:h2/create_alias.sql'").execute();
		ds.getConnection()
				.prepareStatement(
						"runscript from 'classpath:com/ryaltech/job/cr_sii_job.sql'")
				.execute();
		assertEquals(new Integer(0), new JdbcTemplate(getDataSource()).query("select count(*) from JOB_DEFINITION", new ResultSetExtractor<Integer>(){
			@Override
			public Integer extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				rs.next();
				return rs.getInt(1);
			}
		}));


	}

	@After
	public void tearDown() throws Exception {
		logger.debug("tearDown");
		ds.getConnection().prepareStatement("DROP table JOB_DEFINITION").execute();

	}
	
	@Bean
	public DataSource getDataSource() {
		/*
		return new SingleConnectionDataSource("jdbc:h2:mem:db;MODE=Oracle",
				"sa", "", true);
		*/
		JdbcDataSource ds = new JdbcDataSource(); 
		ds.setURL("jdbc:h2:mem:db"+getClass().getSimpleName()+";DB_CLOSE_DELAY=-1;MODE=Oracle");		
		return ds;
		
	}
	@Autowired
	DataSource ds;


}
