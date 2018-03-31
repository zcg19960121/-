package com.lut.transformer.service;

import java.io.IOException;

import com.lut.transformer.model.dim.base.BaseDimension;

/**
 * 提供专门操作dimension表的接口
 * 
 * @author gg
 *
 */
public interface IDimensionConverter {
	 /**
     * 根据dimension的value值获取id<br/>
     * 如果数据库中有，那么直接返回。如果没有，那么进行插入后返回新的id值
     * 
     * @param dimension
     * @return
     * @throws IOException
     */
	public int getDimensionIdByValue(BaseDimension dimension) throws IOException;
}
