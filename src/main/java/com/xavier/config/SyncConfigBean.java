package com.xavier.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据同步配置项
 *
 * @author NewGr8Player
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConfigurationProperties(prefix = "sync.config")
public class SyncConfigBean {

	/**
	 * 启用映射
	 */
	private boolean enable = true;

	/**
	 * 映射关系
	 */
	private List<TableMappting> mapping = new ArrayList<>();

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TableMappting {

		/**
		 * 是否启用改配置
		 */
		private boolean enabled = true;

		/**
		 * 表名
		 */
		private String tableName = "";

		/**
		 * 映射关系
		 */
		private List<MappingRelation> fields = new ArrayList<>();
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MappingRelation {

		/**
		 * 字段名
		 */
		private String fieldName = "";
		/**
		 * 映射字段名
		 */
		private String mappingName = "";

		/**
		 * 是否显示
		 */
		private boolean show = true;

		/**
		 * 是否启用
		 */
		private boolean enable = true;
	}
}
