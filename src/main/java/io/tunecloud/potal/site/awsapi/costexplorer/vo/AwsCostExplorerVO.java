/**
 * 
 */
package io.tunecloud.potal.site.awsapi.costexplorer.vo;

import java.util.List;

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
@Data
public class AwsCostExplorerVO{
	/**
	 * 추산 여부
	 */
	private Boolean isEstimated;
	/**
	 * start Date : 측정 시작 일 
	 */
	private String startDate;
	/**
	 * end Date : 측정 종료 일 
	 */
	private String endDate;
	/**
	 * 사용량 유형 
	 */
	private List<String> usageType;
	/**
	 * unblended Cost Amount
	 */
	private String unblendedCostAmount;
	/**
	 * unblended Cost Unit
	 */
	private String unblendedCostUnit;
	/**
	 * Usage Quantity Amount
	 */
	private String usageQuantityAmount;
	/**
	 * Usage Quantity Unit
	 */
	private String usageQuantityUnit;
	/**
	 * Service  
	 */
	private String service;
	/**
	 * Service value 
	 */
	private String serviceValue;
}
