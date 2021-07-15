/**
 * 
 */
package io.tunecloud.potal.site.awsapi.credentials.svc;

import com.amazonaws.auth.AWSStaticCredentialsProvider;

/**
 * <pre>
 * io.tunecloud.potal.site.awsapi.credentials.svc
 *   |_ AwsCredentialService.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 6. 24 오후 4:24:50
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 6. 24       :                : 신규 개발.
 * 
 */
public interface AwsCredentialService {

	/**
	 * @Method Name  : getCredentialsProvider
	 * @Method Desc  :
	 * @작성일   : 2021. 6. 24 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 6. 24    :                   :    신규 개발.
	 * 
	 * @param secretKey
	 * @param accessKey
	 * @return 
	 */
	AWSStaticCredentialsProvider getCredentialsProvider(String secretKey, String accessKey) throws Exception;

}
