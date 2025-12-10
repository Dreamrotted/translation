<template>
  <div class="container">
    <!-- 搜索筛选栏 -->
    <div class="search-bar">
      <el-form :inline="true">
        <el-form-item label="选择日期">
          <el-date-picker
              v-model="searchForm.date"
              type="date"
              placeholder="选择日期"
              value-format="yyyy-MM-dd"
              :picker-options="datePickerOptions">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-select
              v-model="searchForm.startTime"
              :picker-options="{
              start: '06:00',
              step: '00:30',
              end: '23:30'
            }"
              placeholder="选择开始时间">
          </el-time-select>
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-select
              v-model="searchForm.endTime"
              :picker-options="{
              start: '06:00',
              step: '00:30',
              end: '23:30',
              minTime: searchForm.startTime
            }"
              placeholder="选择结束时间">
          </el-time-select>
        </el-form-item>
        <el-form-item label="自习室">
          <el-select v-model="searchForm.roomId" placeholder="请选择自习室" clearable>
            <el-option
                v-for="room in roomList"
                :key="room.id"
                :label="room.roomNumber + ' - ' + room.location"
                :value="room.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="座位类型">
          <el-select v-model="searchForm.seatType" placeholder="全部类型" clearable>
            <el-option label="普通座位" value="普通座位"></el-option>
            <el-option label="插座座位" value="插座座位"></el-option>
            <el-option label="静音区" value="静音区"></el-option>
            <el-option label="讨论区" value="讨论区"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="searchAvailableSeats">查询可用座位</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 座位列表 -->
    <div class="seat-container" v-if="availableSeats.length > 0">
      <el-alert
          :title="`找到 ${availableSeats.length} 个可用座位`"
          type="success"
          :closable="false"
          show-icon
          style="margin-bottom: 20px;">
      </el-alert>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="seat in availableSeats" :key="seat.id" style="margin-bottom: 20px;">
          <el-card class="seat-card" shadow="hover">
            <div class="seat-info">
              <div class="seat-number">
                <i class="el-icon-s-grid"></i>
                {{ seat.seatNumber }}
              </div>
              <div class="seat-detail">
                <el-tag size="small" type="info">{{ seat.seatType }}</el-tag>
              </div>
              <div class="seat-location">
                <i class="el-icon-location-outline"></i>
                {{ seat.studyRooms ? seat.studyRooms.roomNumber : '--' }}
              </div>
              <div class="seat-location">
                {{ seat.studyRooms ? seat.studyRooms.location : '--' }}
              </div>
            </div>
            <div class="seat-action">
              <el-button type="primary" size="small" @click="reserveSeat(seat)" style="width: 100%;">预约</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div v-else-if="searched" class="empty-result">
      <el-empty description="没有找到可用座位"></el-empty>
    </div>

    <div v-else class="empty-result">
      <el-empty description="请选择日期和时间段查询可用座位"></el-empty>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  data() {
    return {
      searchForm: {
        date: '',
        startTime: '',
        endTime: '',
        roomId: null,
        seatType: ''
      },
      roomList: [],
      availableSeats: [],
      searched: false,
      datePickerOptions: {
        disabledDate(time) {
          // 禁用今天之前的日期
          return time.getTime() < Date.now() - 8.64e7;
        }
      }
    };
  },
  methods: {
    // 获取自习室列表
    getRoomList() {
      request.get('studyRooms/page', {
        params: {
          pageNum: 1,
          pageSize: 999
        }
      }).then((resp) => {
        if (resp.code === 200) {
          this.roomList = resp.data.list.filter(room => room.status === 0);
        }
      });
    },

    // 查询可用座位
    searchAvailableSeats() {
      if (!this.searchForm.date) {
        this.$message.warning('请选择日期');
        return;
      }
      if (!this.searchForm.startTime) {
        this.$message.warning('请选择开始时间');
        return;
      }
      if (!this.searchForm.endTime) {
        this.$message.warning('请选择结束时间');
        return;
      }
      if (!this.searchForm.roomId) {
        this.$message.warning('请选择自习室');
        return;
      }

      const startDateTime = `${this.searchForm.date} ${this.searchForm.startTime}:00`;
      const endDateTime = `${this.searchForm.date} ${this.searchForm.endTime}:00`;

      request.get('/reservation/availableSeats', {
        params: {
          roomId: this.searchForm.roomId,
          startTime: startDateTime,
          endTime: endDateTime,
          seatType: this.searchForm.seatType
        }
      }).then((res) => {
        if (res.code === 200) {
          this.availableSeats = res.data;
          this.searched = true;
          if (this.availableSeats.length === 0) {
            this.$message.info('没有可用座位');
          }
        } else {
          this.$notify.error(res.message);
        }
      });
    },

    // 预约座位
    reserveSeat(seat) {
      this.$confirm(`确认预约座位 ${seat.seatNumber}?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const startDateTime = `${this.searchForm.date} ${this.searchForm.startTime}:00`;
        const endDateTime = `${this.searchForm.date} ${this.searchForm.endTime}:00`;
        
        const data = {
          startTime: startDateTime,
          endTime: endDateTime,
          studentId: JSON.parse(localStorage.getItem("user")).id,
          seatId: seat.id
        };
        
        request.post("/reservation/add", data).then((res) => {
          if (res.code === 200) {
            this.$notify.success('预约成功');
            this.searchAvailableSeats(); // 重新查询可用座位
          } else {
            this.$notify.error(res.message);
          }
        });
      }).catch(() => {});
    },

    // 重置搜索
    resetSearch() {
      this.searchForm = {
        date: '',
        startTime: '',
        endTime: '',
        roomId: null,
        seatType: ''
      };
      this.availableSeats = [];
      this.searched = false;
    }
  },
  created() {
    this.getRoomList();
  }
};
</script>

<style scoped>
.container {
  padding: 20px;
}

.search-bar {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.seat-container {
  margin-top: 20px;
}

.seat-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.seat-card:hover {
  transform: translateY(-5px);
}

.seat-info {
  padding: 10px 0;
}

.seat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.seat-detail {
  margin: 8px 0;
}

.seat-location {
  color: #666;
  font-size: 14px;
  margin: 5px 0;
}

.seat-action {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.empty-result {
  padding: 50px 0;
  text-align: center;
}
</style>