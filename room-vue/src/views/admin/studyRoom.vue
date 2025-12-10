<template>
  <div class="container">
    <div class="action-bar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增</el-button>
    </div>
    <div class="table-container">
      <el-table
          :data="dataList"
          border
          stripe
          v-loading="dataListLoading"
          highlight-current-row
          align-content: center>
        <el-table-column type="index" min-width="100" header-align="center" align="center"
                         label="序号"></el-table-column>
        <el-table-column prop="roomNumber" header-align="center" align="center" min-width="150" label="自习室编号">
        </el-table-column>

        <el-table-column prop="location" header-align="center" align="center" min-width="180" label="位置">
        </el-table-column>
        <el-table-column prop="capacity" header-align="center" align="center" min-width="180" label="可容纳人数"
                         heigh="200">
        </el-table-column>
        <el-table-column prop="reservedNumber" header-align="center" align="center" min-width="180"
                         label="已预约人数">
        </el-table-column>
        <el-table-column prop="openingTime" header-align="center" align="center" min-width="150" label="开放时间">
          <template v-slot="scope">
            {{ scope.row.openingTime || '--' }} ~ {{ scope.row.closingTime || '--' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" header-align="center" align="center" min-width="150" label="状态">
          <template v-slot="scope">
            {{ scope.row.status === 0 ? '使用中' : '不可使用' }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" header-align="center" align="center" width="220" label="操作">
          <template v-slot="scope">
            <el-button type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="warning" v-if="scope.row.status === 0" @click="check(scope.row.id,1)">禁用</el-button>
            <el-button type="success" v-if="scope.row.status === 1" @click="check(scope.row.id,0)">启用</el-button>
            <el-button type="danger" @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <el-pagination
        @size-change="sizeChangeHandle"
        @current-change="currentChangeHandle"
        :current-page="current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        :total="totalPage"
        layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="30%">
      <el-form :model="form" label-width="100px" ref="form" :rules="rules">
        <el-form-item label="自习室编号" prop="roomNumber">
          <el-input v-model="form.roomNumber"></el-input>
        </el-form-item>
        <el-form-item label="可容纳人数" prop="capacity">
          <el-input v-model="form.capacity"></el-input>
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location"></el-input>
        </el-form-item>
        <el-form-item label="开放时间" prop="openingTime">
          <el-time-select
              v-model="form.openingTime"
              :picker-options="{
              start: '06:00',
              step: '00:30',
              end: '23:30'
            }"
              placeholder="选择开放时间">
          </el-time-select>
        </el-form-item>
        <el-form-item label="关闭时间" prop="closingTime">
          <el-time-select
              v-model="form.closingTime"
              :picker-options="{
              start: '06:00',
              step: '00:30',
              end: '23:30',
              minTime: form.openingTime
            }"
              placeholder="选择关闭时间">
          </el-time-select>
        </el-form-item>
        <el-form-item label="状态" v-if="form.id === null">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="可用" value="0"></el-option>
            <el-option label="禁用" value="1"></el-option>
          </el-select>
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
        roomNumber: '',
        capacity: '',
        location: '',
        status: '',
        openingTime: '',
        closingTime: ''
      },
      rules: {
        roomNumber: [{required: true, message: '请输入自习室编号', trigger: 'blur'}],
        capacity: [{required: true, message: '请输入可容纳人数', trigger: 'blur'}],
        location: [{required: true, message: '请输入位置', trigger: 'blur'}],
        openingTime: [{required: true, message: '请选择开放时间', trigger: 'change'}],
        closingTime: [{required: true, message: '请选择关闭时间', trigger: 'change'}]
      }
    };
  },
  methods: {
    // 获取表格数据
    getDataList() {
      this.dataListLoading = true;
      request.get('studyRooms/page', {
        params: {
          pageNum: this.current,
          pageSize: this.pageSize
        }
      }).then((resp) => {
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
        request.delete("/studyRooms/delete/" + id).then(res => {
          if (res.code === 200) {
            this.$notify.success('删除成功');
            this.getDataList();
          } else {
            this.$notify.error(res.message);
          }
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    // 修改状态
    check(id,status) {
      const data = {
        id: id,
        status: status
      };
      request.put('/studyRooms/update', data).then(res => {
        if (res.code === 200) {
          this.$notify.success('修改成功');
          this.getDataList();
        } else {
          this.$notify.error(res.message);
        }
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
      this.dialogTitle = '新增自习室';
      this.dialogVisible = true;
      this.resetForm();
    },
    // 编辑按钮点击事件
    handleEdit(row) {
      this.dialogTitle = '编辑自习室';
      this.dialogVisible = true;
      this.form = {...row}; // 将当前行的数据赋值给表单
    },
    // 提交表单
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.id) {
            // 编辑
            request.put("/studyRooms/update", this.form).then(res => {
              if (res.code === 200) {
                this.$notify.success('修改成功');
                this.dialogVisible = false;
                this.getDataList();
              } else {
                this.$notify.error(res.message);
              }
            });
          } else {
            // 新增
            request.post("/studyRooms/add", this.form).then(res => {
              if (res.code === 200) {
                this.$notify.success('新增成功');
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
        roomNumber: '',
        capacity: '',
        location: '',
        openingTime: '',
        closingTime: ''
      };
    }
  },
  created() {
    this.getDataList();
  }
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