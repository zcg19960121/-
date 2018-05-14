package cn.lut.ae.service.impl;

import java.util.List;
import java.util.Map;

import cn.lut.ae.dao.BrowserDimensionDao;
import cn.lut.ae.dao.CurrencyTypeDimensionDao;
import cn.lut.ae.dao.DateDimensionDao;
import cn.lut.ae.dao.DimensionDao;
import cn.lut.ae.dao.EventDimensionDao;
import cn.lut.ae.dao.InboundDimensionDao;
import cn.lut.ae.dao.KpiDimensionDao;
import cn.lut.ae.dao.LocationDimensionDao;
import cn.lut.ae.dao.PaymentTypeDimensionDao;
import cn.lut.ae.dao.PlatformDimensionDao;
import cn.lut.ae.model.BrowserDimension;
import cn.lut.ae.model.CurrencyTypeDimension;
import cn.lut.ae.model.EventDimension;
import cn.lut.ae.model.InboundDimension;
import cn.lut.ae.model.KpiDimension;
import cn.lut.ae.model.LocationDimension;
import cn.lut.ae.model.PaymentTypeDimension;
import cn.lut.ae.model.PlatformDimension;
import cn.lut.ae.service.DimensionService;

public class DimensionServiceImpl implements DimensionService {
    private DimensionDao dimeDao;
    private DateDimensionDao dateDimeDao;
    private PlatformDimensionDao platformDimeDao;
    private KpiDimensionDao kpiDimeDao;
    private BrowserDimensionDao browserDimeDao;
    private LocationDimensionDao locationDimeDao;
    private InboundDimensionDao inboundDimeDao;
    private EventDimensionDao eventDimeDao;
    private CurrencyTypeDimensionDao currencyTypeDimeDao;
    private PaymentTypeDimensionDao paymentTypeDimeDao;

    public DimensionDao getDimeDao() {
        return dimeDao;
    }

    public void setDimeDao(DimensionDao dimeDao) {
        this.dimeDao = dimeDao;
    }

    public DateDimensionDao getDateDimeDao() {
        return dateDimeDao;
    }

    public void setDateDimeDao(DateDimensionDao dateDimeDao) {
        this.dateDimeDao = dateDimeDao;
    }

    public PlatformDimensionDao getPlatformDimeDao() {
        return platformDimeDao;
    }

    public void setPlatformDimeDao(PlatformDimensionDao platformDimeDao) {
        this.platformDimeDao = platformDimeDao;
    }

    public KpiDimensionDao getKpiDimeDao() {
        return kpiDimeDao;
    }

    public void setKpiDimeDao(KpiDimensionDao kpiDimeDao) {
        this.kpiDimeDao = kpiDimeDao;
    }

    public BrowserDimensionDao getBrowserDimeDao() {
        return browserDimeDao;
    }

    public void setBrowserDimeDao(BrowserDimensionDao browserDimeDao) {
        this.browserDimeDao = browserDimeDao;
    }

    public LocationDimensionDao getLocationDimeDao() {
        return locationDimeDao;
    }

    public void setLocationDimeDao(LocationDimensionDao locationDimeDao) {
        this.locationDimeDao = locationDimeDao;
    }

    public InboundDimensionDao getInboundDimeDao() {
        return inboundDimeDao;
    }

    public void setInboundDimeDao(InboundDimensionDao inboundDimeDao) {
        this.inboundDimeDao = inboundDimeDao;
    }

    public EventDimensionDao getEventDimeDao() {
        return eventDimeDao;
    }

    public void setEventDimeDao(EventDimensionDao eventDimeDao) {
        this.eventDimeDao = eventDimeDao;
    }

    public CurrencyTypeDimensionDao getCurrencyTypeDimeDao() {
        return currencyTypeDimeDao;
    }

    public void setCurrencyTypeDimeDao(CurrencyTypeDimensionDao currencyTypeDimeDao) {
        this.currencyTypeDimeDao = currencyTypeDimeDao;
    }

    public PaymentTypeDimensionDao getPaymentTypeDimeDao() {
        return paymentTypeDimeDao;
    }

    public void setPaymentTypeDimeDao(PaymentTypeDimensionDao paymentTypeDimeDao) {
        this.paymentTypeDimeDao = paymentTypeDimeDao;
    }

    @Override
    public List<Map<String, Object>> queryDimensionData(final Map<String, String> queryMap) {
        return this.dimeDao.queryDimensionData(queryMap);
    }

    @Override
    public PlatformDimension getPlatformDimension(final int dimensionPlatformId) {
        return this.platformDimeDao.getPlatformDimension(dimensionPlatformId);
    }

    @Override
    public PlatformDimension getPlatformDimension(final String platformName) {
        return this.platformDimeDao.getPlatformDimension(platformName);
    }

    @Override
    public Integer getDateDimensionId(final int year, final int season, final int month, final int week, final int day) {
        return this.dateDimeDao.getDateDimensionId(year, season, month, week, day);
    }

    @Override
    public Integer getKpiDimensionId(final String kpiName) {
        KpiDimension dimension = this.kpiDimeDao.getKpiDimension(kpiName);
        if (dimension != null) {
            return dimension.getId();
        }
        return null;
    }

    @Override
    public BrowserDimension getBrowserDimension(int browserId) {
        return this.browserDimeDao.getBrowserDimension(browserId);
    }

    @Override
    public BrowserDimension getBrowserDimension(String browser, String browser_version) {
        return this.browserDimeDao.getBrowserDimension(browser, browser_version);
    }

    @Override
    public LocationDimension getLocationDimension(int dimensionLocationId) {
        return this.locationDimeDao.getLocationDimension(dimensionLocationId);
    }

    @Override
    public LocationDimension getLocationDimension(String country, String province, String city) {
        return this.locationDimeDao.getLocationDimension(country, province, city);
    }

    @Override
    public InboundDimension getInboundDimension(int dimensionInboundId) {
        return this.inboundDimeDao.getInboundDimension(dimensionInboundId);
    }

    @Override
    public InboundDimension getInboundDimension(String name) {
        return this.inboundDimeDao.getInboundDimension(name);
    }

    @Override
    public EventDimension getEventDimension(int dimensionEventId) {
        return this.eventDimeDao.getEventDimension(dimensionEventId);
    }

    @Override
    public EventDimension getEventDimension(String category, String action) {
        return this.eventDimeDao.getEventDimension(category, action);
    }

    @Override
    public CurrencyTypeDimension getCurrencyTypeDimension(int dimensionCurrencyTypeId) {
        return this.currencyTypeDimeDao.getCurrencyTypeDimension(dimensionCurrencyTypeId);
    }

    @Override
    public CurrencyTypeDimension getCurrencyTypeDimension(String currencyType) {
        return this.currencyTypeDimeDao.getCurrencyTypeDimension(currencyType);
    }

    @Override
    public PaymentTypeDimension getPaymentTypeDimension(int dimensionPaymentTypeId) {
        return this.paymentTypeDimeDao.getPaymentTypeDimension(dimensionPaymentTypeId);
    }

    @Override
    public PaymentTypeDimension getPaymentTypeDimension(String paymentType) {
        return this.paymentTypeDimeDao.getPaymentTypeDimension(paymentType);
    }
}