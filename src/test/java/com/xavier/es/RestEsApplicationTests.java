package com.xavier.es;

import com.alibaba.fastjson.JSONObject;
import com.xavier.EsApplication;
import com.xavier.config.BasicTableName;
import com.xavier.es.util.ElasticsearchSqlUtil;
import com.xavier.es.util.ElasticsearchUtil;
import com.xavier.es.util.EsPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = EsApplication.class)
public class RestEsApplicationTests {

	/**
	 * 创建索引
	 */
	@Test
	public void createIndexTest() throws IOException {
		ElasticsearchUtil.createIndex("test_index");
		ElasticsearchUtil.createIndex("test_indexsssss");
	}

	/**
	 * 删除索引
	 */
	@Test
	public void deleteIndexTest() throws IOException {
		ElasticsearchUtil.deleteIndex("test_indexsssss");
	}

	/**
	 * 判断索引是否存在
	 */
	@Test
	public void isIndexExistTest() throws IOException {
		ElasticsearchUtil.isIndexExist("test_index");
	}

	/**
	 * 数据添加
	 */
	@Test
	public void addDataTest() throws Exception {

		for (long i = 0; i < 100; i++) {
			Map<String, Object> map = new HashMap<>();

			map.put("name", "晓晓No." + i + "");
			map.put("age", i);
			map.put("interests", "阅读学习");
			map.put("about", "世界上没有优秀的理念，只有脚踏实地的结果");
			map.put("processTime", new Date());

			ElasticsearchUtil.addData(JSONObject.parseObject(JSONObject.toJSONString(map)), "test_index", "about_test", "id=" + i);
		}
	}

	/**
	 * 通过ID删除数据
	 */
	@Test
	public void deleteDataByIdTest() throws IOException {

		for (int i = 0; i < 100; i++) {
			ElasticsearchUtil.deleteDataById("test_index", "about_test", "id=" + i);
		}
	}


	/**
	 * 通过ID更新数据
	 * <p>
	 * jsonObject 要增加的数据
	 * index      索引，类似数据库
	 * type       类型，类似表
	 * id         数据ID
	 */
	@Test
	public void updateDataByIdTest() throws IOException {
		Map<String, Object> map = new HashMap<>();

		for (int i = 0; i < 100; i++) {
			map.put("name", "晓晓");
			map.put("age", 11);
			map.put("interests", "阅读学习");
			map.put("about", "这条数据被修改");
			map.put("processTime", new Date());

			ElasticsearchUtil.updateDataById(JSONObject.parseObject(JSONObject.toJSONString(map)), "test_index", "about_test", "id=" + i);
		}


	}

	/**
	 * 通过ID获取数据
	 * <p>
	 * index  索引，类似数据库
	 * type   类型，类似表
	 * id     数据ID
	 * fields 需要显示的字段，逗号分隔（缺省为全部字段）
	 */
	@Test
	public void searchDataByIdTest() throws IOException {
		Map<String, Object> map = ElasticsearchUtil.searchDataById(
				"pt_petition_case",
				"pt_petition_case",
				"1008611", null);
		System.out.println(JSONObject.toJSONString(map));
	}


	/**
	 * 使用分词查询
	 * <p>
	 * index          索引名称
	 * type           类型名称,可传入多个type逗号分隔
	 * startTime      开始时间
	 * endTime        结束时间
	 * size           文档大小限制
	 * fields         需要显示的字段，逗号分隔（缺省为全部字段）
	 * sortField      排序字段
	 * matchPhrase    true 使用，短语精准匹配
	 * highlightField 高亮字段
	 * matchStr       过滤条件（xxx=111,aaa=222）
	 */
	@Test
	public void searchListData() throws Exception {

		long startTime = DateUtils.parseDate("2017-11-22 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
		long endTime = DateUtils.parseDate("2017-11-23 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();

		String index = "all_assignservice-*";
		String type = "all_assignservice";
		String matchStr = "message=C000211171122024601";
		int size = 1000;

		List<Map<String, Object>> mapList = ElasticsearchUtil.searchListData(index, type, startTime, endTime, size, "", null, false, null, matchStr);

		Set<String> guidList = new HashSet<String>() {
		};

		Set<String> requestIdList = new HashSet<String>() {
		};

		for (Map<String, Object> guid : mapList) {

			String message = guid.get("message").toString();

			Integer guidIndex = message.indexOf("guid");

			if (message.indexOf("guid") > 1 && message.indexOf("action=>insertWorkOrder") > 1) {

				guidList.add(message.substring(guidIndex + 7, guidIndex + 26));
			}
		}

		for (String guid : guidList) {

			matchStr = "message=" + guid;

			List<Map<String, Object>> tmpMap2 = ElasticsearchUtil.searchListData(index, type, startTime, endTime, size, "", null, false, null, matchStr);

			for (Map<String, Object> requestId : tmpMap2) {

				String message = requestId.get("message").toString();

				if (message.indexOf("crm自动推荐入参:{\"bigClassId") > 1) {
					requestIdList.add(requestId.get("requestId").toString());
				}
			}
		}

		for (String requestId : requestIdList) {

			matchStr = "requestId=" + requestId;
			List<Map<String, Object>> tmpMap3 = ElasticsearchUtil.searchListData(index, type, startTime, endTime, size, "", null, false, null, matchStr);

			for (Map<String, Object> item : tmpMap3) {

				String message = item.get("message").toString();

				Integer startIndex = message.indexOf("crm自动推荐时间-->派工RPC调用-->返回结果为：{");
				Integer endIndex = message.lastIndexOf("}");

				if (startIndex > 1) {

					String resultData = message.substring(startIndex + 28, endIndex + 1);

					JSONObject jsonObject = JSONObject.parseObject(resultData);

					jsonObject = JSONObject.parseObject(jsonObject.get("data").toString());


					System.out.println(jsonObject.get("recommendDabi").toString() + " " + jsonObject.get("startTime").toString());
					System.out.println(jsonObject.get("recommendDabi").toString() + " " + jsonObject.get("endTime").toString());
				}

			}

		}

	}

	/**
	 * 使用分词查询,并分页
	 * <p>
	 * index          索引名称
	 * type           类型名称,可传入多个type逗号分隔
	 * currentPage    当前页
	 * pageSize       每页显示条数
	 * startTime      开始时间
	 * endTime        结束时间
	 * fields         需要显示的字段，逗号分隔（缺省为全部字段）
	 * sortField      排序字段
	 * matchPhrase    true 使用，短语精准匹配
	 * highlightField 高亮字段
	 * matchStr       过滤条件（xxx=111,aaa=222）
	 */
	@Test
	public void searchDataPage() throws Exception {
		EsPage esPage = ElasticsearchUtil.searchDataPage("pt_petition_person", "pt_petition_person", 1, 10, 0, 0,
				"", null, false,
				Arrays.asList("address_label", "name"), "address_label=吉林省,name=冯泽明");
		System.out.println(JSONObject.toJSONString(esPage.getRecordList()));
	}

	/**
	 * rest方式调用测试
	 */
	@Test
	public void restRequestTest() {
		RestTemplate restTemplate = new RestTemplate();

		String sql = "SELECT * FROM pt_petition_case WHERE petition_case_no.keyword LIKE 'LF%'";

		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		// String requestBody = "{\"sql\":\"" + sql + "\"}";
		Map<String,String> map = new HashMap();
		map.put("sql",sql);
		String requestBody = JSONObject.toJSONString(map);
		HttpEntity<String> request = new HttpEntity(requestBody, headers);
		Map apiResponse = restTemplate.postForObject("http://127.0.0.1:9200/_sql", request, Map.class);
		if (null != apiResponse.get("hits") && null != apiResponse.get("hits")) {
			System.out.println(apiResponse.get("hits"));
		}
	}

	/**
	 * 使用Stream分割ListMap测试
	 *
	 * @throws IOException
	 */
	@Test
	public void splitToListStreamTest() throws IOException {
		String highlightFields = "a1b,v,c,df";
		Arrays.stream(highlightFields.split(","))
				.filter(s -> StringUtils.isNotBlank(s))
				.map(s -> s.trim())
				.collect(Collectors.toList())
				.forEach(
						System.out::println
				);
	}

	/**
	 * 使用Stream分割ListMap测试
	 *
	 * @throws IOException
	 */
	@Test
	public void splitToListMapStreamTest() throws IOException {
		String sortField = "a::asc,b::desc,c::asc";

		List sortFieldList = Arrays.stream(sortField.split(","))
				.filter(s -> StringUtils.isNotBlank(s) && s.contains("::"))
				.map(s -> {
					Map fildInfoMap = new HashMap<String, String>();
					String[] fieldAndSort = s.split("::");
					fildInfoMap.put("field", fieldAndSort[0]);
					fildInfoMap.put("sort", fieldAndSort[1].toLowerCase());
					return fildInfoMap;
				})
				.collect(Collectors.toList());

		sortFieldList.forEach(System.out::println);
	}

	/**
	 * 异常测试
	 *
	 * @throws Exception
	 */
	@Test
	public void exceptionTest() throws Exception {
		Map map = new HashMap();
		for (int i = 0; i < 100; i++) {
			map.put(String.format("%10s", i), String.format("%10s", 100 - i));
		}
		ElasticsearchUtil.addData(JSONObject.parseObject(JSONObject.toJSONString(map)), "jlxf", "jlxf", "1008611");
	}

	/**
	 * 批量获取
	 */
	@Test
	public void multiGet() throws IOException {
		String index = BasicTableName.PT_PETITION_CASE;
		List idList = Arrays.asList();
		List resultList = ElasticsearchUtil.findByIdList(index,index,idList);
		System.out.println(resultList);
	}

	@Test
	public void perSqlTest(){
		String sql = "SELECT * FROM pt_petition_case";
		ElasticsearchSqlUtil.findPageBySql(sql,30,10).getRecordList().forEach(
				System.out::println
		);
	}
}
