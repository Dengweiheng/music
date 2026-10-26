<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-visual">
        <div class="visual-content">
          <el-icon :size="48" color="#fff"><Headset /></el-icon>
          <h1>MusicBox</h1>
          <p>开启你的专属音乐空间</p>
        </div>
      </div>
      
      <div class="auth-form-container">
        <div class="form-header">
          <h2>创建账号</h2>
          <p>只需几秒，开启视听之旅</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" class="modern-form">
          <el-form-item prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="设置用户名" 
              prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="nickname">
            <el-input 
              v-model="form.nickname" 
              placeholder="取个好听的昵称" 
              prefix-icon="Star"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="form.password" 
              type="password" 
              show-password 
              placeholder="设置密码" 
              prefix-icon="Lock"
            />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="form.confirmPassword" 
              type="password" 
              show-password 
              placeholder="再次确认密码" 
              prefix-icon="Check"
            />
          </el-form-item>

          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleRegister">
            立即注册
          </el-button>
        </el-form>

        <div class="auth-footer">
          已有账号？ <el-link type="primary" @click="$router.push('/login')">登录账号</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') callback(new Error('请再次输入密码'))
  else if (value !== form.password) callback(new Error('两次输入密码不一致!'))
  else callback()
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await request.post('/user/register', {
          username: form.username,
          password: form.password,
          nickname: form.nickname
        })
        ElMessage.success('注册成功，欢迎加入')
        router.push('/login')
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
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
  background-image: linear-gradient(rgba(0,0,0,0.2), rgba(0,0,0,0.2)), url('https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=1000&auto=format&fit=crop');
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
  margin-bottom: 32px;
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

.submit-btn {
  width: 100%;
  height: 52px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  background-color: #1d1d1f;
  border: none;
  margin-top: 16px;
}

.submit-btn:hover {
  background-color: #000;
}

.auth-footer {
  text-align: center;
  margin-top: auto;
  font-size: 14px;
  color: #86868b;
}
</style>
