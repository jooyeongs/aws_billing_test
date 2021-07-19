/**
 * 
 */
package io.tunecloud.potal.site.recalc.vo;

import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSStaticCredentialsProvider;

import lombok.Data;

/**
 * <pre>
 * io.tunecloud.potal.site.recalc.vo
 *   |_ recalcFilterVO.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 6. 24 오후 4:54:25
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 6. 24       :                : 신규 개발.
 * 
 */
@Data
public class FilterVO {
	/**
	 * 시작일
	 */
	private String startDate;
	/**
	 * 종료일
	 */
	private String endDate;
	/**
	 * 기간 타입
	 */
	private String Granulaity;
	/**
	 * 그룹 기준
	 */
	private String groupBy;
	/**
	 * 서비스
	 */
	private List<String> service;
	/**
	 * 서비스
	 */
	private List<String> serviceValue;
	/**
	 * 계정
	 */
	private String account;
	/**
	 * 지역
	 */
	private String region;
	/**
	 * 인스턴스 유형
	 */
	private String instanceType;
	/**
	 * 사용 유형
	 */
	private String usageType;
	/**
	 * 사용 유형 그룹
	 */
	private String usageTypeGroup;
	/**
	 * 리소스
	 */
	private String resource;
	/**
	 * 비용 범주
	 */
	private String costCategory;
	/**
	 * 태그
	 */
	private String tag;
	/**
	 * apiOperation
	 */
	private String apiOperation;
	/**
	 * 충전 유형
	 */
	private String chargeType;
	/**
	 * 가용영역
	 */
	private String availabilityZone;
	/**
	 * platform
	 */
	private String platform;
	/**
	 * 구매옵션
	 */
	private String purchaseOption;
	/**
	 * 차용권
	 */
	private String tenancy;
	/**
	 * database Engine
	 */
	private String databaseEngine;
	/**
	 * 합법적 엔티티
	 */
	private String legalEntity;
	/**
	 * 청구 엔티티
	 */
	private String billingEntity;
	/**
	 * AWSStaticCredentialsProvider
	 */
	private AWSStaticCredentialsProvider awsCredentialsProvider; 
	/**
	 * 같은 서비스끼리 map으로 같이 다니게 만들기위한 변수
	 */
	private Map<String,String> serviceGroup;
}

