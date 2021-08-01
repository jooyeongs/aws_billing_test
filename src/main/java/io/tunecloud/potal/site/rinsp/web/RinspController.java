/**
 * 
 */
package io.tunecloud.potal.site.rinsp.web;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import io.tunecloud.potal.site.rinsp.svc.RinspService;
import io.tunecloud.potal.site.rinsp.vo.CalResultVO;
import io.tunecloud.potal.site.rinsp.vo.CustomVO;
import io.tunecloud.potal.site.rinsp.vo.FilterVO;

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
public class RinspController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(RinspController.class);
	
	@Resource(name="rinspService")
	RinspService rinspService;
	
	@RequestMapping("/rinspList")
	public String rinspList(@ModelAttribute("filter") FilterVO filterVO, Model model, HttpServletRequest request) throws Exception {
		LOGGER.debug("rinsp start");
		/**
		 * Initial page::초기화면 설정
		 */
		LOGGER.debug("Initial page");
		if (filterVO.getCspId() <= 0) {
			// Defult CSP : AWS ID 넣기
			filterVO.setCspId(1);
		}
		if (StringUtils.isBlank(filterVO.getStartDate())) {
			// Empty object for Start Date
			LOGGER.debug("Empty object for Start Date");
			return "tunecloud/rinsp/rinspList";
		} else if (StringUtils.isBlank(filterVO.getEndDate())) {
			// Empty object for End Date
			LOGGER.debug("Empty object for End Date");
			return "tunecloud/rinsp/rinspList";
		}
		/**
		 * service group::서비스 그룹 만들기
		 */
		LOGGER.debug("set ServiceGroup");
		if (StringUtils.isNotEmpty(filterVO.getServiceName())) {
			if (StringUtils.isNotEmpty(filterVO.getService())) {
				// filterVO에 서비스 그룹 넣기
				filterVO.setServiceGroup(serviceGroup(filterVO.getServiceName(), filterVO.getService()));
			} else {
				// Empty object for Service ID
				LOGGER.debug("Empty object for Service ID");
				return "tunecloud/rinsp/rinspList";
			}
		} else {
			// Empty object for Service Value
			LOGGER.debug("Empty object for Service Value");
			return "tunecloud/rinsp/rinspList";
		}
		LOGGER.debug("Initial page end");
		/**
		 * select project key::프로젝트ID를 통해 Key를 불러온다.
		 */
		LOGGER.debug("select Project Key");
		FilterVO projectKey = new FilterVO();
		if (filterVO.getProjectId() > 0) {
			// select Project Key:DB에서 키 가지고 오기
			projectKey = rinspService.selectProjectKey(filterVO);
		} else {
			// Empty object for project Key
			LOGGER.debug("Empty object for project Key");
		}		
		filterVO.setAccessKey(projectKey.getAccessKey());
		filterVO.setSecretKey(projectKey.getSecretKey());
		/**
		 * result Map
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * process - rinspList
		 */
		if (filterVO.getCspId() == 1) {
			// AWS에 대하여 재산정 플로우 진행 
			result = rinspService.priceListRealignment(filterVO);
			
			List<CustomVO> resultList = new ArrayList<CustomVO>();
			CalResultVO calResultVO = (CalResultVO) result.get("calResultVO");
			
			if (!Objects.isNull(calResultVO)) { 
				for (int i=0; i<calResultVO.getBeginRanges().size();i++) {
					CustomVO custom = new CustomVO();
					custom.setServiceCode(calResultVO.getServicecodes().get(i));
					custom.setUsagetype(calResultVO.getUsagetypes().get(i));
					custom.setIntervalAmount(calResultVO.getIntervalAmount().get(i));
					custom.setPricePerUnit(calResultVO.getPricePerUnits().get(i));
					custom.setUnblendedCost(calResultVO.getOriginUsageTypePrices().get(i));
					custom.setBeginRange(calResultVO.getBeginRanges().get(i));
					custom.setEndRange(calResultVO.getEndRanges().get(i));
					custom.setCurrencyCodes(calResultVO.getCurrencyCodes().get(i));
					custom.setUnit(calResultVO.getServicecodes().get(i));
					custom.setLocation(calResultVO.getServicecodes().get(i));
					
					resultList.add(custom);
				}
			}
			
			model.addAttribute("resultList"	, resultList);
			
//			model.addAttribute("costExplorerList"	, 		result.get("costExplorerList")	);
//			model.addAttribute("priceList"			, 		result.get("priceList")			);
			
		}
		return "tunecloud/recalc/rinspList";
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
	private Map<String,String> serviceGroup(String serviceValue, String service) {
		/** 
		 * 서비스 Value와 서비스를 맵에 등록시킨다. 
		 */
		LOGGER.debug("service value and service in map");
		Map<String,String> serviceGroupMap = new HashMap<String,String>();
		serviceGroupMap.put(serviceValue, service);

		return serviceGroupMap;
	}

}
