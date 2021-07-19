/**
 * 
 */
package io.tunecloud.potal.site.recalc.svc;

import java.util.Map;

import io.tunecloud.potal.site.recalc.vo.FilterVO;

/**
 * <pre>
 * io.tunecloud.potal.site.recalc.svc
 *   |_ RecalcService.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 6. 24 오후 2:01:52
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 6. 24       :                : 신규 개발.
 * 
 */
public interface RecalcService {

	/**
	 * @Method Name  : savePriceList
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 19 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 19    :                   :    신규 개발.
	 * 
	 * @param recalcFilterVO
	 * @return
	 */
	Map<String, Object> savePriceList(FilterVO recalcFilterVO) throws Exception;

	/**
	 * @Method Name  : priceListRealignment
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 19 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 19    :                   :    신규 개발.
	 * 
	 * @param recalcFilterVO
	 * @return
	 */
	Map<String, Object> priceListRealignment(FilterVO recalcFilterVO) throws Exception;

}
