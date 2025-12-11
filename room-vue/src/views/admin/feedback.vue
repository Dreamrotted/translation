<!--管理员投诉与评价管理-->
<template>
  <div class="container">
    <!--条件查询-->
    <div class="search-bar">
      <el-input v-model="query.studentNumber" placeholder="学号" style="width: 150px;"
                @keyup.enter.native="getDataList"></el-input>
      <el-select v-model="query.type" placeholder="类型" style="width: 120px; margin-left: 10px;" clearable>
        <el-option label="投诉" :value="0"></el-option>
        <el-option label="评价" :value="1"></el-option>
      </el-select>
      <el-select v-model="query.status" placeholder="状态" style="width: 120px; margin-left: 10px;" clearable>
        <el-option label="待处理" :value="0"></el-option>
        <el-option label="已回复" :value="1"></el-option>
        <el-option label="已关闭" :value="2"></el-option>
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
              highlight-current-row>
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
      <el-table-column type="index" min-width="60" header-align="center" align="center" label="序号"></el-table-column>
      <el-table-column prop="student.studentName" header-align="center" align="center" min-width="100" label="姓名"></el-table-column>
      <el-table-column prop="student.studentNumber" header-align="center" align="center" min-width="100" label="学号"></el-table-column>
      <el-table-column prop="type" header-align="center" align="center" min-width="80" label="类型">
        <template v-slot="scope">
          <el-tag v-if="scope.row.type === 0" type="warning">投诉</el-tag>
          <el-tag v-else type="success">评价</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" header-align="center" align="center" min-width="150" label="标题" show-overflow-tooltip></el-table-column>
      <el-table-column prop="status" header-align="center" align="center" min-width="90" label="状态">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === 0" type="info">待处理</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="success">已回复</el-tag>
          <el-tag v-else type="info">已关闭</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" header-align="center" align="center" min-width="160" label="提交时间"></el-table-column>
      <el-table-column fixed="right" header-align="center" align="center" width="200" label="操作">
        <template v-slot="scope">
          <el-button type="primary" size="small" @click="handleReply(scope.row)" v-if="scope.row.status === 0">
            回复
          </el-button>
          <el-button type="warning" size="small" @click="handleClose(scope.row.id)" v-if="scope.row.status !== 2">
            关闭
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
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

    <!-- 回复对话框 -->
    <el-dialog title="回复" :visible.sync="replyDialogVisible" width="600px" @close="resetReplyForm">
      <div style="margin-bottom: 15px;">
        <p><strong>学生：</strong>{{ currentFeedback.student ? currentFeedback.student.studentName : '' }}</p>
        <p><strong>标题：</strong>{{ currentFeedback.title }}</p>
        <p><strong>内容：</strong>{{ currentFeedback.content }}</p>
      </div>
      <el-form :model="replyForm" :rules="replyFormRules" ref="replyForm" label-width="80px">
        <el-form-item label="回复内容" prop="reply">
          <el-input type="textarea" v-model="replyForm.reply" placeholder="请输入回复内容" :rows="5"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>



<style scoped>
.container {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
}
</style>
