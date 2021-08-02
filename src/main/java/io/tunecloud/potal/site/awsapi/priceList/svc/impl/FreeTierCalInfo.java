package io.tunecloud.potal.site.awsapi.priceList.svc.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.tunecloud.potal.site.awsapi.priceList.vo.AwsPriceListVO;

public class FreeTierCalInfo {
//	<Trackable AWS Free Tier services>
//	https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/tracking-free-tier-usage.html
//	*Amazon Simple Storage Service
//	Global-Requests-Tier1		
//	Global-Requests-Tier2
//	Global-TimedStorage-ByteHrs
//	
//	*Amazon API Gateway
//	Global-ApiGatewayRequest
//	
//	*Amazon CloudFront	
//	Global-DataTransfer-Out-Bytes
//	Global-Requests-Tier1
//		
//	*Amazon CloudWatch	
//	Global-CW:Requests
//	Global-DataProcessing-Bytes
//	Global-TimedStorage-ByteHrs
//		
//	*Amazon Elastic Compute Cloud	
//	Global-BoxUsage:freetier.micro
//	Global-BoxUsage:freetier.micro
//	Global-DataProcessing-Bytes
//	Global-EBS:SnapshotUsage
//	Global-EBS:VolumeIOUsage
//	Global-EBS:VolumeUsage
//	Global-LCUUsage
//	Global-LoadBalancerUsage
//		
//	*Amazon Simple Email Service	
//	Global-Message
//	Global-Recipients-EC2
//		
//	*Amazon Simple Queue Service
//	Global-Requests
//		
//	*AWSDataTransfer	
//	Global-DataTransfer-Out-Bytes
	
	//프리티어로 인해 절감될 interval Amount 계산	(endRange != Inf)
	public static BigDecimal FreeTierApply(List<AwsPriceListVO> pvo, String usagetype, BigDecimal beginRange, BigDecimal endRange, BigDecimal intervalAmount,Map<String,BigDecimal> getFreeTierList) {
		Map<String,BigDecimal> freeTierList = getFreeTierList;
		
//		BigDecimal freeBeginRange			= new BigDecimal("0");	//프리티어 사용최소범위는 0으로 전제함
		BigDecimal freeEndRange				= new BigDecimal("0");	//프리티어 사용최대범위
		BigDecimal reduceAmount				= new BigDecimal("0");  //프리티어로 인한 절감량
		
		String cmp1; 	 											//프리티어 usageType를 담을 변수 선언
		String cmp2= ""; 											//PriceList usageType를 담을 변수 선언
		if(usagetype.indexOf("-") >= 0) {
			cmp2 = usagetype.substring(usagetype.indexOf("-"));		//비교할 usageType -> 리전정보 제거
		}else {
			cmp2 = ("-").concat(usagetype);							//리전정보가 없는 경우 -를 붙여 형태 맞춤
		}
				
		for(String freeTier : freeTierList.keySet()) {					//프리티어 값을 str에 담아 반복문을 돌린다.
			cmp1 = freeTier.substring(freeTier.indexOf("-")); 				// 프리티어의 유형타입정보만 남긴다.
			//프리티어 검색
			if(cmp2.equals(cmp1)) {									//유형타입이 맞을 경우
				if(freeTierList.get(freeTier).equals(BigDecimal.ZERO)) return BigDecimal.ZERO; //프리티어 적용이 이미 되어 있을 경우 0 반환
										//PriceList의 인덱스 검색을 위한 변수
				for(AwsPriceListVO pvoList : pvo) {			//PriceList에서 프리티어의 구간정보를 찾아내기 위한 반복문 실행
					String pvoUsageType = pvoList.getUsagetype();
					if(freeTier.equals(pvoUsageType)) {							//프리티어 유형타입이 PriceList의 유형타입과 일치할 경우
						if(("Inf").equals(pvoList.getEndRange())){//프리티어가 Inf일 경우
							reduceAmount = intervalAmount;			//프리티어로 인한 감소량에 구간량을 대입하여 구간 사용량을 0으로 만들어 리턴
							return reduceAmount;
						}
						freeEndRange =freeTierList.get(freeTier);		//프리티어 최대값 
						if(null != endRange) {	//endRange 가 "Inf"가 아닐때
							if(-1 == freeEndRange.compareTo(endRange)){//프리티어 최대범위 < 사용최대범위
								if(freeEndRange.compareTo(beginRange) > 0) {
									reduceAmount = freeEndRange.subtract(beginRange);	//프리티어로 인한 감소량 = 프리티어최대범위 - 최소사용범위
									if(reduceAmount.compareTo(intervalAmount) > 0) { 
										reduceAmount = intervalAmount; // 만약 프리티어 감소량이 구간량보다 클 경우 구간량만큼만 프리티어 적용
										freeTierList.put(freeTier, freeEndRange.subtract(reduceAmount));		//프리티어 적용량을 프리티어 최댓값에서 뺸다.
									}else {
										freeTierList.put(freeTier, BigDecimal.ZERO);	
									}
								}
								return reduceAmount;
							}
							else {//사용최대범위 <= 프리티어 최대범위							
								reduceAmount = endRange.subtract(beginRange);		//프리티어로 인한 감소량 = 최대사용범위 - 최소사용범위
								if(reduceAmount.compareTo(intervalAmount) > 0) {
									reduceAmount = intervalAmount; // 만약 프리티어 감소량이 구간량보다 클 경우 구간량만큼만 프리티어 적용								
									freeTierList.put(freeTier, freeEndRange.subtract(reduceAmount));		//프리티어 적용량을 프리티어 최댓값에서 뺸다.
								}else {
									freeTierList.put(freeTier, BigDecimal.ZERO);	
								}
								return reduceAmount;
							}
						}else {	//endRange가 "Inf"일때
																		
								reduceAmount = freeEndRange.subtract(beginRange);	//프리티어가 Inf가 아닐 경우 프리티어로인한 감소량 = 프리티어 최대범위 - 최소범위
								if(reduceAmount.compareTo(intervalAmount) > 0) {
									reduceAmount = intervalAmount; // 만약 프리티어 감소량이 구간량보다 클 경우 구간량만큼만 프리티어 적용								
									freeTierList.put(freeTier, freeEndRange.subtract(reduceAmount));		//프리티어 적용량을 프리티어 최댓값에서 뺸다.
								}else {
									freeTierList.put(freeTier, BigDecimal.ZERO);	
								}
								return reduceAmount;
							}
						}
					}
					
				}
			}
		return reduceAmount;
	}
}
