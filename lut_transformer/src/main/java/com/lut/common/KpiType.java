package com.lut.common;
/**
 * 统计kpi的名称枚举类
 * 
 * @author gg
 *
 */
public enum KpiType {
	NEW_INSTALL_USER("new_insatll_user"),
	BROWSER_NEW_INSTALL_USER("browser_new_insatll_user");
	public final String name;
	private KpiType(String name){
		this.name = name;
	}
}