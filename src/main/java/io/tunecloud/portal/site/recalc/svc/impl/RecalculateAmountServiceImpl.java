/**
 * 
 */
package io.tunecloud.portal.site.recalc.svc.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.tunecloud.portal.site.awsCostExplorer.svc.AwsCallCostExplorerListService;
import io.tunecloud.portal.site.awsCostExplorer.vo.AwsCostExplorerFilterVO;
import io.tunecloud.portal.site.awsCostExplorer.vo.AwsCostExplorerVO;
import io.tunecloud.portal.site.awsPriceList.svc.AwsPriceListService;
import io.tunecloud.portal.site.awsPriceList.vo.AwsPriceListFilterVO;
import io.tunecloud.portal.site.recalc.svc.RecalculateAmountService;

/**
 * <pre>
 * com.cloud.web.aws.recalc.svc.impl
 *   |_ RecalculateAmountServiceImpl.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:30:39
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
@Service("recalculateAmountService")
public class RecalculateAmountServiceImpl implements RecalculateAmountService {

	private final Logger LOGGER = LoggerFactory.getLogger(RecalculateAmountServiceImpl.class);
	
	
	/**
	 * Cost ExplorerList Service
	 */
	@Resource(name="awsCallCostExplorerListService")
	private AwsCallCostExplorerListService awsCostExplorerListService;
	
	/**
	 * priceList Service
	 */
	@Resource(name="awsPriceListService")
	private AwsPriceListService awsPriceListService;

	/**
	 * callExplorerList
	 */
	@Override
	public List<AwsCostExplorerVO> callExplorerList(AwsCostExplorerFilterVO costExplorerFilterVO) throws Exception {	
		// Group이 Null이면 > 'Usage Type으로 지정하기'
		LOGGER.debug("Group setting");		
		String group = costExplorerFilterVO.getAwsCostExplorerGroup();
		group = (group != null ? group : "USAGE_TYPE");
		costExplorerFilterVO.setAwsCostExplorerGroup(group);
		
		// Granularity가 Null이면 > "MONTHLY"로 지정하기
		LOGGER.debug("granularity setting");
		String granularity = costExplorerFilterVO.getAwsCostExplorerGranularity();
		granularity = (granularity != null ? granularity : "MONTHLY");
		costExplorerFilterVO.setAwsCostExplorerGranularity(granularity);
		
		// call cost Explorer API
		LOGGER.debug("call cost Explorer API");
		return awsCostExplorerListService.getCostExplorerList(costExplorerFilterVO);
	}

	/**
	 * callPriceList
	 */
	@Override
	public List<AwsCostExplorerVO> recalculate(AwsPriceListFilterVO priceListFilterVO) throws Exception {
		List<String> priceListServiceList = priceListFilterVO.getAwsPriceListServiceList();
		if (priceListServiceList.size() > 0) {
			// call cost Explorer API
			LOGGER.debug("call cost Explorer API");
			awsPriceListService.recalculate(priceListFilterVO);
		} else {
			LOGGER.error("priceListServiceList.size() > 0");
		}
		
		return null;
	}
	
}
