<template>
  <div class="user-list-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAddUser">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-container">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入用户名或账号"
          style="width: 300px; margin-right: 10px"
        >
          <template #append>
            <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        <el-button type="default" @click="handleReset">重置</el-button>
      </div>
      
      <!-- 用户列表 -->
      <el-table
        v-loading="loading"
        :data="userList"
        style="width: 100%"
        stripe
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userAccount" label="用户账号" min-width="150" />
        <el-table-column prop="userName" label="用户名" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" min-width="200" />
        <el-table-column prop="updateTime" label="更新时间" min-width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditUser(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteUser(scope.row.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form
        :model="userForm"
        :rules="rules"
        ref="userFormRef"
        label-width="80px"
      >
        <el-form-item label="用户账号" prop="userAccount">
          <el-input v-model="userForm.userAccount" placeholder="请输入用户账号" />
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="userForm.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item v-if="!editingUser" label="密码" prop="userPassword">
          <el-input v-model="userForm.userPassword" type="password" placeholder="请输入密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete } from '@element-plus/icons-vue'
import axios from '../../axios'

// 状态
const loading = ref(false)
const userList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const editingUser = ref(false)
const userForm = ref({
  id: null,
  userAccount: '',
  userName: '',
  phone: '',
  userPassword: ''
})
const userFormRef = ref(null)

// 表单验证规则
const rules = {
  userAccount: [
    { required: true, message: '请输入用户账号', trigger: 'blur' }
  ],
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 获取用户列表
const getUsers = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/users/page', {
      params: {
        query: {
          keyword: searchKeyword.value,
          currentPage: currentPage.value,
          pageSize: pageSize.value
        }
      }
    })
    userList.value = response.data
    total.value = response.data.length
  } catch (error) {
    ElMessage.error('获取用户列表失败')
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 添加用户
const handleAddUser = () => {
  editingUser.value = false
  dialogTitle.value = '添加用户'
  userForm.value = {
    id: null,
    userAccount: '',
    userName: '',
    phone: '',
    userPassword: ''
  }
  dialogVisible.value = true
}

// 编辑用户
const handleEditUser = (user) => {
  editingUser.value = true
  dialogTitle.value = '编辑用户'
  userForm.value = {
    id: user.id,
    userAccount: user.userAccount,
    userName: user.userName,
    phone: user.phone,
    userPassword: ''
  }
  dialogVisible.value = true
}

// 删除用户
const handleDeleteUser = async (userId) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await axios.delete(`/api/users/${userId}`)
    ElMessage.success('删除用户成功')
    getUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除用户失败')
      console.error('删除用户失败:', error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    
    if (editingUser.value) {
      // 更新用户
      await axios.put(`/api/users/${userForm.value.id}`, userForm.value)
      ElMessage.success('更新用户成功')
    } else {
      // 添加用户
      await axios.post('/api/users', userForm.value)
      ElMessage.success('添加用户成功')
    }
    
    dialogVisible.value = false
    getUsers()
  } catch (error) {
    if (error.name !== 'Cancel') {
      ElMessage.error(editingUser.value ? '更新用户失败' : '添加用户失败')
      console.error('提交表单失败:', error)
    }
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getUsers()
}

// 重置
const handleReset = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  getUsers()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getUsers()
}

// 当前页变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  getUsers()
}

// 初始化
onMounted(() => {
  getUsers()
})
</script>

<style scoped>
.user-list-container {
  width: 100%;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-container {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>