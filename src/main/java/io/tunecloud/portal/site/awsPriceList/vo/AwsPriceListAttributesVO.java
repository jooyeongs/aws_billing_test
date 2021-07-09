/**
 * 
 */
package io.tunecloud.portal.site.awsPriceList.vo;

import lombok.Data;

/**
 * <pre>
 * com.cloud.api.aws.pricelist.vo
 *   |_ AwsPriceListAttributesVO.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:29:49
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
@Data
public class AwsPriceListAttributesVO {
	/**
	 * Fee Description
	 */
	private String AWSPriceListFeeDescription;	
	/**
	 * Fee Code 
	 */
	private String AWSPriceListFeeCode;			
	/**
	 * Usage Type
	 */
	private String AWSPriceListUsageType;		
	/**
	 * Location Type
	 */
	private String AWSPriceListLocationType;	
	/**
	 * Location
	 */
	private String AWSPriceListLocation;		
	/**
	 * Service Name
	 */
	private String AWSPriceListServiceName;		
	/**
	 * Operation
	 */
	private String AWSPriceListOperation;		
}
