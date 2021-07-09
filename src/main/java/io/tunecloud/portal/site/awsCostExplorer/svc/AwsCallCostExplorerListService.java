/**
 * 
 */
package io.tunecloud.portal.site.awsCostExplorer.svc;

import java.util.List;

import io.tunecloud.portal.site.awsCostExplorer.vo.AwsCostExplorerFilterVO;
import io.tunecloud.portal.site.awsCostExplorer.vo.AwsCostExplorerVO;

/**
 * <pre>
 * com.cloud.api.aws.costexplorer.svc
 *   |_ AwsCallCostExplorerListService.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:29:25
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
public interface AwsCallCostExplorerListService {

	/**
	 * @Method Name  : getCostExplorerList
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 7 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 7    :                   :    신규 개발.
	 * 
	 * @param costExplorerFilterVO
	 * @return
	 */
	List<AwsCostExplorerVO> getCostExplorerList(AwsCostExplorerFilterVO costExplorerFilterVO) throws Exception;


}
