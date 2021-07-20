package io.tunecloud.potal.site.recalc.vo;

import lombok.Data;

@Data
public class CustomVO {
	String serviceCode;
	String usagetype;
	String intervalAmount;
	String pricePerUnit;
	String unblendedCost;
	String beginRange;
	String endRange;
	String currencyCodes;
	String unit;
	String location;
}
