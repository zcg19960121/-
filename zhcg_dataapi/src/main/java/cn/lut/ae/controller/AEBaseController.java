package cn.lut.ae.controller;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lut.ae.common.AEConstants;
import cn.lut.ae.model.BrowserDimension;
import cn.lut.ae.model.CurrencyTypeDimension;
import cn.lut.ae.model.EventDimension;
import cn.lut.ae.model.InboundDimension;
import cn.lut.ae.model.LocationDimension;
import cn.lut.ae.model.Message;
import cn.lut.ae.model.PaymentTypeDimension;
import cn.lut.ae.model.Message.MessageEntry;
import cn.lut.ae.model.PlatformDimension;
import cn.lut.ae.model.QueryColumn;
import cn.lut.ae.service.AEService;
import cn.lut.ae.service.DimensionService;

/**
 * 基础controller类
 * 
 * @author gg
 *
 */
public abstract class AEBaseController {
    private static final Logger log = Logger.getLogger(AEBaseController.class);
    // bucket和metric的映射关系
    @Resource
    protected Map<String, Set<Object>> bucketMetrics;
    // bucket支持group by的列名
    @Resource
    protected Map<String, Set<Object>> groupByColumns;
    // bucket&metric和queryid的映射关系
    @Resource
    protected Map<String, String> bucketMetricQueryId;
    // bucket&metric和返回列的映射关系
    @Resource
    protected Map<String, String> bucketMetricColumns;

    @Resource(name = "aeService")
    protected AEService aeService;

    @Resource(name = "dimensionService")
    protected DimensionService dimensionService;

    /**
     * 全局的处理异常方法
     * 
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public MessageEntry handleThrowable(Throwable e) {
        log.error("服务器发现异常", e);
        return Message.error(e.getMessage());
    }

    /**
     * 处理platform相关参数
     * 
     * @param request
     * @param groups
     * @param queryColumn
     * @return
     */
    protected boolean setDimensionPlatformId(HttpServletRequest request, Set<String> groups, QueryColumn queryColumn) {
        String platformId = request.getParameter(AEConstants.DIMENSION_PLATFORM_ID);
        int dimensionId = 0;
        try {
            dimensionId = StringUtils.isBlank(platformId) ? 0 : Integer.valueOf(platformId.trim());
        } catch (Throwable e) {
            dimensionId = 0;
        }

        PlatformDimension dimension = null;
        if (dimensionId != 0) {
            // 从数据库中获取
            dimension = this.dimensionService.getPlatformDimension(dimensionId);
        }

        // 如果获取的值为空，那么读取platform参数，进行解析操作
        if (dimension == null) {
            String platform = request.getParameter(AEConstants.PLATFORM);
            // 判断group by操作
            platform = this.checkGroupByColumn(platform, AEConstants.PLATFORM, groups);
            if (AEConstants.GROUP_BY.equals(platform)) {
                dimensionId = 0;
            } else {
                dimension = this.dimensionService.getPlatformDimension(platform);
                if (null == dimension) {
                    return false;
                }
                dimensionId = dimension.getId();
            }
        }

        queryColumn.setDimensionPlatformId(dimensionId);
        return true;
    }

    /**
     * 设置browser相关id
     * 
     * @param request
     * @param groups
     * @param queryColumn
     * @return
     */
    protected boolean setDimensionBrowserId(HttpServletRequest request, Set<String> groups, QueryColumn queryColumn) {
        String browser_id = request.getParameter(AEConstants.DIMENSION_BROWSER_ID);
        int dimensionBrowserId = 0;
        try {
            dimensionBrowserId = StringUtils.isBlank(browser_id) ? 0 : Integer.parseInt(browser_id.trim());
        } catch (Exception e) {
            // nothing
            dimensionBrowserId = 0;
        }
        BrowserDimension browserDimension = null;
        if (0 != dimensionBrowserId) {
            browserDimension = dimensionService.getBrowserDimension(dimensionBrowserId);
        }

        String browserName = null;
        if (null == browserDimension) {
            browserName = request.getParameter(AEConstants.BROWSER);
            browserName = checkGroupByColumn(browserName, AEConstants.BROWSER, groups);
            if (AEConstants.GROUP_BY.equals(browserName)) {
                dimensionBrowserId = 0;
            } else if (AEConstants.ALL.equals(browserName)) {
                dimensionBrowserId = 0;
            } else {
                String browserVersion = request.getParameter(AEConstants.BROWSER_VERSION);
                browserVersion = checkGroupByColumn(browserVersion, AEConstants.BROWSER_VERSION, groups);
                if (AEConstants.GROUP_BY.equals(browserVersion)) {
                    // 进行group by version，而且此时的name值确定，那么设置browser值
                    dimensionBrowserId = 0;
                } else {
                    // 指定version或者不进行group by version操作
                    browserDimension = dimensionService.getBrowserDimension(browserName, browserVersion);
                    if (browserDimension == null) {
                        return false;
                    }
                    dimensionBrowserId = browserDimension.getId();
                }
            }
        }
        queryColumn.setDimensionBrowserId(dimensionBrowserId);
        queryColumn.setBrowser(browserName);
        return true;
    }

    /**
     * 设置location相关参数
     * 
     * @param request
     * @param groups
     * @param queryColumn
     * @return
     */
    protected boolean setDimensionLocationId(HttpServletRequest request, Set<String> groups, QueryColumn queryColumn) {
        String locationId = request.getParameter(AEConstants.DIMENSION_LOCATION_ID);
        int dimensionLocationId = 0;
        try {
            dimensionLocationId = StringUtils.isBlank(locationId) ? 0 : Integer.parseInt(locationId.trim());
        } catch (Exception e) {
            return false;
        }

        LocationDimension locationDimension = null;
        if (0 != dimensionLocationId) {
            locationDimension = dimensionService.getLocationDimension(dimensionLocationId);
        }

        String country = AEConstants.ALL, province = AEConstants.ALL, city = AEConstants.ALL;
        if (null == locationDimension) {
            country = request.getParameter(AEConstants.LOCATION_COUNTRY);
            country = checkGroupByColumn(country, AEConstants.LOCATION_COUNTRY, groups);
            if (AEConstants.GROUP_BY.equals(country)) {
                dimensionLocationId = 0;
            } else {
                // 此时给定了country的值
                province = request.getParameter(AEConstants.LOCATION_PROVINCE);
                province = checkGroupByColumn(province, AEConstants.LOCATION_PROVINCE, groups);
                if (AEConstants.GROUP_BY.equals(province)) {
                    // 进行group by province，而且此时的name值确定，那么设置os值
                    dimensionLocationId = 0;
                } else {
                    // 给定country&province的值
                    city = request.getParameter(AEConstants.LOCATION_CITY);
                    city = checkGroupByColumn(city, AEConstants.LOCATION_CITY, groups);
                    if (AEConstants.GROUP_BY.equals(city)) {
                        dimensionLocationId = 0;
                    } else {
                        locationDimension = dimensionService.getLocationDimension(country, province, city);
                        if (locationDimension == null) {
                            return false;
                        }
                        dimensionLocationId = locationDimension.getId();
                    }
                }
            }
        }
        queryColumn.setDimensionLocationId(dimensionLocationId);
        queryColumn.setLocationCountry(country);
        queryColumn.setLocationProvince(province);
        queryColumn.setLocationCity(city);
        return true;
    }

    /**
     * 设置Inbound的参数值
     * 
     * @param request
     * @param groups
     * @param queryColumn
     * @return
     */
    protected boolean setDimensionInboundId(HttpServletRequest request, Set<String> groups, QueryColumn queryColumn) {
        String inboundId = request.getParameter(AEConstants.DIMENSION_INBOUND_ID);
        int dimensionInboundId = 0;
        try {
            dimensionInboundId = StringUtils.isBlank(inboundId) ? 0 : Integer.parseInt(inboundId.trim());
        } catch (Exception e) {
            dimensionInboundId = 0;
        }

        InboundDimension inboundDimension = null;
        if (0 != dimensionInboundId) {
            inboundDimension = dimensionService.getInboundDimension(dimensionInboundId);
        }

        String name = AEConstants.ALL;
        if (null == inboundDimension) {
            name = request.getParameter(AEConstants.INBOUND_NAME);
            name = checkGroupByColumn(name, AEConstants.INBOUND_NAME, groups);
            if (AEConstants.GROUP_BY.equals(name)) {
                dimensionInboundId = 0;
            } else {
                // name is all or user's value
                inboundDimension = dimensionService.getInboundDimension(name);
                if (inboundDimension == null) {
                    return false;
                }
                dimensionInboundId = inboundDimension.getId();
            }
        }
        queryColumn.setDimensionInboundId(dimensionInboundId);
        queryColumn.setInboundName(name);
        return true;
    }

    /**
     * 设置event的参数值
     * 
     * @param request
     * @param groups
     * @param queryColumn
     * @return
     */
    protected boolean setDimensionEventId(HttpServletRequest request, Set<String> groups, QueryColumn queryColumn) {
        String eventId = request.getParameter(AEConstants.DIMENSION_EVENT_ID);
        int dimensionEventId = 0;
        try {
            dimensionEventId = StringUtils.isBlank(eventId) ? 0 : Integer.parseInt(eventId.trim());
        } catch (Exception e) {
            // nothing
            dimensionEventId = 0;
        }
        
        EventDimension eventDimension = null;
        if (0 != dimensionEventId) {
            eventDimension = this.dimensionService.getEventDimension(dimensionEventId);
        }
        
        if (null == eventDimension) {
            String category = request.getParameter(AEConstants.EVENT_CATEGORY);
            category = this.checkGroupByColumn(category, AEConstants.EVENT_CATEGORY, groups);
            if (AEConstants.GROUP_BY.equals(category) || AEConstants.ALL.equals(category)) {
                dimensionEventId = 0;
            } else {
                String action = request.getParameter(AEConstants.EVENT_ACTION);
                action = this.checkGroupByColumn(action, AEConstants.EVENT_ACTION, groups);
                if (AEConstants.GROUP_BY.equals(action) || AEConstants.ALL.equals(action)) {
                    dimensionEventId = 0;
                } else {
                    eventDimension = this.dimensionService.getEventDimension(category, action);
                    if (eventDimension == null) {
                        return false;
                    }
                    dimensionEventId = eventDimension.getId();
                }
            }
        }
        queryColumn.setDimensionEventId(dimensionEventId);
        return true;
    }

    /**
     * 设置currency type的参数值
     * 
     * @param request
     * @param groups
     * @param queryColumn
     * @return
     */
    protected boolean setDimensionCurrencyTypeId(HttpServletRequest request, Set<String> groups, QueryColumn queryColumn) {
        String currencyTypeId = request.getParameter(AEConstants.DIMENSION_CURRENCY_TYPE_ID);
        int dimensionCurrencyTypeId = 0;
        try {
            dimensionCurrencyTypeId = StringUtils.isBlank(currencyTypeId) ? 0 : Integer.parseInt(currencyTypeId.trim());
        } catch (Exception e) {
            // nothing
            dimensionCurrencyTypeId = 0;
        }

        CurrencyTypeDimension currencyTypeDimension = null;
        if (0 != dimensionCurrencyTypeId) {
            currencyTypeDimension = this.dimensionService.getCurrencyTypeDimension(dimensionCurrencyTypeId);
        }
        
        if (null == currencyTypeDimension) {
            String currencyType = request.getParameter(AEConstants.CURRENCY_TYPE);
            currencyType = this.checkGroupByColumn(currencyType, AEConstants.CURRENCY_TYPE, groups);
            if (AEConstants.GROUP_BY.equals(currencyType)){
                dimensionCurrencyTypeId = 0;
            } else {
                currencyTypeDimension = this.dimensionService.getCurrencyTypeDimension(currencyType);
                if (currencyTypeDimension == null) {
                    return false;
                }
                dimensionCurrencyTypeId = currencyTypeDimension.getId();
            }
        }
        queryColumn.setDimensionCurrencyTypeId(dimensionCurrencyTypeId);
        return true;
    }

    
    /**
     * 设置payment type的参数值
     * 
     * @param request
     * @param groups
     * @param queryColumn
     * @return
     */
    protected boolean setDimensionPaymentTypeId(HttpServletRequest request, Set<String> groups, QueryColumn queryColumn) {
        String paymentTypeId = request.getParameter(AEConstants.DIMENSION_PAYMENT_TYPE_ID);
        int dimensionPaymentTypeId = 0;
        try {
            dimensionPaymentTypeId = StringUtils.isBlank(paymentTypeId) ? 0 : Integer.parseInt(paymentTypeId.trim());
        } catch (Exception e) {
            // nothing
            dimensionPaymentTypeId = 0;
        }
        
        PaymentTypeDimension paymentTypeDimension = null;
        if (0 != dimensionPaymentTypeId) {
            paymentTypeDimension = this.dimensionService.getPaymentTypeDimension(dimensionPaymentTypeId);
        }
        
        if (null == paymentTypeDimension) {
            String paymentType = request.getParameter(AEConstants.PAYMENT_TYPE);
            paymentType = this.checkGroupByColumn(paymentType, AEConstants.PAYMENT_TYPE, groups);
            if (AEConstants.GROUP_BY.equals(paymentType)){
                dimensionPaymentTypeId = 0;
            } else {
                paymentTypeDimension = this.dimensionService.getPaymentTypeDimension(paymentType);
                if (paymentTypeDimension == null) {
                    return false;
                }
                dimensionPaymentTypeId = paymentTypeDimension.getId();
            }
        }
        queryColumn.setDimensionPaymentTypeId(dimensionPaymentTypeId);
        return true;
    }

    /**
     * 前提条件： column为空<br/>
     * 检查是否是group操作，即检查给定的groupBy参数是否在groups中出现，如果出现返回group_by。<br/>
     * 否则返回all<br/>
     * 如果column不为空，直接返回原值
     * 
     * @param column
     * @param groupBy
     * @param groups
     * @return
     */
    protected String checkGroupByColumn(String column, String groupBy, Set<String> groups) {
        if (StringUtils.isBlank(column)) {
            if (groups.isEmpty() || !groups.contains(groupBy)) {
                return AEConstants.ALL;
            }
            return AEConstants.GROUP_BY;
        }
        return column.trim();
    }
}
