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
	public List<AwsPriceListVO> callPriceListList(FilterVO filterVO) throws Exception {
		LOGGER.debug("callCostExplorerServiceList");
		AwsPriceListVO 		 priceVo   = new AwsPriceListVO(); 
		List<AwsPriceListVO> priceList = new ArrayList<AwsPriceListVO>(); 
		GetProductsRequest getProductsRequest = new GetProductsRequest();				//요청할 priceList
		GetProductsResult  getProductsResult  = new GetProductsResult();				//받은 priceList
		boolean isNextTokenCheck = false;	//토큰 체크
		
		/**
		 * <call utils>
		 *	US_EAST_1("us-east-1", "US East (N. Virginia)")
		 *	(Regions regions, String accessKey, String secretKey, String aes256Key)
		 */
		Regions 		endPoint = Regions.fromName(regionName);
		AWSPricing      pricing  = AwsUtils.authAwsPricing(endPoint, filterVO.getAccessKey(), filterVO.getSecretKey(), aes256Key);
			
		
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
	                			 
	                			 priceVo = new AwsPriceListVO(); 
	                			 priceVo.setServicecode((String) attributes.get("servicecode"));
	                			 priceVo.setServicename((String) attributes.get("servicename"));	
	                			 priceVo.setUsagetype  ((String) attributes.get("usagetype"));	
	                			 if(null != attributes.get("location")){
	                				 priceVo.setLocation((String) attributes.get("location")); 
	                			 } else if(null == attributes.get("location") && null != attributes.get("fromLocation")){
	                				 priceVo.setLocation((String) attributes.get("fromLocation")); 
	                			 }	else {
	                				 priceVo.setLocation("global");
	                			 }
	                			 
	                			 priceVo.setUnit		((String) priceDimensionsValue.get("unit"));
	                			 priceVo.setBeginRange	((String) priceDimensionsValue.get("beginRange"));	
	                			 priceVo.setEndRange	((String) priceDimensionsValue.get("endRange"));	  
	                			 priceVo.setCurrencyCode((String) pricePerUnitMap.keySet().toString());	                			
	                			 priceVo.setCurrencyRate((String) pricePerUnitMap.values().toArray()[0]);	                			
	                			 priceVo.setDescription	((String) priceDimensionsValue.get("description"));
	                			 
	                			 priceList.add(priceVo);
	                		 }
	                	 }
	                 }
                 }catch (Exception e) {
	                 LOGGER.error("Parsing Exception {}", e.getMessage(), e);
	             }
	        }
		}while(null != getProductsResult.getNextToken()); //NextToken 없을시 종료
		
		return priceList;
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
