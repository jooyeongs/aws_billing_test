/**
 * 
 */
package io.tunecloud.potal.site.recalc.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.amazonaws.auth.AWSStaticCredentialsProvider;

import io.tunecloud.potal.site.awsapi.credentials.svc.AwsCredentialService;
import io.tunecloud.potal.site.recalc.svc.RecalcService;
import io.tunecloud.potal.site.recalc.vo.CalResultVO;
import io.tunecloud.potal.site.recalc.vo.FilterVO;
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
	
	@Resource(name="recalcService")
	RecalcService recalcService;
	
	@RequestMapping("/recalcList")
	public String recalcList(@ModelAttribute("filter") FilterVO filterVO, Model model, HttpServletRequest request) throws Exception {
		LOGGER.debug("recalcList start");
		/**
		 * result Map
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * TEST group, granularity
		 */
		filterVO.setGroupBy("USAGE_TYPE");
		filterVO.setGranulaity("MONTHLY");
		/**
		 * validation check
		 */
		LOGGER.debug("validation");
		if (StringUtils.isBlank(filterVO.getStartDate())) {
			LOGGER.error("StartDate is null");
			
			model.addAttribute("result", result);
			return "tunecloud/recalc/recalcList";
			
		} else if (StringUtils.isBlank(filterVO.getEndDate())) {
			LOGGER.error("EndDate is null");
			
			model.addAttribute("result", result);
			return "tunecloud/recalc/recalcList";
			
		} else if (filterVO.getService() == null || filterVO.getService().size() < 1) {
			LOGGER.error("Service is null");
			
			model.addAttribute("result", result);
			return "tunecloud/recalc/recalcList";
			
		} else if (filterVO.getServiceValue() == null || filterVO.getServiceValue().size() < 1) {
			LOGGER.error("Service Value is null");
			
			model.addAttribute("result", result);
			return "tunecloud/recalc/recalcList";
			
		} else {
			/**
			 * service group
			 */
			Map<String,String> serviceGroup = serviceGroup(filterVO);
			filterVO.setServiceGroup(serviceGroup);
		}

		
		/**
		 * Credentials Provider
		 * : DB에서가져올 부분
		 */
		LOGGER.debug("get awsCredential");
		EncryptUtil encryptUtil = new EncryptUtil(aesKey); 
		
		String accessKey = "AKIAVXG4IB3B57753VJC";
		String secretKey = "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";
		
		accessKey = encryptUtil.encryptAES(accessKey);
		secretKey = encryptUtil.encryptAES(secretKey);
		
		AWSStaticCredentialsProvider credentialsProvider = credentialService.getCredentialsProvider(accessKey, secretKey);
		
		filterVO.setAwsCredentialsProvider(credentialsProvider);
		LOGGER.debug("Test Data set complate");

		List<String> 	resultList = new ArrayList<String>();
		if (StringUtils.isNotBlank(filterVO.getStartDate())) {
			// ver1. PriceList를 DB에 적재한다.
//			result = recalcService.savePriceList(filterVO);
			// ver2. 조회된 PriceList와 Cost Explorer를 이용하여 재산정을 진행한다.
			result = recalcService.priceListRealignment(filterVO);	
			
		}
		
//		ExplorerListApiParsing.explorerPasingJson(vo, evo);	// explorerListParsing
//		PriceListApiParsing.priceParsingJson(vo, priceVo);	// priceListJsonParsing		
//		CalListPrintImpl.calList(priceVo, evo, result);				// 검산식 수행
//		CalListPrintImpl.calInfoPrint(result);						// 검산값 비교 출력
//		CalListPrintImpl.calPrint();
						
//		if(!result.isEmpty()) Objects.isNull(result.get(""),""); 

//		List<CalResultVO> calLiat =  result.get("calResultVO");
		
		
		model.addAttribute("costExplorerList"	, 		result.get("costExplorerList")	);
		model.addAttribute("priceList"			, 		result.get("priceList")			);
		model.addAttribute("resultList"			, 		result.get("calResultVO")		);
		
		return "tunecloud/recalc/recalcList";
	}
	
	/**
	 * @Method Name  : serviceGroup
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 20 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 20    :                   :    신규 개발.
	 * 
	 * @param filterVO
	 */
	private Map<String,String> serviceGroup(FilterVO filterVO) {
		Map<String,String> m = new HashMap<String,String>();
		if (filterVO.getServiceValue() != null && filterVO.getServiceValue().size() > 0) {
			List<String> serviceValue = filterVO.getServiceValue(); 
			if (filterVO.getService() != null && filterVO.getService().size() > 0) {
				List<String> service = filterVO.getService();
				if (serviceValue.size() == service.size()) {
					for (int i=0;i<=(serviceValue.size()-1);i++) {
						m.put(serviceValue.get(i), service.get(i));
					}
				}
			}
		}
		return m;
	}

	@RequestMapping("/recalc") 
	public String recalc(FilterVO filterVO, Model model, HttpServletRequest request) throws Exception {
		LOGGER.debug("recalc start");
		/**
		 * TEST Credentials Provider
		 */
		LOGGER.debug("get awsCredential");
		EncryptUtil encryptUtil = new EncryptUtil(aesKey); 
		
		String accessKey = "AKIAVXG4IB3B57753VJC";
		String secretKey = "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";
		
		accessKey = encryptUtil.encryptAES(accessKey);
		secretKey = encryptUtil.encryptAES(secretKey);
		
		AWSStaticCredentialsProvider credentialsProvider = credentialService.getCredentialsProvider(accessKey, secretKey);
		
		filterVO.setAwsCredentialsProvider(credentialsProvider);
		/**
		 * TEST Start Date
		 */
		LOGGER.debug("get start date");
		String startDate = "2021-06-01";
		
		filterVO.setStartDate(startDate);
		/**
		 * TEST End Date
		 */
		LOGGER.debug("get end date");
		String endDate = "2021-07-19";	
		
		filterVO.setEndDate(endDate);
		/**
		 * TEST Service code : 
		 * AmazonS3 		,  AWSCertificateManager 	,  AmazonGlacier
		 * AmazonRoute53 	,  AmazonAPIGateway 		,  AmazonCloudFront
		 * AmazonSES 		,  AWSQueueService 			,  AmazonCloudWatch
		 * AmazonEC2 		,  AWSOtherEBS 
		 */
		LOGGER.debug("get Service value");
		List<String> service = new ArrayList<String>();
		
		service.add("AmazonS3"							);
		service.add("AWSCertificateManager"				);
		
		filterVO.setService(service);
		/**
		 * TEST Service value : 
		 * Amazon Simple Storage Service 	,  AWS Certificate Manager 		,  Amazon Glacier
		 * Amazon Route 53 					,  Amazon API Gateway 			,  Amazon CloudFront
		 * Amazon Simple Email Service 		,  Amazon Simple Queue Service 	,  AmazonCloudWatch
		 * Amazon Elastic Compute Cloud 	,  AWSOtherEBS 
		 */
		LOGGER.debug("get Service value");
		List<String> serviceValue = new ArrayList<String>();
		
		serviceValue.add("Amazon Simple Storage Service"	);
		serviceValue.add("AWS Certificate Manager"			);
		
		filterVO.setServiceValue(serviceValue);
		LOGGER.debug("Test Data set complate");
		
		/**
		 * result Map
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		// ver1. PriceList를 DB에 적재한다.
//		result = recalcService.savePriceList(filterVO);
		// ver2. 조회된 PriceList와 Cost Explorer를 이용하여 재산정을 진행한다.
		result = recalcService.priceListRealignment(filterVO);		
		
		model.addAttribute("result"		, result);
		
		return "tunecloud/recalc/recalc";
		
	}
}
