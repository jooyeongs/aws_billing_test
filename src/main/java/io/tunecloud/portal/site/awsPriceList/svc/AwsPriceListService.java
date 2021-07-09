/**
 * 
 */
package io.tunecloud.portal.site.awsPriceList.svc;

import java.util.List;

import io.tunecloud.portal.site.awsPriceList.vo.AwsPriceListFilterVO;
import io.tunecloud.portal.site.awsPriceList.vo.AwsPriceListVO;

/**
 * <pre>
 * com.cloud.api.aws.pricelist.svc
 *   |_ AwsPriceListService.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:29:42
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
public interface AwsPriceListService {
	/**
	 * @Method Name  : getPricing
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 9 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 9    :                   :    신규 개발.
	 * 
	 * @param priceListFilter
	 * @return
	 */
	List<AwsPriceListVO> recalculate(AwsPriceListFilterVO priceListFilter) throws Exception; 

}
