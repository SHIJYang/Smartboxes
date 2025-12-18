<template>
  <div class="error-recovery-container">
    <el-card class="error-card">
      <template #header>
        <div class="card-header">
          <span>错误修复管理</span>
        </div>
      </template>
      
      <!-- 错误记录区域 -->
      <el-card class="sub-card">
        <template #header>
          <span>错误记录</span>
        </template>
        
        <div class="error-list">
          <div v-if="errorLogs.length === 0" class="empty-state">
            <el-empty description="暂无错误记录" />
          </div>
          <div v-else>
            <el-table
              v-loading="loading"
              :data="errorLogs"
              style="width: 100%"
              stripe
              border
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="errorCode" label="错误代码" min-width="150" />
              <el-table-column prop="errorMessage" label="错误信息" min-width="300" />
              <el-table-column prop="errorTime" label="错误时间" min-width="200" />
              <el-table-column prop="status" label="处理状态" min-width="120">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 0 ? 'warning' : 'success'">
                    {{ scope.row.status === 0 ? '未处理' : '已处理' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="handleMarkAsProcessed(scope.row.id)">
                    <el-icon><Check /></el-icon>
                    标记已处理
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
          </div>
        </div>
      </el-card>
      
      <!-- 错误修复操作区域 -->
      <el-card class="sub-card">
        <template #header>
          <span>错误修复操作</span>
        </template>
        
        <div class="recovery-actions">
          <el-button type="warning" @click="handleTriggerFallback" :loading="fallbackLoading">
            <el-icon><Warning /></el-icon>
            触发优雅降级策略
          </el-button>
          <el-button type="danger" @click="handleRecordError" :loading="recordingError">
            <el-icon><CircleClose /></el-icon>
            记录测试错误
          </el-button>
        </div>
      </el-card>
    </el-card>
    
    <!-- 记录错误对话框 -->
    <el-dialog
      v-model="recordErrorDialogVisible"
      title="记录错误"
      width="600px"
    >
      <el-form
        :model="errorForm"
        :rules="rules"
        ref="errorFormRef"
        label-width="100px"
      >
        <el-form-item label="错误代码" prop="errorCode">
          <el-input v-model="errorForm.errorCode" placeholder="请输入错误代码" />
        </el-form-item>
        <el-form-item label="错误信息" prop="errorMessage">
          <el-input v-model="errorForm.errorMessage" type="textarea" placeholder="请输入错误信息" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="recordErrorDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitError">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Warning, CircleClose } from '@element-plus/icons-vue'
import axios from '../../axios'

// 状态
const loading = ref(false)
const errorLogs = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const fallbackLoading = ref(false)
const recordingError = ref(false)

// 对话框
const recordErrorDialogVisible = ref(false)
const errorForm = ref({
  errorCode: '',
  errorMessage: ''
})
const errorFormRef = ref(null)

// 表单验证规则
const rules = {
  errorCode: [
    { required: true, message: '请输入错误代码', trigger: 'blur' }
  ],
  errorMessage: [
    { required: true, message: '请输入错误信息', trigger: 'blur' }
  ]
}

// 获取错误记录列表
const getErrorLogs = async () => {
  loading.value = true
  try {
    // 这里需要根据实际API调整
    const response = await axios.get('/api/errors/list', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    errorLogs.value = response.data.data || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取错误记录失败')
    console.error('获取错误记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 触发优雅降级策略
const handleTriggerFallback = async () => {
  try {
    await ElMessageBox.confirm('确定要触发优雅降级策略吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    fallbackLoading.value = true
    await axios.post('/api/error-recovery/trigger-fallback')
    ElMessage.success('优雅降级策略已触发')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('触发优雅降级策略失败')
      console.error('触发优雅降级策略失败:', error)
    }
  } finally {
    fallbackLoading.value = false
  }
}

// 打开记录错误对话框
const handleRecordError = () => {
  recordErrorDialogVisible.value = true
}

// 提交错误记录
const handleSubmitError = async () => {
  if (!errorFormRef.value) return
  
  try {
    await errorFormRef.value.validate()
    
    recordingError.value = true
    await axios.post('/api/error-recovery/record-error', {
      ...errorForm.value,
      errorTime: new Date().toISOString()
    })
    
    ElMessage.success('错误记录已保存')
    recordErrorDialogVisible.value = false
    getErrorLogs()
  } catch (error) {
    if (error.name !== 'Cancel') {
      ElMessage.error('保存错误记录失败')
      console.error('保存错误记录失败:', error)
    }
  } finally {
    recordingError.value = false
  }
}

// 标记错误为已处理
const handleMarkAsProcessed = async (errorId) => {
  try {
    await axios.put(`/api/errors/update-status/${errorId}`, {
      status: 1
    })
    ElMessage.success('错误已标记为已处理')
    getErrorLogs()
  } catch (error) {
    ElMessage.error('标记失败')
    console.error('标记错误状态失败:', error)
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getErrorLogs()
}

// 当前页变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  getErrorLogs()
}

// 初始化
onMounted(() => {
  getErrorLogs()
})
</script>

<style scoped>
.error-recovery-container {
  width: 100%;
  height: 100%;
}

.error-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sub-card {
  margin-bottom: 20px;
}

.error-list {
  margin-top: 20px;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.recovery-actions {
  padding: 20px 0;
}

.recovery-actions .el-button {
  margin-right: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>