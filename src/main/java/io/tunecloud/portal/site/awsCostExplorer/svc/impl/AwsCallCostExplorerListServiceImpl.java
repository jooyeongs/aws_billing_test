/**
 * 
 */
package io.tunecloud.portal.site.awsCostExplorer.svc.impl;

import java.util.ArrayList;
import java.util.List;

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

import io.tunecloud.portal.site.awsCostExplorer.svc.AwsCallCostExplorerListService;
import io.tunecloud.portal.site.awsCostExplorer.vo.AwsCostExplorerFilterVO;
import io.tunecloud.portal.site.awsCostExplorer.vo.AwsCostExplorerVO;

/**
 * <pre>
 * com.cloud.api.aws.costexplorer.svc.impl
 *   |_ AwsCallCostExplorerListServiceImpl.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:28:22
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
@Service("awsCallCostExplorerListService")
public class AwsCallCostExplorerListServiceImpl implements AwsCallCostExplorerListService {
	private final Logger LOGGER = LoggerFactory.getLogger(AwsCallCostExplorerListServiceImpl.class);

    
	/**
	 * getCostExplorerList
	 */
	@Override
	public List<AwsCostExplorerVO> getCostExplorerList(AwsCostExplorerFilterVO costExplorerFilter) {
		// api 호출
		LOGGER.debug("call API");
		AWSCostExplorer costExplorer = callCostExplorer(costExplorerFilter);
		/*
		 * dateInterval
		 */
		LOGGER.debug("DateInterval");
		DateInterval dateInterval = new DateInterval().withStart(costExplorerFilter.getAwsCostExplorerStartDate())
													  .withEnd(costExplorerFilter.getAwsCostExplorerEndDate()	 );
        /*
         *  service Values
         *  Amazon Simple Storage Service
         *  AWS Certificate Manager
         */ 
		LOGGER.debug("serviceValues");
        List<String> serviceValues = new ArrayList<String>();
        serviceValues.addAll(costExplorerFilter.getAwsCostExplorerServiceValue());
        /*
         *  withGroupBy
         */
        LOGGER.debug("groupDefinition Type and key");
        GroupDefinition groupDefinition = new GroupDefinition().withType("DIMENSION")
        													   .withKey(costExplorerFilter.getAwsCostExplorerGroup());        
        List<GroupDefinition> groupDefinitions = new ArrayList<GroupDefinition>();
        groupDefinitions.add(groupDefinition);
        /*
         *  withFilter
         */
        LOGGER.debug("dimensionValues Type and key");
        DimensionValues dimensionValues = new DimensionValues();
        if (serviceValues.size() > 0) {
        	dimensionValues.withKey("SERVICE")
            			   .withValues(serviceValues);
        }
        Expression filter = new Expression().withDimensions(dimensionValues);
        /*
         *  withMetrics
         */
        List<String> metrics = new ArrayList<String>();
        metrics.add("UnblendedCost");
        metrics.add("UsageQuantity");
        /*
         *  getCostAndUsageRequest setting
         */
        LOGGER.debug("getCostAndUsageRequest setting");
        GetCostAndUsageRequest getCostAndUsageRequest = new GetCostAndUsageRequest().withTimePeriod(dateInterval)
																	        		.withGranularity(costExplorerFilter.getAwsCostExplorerGranularity())
																	        		.withFilter(filter)
																	        		.withGroupBy(groupDefinitions)
																	        		.withMetrics(metrics);
        /*
         * costExplorer.getCostAndUsage
         */
        LOGGER.debug("getCostAndUsageResult");
        GetCostAndUsageResult getCostAndUsageResult = costExplorer.getCostAndUsage(getCostAndUsageRequest);
        /*
         * getCostAndUsageResult
         */
        LOGGER.debug("resultByTimeList");
        List<ResultByTime> resultByTimeList = getCostAndUsageResult.getResultsByTime();
        /*
         * add awsCostExplorerList
         */
        LOGGER.debug("awsCostExplorerList");
        List<AwsCostExplorerVO> awsCostExplorerList = new ArrayList<AwsCostExplorerVO> ();
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
        		awsCostExplorerVO.setGroupKey(groupKey);
        		awsCostExplorerVO.setUsageQuantityCostAmount(usageQuantityAmount);
        		awsCostExplorerVO.setUsageQuantityCostUnit(usageQuantityUnit);
        		awsCostExplorerVO.setUnblendedCostAmount(unblendedCostAmount);
        		awsCostExplorerVO.setUnblendedCostUnit(unblendedCostUnit);
        		
        		awsCostExplorerList.add(awsCostExplorerVO);
        	}
        	
        }
        
		
		return awsCostExplorerList;
	}

	/**
	 * @Method Name  : callCostExplorer
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 8 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 8    :                   :    신규 개발.
	 * 
	 * @param costExplorerFilterVO
	 * @return
	 */
	private AWSCostExplorer callCostExplorer(AwsCostExplorerFilterVO costExplorerFilter) {
		LOGGER.debug("getCostExplorer");

		AWSCostExplorer costExplorer = null;
		try {
			LOGGER.debug("set AWSCostExplorer");
			costExplorer = AWSCostExplorerClientBuilder.standard()
													   .withRegion(Regions.US_EAST_1)
													   .withCredentials(costExplorerFilter.getAwsCredentialsProvider())
													   .build();
		} catch (Exception e) {
			LOGGER.error("AWSCostExplorer Exception {}", e.getMessage());
		}
		return costExplorer;
	}

}
