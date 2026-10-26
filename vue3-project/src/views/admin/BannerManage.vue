<template>
  <div class="banner-manage">
    <div class="page-header">
      <div class="header-left">
        <h2>轮播图管理</h2>
        <p>配置首页顶部的精彩推荐内容</p>
      </div>
      <el-button type="primary" icon="Plus" @click="handleAdd">新增轮播图</el-button>
    </div>

    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column label="预览" width="180">
          <template #default="{ row }">
            <el-image :src="row.imageUrl" class="banner-preview" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="sort" label="排序" width="80" sortable />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑' : '新增'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="图片地址">
          <el-upload
            class="resource-uploader"
            action="/api/music/admin/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
          >
            <img v-if="form.imageUrl" :src="form.imageUrl" class="uploaded-img" />
            <el-icon v-else class="uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="排序权重">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${userStore.token}` }))
const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: null, title: '', imageUrl: '', linkUrl: '', sort: 0 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/banner/list')
    tableData.value = res
  } catch (e) {} finally { loading.value = false }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, title: '', imageUrl: '', linkUrl: '', sort: 0 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleUploadSuccess = (res) => {
  if (res.code === 200) form.imageUrl = res.data
}

const submitSave = async () => {
  try {
    if (isEdit.value) await request.put('/banner/admin/update', form)
    else await request.post('/banner/admin/save', form)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {}
}

const handleDelete = async (id) => {
  try {
    await request.delete(`/banner/admin/delete/${id}`)
    ElMessage.success('已删除')
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.header-left h2 { font-size: 24px; font-weight: 800; margin: 0; }
.header-left p { color: #86868b; margin: 4px 0 0 0; }
.table-card { border-radius: 20px; }
.banner-preview { width: 140px; height: 60px; border-radius: 8px; }
.resource-uploader { border: 1px dashed #d2d2d7; border-radius: 12px; width: 200px; height: 100px; display: flex; align-items: center; justify-content: center; overflow: hidden; }
.uploaded-img { width: 100%; height: 100%; object-fit: cover; }
.uploader-icon { font-size: 24px; color: #86868b; }
</style>
