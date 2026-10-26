<template>
  <div class="notice-manage">
    <div class="page-header">
      <div class="header-left">
        <h2>公告管理</h2>
        <p>发布或调整平台系统公告通知</p>
      </div>
      <el-button type="primary" icon="Plus" @click="handleAdd">发布公告</el-button>
    </div>

    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="公告标题" width="250" />
        <el-table-column prop="content" label="内容摘要" min-width="300">
          <template #default="{ row }">
            <span class="text-truncate">{{ row.content }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-divider direction="vertical" />
            <el-popconfirm title="确定删除?" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布新公告'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入公告正文..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSave">立即发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: null, title: '', content: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/notice/list')
    tableData.value = res
  } catch (e) {} finally { loading.value = false }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, title: '', content: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const submitSave = async () => {
  try {
    if (isEdit.value) await request.put('/notice/admin/update', form)
    else await request.post('/notice/admin/save', form)
    ElMessage.success('发布成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {}
}

const handleDelete = async (id) => {
  try {
    await request.delete(`/notice/admin/delete/${id}`)
    ElMessage.success('已删除')
    loadData()
  } catch (e) {}
}

const formatDateTime = (ds) => {
  if(!ds) return '-'
  const d = new Date(ds)
  return `${d.getFullYear()}-${(d.getMonth()+1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.header-left h2 { font-size: 24px; font-weight: 800; margin: 0; }
.header-left p { color: #86868b; margin: 4px 0 0 0; }
.table-card { border-radius: 20px; }
.text-truncate { display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden; font-size: 14px; color: #515154; }
</style>
