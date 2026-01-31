<template>
  <div class="resource-list-container">
    <el-card class="resource-card">
      <template #header>
        <div class="card-header">
          <span>模型资源管理</span>
          <el-button type="primary" @click="handleAddResource">
            <el-icon><Plus /></el-icon>
            添加模型资源
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-container">
        <el-input
          v-model="queryParams.key"
          placeholder="请输入资源键值"
          style="width: 200px; margin-right: 10px"
        >
          <template #append>
            <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        
        <el-button type="default" @click="handleReset">重置</el-button>
      </div>
      
      <!-- 模型资源列表 -->
      <el-table
        v-loading="loading"
        :data="resourceList"
        style="width: 100%"
        stripe
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="key" label="资源键值" min-width="150" />
        <el-table-column prop="value" label="资源值" min-width="200" />
        <el-table-column prop="description" label="资源描述" min-width="200" />
        <el-table-column prop="createTime" label="创建时间" min-width="200" />
        <el-table-column prop="updateTime" label="更新时间" min-width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditResource(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteResource(scope.row.id)">
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
    
    <!-- 添加/编辑模型资源对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        :model="resourceForm"
        :rules="rules"
        ref="resourceFormRef"
        label-width="100px"
      >
        <el-form-item label="资源键值" prop="key">
          <el-input v-model="resourceForm.key" placeholder="请输入资源键值" />
        </el-form-item>
        <el-form-item label="资源值" prop="value">
          <el-input v-model="resourceForm.value" type="textarea" placeholder="请输入资源值" :rows="3" />
        </el-form-item>
        <el-form-item label="资源描述" prop="description">
          <el-input v-model="resourceForm.description" type="textarea" placeholder="请输入资源描述" :rows="2" />
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
const resourceList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 查询参数
const queryParams = ref({
  key: ''
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加模型资源')
const editingResource = ref(false)
const resourceForm = ref({
  id: null,
  key: '',
  value: '',
  description: ''
})
const resourceFormRef = ref(null)

// 表单验证规则
const rules = {
  key: [
    { required: true, message: '请输入资源键值', trigger: 'blur' }
  ],
  value: [
    { required: true, message: '请输入资源值', trigger: 'blur' }
  ]
}

// 获取模型资源列表
const getResources = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/resources/list')
    resourceList.value = response.data.data || []
    total.value = response.data.data ? response.data.data.length : 0
  } catch (error) {
    ElMessage.error('获取模型资源列表失败')
    console.error('获取模型资源列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 添加模型资源
const handleAddResource = () => {
  editingResource.value = false
  dialogTitle.value = '添加模型资源'
  resourceForm.value = {
    id: null,
    key: '',
    value: '',
    description: ''
  }
  dialogVisible.value = true
}

// 编辑模型资源
const handleEditResource = (resource) => {
  editingResource.value = true
  dialogTitle.value = '编辑模型资源'
  resourceForm.value = { ...resource }
  dialogVisible.value = true
}

// 删除模型资源
const handleDeleteResource = async (resourceId) => {
  try {
    await ElMessageBox.confirm('确定要删除该模型资源吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 这里需要根据实际API调整
    await axios.delete(`/api/resources/delete/${resourceId}`)
    ElMessage.success('删除模型资源成功')
    getResources()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除模型资源失败')
      console.error('删除模型资源失败:', error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!resourceFormRef.value) return
  
  try {
    await resourceFormRef.value.validate()
    
    if (editingResource.value) {
      // 更新模型资源
      // 这里需要根据实际API调整
      await axios.put('/api/resources/update', resourceForm.value)
      ElMessage.success('更新模型资源成功')
    } else {
      // 添加模型资源
      await axios.post('/api/resources/add', resourceForm.value)
      ElMessage.success('添加模型资源成功')
    }
    
    dialogVisible.value = false
    getResources()
  } catch (error) {
    if (error.name !== 'Cancel') {
      ElMessage.error(editingResource.value ? '更新模型资源失败' : '添加模型资源失败')
      console.error('提交表单失败:', error)
    }
  }
}

// 搜索
const handleSearch = () => {
  getResources()
}

// 重置
const handleReset = () => {
  queryParams.value = {
    key: ''
  }
  getResources()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getResources()
}

// 当前页变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  getResources()
}

// 初始化
onMounted(() => {
  getResources()
})
</script>

<style scoped>
.resource-list-container {
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