<template>
  <div class="emotion-list-container">
    <el-card class="emotion-card">
      <template #header>
        <div class="card-header">
          <span>情感标签管理</span>
          <el-button type="primary" @click="handleAddEmotion">
            <el-icon><Plus /></el-icon>
            添加情感标签
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-container">
        <el-input
          v-model="queryParams.emotionTag"
          placeholder="请输入情感标签"
          style="width: 200px; margin-right: 10px"
        >
          <template #append>
            <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        
        <el-select
          v-model="queryParams.status"
          placeholder="选择状态"
          style="width: 150px; margin-right: 10px"
        >
          <el-option label="启用" :value="0" />
          <el-option label="禁用" :value="1" />
        </el-select>
        
        <el-button type="default" @click="handleReset">重置</el-button>
      </div>
      
      <!-- 情感标签列表 -->
      <el-table
        v-loading="loading"
        :data="emotionList"
        style="width: 100%"
        stripe
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="emotionTag" label="情感标签" min-width="150" />
        <el-table-column prop="itemId" label="商品ID" min-width="100" />
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
              {{ scope.row.status === 0 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="200" />
        <el-table-column prop="updateTime" label="更新时间" min-width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditEmotion(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteEmotion(scope.row.id)">
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
    
    <!-- 添加/编辑情感标签对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        :model="emotionForm"
        :rules="rules"
        ref="emotionFormRef"
        label-width="100px"
      >
        <el-form-item label="情感标签" prop="emotionTag">
          <el-input v-model="emotionForm.emotionTag" placeholder="请输入情感标签" />
        </el-form-item>
        <el-form-item label="商品ID" prop="itemId">
          <el-input-number v-model="emotionForm.itemId" :min="1" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="emotionForm.status" placeholder="选择状态">
            <el-option label="启用" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
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
const emotionList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 查询参数
const queryParams = ref({
  emotionTag: '',
  status: undefined
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加情感标签')
const editingEmotion = ref(false)
const emotionForm = ref({
  id: null,
  emotionTag: '',
  itemId: null,
  status: 0
})
const emotionFormRef = ref(null)

// 表单验证规则
const rules = {
  emotionTag: [
    { required: true, message: '请输入情感标签', trigger: 'blur' }
  ],
  itemId: [
    { required: true, message: '请输入商品ID', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取情感标签列表
const getEmotions = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/emotions/list', {
      params: {
        queryEmotionDTO: {
          ...queryParams.value,
          pageNum: currentPage.value,
          pageSize: pageSize.value
        }
      }
    })
    emotionList.value = response.data.data || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取情感标签列表失败')
    console.error('获取情感标签列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 添加情感标签
const handleAddEmotion = () => {
  editingEmotion.value = false
  dialogTitle.value = '添加情感标签'
  emotionForm.value = {
    id: null,
    emotionTag: '',
    itemId: null,
    status: 0
  }
  dialogVisible.value = true
}

// 编辑情感标签
const handleEditEmotion = (emotion) => {
  editingEmotion.value = true
  dialogTitle.value = '编辑情感标签'
  emotionForm.value = { ...emotion }
  dialogVisible.value = true
}

// 删除情感标签
const handleDeleteEmotion = async (emotionId) => {
  try {
    await ElMessageBox.confirm('确定要删除该情感标签吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await axios.delete(`/api/emotions/delete/${emotionId}`)
    ElMessage.success('删除情感标签成功')
    getEmotions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除情感标签失败')
      console.error('删除情感标签失败:', error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!emotionFormRef.value) return
  
  try {
    await emotionFormRef.value.validate()
    
    if (editingEmotion.value) {
      // 更新情感标签
      await axios.put('/api/emotions/update', emotionForm.value)
      ElMessage.success('更新情感标签成功')
    } else {
      // 添加情感标签
      await axios.post('/api/emotions/add', emotionForm.value)
      ElMessage.success('添加情感标签成功')
    }
    
    dialogVisible.value = false
    getEmotions()
  } catch (error) {
    if (error.name !== 'Cancel') {
      ElMessage.error(editingEmotion.value ? '更新情感标签失败' : '添加情感标签失败')
      console.error('提交表单失败:', error)
    }
  }
}

// 搜索
const handleSearch = () => {
  getEmotions()
}

// 重置
const handleReset = () => {
  queryParams.value = {
    emotionTag: '',
    status: undefined
  }
  getEmotions()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getEmotions()
}

// 当前页变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  getEmotions()
}

// 初始化
onMounted(() => {
  getEmotions()
})
</script>

<style scoped>
.emotion-list-container {
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