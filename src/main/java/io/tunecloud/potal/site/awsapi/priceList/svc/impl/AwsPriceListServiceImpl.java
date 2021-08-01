/**
 * 
 */
package io.tunecloud.potal.site.awsapi.priceList.svc.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.pricing.AWSPricing;
import com.amazonaws.services.pricing.AWSPricingClientBuilder;
import com.amazonaws.services.pricing.model.GetProductsRequest;
import com.amazonaws.services.pricing.model.GetProductsResult;

import io.tunecloud.potal.site.awsapi.priceList.svc.AwsPriceListService;
import io.tunecloud.potal.site.awsapi.priceList.vo.AwsPriceListVO;
import io.tunecloud.potal.site.awsapi.util.AwsUtils;
import io.tunecloud.potal.site.rinsp.vo.FilterVO;

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
	
	@Value("${encrypt.aeskey}")
    private String aes256Key;
	
	@Value("${aws.endpoint.regionName}")
    private String regionName;
	
	@Override
	public AwsPriceListVO callPriceListList(FilterVO filterVO) throws Exception {
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
		
		/**
		 * <call utils>
		 *	US_EAST_1("us-east-1", "US East (N. Virginia)")
		 *	(Regions regions, String accessKey, String secretKey, String aes256Key)
		 */
		Regions 		endPoint = Regions.fromName(regionName);
		AWSPricing      pricing  = AwsUtils.authAwsPricing(endPoint, filterVO.getAccessKey(), filterVO.getSecretKey(), aes256Key);
			
		GetProductsRequest getProductsRequest = new GetProductsRequest();				//요청할 priceList
		GetProductsResult  getProductsResult  = new GetProductsResult();				//받은 priceList
		
		boolean isNextTokenCheck = false;	//토큰 체크
		do{		
			//nextToken validation check
			
			if( isNextTokenCheck == false ) {
				getProductsRequest = new GetProductsRequest().withServiceCode(filterVO.getService());	//요청할 서비스코드 입력
				getProductsResult  = pricing.getProducts(getProductsRequest);						//priceList 등록
				LOGGER.debug(getProductsResult.toString());
				isNextTokenCheck 	   = true;
			}else {
				//이후 실행시 NextToken 값을 이용 다음 페이지 priceList 정보 가져옴
				getProductsRequest = new GetProductsRequest().withServiceCode(filterVO.getService())				//요청할 서비스코드 입력
															 .withNextToken(getProductsResult.getNextToken());  //가져올 페이지 토큰 입력
				getProductsResult  = pricing.getProducts(getProductsRequest);		 							//priceList 등록
			}			
			
			//가져온 priceList JSON-simple을 이용하여 Parsing
	        for(String price : getProductsResult.getPriceList()) {
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
		}while(null != getProductsResult.getNextToken()); //NextToken 없을시 종료
		
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
