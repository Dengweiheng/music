<template>
  <div class="category-manage">
    <!-- 极简白底黑字头部 -->
    <div class="header">
      <h2>分类管理 (风格流派)</h2>
      <el-button type="primary" class="add-btn" @click="handleAdd">新增分类</el-button>
    </div>

    <!-- 极简白色卡片 -->
    <div class="content-card">
      <el-table :data="tableData" v-loading="loading" class="simple-table" stripe>
        <el-table-column type="index" label="编号" width="80" align="center" />
        <el-table-column prop="name" label="分类名称 (风格流派)" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除此分类吗?" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分类' : '新增分类'"
      width="400px"
      class="simple-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名(如：流行)" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitSave">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({ id: null, name: '' })

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/category/admin/list', {
      params: { current: currentPage.value, size: pageSize.value }
    })
    tableData.value = res.records
    total.value = res.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.name = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  dialogVisible.value = true
}

const submitSave = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await request.put('/category/admin/update', form)
        } else {
          await request.post('/category/admin/save', form)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        loadData()
      } catch (e) {
        console.error(e)
      }
    }
  })
}

const handleDelete = async (id) => {
  try {
    await request.delete(`/category/admin/delete/${id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
/* 强制极简：白底黑字 */
.category-manage {
  background-color: #ffffff;
  min-height: calc(100vh - 100px);
  padding: 24px;
  color: #000000;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #000000; /* 黑线 */
}

.header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #000000;
  margin: 0;
}

.add-btn {
  background-color: #000000 !important;
  border-color: #000000 !important;
  color: #ffffff !important;
  font-weight: bold;
}

.add-btn:hover {
  background-color: #333333 !important;
  border-color: #333333 !important;
}

.content-card {
  background: #ffffff;
}

.simple-table {
  border: 1px solid #000000;
}

:deep(.simple-table th.el-table__cell) {
  background-color: #f0f0f0 !important;
  color: #000000 !important;
  font-weight: bold;
  border-bottom: 1px solid #000000;
}

:deep(.simple-table td.el-table__cell) {
  color: #000000 !important;
  border-bottom: 1px solid #e0e0e0;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-pager li) {
  color: #000000 !important;
}

:deep(.el-pager li.is-active) {
  background-color: #000000 !important;
  color: #ffffff !important;
}

/* 强制对话框也是白底黑字 */
:deep(.simple-dialog .el-dialog) {
  background-color: #ffffff !important;
}

:deep(.simple-dialog .el-dialog__title) {
  color: #000000 !important;
  font-weight: bold;
}

:deep(.simple-dialog .el-form-item__label) {
  color: #000000 !important;
  font-weight: bold;
}

:deep(.simple-dialog .el-input__inner) {
  color: #000000 !important;
  border-color: #000000 !important;
}

.dialog-footer .el-button--primary {
  background-color: #000000 !important;
  border-color: #000000 !important;
  color: #ffffff !important;
}

.dialog-footer .el-button--primary:hover {
  background-color: #333333 !important;
}

.dialog-footer .el-button {
  color: #000000;
  border-color: #000000;
}
</style>
