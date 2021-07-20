package io.tunecloud.potal.site.recalc.vo;

import java.util.List;

import lombok.Data;

@Data
public class CalResultVO extends AwsComDefaultVO{
	
	private List<String> usagetypes;
	
	private List<String> usageQuantitys;
	
	private List<String> startDates;
	
	private List<String> endDates;
	
	private List<String> intervalAmount;
	
	private List<String> pricePerUnits;
	
	private List<String> usageTypePrices;
	
	private List<String> originUsageTypePrices;
	
	private List<String> isConfirms;
	
	private List<String> usageTypePriceTotals;

}
