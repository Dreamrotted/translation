<template>
  <div class="container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true">
        <el-form-item label="自习室">
          <el-select v-model="searchForm.roomId" placeholder="请选择自习室" clearable @change="getDataList">
            <el-option
                v-for="room in roomList"
                :key="room.id"
                :label="room.roomNumber + ' - ' + room.location"
                :value="room.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="座位编号">
          <el-input v-model="searchForm.seatNumber" placeholder="请输入座位编号" clearable @keyup.enter.native="getDataList"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="getDataList">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增座位</el-button>
      <el-button type="success" icon="el-icon-magic-stick" @click="handleBatchGenerate">批量生成</el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
          :data="dataList"
          border
          stripe
          v-loading="dataListLoading"
          highlight-current-row>
        <el-table-column type="index" min-width="80" header-align="center" align="center" label="序号"></el-table-column>
        <el-table-column prop="seatNumber" header-align="center" align="center" min-width="120" label="座位编号"></el-table-column>
        <el-table-column header-align="center" align="center" min-width="150" label="所属自习室">
          <template v-slot="scope">
            {{ scope.row.studyRooms ? scope.row.studyRooms.roomNumber : '--' }}
          </template>
        </el-table-column>
        <el-table-column header-align="center" align="center" min-width="150" label="位置">
          <template v-slot="scope">
            {{ scope.row.studyRooms ? scope.row.studyRooms.location : '--' }}
          </template>
        </el-table-column>
        <el-table-column prop="seatType" header-align="center" align="center" min-width="120" label="座位类型"></el-table-column>
        <el-table-column prop="status" header-align="center" align="center" min-width="100" label="状态">
          <template v-slot="scope">
            <el-tag v-if="scope.row.status === 0" type="success">可用</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="warning">维修中</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" header-align="center" align="center" width="200" label="操作">
          <template v-slot="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px" ref="form" :rules="rules">
        <el-form-item label="自习室" prop="roomId">
          <el-select v-model="form.roomId" placeholder="请选择自习室" style="width: 100%">
            <el-option
                v-for="room in roomList"
                :key="room.id"
                :label="room.roomNumber + ' - ' + room.location"
                :value="room.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="座位编号" prop="seatNumber">
          <el-input v-model="form.seatNumber" placeholder="请输入座位编号"></el-input>
        </el-form-item>
        <el-form-item label="座位类型" prop="seatType">
          <el-select v-model="form.seatType" placeholder="请选择座位类型" style="width: 100%">
            <el-option label="普通座位" value="普通座位"></el-option>
            <el-option label="插座座位" value="插座座位"></el-option>
            <el-option label="静音区" value="静音区"></el-option>
            <el-option label="讨论区" value="讨论区"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="可用" :value="0"></el-option>
            <el-option label="维修中" :value="1"></el-option>
            <el-option label="禁用" :value="2"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 批量生成弹窗 -->
    <el-dialog title="批量生成座位" :visible.sync="batchDialogVisible" width="500px">
      <el-form :model="batchForm" label-width="100px" ref="batchForm" :rules="batchRules">
        <el-form-item label="自习室" prop="roomId">
          <el-select v-model="batchForm.roomId" placeholder="请选择自习室" style="width: 100%">
            <el-option
                v-for="room in roomList"
                :key="room.id"
                :label="room.roomNumber + ' - ' + room.location"
                :value="room.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="生成数量" prop="count">
          <el-input-number v-model="batchForm.count" :min="1" :max="200" placeholder="请输入生成数量" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="编号前缀" prop="prefix">
          <el-input v-model="batchForm.prefix" placeholder="如：A、B、1A（可选）"></el-input>
        </el-form-item>
        <el-form-item label="座位类型" prop="seatType">
          <el-select v-model="batchForm.seatType" placeholder="请选择座位类型" style="width: 100%">
            <el-option label="普通座位" value="普通座位"></el-option>
            <el-option label="插座座位" value="插座座位"></el-option>
            <el-option label="静音区" value="静音区"></el-option>
            <el-option label="讨论区" value="讨论区"></el-option>
          </el-select>
        </el-form-item>
        <el-alert
            title="提示：将自动生成座位编号，如前缀为A，数量为10，则生成A001-A010"
            type="info"
            :closable="false"
            show-icon>
        </el-alert>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="batchDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitBatchForm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  data() {
    return {
      dataList: [],
      current: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      
      searchForm: {
        roomId: null,
        seatNumber: ''
      },
      
      roomList: [], // 自习室列表
      
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        roomId: null,
        seatNumber: '',
        seatType: '普通座位',
        status: 0
      },
      rules: {
        roomId: [{required: true, message: '请选择自习室', trigger: 'change'}],
        seatNumber: [{required: true, message: '请输入座位编号', trigger: 'blur'}],
        seatType: [{required: true, message: '请选择座位类型', trigger: 'change'}],
        status: [{required: true, message: '请选择状态', trigger: 'change'}]
      },
      
      batchDialogVisible: false,
      batchForm: {
        roomId: null,
        count: 10,
        prefix: '',
        seatType: '普通座位'
      },
      batchRules: {
        roomId: [{required: true, message: '请选择自习室', trigger: 'change'}],
        count: [{required: true, message: '请输入生成数量', trigger: 'blur'}],
        seatType: [{required: true, message: '请选择座位类型', trigger: 'change'}]
      }
    };
  },
  methods: {
    // 获取表格数据
    getDataList() {
      this.dataListLoading = true;
      request.get('seat/page', {
        params: {
          pageNum: this.current,
          pageSize: this.pageSize,
          roomId: this.searchForm.roomId,
          seatNumber: this.searchForm.seatNumber
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
    
    // 获取自习室列表
    getRoomList() {
      request.get('studyRooms/page', {
        params: {
          pageNum: 1,
          pageSize: 999
        }
      }).then((resp) => {
        if (resp.code === 200) {
          this.roomList = resp.data.list;
        }
      });
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        roomId: null,
        seatNumber: ''
      };
      this.current = 1;
      this.getDataList();
    },
    
    // 新增
    handleAdd() {
      this.dialogTitle = '新增座位';
      this.dialogVisible = true;
      this.resetForm();
    },
    
    // 编辑
    handleEdit(row) {
      this.dialogTitle = '编辑座位';
      this.dialogVisible = true;
      this.form = {...row};
    },
    
    // 删除
    handleDelete(id) {
      this.$confirm('此操作将永久删除该座位, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete("/seat/delete/" + id).then(res => {
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
    
    // 提交表单
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.id) {
            // 编辑
            request.put("/seat/update", this.form).then(res => {
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
            request.post("/seat/add", this.form).then(res => {
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
    
    // 批量生成
    handleBatchGenerate() {
      this.batchDialogVisible = true;
      this.resetBatchForm();
    },
    
    // 提交批量生成
    submitBatchForm() {
      this.$refs.batchForm.validate(valid => {
        if (valid) {
          request.post("/seat/batchGenerate", this.batchForm).then(res => {
            if (res.code === 200) {
              this.$notify.success('批量生成成功');
              this.batchDialogVisible = false;
              this.getDataList();
            } else {
              this.$notify.error(res.message);
            }
          });
        }
      });
    },
    
    // 重置表单
    resetForm() {
      this.form = {
        id: null,
        roomId: null,
        seatNumber: '',
        seatType: '普通座位',
        status: 0
      };
    },
    
    // 重置批量生成表单
    resetBatchForm() {
      this.batchForm = {
        roomId: null,
        count: 10,
        prefix: '',
        seatType: '普通座位'
      };
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
    }
  },
  created() {
    this.getDataList();
    this.getRoomList();
  }
};
</script>

<style scoped>
.container {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}

.action-bar {
  margin-bottom: 20px;
}

.table-container {
  overflow-x: auto;
}
</style>
