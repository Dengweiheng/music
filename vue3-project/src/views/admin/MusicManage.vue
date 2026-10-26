<template>
  <div class="music-manage">
    <div class="page-header">
      <div class="header-left">
        <h2>曲库管理</h2>
        <p>在这里管理平台所有的音乐资源和元数据</p>
      </div>
      <el-button type="primary" icon="Plus" @click="handleAdd">上传音乐资源</el-button>
    </div>

    <el-card shadow="never" class="table-card">
      <div class="filter-row">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索歌名、歌手或专辑..." 
          prefix-icon="Search" 
          clearable
          @keyup.enter="loadData"
          class="search-input"
        />
        <el-select 
          v-model="selectedStyle" 
          placeholder="筛选音乐风格" 
          clearable 
          @change="loadData"
          class="style-select"
        >
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.name" />
        </el-select>
        <el-select 
          v-model="selectedStatus" 
          placeholder="审核状态" 
          clearable 
          @change="loadData"
          style="width: 120px"
        >
          <el-option label="待审核" :value="0" />
          <el-option label="已上岸" :value="1" />
          <el-option label="未通过" :value="2" />
        </el-select>
        <el-button type="primary" icon="Search" @click="loadData">查找音乐</el-button>
        <el-button icon="Refresh" @click="resetSearch">重置搜索</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" class="modern-table">
        <el-table-column label="预览" width="80">
          <template #default="{ row }">
            <el-image :src="row.coverUrl" class="table-cover">
              <template #error>
                <div class="cover-placeholder"><el-icon><Headset /></el-icon></div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="歌曲名称" min-width="150" />
        <el-table-column prop="singer" label="歌手" min-width="120" />
        <el-table-column prop="album" label="所属专辑" min-width="120" />
        <el-table-column prop="style" label="风格流派">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" type="info">{{ row.style || '未分类' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="播放量" width="100" prop="playCount" />
        
        <el-table-column label="审核状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success" size="small">已上岸</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger" size="small">未通过</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="管理操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" v-if="row.status !== 1" @click="handleAudit(row, 1)">通过</el-button>
            <el-button link type="warning" v-if="row.status === 0" @click="handleAudit(row, 2)">拒绝</el-button>
            <el-divider v-if="row.status !== 1 || row.status === 0" direction="vertical" />
            <el-button link type="primary" icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除此音乐?" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger" icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑音乐信息' : '上传新音乐'"
      width="650px"
      class="modern-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" label-position="top">
        <div class="form-grid">
          <el-form-item label="歌曲名称" prop="title">
            <el-input v-model="form.title" />
          </el-form-item>
          <el-form-item label="演唱歌手" prop="singer">
            <el-input v-model="form.singer" />
          </el-form-item>
          <el-form-item label="专辑名称" prop="album">
            <el-input v-model="form.album" />
          </el-form-item>
          <el-form-item label="风格流派" prop="style">
            <el-select v-model="form.style" filterable allow-create>
              <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.name" />
            </el-select>
          </el-form-item>
        </div>
        
        <el-form-item label="上传音乐资源 (MP3/WAV)" prop="fileUrl">
          <el-upload
            class="resource-uploader"
            drag
            action="/api/music/admin/upload"
            :headers="uploadHeaders"
            :on-success="handleFileSuccess"
            :limit="1"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或 <em>点击上传</em></div>
          </el-upload>
        </el-form-item>

        <el-form-item label="上传封面图">
          <el-upload
            class="cover-uploader"
            action="/api/music/admin/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
          >
            <img v-if="form.coverUrl" :src="form.coverUrl" class="uploaded-cover" />
            <div v-else class="cover-uploader-trigger">
              <el-icon><Picture /></el-icon>
              <span>选择图片</span>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitSave">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api'
import { useUserStore } from '@/store/user'
import { useRoute } from 'vue-router'

const userStore = useUserStore()
const route = useRoute()
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${userStore.token}` }))

const tableData = ref([])
const loading = ref(false)
const searchKeyword = ref(route.query.keyword || '')
const selectedStyle = ref(route.query.style || '')
const selectedStatus = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({ id: null, title: '', singer: '', album: '', style: '', fileUrl: '', coverUrl: '', status: 1 })
const categories = ref([])

const rules = {
  title: [{ required: true, message: '请输入歌名', trigger: 'blur' }],
  fileUrl: [{ required: true, message: '请上传音频资源', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const [catRes, musicRes] = await Promise.all([
      request.get('/category/all'),
      request.get('/music/list', {
        params: { 
          keyword: searchKeyword.value, 
          style: selectedStyle.value, 
          status: selectedStatus.value,
          current: currentPage.value, 
          size: pageSize.value 
        }
      })
    ])
    categories.value = catRes || []
    tableData.value = musicRes.records
    total.value = musicRes.total
  } catch (e) {} finally { loading.value = false }
}

const resetSearch = () => {
  searchKeyword.value = ''
  selectedStyle.value = ''
  selectedStatus.value = null
  currentPage.value = 1
  loadData()
}

onMounted(() => {
  loadData()
})

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, title: '', singer: '', album: '', style: '', fileUrl: '', coverUrl: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleAudit = async (row, status) => {
  try {
    await request.put(`/music/admin/audit/${row.id}/${status}`)
    ElMessage.success('操作成功')
    loadData()
  } catch (e) {}
}

const handleFileSuccess = (res) => { if(res.code === 200) form.fileUrl = res.data }
const handleCoverSuccess = (res) => { if(res.code === 200) form.coverUrl = res.data }

const submitSave = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) await request.put('/music/admin/update', form)
      else await request.post('/music/admin/save', form)
      ElMessage.success('操作成功')
      dialogVisible.value = false
      loadData()
    } catch (e) {}
  })
}

const handleDelete = async (id) => {
  try {
    await request.delete(`/music/admin/delete/${id}`)
    ElMessage.success('已移除')
    loadData()
  } catch (e) {}
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-left h2 {
  font-size: 24px;
  font-weight: 800;
  color: #1d1d1f;
  margin: 0 0 4px 0;
}

.header-left p {
  color: #86868b;
  font-size: 14px;
}

.table-card {
  border: none;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.02);
}

.filter-row {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.search-input { width: 250px; }
.style-select { width: 150px; }

.table-cover {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: block;
}

.cover-placeholder {
  width: 48px;
  height: 48px;
  background: #f5f5f7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #d2d2d7;
  border-radius: 8px;
}

.modern-table :deep(.el-table__header) th {
  background-color: #f5f5f7 !important;
  color: #86868b;
  font-weight: 700;
  text-transform: uppercase;
  font-size: 12px;
  letter-spacing: 0.5px;
}

.pagination-container {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.resource-uploader :deep(.el-upload-dragger) {
  border-radius: 16px;
  background-color: #f5f5f7;
  border: 1px dashed #d2d2d7;
}

.cover-uploader {
  width: 120px;
  height: 120px;
  border: 1px dashed #d2d2d7;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
}

.uploaded-cover {
  width: 120px;
  height: 120px;
  object-fit: cover;
}

.cover-uploader-trigger {
  width: 120px;
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #86868b;
  gap: 8px;
}
</style>
