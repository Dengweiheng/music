<script setup>
import { RouterView } from 'vue-router'
</script>

<template>
  <el-config-provider>
    <RouterView />
  </el-config-provider>
</template>

<style>
/* 可以在这里放一些全局样式 */
body {
  margin: 0;
  padding: 0;
  background-color: #f5f5f7; /* 浅色背景 */
  color: #1d1d1f;
}

#app {
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Myriad Set Pro", "SF Pro Icons", "Apple Legacy Chevron", "Helvetica Neue", "Helvetica", "Arial", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 覆盖 Element Plus 默认颜色以匹配浅色高级风格 */
:root {
  --el-color-primary: #007aff; /* 经典的蓝色 */
  --el-bg-color: #ffffff;
  --el-bg-color-overlay: #ffffff;
  --el-text-color-primary: #1d1d1f;
  --el-text-color-regular: #86868b;
  --el-border-color-light: #d2d2d7;
}
</style>
