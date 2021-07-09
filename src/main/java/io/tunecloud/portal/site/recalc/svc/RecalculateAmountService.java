/**
 * 
 */
package io.tunecloud.portal.site.recalc.svc;

import java.util.List;

import io.tunecloud.portal.site.awsCostExplorer.vo.AwsCostExplorerFilterVO;
import io.tunecloud.portal.site.awsCostExplorer.vo.AwsCostExplorerVO;
import io.tunecloud.portal.site.awsPriceList.vo.AwsPriceListFilterVO;

/**
 * <pre>
 * com.cloud.web.aws.recalc.svc
 *   |_ RecalculateAmountService.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:30:47
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
public interface RecalculateAmountService {

	/**
	 * @Method Name  : callExplorerList
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 8 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 8    :                   :    신규 개발.
	 * 
	 * @param costExplorerFilterVO
	 * @return
	 */
	List<AwsCostExplorerVO> callExplorerList(AwsCostExplorerFilterVO costExplorerFilterVO) throws Exception;

	/**
	 * @Method Name  : recalculate
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 9 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 9    :                   :    신규 개발.
	 * 
	 * @param priceListFilterVO
	 * @return
	 */
	List<AwsCostExplorerVO> recalculate(AwsPriceListFilterVO priceListFilterVO) throws Exception;

}
