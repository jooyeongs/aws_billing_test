/**
 * 
 */
package io.tunecloud.potal.site.awsapi.priceList.svc;

import java.util.List;

import io.tunecloud.potal.site.awsapi.costexplorer.vo.AwsCostExplorerVO;
import io.tunecloud.potal.site.awsapi.priceList.vo.AwsPriceListVO;
import io.tunecloud.potal.site.recalc.vo.FilterVO;

/**
 * <pre>
 * io.tunecloud.potal.site.awsapi.priceList.svc
 *   |_ AwsPriceListService.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 19 오후 5:16:20
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 19       :                : 신규 개발.
 * 
 */
public interface AwsPriceListService {

	/**
	 * @Method Name  : callPriceListList
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 20 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 20    :                   :    신규 개발.
	 * 
	 * @param filterVO
	 * @param costExplorerList
	 * @return
	 * @throws Exception
	 */
	AwsPriceListVO callPriceListList(FilterVO filterVO, AwsCostExplorerVO costExplorerList) throws Exception;

}
