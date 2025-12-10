<template>
  <el-container class="layout-container">
    <!-- 头部区域 -->
    <el-header class="header">
      <div class="logo">
        <span class="logo-text">自习室预约管理系统</span>
      </div>
      <div class="user-info">
        <el-dropdown size="medium">
          <span class="el-dropdown-link user-name" style="cursor: pointer">
            <span>{{ username }}</span>
            <i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown" class="dropdown-menu">
            <el-dropdown-item @click.native="logout">退出</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 侧边栏和主体 -->
    <el-container>
      <!-- 侧边栏导航 -->
      <el-aside class="sidebar">
        <el-menu :default-active="activeIndex" class="el-menu-demo" @select="handleSelect">
          <el-menu-item index="/dashboard" v-if="isAdmin">
            <i class="el-icon-data-line"></i>
            <span>数据分析</span>
          </el-menu-item>
          <el-menu-item index="/student" v-if="isAdmin">
            <i class="el-icon-files"></i>
            <span>学生信息管理</span>
          </el-menu-item>
          <el-menu-item index="/studyRoom" v-if="isAdmin">
            <i class="el-icon-eleme"></i>
            <span>教室管理</span>
          </el-menu-item>
          <el-menu-item index="/seat" v-if="isAdmin">
            <i class="el-icon-s-grid"></i>
            <span>座位管理</span>
          </el-menu-item>
          <el-menu-item index="/reservationData" v-if="isAdmin">
            <i class="el-icon-tickets"></i>
            <span>预约数据</span>
          </el-menu-item>
          <el-menu-item index="/violation" v-if="isAdmin">
            <i class="el-icon-warning"></i>
            <span>违规记录</span>
          </el-menu-item>
          <el-menu-item index="/blacklist" v-if="isAdmin">
            <i class="el-icon-user-solid"></i>
            <span>黑名单管理</span>
          </el-menu-item>
          <el-menu-item index="/feedbackAdmin" v-if="isAdmin">
            <i class="el-icon-chat-dot-square"></i>
            <span>投诉与评价</span>
          </el-menu-item>
          <el-menu-item index="/reservationRoom" v-if="isStudent">
            <i class="el-icon-thumb"></i>
            <span>预约自习室</span>
          </el-menu-item>
          <el-menu-item index="/info" v-if="isStudent">
            <i class="el-icon-user"></i>
            <span>个人信息</span>
          </el-menu-item>
          <el-menu-item index="/record" v-if="isStudent">
            <i class="el-icon-monitor"></i>
            <span>预约记录</span>
          </el-menu-item>
          <el-menu-item index="/feedback" v-if="isStudent">
            <i class="el-icon-edit"></i>
            <span>提交反馈</span>
          </el-menu-item>
          <el-menu-item index="/feedbackList" v-if="isStudent">
            <i class="el-icon-chat-line-square"></i>
            <span>我的反馈</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主体数据 -->
      <el-main class="main-body">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: "Layout.vue",
  data() {
    return {
      role: localStorage.getItem('role'),
      isAdmin: false,
      isStudent: false,
      username: '',
      activeIndex: '',
    };
  },
  methods: {
    // 退出
    logout() {
      // 清除浏览器用户数据
      localStorage.removeItem('user')
      localStorage.removeItem('role');
      localStorage.removeItem('token');
      this.$router.push('/login');
    },
    handleSelect(key, keyPath) {
      this.activeIndex = key;
      this.$router.push(key);
    },
  },
  created() {
    if (this.role === 'student') {
      this.isStudent = true;
      this.username = JSON.parse(localStorage.getItem('user')).studentName;
      this.activeIndex = '/reservationRoom';
    } else if (this.role === 'admin') {
      this.isAdmin = true;
      this.username = JSON.parse(localStorage.getItem('user')).account;
      this.activeIndex = '/dashboard';
    }
  },
};
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.header {
  background-color: #409EFF; /* 更改背景颜色 */
  color: white; /* 更改文字颜色 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.logo {
  font-size: 28px; /* 增加字体大小 */
  font-weight: bold;
}

.user-info {
  text-align: right;
}

.user-name {
  font-size: 18px;
  color: white; /* 继承header的文字颜色 */
  display: flex;
  align-items: center;
}

.dropdown-menu {
  margin-top: -5px;
}

.sidebar {
  width: 150px !important;
  background-color: white;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.1);
}

.main-body {
  background-color: white;
  padding: 20px;
}
</style>
