<template>
  <div class="container">
    <el-card class="submit-card">
      <div slot="header">
        <span style="font-size: 18px; font-weight: bold;">提交投诉/评价</span>
        <el-tag type="info" style="margin-left: 15px;">纯前端演示模式</el-tag>
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
      roomList: [],
      // 本地存储的反馈数据
      localFeedbackData: []
    };
  },
  created() {
    this.getRoomList();
    this.loadLocalFeedbackData();
  },
  methods: {
    // 模拟获取自习室列表 - 不使用后端
    getRoomList() {
      // 使用模拟数据
      const mockRooms = [
        { id: 1, roomNumber: '101', location: 'A栋1楼' },
        { id: 2, roomNumber: '102', location: 'A栋1楼' },
        { id: 3, roomNumber: '201', location: 'B栋2楼' },
        { id: 4, roomNumber: '202', location: 'B栋2楼' },
        { id: 5, roomNumber: '301', location: 'C栋3楼' },
        { id: 6, roomNumber: '302', location: 'C栋3楼' },
        { id: 7, roomNumber: '401', location: 'D栋4楼' },
        { id: 8, roomNumber: '402', location: 'D栋4楼' }
      ];

      // 模拟网络延迟
      setTimeout(() => {
        this.roomList = mockRooms;
        console.log('加载自习室列表（模拟数据）:', mockRooms);
      }, 300);
    },

    // 提交反馈 - 全部在前端处理
    handleSubmit() {
      this.$refs.submitForm.validate(valid => {
        if (valid) {
          this.submitting = true;

          // 模拟网络请求延迟
          setTimeout(() => {
            try {
              // 获取当前用户（模拟）
              const currentUser = JSON.parse(localStorage.getItem("user") || "{}") || {
                id: 10001,
                name: '张三',
                studentId: '20230001'
              };

              // 创建反馈对象
              const feedback = {
                id: Date.now(), // 使用时间戳作为唯一ID
                studentId: currentUser.id,
                studentName: currentUser.name || '匿名用户',
                studentNumber: currentUser.studentId || '未设置',
                type: this.submitForm.type,
                title: this.submitForm.title,
                content: this.submitForm.content,
                roomId: this.submitForm.roomId,
                roomInfo: this.getRoomInfo(this.submitForm.roomId),
                status: 0, // 0-待处理, 1-处理中, 2-已解决
                createTime: new Date().toLocaleString(),
                updateTime: new Date().toLocaleString()
              };

              // 保存到本地存储
              this.saveFeedbackToLocal(feedback);

              // 显示成功消息
              this.$message({
                message: '提交成功！反馈已保存到本地（不与后端连接）',
                type: 'success',
                duration: 3000
              });

              // 重置表单
              this.resetSubmitForm();
              this.submitting = false;

              // 1.5秒后提示可查看反馈
              setTimeout(() => {
                this.$message.info('点击"查看我的反馈"按钮可以查看所有提交的反馈');
              }, 1500);

            } catch (error) {
              console.error('提交失败:', error);
              this.$message.error('提交失败：' + error.message);
              this.submitting = false;
            }
          }, 800); // 模拟800ms网络延迟
        }
      });
    },

    // 获取自习室信息
    getRoomInfo(roomId) {
      if (!roomId) return null;
      const room = this.roomList.find(item => item.id === roomId);
      return room ? `${room.roomNumber} - ${room.location}` : null;
    },

    // 保存反馈到本地存储
    saveFeedbackToLocal(feedback) {
      try {
        // 从本地存储加载现有数据
        let feedbacks = JSON.parse(localStorage.getItem('localFeedbacks') || '[]');

        // 添加新反馈
        feedbacks.unshift(feedback); // 添加到开头，最新在最前

        // 保存回本地存储
        localStorage.setItem('localFeedbacks', JSON.stringify(feedbacks));

        // 更新组件数据
        this.localFeedbackData = feedbacks;

        console.log('反馈已保存到本地:', feedback);
        return true;
      } catch (error) {
        console.error('保存到本地存储失败:', error);
        throw new Error('保存失败，请检查本地存储');
      }
    },

    // 从本地存储加载反馈数据
    loadLocalFeedbackData() {
      try {
        const feedbacks = JSON.parse(localStorage.getItem('localFeedbacks') || '[]');
        this.localFeedbackData = feedbacks;
        console.log('从本地存储加载反馈数据:', feedbacks.length, '条');
      } catch (error) {
        console.error('加载本地反馈数据失败:', error);
        this.localFeedbackData = [];
      }
    },

    // 重置表单
    resetSubmitForm() {
      this.$refs.submitForm.resetFields();
      this.$message.info('表单已重置');
    },

    // 查看我的反馈 - 显示本地数据
    viewMyFeedback() {
      if (this.localFeedbackData.length === 0) {
        this.$alert('暂无反馈数据，请先提交反馈。', '我的反馈', {
          confirmButtonText: '确定',
          callback: action => {
            // 可以在这里添加跳转到反馈列表页面的逻辑
            // this.$router.push('/feedbackList');
          }
        });
      } else {
        // 创建反馈列表的HTML内容
        const feedbackHtml = this.createFeedbackHtml();

        // 使用自定义对话框样式
        const dialogStyle = `
          <style>
            .custom-feedback-dialog {
              width: 700px;
              max-width: 90%;
            }
            .custom-feedback-dialog .el-message-box__content {
              max-height: 500px;
              overflow: hidden;
            }
          </style>
        `;

        this.$alert(feedbackHtml, `我的反馈（共${this.localFeedbackData.length}条）`, {
          dangerouslyUseHTMLString: true,
          showConfirmButton: true,
          showCancelButton: true,
          confirmButtonText: '清空所有反馈',
          cancelButtonText: '关闭',
          customClass: 'custom-feedback-dialog',
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              this.$confirm('确定要清空所有反馈数据吗？此操作不可恢复。', '警告', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
              }).then(() => {
                this.clearAllFeedback();
                done();
              }).catch(() => {
                // 用户取消，不关闭对话框
              });
            } else {
              done();
            }
          }
        });
      }
    },

    // 创建反馈列表的HTML
    createFeedbackHtml() {
      let html = `
        <div class="feedback-list" style="max-height: 400px; overflow-y: auto;">
          <div style="margin-bottom: 15px; color: #666; font-size: 14px;">
            所有数据仅保存在本地浏览器中，清空缓存或更换浏览器会丢失数据。
          </div>
      `;

      this.localFeedbackData.forEach((item, index) => {
        const typeText = item.type === 0 ? '投诉' : '评价';
        const typeColor = item.type === 0 ? '#f56c6c' : '#67c23a';
        const statusText = ['待处理', '处理中', '已解决'][item.status] || '未知';
        const statusColor = ['#e6a23c', '#409eff', '#67c23a'][item.status] || '#909399';

        html += `
          <div class="feedback-item" style="
            border: 1px solid #ebeef5;
            border-radius: 4px;
            padding: 12px;
            margin-bottom: 10px;
            background-color: #fafafa;
          ">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
              <div>
                <strong style="font-size: 16px;">${item.title}</strong>
                <span style="
                  background-color: ${typeColor};
                  color: white;
                  padding: 2px 8px;
                  border-radius: 10px;
                  font-size: 12px;
                  margin-left: 10px;
                ">${typeText}</span>
                <span style="
                  background-color: ${statusColor};
                  color: white;
                  padding: 2px 8px;
                  border-radius: 10px;
                  font-size: 12px;
                  margin-left: 5px;
                ">${statusText}</span>
              </div>
              <div style="font-size: 12px; color: #909399;">${item.createTime}</div>
            </div>

            <div style="margin-bottom: 8px; color: #606266;">
              ${item.content}
            </div>

            <div style="display: flex; justify-content: space-between; font-size: 12px; color: #909399;">
              <div>
                ${item.roomInfo ? `关联自习室：${item.roomInfo}` : '未关联自习室'}
              </div>
              <div>
                提交人：${item.studentName} (${item.studentNumber})
              </div>
            </div>
          </div>
        `;
      });

      html += `</div>`;
      return html;
    },

    // 清空所有反馈数据
    clearAllFeedback() {
      try {
        localStorage.removeItem('localFeedbacks');
        this.localFeedbackData = [];
        this.$message.success('所有反馈数据已清空');
      } catch (error) {
        this.$message.error('清空数据失败：' + error.message);
      }
    }
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

/* 如果使用 Vue 2，深度选择器的写法 */
/* /deep/ .feedback-dialog {
  width: 700px;
  max-width: 90%;
} */

/* 如果使用 Vue 2.7+ 或 Vue 3 */
/* ::v-deep .feedback-dialog {
  width: 700px;
  max-width: 90%;
} */

/* 最安全的写法：使用内联样式或移除深度选择器 */
/* 因为对话框是动态创建的，我们可以通过 customClass 属性来添加样式 */
</style>

<style>
/* 全局样式 - 用于对话框 */
.custom-feedback-dialog {
  width: 700px !important;
  max-width: 90% !important;
}

.custom-feedback-dialog .el-message-box__content {
  max-height: 500px !important;
  overflow: hidden !important;
}
</style>