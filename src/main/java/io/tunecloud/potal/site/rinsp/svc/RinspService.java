/**
 * 
 */
package io.tunecloud.potal.site.rinsp.svc;

import java.util.List;
import java.util.Map;

import io.tunecloud.potal.site.rinsp.vo.CalResultVO;
import io.tunecloud.potal.site.rinsp.vo.CustomVO;
import io.tunecloud.potal.site.rinsp.vo.FilterVO;

/**
 * <pre>
 * io.tunecloud.potal.site.rinsp.svc
 *   |_ RinspService.java
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
 *          JuYoung2  : 2021. 6. 24       :                : 신규 개발.
 * 
 */
public interface RinspService {
	
	/**
	 * @Method Name  : selectProjectKey
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 19 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 27    :                   :    신규 개발.
	 * 
	 * @param filterVO
	 * @return
	 */
	FilterVO selectProjectKey(FilterVO filterVO) throws Exception;

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
	List<CalResultVO> priceListRealignment(FilterVO recalcFilterVO) throws Exception;

}
