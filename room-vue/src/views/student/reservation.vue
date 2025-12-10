<!--学生预约记录-->
<template>
  <div class="container">
    <!--条件查询-->
    <div class="search-bar">
      <el-input v-model="query.roomNumber" placeholder="自习室编号" style="width: 150px;"
                @keyup.enter.native="getDataList"></el-input>
      <el-input v-model="query.seatNumber" placeholder="座位编号" style="width: 150px; margin-left: 10px;"
                @keyup.enter.native="getDataList"></el-input>
      <el-select v-model="query.status" placeholder="预约状态" style="width: 150px; margin-left: 10px;" clearable>
        <el-option label="已预约" value="已预约"></el-option>
        <el-option label="已取消" value="已取消"></el-option>
        <el-option label="已过期" value="已过期"></el-option>
        <el-option label="已完成" value="已完成"></el-option>
      </el-select>
      <el-date-picker
          v-model="query.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          style="width: 240px; margin-left: 10px;">
      </el-date-picker>
      <el-button type="primary" icon="el-icon-search" @click="getDataList" style="margin-left: 10px">搜索</el-button>
      <el-button icon="el-icon-refresh" @click="resetSearch" style="margin-left: 10px">重置</el-button>
    </div>

    <el-table :data="dataList"
              border
              stripe
              v-loading="dataListLoading"
              highlight-current-row
              align-content: center>
      <el-table-column type="index" min-width="80" header-align="center" align="center" label="序号"></el-table-column>
      <el-table-column prop="studyRooms.roomNumber" header-align="center" align="center" min-width="120" label="自习室编号">
      </el-table-column>
      <el-table-column prop="studyRooms.location" header-align="center" align="center" min-width="150" label="位置">
      </el-table-column>
      <el-table-column header-align="center" align="center" min-width="120" label="座位编号">
        <template v-slot="scope">
          <el-tag type="primary" v-if="scope.row.seat">
            {{ scope.row.seat.seatNumber }}
          </el-tag>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column header-align="center" align="center" min-width="100" label="座位类型">
        <template v-slot="scope">
          {{ scope.row.seat ? scope.row.seat.seatType : '--' }}
        </template>
      </el-table-column>
      <el-table-column header-align="center" align="center" min-width="120" label="签到时间">
        <template v-slot="scope">
          {{ scope.row.checkInTime || '--' }}
        </template>
      </el-table-column>
      <el-table-column header-align="center" align="center" min-width="120" label="签退时间">
        <template v-slot="scope">
          {{ scope.row.checkOutTime || '--' }}
        </template>
      </el-table-column>
      <el-table-column prop="startTime" header-align="center" align="center" min-width="150" label="开始时间">
      </el-table-column>
      <el-table-column prop="endTime" header-align="center" align="center" min-width="150" label="结束时间">
      </el-table-column>
      <el-table-column prop="status" header-align="center" align="center" min-width="100" label="状态">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === '已预约'" type="success">已预约</el-tag>
          <el-tag v-else-if="scope.row.status === '已取消'" type="info">已取消</el-tag>
          <el-tag v-else-if="scope.row.status === '已过期'" type="warning">已过期</el-tag>
          <el-tag v-else-if="scope.row.status === '已完成'" type="primary">已完成</el-tag>
          <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column fixed="right" header-align="center" align="center" width="250" label="操作">
        <template v-slot="scope">
          <!-- 签到按钮 -->
          <el-button 
            type="primary" 
            size="small" 
            @click="checkIn(scope.row)" 
            v-if="scope.row.status === '已预约' && scope.row.isCheckedIn === 0 && canCheckIn(scope.row)">
            签到
          </el-button>
          <!-- 签退按钮 -->
          <el-button 
            type="success" 
            size="small" 
            @click="checkOut(scope.row)" 
            v-if="scope.row.status === '已预约' && scope.row.isCheckedIn === 1 && scope.row.isCheckedOut === 0">
            签退
          </el-button>
          <!-- 取消预约按钮 -->
          <el-button 
            type="danger" 
            size="small" 
            @click="cancel(scope.row)" 
            v-if="scope.row.status === '已预约' && scope.row.isCheckedIn === 0 && canCancel(scope.row)">
            取消预约
          </el-button>
          <!-- 无法取消提示 -->
          <el-tooltip v-else-if="scope.row.status === '已预约' && scope.row.isCheckedIn === 0" content="预约开始前30分钟内不能取消" placement="top">
            <el-button type="info" size="small" disabled>无法取消</el-button>
          </el-tooltip>
          <span v-else>--</span>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        @size-change="sizeChangeHandle"
        @current-change="currentChangeHandle"
        :current-page="current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        :total="totalPage"
        layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
  </div>
</template>

<script>

import request from "@/utils/request";

export default {
  data() {
    return {
      dataList: [],
      current: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      query: {
        roomNumber: '',
        seatNumber: '',
        status: '',
        dateRange: null
      }
    };
  },
  methods: {
    getDataList() {
      this.dataListLoading = true;
      const params = {
        pageNum: this.current,
        pageSize: this.pageSize,
        studentNumber: JSON.parse(localStorage.getItem("user")).studentNumber,
      };
      
      // 添加查询条件
      if (this.query.roomNumber) {
        params.roomNumber = this.query.roomNumber;
      }
      if (this.query.seatNumber) {
        params.seatNumber = this.query.seatNumber;
      }
      if (this.query.status) {
        params.status = this.query.status;
      }
      if (this.query.dateRange && this.query.dateRange.length === 2) {
        params.startDate = this.query.dateRange[0];
        params.endDate = this.query.dateRange[1];
      }
      
      request.get('/reservation/page', { params }).then((resp) => {
        if (resp.code === 200) {
          this.dataList = resp.data.list;
          this.totalPage = resp.data.total;
          console.log('预约记录数据:', this.dataList); // 调试日志
        } else {
          this.dataList = [];
          this.$message.error(resp.message || '获取预约记录失败');
        }
        this.dataListLoading = false;
      }).catch(err => {
        console.error('获取预约记录错误:', err);
        this.dataList = [];
        this.dataListLoading = false;
        this.$message.error('获取预约记录失败，请稍后重试');
      });
    },
    resetSearch() {
      this.query = {
        roomNumber: '',
        seatNumber: '',
        status: '',
        dateRange: null
      };
      this.current = 1;
      this.getDataList();
    },
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.current = 1;
      this.getDataList();
    },
    currentChangeHandle(val) {
      this.current = val;
      this.getDataList();
    },
    // 判断是否可以签到
    canCheckIn(row) {
      const startTime = new Date(row.startTime);
      const now = new Date();
      // 预约开始后15分钟内可以签到
      const diffMinutes = (now.getTime() - startTime.getTime()) / (1000 * 60);
      return now >= startTime && diffMinutes <= 15;
    },
    // 判断是否可以取消
    canCancel(row) {
      const startTime = new Date(row.startTime);
      const now = new Date();
      const diffMinutes = (startTime.getTime() - now.getTime()) / (1000 * 60);
      return diffMinutes >= 30;
    },
    // 取消预约
    cancel(row) {
      this.$confirm('确认取消预约, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post('/reservation/cancel', {
          reservationId: row.id,
          studentId: JSON.parse(localStorage.getItem("user")).id
        }).then((resp) => {
          if (resp.code === 200) {
            this.$message.success('取消成功');
            this.getDataList();
          } else {
            this.$message.error(resp.message || '取消失败');
          }
        }).catch(err => {
          console.error('取消预约错误:', err);
          this.$message.error('取消失败，请稍后重试');
        });
      }).catch(() => {});
    },
    // 签到
    checkIn(row) {
      this.$confirm('确认签到, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        request.post('/reservation/checkIn', {
          reservationId: row.id,
          studentId: JSON.parse(localStorage.getItem("user")).id
        }).then((resp) => {
          if (resp.code === 200) {
            this.$message.success('签到成功');
            this.getDataList();
          } else {
            this.$message.error(resp.message || '签到失败');
          }
        }).catch(err => {
          console.error('签到错误:', err);
          this.$message.error('签到失败，请稍后重试');
        });
      }).catch(() => {});
    },
    // 签退
    checkOut(row) {
      this.$confirm('确认签退, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        request.post('/reservation/checkOut', {
          reservationId: row.id,
          studentId: JSON.parse(localStorage.getItem("user")).id
        }).then((resp) => {
          if (resp.code === 200) {
            this.$message.success('签退成功');
            this.getDataList();
          } else {
            this.$message.error(resp.message || '签退失败');
          }
        }).catch(err => {
          console.error('签退错误:', err);
          this.$message.error('签退失败，请稍后重试');
        });
      }).catch(() => {});
    }
  },
  created() {
    this.getDataList();
  },
}
</script>

<style scoped>
.container {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
}
</style>