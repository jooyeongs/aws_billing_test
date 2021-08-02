/**
 * 
 */
package io.tunecloud.potal.site.awsapi.priceList.vo;

import lombok.Data;

/**
 * <pre>
 * io.tunecloud.potal.site.awsapi.priceList.vo
 *   |_ PriceListVO.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 6. 24 오후 5:19:50
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 6. 24       :                : 신규 개발.
 * 
 */
@Data
public class AwsPriceListVO {
	//Common Product
	private String servicecode;
	private String usagetype;
	private String servicename;

	//Common Terms
	private String unit;
	private String beginRange;
	private String endRange;
	private String currencyCode;
	private String currencyRate;
	private String description;
	
	private String location;
	
}
