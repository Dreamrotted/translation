<!--管理员数据分析与可视化-->
<template>
  <div class="dashboard-container">
    <!-- 实时概览数据卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :xs="12" :sm="6">
        <el-card class="data-card">
          <div class="card-content">
            <i class="el-icon-s-home card-icon" style="color: #409EFF;"></i>
            <div class="card-info">
              <div class="card-value">{{ overview.totalSeats }}</div>
              <div class="card-label">总座位数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="data-card">
          <div class="card-content">
            <i class="el-icon-user card-icon" style="color: #67C23A;"></i>
            <div class="card-info">
              <div class="card-value">{{ overview.occupiedSeats }}</div>
              <div class="card-label">使用中座位</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="data-card">
          <div class="card-content">
            <i class="el-icon-pie-chart card-icon" style="color: #E6A23C;"></i>
            <div class="card-info">
              <div class="card-value">{{ overview.usageRate }}%</div>
              <div class="card-label">总使用率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="data-card">
          <div class="card-content">
            <i class="el-icon-warning card-icon" style="color: #F56C6C;"></i>
            <div class="card-info">
              <div class="card-value">{{ overview.todayViolations }}</div>
              <div class="card-label">今日违规次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 各自习室实时人数 -->
    <el-card class="chart-card">
      <div slot="header" class="card-header">
        <span>各自习室实时人数</span>
        <span class="update-time">更新时间: {{ realTimeData.updateTime }}</span>
      </div>
      <el-table :data="realTimeData.rooms" stripe border max-height="300">
        <el-table-column prop="roomNumber" label="自习室编号" align="center" width="150"></el-table-column>
        <el-table-column prop="location" label="位置" align="center" min-width="150"></el-table-column>
        <el-table-column prop="totalSeats" label="总座位数" align="center" width="100"></el-table-column>
        <el-table-column prop="occupiedSeats" label="使用中" align="center" width="100">
          <template v-slot="scope">
            <el-tag type="success">{{ scope.row.occupiedSeats }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="availableSeats" label="可用" align="center" width="100">
          <template v-slot="scope">
            <el-tag type="info">{{ scope.row.availableSeats }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="usageRate" label="使用率" align="center" width="120">
          <template v-slot="scope">
            <el-progress :percentage="parseFloat(scope.row.usageRate)" :color="getProgressColor(scope.row.usageRate)"></el-progress>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 自习室使用率对比（柱状图） -->
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span>自习室使用率对比</span>
            <div class="date-picker-wrapper">
              <el-date-picker
                  v-model="comparisonDateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="yyyy-MM-dd"
                  size="small"
                  @change="getRoomUsageComparison">
              </el-date-picker>
            </div>
          </div>
          <div id="roomComparisonChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>

      <!-- 时段使用率趋势（折线图） -->
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span>时段使用率趋势</span>
            <div class="controls-wrapper">
              <el-select v-model="selectedRoomId" placeholder="选择自习室" size="small" @change="getRoomHourlyTrend" style="width: 150px; margin-right: 10px;">
                <el-option v-for="room in roomList" :key="room.id" :label="room.roomNumber" :value="room.id"></el-option>
              </el-select>
              <el-date-picker
                  v-model="trendDate"
                  type="date"
                  placeholder="选择日期"
                  value-format="yyyy-MM-dd"
                  size="small"
                  @change="getRoomHourlyTrend">
              </el-date-picker>
            </div>
          </div>
          <div id="hourlyTrendChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from "@/utils/request";
import * as echarts from 'echarts';

export default {
  data() {
    return {
      overview: {
        totalSeats: 0,
        occupiedSeats: 0,
        usageRate: 0,
        todayViolations: 0,
        todayReservations: 0,
        todayCompleted: 0
      },
      realTimeData: {
        rooms: [],
        updateTime: ''
      },
      comparisonDateRange: null,
      selectedRoomId: null,
      trendDate: null,
      roomList: [],
      roomComparisonChart: null,
      hourlyTrendChart: null,
      autoRefreshTimer: null
    };
  },
  methods: {
    // 获取实时概览数据
    getOverview() {
      request.get('/statistics/overview').then(resp => {
        if (resp.code === 200) {
          this.overview = resp.data;
        }
      });
    },

    // 获取各自习室实时人数
    getRealTimeOccupancy() {
      request.get('/statistics/realTimeOccupancy').then(resp => {
        if (resp.code === 200) {
          this.realTimeData = resp.data;
        }
      });
    },

    // 获取自习室使用率对比
    getRoomUsageComparison() {
      const params = {};
      if (this.comparisonDateRange && this.comparisonDateRange.length === 2) {
        params.startDate = this.comparisonDateRange[0];
        params.endDate = this.comparisonDateRange[1];
      }

      request.get('/statistics/roomUsageComparison', {params}).then(resp => {
        if (resp.code === 200) {
          this.renderRoomComparisonChart(resp.data);
        }
      });
    },

    // 获取时段使用率趋势
    getRoomHourlyTrend() {
      if (!this.selectedRoomId) {
        return;
      }

      const params = {roomId: this.selectedRoomId};
      if (this.trendDate) {
        params.date = this.trendDate;
      }

      request.get('/statistics/roomHourlyTrend', {params}).then(resp => {
        if (resp.code === 200) {
          this.renderHourlyTrendChart(resp.data);
        }
      });
    },

    // 渲染柱状图
    renderRoomComparisonChart(data) {
      if (!this.roomComparisonChart) {
        this.roomComparisonChart = echarts.init(document.getElementById('roomComparisonChart'));
      }

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: '{b}<br/>使用率: {c}%'
        },
        xAxis: {
          type: 'category',
          data: data.roomNames,
          axisLabel: {
            rotate: 45,
            interval: 0
          }
        },
        yAxis: {
          type: 'value',
          name: '使用率(%)',
          max: 100
        },
        series: [{
          name: '使用率',
          type: 'bar',
          data: data.usageRates,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {offset: 0, color: '#83bff6'},
              {offset: 0.5, color: '#188df0'},
              {offset: 1, color: '#188df0'}
            ])
          },
          label: {
            show: true,
            position: 'top',
            formatter: '{c}%'
          }
        }]
      };

      this.roomComparisonChart.setOption(option);
    },

    // 渲染折线图
    renderHourlyTrendChart(data) {
      if (!this.hourlyTrendChart) {
        this.hourlyTrendChart = echarts.init(document.getElementById('hourlyTrendChart'));
      }

      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: '{b}<br/>使用率: {c}%'
        },
        xAxis: {
          type: 'category',
          data: data.hours,
          boundaryGap: false
        },
        yAxis: {
          type: 'value',
          name: '使用率(%)',
          max: 100
        },
        series: [{
          name: '使用率',
          type: 'line',
          data: data.usageRates,
          smooth: true,
          itemStyle: {
            color: '#67C23A'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {offset: 0, color: 'rgba(103, 194, 58, 0.3)'},
              {offset: 1, color: 'rgba(103, 194, 58, 0.1)'}
            ])
          },
          label: {
            show: false
          }
        }]
      };

      this.hourlyTrendChart.setOption(option);
    },

    // 获取自习室列表
    getRoomList() {
      request.get('/studyRooms/list').then(resp => {
        if (resp.code === 200) {
          this.roomList = resp.data;
          if (this.roomList.length > 0) {
            this.selectedRoomId = this.roomList[0].id;
            this.getRoomHourlyTrend();
          }
        }
      });
    },

    // 进度条颜色
    getProgressColor(rate) {
      const r = parseFloat(rate);
      if (r >= 80) return '#F56C6C';
      if (r >= 60) return '#E6A23C';
      if (r >= 40) return '#409EFF';
      return '#67C23A';
    },

    // 刷新所有数据
    refreshAllData() {
      this.getOverview();
      this.getRealTimeOccupancy();
      this.getRoomUsageComparison();
      if (this.selectedRoomId) {
        this.getRoomHourlyTrend();
      }
    },

    // 启动自动刷新
    startAutoRefresh() {
      // 每10秒刷新一次
      this.autoRefreshTimer = setInterval(() => {
        this.refreshAllData();
      }, 10000);
    },

    // 停止自动刷新
    stopAutoRefresh() {
      if (this.autoRefreshTimer) {
        clearInterval(this.autoRefreshTimer);
        this.autoRefreshTimer = null;
      }
    }
  },
  mounted() {
    // 初始化数据
    this.getOverview();
    this.getRealTimeOccupancy();
    this.getRoomUsageComparison();
    this.getRoomList();

    // 启动自动刷新
    this.startAutoRefresh();

    // 窗口大小改变时重新渲染图表
    window.addEventListener('resize', () => {
      if (this.roomComparisonChart) {
        this.roomComparisonChart.resize();
      }
      if (this.hourlyTrendChart) {
        this.hourlyTrendChart.resize();
      }
    });
  },
  beforeDestroy() {
    // 停止自动刷新
    this.stopAutoRefresh();

    // 销毁图表实例
    if (this.roomComparisonChart) {
      this.roomComparisonChart.dispose();
    }
    if (this.hourlyTrendChart) {
      this.hourlyTrendChart.dispose();
    }

    // 移除窗口大小监听
    window.removeEventListener('resize', () => {});
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f0f2f5;
}

.overview-cards {
  margin-bottom: 20px;
}

.data-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.data-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-content {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.card-icon {
  font-size: 48px;
  margin-right: 20px;
}

.card-info {
  flex: 1;
}

.card-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.card-label {
  font-size: 14px;
  color: #909399;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.update-time {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}

.date-picker-wrapper, .controls-wrapper {
  display: flex;
  align-items: center;
}

.charts-row {
  margin-top: 20px;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .card-icon {
    font-size: 36px;
    margin-right: 10px;
  }

  .card-value {
    font-size: 24px;
  }

  .card-label {
    font-size: 12px;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .date-picker-wrapper, .controls-wrapper {
    margin-top: 10px;
  }
}
</style>
