package io.tunecloud.potal.site.recalc.vo;

import java.util.List;

import com.amazonaws.services.costexplorer.model.ResultByTime;

import lombok.Getter;

@Getter
public class ExplorerListVO {
	
	List<ResultByTime> resultByTimes;
	
	public void setResultByTimes(List<ResultByTime> resultByTimes) {
		this.resultByTimes = resultByTimes;
	}
}
