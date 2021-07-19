/**
 * 
 */
package io.tunecloud.potal.site.awsapi.costexplorer.svc.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.costexplorer.AWSCostExplorer;
import com.amazonaws.services.costexplorer.AWSCostExplorerClientBuilder;
import com.amazonaws.services.costexplorer.model.DateInterval;
import com.amazonaws.services.costexplorer.model.DimensionValues;
import com.amazonaws.services.costexplorer.model.Expression;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageRequest;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageResult;
import com.amazonaws.services.costexplorer.model.Group;
import com.amazonaws.services.costexplorer.model.GroupDefinition;
import com.amazonaws.services.costexplorer.model.ResultByTime;

import io.tunecloud.potal.site.awsapi.costexplorer.svc.AwsCostExplorerService;
import io.tunecloud.potal.site.awsapi.costexplorer.vo.AwsCostExplorerVO;
import io.tunecloud.potal.site.awsapi.credentials.svc.AwsCredentialService;
import io.tunecloud.potal.site.recalc.vo.FilterVO;

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
	
	@Resource(name="awsCredentialService")
	AwsCredentialService awsCredentialService;
	
	@Override
	public List<AwsCostExplorerVO> callCostExplorerServiceList(FilterVO filterVO) throws Exception {
		/**
		 * api 호출
		 */
		LOGGER.debug("call CostExplorer API");
		AWSCostExplorer costExplorer = callCostExplorer(filterVO);
		/**
		 * dateInterval
		 */
		LOGGER.debug("DateInterval");
		DateInterval dateInterval = new DateInterval().withStart(filterVO.getStartDate())
													  .withEnd(filterVO.getEndDate()	);
        /**
         *  withGroupBy
         */
        LOGGER.debug("groupDefinition Type and key");
        GroupDefinition groupDefinition = new GroupDefinition().withType("DIMENSION")
        													   .withKey(filterVO.getGroupBy());        
        List<GroupDefinition> groupDefinitions = new ArrayList<GroupDefinition>();
        groupDefinitions.add(groupDefinition);
        /**
         *  withMetrics
         */
        List<String> metrics = new ArrayList<String>();
        metrics.add("UnblendedCost");
        metrics.add("UsageQuantity");
        /**
         * service Values
         * ex) Amazon Simple Storage Service ,  AWS Certificate Manager
         */ 
		LOGGER.debug("serviceValues");
		List<AwsCostExplorerVO> awsCostExplorerList = new ArrayList<AwsCostExplorerVO> ();
		
		List<String> serviceValues = new ArrayList<String>();
		serviceValues.addAll(filterVO.getServiceValue());
		for (String serviceValue : serviceValues) {
			if (!"".equals(serviceValue)) {
		        /**
		         *  withFilter
		         */
		        LOGGER.debug("dimensionValues Type and key");
		        DimensionValues dimensionValues = new DimensionValues();
		        if (serviceValues.size() > 0) {
		        	dimensionValues.withKey("SERVICE")
		            			   .withValues(serviceValues);
		        }
		        Expression filter = new Expression().withDimensions(dimensionValues);
		        /**
		         *  getCostAndUsageRequest setting
		         */
		        LOGGER.debug("getCostAndUsageRequest setting");
		        GetCostAndUsageRequest getCostAndUsageRequest = new GetCostAndUsageRequest().withTimePeriod(dateInterval)
																			        		.withGranularity(filterVO.getGranulaity())
																			        		.withFilter(filter)
																			        		.withGroupBy(groupDefinitions)
																			        		.withMetrics(metrics);
		        /**
		         * costExplorer.getCostAndUsage
		         */
		        LOGGER.debug("getCostAndUsageResult");
		        GetCostAndUsageResult getCostAndUsageResult = costExplorer.getCostAndUsage(getCostAndUsageRequest);
		        /**
		         * getCostAndUsageResult
		         */
		        LOGGER.debug("resultByTimeList");
		        List<ResultByTime> resultByTimeList = getCostAndUsageResult.getResultsByTime();
		        /**
		         * add awsCostExplorerList
		         */
		        LOGGER.debug("awsCostExplorerList");
		        
		        for (ResultByTime rbt : resultByTimeList) {
		        	String startDate 	= rbt.getTimePeriod().getStart();
		        	String endDate 		= rbt.getTimePeriod().getEnd();
		        	boolean isEstimated = rbt.getEstimated() != null ? rbt.getEstimated() : false;
		        	
		        	for (Group group : rbt.getGroups()) {
		        		/*
		        		 * ex {  Keys: [APN1-Requests-Tier2]
		        		 * 		,Metrics: {
		        		 * 			  UsageQuantity={Amount: 61			, Unit: Requests}
		        		 * 			, UnblendedCost={Amount: 0.00002257	, Unit: USD}
		        		 * 		 }
		        		 * 	  }, ....
		        		 */
		        		String groupKey = "";
		        		for (String key : group.getKeys()) {
		        			if (group.getKeys().size() > 1) {
		        				groupKey += key+",";
		        			} else {
		        				groupKey = key;
		        			}
		        		}
		        		String usageQuantityAmount	= group.getMetrics().get("UsageQuantity").getAmount();
		        		String usageQuantityUnit	= group.getMetrics().get("UsageQuantity").getUnit();
		        		String unblendedCostAmount	= group.getMetrics().get("UnblendedCost").getAmount();
		        		String unblendedCostUnit	= group.getMetrics().get("UnblendedCost").getUnit();
		        		
		        		AwsCostExplorerVO awsCostExplorerVO = new AwsCostExplorerVO();
		        		awsCostExplorerVO.setStartDate(startDate);
		        		awsCostExplorerVO.setEndDate(endDate);
		        		awsCostExplorerVO.setIsEstimated(isEstimated);
		        		awsCostExplorerVO.setGroupByKey(groupKey);
		        		awsCostExplorerVO.setUsageQuantityAmount(usageQuantityAmount);
		        		awsCostExplorerVO.setUsageQuantityUnit(usageQuantityUnit);
		        		awsCostExplorerVO.setUnblendedCostAmount(unblendedCostAmount);
		        		awsCostExplorerVO.setUnblendedCostUnit(unblendedCostUnit);
		        		
		        		awsCostExplorerList.add(awsCostExplorerVO);
		        	}
		        	
		        }
				
			} else if ("".equals(serviceValue)) {
				
			}
		}
        

		return awsCostExplorerList;
	}
	
	private AWSCostExplorer callCostExplorer(FilterVO filter) {
		LOGGER.debug("getCostExplorer");

		AWSCostExplorer costExplorer = null;
		try {
			LOGGER.debug("set AWSCostExplorer");
			costExplorer = AWSCostExplorerClientBuilder.standard()
													   .withRegion(Regions.US_EAST_1)
													   .withCredentials(filter.getAwsCredentialsProvider())
													   .build();
		} catch (Exception e) {
			LOGGER.error("AWSCostExplorer Exception {}", e.getMessage());
		}
		return costExplorer;
	}

}
