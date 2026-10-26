<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-visual">
        <div class="visual-content">
          <el-icon :size="48" color="#fff"><Headset /></el-icon>
          <h1>MusicBox</h1>
          <p>遇见你的下一首最爱</p>
        </div>
      </div>
      
      <div class="auth-form-container">
        <div class="form-header">
          <h2>账号登录</h2>
          <p>欢迎回到 MusicBox</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" class="modern-form">
          <el-form-item prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="用户名 / 手机号" 
              prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="form.password" 
              type="password" 
              show-password 
              placeholder="请输入密码" 
              prefix-icon="Lock"
            />
          </el-form-item>
          
          <div class="form-utils">
            <el-checkbox v-model="remember">自动登录</el-checkbox>
            <el-link type="info" underline="never" @click="forgotVisible = true">忘记密码？</el-link>
          </div>

          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">
            立即登录
          </el-button>
        </el-form>

        <div class="auth-footer">
          没有账号？ <el-link type="primary" @click="$router.push('/register')">点击注册</el-link>
        </div>
      </div>
    </div>

    <!-- 忘记密码弹窗 -->
    <el-dialog v-model="forgotVisible" title="找回密码" width="400px">
      <el-form :model="forgotForm" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="forgotForm.username" placeholder="请输入您的用户名" />
        </el-form-item>
        <el-form-item label="验证昵称">
          <el-input v-model="forgotForm.nickname" placeholder="请输入该账户的昵称以验证身份" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="forgotForm.newPassword" type="password" show-password placeholder="请输入新的密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="forgotVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReset">重置密码</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import request from '@/api'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const remember = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const forgotVisible = ref(false)
const forgotForm = reactive({ username: '', nickname: '', newPassword: '' })

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/user/login', form)
        userStore.setToken(res.token)
        userStore.setUserInfo(res.user)
        ElMessage.success('登录成功')
        if (res.user.role === 1) router.push('/admin/music')
        else router.push('/')
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReset = async () => {
  if (!forgotForm.username || !forgotForm.nickname || !forgotForm.newPassword) {
    ElMessage.warning('请补全所有信息')
    return
  }
  try {
    await request.post('/user/reset-password', forgotForm)
    ElMessage.success('密码已成功重置')
    forgotVisible.value = false
  } catch (e) {}
}
</script>

<style scoped>
.auth-page {
  height: 100vh;
  width: 100vw;
  background-color: #f5f5f7;
  display: flex;
  align-items: center;
  justify-content: center;
}

.auth-card {
  width: 900px;
  height: 600px;
  background-color: #ffffff;
  border-radius: 32px;
  display: flex;
  overflow: hidden;
  box-shadow: 0 40px 100px rgba(0,0,0,0.08);
}

.auth-visual {
  flex: 1;
  background: #1d1d1f;
  background-image: linear-gradient(rgba(0,0,0,0.1), rgba(0,0,0,0.1)), url('https://images.unsplash.com/photo-1459749411177-042180ce673c?q=80&w=1000&auto=format&fit=crop');
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.visual-content {
  text-align: center;
  color: #fff;
}

.visual-content h1 {
  font-size: 32px;
  font-weight: 800;
  margin: 16px 0 8px 0;
}

.visual-content p {
  font-size: 16px;
  opacity: 0.8;
}

.auth-form-container {
  flex: 1;
  padding: 60px;
  display: flex;
  flex-direction: column;
}

.form-header {
  margin-bottom: 40px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 800;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.form-header p {
  color: #86868b;
  font-size: 15px;
}

:deep(.el-input__wrapper) {
  background-color: #f5f5f7 !important;
  box-shadow: none !important;
  border: 1px solid transparent;
  border-radius: 14px;
  height: 52px;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #1d1d1f;
  background-color: #fff !important;
}

.form-utils {
  display: flex;
  justify-content: space-between;
  margin: 8px 0 32px 0;
}

.submit-btn {
  width: 100%;
  height: 52px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  background-color: #1d1d1f;
  border: none;
}

.submit-btn:hover {
  background-color: #000;
}

.auth-footer {
  text-align: center;
  margin-top: 40px;
  font-size: 14px;
  color: #86868b;
}
</style>
