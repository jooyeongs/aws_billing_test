/**
 * 
 */
package io.tunecloud.portal.site.awsPriceList.vo;

import java.util.ArrayList;

import lombok.Data;

/**
 * <pre>
 * com.cloud.api.aws.pricelist.vo
 *   |_ AwsPriceListTermsVO.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:30:22
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
@Data
public class AwsPriceListTermsVO {
	/**
	 * Term Type
	 */
	private String AWSPriceListTermType;
	/**
	 * AWSPriceListPriceDimensionsVO
	 */
	private AwsPriceListPriceDimensionsVO awsPriceListPriceDimensionsVO;
	/**
	 * AWSPriceListPriceDimensionsVOList
	 */
	private ArrayList<AwsPriceListPriceDimensionsVO> awsPriceListPriceDimensionsVOList;
}
