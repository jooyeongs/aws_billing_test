/**
 * 
 */
package io.tunecloud.portal.site.recalc.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import io.tunecloud.portal.site.recalc.vo.FilterVO;

/**
 * <pre>
 * io.tunecloud.portal.site.recalc.web
 *   |_ recalc.java
 * </pre>
 * 
 * @Company : 
 * @Author  : JuYoung2
 * @Date    : 2021. 7. 9 오후 5:32:55
 * @Version : 1.0
 * @Desc    : 
 * @History :
 *            이름     :     일자             :    근거자료          : 변경내용
 *           ------------------------------------------------------
 *         JuYoung2  : 2021. 7. 9       :                : 신규 개발.
 * 
 */
@Controller
@RequestMapping("/cost")
public class recalc {
	@RequestMapping("/recalc")
	public String recalc(@ModelAttribute("filterVO") FilterVO FilterVO
						, Model model, HttpServletRequest request) throws Exception {
		return "recalc";
		
	}
}
