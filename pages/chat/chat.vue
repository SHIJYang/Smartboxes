{
type: uploaded file
fileName: chat.vue
fullContent:
<template>
  <view class="chat-container">
    <PCHeader current="chat" />
    <view class="pc-placeholder"></view>

    <view class="mobile-header">
      <view class="header-content">
        <text class="header-icon">✨</text>
        <view>
          <text class="header-title">AI 收纳酱</text>
          <text class="header-subtitle">你的贴身物品管家</text>
        </view>
      </view>
    </view>

    <scroll-view 
      scroll-y 
      class="msg-box" 
      :scroll-top="scrollTop" 
      :scroll-with-animation="true"
      :show-scrollbar="false"
    >
      <view class="msg-padding">
        <view v-for="(m, i) in list" :key="i" :class="['row', m.role === 'assistant' ? 'ai' : 'user']">
          
          <view class="avatar-wrapper">
            <view class="avatar" :class="m.role === 'assistant' ? 'ai' : 'user'">
              <image v-if="m.role === 'assistant'" src="/static/ai-avatar.png" mode="aspectFill" class="avatar-img">
                <text class="avatar-emoji">🤖</text> 
              </image>
              <text v-else class="avatar-emoji">🐱</text>
            </view>
          </view>

          <view class="bubble-wrapper">
            <text class="name-tag">{{ m.role === 'assistant' ? '收纳酱' : '我' }}</text>
            <view class="bubble">
              {{ m.text }}
            </view>
          </view>
        </view>
      </view>
      
      <view class="bottom-spacer"></view>
    </scroll-view>

    <view v-if="!userStore.isLoggedIn" class="login-prompt">
      <view class="prompt-content">
        <text class="prompt-icon">🔐</text>
        <text class="prompt-text">请先登录以使用 AI 对话功能</text>
        <button class="login-btn" @click="goToLogin">去登录</button>
      </view>
    </view>

    <view v-if="userStore.isLoggedIn" class="input-area">
      <view class="input-shell">
        <input 
          class="chat-input"
          v-model="txt" 
          placeholder="例如：我的 Switch 藏哪啦？" 
          placeholder-style="color: #bbb; font-size: 28rpx;"
          confirm-type="send" 
          @confirm="send" 
          :disabled="sending"
        />
        <button class="send-btn" @click="send" :loading="sending" :disabled="sending || !txt.trim()">
          <text v-if="!sending">发送</text>
          <text v-else>发送中...</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, computed, watch } from 'vue';
import { useUserStore, useChatStore } from '@/stores';

const userStore = useUserStore();
const chatStore = useChatStore();

// Map Store messages to UI format
const list = computed(() => {
  return chatStore.messages.map(msg => ({
    role: msg.role, // 'user' | 'assistant'
    text: msg.content
  }));
});

const txt = ref('');
const sending = ref(false);
const scrollTop = ref(0);

// Initial greeting check
const checkInitialMessages = () => {
  if (chatStore.isEmpty && userStore.isLoggedIn) {
     // Store allows direct mutation or we can ignore until first message
     // Usually we might want to push a local fake message, but let's keep it clean
  }
};

const send = async () => {
  if (!txt.value.trim() || sending.value || !userStore.isLoggedIn) return;
  
  const message = txt.value;
  txt.value = '';
  sending.value = true;
  
  try {
    // Send using Store Action
    await chatStore.sendMessage(message, userStore.userId || 0);
  } catch (error) {
    console.error('Failed to send message:', error);
    uni.showToast({ title: '发送失败', icon: 'none' });
  } finally {
    sending.value = false;
    scrollToBottom();
  }
};

const scrollToBottom = () => {
  nextTick(() => {
    scrollTop.value = 9999999; 
  });
};

const goToLogin = () => {
  uni.navigateTo({ url: '/pages/user/login' });
};

// Scroll when messages change
watch(() => chatStore.messages.length, () => {
    scrollToBottom();
});

onMounted(() => {
  userStore.checkLoginStatus();
  if (userStore.isLoggedIn) {
    checkInitialMessages();
  }
  scrollToBottom();
});
</script>

<style lang="scss" scoped>
/* 页面背景：奶油色 */
.chat-container {
  display: flex; flex-direction: column; height: 100vh;
  background-color: #FFF9F0; position: relative;
  
  @media screen and (min-width: 768px) { max-width: 100%; margin: 0 auto; }
}

.pc-placeholder { display: none; height: 60px; flex-shrink: 0; @media screen and (min-width: 768px) { display: block; } }

.mobile-header {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  padding: 48rpx 40rpx 40rpx; 
  border-bottom-left-radius: 60rpx; border-bottom-right-radius: 60rpx;
  box-shadow: 0 8rpx 30rpx rgba(255, 154, 158, 0.3);
  z-index: 10; flex-shrink: 0;
  
  @media screen and (min-width: 768px) { display: none; }
}

.header-content { display: flex; align-items: center; }
.header-icon { font-size: 56rpx; margin-right: 20rpx; }
.header-title { font-size: 40rpx; font-weight: 800; color: #fff; display: block; text-shadow: 2rpx 2rpx 4rpx rgba(0,0,0,0.1); }
.header-subtitle { font-size: 26rpx; color: rgba(255,255,255, 0.9); margin-top: 4rpx; display: block; }

/* 消息区域 */
.msg-box {
  flex: 1; background-color: #FFF9F0; height: 0;
  @media screen and (min-width: 768px) {
    & ::-webkit-scrollbar { width: 6px; }
    & ::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.1); border-radius: 3px; }
    & ::-webkit-scrollbar-track { background: transparent; }
  }
}

.msg-padding { 
  padding: 40rpx 30rpx; 
  @media screen and (min-width: 768px) { max-width: 900px; margin: 0 auto; }
}

.row { display: flex; margin-bottom: 50rpx; align-items: flex-start; }
.row.user { flex-direction: row-reverse; }

.avatar-wrapper { flex-shrink: 0; margin: 0 20rpx; }
.avatar {
  width: 88rpx; height: 88rpx; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  border: 6rpx solid #fff; box-shadow: 0 8rpx 16rpx rgba(0,0,0,0.08);
  overflow: hidden;
  &.ai { background: #A18CD1; }
  &.user { background: #FBC2EB; }
}
.avatar-emoji { font-size: 48rpx; }
.avatar-img { width: 100%; height: 100%; }

.bubble-wrapper {
  display: flex; flex-direction: column; max-width: 70%;
  @media screen and (min-width: 768px) { max-width: 60%; }
}
.row.user .bubble-wrapper { align-items: flex-end; }

.name-tag { font-size: 20rpx; color: #999; margin-bottom: 8rpx; margin-left: 10rpx; }
.row.user .name-tag { margin-right: 10rpx; }

.bubble {
  padding: 28rpx 36rpx; border-radius: 40rpx;
  font-size: 30rpx; line-height: 1.5; position: relative;
  box-shadow: 4rpx 4rpx 20rpx rgba(0,0,0,0.05); word-break: break-all;
}

.row.ai .bubble { background: #fff; color: #555; border-top-left-radius: 8rpx; }
.row.user .bubble { background: linear-gradient(120deg, #84fab0 0%, #8fd3f4 100%); color: #2c5875; border-top-right-radius: 8rpx; font-weight: 500; }

.bottom-spacer { height: 180rpx; }

/* 登录提示 */
.login-prompt {
  position: fixed; bottom: 126rpx; left: 0; right: 0; z-index: 30;
  background: rgba(255, 249, 240, 0.95); padding: 40rpx 30rpx;
  backdrop-filter: blur(10rpx); text-align: center;
  
  @media screen and (min-width: 768px) {
    background: transparent; backdrop-filter: none; padding: 0; position: relative; bottom: auto; margin-top: auto; margin-bottom: 50px;
  }
}

.prompt-content { display: flex; flex-direction: column; align-items: center; gap: 20rpx; }
.prompt-icon { font-size: 64rpx; margin-bottom: 10rpx; }
.prompt-text { font-size: 32rpx; color: #666; font-weight: 500; }
.login-btn {
  background: linear-gradient(to right, #FF9A9E, #FECFEF); color: #fff;
  border: none; border-radius: 40rpx; padding: 20rpx 60rpx;
  font-size: 28rpx; font-weight: bold; box-shadow: 0 4rpx 10rpx rgba(255, 154, 158, 0.4);
  margin-top: 20rpx;
}

/* 输入区 */
.input-area {
  position: fixed; bottom: 126rpx; left: 0; right: 0; z-index: 20;
  background: rgba(255,249,240, 0.95);
  padding: 20rpx 30rpx calc(20rpx + constant(safe-area-inset-bottom)) 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); 
  backdrop-filter: blur(10rpx);

  @media screen and (min-width: 768px) {
    background: transparent; backdrop-filter: none; padding: 0;
    left: 50%; transform: translateX(-50%); bottom: 50px;
    width: 90%; max-width: 800px;
  }
}

.input-shell {
  background: #fff; padding: 12rpx 12rpx 12rpx 40rpx;
  border-radius: 60rpx; display: flex; align-items: center;
  box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.08); border: 4rpx solid #fff;
  
  @media screen and (min-width: 768px) {
    box-shadow: 0 15rpx 40rpx rgba(0,0,0,0.12);
    border: 1px solid rgba(255,255,255,0.8);
    background: rgba(255,255,255,0.95); backdrop-filter: blur(10px);
  }
}

.chat-input {
  flex: 1; height: 72rpx; font-size: 30rpx; color: #333;
  &:disabled { background-color: #f5f5f5; cursor: not-allowed; }
}

.send-btn {
  border-radius: 40rpx; background: linear-gradient(to right, #FF9A9E, #FECFEF);
  color: #fff; font-size: 28rpx; padding: 0 40rpx; height: 72rpx; line-height: 72rpx;
  border: none; font-weight: bold; margin-left: 20rpx;
  box-shadow: 0 4rpx 10rpx rgba(255, 154, 158, 0.4); cursor: pointer;
  
  &[disabled] { opacity: 0.6; filter: grayscale(0.5); cursor: not-allowed; }
  &:active:not([disabled]) { transform: scale(0.95); }
}
</style>
}