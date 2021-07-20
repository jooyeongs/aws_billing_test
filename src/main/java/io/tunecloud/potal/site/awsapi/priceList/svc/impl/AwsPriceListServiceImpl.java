/**
 * 
 */
package io.tunecloud.potal.site.awsapi.priceList.svc.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
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

import io.tunecloud.potal.site.awsapi.costexplorer.vo.AwsCostExplorerVO;
import io.tunecloud.potal.site.awsapi.credentials.svc.AwsCredentialService;
import io.tunecloud.potal.site.awsapi.priceList.svc.AwsPriceListService;
import io.tunecloud.potal.site.awsapi.priceList.vo.AwsPriceListVO;
import io.tunecloud.potal.site.recalc.vo.FilterVO;

/**
 * <pre>
 * io.tunecloud.potal.site.awsapi.priceList.svc.impl
 *   |_ AwsPriceListServiceImpl.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 6. 24 오후 5:16:03
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 6. 24       :                : 신규 개발.
 * 
 */
@Service("awsPriceListService")
public class AwsPriceListServiceImpl implements AwsPriceListService {
	private final Logger LOGGER = LoggerFactory.getLogger(AwsPriceListServiceImpl.class);
	
	@Resource(name="awsCredentialService")
	AwsCredentialService awsCredentialService;
	
	@Override
	public AwsPriceListVO callPriceListList(
			FilterVO filterVO, List<AwsCostExplorerVO> costExplorerList) throws Exception {
		LOGGER.debug("callCostExplorerServiceList");
		AwsPriceListVO awsPriceList = new AwsPriceListVO();
		/**
		 * priceList API 값을 담을 리스트 생성	
		 */
		//Common Product
		List<String> servicecodes 	= new ArrayList<String>();	//서비스코드
		List<String> servicenames 	= new ArrayList<String>();	//서비스네임
		List<String> usagetypes		= new ArrayList<String>();	//사용유형 
		//Common Terms
		List<String> units			= new ArrayList<String>();	 
		List<String> beginRanges	= new ArrayList<String>();	//사용유형 최소범위
		List<String> endRanges		= new ArrayList<String>();	//사용유형 최대범위
		List<String> currencyCodes	= new ArrayList<String>();	//화폐
		List<String> currencyRates	= new ArrayList<String>();	//단위가격		
		List<String> descriptions	= new ArrayList<String>();	//단위 설명
		
		List<String> locations		= new ArrayList<String>();	//리전정보

		String serviceCode = null; 
		/**
		 * api 호출
		 */
		LOGGER.debug("call CostExplorer API");
		AWSPricing pricing = callPricing(filterVO);
		/**
		 * CostExplorerList에서 건 바이 건으로 usagetype별로 priceList를 불러오는 부분 
		 */
		Map<String,String> serviceGroup = filterVO.getServiceGroup();
		for (AwsCostExplorerVO awsCostExplorer : costExplorerList) {
			/**
			 * Products Request setting
			 * : 요청할 priceList
			 */
			List<Filter> 	   filters 				= new ArrayList<Filter>();
			GetProductsRequest getProductsRequest 	= new GetProductsRequest().withFilters(filters);	// 요청할	priceList
			GetProductsResult  getProductsResult  	= new GetProductsResult();							// 받은	priceList
			
			/**
			 * ServiceValue로 Service가져오기 
			 */
			serviceCode = serviceGroup.get(awsCostExplorer.getServiceValue());
			getProductsRequest.withServiceCode(serviceCode);	// 필수값 서비스 필터에 적용
			
			/**
			 * Products Request에 서비스와 필터 적용
			 */
			List<String> usageType = awsCostExplorer.getUsageType();
			if (usageType != null) {
				// 필터 usagetype에 groupKey등록
				getProductsRequest.getFilters()
						   		  .add(new Filter().withType ("TERM_MATCH"		)
											       .withField("usageType"		)							
											       .withValue(usageType.get(0)	));
			}
		
			/*
			 * get Products
			 */ 
			try {
				getProductsResult = pricing.getProducts(getProductsRequest);					
			} catch (InternalErrorException e) {
				LOGGER.error("InternalErrorException {}"	, e);
			} catch (InvalidParameterException e) {
				LOGGER.error("InvalidParameterException {}"	, e);
			} catch (NotFoundException e) {
				LOGGER.error("NotFoundException {}"			, e);
			} catch (InvalidNextTokenException e) {
				LOGGER.error("InvalidNextTokenException {}"	, e);
			} catch (ExpiredNextTokenException e) {
				LOGGER.error("ExpiredNextTokenException {}"	, e);
			}
			
			/*
			 * priceList Request setting
			 */
			List<String> priceList = new ArrayList<String>();
			priceList = getProductsResult.getPriceList();
			
			/*
			 * servicecode와 usagetype를 가지고 조회
			 */
			if (priceList.size() > 1) {
				/*
				 * Products pagin > addAll Json List
				 */
				while (StringUtils.isNotEmpty(getProductsResult.getNextToken())) {
//					getProductsRequest = new GetProductsRequest().withFilters  (filters						  	)
//																 .withNextToken(getProductsResult.getNextToken());
					getProductsRequest = new GetProductsRequest().withFilters  	 (filters						  )
																 .withServiceCode(serviceCode					  )
																 .withNextToken	 (getProductsResult.getNextToken());
					if (usageType != null) {
						// 필터 usagetype에 groupKey등록
						getProductsRequest.getFilters()
								   		  .add(new Filter().withType ("TERM_MATCH"		)
													       .withField("usageType"		)							
													       .withValue(usageType.get(0)	));
					}
					
					getProductsResult = pricing.getProducts(getProductsRequest);
					priceList.addAll(getProductsResult.getPriceList());
				}
			}
	        for(String price : priceList) {
	        	 try {
	        		 JSONParser parser 				= new JSONParser();
	                 Object obj         			= parser.parse(price);				//JSON Parsing 리턴객체를 object로 받음
	                 																	//필요한 값들은 JSONObject타입임
	                 JSONObject jsonObject        	= (JSONObject) obj;
	                 
	                 JSONObject product           	= (JSONObject) jsonObject.get("product");
	                 JSONObject terms             	= (JSONObject) jsonObject.get("terms");	                
	                 JSONObject attributes 			= (JSONObject) product.get("attributes");	                 
	                 for(Object termsKey : terms.keySet()) {							//OnDemand 와 Reserved 분리
	                	 JSONObject termsValue = (JSONObject) terms.get(termsKey);      
	                	 
	                	 for(Object onDemandkey : termsValue.keySet()) {
	                		 JSONObject onDemandValue 	= (JSONObject) termsValue.get(onDemandkey);
	                		 JSONObject priceDimensions = (JSONObject) onDemandValue.get("priceDimensions");
	                		 
	                		 for(Object priceDimensionskey : priceDimensions.keySet()) {
	                			 JSONObject priceDimensionsValue = (JSONObject) priceDimensions.get(priceDimensionskey);
	                			 JSONObject pricePerUnitMap = (JSONObject) priceDimensionsValue.get("pricePerUnit");	
	                			 servicecodes	.add((String) attributes.get("servicecode"));
	                			 servicenames	.add((String) attributes.get("servicename"));
	                			 usagetypes		.add((String) attributes.get("usagetype"));	
	                			 if(null != attributes.get("location")){
	                				 locations  .add((String) attributes.get("location"));
	                			 } else if(null == attributes.get("location") && null != attributes.get("fromLocation")){
	                				 locations  .add((String) attributes.get("fromLocation"));
	                			 }	else {
	                				 locations  .add("global");
	                			 }
	                			 
	                			 units			.add((String) priceDimensionsValue.get("unit"));
	                			 beginRanges	.add((String) priceDimensionsValue.get("beginRange"));	
	                			 endRanges		.add((String) priceDimensionsValue.get("endRange"));	  
	                			 currencyCodes	.add((String) pricePerUnitMap.keySet().toString());	                			
	                			 currencyRates	.add((String) pricePerUnitMap.values().toArray()[0]);	                			
	                			 descriptions	.add((String) priceDimensionsValue.get("description"));
	                			 
	                		 }
	                	 }
	                 }
	                 
	                 //VO에 저장
	                 
	                 //Common Product
	                 awsPriceList.setServicecodes	(servicecodes);
	                 awsPriceList.setServicenames	(servicenames);
	                 awsPriceList.setUsagetypes		(usagetypes);	
	                 //Common Terms
	                 awsPriceList.setUnits			(units);       
	                 awsPriceList.setBeginRanges	(beginRanges);
	                 awsPriceList.setEndRanges		(endRanges);	
	                 awsPriceList.setCurrencyCodes	(currencyCodes);
	                 awsPriceList.setCurrencyRates	(currencyRates);	                 
	                 awsPriceList.setDescriptions	(descriptions);	
	                 awsPriceList.setLocations		(locations);
                }catch (Exception e) {
	                 LOGGER.error("Parsing Exception {}", e.getMessage(), e);
	             }
	        }
		}
		return awsPriceList;
	}

	/**
	 * @Method Name  : callPricing
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
	private AWSPricing callPricing(FilterVO filterVO) {
		LOGGER.debug("getAWSPricing");
		
		AWSPricing pricing = null;
		try {
			LOGGER.debug("AWSPricing builder");
			pricing = AWSPricingClientBuilder.standard		 (									  )
											 .withRegion	 (Regions.US_EAST_1					  )
											 .withCredentials(filterVO.getAwsCredentialsProvider())
											 .build			 (									  );
		} catch (Exception e) {
			LOGGER.error("AWSPricing Exception {}", e.getMessage());
		}
		return pricing;
	}

}
