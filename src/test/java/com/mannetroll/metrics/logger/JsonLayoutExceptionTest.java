package com.mannetroll.metrics.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class JsonLayoutExceptionTest {
	private static final Logger LOG = LogManager.getLogger(JsonLayoutExceptionTest.class);

	@Test
	public void test() {
		RuntimeException ex = new RuntimeException("hello?");
		LOG.info("Something went wrong!", ex);
	}

}
