<!--学生反馈记录列表-->
<template>
  <div class="container">
    <el-card class="list-card">
      <div slot="header" class="card-header">
        <span style="font-size: 18px; font-weight: bold;">我的反馈记录</span>
        <div>
          <el-button type="primary" size="small" icon="el-icon-plus" @click="goToSubmit">提交新反馈</el-button>
          <el-button size="small" icon="el-icon-refresh" @click="getMyFeedback">刷新</el-button>
        </div>
      </div>
      <el-table :data="feedbackList" v-loading="listLoading" border stripe>
        <el-table-column type="expand">
          <template v-slot="scope">
            <div style="padding: 10px 20px;">
              <p><strong>详细内容：</strong>{{ scope.row.content }}</p>
              <p v-if="scope.row.studyRooms">
                <strong>关联自习室：</strong>{{ scope.row.studyRooms.roomNumber }} - {{ scope.row.studyRooms.location }}
              </p>
              <p v-if="scope.row.reply">
                <strong style="color: #409EFF;">管理员回复：</strong>
                <span style="color: #606266;">{{ scope.row.reply }}</span>
              </p>
              <p v-if="scope.row.replyTime">
                <strong>回复时间：</strong>{{ scope.row.replyTime }}
              </p>
            </div>
          </template>
        </el-table-column>
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="type" label="类型" width="80" align="center">
          <template v-slot="scope">
            <el-tag v-if="scope.row.type === 0" type="warning">投诉</el-tag>
            <el-tag v-else type="success">评价</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template v-slot="scope">
            <el-tag v-if="scope.row.status === 0" type="info">待处理</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">已回复</el-tag>
            <el-tag v-else type="info">已关闭</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" align="center"></el-table-column>
      </el-table>

      <el-pagination
          v-if="totalPage > 0"
          @size-change="sizeChangeHandle"
          @current-change="currentChangeHandle"
          :current-page="current"
          :page-sizes="[10, 20, 50]"
          :page-size="pageSize"
          :total="totalPage"
          layout="total, sizes, prev, pager, next, jumper"
          style="margin-top: 20px; text-align: right;">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  data() {
    return {
      feedbackList: [],
      listLoading: false,
      current: 1,
      pageSize: 10,
      totalPage: 0
    };
  },
  methods: {
    getMyFeedback() {
      this.listLoading = true;
      const studentNumber = JSON.parse(localStorage.getItem("user")).studentNumber;
      request.get('/feedback/page', {
        params: {
          pageNum: this.current,
          pageSize: this.pageSize,
          studentNumber: studentNumber
        }
      }).then(resp => {
        if (resp.code === 200) {
          this.feedbackList = resp.data.list;
          this.totalPage = resp.data.total;
        } else {
          this.feedbackList = [];
          this.$message.error(resp.message || '获取记录失败');
        }
        this.listLoading = false;
      }).catch(err => {
        console.error('获取反馈记录错误:', err);
        this.feedbackList = [];
        this.listLoading = false;
        this.$message.error('获取记录失败');
      });
    },
    goToSubmit() {
      this.$router.push('/feedback');
    },
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.current = 1;
      this.getMyFeedback();
    },
    currentChangeHandle(val) {
      this.current = val;
      this.getMyFeedback();
    }
  },
  created() {
    this.getMyFeedback();
  }
}
</script>

<style scoped>
.container {
  padding: 20px;
}

.list-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
