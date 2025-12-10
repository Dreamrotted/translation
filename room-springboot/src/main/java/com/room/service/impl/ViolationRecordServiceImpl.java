package com.room.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.Blacklist;
import com.room.entity.pojo.ViolationRecord;
import com.room.mapper.BlacklistMapper;
import com.room.mapper.ViolationRecordMapper;
import com.room.service.ViolationRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ViolationRecordServiceImpl implements ViolationRecordService {

    @Resource
    private ViolationRecordMapper violationRecordMapper;

    @Resource
    private BlacklistMapper blacklistMapper;

    // 黑名单规则：月累计违规次数
    private static final int BLACKLIST_THRESHOLD = 3;
    // 黑名单天数
    private static final int BLACKLIST_DAYS = 7;

    @Override
    public PageInfo<ViolationRecord> page(Integer pageNum, Integer pageSize, Integer studentNumber, String violationType, Integer recordType, Integer status, String startDate, String endDate) {
        PageHelper.startPage(pageNum, pageSize);
        List<ViolationRecord> list = violationRecordMapper.list(studentNumber, violationType, recordType, status, startDate, endDate);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(ViolationRecord violationRecord) {
        violationRecord.setViolationTime(new Date());
        violationRecord.setRecordType(1); // 管理员手动
        violationRecord.setStatus(0); // 未处理
        violationRecordMapper.add(violationRecord);

        // 检查是否需要加入黑名单
        checkAndAddToBlacklist(violationRecord.getStudentId());
    }

    @Override
    public void update(ViolationRecord violationRecord) {
        violationRecordMapper.update(violationRecord);
    }

    @Override
    public void deleteById(Integer id) {
        violationRecordMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordViolation(Integer studentId, Integer reservationId, String violationType, String violationDesc) {
        ViolationRecord record = new ViolationRecord();
        record.setStudentId(studentId);
        record.setReservationId(reservationId);
        record.setViolationType(violationType);
        record.setViolationDesc(violationDesc);
        record.setViolationTime(new Date());
        record.setRecordType(0); // 系统自动
        record.setStatus(0); // 未处理
        violationRecordMapper.add(record);

        // 检查是否需要加入黑名单
        checkAndAddToBlacklist(studentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndAddToBlacklist(Integer studentId) {
        // 统计本月违规次数
        int count = violationRecordMapper.countMonthViolations(studentId);

        // 如果达到阈值，自动加入黑名单
        if (count >= BLACKLIST_THRESHOLD) {
            // 检查是否已经在黑名单中
            Blacklist existingBlacklist = blacklistMapper.getActiveBlacklist(studentId);
            if (existingBlacklist == null) {
                // 加入黑名单
                Blacklist blacklist = new Blacklist();
                blacklist.setStudentId(studentId);
                blacklist.setReason("本月累计违规" + count + "次，自动加入黑名单");
                blacklist.setStartTime(new Date());

                // 计算结束时间（7天后）
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, BLACKLIST_DAYS);
                blacklist.setEndTime(calendar.getTime());

                blacklist.setAddType(0); // 系统自动
                blacklist.setStatus(1); // 生效中
                blacklistMapper.add(blacklist);
            }
        }
    }

    @Override
    public int getMonthViolationCount(Integer studentId) {
        return violationRecordMapper.countMonthViolations(studentId);
    }
}
