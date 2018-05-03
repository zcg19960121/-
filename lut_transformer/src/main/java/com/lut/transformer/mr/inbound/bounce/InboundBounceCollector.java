package com.lut.transformer.mr.inbound.bounce;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.hadoop.conf.Configuration;

import com.lut.common.GlobalConstants;
import com.lut.transformer.model.dim.StatsInboundDimension;
import com.lut.transformer.model.dim.base.BaseDimension;
import com.lut.transformer.model.value.BaseStatsValueWritable;
import com.lut.transformer.model.value.reduce.InboundBounceReduceValue;
import com.lut.transformer.mr.IOutputCollector;
import com.lut.transformer.service.rpc.IDimensionConverter;

public class InboundBounceCollector implements IOutputCollector {

    @Override
    public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt, IDimensionConverter converter) throws SQLException, IOException {
        StatsInboundDimension inboundDimension = (StatsInboundDimension) key;
        InboundBounceReduceValue inboundReduceValue = (InboundBounceReduceValue) value;

        int i = 0;
        pstmt.setInt(++i, converter.getDimensionIdByValue(inboundDimension.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getDimensionIdByValue(inboundDimension.getStatsCommon().getDate()));
        pstmt.setInt(++i, inboundDimension.getInbound().getId()); // 直接设置，在mapper类中已经设置
        pstmt.setInt(++i, inboundReduceValue.getBounceNumber());
        pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
        pstmt.setInt(++i, inboundReduceValue.getBounceNumber());

        pstmt.addBatch();
    }

}
