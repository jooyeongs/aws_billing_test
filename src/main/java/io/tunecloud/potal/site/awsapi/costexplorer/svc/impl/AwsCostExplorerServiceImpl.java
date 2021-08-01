/**
 * 
 */
package io.tunecloud.potal.site.awsapi.costexplorer.svc.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.costexplorer.AWSCostExplorer;
import com.amazonaws.services.costexplorer.AWSCostExplorerClientBuilder;
import com.amazonaws.services.costexplorer.model.DateInterval;
import com.amazonaws.services.costexplorer.model.DimensionValues;
import com.amazonaws.services.costexplorer.model.DimensionValuesWithAttributes;
import com.amazonaws.services.costexplorer.model.Expression;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageRequest;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageResult;
import com.amazonaws.services.costexplorer.model.GetDimensionValuesRequest;
import com.amazonaws.services.costexplorer.model.GetDimensionValuesResult;
import com.amazonaws.services.costexplorer.model.Group;
import com.amazonaws.services.costexplorer.model.GroupDefinition;
import com.amazonaws.services.costexplorer.model.ResultByTime;

import io.tunecloud.potal.site.awsapi.costexplorer.svc.AwsCostExplorerService;
import io.tunecloud.potal.site.awsapi.costexplorer.vo.AwsCostExplorerVO;
import io.tunecloud.potal.site.awsapi.util.AwsUtils;
import io.tunecloud.potal.site.rinsp.vo.FilterVO;

/**
 * <pre>
 * io.tunecloud.potal.site.awsapi.costexplorer.svc.impl
 *   |_ AwsCostExplorerServiceImpl.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 19 오후 5:12:17
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 19       :                : 신규 개발.
 * 
 */
@Service("awsCostExplorerService")
public class AwsCostExplorerServiceImpl implements AwsCostExplorerService {
	private final Logger LOGGER = LoggerFactory.getLogger(AwsCostExplorerServiceImpl.class);

	@Value("${encrypt.aeskey}")
    private String aes256Key;
	
	@Value("${aws.endpoint.regionName}")
    private String regionName;
	
	@Override
	public AwsCostExplorerVO callCostExplorerList(FilterVO filterVO) throws Exception {
		LOGGER.debug("callCostExplorerServiceList");
		/**
		 * <call utils>
		 *	US_EAST_1("us-east-1", "US East (N. Virginia)")
		 *	(Regions regions, String accessKey, String secretKey, String aes256Key)
		 */
		Regions 		endPoint 	 = Regions.fromName(regionName);
		AWSCostExplorer costExplorer = AwsUtils.authAwsCe(endPoint, filterVO.getAccessKey(), filterVO.getSecretKey(), aes256Key);
		
		DateInterval 	dateInterval    = new DateInterval().withStart(filterVO.getStartDate()).withEnd(filterVO.getEndDate()); //검색기간 설정
		DimensionValues dimensionValues = null;
		
		if("AwsDataTransfer".equals(filterVO.getServiceName()) || "Amazon Elastic Compute Cloud".equals(filterVO.getServiceName()) ){
			List<String> usageTypes    = new ArrayList<String>();
			 // 일정 기간 동안 지정된 필터에 대해 사용 가능한 모든 필터 값을 검색하기 위한 GetDimensionValuesRequest 객체를 생성한다.
		    GetDimensionValuesRequest getDimensionValuesRequest = new GetDimensionValuesRequest().withContext("COST_AND_USAGE") // 호출 컨텍스트 기본 값은 COST_AND_USAGE, GetCostAndUsage 작업에 사용 가능
																					             .withDimension("USAGE_TYPE")
																					             .withTimePeriod(dateInterval);  // 기간 설정
		    GetDimensionValuesResult getDimensionValuesResult = costExplorer.getDimensionValues(getDimensionValuesRequest); // 일정 기간 동안 지정된 필터에 대해 사용 가능한 모든 필터 값을 담을 GetDimensionValuesResult 객체를 생성한다.
		    
		    for(DimensionValuesWithAttributes dimensionValuesWithAttributes : getDimensionValuesResult.getDimensionValues()) {   // 요청을 필터링하는 데 사용한 필터의 목록들을 순회합니다.
		    	String value = dimensionValuesWithAttributes.getValue();             		 // 요청을 필터링하는 데 사용한 필터의 값을 가져온다.
		    	if(filterVO.getServiceName().equals("AwsDataTransfer")) {
		    		if(value.contains("AWS-Out-Bytes") || value.contains("DataTransfer")) { // 필터의 값이 Out 또는 DataTransfer를 포함하는지 확인한다.
		    			usageTypes.add(value);                                     			 // usageTypes의 리스트에 필터의 값을 넣어준다.
		    		}
		    	} else if(filterVO.getServiceName().equals("Amazon Elastic Compute Cloud")) {
		    		if(value.contains("EBS")) { 											 // 필터의 값이 Out 또는 DataTransfer를 포함하는지 확인한다.
		    			usageTypes.add(value);                                     		 // usageTypes의 리스트에 필터의 값을 넣어준다.
		    		}
		    	}
		    }
		      
		    usageTypes.remove("AP-DataTransfer-Out-Bytes");                   // usageTypes리스트에서 필터의 값이  AP-DataTransfer-Out-Bytes인 객체를 삭제한다.
		      
			dimensionValues = new DimensionValues().withKey	("USAGE_TYPE")						//키값 설정
					   							  .withValues(usageTypes);	
		}else {
			dimensionValues = new DimensionValues().withKey	("SERVICE")							//키값 설정
												   .withValues(filterVO.getServiceName());		//서비스네임
		}
		
		Expression filter = new Expression().withDimensions(dimensionValues);
		
		List<GroupDefinition> groupDefinitions = new ArrayList<GroupDefinition>();
		GroupDefinition 		groupDefinition  = new GroupDefinition().withType("DIMENSION")					//GroupBy 필터설정
																		.withKey("USAGE_TYPE");					//사용유형별
		groupDefinitions.add(groupDefinition);
		
		List<String> metrics = new ArrayList<String>();
	    metrics.add("UnblendedCost");	//사용자 지불 비용
	    metrics.add("UsageQuantity");	//사용자 이용량
	    
	    GetCostAndUsageRequest getCostAndUsageRequest = new GetCostAndUsageRequest()							//Explorer 정보 요청
	              .withTimePeriod	(dateInterval)		//기간 설정
	              .withGranularity	("MONTHLY")			//월별
	              .withFilter		(filter)			//필터 설정: 서비스
	              .withGroupBy		(groupDefinitions)	//GroupBy 설정: 사용유형별
	              .withMetrics		(metrics);			//표출내용: 사용자 지불 비용, 사용자 이용량
	    GetCostAndUsageResult getCostAndUsageResult = costExplorer.getCostAndUsage(getCostAndUsageRequest);		//결과 리턴 객체 담기
	    List<ResultByTime> resultByTimes = getCostAndUsageResult.getResultsByTime();
	    
	    //VO에 저장
	    
	    System.out.println(resultByTimes.toString());
	    
	    for(ResultByTime resultByTime : resultByTimes) {
	    	 System.out.println(resultByTime.getTimePeriod());
	    	 for(Group group : resultByTime.getGroups()) {
	    		 System.out.println(group);
	    	 }
	    	 System.out.println();
	    }
	    AwsCostExplorerVO ceVO = new AwsCostExplorerVO();
	    
	    ceVO.setResultByTimes(resultByTimes);	

		return ceVO;
	}
	
	/**
	 * @Method Name  : parsingResultByTimeList
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 20 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 20    :                   :    신규 개발.
	 * 
	 * @param resultByTimeList
	 * @return
	 */
	private List<AwsCostExplorerVO> parsingResultByTimeList(String serviceValue, List<ResultByTime> resultByTimeList) {
		List<AwsCostExplorerVO> awsCostExplorerList = new ArrayList<AwsCostExplorerVO> ();
		/**
		 * ResultByTime을 풀어서 AwsCostExplorerVO에 넣기
		 */
		LOGGER.debug("parsingResultByTimeList");
		
		for (ResultByTime rbt : resultByTimeList) {
			String startDate 	= rbt.getTimePeriod().getStart();
			String endDate 		= rbt.getTimePeriod().getEnd();
			boolean isEstimated = rbt.getEstimated() != null ? rbt.getEstimated() : false;
			
			for (Group group : rbt.getGroups()) {
				String usageQuantityAmount	= group.getMetrics().get("UsageQuantity").getAmount();
				String usageQuantityUnit	= group.getMetrics().get("UsageQuantity").getUnit();
				String unblendedCostAmount	= group.getMetrics().get("UnblendedCost").getAmount();
				String unblendedCostUnit	= group.getMetrics().get("UnblendedCost").getUnit();
				
				AwsCostExplorerVO awsCostExplorerVO = new AwsCostExplorerVO();
				awsCostExplorerVO.setServiceValue		(serviceValue		);
				awsCostExplorerVO.setStartDate			(startDate			);
				awsCostExplorerVO.setEndDate			(endDate			);
				awsCostExplorerVO.setIsEstimated		(isEstimated		);
				awsCostExplorerVO.setUsageType			(group.getKeys()	);
				awsCostExplorerVO.setUsageQuantityAmount(usageQuantityAmount);
				awsCostExplorerVO.setUsageQuantityUnit	(usageQuantityUnit	);
				awsCostExplorerVO.setUnblendedCostAmount(unblendedCostAmount);
				awsCostExplorerVO.setUnblendedCostUnit	(unblendedCostUnit	);
				awsCostExplorerVO.setResultByTimeList	(resultByTimeList);
				awsCostExplorerList.add(awsCostExplorerVO);
			}
		}
		return awsCostExplorerList;
	}

	/**
	 * @Method Name  : setGroupDefinitions
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 20 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 20    :                   :    신규 개발.
	 * 
	 * @param filterVO
	 * @return
	 */
	private List<GroupDefinition> setGroupDefinitions(FilterVO filterVO) {
		List<GroupDefinition> groupDefinitions = new ArrayList<GroupDefinition>();
		/**
		 *  withGroupBy
		 */
		LOGGER.debug("groupDefinition Type and key");
		GroupDefinition groupDefinition = new GroupDefinition().withType("DIMENSION"		  )	//GroupBy 필터설정
															   .withKey	(filterVO.getGroupBy());//사용유형별
		groupDefinitions.add(groupDefinition);
		return groupDefinitions;
	}

	/**
	 * @Method Name  : createDimensionValues
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 20 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 20    :                   :    신규 개발.
	 * 
	 * @return
	 */
	private Expression createDimensionValues(AWSCostExplorer costExplorer, String serviceValue, DateInterval dateInterval) {
		LOGGER.debug("createDimensionValues");
		/**
		 * dimensionValues를 설정한다
		 */
		DimensionValues dimensionValues = new DimensionValues();
		if("AwsDataTransfer".equals(serviceValue) || "AmazonEBS".equals(serviceValue)){
			LOGGER.debug("AwsDataTransfer or AmazonEBS");
			/*
			 * 일정 기간 동안 지정된 필터에 대해 사용 가능한 모든 필터 값을 넣기 위한 String형 리스트를 생성한다.
			 */
			List<String> usageTypes = new ArrayList<String>(); 
			/*
			 * 일정 기간 동안 지정된 필터에 대해 사용 가능한 모든 필터 값을 검색하기 위한 GetDimensionValuesRequest 객체를 생성한다.
			 */
			LOGGER.debug("getDimensionValuesRequest");
			GetDimensionValuesRequest getDimensionValuesRequest = new GetDimensionValuesRequest().withContext("COST_AND_USAGE")  // 호출 컨텍스트 기본 값은 COST_AND_USAGE, GetCostAndUsage 작업에 사용 가능
																								 .withDimension("USAGE_TYPE")
																								 .withTimePeriod(dateInterval);  // 기간 설정
			/*
			 * 일정 기간 동안 지정된 필터에 대해 사용 가능한 모든 필터 값을 담을 GetDimensionValuesResult 객체를 생성한다.
			 */
			LOGGER.debug("getDimensionValuesResult");
			GetDimensionValuesResult getDimensionValuesResult = costExplorer.getDimensionValues(getDimensionValuesRequest);
			/*
			 * 요청을 필터링하는 데 사용한 필터의 목록들을 순회합니다.
			 */
			for (DimensionValuesWithAttributes dimensionValuesWithAttributes : getDimensionValuesResult.getDimensionValues()) {
				LOGGER.debug("dimensionValuesWithAttributes.getValue");
				String value = dimensionValuesWithAttributes.getValue();					// 요청을 필터링하는 데 사용한 필터의 값을 가져온다.
				if("AwsDataTransfer".equals(serviceValue)) {
					if(value.contains("AWS-Out-Bytes") || value.contains("DataTransfer")) {	// 필터의 값이 Out 또는 DataTransfer를 포함하는지 확인한다.
						usageTypes.add(value);                                     			// usageTypes의 리스트에 필터의 값을 넣어준다.
					}
				} else if("AmazonEBS".equals(serviceValue)) {
					if(value.contains("EBS")) { 											// 필터의 값이 Out 또는 DataTransfer를 포함하는지 확인한다.
						usageTypes.add(value);                                     		 	// usageTypes의 리스트에 필터의 값을 넣어준다.
					}
				}
			}
			usageTypes.remove("AP-DataTransfer-Out-Bytes");                   				// usageTypes리스트에서 필터의 값이  AP-DataTransfer-Out-Bytes인 객체를 삭제한다.
			
			LOGGER.debug("dimensionValues setting");
			dimensionValues = new DimensionValues().withKey	  ("USAGE_TYPE"	)				//키값 설정
												   .withValues(usageTypes	);
		}else {
			dimensionValues = new DimensionValues().withKey	  ("SERVICE"	)				//키값 설정
												   .withValues(serviceValue );				//서비스네임
		}
		/**
		 * filter setting
		 */
		LOGGER.debug("filter setting");
		Expression filter = new Expression().withDimensions(dimensionValues);
		
		return filter;
	}

	private AWSCostExplorer callCostExplorer(FilterVO filter) {
		LOGGER.debug("getCostExplorer");

		AWSCostExplorer costExplorer = null;
		try {
			LOGGER.debug("AWSCostExplorer builder");
			costExplorer = AWSCostExplorerClientBuilder.standard	   (								  )
													   .withRegion	   (Regions.US_EAST_1				  )
													   .withCredentials(filter.getAwsCredentialsProvider())
													   .build		   (								  );
		} catch (Exception e) {
			LOGGER.error("AWSCostExplorer Exception {}", e.getMessage());
		}
		return costExplorer;
	}


}
