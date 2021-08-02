package io.tunecloud.potal.site.rinsp.vo;

import lombok.Data;

@Data
public class CustomVO {
	String serviceCode;
	String usagetype;
	String startDate;
	String endDate;
	String usageQuantityTotal;
	String usageQuantitySection;
	String pricePerUnit;
	String inspAmount;
	String unblendedCost;
	String isInsp;
	String beginRange;
	String endRange;
	String unit;
	String location;
	
	String intervalAmount;
	String currencyCodes;
	
	String description;
}


