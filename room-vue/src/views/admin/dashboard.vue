<!--管理员数据分析与可视化 - 纯静态演示版-->
<template>
  <div class="dashboard-container">
    <!-- 演示提示 -->
    <div class="demo-notice">
      <el-alert
          title="📊 纯静态数据演示页面"
          type="info"
          description="这是一个完全静态的演示页面，所有数据均为模拟数据，不连接任何后端服务器。图表和按钮仅用于界面展示。"
          show-icon
          :closable="false"
      ></el-alert>
    </div>

    <!-- 实时概览数据卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :xs="12" :sm="6">
        <el-card class="data-card">
          <div class="card-content">
            <i class="el-icon-s-home card-icon" style="color: #409EFF;"></i>
            <div class="card-info">
              <div class="card-value">480</div>
              <div class="card-label">总座位数</div>
            </div>
          </div>
          <div class="card-tip">模拟数据</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="data-card">
          <div class="card-content">
            <i class="el-icon-user card-icon" style="color: #67C23A;"></i>
            <div class="card-info">
              <div class="card-value">326</div>
              <div class="card-label">使用中座位</div>
            </div>
          </div>
          <div class="card-tip">模拟数据</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="data-card">
          <div class="card-content">
            <i class="el-icon-pie-chart card-icon" style="color: #E6A23C;"></i>
            <div class="card-info">
              <div class="card-value">67.9%</div>
              <div class="card-label">总使用率</div>
            </div>
          </div>
          <div class="card-tip">模拟数据</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="data-card">
          <div class="card-content">
            <i class="el-icon-warning card-icon" style="color: #F56C6C;"></i>
            <div class="card-info">
              <div class="card-value">8</div>
              <div class="card-label">今日违规次数</div>
            </div>
          </div>
          <div class="card-tip">模拟数据</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 各自习室实时人数 -->
    <el-card class="chart-card">
      <div slot="header" class="card-header">
        <span>各自习室实时人数</span>
        <span class="update-time">更新时间: {{ currentTime }}</span>
        <el-button type="text" @click="refreshDemoData" size="small">刷新演示数据</el-button>
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
      <div class="demo-hint">点击"刷新演示数据"按钮可随机生成新的模拟数据</div>
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
                  @change="showDateChangeDemo('对比图日期已更新')">
              </el-date-picker>
            </div>
          </div>
          <div id="roomComparisonChart" style="width: 100%; height: 400px;">
            <div class="demo-chart-placeholder">
              <i class="el-icon-data-analysis" style="font-size: 60px; color: #909399; margin-bottom: 20px;"></i>
              <h3>柱状图演示区域</h3>
              <p>选择日期范围可查看模拟数据变化</p>
              <el-button type="primary" @click="generateDemoChartData" size="small">生成演示数据</el-button>
            </div>
          </div>
          <div class="chart-tip">此为静态图表演示区域，数据随机生成</div>
        </el-card>
      </el-col>

      <!-- 时段使用率趋势（折线图） -->
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span>时段使用率趋势</span>
            <div class="controls-wrapper">
              <el-select v-model="selectedRoomId" placeholder="选择自习室" size="small" @change="showSelectChangeDemo" style="width: 150px; margin-right: 10px;">
                <el-option v-for="room in roomList" :key="room.id" :label="room.roomNumber" :value="room.id"></el-option>
              </el-select>
              <el-date-picker
                  v-model="trendDate"
                  type="date"
                  placeholder="选择日期"
                  value-format="yyyy-MM-dd"
                  size="small"
                  @change="showDateChangeDemo('趋势图日期已更新')">
              </el-date-picker>
            </div>
          </div>
          <div id="hourlyTrendChart" style="width: 100%; height: 400px;">
            <div class="demo-chart-placeholder">
              <i class="el-icon-trend-charts" style="font-size: 60px; color: #909399; margin-bottom: 20px;"></i>
              <h3>折线图演示区域</h3>
              <p>选择自习室和日期可查看模拟趋势</p>
              <el-button type="success" @click="generateDemoTrendData" size="small">生成趋势数据</el-button>
            </div>
          </div>
          <div class="chart-tip">此为静态图表演示区域，数据随机生成</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 演示操作面板 -->
    <el-card class="demo-controls">
      <div slot="header">
        <span>演示控制面板</span>
      </div>
      <div class="controls-content">
        <el-button type="primary" @click="showAllData">查看所有数据</el-button>
        <el-button type="success" @click="exportDemoData">导出演示数据</el-button>
        <el-button type="warning" @click="resetDemoData">重置演示</el-button>
        <el-button type="info" @click="showHelp">查看帮助</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    // 模拟自习室列表
    const mockRoomList = [
      { id: 1, roomNumber: '101', location: 'A栋1楼' },
      { id: 2, roomNumber: '102', location: 'A栋1楼' },
      { id: 3, roomNumber: '201', location: 'B栋2楼' },
      { id: 4, roomNumber: '202', location: 'B栋2楼' },
      { id: 5, roomNumber: '301', location: 'C栋3楼' },
      { id: 6, roomNumber: '302', location: 'C栋3楼' }
    ];

    // 生成模拟实时数据
    const generateRealTimeData = () => {
      return mockRoomList.map(room => {
        const totalSeats = Math.floor(Math.random() * 80) + 40; // 40-120个座位
        const occupiedSeats = Math.floor(Math.random() * totalSeats * 0.8) + Math.floor(totalSeats * 0.2); // 20%-100%
        const usageRate = ((occupiedSeats / totalSeats) * 100).toFixed(1);

        return {
          ...room,
          totalSeats,
          occupiedSeats,
          availableSeats: totalSeats - occupiedSeats,
          usageRate
        };
      });
    };

    return {
      // 当前时间（用于演示）
      currentTime: new Date().toLocaleTimeString(),

      // 模拟数据
      roomList: mockRoomList,
      realTimeData: {
        rooms: generateRealTimeData(),
        updateTime: new Date().toLocaleString()
      },

      // 演示控件
      comparisonDateRange: null,
      selectedRoomId: mockRoomList[0]?.id,
      trendDate: new Date().toISOString().split('T')[0],

      // 自动刷新计时器
      autoRefreshTimer: null
    };
  },

  methods: {
    // 显示日期选择器更改演示
    showDateChangeDemo(message) {
      this.$message({
        message: `演示: ${message}`,
        type: 'info',
        duration: 1500
      });
    },

    // 显示下拉框选择演示
    showSelectChangeDemo() {
      this.$message({
        message: '演示: 自习室选择已更新',
        type: 'info',
        duration: 1500
      });
    },

    // 刷新演示数据
    refreshDemoData() {
      // 更新当前时间
      this.currentTime = new Date().toLocaleTimeString();

      // 重新生成模拟数据
      this.realTimeData = {
        rooms: this.roomList.map(room => {
          const totalSeats = Math.floor(Math.random() * 80) + 40;
          const occupiedSeats = Math.floor(Math.random() * totalSeats * 0.8) + Math.floor(totalSeats * 0.2);
          const usageRate = ((occupiedSeats / totalSeats) * 100).toFixed(1);

          return {
            ...room,
            totalSeats,
            occupiedSeats,
            availableSeats: totalSeats - occupiedSeats,
            usageRate
          };
        }),
        updateTime: new Date().toLocaleString()
      };

      this.$message({
        message: '演示数据已刷新',
        type: 'success',
        duration: 1500
      });
    },

    // 生成演示图表数据
    generateDemoChartData() {
      this.$message({
        message: '正在生成柱状图演示数据...',
        type: 'info',
        duration: 1000
      });

      // 模拟延迟效果
      setTimeout(() => {
        this.$message({
          message: '柱状图演示数据已生成',
          type: 'success',
          duration: 1500
        });
      }, 800);
    },

    // 生成演示趋势数据
    generateDemoTrendData() {
      this.$message({
        message: '正在生成折线图演示数据...',
        type: 'info',
        duration: 1000
      });

      // 模拟延迟效果
      setTimeout(() => {
        this.$message({
          message: '折线图演示数据已生成',
          type: 'success',
          duration: 1500
        });
      }, 800);
    },

    // 查看所有数据
    showAllData() {
      this.$alert(`
        <div style="max-height: 400px; overflow-y: auto;">
          <h4>当前演示数据概览</h4>
          <p><strong>自习室数量：</strong>${this.roomList.length}个</p>
          <p><strong>总座位数模拟：</strong>480个</p>
          <p><strong>使用率模拟：</strong>67.9%</p>
          <hr>
          <p><em>注意：所有数据均为前端生成的随机数据，仅用于界面演示。</em></p>
        </div>
      `, '演示数据概览', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定'
      });
    },

    // 导出演示数据
    exportDemoData() {
      const demoData = {
        title: '自习室管理演示数据',
        exportTime: new Date().toLocaleString(),
        rooms: this.realTimeData.rooms,
        totalStats: {
          totalSeats: 480,
          occupiedSeats: 326,
          usageRate: '67.9%'
        }
      };

      const dataStr = JSON.stringify(demoData, null, 2);
      const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);

      this.$message({
        message: '演示数据已准备导出',
        type: 'success',
        duration: 2000
      });

      // 提示用户下载
      setTimeout(() => {
        const linkElement = document.createElement('a');
        linkElement.setAttribute('href', dataUri);
        linkElement.setAttribute('download', 'demo-data.json');
        linkElement.click();
      }, 500);
    },

    // 重置演示数据
    resetDemoData() {
      this.$confirm('确定要重置所有演示数据吗？', '重置演示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.refreshDemoData();
        this.comparisonDateRange = null;
        this.selectedRoomId = this.roomList[0]?.id;
        this.trendDate = new Date().toISOString().split('T')[0];

        this.$message({
          message: '演示数据已重置',
          type: 'success'
        });
      });
    },

    // 显示帮助
    showHelp() {
      this.$alert(`
        <div style="line-height: 1.6;">
          <h4>📊 纯静态演示页面使用说明</h4>
          <p><strong>功能说明：</strong></p>
          <ul>
            <li>这是一个完全静态的数据分析演示页面</li>
            <li>所有数据均为前端随机生成的模拟数据</li>
            <li>不连接任何后端服务器或数据库</li>
            <li>所有按钮操作仅用于界面交互演示</li>
          </ul>

          <p><strong>可用操作：</strong></p>
          <ul>
            <li><strong>刷新演示数据：</strong>随机生成新的自习室使用数据</li>
            <li><strong>日期选择器：</strong>体验日期选择的交互效果</li>
            <li><strong>自习室选择：</strong>体验下拉选择的交互效果</li>
            <li><strong>控制面板按钮：</strong>体验不同类型按钮的交互</li>
          </ul>

          <p><em>注意：此页面仅用于演示界面效果，所有数据操作不会持久保存。</em></p>
        </div>
      `, '演示页面帮助', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '我知道了',
        customClass: 'help-dialog'
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

    // 启动时间更新
    startTimeUpdate() {
      this.autoRefreshTimer = setInterval(() => {
        this.currentTime = new Date().toLocaleTimeString();
      }, 1000); // 每秒更新一次时间
    },

    // 停止时间更新
    stopTimeUpdate() {
      if (this.autoRefreshTimer) {
        clearInterval(this.autoRefreshTimer);
      }
    }
  },

  mounted() {
    // 启动时间更新
    this.startTimeUpdate();

    // 设置初始数据
    this.refreshDemoData();

    // 模拟加载效果
    setTimeout(() => {
      this.$message({
        message: '演示数据加载完成',
        type: 'success',
        duration: 2000
      });
    }, 1000);
  },

  beforeDestroy() {
    // 清理计时器
    this.stopTimeUpdate();
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.demo-notice {
  margin-bottom: 20px;
}

.overview-cards {
  margin-bottom: 20px;
}

.data-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
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

.card-tip {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 12px;
  color: #909399;
  background-color: #f0f0f0;
  padding: 2px 6px;
  border-radius: 3px;
}

.chart-card {
  margin-bottom: 20px;
  position: relative;
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

.demo-hint {
  margin-top: 15px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
  font-size: 12px;
  color: #909399;
  text-align: center;
}

.chart-tip {
  position: absolute;
  bottom: 10px;
  right: 10px;
  font-size: 12px;
  color: #909399;
  background-color: rgba(255, 255, 255, 0.8);
  padding: 4px 8px;
  border-radius: 3px;
}

.demo-chart-placeholder {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 400px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 4px;
  color: #606266;
}

.demo-chart-placeholder h3 {
  margin: 10px 0;
  color: #303133;
}

.demo-chart-placeholder p {
  margin-bottom: 20px;
  text-align: center;
}

.demo-controls {
  margin-top: 30px;
}

.controls-content {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
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

  .controls-content {
    flex-direction: column;
  }

  .controls-content .el-button {
    width: 100%;
    margin-bottom: 10px;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.data-card, .chart-card, .demo-controls {
  animation: fadeIn 0.5s ease-out;
}
</style>