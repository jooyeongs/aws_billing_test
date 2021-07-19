/**
 * 
 */
package io.tunecloud.potal.site.recalc.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.amazonaws.auth.AWSStaticCredentialsProvider;

import io.tunecloud.potal.site.awsapi.credentials.svc.AwsCredentialService;
import io.tunecloud.potal.site.util.EncryptUtil;

/**
 * <pre>
 * io.tunecloud.potal.site.recalc.web
 *   |_ RecalcController.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 6. 24 오후 3:34:18
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 6. 24       :                : 신규 개발.
 * 
 */
@Controller
public class RecalcController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(RecalcController.class);
	
	@Value("${encrypt.aeskey}")
    private String aesKey;
	
	@Resource(name="awsCredentialService")
	AwsCredentialService credentialService; 
	
	@RequestMapping("/recalc") // @ModelAttribute("") 
	public String recalc(Model model, HttpServletRequest request) throws Exception {
		LOGGER.debug("recalc start");
		/**
		 * Test Data
		 */
		EncryptUtil encryptUtil = new EncryptUtil(aesKey); 
		String accessKey = "AKIAVXG4IB3B57753VJC";
		String secretKey = "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";
		LOGGER.debug("Test Data set complate");
		// Test Data end
		
		/**
		 * credentialService
		 */
		LOGGER.debug("get awsCredential");
		AWSStaticCredentialsProvider credentialsProvider = credentialService.getCredentialsProvider(secretKey, accessKey);
		
		/**
		 * cost Explorer
		 */
		
		
		
		return "tunecloud/recalc/recalc";
	}
}
