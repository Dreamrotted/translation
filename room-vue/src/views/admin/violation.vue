<!--管理员违规记录管理-->
<template>
  <div class="container">
    <!--条件查询-->
    <div class="search-bar">
      <el-input v-model="query.studentNumber" placeholder="学号" style="width: 150px;"
                @keyup.enter.native="getDataList"></el-input>
      <el-select v-model="query.violationType" placeholder="违规类型" style="width: 150px; margin-left: 10px;" clearable>
        <el-option label="未按时签到" value="未按时签到"></el-option>
        <el-option label="未主动签退" value="未主动签退"></el-option>
        <el-option label="其他" value="其他"></el-option>
      </el-select>
      <el-select v-model="query.recordType" placeholder="记录类型" style="width: 150px; margin-left: 10px;" clearable>
        <el-option label="系统自动" :value="0"></el-option>
        <el-option label="管理员手动" :value="1"></el-option>
      </el-select>
      <el-select v-model="query.status" placeholder="处理状态" style="width: 150px; margin-left: 10px;" clearable>
        <el-option label="未处理" :value="0"></el-option>
        <el-option label="已处理" :value="1"></el-option>
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
      <el-button type="success" icon="el-icon-plus" @click="handleAdd" style="margin-left: 10px">手动登记</el-button>
    </div>

    <el-table :data="dataList"
              border
              stripe
              v-loading="dataListLoading"
              highlight-current-row>
      <el-table-column type="index" min-width="60" header-align="center" align="center" label="序号"></el-table-column>
      <el-table-column prop="student.studentName" header-align="center" align="center" min-width="100" label="姓名"></el-table-column>
      <el-table-column prop="student.studentNumber" header-align="center" align="center" min-width="100" label="学号"></el-table-column>
      <el-table-column prop="violationType" header-align="center" align="center" min-width="120" label="违规类型">
        <template v-slot="scope">
          <el-tag v-if="scope.row.violationType === '未按时签到'" type="warning">{{ scope.row.violationType }}</el-tag>
          <el-tag v-else-if="scope.row.violationType === '未主动签退'" type="info">{{ scope.row.violationType }}</el-tag>
          <el-tag v-else>{{ scope.row.violationType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="violationDesc" header-align="center" align="center" min-width="200" label="违规描述"></el-table-column>
      <el-table-column prop="violationTime" header-align="center" align="center" min-width="160" label="违规时间"></el-table-column>
      <el-table-column prop="recordType" header-align="center" align="center" min-width="100" label="记录类型">
        <template v-slot="scope">
          <el-tag v-if="scope.row.recordType === 0" type="success">系统自动</el-tag>
          <el-tag v-else type="primary">管理员手动</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" header-align="center" align="center" min-width="100" label="处理状态">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === 0" type="danger">未处理</el-tag>
          <el-tag v-else type="success">已处理</el-tag>
        </template>
      </el-table-column>
      <el-table-column fixed="right" header-align="center" align="center" width="180" label="操作">
        <template v-slot="scope">
          <el-button type="primary" size="small" @click="handleProcess(scope.row)" v-if="scope.row.status === 0">
            标记已处理
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

    <!-- 添加违规记录对话框 -->
    <el-dialog title="手动登记违规" :visible.sync="addDialogVisible" width="500px" @close="resetAddForm">
      <el-form :model="addForm" :rules="addFormRules" ref="addForm" label-width="100px">
        <el-form-item label="学号" prop="studentNumber">
          <el-input v-model.number="addForm.studentNumber" placeholder="请输入学号" @blur="getStudentInfo"></el-input>
        </el-form-item>
        <el-form-item label="学生姓名" v-if="studentInfo.studentName">
          <el-input v-model="studentInfo.studentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="违规类型" prop="violationType">
          <el-select v-model="addForm.violationType" placeholder="请选择违规类型" style="width: 100%;">
            <el-option label="未按时签到" value="未按时签到"></el-option>
            <el-option label="未主动签退" value="未主动签退"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="违规描述" prop="violationDesc">
          <el-input type="textarea" v-model="addForm.violationDesc" placeholder="请输入违规描述" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
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
