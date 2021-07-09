/**
 * 
 */
package io.tunecloud.portal.site.awsPriceList.svc.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.pricing.AWSPricing;
import com.amazonaws.services.pricing.AWSPricingClientBuilder;
import com.amazonaws.services.pricing.model.ExpiredNextTokenException;
import com.amazonaws.services.pricing.model.Filter;
import com.amazonaws.services.pricing.model.GetProductsRequest;
import com.amazonaws.services.pricing.model.GetProductsResult;
import com.amazonaws.services.pricing.model.InternalErrorException;
import com.amazonaws.services.pricing.model.InvalidNextTokenException;
import com.amazonaws.services.pricing.model.InvalidParameterException;
import com.amazonaws.services.pricing.model.NotFoundException;
import com.amazonaws.util.StringUtils;

import io.tunecloud.portal.site.awsCostExplorer.svc.impl.AwsCallCostExplorerListServiceImpl;
import io.tunecloud.portal.site.awsCostExplorer.vo.AwsCostExplorerVO;
import io.tunecloud.portal.site.awsPriceList.svc.AwsPriceListService;
import io.tunecloud.portal.site.awsPriceList.vo.AwsPriceListFilterVO;
import io.tunecloud.portal.site.awsPriceList.vo.AwsPriceListVO;

/**
 * <pre>
 * com.cloud.api.aws.pricelist.svc.impl
 *   |_ AwsPriceListServiceImpl.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:29:34
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
@Service("awsPriceListService")
public class AwsPriceListServiceImpl implements AwsPriceListService {
	private final Logger LOGGER = LoggerFactory.getLogger(AwsCallCostExplorerListServiceImpl.class);
	

	/**
	 * getPriceList
	 */
	@Override
	public List<AwsPriceListVO> recalculate(AwsPriceListFilterVO priceListFilter) {
		// api 호출
		LOGGER.debug("call API");
		AWSPricing pricing = callPricing(priceListFilter);
		/*
		 * CostExplorerList에서 건 바이 건으로 usagetype별로 priceList를 불러오는 부분 
		 */
		for (AwsCostExplorerVO awsCostExplorer : priceListFilter.getAwsCostExplorerVOList()) {
			// 검색필터에 서비스가 1건 이상 넘어온 경우 서비스별로  제품을 조회 하기위해 priceList를 분리
			for (String priceListService : priceListFilter.getAwsPriceListServiceList()) {
				/*
				 * Products Request setting
				 */
				List<Filter> 		filters 		= new ArrayList<Filter>();
				GetProductsRequest 	productsReq 	= new GetProductsRequest().withFilters(filters);
				/*
				 * Products Request에 서비스와 필터 적용
				 */
				String groupKey = awsCostExplorer.getGroupKey();
				if (!StringUtils.isNullOrEmpty(groupKey)) {
					// 필수값 서비스 필터에 적용
					productsReq.withServiceCode(priceListService);
					// 필터 usagetype에 groupKey등록
					productsReq.getFilters()
							   .add(new Filter().withType("TERM_MATCH")
											    .withField("usageType")							
											    .withValue(groupKey));
				}
				/*
				 * get Products
				 */ 
				GetProductsResult productsResult = null;
				try {
					productsResult = pricing.getProducts(productsReq);					
				} catch (InternalErrorException e) {
					LOGGER.error("InternalErrorException {}"	, e);
					continue;
				} catch (InvalidParameterException e) {
					LOGGER.error("InvalidParameterException {}"	, e);
					continue;
				} catch (NotFoundException e) {
					LOGGER.error("NotFoundException {}"			, e);
					continue;
				} catch (InvalidNextTokenException e) {
					LOGGER.error("InvalidNextTokenException {}"	, e);
					continue;
				} catch (ExpiredNextTokenException e) {
					LOGGER.error("ExpiredNextTokenException {}"	, e);
					continue;
				}
				/*
				 * priceList Request setting
				 */
				List<String> priceList = new ArrayList<String>();
				priceList = productsResult.getPriceList();
				
				/*
				 * JJY
				 * 20210709
				 * 필수값인 servicecode와 usagetype를 가지고 조회하기때문에 
				 * priceList는 무조건 1건 이므로 priceList취합하는 로직 주석처리
				 */
//				if (priceList.size() > 1) {
//					/*
//					 * Products pagin > addAll Json List
//					 */
//					while (!StringUtils.isNullOrEmpty(productsResult.getNextToken())) {
//						productsReq = new GetProductsRequest().withFilters(filters)
//															  .withServiceCode(priceListFilter.getAwsPriceListService())
//															  .withNextToken(productsResult.getNextToken());
//						productsResult = pricing.getProducts(productsReq);
//						priceList.addAll(productsResult.getPriceList());
//					}
//				}
				
				/*
				 * price Json List > parse
				 */
				if (!priceList.isEmpty()) {
					try {
						// json 초기값 setting
						JSONParser parser 		 = new JSONParser();
						Object	   obj	  		 = parser.parse(priceList.get(0));
						
						JSONObject jsonObject	 = (JSONObject) obj;
						// jsonObject
						JSONObject product		 = (JSONObject) jsonObject.get("product"	);
						JSONObject terms		 = (JSONObject) jsonObject.get("terms"	  	);
						String 	   serviceCode	 = (String)	    jsonObject.get("serviceCode");
						// product
						JSONObject attributes	 = (JSONObject) product.get("attributes"   );
						String	   productFamily = (String)	    product.get("productFamily");
						String	   sku			 = (String)	    product.get("sku"		   );
						// terms
						JSONObject onDemand      = (JSONObject) terms.get("OnDemand");
						
						
						// jsonStrings.add(sku);
						
					} catch (Exception e) {
						LOGGER.error("Parsing Exception {}", e.getMessage(), e);
					}
				}
			}
		}
		
		
		
		
		// priceListList가 1건 이상일때 
//	if (priceListList != null && priceListList.size() > 0) {
//		/*
//		 * Recalculate Amount 
//		 */
//		LOGGER.debug("recalc {}","Recalculate Amount start");
//		// Recalculate Amount
//		// Comparison Amount
//		recalcCostExplorerList = recalculateAmountService.recalculate(costExplorerList, priceListList);
//		LOGGER.debug("recalc {}","Recalculate Amount end");
//
//
//	}
		
		return null;
	}

	/**
	 * @Method Name  : contains
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 9 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 9    :                   :    신규 개발.
	 * 
	 */
	private static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.indexOf(searchStr) >= 0;
	}

	/**
	 * @Method Name  : callPriceList
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 9 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 9    :                   :    신규 개발.
	 * 
	 * @param priceListFilterVO
	 * @return
	 */
	private AWSPricing callPricing(AwsPriceListFilterVO priceListFilter) {
		LOGGER.debug("getAWSPricing");
		
		AWSPricing pricing = null;
		try {
			pricing = AWSPricingClientBuilder.standard()
											 .withRegion(Regions.US_EAST_1)
											 .withCredentials(priceListFilter.getAwsCredentialsProvider())
											 .build();
		} catch (Exception e) {
			LOGGER.error("AWSCostExplorer Exception {}", e.getMessage());
		}
		return pricing;
	}


}
