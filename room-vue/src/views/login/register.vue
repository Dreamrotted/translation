<template>
  <div class="register-container">
    <div class="register-card">
      <div class="title">注 册</div>
      <el-form :model="form" :rules="rules" ref="registerForm">
        <el-form-item prop="studentNumber" label="学号">
          <el-input 
            placeholder="请输入学号" 
            prefix-icon="el-icon-document" 
            v-model.number="form.studentNumber"
            type="number">
          </el-input>
        </el-form-item>
        <el-form-item prop="studentName" label="姓名">
          <el-input 
            placeholder="请输入姓名" 
            prefix-icon="el-icon-user" 
            v-model="form.studentName">
          </el-input>
        </el-form-item>
        <el-form-item prop="password" label="密码">
          <el-input 
            placeholder="请输入密码(6-20字符)" 
            show-password 
            prefix-icon="el-icon-lock"
            v-model="form.password">
          </el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword" label="确认密码">
          <el-input 
            placeholder="请再次输入密码" 
            show-password 
            prefix-icon="el-icon-lock"
            v-model="form.confirmPassword">
          </el-input>
        </el-form-item>
        <el-form-item prop="gender" label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="majorName" label="专业">
          <el-input 
            placeholder="请输入专业" 
            prefix-icon="el-icon-school" 
            v-model="form.majorName">
          </el-input>
        </el-form-item>
        <el-button class="register-button" size="medium" type="primary" @click="register">注册</el-button>
        <div class="login-link">
          已有账号？<router-link to="/login">返回登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: 'Register',
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else if (value.length < 6 || value.length > 20) {
        callback(new Error('密码长度必须在6-20个字符之间'));
      } else {
        if (this.form.confirmPassword !== '') {
          this.$refs.registerForm.validateField('confirmPassword');
        }
        callback();
      }
    };

    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.form.password) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };

    return {
      form: {
        studentNumber: '',
        studentName: '',
        password: '',
        confirmPassword: '',
        gender: 1,
        majorName: ''
      },
      rules: {
        studentNumber: [
          { required: true, message: '请输入学号', trigger: 'blur' },
          { type: 'number', message: '学号必须是数字', trigger: 'blur' }
        ],
        studentName: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 50, message: '姓名长度在2-50个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { validator: validatePassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'blur' }
        ],
        majorName: [
          { required: true, message: '请输入专业', trigger: 'blur' },
          { min: 2, max: 100, message: '专业名称长度在2-100个字符', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    register() {
      this.$refs['registerForm'].validate((valid) => {
        if (valid) {
          const registerData = {
            studentNumber: this.form.studentNumber,
            studentName: this.form.studentName,
            password: this.form.password,
            gender: this.form.gender,
            majorName: this.form.majorName
          };
          
          request.post('/register', registerData).then(res => {
            if (res.code === 200) {
              this.$notify.success("注册成功，请登录");
              setTimeout(() => {
                this.$router.push('/login');
              }, 1500);
            } else {
              this.$notify.error(res.message || '注册失败');
            }
          }).catch((error) => {
            console.error(error);
            this.$notify.error("注册失败，请稍后重试");
          });
        }
      });
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.register-card {
  background: #fff;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  text-align: center;
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.register-button {
  width: 100%;
  margin-top: 20px;
  border-radius: 20px;
  background-color: #2575fc;
  border: none;
}

.register-button:hover {
  background-color: #1a5bbf;
}

.el-form-item {
  margin-bottom: 15px;
}

.el-input {
  border-radius: 20px;
}

.login-link {
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #2575fc;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
