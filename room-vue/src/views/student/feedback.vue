<!--学生提交投诉与评价-->
<template>
  <div class="container">
    <el-card class="submit-card">
      <div slot="header">
        <span style="font-size: 18px; font-weight: bold;">提交投诉/评价</span>
      </div>
      <el-form :model="submitForm" :rules="submitFormRules" ref="submitForm" label-width="100px" style="max-width: 600px;">
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="submitForm.type">
            <el-radio :label="0">投诉</el-radio>
            <el-radio :label="1">评价</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="submitForm.title" placeholder="请输入标题"></el-input>
        </el-form-item>
        <el-form-item label="关联自习室">
          <el-select v-model="submitForm.roomId" placeholder="请选择自习室（可选）" style="width: 100%;" clearable>
            <el-option v-for="item in roomList" :key="item.id" :label="item.roomNumber + ' - ' + item.location"
                       :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" v-model="submitForm.content" placeholder="请详细描述您的问题或建议"
                    :rows="8"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
          <el-button @click="resetSubmitForm">重置</el-button>
          <el-button @click="viewMyFeedback">查看我的反馈</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  data() {
    return {
      submitForm: {
        type: 0,
        title: '',
        content: '',
        roomId: null
      },
      submitFormRules: {
        type: [{required: true, message: '请选择类型', trigger: 'change'}],
        title: [{required: true, message: '请输入标题', trigger: 'blur'}],
        content: [{required: true, message: '请输入内容', trigger: 'blur'}]
      },
      submitting: false,
      roomList: []
    };
  },
  methods: {
    handleSubmit() {
      this.$refs.submitForm.validate(valid => {
        if (valid) {
          this.submitting = true;
          request.post('/feedback/add', {
            studentId: JSON.parse(localStorage.getItem("user")).id,
            type: this.submitForm.type,
            title: this.submitForm.title,
            content: this.submitForm.content,
            roomId: this.submitForm.roomId
          }).then(resp => {
            if (resp.code === 200) {
              this.$message.success('提交成功');
              this.resetSubmitForm();
              // 1.5秒后跳转到我的反馈页面
              setTimeout(() => {
                this.$router.push('/feedbackList');
              }, 1500);
            } else {
              this.$message.error(resp.message || '提交失败');
            }
            this.submitting = false;
          }).catch(err => {
            console.error('提交反馈错误:', err);
            this.$message.error('提交失败，请稍后重试');
            this.submitting = false;
          });
        }
      });
    },
    resetSubmitForm() {
      this.$refs.submitForm.resetFields();
    },
    viewMyFeedback() {
      this.$router.push('/feedbackList');
    },
    getRoomList() {
      request.get('/studyRooms/list').then(resp => {
        if (resp.code === 200) {
          this.roomList = resp.data;
        }
      });
    }
  },
  created() {
    this.getRoomList();
  }
}
</script>

<style scoped>
.container {
  padding: 20px;
  display: flex;
  justify-content: center;
}

.submit-card {
  width: 100%;
  max-width: 800px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>
