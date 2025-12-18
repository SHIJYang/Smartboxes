<template>
  <div class="ai-chat-container">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <span>AI聊天</span>
        </div>
      </template>
      
      <!-- 聊天记录区域 -->
      <div class="chat-messages">
        <div
          v-for="(message, index) in chatMessages"
          :key="index"
          :class="['message-item', message.sender === 'user' ? 'user-message' : 'ai-message']"
        >
          <div class="message-avatar">
            <el-avatar :icon="message.sender === 'user' ? User : Robot" />
          </div>
          <div class="message-content">
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ message.time }}</div>
          </div>
        </div>
      </div>
      
      <!-- 输入区域 -->
      <div class="chat-input-container">
        <el-input
          v-model="userInput"
          type="textarea"
          placeholder="请输入消息..."
          :rows="3"
          @keyup.enter.exact="handleSend"
        />
        <div class="input-actions">
          <el-button type="primary" @click="handleSend" :loading="sending">
            <el-icon><Send /></el-icon>
            发送
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Robot, Send } from '@element-plus/icons-vue'
import axios from '../../axios'
import { useUserStore } from '../../stores/user'

// 获取用户信息
const userStore = useUserStore()

// 状态
const chatMessages = ref([])
const userInput = ref('')
const sending = ref(false)

// 格式化时间
const formatTime = () => {
  const now = new Date()
  const hours = now.getHours().toString().padStart(2, '0')
  const minutes = now.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

// 发送消息
const handleSend = async () => {
  if (!userInput.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }
  
  const message = userInput.value.trim()
  // 添加用户消息到聊天记录
  chatMessages.value.push({
    sender: 'user',
    content: message,
    time: formatTime()
  })
  
  userInput.value = ''
  sending.value = true
  
  try {
    // 调用AI聊天API
    const response = await axios.post('/api/chat', {
      userId: userStore.currentUser?.id || 0,
      message: message
    })
    
    // 添加AI回复到聊天记录
    chatMessages.value.push({
      sender: 'ai',
      content: response.data.data || '抱歉，我无法理解您的问题。',
      time: formatTime()
    })
    
    // 滚动到底部
    scrollToBottom()
  } catch (error) {
    ElMessage.error('发送消息失败，请重试')
    console.error('发送消息失败:', error)
  } finally {
    sending.value = false
  }
}

// 滚动到底部
const scrollToBottom = () => {
  setTimeout(() => {
    const messagesContainer = document.querySelector('.chat-messages')
    if (messagesContainer) {
      messagesContainer.scrollTop = messagesContainer.scrollHeight
    }
  }, 100)
}

// 初始化
onMounted(() => {
  // 添加欢迎消息
  chatMessages.value.push({
    sender: 'ai',
    content: '您好！我是智能助手，有什么可以帮助您的吗？',
    time: formatTime()
  })
})
</script>

<style scoped>
.ai-chat-container {
  width: 100%;
  height: 100%;
}

.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  margin: 0 10px;
}

.user-message .message-avatar {
  margin: 0 10px 0 0;
}

.ai-message .message-avatar {
  margin: 0 0 0 10px;
}

.message-content {
  max-width: 70%;
}

.user-message .message-content {
  text-align: right;
}

.ai-message .message-content {
  text-align: left;
}

.message-text {
  padding: 10px 15px;
  border-radius: 18px;
  word-break: break-word;
}

.user-message .message-text {
  background-color: #409eff;
  color: white;
}

.ai-message .message-text {
  background-color: white;
  color: #333;
  border: 1px solid #e4e7ed;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.chat-input-container {
  display: flex;
  flex-direction: column;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
</style>