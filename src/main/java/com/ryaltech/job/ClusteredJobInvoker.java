package com.ryaltech.job;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * Exists exclusively to allow @transactional annotation. Would be good to have this within ClusteredJobAspect
 * @author rykov
 *
 */
@Component
public class ClusteredJobInvoker {	
	private static Logger logger = Logger.getLogger(ClusteredJobInvoker.class);
	@Autowired
	private ClusteredJobEntryCreator jobEntryCreator;
	@Autowired
	private DataSource ds;
	public boolean lock(String jobCode){
		return lock(jobCode, true);
	}
	private boolean lock(String jobCode, boolean createEntryIfMissing){
		try{
			if("Y".equalsIgnoreCase(new JdbcTemplate(ds).queryForObject("select ENABLED_FLG from JOB_DEFINITION where JOB_NAME_CD=? for UPDATE NOWAIT", String.class, jobCode)))return true;
			else{
				logger.info(String.format("Skipping job: %s since it is disabled", jobCode));
				return false;
			}
		}catch(EmptyResultDataAccessException erdae){
			logger.debug(String.format("missing job entry: %s", jobCode), erdae);
			if(createEntryIfMissing){
				logger.debug(String.format("Trying to create missing job entry: %s", jobCode));				
				jobEntryCreator.createMissingEntry(jobCode);
				return lock(jobCode, false);
			}else{
				throw erdae;
			}		
						
		}catch(CannotAcquireLockException ex){
			logger.debug(String.format("Skipping job: %s due to another instance running", jobCode));			
		}catch(BadSqlGrammarException ex){
			logger.fatal("JOB_DEFINITION table has likely not been created", ex);			
		}
		
		return false;
		
	}
	
	
	@Transactional
	public void  invokeJob(ProceedingJoinPoint joinPoint) throws Throwable{
		String jobCode = joinPoint.getTarget().getClass().getName()+"."+joinPoint.getSignature().getName();
		
		if(!TransactionSynchronizationManager.isActualTransactionActive()){
			logger.fatal(String.format("Transactions are not configured correctly. This prevents job: %s from executing.", jobCode));
			throw new RuntimeException(String.format("Transactions are not configured correctly. This prevents job: %s from executing.", jobCode));
		}
		if(lock(jobCode)){
			joinPoint.proceed();
		}
	}
}
