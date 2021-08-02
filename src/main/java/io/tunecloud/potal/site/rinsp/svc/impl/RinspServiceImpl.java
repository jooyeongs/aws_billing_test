/**
 * 
 */
package io.tunecloud.potal.site.rinsp.svc.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.costexplorer.model.DateInterval;
import com.amazonaws.services.costexplorer.model.Group;
import com.amazonaws.services.costexplorer.model.ResultByTime;

import io.tunecloud.potal.site.awsapi.costexplorer.svc.AwsCostExplorerService;
import io.tunecloud.potal.site.awsapi.costexplorer.vo.AwsCostExplorerVO;
import io.tunecloud.potal.site.awsapi.priceList.svc.AwsPriceListService;
import io.tunecloud.potal.site.awsapi.priceList.svc.impl.FreeTierCalInfo;
import io.tunecloud.potal.site.awsapi.priceList.vo.AwsPriceListVO;
import io.tunecloud.potal.site.rinsp.dao.RinspDAO;
import io.tunecloud.potal.site.rinsp.svc.RinspService;
import io.tunecloud.potal.site.rinsp.vo.CalResultVO;
import io.tunecloud.potal.site.rinsp.vo.FilterVO;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * io.tunecloud.potal.site.rinsp.svc.impl
 *   |_ RinspServiceImpl.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 6. 24 오후 4:39:09
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 6. 24       :                : 신규 개발.
 * 
 */
@RequiredArgsConstructor
@Service("rinspService")
public class RinspServiceImpl implements RinspService{
	
	private final Logger LOGGER = LoggerFactory.getLogger(RinspServiceImpl.class);
	
	private final RinspDAO rinspDAO;

	@Value("${encrypt.aeskey}")
    private String aes256Key;
	
	@Value("${aws.endpoint.regionName}")
    private String regionName;
	
	@Resource(name="awsCostExplorerService")
	AwsCostExplorerService costExplorerService;
	
	@Resource(name="awsPriceListService")
	AwsPriceListService priceListService;
	
	static List<String> 	  monTotalPriceList 	= new ArrayList<String>(); 	  	 //월별 총가격 검산 결과 리스트
 	static List<List<String>> monUsageTypePriceList = new ArrayList<List<String>>(); //월별 사용타입별 가격 검산 결과 리스트	
 	
	/**
	 * 프로젝트의 csp 접근키와 암호키를 조회한다.
	 */
	@Override
	public FilterVO selectProjectKey(FilterVO filterVO) throws Exception {
		return rinspDAO.selectProjectKey(filterVO);
	}

	/**
	 * PriceList와 CostExplorer값을 비교하여 재산정값을 도출한다.
	 */
	@Override
	public List<CalResultVO> priceListRealignment(FilterVO filterVO) throws Exception {
		/**
		 * CostExplorer 
		 */
		LOGGER.debug("costExplorer");
		AwsCostExplorerVO 	 costExplorer 	 = costExplorerService.callCostExplorerList(filterVO);
		/**
		 * priceList
		 */
		LOGGER.debug("priceList ");
		List<AwsPriceListVO> priceList 		 = priceListService.callPriceListList(filterVO);
		/**
		 * 검산
		 */
		LOGGER.debug("priceList Realignment ");
		List<CalResultVO> 	 calResultVOList = calList(costExplorer,priceList);
		
		/**
		 * INSERT TO DB
		 */
		LOGGER.debug("INSERT TO DB");
		if (!Objects.isNull(calResultVOList) && calResultVOList.size() > 0) {
			try {
				rinspDAO.insertCspCostRInsp(calResultVOList);
			} catch (Exception e) {
				
			}
		} else {
			LOGGER.debug("Fail to INSERT");
		}
		
		
		return calResultVOList;
	}
	
	/**
	 * @Method Name  : calList
	 * @Method Desc  :
	 * @작성일   : 2021. 7. 18 
	 * @작성자   : JuYoung2
	 * @변경이력  :
	 *           이름              :      일자                :         근거자료       :          변경내용
	 *           -------------------------------------------------------------------
	 *          JuYoung2    :   2021. 7. 18    :                   :    신규 개발.
	 * 
	 * @param priceList
	 * @param costExplorerList
	 * @return
	 */
	private List<CalResultVO> calList(AwsCostExplorerVO evo, List<AwsPriceListVO> pvo) {
		CalResultVO calResult = new CalResultVO();
		List<CalResultVO> calResultList = new ArrayList<CalResultVO>();
		
		BigDecimal   calTotalPrice		  = new BigDecimal("0"); 	//검산 총 금액
		BigDecimal   originTotalPrice	  = new BigDecimal("0");	//원래 총 금액		
		BigDecimal   usageQuantity		  = new BigDecimal("0");	//사용자 사용량
		DateInterval timePeriod;									//검색간격
		BigDecimal 	 intervalAmount 	  = new BigDecimal("0");	//구간사용량
		BigDecimal 	 pricePerUnit		  = new BigDecimal("0");	//단위 가격
		BigDecimal 	 usageTypePrice		  = new BigDecimal("0");	//사용유형 구간별 검산 금액
		BigDecimal 	 originUsageTypePrice = new BigDecimal("0");	//원래 사용유형 금액	
		boolean 	 isConfirm;										//검산확인
		BigDecimal 	 beginRange			  = new BigDecimal("0");	//사용최소범위
		BigDecimal 	 endRange			  = null;					//사용최대범위
		String		 currencyCode;									//통화
		String 	 	 unit;											//유형단위					
		String 		 description;									//설명
		String 		 location;										//리전정보
		String 		 startDate;
		String 		 endDate;
		String		 servicecode;
		BigDecimal 	 usageTypePriceTotal  = new BigDecimal("0");	//사용유형 검산 금액
	 	BigDecimal 	 reduceAmount		  = new BigDecimal("0");	//프리티어 절감량
	 	
		//계산방식:유형별로 구간별 사용량*구간단위가격 의 총합을 소수점둘째자리까지 반올림하여 청구서 금액과 비교
	 	//위와 같은 계산식이 맞는지 판별 후 적용해야함
	 	for(ResultByTime resultByTime : evo.getResultByTimes()) {		//월별	 		
 			List<String> tempUsagePriceList = new ArrayList<String>(); 	//유형요금별 비교값 담을 리스트 생성(월별로 사용유형 묶음)
 			Map<String,BigDecimal> getFreeTierList = getFreeTierList(pvo);
 			timePeriod 	= resultByTime.getTimePeriod();
 			
 			calTotalPrice	= new BigDecimal("0");				//검산할 총합값 초기화
 			originTotalPrice= new BigDecimal("0");				//Explorer 총합값 초기화
 			intervalAmount	= new BigDecimal("0");				//구간사용량
 			for(Group group : resultByTime.getGroups()) {		//월별 사용유형값
 				String usageTypeName = group.getKeys().get(0);	//검산할 사용유형 이름
 				
 				usageTypePriceTotal  = new BigDecimal("0");		//검산할 사용유형 가격 초기화	 			
 				originUsageTypePrice = new BigDecimal(group.getMetrics().get("UnblendedCost").getAmount()); //Exploerer 사용유형별 요금
 				originUsageTypePrice = originUsageTypePrice.setScale(2, RoundingMode.HALF_UP); 				// 소수점반올림
 				
 				//Exploerer 총요금
 				originTotalPrice = originTotalPrice.add(originUsageTypePrice).setScale(2, RoundingMode.HALF_UP);//Explore 유형요금 가져오기	 		
 				
 				int pListIdxCnt = 0; //priceList 인덱스 카운트
 				for(AwsPriceListVO pvoList : pvo) {
 					String pvoUsageType = pvoList.getUsagetype();
 					//priceList 사용유형과 비교
 					if(pvoUsageType.equals(group.getKeys().get(0)) ) {
 						calResult = new CalResultVO();
 						
 						servicecode  = pvoList.getServicecode();
 						startDate	 = timePeriod.getStart();
 						endDate 	 = timePeriod.getEnd();
 						currencyCode = pvoList.getCurrencyCode();
 						unit 		 = pvoList.getUnit();
 						description  = pvoList.getDescription();
 						location	 = pvoList.getLocation();
 						
 						pricePerUnit = new BigDecimal(pvoList.getCurrencyRate());				//priceList Unit당 가격 담기	 					
 						beginRange   = new BigDecimal(pvoList.getBeginRange());				//priceList 최소범위	 					
 						usageQuantity= new BigDecimal(group.getMetrics().get("UsageQuantity").getAmount()); //사용자 사용량
 						endRange 	 = null;
 						//각 사용량 구간별 단위가격으로 나누어 합산한 가격을 usageTypePrice에 담는다.
 						//사용최소범위 <= 사용자 사용량
 						if(0 <= usageQuantity.compareTo(beginRange)){
 							
 							
 							if(!pvoList.getEndRange().equals("Inf")) { // 최대값 범위가 INF가 아닐때, 즉 최대값이 정해져 있을때
 								endRange   	= new BigDecimal(pvoList.getEndRange());//priceList 최대범위
 								//사용최소범위 <= 사용자 사용량 < 사용최대범위
 								if(-1 == usageQuantity.compareTo(endRange)) {
 									intervalAmount = usageQuantity.subtract(beginRange);//사용량 = 사용자 사용량 - 사용최소범위		 								
 								}else {	//사용최대범위 <= 사용자 사용량
 									intervalAmount = endRange.subtract(beginRange);		//사용량 = 사용최대범위 - 사용최소범위		 								
 								}
 							}else { //최대값 범위가 INF일때
 								intervalAmount  = usageQuantity.subtract(beginRange);
 							}
 							reduceAmount = FreeTierCalInfo.FreeTierApply(pvo,pvoUsageType,beginRange,endRange,intervalAmount,getFreeTierList); //프리티어 적용
 							
 						}else { //사용최소범위 > 사용자 사용량
 							intervalAmount	= BigDecimal.ZERO;
 							usageQuantity 	= BigDecimal.ZERO;
 							reduceAmount 	= BigDecimal.ZERO;
 							if(!("Inf").equals(pvoList.getEndRange()))
 								endRange = new BigDecimal(pvoList.getEndRange());
 						}
 						//구간량 < 프리티어절감량 => 0으로 치환
 						if(reduceAmount.compareTo(intervalAmount) >= 0) {
 							intervalAmount = BigDecimal.ZERO;
 						}else {
 							intervalAmount = intervalAmount.subtract(reduceAmount); // 구간사용량 = 구간사용량 - 프리티어절감량
 						}	
 						usageTypePrice 		= intervalAmount.multiply(pricePerUnit);	//사용유형 구간별 가격
 						usageTypePriceTotal = usageTypePriceTotal.add(usageTypePrice);
 						usageTypePriceTotal = usageTypePriceTotal.setScale(2, RoundingMode.HALF_UP);//사용유형 가격 += 단위당가격*사용량 
 						isConfirm = false;
 						if(originUsageTypePrice.equals(usageTypePriceTotal)) isConfirm = true;
 						
 						//VO에 담기
 						calResult.setServicecode(servicecode);			
 						calResult.setUsagetype(pvoUsageType);														
 						calResult.setUsageQuantity(usageQuantity.toString());		
 						calResult.setStartDate(startDate);				
 						calResult.setEndDate(endDate);
 						calResult.setIntervalAmount(intervalAmount.toString());    
 						calResult.setPricePerUnit(pricePerUnit.toString());
 						calResult.setUsageTypePrice(usageTypePrice.toString());
 						calResult.setUsageTypePriceTotal(usageTypePriceTotal.toString());
 						calResult.setOriginUsageTypePrice(originUsageTypePrice.toString());	
 						calResult.setIsConfirm(isConfirm+"");
 						calResult.setCurrencyCode(currencyCode);
 						calResult.setUnit(unit);
 						calResult.setDescription(description);     
 						calResult.setLocation(location);
 						calResult.setBeginRange(beginRange.toString());
 						if(endRange == null) 	calResult.setEndRange("Inf");	//구간 최대범위가 Inf일시 String Inf값 입력
 						else 					calResult.setEndRange(endRange.toString());
 						
 						calResultList.add(calResult);
 					}
	 			}	 			
	 			calTotalPrice = calTotalPrice.add(usageTypePriceTotal);// 검사총합에 사용유형별 값 추가	 			
	 			//사용유형별 검산 확인
	 			isConfirm = false;
		 		if(originUsageTypePrice.equals(usageTypePriceTotal)) isConfirm = true;		 		
		 		
		 		tempUsagePriceList.add("\t"+usageTypeName+"\t "+usageTypePriceTotal.toString()+"\t"+originUsageTypePrice.toString()+" \t" +isConfirm);
	 		}
	 		
	 		//월별 사용유형 검산값 담기
	 		monUsageTypePriceList.add(tempUsagePriceList);
	 		//월별 총합 검산 확인
	 		isConfirm = false;
	 		if(originTotalPrice.equals(calTotalPrice)) isConfirm = true;	
	 		monTotalPriceList.add(resultByTime.getTimePeriod() +"    "+calTotalPrice.toString()+"   \t"+originTotalPrice.toString()+" \t" +isConfirm);
	 	}
	 	calInfoPrint(calResultList);
		return calResultList;
	}
	
	public static void calInfoPrint(List<CalResultVO> calResultVO) {
		System.out.println("*************************************************************************************");	
 		System.out.println("[0]서비스코드 /[1]시작일 /[2]종료일 /[3]유형타입 /[4]사용량 /[5]구간사용량 /[6]단위가격 /[7]구간가격 /[8]청구서가격 /[9]검산확인 /[10]최소범위 /[11]최대범위 /[12]통화 /[13]유형단위 [14]리전정보 [15]설명 ");
 		System.out.println();
 		for(CalResultVO vo : calResultVO) {
 			System.out.println(vo.getServicecode()+"\t"+vo.getStartDate()+"\t"+vo.getEndDate()+"\t"+vo.getUsagetype()+"\t"
 							  +vo.getUsageQuantity()+"\t"+vo.getIntervalAmount()+"\t"+ vo.getPricePerUnit()+"\t"+ vo.getUsageTypePrice()+"\t"
 							  +vo.getOriginUsageTypePrice()+"\t"+vo.getIsConfirm()+"\t"+vo.getBeginRange()+"\t"+vo.getEndRange()+"\t"
 							  +vo.getCurrencyCode()+"\t"+vo.getUnit()+"\t"+vo.getLocation()+"\t"+vo.getDescription());
 			
 		}
 		System.out.println();
	}
	
	public static void calPrint() {
		int cnt = 0;
		for(String str : monTotalPriceList) {
			System.out.println("*****************************************************************");	
			System.out.println("서비스 이용일 \t\t\t       검산요금   원래요금 \t확인      ");
			System.out.println(str);
			System.out.println("*****************************************************************");
			System.out.println("\t사용유형 \t\t검산요금  원래요금    확인      ");		 		
			for(String strr : monUsageTypePriceList.get(cnt)) {		 			
				System.out.println(strr);
			}
			cnt++;
			System.out.println();
		}
	}
	
	//프리티어 리스트를 담는 클래스
	public static Map<String,BigDecimal> getFreeTierList(List<AwsPriceListVO> pvo) {
//			List<String> freeTierList = new ArrayList<String>();	//프리티어 담을 리스트 생성
		Map<String,BigDecimal> freeTierList = new HashMap<String, BigDecimal>();	
		
		for(AwsPriceListVO pvoList : pvo) {					//PriceList 검색
			String pvoUsageType = pvoList.getUsagetype();
			if(pvoUsageType.startsWith("Global-")) {
				//freeTierList.add(str);//"Global-"로 시작하는 유형타입 담기
				freeTierList.put(pvoUsageType, new BigDecimal(pvoList.getEndRange()));
			}
		}		
		
		return freeTierList;
	}
	
}
