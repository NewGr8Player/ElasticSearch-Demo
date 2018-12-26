package com.xavier.es.util;

import com.xavier.config.SyncConfigBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class FieldMappingUtil {

	@Autowired
	private SyncConfigBean syncConfigBean;

	private static SyncConfigBean config;

	@PostConstruct
	public void init(){
		config = syncConfigBean;
	}
}
