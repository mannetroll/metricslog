package com.mannetroll.metrics.logger;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class JsonLayoutTest {
	private static final Logger LOG = LogManager.getLogger(JsonLayoutTest.class);

	@Test
	public void test() {
		System.setProperty("log4j2.enable.threadlocals", "false");
		Map<String, Object> tmp = new HashMap<String, Object>();
		tmp.put("a_string", "hello");
		tmp.put("a_number", 11L);
		tmp.put("a_float", 1.1234F);
		tmp.put("a_boolean", true);
		LOG.info(tmp);
	}

}
