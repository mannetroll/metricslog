package com.mannetroll.metrics.logger;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.mannetroll.metrics.util.LogKeys;

public class JsonLayoutTest {
	private static final Logger LOG = LogManager.getLogger(JsonLayoutTest.class);

	//
	// -Dlog4j2.enable.threadlocals=false
	//

	@Test
	public void test() {
		Map<String, Object> tmp = new HashMap<String, Object>();
		tmp.put(LogKeys.MESSAGE, "The message goes here!");
		tmp.put("a_string", "hello");
		tmp.put("a_number", 11L);
		tmp.put("a_float", 1.1234F);
		tmp.put("a_boolean", true);
		LOG.info(tmp);
	}

}
