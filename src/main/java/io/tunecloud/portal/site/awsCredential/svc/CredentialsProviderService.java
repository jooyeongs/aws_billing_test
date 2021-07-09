/**
 * 
 */
package io.tunecloud.portal.site.awsCredential.svc;

import com.amazonaws.auth.AWSStaticCredentialsProvider;

/**
 * <pre>
 * com.cloud.api.aws.credential.svc
 *   |_ CredentialsProviderService.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:29:21
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
public interface CredentialsProviderService {

	/**
	 * @Method Name  : getCredentialsProvider
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 8 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 8    :                   :    신규 개발.
	 * 
	 * @return
	 */
	AWSStaticCredentialsProvider getCredentialsProvider(String secretKey, String accessKey) throws Exception; 

}
