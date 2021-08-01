/**
 * 
 */
package io.tunecloud.potal.site.awsapi.costexplorer.svc;

import java.util.List;

import com.amazonaws.services.costexplorer.AWSCostExplorer;

import io.tunecloud.potal.site.awsapi.costexplorer.vo.AwsCostExplorerVO;
import io.tunecloud.potal.site.awsapi.costexplorer.vo.ExplorerListVO;
import io.tunecloud.potal.site.rinsp.vo.FilterVO;

/**
 * <pre>
 * io.tunecloud.potal.site.awsapi.costexplorer.svc
 *   |_ AwsCostExplorerService.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 19 오후 5:11:49
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 19       :                : 신규 개발.
 * 
 */
public interface AwsCostExplorerService {

	/**
	 * @Method Name  : callCostExplorerServiceList
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
	List<AwsCostExplorerVO> callCostExplorerList(FilterVO filterVO) throws Exception;

}
