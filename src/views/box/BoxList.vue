<template>
  <div class="box-list-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>盒子管理</span>
          <el-button type="primary" @click="handleAddBox">
            <el-icon><Plus /></el-icon>
            添加盒子
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-container">
        <el-input
          v-model="queryParams.boxCode"
          placeholder="请输入盒子编码"
          style="width: 200px; margin-right: 10px"
        >
          <template #append>
            <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        
        <el-input
          v-model="queryParams.boxName"
          placeholder="请输入盒子名称"
          style="width: 200px; margin-right: 10px"
        />
        
        <el-select
          v-model="queryParams.boxType"
          placeholder="选择盒子类型"
          style="width: 150px; margin-right: 10px"
        >
          <el-option label="普通盒子" :value="0" />
          <el-option label="智能盒子" :value="1" />
        </el-select>
        
        <el-select
          v-model="queryParams.status"
          placeholder="选择状态"
          style="width: 150px; margin-right: 10px"
        >
          <el-option label="正常" :value="0" />
          <el-option label="离线" :value="1" />
          <el-option label="低电量" :value="2" />
          <el-option label="故障" :value="3" />
        </el-select>
        
        <el-button type="default" @click="handleReset">重置</el-button>
      </div>
      
      <!-- 盒子列表 -->
      <el-table
        v-loading="loading"
        :data="boxList"
        style="width: 100%"
        stripe
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="boxCode" label="盒子编码" min-width="150" />
        <el-table-column prop="boxName" label="盒子名称" min-width="150" />
        <el-table-column prop="boxType" label="盒子类型" min-width="100">
          <template #default="scope">
            {{ scope.row.boxType === 0 ? '普通盒子' : '智能盒子' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rssi" label="信号强度" min-width="100" />
        <el-table-column prop="battery" label="电池电量" min-width="100">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.battery"
              :stroke-width="8"
              :format="(percentage) => `${percentage}%`"
            />
          </template>
        </el-table-column>
        <el-table-column prop="lastHeartbeatTime" label="最后心跳时间" min-width="200" />
        <el-table-column prop="createTime" label="创建时间" min-width="200" />
        <el-table-column prop="updateTime" label="更新时间" min-width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditBox(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteBox(scope.row.id)">
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
    
    <!-- 添加/编辑盒子对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        :model="boxForm"
        :rules="rules"
        ref="boxFormRef"
        label-width="100px"
      >
        <el-form-item label="盒子编码" prop="boxCode">
          <el-input v-model="boxForm.boxCode" placeholder="请输入盒子编码" />
        </el-form-item>
        <el-form-item label="盒子名称" prop="boxName">
          <el-input v-model="boxForm.boxName" placeholder="请输入盒子名称" />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input-number v-model="boxForm.userId" :min="1" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="盒子类型" prop="boxType">
          <el-select v-model="boxForm.boxType" placeholder="选择盒子类型">
            <el-option label="普通盒子" :value="0" />
            <el-option label="智能盒子" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="boxForm.status" placeholder="选择状态">
            <el-option label="正常" :value="0" />
            <el-option label="离线" :value="1" />
            <el-option label="低电量" :value="2" />
            <el-option label="故障" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="信号强度" prop="rssi">
          <el-input-number v-model="boxForm.rssi" :min="-100" :max="0" placeholder="请输入信号强度" />
        </el-form-item>
        <el-form-item label="电池电量" prop="battery">
          <el-slider v-model="boxForm.battery" :min="0" :max="100" show-input />
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
const boxList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 查询参数
const queryParams = ref({
  boxCode: '',
  boxName: '',
  boxType: undefined,
  status: undefined
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加盒子')
const editingBox = ref(false)
const boxForm = ref({
  id: null,
  boxCode: '',
  boxName: '',
  userId: null,
  boxType: 0,
  status: 0,
  rssi: 0,
  battery: 100
})
const boxFormRef = ref(null)

// 表单验证规则
const rules = {
  boxCode: [
    { required: true, message: '请输入盒子编码', trigger: 'blur' }
  ],
  boxName: [
    { required: true, message: '请输入盒子名称', trigger: 'blur' }
  ],
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' }
  ],
  boxType: [
    { required: true, message: '请选择盒子类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取盒子列表
const getBoxes = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/boxes/list', {
      params: {
        queryBoxDTO: {
          ...queryParams.value,
          pageNum: currentPage.value,
          pageSize: pageSize.value
        }
      }
    })
    boxList.value = response.data.data || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取盒子列表失败')
    console.error('获取盒子列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 添加盒子
const handleAddBox = () => {
  editingBox.value = false
  dialogTitle.value = '添加盒子'
  boxForm.value = {
    id: null,
    boxCode: '',
    boxName: '',
    userId: null,
    boxType: 0,
    status: 0,
    rssi: 0,
    battery: 100
  }
  dialogVisible.value = true
}

// 编辑盒子
const handleEditBox = (box) => {
  editingBox.value = true
  dialogTitle.value = '编辑盒子'
  boxForm.value = { ...box }
  dialogVisible.value = true
}

// 删除盒子
const handleDeleteBox = async (boxId) => {
  try {
    await ElMessageBox.confirm('确定要删除该盒子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await axios.delete(`/api/boxes/delete/${boxId}`)
    ElMessage.success('删除盒子成功')
    getBoxes()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除盒子失败')
      console.error('删除盒子失败:', error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!boxFormRef.value) return
  
  try {
    await boxFormRef.value.validate()
    
    if (editingBox.value) {
      // 更新盒子
      await axios.put('/api/boxes/update', boxForm.value)
      ElMessage.success('更新盒子成功')
    } else {
      // 添加盒子
      await axios.post('/api/boxes/add', boxForm.value)
      ElMessage.success('添加盒子成功')
    }
    
    dialogVisible.value = false
    getBoxes()
  } catch (error) {
    if (error.name !== 'Cancel') {
      ElMessage.error(editingBox.value ? '更新盒子失败' : '添加盒子失败')
      console.error('提交表单失败:', error)
    }
  }
}

// 搜索
const handleSearch = () => {
  getBoxes()
}

// 重置
const handleReset = () => {
  queryParams.value = {
    boxCode: '',
    boxName: '',
    boxType: undefined,
    status: undefined
  }
  getBoxes()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getBoxes()
}

// 当前页变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  getBoxes()
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '正常',
    1: '离线',
    2: '低电量',
    3: '故障'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    0: 'success',
    1: 'warning',
    2: 'info',
    3: 'danger'
  }
  return typeMap[status] || 'info'
}

// 初始化
onMounted(() => {
  getBoxes()
})
</script>

<style scoped>
.box-list-container {
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