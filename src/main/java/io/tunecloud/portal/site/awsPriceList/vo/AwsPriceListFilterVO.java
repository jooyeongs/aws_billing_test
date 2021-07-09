/**
 * 
 */
package io.tunecloud.portal.site.awsPriceList.vo;

import java.util.List;

import com.amazonaws.auth.AWSStaticCredentialsProvider;

import io.tunecloud.portal.site.awsCostExplorer.vo.AwsCostExplorerVO;
import lombok.Data;

/**
 * <pre>
 * com.cloud.api.aws.pricelist.vo
 *   |_ AwsPriceListFilterVO.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:29:57
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
@Data
public class AwsPriceListFilterVO {
	/**
	 * Service code
	 */
	private String awsPriceListService;
	/**
	 * Service code List
	 */
	private List<String> awsPriceListServiceList;
	/**
	 * Service value
	 */
	private String awsPriceListServiceValue;
	/**
	 * Service value List
	 */
	private List<String> awsPriceListServiceValueList;
	/**
	 * Usage Type
	 */
	private String awsPriceListUsageType;
	/**
	 * AWS Static Credentials Provider
	 */
	private AWSStaticCredentialsProvider awsCredentialsProvider;	
	/**
	 * AWS Cost Explorer List
	 */
	private List<AwsCostExplorerVO> awsCostExplorerVOList;
}
