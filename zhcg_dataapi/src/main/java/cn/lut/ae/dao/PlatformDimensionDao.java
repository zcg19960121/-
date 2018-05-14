package cn.lut.ae.dao;

import cn.lut.ae.model.PlatformDimension;

public interface PlatformDimensionDao {
    public PlatformDimension getPlatformDimension(PlatformDimension platform);

    public PlatformDimension getPlatformDimension(String platform);

    public PlatformDimension getPlatformDimension(int id);
}