<template>
  <div class="user-manage">
    <div class="page-header">
      <div class="title-section">
        <h2>用户管理</h2>
        <p>查看并管理平台所有注册用户及其角色权限</p>
      </div>
    </div>

    <div class="table-card">
      <div class="table-toolbar">
        <el-input
          v-model="search"
          placeholder="关键词搜索用户..."
          prefix-icon="Search"
          class="search-input"
          clearable
        />
        <div class="actions">
          <el-button icon="Refresh" circle @click="loadUsers" />
          <el-button type="primary" icon="Plus" @click="handleAdd">新增虚拟用户</el-button>
        </div>
      </div>

      <el-table :data="filteredUsers" v-loading="loading" style="width: 100%">
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info-cell">
              <el-avatar :size="40" :src="row.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
              <div class="meta">
                <span class="nickname">{{ row.nickname || '未设置昵称' }}</span>
                <span class="username">@{{ row.username }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="角色权限" width="150">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'danger' : 'success'" effect="light" round>
              {{ row.role === 1 ? '系统管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="注册时间" sortable width="180">
          <template #default="{ row }">
             {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'danger' : 'success'" size="small">
              {{ row.status === 1 ? '已禁用' : '正常活跃' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="editUser(row)">编辑</el-button>
            <el-divider direction="vertical" />
            <el-dropdown trigger="click" @command="handleCommand($event, row)">
              <el-button link type="primary">更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="role">{{ row.role === 1 ? '降为用户' : '设为管理员' }}</el-dropdown-item>
                  <el-dropdown-item :command="row.status === 1 ? 'enable' : 'disable'">
                    {{ row.status === 1 ? '启用账号' : '禁用账号' }}
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided type="danger">删除账号</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-area">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="userList.length"
          :page-size="10"
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="400px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role">
            <el-option label="普通用户" :value="0" />
            <el-option label="系统管理员" :value="1" />
          </el-select>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api'

const search = ref('')
const loading = ref(false)
const userList = ref([])

const filteredUsers = computed(() => {
  return userList.value.filter(u => 
    u.username.includes(search.value) || 
    (u.nickname && u.nickname.includes(search.value))
  )
})

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/list') 
    userList.value = res.records || res || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({ id: null, username: '', password: '', nickname: '', role: 0 })

const rules = {
  username: [{ required: true, message: '必填', trigger: 'blur' }],
  password: [{ required: true, message: '必填', trigger: 'blur' }]
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, username: '', password: '', nickname: '', role: 0 })
  dialogVisible.value = true
}

const editUser = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const submitSave = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await request.put('/user/update', form)
        } else {
          await request.post('/user/register', form)
        }
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadUsers()
      } catch (e) {}
    }
  })
}

const handleCommand = async (cmd, row) => {
  if (cmd === 'role') {
    const newRole = row.role === 1 ? 0 : 1
    try {
      await request.put('/user/update', { ...row, role: newRole })
      ElMessage.success('角色更新成功')
      loadUsers()
    } catch(e) {}
  } else if (cmd === 'enable' || cmd === 'disable') {
    const newStatus = cmd === 'enable' ? 0 : 1
    try {
      await request.put(`/user/admin/status/${row.id}/${newStatus}`)
      ElMessage.success(newStatus === 1 ? '已禁用账号' : '已启用账号')
      loadUsers()
    } catch(e) {}
  } else if (cmd === 'delete') {
    ElMessageBox.confirm('确定要删除此用户吗?', '警告', { type: 'warning' }).then(async () => {
      try {
        await request.delete(`/user/delete/${row.id}`)
        ElMessage.success('已删除')
        loadUsers()
      } catch(e) {}
    }).catch(() => {})
  }
}

const formatDate = (ds) => {
  if (!ds) return '-'
  const d = new Date(ds)
  return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-manage {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.title-section h2 {
  font-size: 28px;
  font-weight: 800;
  margin: 0 0 8px 0;
  color: #1d1d1f;
}

.title-section p {
  color: #86868b;
  margin: 0;
}

.table-card {
  background: white;
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.03);
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
}

.search-input {
  width: 300px;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  background-color: #f5f5f7 !important;
  box-shadow: none !important;
}

.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.meta {
  display: flex;
  flex-direction: column;
}

.nickname {
  font-weight: 700;
  color: #1d1d1f;
  font-size: 14px;
}

.username {
  font-size: 12px;
  color: #86868b;
}

.pagination-area {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style>
