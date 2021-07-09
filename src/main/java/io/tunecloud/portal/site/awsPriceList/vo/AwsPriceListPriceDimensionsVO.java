/**
 * 
 */
package io.tunecloud.portal.site.awsPriceList.vo;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Data;

/**
 * <pre>
 * com.cloud.api.aws.pricelist.vo
 *   |_ AwsPriceListPriceDimensionsVO.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:30:06
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
@Data
public class AwsPriceListPriceDimensionsVO {
	/**
	 * Offer Term Code
	 */
	private String AWSPriceListOfferTermCode;	
	/**
	 * Rate Code
	 */
	private String AWSPriceListRateCode;		
	/**
	 * Unit
	 */
	private String AWSPriceListUnit;			
	/**
	 * Begin Range
	 */
	private String AWSPriceListBeginRange;		
	/**
	 * End Range
	 */
	private String AWSPriceListEndRange;		
	/**
	 * Description
	 */
	private String AWSPriceListDescription;		
	/**
	 * Price Per Unit
	 */
	private String AWSPriceListPricePerUnit;	
	/**
	 * Currency
	 */
	private String AWSPriceListCurrency;	
	/**
	 * Applies To List
	 */
	private ArrayList<String> AWSPriceListAppliesToList;	
	/**
	 * Term Attributes
	 */
	private HashMap<String,String> AWSPriceListTermAttributes;	
}
