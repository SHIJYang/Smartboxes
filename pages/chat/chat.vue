<template>
  <view class="chat-page">
    <u-navbar title="智能助手" />

    <!-- 聊天区域 -->
    <scroll-view scroll-y class="chat-area">
      <chatbubble
        v-for="(msg, index) in messages"
        :key="index"
        :text="msg.text"
        :is-self="msg.isSelf"
        :avatar="msg.avatar"
      />
    </scroll-view>

    <!-- 输入区 -->
    <view class="input-area">
      <u-input v-model="inputText" placeholder="请输入..." />
      <chatcard
        @start="onVoiceStart"
        @stop="onVoiceStop"
      />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import chatbubble from '@/components/chatbubble.vue'
import chatcard from '@/components/chatcard.vue'

const messages = ref([
  { text: '你好！我是收纳助手，可以帮你查找物品。', isSelf: false, avatar: '/static/avatar/ai.png' },
  { text: '我想找我的钢笔', isSelf: true, avatar: '/static/avatar/user.png' }
])

const inputText = ref('')

const onVoiceStart = () => {
  console.log('开始录音')
  // 实际项目中调用 uni.startRecord()
}

const onVoiceStop = () => {
  console.log('结束录音')
  // 调用 uni.stopRecord() 并上传识别
}
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
.chat-area {
  flex: 1;
  padding: 20rpx;
  background-color: #f5f5f5;
}
.input-area {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background-color: #fff;
  border-top: 1px solid #eee;
}
</style>