<template>
  <div class="sensitive-word-manage">
    <div class="page-header">
      <div class="header-left">
        <h2>敏感词库维护</h2>
        <p>管理平台评论区的违规词过滤列表</p>
      </div>
      <div class="header-actions">
        <el-input 
          v-model="newWord" 
          placeholder="输入新敏感词..." 
          @keyup.enter="handleAdd"
          style="width: 250px; margin-right: 12px"
        />
        <el-button type="primary" icon="Plus" @click="handleAdd">添加敏感词</el-button>
      </div>
    </div>

    <el-card shadow="never" class="table-card">
      <div class="word-cloud-sim">
        <el-tag 
          v-for="item in tableData" 
          :key="item.id" 
          closable 
          @close="handleDelete(item.id)"
          class="word-tag"
          size="large"
          type="info"
        >
          {{ item.word }}
        </el-tag>
        <el-empty v-if="tableData.length === 0" description="词库空空如也" />
      </div>
    </el-card>

    <div class="tip-box">
      <el-icon><InfoFilled /></el-icon>
      <p>提示：敏感词添加后立即生效，用户在发表包含这些词汇的评论时将被系统自动阻断。</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api'

const loading = ref(false)
const tableData = ref([])
const newWord = ref('')

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/sensitive/admin/list')
    tableData.value = res
  } catch (e) {} finally { loading.value = false }
}

const handleAdd = async () => {
  if (!newWord.value.trim()) {
    ElMessage.warning('请输入敏感词内容')
    return
  }
  try {
    await request.post('/sensitive/admin/save', { word: newWord.value })
    ElMessage.success('添加成功')
    newWord.value = ''
    loadData()
  } catch (e) {
    console.error(e)
    ElMessage.error(e.message || '添加失败，可能词条已存在或系统错误')
  }
}

const handleDelete = async (id) => {
  try {
    await request.delete(`/sensitive/admin/delete/${id}`)
    ElMessage.success('已移除')
    loadData()
  } catch (e) {
    ElMessage.error('移除失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.header-left h2 { font-size: 24px; font-weight: 800; margin: 0; }
.header-left p { color: #86868b; margin: 4px 0 0 0; }
.table-card { border-radius: 20px; min-height: 400px; padding: 20px; }
.word-cloud-sim { display: flex; flex-wrap: wrap; gap: 12px; }
.word-tag { border-radius: 10px; font-size: 15px; background: #f5f5f7; border: 1px solid #e5e5e7; color: #1d1d1f; }
.tip-box { margin-top: 24px; background: #eef7ff; padding: 16px; border-radius: 12px; display: flex; align-items: center; gap: 10px; color: #007aff; font-size: 13px; }
.tip-box p { margin: 0; }
</style>
