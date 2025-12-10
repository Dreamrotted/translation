<template>
  <div class="container">
    <div class="action-bar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增</el-button>
    </div>

    <div class="table-container">
      <el-table
          :data="dataList"
          v-loading="dataListLoading"
          stripe
          border
          highlight-current-row
      >
        <el-table-column type="index" min-width="80" header-align="center" align="center" label="序号"></el-table-column>
        <el-table-column prop="studentNumber" min-width="120" header-align="center" align="center" label="学号">
        </el-table-column>
<!--        <el-table-column prop="password" min-width="200" header-align="center" align="center" label="密码">-->
<!--        </el-table-column>-->
        <el-table-column prop="studentName" min-width="120" header-align="center" align="center" label="姓名">
        </el-table-column>
        <el-table-column prop="gender" min-width="80" header-align="center" align="center" label="性别">
          <template v-slot="scope">
            {{ scope.row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="majorName" min-width="120" header-align="center" align="center" label="专业">
        </el-table-column>
        <el-table-column min-width="180" header-align="center" align="center" label="操作">
          <template v-slot="scope">
            <el-button type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <el-pagination
        style="margin-top: 20px; text-align: center"
        @size-change="sizeChangeHandle"
        @current-change="currentChangeHandle"
        :current-page="current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        :total="totalPage"
        layout="total, sizes, prev, pager, next, jumper"
    ></el-pagination>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="30%">
      <el-form :model="form" label-width="80px" ref="form" :rules="rules">
        <el-form-item label="学号" prop="studentNumber">
          <el-input v-model="form.studentNumber"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="studentName">
          <el-input v-model="form.studentName"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="专业" prop="majorName">
          <el-input v-model="form.majorName"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import request from "@/utils/request";

export default {
  data() {
    return {
      dataList: [], // 表格数据
      current: 1, // 当前页码
      pageSize: 10, // 每页条数
      totalPage: 0, // 总条数
      dataListLoading: false, // 加载状态

      dialogVisible: false, // 弹窗显示状态
      dialogTitle: '', // 弹窗标题
      form: {
        id: null,
        studentNumber: '',
        studentName: '',
        gender: 1,
        majorName: '',
        password:''
      },
      rules: {
        studentNumber: [{ required: true, message: '请输入学号', trigger: 'blur' }],
        studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
        majorName: [{ required: true, message: '请输入专业', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    };
  },
  created() {
    this.getDataList();
  },
  methods: {
    // 获取表格数据
    getDataList() {
      this.dataListLoading = true;
      request
          .get("student/page", {
            params: {
              pageNum: this.current,
              pageSize: this.pageSize,
            },
          })
          .then((resp) => {
            if (resp.code === 200) {
              this.dataList = resp.data.list;
              this.totalPage = resp.data.total;
            } else {
              this.dataList = [];
            }
            this.dataListLoading = false;
          });
    },
    // 删除数据
    del(id) {
      this.$confirm('此操作将永久删除该数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete("/student/delete/" + id).then(res => {
          if (res.code === 200) {
            this.$notify.success('删除成功')
            this.getDataList()
          } else {
            this.$notify.error(res.message)
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    // 分页大小变化
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.current = 1;
      this.getDataList();
    },
    // 当前页码变化
    currentChangeHandle(val) {
      this.current = val;
      this.getDataList();
    },
    // 新增按钮点击事件
    handleAdd() {
      this.dialogTitle = '新增学生';
      this.dialogVisible = true;
      this.resetForm();
    },
    // 编辑按钮点击事件
    handleEdit(row) {
      this.dialogTitle = '编辑学生';
      this.dialogVisible = true;
      this.form = { ...row };
    },
    // 提交表单
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.id) {
            // 编辑
            request.put("/student/update", this.form).then(res => {
              if (res.code === 200) {
                this.$notify.success("修改成功");
                this.dialogVisible = false;
                this.getDataList();
              } else {
                this.$notify.error(res.message);
              }
            });
          } else {
            // 新增
            request.post("/student/add", this.form).then(res => {
              if (res.code === 200) {
                this.$notify.success("新增成功");
                this.dialogVisible = false;
                this.getDataList();
              } else {
                this.$notify.error(res.message);
              }
            });
          }
        }
      });
    },
    // 重置表单
    resetForm() {
      this.form = {
        id: null,
        studentNumber: '',
        studentName: '',
        gender: 1,
        majorName: ''
      };
    }
  },
};
</script>
<style scoped>
.container {
  padding: 20px;
}

.action-bar {
  margin-bottom: 20px;
}

.table-container {
  overflow-x: auto;
}
</style>