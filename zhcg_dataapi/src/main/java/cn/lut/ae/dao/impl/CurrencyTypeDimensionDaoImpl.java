package cn.lut.ae.dao.impl;

import cn.lut.ae.dao.CurrencyTypeDimensionDao;
import cn.lut.ae.dao.mybatis.BaseDao;
import cn.lut.ae.model.CurrencyTypeDimension;

public class CurrencyTypeDimensionDaoImpl extends BaseDao implements CurrencyTypeDimensionDao {
    private static String modelClass = CurrencyTypeDimension.class.getName();
    private static String getCurrencyTypeDimension = modelClass + ".getCurrencyTypeDimension";

    @Override
    public CurrencyTypeDimension getCurrencyTypeDimension(CurrencyTypeDimension currencyTypeDimension) {
        return super.getSqlSession().selectOne(getCurrencyTypeDimension, currencyTypeDimension);
    }

    @Override
    public CurrencyTypeDimension getCurrencyTypeDimension(int id) {
        return this.getCurrencyTypeDimension(new CurrencyTypeDimension(id));
    }

    @Override
    public CurrencyTypeDimension getCurrencyTypeDimension(String currencyType) {
        return this.getCurrencyTypeDimension(new CurrencyTypeDimension(currencyType));
    }

}
