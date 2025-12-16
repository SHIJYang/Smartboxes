<template>
  <view class="chat-container">
    <view class="chat-header">
      <view class="header-content">
        <text class="header-icon">✨</text>
        <view>
          <text class="header-title">AI 收纳酱</text>
          <text class="header-subtitle">你的贴身物品管家</text>
        </view>
      </view>
    </view>

    <scroll-view scroll-y class="msg-box" :scroll-top="scrollTop" :scroll-with-animation="true">
      <view class="msg-padding">
        <view v-for="(m, i) in list" :key="i" :class="['row', m.role]">
          
          <view class="avatar-wrapper">
            <view class="avatar" :class="m.role">
              <image v-if="m.role === 'ai'" src="/static/ai-avatar.png" mode="aspectFill" class="avatar-img">
                <text class="avatar-emoji">🤖</text> 
              </image>
              <text v-else class="avatar-emoji">🐱</text>
            </view>
          </view>

          <view class="bubble-wrapper">
            <text class="name-tag">{{ m.role === 'ai' ? '收纳酱' : '我' }}</text>
            <view class="bubble">
              {{ m.text }}
            </view>
          </view>
        </view>
      </view>
      <view style="height: 180rpx;"></view>
    </scroll-view>

    <view class="input-area">
      <view class="input-shell">
        <input 
          v-model="txt" 
          placeholder="例如：我的 Switch 藏哪啦？" 
          placeholder-style="color: #bbb; font-size: 28rpx;"
          confirm-type="send" 
          @confirm="send" 
        />
        <button class="send-btn" @click="send" :loading="sending">
          <text v-if="!sending">发送</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue';
import { onShow, onLoad } from '@dcloudio/uni-app';
import { sendChat } from '@/api/index';

const list = ref([
  { role: 'ai', text: '主人您好！我是收纳酱 (｡♥‿♥｡)' },
  { role: 'ai', text: '找不到东西了吗？快告诉我，比如："我的 Switch 游戏机放在哪里了？"' }
]);

const txt = ref('');
const sending = ref(false);
const scrollTop = ref(0);

const send = async () => {
  if (!txt.value.trim() || sending.value) return;
  
  list.value.push({ role: 'user', text: txt.value });
  const q = txt.value;
  txt.value = '';
  sending.value = true;
  
  scrollToBottom();
  
  try {
    const res = await sendChat({ userId: 1001, message: q });
    if (res.code === 200) {
      list.value.push({ role: 'ai', text: res.data.reply });
    } else {
      list.value.push({ role: 'ai', text: '呜呜，大脑短路了，稍后再试一下吧~' });
    }
  } catch (error) {
    list.value.push({ role: 'ai', text: '网络开小差了，检查一下信号哦。' });
  } finally {
    sending.value = false;
    scrollToBottom();
  }
};

const scrollToBottom = () => {
  nextTick(() => {
    scrollTop.value = scrollTop.value + 10000; 
  });
};
</script>

<style lang="scss">
/* 页面背景：奶油色 */
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #FFF9F0; 
}

/* 头部设计 - 尺寸已转 rpx */
.chat-header {
  background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
  /* 顶部留白增加，适应刘海屏 */
  padding: 80rpx 40rpx 40rpx; 
  border-bottom-left-radius: 60rpx;
  border-bottom-right-radius: 60rpx;
  box-shadow: 0 8rpx 30rpx rgba(255, 154, 158, 0.3);
  z-index: 10;
}

.header-content {
  display: flex;
  align-items: center;
}

.header-icon {
  font-size: 56rpx;
  margin-right: 20rpx;
}

.header-title {
  font-size: 40rpx;
  font-weight: 800;
  color: #fff;
  display: block;
  text-shadow: 2rpx 2rpx 4rpx rgba(0,0,0,0.1);
}

.header-subtitle {
  font-size: 26rpx;
  color: rgba(255,255,255, 0.9);
  margin-top: 4rpx;
  display: block;
}

/* 消息区域 */
.msg-box {
  flex: 1;
  background-color: #FFF9F0;
}

.msg-padding {
  padding: 40rpx 30rpx;
}

.row {
  display: flex;
  margin-bottom: 50rpx;
  align-items: flex-start;
}

.row.user {
  flex-direction: row-reverse;
}

/* 头像样式 - 88rpx 约等于 44px */
.avatar-wrapper {
  flex-shrink: 0;
  margin: 0 20rpx;
}

.avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 6rpx solid #fff;
  box-shadow: 0 8rpx 16rpx rgba(0,0,0,0.08);
  overflow: hidden;
}

.avatar.ai {
  background: #A18CD1;
}

.avatar.user {
  background: #FBC2EB;
}

.avatar-emoji {
  font-size: 48rpx;
}

/* 气泡外壳 */
.bubble-wrapper {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.row.user .bubble-wrapper {
  align-items: flex-end;
}

.name-tag {
  font-size: 20rpx;
  color: #999;
  margin-bottom: 8rpx;
  margin-left: 10rpx;
}

.row.user .name-tag {
  margin-right: 10rpx;
}

/* 气泡本体 */
.bubble {
  padding: 28rpx 36rpx;
  border-radius: 40rpx;
  font-size: 30rpx;
  line-height: 1.5;
  position: relative;
  box-shadow: 4rpx 4rpx 20rpx rgba(0,0,0,0.05);
  word-break: break-all;
}

.row.ai .bubble {
  background: #fff;
  color: #555;
  border-top-left-radius: 8rpx; 
}

.row.user .bubble {
  background: linear-gradient(120deg, #84fab0 0%, #8fd3f4 100%);
  color: #2c5875;
  border-top-right-radius: 8rpx;
  font-weight: 500;
}

/* 输入区 */
.input-area {
  position: fixed;
  bottom: 80rpx;
  left: 0;
  right: 0;
  background: rgba(255,249,240, 0.95);
  /* 底部留白适配 iPhone Home 条：20rpx padding + constant(safe-area) */
  padding: 20rpx 30rpx calc(20rpx + constant(safe-area-inset-bottom)) 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); 
  backdrop-filter: blur(10rpx);
  z-index: 20;
}

.input-shell {
  background: #fff;
  padding: 12rpx 12rpx 12rpx 40rpx;
  border-radius: 60rpx;
  display: flex;
  align-items: center;
  box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.08);
  border: 4rpx solid #fff;
}

.input-shell input {
  flex: 1;
  height: 72rpx;
  font-size: 30rpx;
  color: #333;
}

.send-btn {
  border-radius: 40rpx;
  background: linear-gradient(to right, #FF9A9E, #FECFEF);
  color: #fff;
  font-size: 28rpx;
  padding: 0 40rpx;
  height: 72rpx;
  line-height: 72rpx;
  border: none;
  font-weight: bold;
  margin-left: 20rpx;
  box-shadow: 0 4rpx 10rpx rgba(255, 154, 158, 0.4);
}

.send-btn:active {
  transform: scale(0.95);
}
</style>