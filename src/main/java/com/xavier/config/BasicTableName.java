package com.xavier.config;

/**
 * 涉及到的表名
 *
 * @author NewGr8Player
 */
public class BasicTableName {

	/**
	 * 信访件表
	 */
	public static final String PT_PETITION_CASE = "pt_petition_case";

	/**
	 * 信访件状态
	 */
	public static final String PT_PETITION_STATUS = "pt_petition_status";

	/**
	 * 信访件概况大文本
	 */
	public static final String PT_PETITION_CONTENT = "pt_petition_content";

	/**
	 * 信访人
	 */
	public static final String PT_PETITION_PERSON = "pt_petition_person";

	/**
	 * 办理方式
	 */
	public static final String PT_TRANSACT_WAY = "pt_transact_way";

	/**
	 * 答复意见书
	 */
	public static final String PT_REPLY_NOTICE = "pt_reply_notice";

	/**
	 * 满意度评价
	 */
	public static final String PT_SATISFACTION = "pt_satisfaction";

	/**
	 * 基础字段
	 */
	public static class Field {
		/**
		 * 创建时间
		 */
		public static final String CREATE_DATE = "create_date";
		/**
		 * 创建人
		 */
		public static final String CREATE_BY = "create_by";

		/**
		 * 更新时间
		 */
		public static final String UPDATE_DATE = "update_date";

		/**
		 * 更新人
		 */
		public static final String UPDATE_BY = "update_by";
	}
}