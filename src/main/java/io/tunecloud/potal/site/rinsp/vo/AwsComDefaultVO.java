package io.tunecloud.potal.site.rinsp.vo;

import java.util.List;

import lombok.Data;


@Data
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
	
}
