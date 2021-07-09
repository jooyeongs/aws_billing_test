/**
 * 
 */
package io.tunecloud.portal.site.awsCredential.svc.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import io.tunecloud.portal.site.awsCredential.svc.CredentialsProviderService;

/**
 * <pre>
 * com.cloud.api.aws.credential.svc.impl
 *   |_ CredentialsProviderServiceImpl.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:29:09
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
@Service("credentialsProviderService")
public class CredentialsProviderServiceImpl implements CredentialsProviderService {

    /**
     * encrypt aeskey
     */
//    @Value("${encrypt.aeskey}")
//    private String aesKey;
//	
	@Override
	public AWSStaticCredentialsProvider getCredentialsProvider(String accessKey, String secretKey) throws Exception {
		// 암호화 되어있는 키 복호화 
//		EncryptUtil encryptUtil = new EncryptUtil(aesKey);
		
		// AWS 자격 증명 (AWS 액세스 키 ID 및 보안 액세스 키)에 대한 액세스를 제공
//		AWSCredentials credentials = new BasicAWSCredentials(encryptUtil.decryptAES(accessKey)
//															,encryptUtil.decryptAES(secretKey));
		AWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
		// 호출자가 AWS 요청을 승인하는 데 사용할 수있는 AWSCredentials를 반환합니다. 자격 증명을로드하기위한 자체 전략을 선택할 수 있습니다.
		AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
		return credentialsProvider;
	}


}
