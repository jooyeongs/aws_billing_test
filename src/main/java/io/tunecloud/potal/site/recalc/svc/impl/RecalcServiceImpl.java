/**
 * 
 */
package io.tunecloud.potal.site.recalc.svc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.tunecloud.potal.site.awsapi.costexplorer.svc.AwsCostExplorerService;
import io.tunecloud.potal.site.awsapi.costexplorer.vo.AwsCostExplorerVO;
import io.tunecloud.potal.site.awsapi.priceList.svc.AwsPriceListService;
import io.tunecloud.potal.site.awsapi.priceList.vo.AwsPriceListVO;
import io.tunecloud.potal.site.recalc.svc.RecalcService;
import io.tunecloud.potal.site.recalc.vo.FilterVO;

/**
 * <pre>
 * io.tunecloud.potal.site.recalc.svc.impl
 *   |_ RecalcServiceImpl.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 6. 24 오후 2:02:08
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 6. 24        :                : 신규 개발.
 * 
 */
/**
 * <pre>
 * io.tunecloud.potal.site.recalc.svc.impl
 *   |_ RecalcServiceImpl.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 19 오후 5:22:05
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 19       :                : 신규 개발.
 * 
 */
@Service("recalcService")
public class RecalcServiceImpl implements RecalcService {
	private final Logger LOGGER = LoggerFactory.getLogger(RecalcServiceImpl.class);
	
	@Resource(name="awsCostExplorerService")
	AwsCostExplorerService costExplorerService;
	
	@Resource(name="awsPriceListService")
	AwsPriceListService priceListService;

	/**
	 * 조회된 PriceList를 DB에 등록한다.
	 */
	@Override
	public Map<String, Object> savePriceList(FilterVO filterVO) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		/**
		 * CostExplorer 리스트 추출
		 */
		LOGGER.debug("costExplorerList 리스트 추출 ");
		List<AwsCostExplorerVO> costExplorerList = costExplorerService.callCostExplorerList(filterVO);
		
		// costExplorerList가 1건 이상시
		if (costExplorerList != null && costExplorerList.size() > 0) {
			/** 
			 * PriceList 리스트 추출
			 */
			/**
			 * DB에 적재
			 */
			/**
			 * 결과값 map에 put
			 */
			return result;
		} else {
			// error msg
			return result;
		}
	}

	/**
	 * PriceList와 CostExplorer값을 비교하여 재산정값을 도출한다.
	 */
	@Override
	public Map<String, Object> priceListRealignment(FilterVO filterVO) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();		
		/**
		 * CostExplorer 리스트 추출 ver1.CostExplorer API 접근 
		 */
		LOGGER.debug("costExplorerList 리스트 추출 ");
		List<AwsCostExplorerVO> costExplorerList = costExplorerService.callCostExplorerList(filterVO);
		// costExplorerList가 1건 이상시
		if (costExplorerList != null && costExplorerList.size() > 0) {
			/** 
			 * PriceList 리스트 추출
			 */
			LOGGER.debug("recalc {}","price List start");
			AwsPriceListVO priceList = priceListService.callPriceListList(filterVO,costExplorerList);
			/**
			 * 재산정
			 */
			/**
			 * 결과값 map에 put
			 */
			return result;
		} else {
			// error msg
			return result;
		}
		// CostExplorer 리스트 추출 ver2.DB 접근 >> 예정		
	}
}
