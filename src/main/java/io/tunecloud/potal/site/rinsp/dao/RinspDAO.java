/**
 * 
 */
package io.tunecloud.potal.site.rinsp.dao;

import org.apache.ibatis.annotations.Mapper;

import io.tunecloud.potal.site.rinsp.vo.FilterVO;

/**
 * <pre>
 * io.tunecloud.potal.site.recalc.dao
 *   |_ RecalcDAO.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 19 오후 3:23:06
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 27       :                : 신규 개발.
 * 
 */
@Mapper
public interface RinspDAO {

	FilterVO selectProjectKey(FilterVO filterVO) throws Exception;

}
