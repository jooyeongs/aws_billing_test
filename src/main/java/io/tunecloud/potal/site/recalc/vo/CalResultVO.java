package io.tunecloud.potal.site.recalc.vo;

import java.util.List;

import lombok.Getter;

@Getter
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

	// Route53
	private List<String> routingTypes;
	private List<String> routingTargets;
	
	public void setUsageTypes(List<String> usagetypes) {
		this.usagetypes = usagetypes;
	}
	public void setUsageQuantitys(List<String> usageQuantitys) {
		this.usageQuantitys = usageQuantitys;
	}
	public void setStartDates(List<String> startDates) {
		this.startDates = startDates;
	}
	public void setEndDates(List<String> endDates) {
		this.endDates = endDates;
	}
	public void setIntervalAmount(List<String> intervalAmount) {
		this.intervalAmount = intervalAmount;
	}
	public void setPricePerUnits(List<String> pricePerUnits) {
		this.pricePerUnits = pricePerUnits;
	}
	public void setUsageTypePrices(List<String> usageTypePrices) {
		this.usageTypePrices = usageTypePrices;
	}
	public void setOriginUsageTypePrices(List<String> originUsageTypePrices) {
		this.originUsageTypePrices = originUsageTypePrices;
	}
	public void setIsConfirms(List<String> isConfirms) {
		this.isConfirms = isConfirms;
	}
	public void setRoutingTypes(List<String> routingTypes) {
		this.routingTypes = routingTypes;
	}
	public void setRoutingTargets(List<String> routingTargets) {
		this.routingTargets = routingTargets;
	}
	public void setUsageTypePriceTotals(List<String> usageTypePriceTotals) {
		this.usageTypePriceTotals = usageTypePriceTotals;
	}
}
