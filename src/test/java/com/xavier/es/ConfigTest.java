package com.xavier.es;

import com.alibaba.fastjson.JSON;
import com.xavier.config.SyncConfigBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfigTest extends RestEsApplicationTests {

	@Autowired
	private SyncConfigBean syncConfigBean;

	@Test
	public void configGetterTest() {
		System.out.println(JSON.toJSONString(syncConfigBean));
	}
}
