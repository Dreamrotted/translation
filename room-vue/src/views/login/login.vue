<template>
  <div class="login-container">
    <div class="login-card">
      <div class="title">登 录</div>
      <el-form :model="user" :rules="rules" ref="loginForm">
        <el-form-item prop="account" label="账号">
          <el-input placeholder="请输入账号" prefix-icon="el-icon-user" v-model="user.account"></el-input>
        </el-form-item>
        <el-form-item prop="password" label="密码">
          <el-input placeholder="请输入密码" show-password prefix-icon="el-icon-lock"
                    v-model="user.password"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="user.role">
            <el-radio label="student">学生</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-button class="login-button" size="medium" type="primary" @click="login">登录</el-button>
        <div class="register-link">
          没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import el from "element-ui/src/locale/lang/el";

export default {
  name: 'Login',
  data() {
    return {
      user: {
        account: '',
        password: '',
        role: ''
      },
      rules: {
        account: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 3, max: 10, message: '长度在3-10个字符', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 3, max: 10, message: '长度在3-10个字符', trigger: 'blur'}
        ],
        role: [
          {required: true, message: '请选择角色', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    login() {
      this.$refs['loginForm'].validate((valid) => {
        if (valid) {
          request.post('/login', this.user).then(res => {
            if (res.code === 200) {
              this.$notify.success("登录成功")
              localStorage.setItem('user', JSON.stringify(res.data.user))
              localStorage.setItem('token', res.data.token)
              localStorage.setItem('role', res.data.role)
              if (res.data.role === 'student') {
                this.$router.push('/reservationRoom')
              } else {
                this.$router.push('/dashboard')
              }
            } else {
              this.$notify.error(res.message)
            }
          }).catch(() => {
            this.$notify.error("登录失败")
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.login-card {
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

.login-button {
  width: 100%;
  margin-top: 20px;
  border-radius: 20px;
  background-color: #2575fc;
  border: none;
}

.login-button:hover {
  background-color: #1a5bbf;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-input {
  border-radius: 20px;
}

.register-link {
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.register-link a {
  color: #2575fc;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>