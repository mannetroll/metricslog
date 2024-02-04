package com.mannetroll.metrics.logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class JsonLayoutTest {
	private static final Logger LOG = LogManager.getLogger(JsonLayoutTest.class);

	//
	// -Dlog4j2.enable.threadlocals=false
	//
	
	@Test
	public void test() throws ReflectiveOperationException {
		JsonLayoutTest.updateEnv("log4j2.enable.threadlocals", "false");
		Map<String, Object> tmp = new HashMap<String, Object>();
		tmp.put("a_string", "hello");
		tmp.put("a_number", 11L);
		tmp.put("a_float", 1.1234F);
		tmp.put("a_boolean", true);
		LOG.info(tmp);
	}

	@SuppressWarnings({ "unchecked" })
	  public static void updateEnv(String name, String val) throws ReflectiveOperationException {
	    Map<String, String> env = System.getenv();
	    Field field = env.getClass().getDeclaredField("m");
	    field.setAccessible(true);
	    ((Map<String, String>) field.get(env)).put(name, val);
	  }	
	
}
