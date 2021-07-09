/**
 * 
 */
package io.tunecloud.portal.site.awsCostExplorer.vo;

import java.util.List;

import com.amazonaws.auth.AWSStaticCredentialsProvider;

import lombok.Data;

/**
 * <pre>
 * com.cloud.api.aws.costexplorer.vo
 *   |_ AwsCostExplorerFilterVO.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:28:52
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
@Data
public class AwsCostExplorerFilterVO {
	/**
	 * StartDate
	 */
	private String awsCostExplorerStartDate;		
	/**
	 * EndDate
	 */
	private String awsCostExplorerEndDate;		
	/**
	 * Granularity
	 */
	private String awsCostExplorerGranularity;
	/**
	 * Group
	 */
	private String awsCostExplorerGroup;			
	/**
	 * Service code
	 */
	private List<String> awsCostExplorerService;
	/**
	 * Service value
	 */
	private List<String> awsCostExplorerServiceValue;	
	/**
	 * AWS Static Credentials Provider
	 */
	private AWSStaticCredentialsProvider awsCredentialsProvider;	
	
}
