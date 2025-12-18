<template>
  <div class="item-list-container">
    <el-card class="item-card">
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" @click="handleAddItem">
            <el-icon><Plus /></el-icon>
            添加商品
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-container">
        <el-input
          v-model="queryParams.itemCode"
          placeholder="请输入商品编码"
          style="width: 200px; margin-right: 10px"
        >
          <template #append>
            <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        
        <el-input
          v-model="queryParams.itemName"
          placeholder="请输入商品名称"
          style="width: 200px; margin-right: 10px"
        />
        
        <el-select
          v-model="queryParams.status"
          placeholder="选择状态"
          style="width: 150px; margin-right: 10px"
        >
          <el-option label="上架" :value="0" />
          <el-option label="下架" :value="1" />
          <el-option label="缺货" :value="2" />
        </el-select>
        
        <el-button type="default" @click="handleReset">重置</el-button>
      </div>
      
      <!-- 商品列表 -->
      <el-table
        v-loading="loading"
        :data="itemList"
        style="width: 100%"
        stripe
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="itemCode" label="商品编码" min-width="150" />
        <el-table-column prop="itemName" label="商品名称" min-width="150" />
        <el-table-column prop="description" label="商品描述" min-width="200" />
        <el-table-column prop="boxId" label="所属盒子ID" min-width="120" />
        <el-table-column prop="price" label="价格" min-width="100">
          <template #default="scope">
            {{ scope.row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="200" />
        <el-table-column prop="updateTime" label="更新时间" min-width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditItem(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteItem(scope.row.id)">
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
    
    <!-- 添加/编辑商品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        :model="itemForm"
        :rules="rules"
        ref="itemFormRef"
        label-width="100px"
      >
        <el-form-item label="商品编码" prop="itemCode">
          <el-input v-model="itemForm.itemCode" placeholder="请输入商品编码" />
        </el-form-item>
        <el-form-item label="商品名称" prop="itemName">
          <el-input v-model="itemForm.itemName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="itemForm.description" type="textarea" placeholder="请输入商品描述" :rows="3" />
        </el-form-item>
        <el-form-item label="所属盒子ID" prop="boxId">
          <el-input-number v-model="itemForm.boxId" :min="1" placeholder="请输入盒子ID" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="itemForm.price" :min="0" :step="0.01" :precision="2" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="itemForm.status" placeholder="选择状态">
            <el-option label="上架" :value="0" />
            <el-option label="下架" :value="1" />
            <el-option label="缺货" :value="2" />
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
const itemList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 查询参数
const queryParams = ref({
  itemCode: '',
  itemName: '',
  status: undefined
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加商品')
const editingItem = ref(false)
const itemForm = ref({
  id: null,
  itemCode: '',
  itemName: '',
  description: '',
  boxId: null,
  price: 0,
  status: 0
})
const itemFormRef = ref(null)

// 表单验证规则
const rules = {
  itemCode: [
    { required: true, message: '请输入商品编码', trigger: 'blur' }
  ],
  itemName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  boxId: [
    { required: true, message: '请输入所属盒子ID', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取商品列表
const getItems = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/items/list', {
      params: {
        queryItemDTO: {
          ...queryParams.value,
          pageNum: currentPage.value,
          pageSize: pageSize.value
        }
      }
    })
    itemList.value = response.data.data || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取商品列表失败')
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 添加商品
const handleAddItem = () => {
  editingItem.value = false
  dialogTitle.value = '添加商品'
  itemForm.value = {
    id: null,
    itemCode: '',
    itemName: '',
    description: '',
    boxId: null,
    price: 0,
    status: 0
  }
  dialogVisible.value = true
}

// 编辑商品
const handleEditItem = (item) => {
  editingItem.value = true
  dialogTitle.value = '编辑商品'
  itemForm.value = { ...item }
  dialogVisible.value = true
}

// 删除商品
const handleDeleteItem = async (itemId) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await axios.delete(`/api/items/delete/${itemId}`)
    ElMessage.success('删除商品成功')
    getItems()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除商品失败')
      console.error('删除商品失败:', error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!itemFormRef.value) return
  
  try {
    await itemFormRef.value.validate()
    
    if (editingItem.value) {
      // 更新商品
      await axios.put('/api/items/update', itemForm.value)
      ElMessage.success('更新商品成功')
    } else {
      // 添加商品
      await axios.post('/api/items/add', itemForm.value)
      ElMessage.success('添加商品成功')
    }
    
    dialogVisible.value = false
    getItems()
  } catch (error) {
    if (error.name !== 'Cancel') {
      ElMessage.error(editingItem.value ? '更新商品失败' : '添加商品失败')
      console.error('提交表单失败:', error)
    }
  }
}

// 搜索
const handleSearch = () => {
  getItems()
}

// 重置
const handleReset = () => {
  queryParams.value = {
    itemCode: '',
    itemName: '',
    status: undefined
  }
  getItems()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getItems()
}

// 当前页变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  getItems()
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '上架',
    1: '下架',
    2: '缺货'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    0: 'success',
    1: 'info',
    2: 'warning'
  }
  return typeMap[status] || 'info'
}

// 初始化
onMounted(() => {
  getItems()
})
</script>

<style scoped>
.item-list-container {
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