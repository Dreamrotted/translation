<!--管理员黑名单管理-->
<template>
  <div class="container">
    <!--条件查询-->
    <div class="search-bar">
      <el-input v-model="query.studentNumber" placeholder="学号" style="width: 150px;"
                @keyup.enter.native="getDataList"></el-input>
      <el-select v-model="query.status" placeholder="状态" style="width: 150px; margin-left: 10px;" clearable>
        <el-option label="生效中" :value="1"></el-option>
        <el-option label="已解除" :value="0"></el-option>
      </el-select>
      <el-button type="primary" icon="el-icon-search" @click="getDataList" style="margin-left: 10px">搜索</el-button>
      <el-button icon="el-icon-refresh" @click="resetSearch" style="margin-left: 10px">重置</el-button>
      <el-button type="danger" icon="el-icon-plus" @click="handleAdd" style="margin-left: 10px">手动加入黑名单</el-button>
    </div>

    <el-table :data="dataList"
              border
              stripe
              v-loading="dataListLoading"
              highlight-current-row>
      <el-table-column type="index" min-width="60" header-align="center" align="center" label="序号"></el-table-column>
      <el-table-column prop="student.studentName" header-align="center" align="center" min-width="100" label="姓名"></el-table-column>
      <el-table-column prop="student.studentNumber" header-align="center" align="center" min-width="100" label="学号"></el-table-column>
      <el-table-column prop="student.majorName" header-align="center" align="center" min-width="150" label="专业"></el-table-column>
      <el-table-column prop="reason" header-align="center" align="center" min-width="200" label="原因"></el-table-column>
      <el-table-column prop="startTime" header-align="center" align="center" min-width="160" label="开始时间"></el-table-column>
      <el-table-column prop="endTime" header-align="center" align="center" min-width="160" label="结束时间"></el-table-column>
      <el-table-column prop="addType" header-align="center" align="center" min-width="100" label="加入方式">
        <template v-slot="scope">
          <el-tag v-if="scope.row.addType === 0" type="warning">系统自动</el-tag>
          <el-tag v-else type="primary">管理员手动</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" header-align="center" align="center" min-width="100" label="状态">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === 1" type="danger">生效中</el-tag>
          <el-tag v-else type="success">已解除</el-tag>
        </template>
      </el-table-column>
      <el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
        <template v-slot="scope">
          <el-button type="success" size="small" @click="handleRemove(scope.row.id)" v-if="scope.row.status === 1">
            解除
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

    <!-- 添加黑名单对话框 -->
    <el-dialog title="手动加入黑名单" :visible.sync="addDialogVisible" width="500px" @close="resetAddForm">
      <el-form :model="addForm" :rules="addFormRules" ref="addForm" label-width="100px">
        <el-form-item label="学号" prop="studentNumber">
          <el-input v-model.number="addForm.studentNumber" placeholder="请输入学号" @blur="getStudentInfo"></el-input>
        </el-form-item>
        <el-form-item label="学生姓名" v-if="studentInfo.studentName">
          <el-input v-model="studentInfo.studentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="违规次数" v-if="studentInfo.id">
          <el-tag type="warning">本月违规 {{ violationCount }} 次</el-tag>
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input type="textarea" v-model="addForm.reason" placeholder="请输入加入黑名单的原因" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="期限(天)" prop="days">
          <el-input-number v-model="addForm.days" :min="1" :max="30" placeholder="黑名单期限"></el-input-number>
          <span style="margin-left: 10px; color: #999;">默认7天</span>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request";
import { formatDateTime } from "@/utils/dateUtil";

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
        status: null
      },
      addDialogVisible: false,
      addForm: {
        studentNumber: null,
        reason: '',
        days: 7
      },
      addFormRules: {
        studentNumber: [{required: true, message: '请输入学号', trigger: 'blur'}],
        reason: [{required: true, message: '请输入原因', trigger: 'blur'}],
        days: [{required: true, message: '请输入期限', trigger: 'blur'}]
      },
      studentInfo: {},
      violationCount: 0
    };
  },
  methods: {
    getDataList() {
      this.dataListLoading = true;
      const params = {
        pageNum: this.current,
        pageSize: this.pageSize,
      };

      if (this.query.studentNumber) {
        params.studentNumber = this.query.studentNumber;
      }
      if (this.query.status !== null && this.query.status !== '') {
        params.status = this.query.status;
      }

      request.get('/blacklist/page', {params}).then((resp) => {
        if (resp.code === 200) {
          this.dataList = resp.data.list;
          this.totalPage = resp.data.total;
        } else {
          this.dataList = [];
          this.$message.error(resp.message || '获取数据失败');
        }
        this.dataListLoading = false;
      }).catch(err => {
        console.error('获取黑名单错误:', err);
        this.dataList = [];
        this.dataListLoading = false;
        this.$message.error('获取数据失败，请稍后重试');
      });
    },
    resetSearch() {
      this.query = {
        studentNumber: '',
        status: null
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
    handleAdd() {
      this.addDialogVisible = true;
    },
    getStudentInfo() {
      if (this.addForm.studentNumber) {
        request.get('/student/getByNumber', {
          params: {studentNumber: this.addForm.studentNumber}
        }).then(resp => {
          if (resp.code === 200 && resp.data) {
            this.studentInfo = resp.data;
            // 获取违规次数
            this.getViolationCount(resp.data.id);
          } else {
            this.studentInfo = {};
            this.violationCount = 0;
            this.$message.warning('未找到该学生');
          }
        });
      }
    },
    getViolationCount(studentId) {
      request.get(`/violation/monthCount/${studentId}`).then(resp => {
        if (resp.code === 200) {
          this.violationCount = resp.data;
        } else {
          this.violationCount = 0;
        }
      }).catch(() => {
        this.violationCount = 0;
      });
    },
    submitAdd() {
      this.$refs.addForm.validate(valid => {
        if (valid) {
          // 计算结束时间
          const endTime = new Date();
          endTime.setDate(endTime.getDate() + this.addForm.days);
          
          request.post('/blacklist/add', {
            studentId: this.studentInfo.id,
            reason: this.addForm.reason,
            endTime: formatDateTime(endTime),
            adminId: JSON.parse(localStorage.getItem("user")).id
          }).then(resp => {
            if (resp.code === 200) {
              this.$message.success('添加成功');
              this.addDialogVisible = false;
              this.getDataList();
            } else {
              this.$message.error(resp.message || '添加失败');
            }
          });
        }
      });
    },
    resetAddForm() {
      this.$refs.addForm.resetFields();
      this.studentInfo = {};
      this.violationCount = 0;
    },
    handleRemove(id) {
      this.$confirm('确认解除该学生的黑名单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.put(`/blacklist/remove/${id}`).then(resp => {
          if (resp.code === 200) {
            this.$message.success('解除成功');
            this.getDataList();
          } else {
            this.$message.error(resp.message || '解除失败');
          }
        });
      }).catch(() => {});
    },
    handleDelete(id) {
      this.$confirm('确认删除该记录?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/blacklist/delete/${id}`).then(resp => {
          if (resp.code === 200) {
            this.$message.success('删除成功');
            this.getDataList();
          } else {
            this.$message.error(resp.message || '删除失败');
          }
        });
      }).catch(() => {});
    }
  },
  created() {
    this.getDataList();
  }
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
