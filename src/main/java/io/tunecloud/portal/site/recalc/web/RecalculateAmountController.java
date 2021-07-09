/**
 * 
 */
package io.tunecloud.portal.site.recalc.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.amazonaws.auth.AWSStaticCredentialsProvider;

import io.tunecloud.portal.site.awsCostExplorer.vo.AwsCostExplorerFilterVO;
import io.tunecloud.portal.site.awsCostExplorer.vo.AwsCostExplorerVO;
import io.tunecloud.portal.site.awsCredential.svc.CredentialsProviderService;
import io.tunecloud.portal.site.awsPriceList.vo.AwsPriceListFilterVO;
import io.tunecloud.portal.site.recalc.svc.RecalculateAmountService;

/**
 * <pre>
 * com.cloud.web.aws.recalc.web
 *   |_ RecalculateAmountController.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 8 오후 4:31:23
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 8       :                : 신규 개발.
 * 
 */
@Controller
//@RequestMapping("/cost")
public class RecalculateAmountController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(RecalculateAmountController.class);
	/**
     * encrypt aeskey
     */
//    @Value("${encrypt.aeskey}")
//    private String aesKey;
	/**
	 * recalculate Amount Service
	 */
	@Resource(name="credentialsProviderService")
	private CredentialsProviderService credentialsProviderService;
	
	/**
	 * recalculate Amount Service
	 */
	@Resource(name="recalculateAmountService")
	private RecalculateAmountService recalculateAmountService;

	/**
	 * @Method Name  : recalc
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 8 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 8    :                   :    신규 개발.
	 * 
	 * @param costExplorerFilterVO
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/")
	public String recalc(@ModelAttribute("filterVO") AwsCostExplorerFilterVO costExplorerFilterVO
						, Model model, HttpServletRequest request) throws Exception {
		/*
		 *  초기화
		 */
		AwsPriceListFilterVO 	priceListFilterVO 		= new AwsPriceListFilterVO();
		List<AwsCostExplorerVO> costExplorerList 		= new ArrayList<AwsCostExplorerVO>();
		List<AwsCostExplorerVO> recalcCostExplorerList 	= new ArrayList<AwsCostExplorerVO>();
		
		
		/*
		 * cost Explorer filter setting
		 */
		// TEST Start Date
		String startDate = "2021-06-01";
		costExplorerFilterVO.setAwsCostExplorerStartDate(startDate);
		
		// TEST End Date
		String endDate = "2021-07-08";	
		costExplorerFilterVO.setAwsCostExplorerEndDate(endDate);
		
		// TEST Service value : Amazon Simple Storage Service ,  AWS Certificate Manager
		List<String> serviceValue = new ArrayList<String>();
		serviceValue.add("Amazon Simple Storage Service");
		serviceValue.add("AWS Certificate Manager");
		// 리스트형태로 서비스 받기		
		costExplorerFilterVO.setAwsCostExplorerServiceValue(serviceValue);
		priceListFilterVO.setAwsPriceListServiceValueList(serviceValue);
		
		// TEST Service code : AmazonS3 ,  AWSCertificateManager
		List<String> serviceCode = new ArrayList<String>();
		serviceCode.add("AmazonS3");
		serviceCode.add("AWSCertificateManager");
		// 리스트형태로 서비스 받기
		costExplorerFilterVO.setAwsCostExplorerService(serviceCode);
		priceListFilterVO.setAwsPriceListServiceList(serviceCode);
		
		// TEST access, secret key
		String accessKey = "AKIAVXG4IB3B57753VJC";
		String secretKey = "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";
		
//		/EncryptUtil encryptUtil = new EncryptUtil(aesKey);		
//		accessKey = encryptUtil.encryptAES(accessKey);
//		secretKey = encryptUtil.encryptAES(secretKey);
		
		
		/*
		 * Get Credentials Provider
		 */
		LOGGER.debug("recalc {}","Get Credentials Provider");
		AWSStaticCredentialsProvider credentialsProvider = credentialsProviderService.getCredentialsProvider(accessKey,secretKey);
		
		
		/*
		 * cost Explorer
		 */
		LOGGER.debug("recalc {}","cost Explorer start");
		// credentials Provider > cost explorer 
		costExplorerFilterVO.setAwsCredentialsProvider(credentialsProvider);
		
		// api를 통해 costExplorer를 조회한 뒤 가공하여 List를 가지고 온다 
		costExplorerList = recalculateAmountService.callExplorerList(costExplorerFilterVO);		
		LOGGER.debug("recalc {}","cost Explorer end");

		
		// costExplorer가 1건 이상일때 
		if (costExplorerList != null && costExplorerList.size() > 0) {
			priceListFilterVO.setAwsCostExplorerVOList(costExplorerList);
			/*
			 * price List 
			 */
			LOGGER.debug("recalc {}","price List start");
			// credentials Provider > price List 
			priceListFilterVO.setAwsCredentialsProvider(credentialsProvider);
			
			// api를 통해 priceList를 조회한 뒤 가공하여 재산정 List를 가지고 온다 
			// priceListList = recalculateAmountService.callPriceList(priceListFilterVO);
			recalcCostExplorerList = recalculateAmountService.recalculate(priceListFilterVO);
			LOGGER.debug("recalc {}","price List end");

		}
		
		model.addAttribute("filterVO"				, costExplorerFilterVO	);
		model.addAttribute("costExplorerList"		, costExplorerList		);
		model.addAttribute("recalcCostExplorerList"	, recalcCostExplorerList);

		return "recalc";
		
	}
	
}
