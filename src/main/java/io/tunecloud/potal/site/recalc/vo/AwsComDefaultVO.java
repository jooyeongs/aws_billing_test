package io.tunecloud.potal.site.recalc.vo;

import java.util.List;

import lombok.Getter;

@Getter
public class AwsComDefaultVO {
	
	//Common Product
	private List<String> servicecodes;
	private List<String> usagetypes;
	private List<String> servicenames;

	//Common Terms
	private List<String> units;
	private List<String> beginRanges;
	private List<String> endRanges;
	private List<String> currencyCodes;
	private List<String> currencyRates;
	private List<String> descriptions;
	
	private List<String> locations;
	
	public void setServicecodes(List<String> servicecodes) {
		this.servicecodes = servicecodes;
	}	
	public void setUsagetypes(List<String> usagetypes) {
		this.usagetypes = usagetypes;
	}	
	public void setServicenames(List<String> servicenames) {
		this.servicenames = servicenames;
	}
	public void setUnits(List<String> units) {
		this.units = units;
	}
	public void setBeginRanges(List<String> beginRanges) {
		this.beginRanges = beginRanges;
	}
	public void setEndRanges(List<String> endRanges) {
		this.endRanges = endRanges;
	}
	public void setCurrencyCodes(List<String> currencyCodes) {
		this.currencyCodes = currencyCodes;
	}
	public void setCurrencyRates(List<String> currencyRates) {
		this.currencyRates = currencyRates;
	}
	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}
	public void setLocations(List<String> locations) {
		this.locations = locations;
	}
}
