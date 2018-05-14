package cn.lut.ae.dao.impl;

import cn.lut.ae.dao.PaymentTypeDimensionDao;
import cn.lut.ae.dao.mybatis.BaseDao;
import cn.lut.ae.model.PaymentTypeDimension;

public class PaymentTypeDimensionDaoImpl extends BaseDao implements PaymentTypeDimensionDao {
    private static String modelClass = PaymentTypeDimension.class.getName();
    private static String getPaymentTypeDimension = modelClass + ".getPaymentTypeDimension";

    @Override
    public PaymentTypeDimension getPaymentTypeDimension(PaymentTypeDimension paymentTypeDimension) {
        return super.getSqlSession().selectOne(getPaymentTypeDimension, paymentTypeDimension);
    }

    @Override
    public PaymentTypeDimension getPaymentTypeDimension(int id) {
        return this.getPaymentTypeDimension(new PaymentTypeDimension(id));
    }

    @Override
    public PaymentTypeDimension getPaymentTypeDimension(String paymentType) {
        return this.getPaymentTypeDimension(new PaymentTypeDimension(paymentType));
    }

}
