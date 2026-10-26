<template>
  <div class="profile-page-wrapper">
    <div class="profile-container">
      <div class="profile-header">
        <el-button icon="Back" circle @click="router.back()" class="back-btn"></el-button>
        <h2>个人信息设置</h2>
      </div>

      <div class="profile-content">
        <el-card class="box-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>基础信息</span>
            </div>
          </template>
          <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="80px">
             <el-form-item label="头像上传" class="avatar-item">
              <el-upload
                class="avatar-uploader"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                accept="image/*"
              >
                <img v-if="infoForm.avatar" :src="infoForm.avatar" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
              <div class="upload-tip">点击图片更换头像</div>
            </el-form-item>

            <el-form-item label="登录账号">
              <el-input v-model="infoForm.username" disabled />
            </el-form-item>
            <el-form-item label="用户昵称" prop="nickname">
              <el-input v-model="infoForm.nickname" placeholder="给您自己起个响亮的昵称吧" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitInfo" :loading="loading" class="save-btn">保存个人信息</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="box-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>安全设置</span>
            </div>
          </template>
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="danger" @click="submitPwd" :loading="loading" class="save-btn">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import request from '@/api'

const router = useRouter()
const userStore = useUserStore()

const uploadUrl = '/api/user/upload' // User specific upload endpoint
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const loading = ref(false)
const infoFormRef = ref()
const pwdFormRef = ref()

const infoForm = reactive({
  id: null,
  username: '',
  nickname: '',
  avatar: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const infoRules = {
  nickname: [{ required: true, message: '昵称不能为空', trigger: 'blur' }]
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }]
}

const loadUserInfo = async () => {
  try {
    const res = await request.get(`/user/info/${userStore.userInfo.id}`)
    infoForm.id = res.id
    infoForm.username = res.username
    infoForm.nickname = res.nickname
    infoForm.avatar = res.avatar
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadUserInfo()
})

const handleAvatarSuccess = (response, uploadFile) => {
  if (response.code === 200) {
    infoForm.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.msg || '头像上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('头像只能是图片格式!')
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
  }
  return isImage && isLt2M
}

const submitInfo = async () => {
  await infoFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.put('/user/update', {
          id: infoForm.id,
          nickname: infoForm.nickname,
          avatar: infoForm.avatar
        })
        ElMessage.success('个人信息更新成功')
        
        // 更新本地 store
        userStore.setUserInfo({
          ...userStore.userInfo,
          nickname: infoForm.nickname,
          avatar: infoForm.avatar
        })
      } catch (e) {
        console.error(e)
      } finally {
        loading.value = false
      }
    }
  })
}

const submitPwd = async () => {
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await request.put('/user/update/password', {
          id: userStore.userInfo.id,
          oldPassword: pwdForm.oldPassword,
          newPassword: pwdForm.newPassword
        })
        ElMessage.success('密码修改成功，请重新登录')
        userStore.clearToken()
        router.push('/login')
      } catch (e) {
        ElMessage.error(e.response?.data?.msg || '修改密码失败，原密码可能错误')
        console.error(e)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.profile-page-wrapper {
  min-height: 100vh;
  background-color: #f5f5f7; /* Very light gray/white background */
  padding-top: 40px;
}

.profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.profile-header h2 {
  font-size: 28px;
  font-weight: bold;
  margin: 0;
  color: var(--text-primary);
}

.back-btn {
  font-size: 20px;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.box-card {
  border-radius: 16px;
  border: 1px solid #e0e0e0;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.save-btn {
  width: 140px;
  border-radius: 8px;
  font-weight: bold;
}

/* 头像上传样式 */
.avatar-item {
  display: flex;
  align-items: center;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  background-color: #fafafa;
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  margin-left: 20px;
  font-size: 12px;
  color: #999;
}
</style>
