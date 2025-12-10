<!--管理员预约数据查看-->
<template>
  <div class="container">
    <!--条件查询-->
    <div class="search-bar">
      <el-input v-model="query.studentNumber" placeholder="学号" style="width: 150px;"
                @keyup.enter.native="getDataList"></el-input>
      <el-input v-model="query.studentName" placeholder="姓名" style="width: 150px; margin-left: 10px;"
                @keyup.enter.native="getDataList"></el-input>
      <el-input v-model="query.roomNumber" placeholder="自习室编号" style="width: 150px; margin-left: 10px;"
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
      <el-table-column prop="student.studentName" header-align="center" align="center" min-width="100" label="姓名">
      </el-table-column>
      <el-table-column prop="student.studentNumber" header-align="center" align="center" min-width="100" label="学号">
      </el-table-column>
      <el-table-column prop="student.majorName" header-align="center" align="center" min-width="120" label="专业">
      </el-table-column>
      <el-table-column prop="studyRooms.roomNumber" header-align="center" align="center" min-width="120" label="自习室编号">
      </el-table-column>
      <el-table-column prop="studyRooms.location" header-align="center" align="center" min-width="150" label="自习室位置">
      </el-table-column>
      <el-table-column header-align="center" align="center" min-width="120" label="座位编号">
        <template v-slot="scope">
          <el-tag type="primary" v-if="scope.row.seat">
            {{ scope.row.seat.seatNumber }}
          </el-tag>
          <span v-else style="color: #999;">--</span>
        </template>
      </el-table-column>
      <el-table-column header-align="center" align="center" min-width="100" label="座位类型">
        <template v-slot="scope">
          <el-tag size="small" type="info" v-if="scope.row.seat">
            {{ scope.row.seat.seatType }}
          </el-tag>
          <span v-else style="color: #999;">--</span>
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
        studentNumber: '',
        studentName: '',
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
      };
      
      // 添加查询条件
      if (this.query.studentNumber) {
        params.studentNumber = this.query.studentNumber;
      }
      if (this.query.studentName) {
        params.studentName = this.query.studentName;
      }
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
        } else {
          this.dataList = [];
          this.$message.error(resp.message || '获取数据失败');
        }
        this.dataListLoading = false;
      }).catch(err => {
        console.error('获取预约数据错误:', err);
        this.dataList = [];
        this.dataListLoading = false;
        this.$message.error('获取数据失败，请稍后重试');
      });
    },
    resetSearch() {
      this.query = {
        studentNumber: '',
        studentName: '',
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