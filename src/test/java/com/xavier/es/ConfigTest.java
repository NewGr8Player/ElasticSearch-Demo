package com.xavier.es;

import com.alibaba.fastjson.JSON;
import com.xavier.config.SyncConfigBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class ConfigTest extends RestEsApplicationTests {

	@Autowired
	private SyncConfigBean syncConfigBean;

	@Test
	public void configGetterTest() {
		System.out.println(JSON.toJSONString(syncConfigBean));
	}

	@Test
	public void optionalTest() {
		String fields = null;
		fields = Optional.ofNullable(fields).orElse("");
		System.out.println(fields);
	}
}
