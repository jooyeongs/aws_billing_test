/**
 * 
 */
package io.tunecloud.portal.site.awsCostExplorer.vo;

import java.math.BigDecimal;

import lombok.Data;

/**
 * <pre>
 * com.cloud.api.aws.costexplorer.vo
 *   |_ AwsCostExplorerVO.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:29:01
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
@Data
public class AwsCostExplorerVO {
	/**
	 * group key : usage type등 그룹 키워드
	 */
	private String groupKey;			
	/**
	 * start Date : 측정 시작 일 
	 */
	private String startDate;
	/**
	 * end Date : 측정 종료 일 
	 */
	private String endDate;
	/**
	 * usageQuantity Amount : 사용량
	 */
	private String usageQuantityCostAmount;		
	/**
	 * usageQuantity Unit : 사용량 단위
	 */
	private String usageQuantityCostUnit;			
	/**
	 * unblendedCost Amount : 사용 요금
	 */
	private String unblendedCostAmount;		
	/**
	 * unblendedCost Unit : 화폐 단위
	 */
	private String unblendedCostUnit;	
	/**
	 * Recalc Amount : 재산정 요금
	 */
	private BigDecimal recalcAmount;	
	/**
	 * 추산 여부
	 */
	private Boolean isEstimated;
	/**
	 * Service  
	 */
	private String service;
	/**
	 * Service value 
	 */
	private String serviceValue;
}
