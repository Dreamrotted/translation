<template>
  <div class="container">
    <el-row :gutter="20">
      <!-- 左侧：个人信息 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="info-card">
          <div slot="header" class="clearfix">
            <span style="font-size: 18px; font-weight: bold;">个人信息</span>
          </div>
          <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="学号" prop="studentNumber">
              <el-input v-model="ruleForm.studentNumber" :disabled="!isEditing"></el-input>
            </el-form-item>
            <el-form-item label="姓名" prop="studentName">
              <el-input v-model="ruleForm.studentName" :disabled="!isEditing"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="ruleForm.gender" :disabled="!isEditing">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="专业" prop="majorName">
              <el-input v-model="ruleForm.majorName" :disabled="!isEditing"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="toggleEdit" v-if="!isEditing">编辑</el-button>
              <el-button type="success" @click="saveInfo" v-if="isEditing">保存</el-button>
              <el-button @click="cancel" v-if="isEditing">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧：修改密码 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="password-card">
          <div slot="header" class="clearfix">
            <span style="font-size: 18px; font-weight: bold;">修改密码</span>
          </div>
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input type="password" v-model="passwordForm.oldPassword" placeholder="请输入原密码" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword">修改密码</el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  data() {
    // 自定义验证规则 - 确认密码
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };

    return {
      ruleForm: {
        id: null,
        studentNumber: '',
        studentName: '',
        gender: 1,
        majorName: '',
      },
      rules: {
        studentNumber: [{required: true, message: '请输入学号', trigger: 'blur'}],
        studentName: [{required: true, message: '请输入姓名', trigger: 'blur'}],
        gender: [{required: true, message: '请选择性别', trigger: 'change'}],
        majorName: [{required: true, message: '请输入专业', trigger: 'blur'}],
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [{required: true, message: '请输入原密码', trigger: 'blur'}],
        newPassword: [
          {required: true, message: '请输入新密码', trigger: 'blur'},
          {min: 6, message: '密码长度不能少于6位', trigger: 'blur'}
        ],
        confirmPassword: [
          {required: true, message: '请再次输入密码', trigger: 'blur'},
          {validator: validateConfirmPassword, trigger: 'blur'}
        ]
      },
      isEditing: false,
      originalData: {} // 保存原始数据
    };
  },
  created() {
    this.fetchStudentInfo();
  },
  methods: {
    fetchStudentInfo() {
      const studentId = JSON.parse(localStorage.getItem("user")).id;
      request.get(`/student/${studentId}`).then((res) => {
        if (res.code === 200) {
          // 不包含密码字段
          this.ruleForm = {
            id: res.data.id,
            studentNumber: res.data.studentNumber,
            studentName: res.data.studentName,
            gender: res.data.gender,
            majorName: res.data.majorName
          };
          this.originalData = {...this.ruleForm}; // 保存原始数据
        } else {
          this.$message.error(res.message || '获取信息失败');
        }
      }).catch(err => {
        console.error('获取学生信息错误:', err);
        this.$message.error('获取信息失败');
      });
    },
    toggleEdit() {
      this.isEditing = true;
    },
    saveInfo() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          // 只提交基本信息，不包含密码
          request.put('/student/updateInfo', this.ruleForm).then((res) => {
            if (res.code === 200) {
              this.$notify.success('修改成功');
              this.isEditing = false;
              this.originalData = {...this.ruleForm};
              // 更新 localStorage 中的用户信息
              const user = JSON.parse(localStorage.getItem("user"));
              user.studentName = this.ruleForm.studentName;
              user.majorName = this.ruleForm.majorName;
              localStorage.setItem("user", JSON.stringify(user));
            } else {
              this.$notify.error(res.message || '修改失败');
            }
          }).catch(err => {
            console.error('修改信息错误:', err);
            this.$notify.error('修改失败');
          });
        }
      });
    },
    cancel() {
      this.isEditing = false;
      this.ruleForm = {...this.originalData}; // 恢复原始数据
      this.$refs.ruleForm.clearValidate();
    },
    changePassword() {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          const studentId = JSON.parse(localStorage.getItem("user")).id;
          request.put('/student/changePassword', {
            studentId: studentId,
            oldPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
          }).then((res) => {
            if (res.code === 200) {
              this.$notify.success('密码修改成功，请重新登录');
              this.resetPasswordForm();
              // 3秒后跳转到登录页
              setTimeout(() => {
                localStorage.clear();
                this.$router.push('/login');
              }, 3000);
            } else {
              this.$notify.error(res.message || '密码修改失败');
            }
          }).catch(err => {
            console.error('修改密码错误:', err);
            this.$notify.error('修改密码失败');
          });
        }
      });
    },
    resetPasswordForm() {
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      };
      this.$refs.passwordForm.clearValidate();
    }
  },
};
</script>

<style scoped>
.container {
  padding: 20px;
}

.info-card, .password-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  height: 100%;
}

/* 小屏幕时保持间隔 */
@media (max-width: 992px) {
  .el-col:first-child {
    margin-bottom: 20px;
  }
}
</style>
