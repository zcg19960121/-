package cn.lut.ae.service;

import java.util.List;
import java.util.Map;

import cn.lut.ae.model.BrowserDimension;
import cn.lut.ae.model.CurrencyTypeDimension;
import cn.lut.ae.model.EventDimension;
import cn.lut.ae.model.InboundDimension;
import cn.lut.ae.model.LocationDimension;
import cn.lut.ae.model.PaymentTypeDimension;
import cn.lut.ae.model.PlatformDimension;

public interface DimensionService {
    public List<Map<String, Object>> queryDimensionData(Map<String, String> queryMap);

    public PlatformDimension getPlatformDimension(int dimensionPlatformId);

    public PlatformDimension getPlatformDimension(String platformName);

    public Integer getKpiDimensionId(String kpiName);

    public Integer getDateDimensionId(int year, int season, int month, int week, int day);

    public BrowserDimension getBrowserDimension(int browserId);

    public BrowserDimension getBrowserDimension(String browser, String browser_version);

    public LocationDimension getLocationDimension(int dimensionLocationId);

    public LocationDimension getLocationDimension(String country, String province, String city);

    public InboundDimension getInboundDimension(int dimensionInboundId);

    public InboundDimension getInboundDimension(String name);

    public EventDimension getEventDimension(int dimensionEventId);

    public EventDimension getEventDimension(String category, String action);

    public CurrencyTypeDimension getCurrencyTypeDimension(int dimensionCurrencyTypeId);

    public CurrencyTypeDimension getCurrencyTypeDimension(String currencyType);

    public PaymentTypeDimension getPaymentTypeDimension(int dimensionPaymentTypeId);

    public PaymentTypeDimension getPaymentTypeDimension(String paymentType);
}