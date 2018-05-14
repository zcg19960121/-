package cn.lut.ae.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import cn.lut.ae.common.AEConstants;
import cn.lut.ae.dao.mybatis.AEDao;
import cn.lut.ae.model.QueryColumn;
import cn.lut.ae.model.QueryModel;
import cn.lut.ae.service.AEService;
import cn.lut.ae.util.TimeUtil;

public class UserBehaviorSpeedRateServiceImpl implements AEService {
    @Autowired
    private AEDao aeDao;

    public void setAeDao(AEDao aeDao) {
        this.aeDao = aeDao;
    }

    @Override
    public List<Map<String, Object>> execute(QueryModel queryModel) throws Exception {
        Set<String> groups = queryModel.getGroups();
        if (!CollectionUtils.isEmpty(groups)) {
            throw new Exception("不支持获取group用户变化速率");
        }
        if (!AEConstants.BUCKET_USER_BEHAVIOR.equals(queryModel.getBucket())) {
            throw new Exception("bucket必须为" + AEConstants.BUCKET_USER_BEHAVIOR);
        }

        Set<String> metrics = queryModel.getMetrics().keySet();
        List<Map<String, Object>> todayMetricData = null;
        List<Map<String, Object>> yesterdayMetricData = null;

        if (metrics.contains(AEConstants.METRIC_ACTIVE_USER_SPEED_RATE) // 活跃用户
                || metrics.contains(AEConstants.METRIC_NEW_USER_SPEED_RATE) // 新用户
                || metrics.contains(AEConstants.METRIC_TOTAL_USER_SPEED_RATE) // 总用户
                || metrics.contains(AEConstants.METRIC_ACTIVE_MEMBER_SPEED_RATE) // 活跃member
                || metrics.contains(AEConstants.METRIC_NEW_MEMBER_SPEED_RATE) // 新member
                || metrics.contains(AEConstants.METRIC_TOTAL_MEMBER_SPEED_RATE) // 总member
                ) {
            queryModel.setQueryId("cn.lut.ae.user.behavior.getUserStats");
            QueryColumn column = queryModel.getQueryColumn();
            column.setEndDate(column.getStartDate());
            todayMetricData = this.aeDao.fetchMetricData(queryModel);

            column.setStartDate(TimeUtil.getSpecifiedDate(column.getStartDate(), -1));
            column.setEndDate(column.getStartDate());
            yesterdayMetricData = this.aeDao.fetchMetricData(queryModel);
        }

        List<Map<String, Object>> returnValues = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        for (String metric : metrics) {
            switch (metric) {
            case AEConstants.METRIC_ACTIVE_USER_SPEED_RATE:
                // 处理活跃访客
                resultMap.put(AEConstants.PRIFIX + metric, processSpeedRate(todayMetricData, yesterdayMetricData, AEConstants.ACTIVE_USERS));
                break;
            case AEConstants.METRIC_NEW_USER_SPEED_RATE:
                // 处理新访客
                resultMap.put(AEConstants.PRIFIX + metric, processSpeedRate(todayMetricData, yesterdayMetricData, AEConstants.NEW_USERS));
                break;
            case AEConstants.METRIC_TOTAL_USER_SPEED_RATE:
                // 处理总访客
                resultMap.put(AEConstants.PRIFIX + metric, processSpeedRate(todayMetricData, yesterdayMetricData, AEConstants.TOTAL_USERS));
                break;
            case AEConstants.METRIC_TOTAL_MEMBER_SPEED_RATE:
                // 处理总会员
                resultMap.put(AEConstants.PRIFIX + metric, processSpeedRate(todayMetricData, yesterdayMetricData, AEConstants.TOTAL_MEMBERS));
                break;
            case AEConstants.METRIC_NEW_MEMBER_SPEED_RATE:
                // 处理新会员
                resultMap.put(AEConstants.PRIFIX + metric, processSpeedRate(todayMetricData, yesterdayMetricData, AEConstants.NEW_MEMBERS));
                break;
            case AEConstants.METRIC_ACTIVE_MEMBER_SPEED_RATE:
                // 处理活跃会员
                resultMap.put(AEConstants.PRIFIX + metric, processSpeedRate(todayMetricData, yesterdayMetricData, AEConstants.ACTIVE_MEMBERS));
                break;
            default:
                throw new Exception(AEConstants.BUCKET_USER_BEHAVIOR + "bucket下不支持该metric" + metric);
            }
        }
        returnValues.add(resultMap);
        return returnValues;
    }

    private Object processSpeedRate(List<Map<String, Object>> todayMetricData, List<Map<String, Object>> yesterdayMetricData, String key) {
        BigDecimal value1 = null;
        BigDecimal value2 = null;
        if (CollectionUtils.isEmpty(todayMetricData) || CollectionUtils.isEmpty(todayMetricData.get(0)) || !todayMetricData.get(0).containsKey(key)) {
            value1 = null;
        } else {
            String s = todayMetricData.get(0).get(key).toString();
            if (!"0".equals(s)) {
                value1 = BigDecimal.valueOf(Long.valueOf(s));
            }
        }
        if (CollectionUtils.isEmpty(yesterdayMetricData) || CollectionUtils.isEmpty(yesterdayMetricData.get(0)) || !yesterdayMetricData.get(0).containsKey(key)) {
            value2 = null;
        } else {
            String s = yesterdayMetricData.get(0).get(key).toString();
            if (!"0".equals(s)) {
                value2 = BigDecimal.valueOf(Long.valueOf(s));
            }
        }

        if (value1 == null && value2 == null) {
            return 0;
        } else if (value1 == null) {
            return -1;
        } else if (value2 == null) {
            return 1;
        }

        if (value1.compareTo(value2) == 0) {
            return 0;
        }

        return value1.subtract(value2).divide(value2, 4, BigDecimal.ROUND_HALF_UP);
    }
}
