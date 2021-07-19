/**
 * 
 */
package io.tunecloud.potal.site.awsapi.credentials.svc.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import io.tunecloud.potal.site.awsapi.credentials.svc.AwsCredentialService;
import io.tunecloud.potal.site.util.EncryptUtil;

/**
 * <pre>
 * io.tunecloud.potal.site.awsapi.credentials.svc.impl
 *   |_ AwsCredentialServiceImpl.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 6. 24 오후 4:24:14
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 6. 24       :                : 신규 개발.
 * 
 */
@Service("awsCredentialService")
public class AwsCredentialServiceImpl implements AwsCredentialService {
	private final Logger LOGGER = LoggerFactory.getLogger(AwsCredentialServiceImpl.class);
	
	@Value("${encrypt.aeskey}")
    private String aesKey;

	@Override
	public AWSStaticCredentialsProvider getCredentialsProvider(String accessKey, String secretKey) throws Exception {
		LOGGER.debug("getCredentialsProvider start");
		/**
		 * 암호화된 AWS계정을 복호화
		 */
		EncryptUtil encryptUtil = new EncryptUtil(aesKey);
		accessKey = encryptUtil.decryptAES(accessKey);
		secretKey = encryptUtil.decryptAES(secretKey);
		LOGGER.debug("decrypt complet");
		/**
		 * AWS 자격 증명 (AWS 액세스 키 ID 및 보안 액세스 키)에 대한 액세스를 제공
		 */
		AWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
		LOGGER.debug("credentials");
		/**
		 * 호출자가 AWS 요청을 승인하는 데 사용할 수있는 AWSCredentials를 반환
		 */
		AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
		LOGGER.debug("credentialsProvider");
		return credentialsProvider;
	}
}
