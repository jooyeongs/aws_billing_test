/**
 * 
 */
package io.tunecloud.potal.site.awsapi.costexplorer.vo;

import java.util.ArrayList;
import java.util.Map;

import lombok.Data;

/**
 * <pre>
 * io.tunecloud.potal.site.awsapi.costexplorer.vo
 *   |_ AwsCostExplorerVO.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 6. 24 오후 5:16:04
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 6. 24       :                : 신규 개발.
 * 
 */
// @Data
public class AwsCostExplorerVO {
	/**
	 * TimePeriod : 시작일
	 */
	private String startDate;
	/**
	 * TimePeriod : 종료일
	 */
	private String endDate;
	/**
	 * Granularity(기간 타입) : MONTHLY
	 */
	private String Granulaity;
	/**
	 * Filter:Dimensions Key
	 */
	private String dimensionKey;
	/**
	 * Filter:Dimensions Values
	 */
	private ArrayList<String> dimensionValues;
	/**
	 * Metrics
	 */
	private ArrayList<String> metrics;
	/**
	 * GroupBy : Type
	 */
	private String groupByType;
	/**
	 * GroupBy : Key
	 */
	private String groupByKey;
	/**
	 * groupDefinition key
	 */
	private String groupDefinitionKey;
	/**
	 * groupDefinition type
	 */
	private String groupDefinitionType;
	/**
	 * Group Definitions
	 */
	private String GroupDefinitions;
	/**
	 * Service  
	 */
	private String service;
	/**
	 * Service value 
	 */
	private String serviceValue;
}
