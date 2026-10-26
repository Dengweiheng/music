<template>
  <el-container class="admin-wrapper">
    <!-- 侧边栏 -->
    <el-aside width="240px" class="admin-sidebar">
      <div class="sidebar-logo">
        <el-icon :size="24"><Headset /></el-icon>
        <span>MusicBox Admin</span>
      </div>
      
       <el-menu
        :default-active="route.path"
        class="admin-menu"
        router
      >
        <el-menu-item index="/admin">
          <el-icon><Odometer /></el-icon>
          <span>控制台概览</span>
        </el-menu-item>
        <el-menu-item index="/admin/music">
          <el-icon><Headset /></el-icon>
          <span>曲库管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Grid /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/banners">
          <el-icon><Picture /></el-icon>
          <span>轮播管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/notices">
          <el-icon><ChatDotRound /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/sensitive">
          <el-icon><Warning /></el-icon>
          <span>词库维护</span>
        </el-menu-item>
        <el-menu-item index="/">
          <el-icon><Monitor /></el-icon>
          <span>预览前台</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <div class="admin-info">
          <el-avatar :size="32" :src="userStore.userInfo.avatar" />
          <el-dropdown trigger="click">
            <span class="admin-name" style="cursor: pointer;">
              {{ userStore.userInfo.nickname || userStore.userInfo.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">修改个人信息</el-dropdown-item>
                <el-dropdown-item @click="handleLogout" divided type="danger">安全退出</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-aside>
    
    <el-container class="admin-content-container">
      <el-header class="admin-header">
        <div class="header-breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>管理中心</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-tools">
          <el-button circle icon="QuestionFilled" size="small" />
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter, useRoute } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

const currentRouteName = computed(() => {
  if (route.path.includes('music')) return '曲库管理'
  if (route.path.includes('users')) return '用户管理'
  if (route.path.includes('banners')) return '轮播管理'
  if (route.path.includes('notices')) return '公告管理'
  if (route.path.includes('sensitive')) return '词库维护'
  if (route.path.includes('categories')) return '分类管理'
  return '仪表盘'
})

const handleLogout = () => {
  userStore.clearToken()
  router.push('/login')
}
</script>

<style scoped>
.admin-wrapper {
  height: 100vh;
  background-color: #f5f5f7;
}

.admin-sidebar {
  background-color: #ffffff;
  border-right: 1px solid #e5e5e5;
  display: flex;
  flex-direction: column;
}

.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 24px;
  font-size: 18px;
  font-weight: 800;
  color: #1d1d1f;
  border-bottom: 1px solid #f5f5f7;
}

.admin-menu {
  border-right: none;
  flex: 1;
  padding: 12px;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  border-radius: 12px;
  margin-bottom: 4px;
  font-weight: 600;
  color: #515154;
}

:deep(.el-menu-item.is-active) {
  background-color: #f5f5f7 !important;
  color: #1d1d1f !important;
}

.sidebar-footer {
  padding: 24px;
  border-top: 1px solid #f5f5f7;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.admin-name {
  font-size: 14px;
  font-weight: 700;
  color: #1d1d1f;
}

.admin-header {
  height: 64px;
  background-color: #ffffff;
  border-bottom: 1px solid #e5e5e5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

.admin-main {
  padding: 24px;
}
</style>
