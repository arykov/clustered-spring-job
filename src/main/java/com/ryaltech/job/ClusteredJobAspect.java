package com.ryaltech.job;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ClusteredJobAspect {
	@Autowired
	ClusteredJobInvoker tji;

	static Logger logger = Logger.getLogger(ClusteredJobAspect.class);

	@Around("execution(@ClusteredJob void *.*(..))")
	public Object aroundJobInvocation(final ProceedingJoinPoint joinPoint)
			throws Throwable {
		tji.invokeJob(joinPoint);
		//TODO: consider adding exception logging to notify of misconfigurations
		return null;		
	}
}
