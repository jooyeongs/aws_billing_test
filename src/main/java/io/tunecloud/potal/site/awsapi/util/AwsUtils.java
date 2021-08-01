/**
 * 
 */
package io.tunecloud.potal.site.awsapi.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.costexplorer.AWSCostExplorer;
import com.amazonaws.services.costexplorer.AWSCostExplorerClientBuilder;
import com.amazonaws.services.pricing.AWSPricing;
import com.amazonaws.services.pricing.AWSPricingClientBuilder;

import io.tunecloud.potal.site.util.EncryptUtil;

/**
 * <pre>
 * io.tunecloud.potal.site.util
 *   |_ AwsUtils.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 19 오후 1:05:24
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 19       :                : 신규 개발.
 * 
 */
@Service
public class AwsUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(AwsUtils.class);
	private String accessKey;
	private String secretKey;

	private AwsUtils() {
	  this.accessKey = "AKIAVXG4IB3B57753VJC";
	  this.secretKey = "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";
	}

	public AwsUtils(String accessKey, String secretKey, String aes256Key) 
		  throws UnsupportedEncodingException, NoSuchAlgorithmException, GeneralSecurityException {
		EncryptUtil encryptUtil = new EncryptUtil(aes256Key);
	
		this.accessKey = encryptUtil.decryptAES(accessKey);
		this.secretKey = encryptUtil.decryptAES(secretKey);
		LOGGER.debug("decrypt complet");
	}
	  
	public static AWSStaticCredentialsProvider getCredentialsProvider(String accessKey, String secretKey, String aes256Key) {
		LOGGER.debug("Credentials Provider start!!");
		/**
		 * 암호화된 AWS계정을 복호화
		 */
		AwsUtils awsUtils = null;		
		if (StringUtils.isEmpty(accessKey) || StringUtils.isEmpty(secretKey)) {
			awsUtils = new AwsUtils();
		} else {
			try {
				awsUtils = new AwsUtils(accessKey, secretKey, aes256Key);
			} catch(UnsupportedEncodingException | GeneralSecurityException e) {
				LOGGER.error("UnsupportedEncodingException | GeneralSecurityException", e);
				return null;
			}
		}
		/**
		 * AWS 자격 증명 (AWS 액세스 키 ID 및 보안 액세스 키)에 대한 액세스를 제공
		 */
		AWSCredentials credentials = new BasicAWSCredentials(awsUtils.accessKey, awsUtils.secretKey);
		LOGGER.debug("credentials");
		/**
		 * 호출자가 AWS 요청을 승인하는 데 사용할 수있는 AWSCredentials를 반환
		 */
		AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
		LOGGER.debug("credentialsProvider");
		
		return credentialsProvider;
	}
	
	public static AWSCostExplorer authAwsCe(Regions regions, String accessKey, String secretKey, String aes256Key) {
		LOGGER.debug("getCostExplorer");
		
		if (regions == null) {
			regions = Regions.US_EAST_1;
		}
		
		AWSStaticCredentialsProvider credentialsProvider = getCredentialsProvider(accessKey, secretKey, aes256Key);
		
		AWSCostExplorer costExplorer = null;
		try {
			LOGGER.debug("AWSCostExplorer builder");
			costExplorer = AWSCostExplorerClientBuilder.standard	   (				   )
													   .withRegion	   (regions  		   )
													   .withCredentials(credentialsProvider)
													   .build		   (				   );
		} catch (Exception e) {
			LOGGER.error("AWSCostExplorer Exception {}", e.getMessage());
		}
		return costExplorer;
	}
	public static AWSPricing authAwsPricing(Regions regions, String accessKey, String secretKey, String aes256Key) {
		LOGGER.debug("getAWSPricing");
		
		if (regions == null) {
			regions = Regions.US_EAST_1;
		}
		
		AWSStaticCredentialsProvider credentialsProvider = getCredentialsProvider(accessKey, secretKey, aes256Key);
		
		AWSPricing pricing = null;
		try {
			LOGGER.debug("AWSPricing builder");
			pricing = AWSPricingClientBuilder.standard		 (					 )
										     .withRegion	 (regions			 )
										     .withCredentials(credentialsProvider)
										     .build			 (					 );
		} catch (Exception e) {
			LOGGER.error("AWSPricing Exception {}", e.getMessage());
		}
		return pricing;
	}
	
}
