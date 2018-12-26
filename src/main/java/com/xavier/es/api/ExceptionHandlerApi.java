package com.xavier.es.api;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一异常处理接口
 *
 * @author NewGr8Player
 */
@RestControllerAdvice
public class ExceptionHandlerApi {

	/**
	 * 统一处理异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public Map errorHandler(Exception ex) {
		Map map = new HashMap();
		map.put("code", 500);
		map.put("msg", ex.getMessage());
		return map;
	}
}
