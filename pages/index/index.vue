<template>
  <view class="home-container">
    <view class="bg-layer">
      <view class="bg-shape shape-1"></view>
      <view class="bg-shape shape-2"></view>
    </view>

    <scroll-view scroll-y class="scroll-content" :show-scrollbar="false">
      
      <view :style="{ height: statusBarHeight + 'px' }"></view>
      
      <view class="main-wrapper">
        
        <view class="welcome-section fade-in-down">
          <view class="text-group">
            <text class="date">{{ currentDate }}</text>
            <text class="greet">Hi, {{ user?.username || '收纳达人' }}</text>
            <text class="sub-greet">你的物品管家已就绪 ✨</text>
          </view>
          <view class="avatar-box" @click="go('/pages/user/user')">
            <image v-if="user?.avatar" :src="user.avatar" class="avatar-img" mode="aspectFill" />
            <view v-else class="avatar-placeholder">{{ user?.username?.charAt(0).toUpperCase() || 'U' }}</view>
          </view>
        </view>

        <view class="stats-panel fade-in-up">
          <view class="stat-big-card blue" @click="go('/pages/box/boxlist')">
            <view class="inner">
              <view class="icon-circle">📦</view>
              <view class="data">
                <text class="num">{{ stats.boxes }}</text>
                <text class="lbl">盒子总数</text>
              </view>
            </view>
            <text class="bg-text">BOX</text>
          </view>
          
          <view class="stat-big-card pink" @click="go('/pages/item/itemlist')">
            <view class="inner">
              <view class="icon-circle">🏷️</view>
              <view class="data">
                <text class="num">{{ stats.items }}</text>
                <text class="lbl">收纳物品</text>
              </view>
            </view>
            <text class="bg-text">ITEM</text>
          </view>
        </view>

        <view class="menu-section fade-in-up">
          <text class="section-header">快捷功能</text>
          
          <view class="menu-grid">
            <view class="menu-card" hover-class="card-hover" @click="go('/pages/box/boxlist')">
              <view class="icon-bg c-blue">📦</view>
              <view class="menu-info">
                <text class="title">盒子管理</text>
                <text class="desc">录入与编辑</text>
              </view>
              <text class="arrow">→</text>
            </view>

            <view class="menu-card" hover-class="card-hover" @click="go('/pages/item/itemlist')">
              <view class="icon-bg c-green">🔍</view>
              <view class="menu-info">
                <text class="title">查找物品</text>
                <text class="desc">快速定位</text>
              </view>
              <text class="arrow">→</text>
            </view>

            <view class="menu-card" hover-class="card-hover" @click="go('/pages/chat/chat')">
              <view class="icon-bg c-purple">🤖</view>
              <view class="menu-info">
                <text class="title">AI 助手</text>
                <text class="desc">智能问答</text>
              </view>
              <text class="arrow">→</text>
            </view>

            <view class="menu-card" hover-class="card-hover" @click="go('/pages/user/user')">
              <view class="icon-bg c-orange">⚙️</view>
              <view class="menu-info">
                <text class="title">系统设置</text>
                <text class="desc">账号与偏好</text>
              </view>
              <text class="arrow">→</text>
            </view>
          </view>
        </view>
        
        <view style="height: 40rpx;"></view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onShow, onLoad } from '@dcloudio/uni-app';
import type { UserDTO } from '@/common/types';
import { getBoxList, getItemList } from '@/api/index';
import { useUserStore } from '@/stores/user';

const user = ref<UserDTO | null>(null);
const stats = ref({ boxes: 0, items: 0 });
const userStore = useUserStore();
const statusBarHeight = ref(20); // 默认高度
const currentDate = ref('');

onLoad(() => {
  // 1. 获取系统状态栏高度，实现沉浸式适配
  const sysInfo = uni.getSystemInfoSync();
  if (sysInfo.statusBarHeight) {
    statusBarHeight.value = sysInfo.statusBarHeight;
  }
  
  // 2. 设置日期
  const now = new Date();
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  currentDate.value = `${months[now.getMonth()]} ${now.getDate()}, ${now.getFullYear()}`;
});

onShow(async () => {
  const u = userStore.userInfo || uni.getStorageSync('userInfo');
  if (u) {
    user.value = u;
    await loadStats(u.id);
  }
});

const loadStats = async (userId: number) => {
  try {
    const boxRes = await getBoxList({ userId });
    if (boxRes.code === 200) stats.value.boxes = boxRes.data.length;
    
    const itemRes = await getItemList({});
    if (itemRes.code === 200) stats.value.items = itemRes.data.length;
  } catch (e) {
    console.error(e);
  }
};

const go = (url: string) => {
  const tabs = ['/pages/index/index', '/pages/box/boxlist', '/pages/chat/chat', '/pages/user/user'];
  if (tabs.includes(url)) {
    uni.switchTab({ url });
  } else {
    uni.navigateTo({ url });
  }
};
</script>

<style lang="scss" scoped>
/* 变量定义 */
$glass-bg: rgba(255, 255, 255, 0.85);
$shadow-sm: 0 10rpx 20rpx rgba(0,0,0,0.03);
$shadow-md: 0 15rpx 30rpx rgba(0,0,0,0.06);

.home-container {
  height: 100vh;
  width: 100vw;
  position: relative;
  overflow: hidden;
  background: #f6f9fc;
}

/* 1. 背景层 (绝对定位) */
.bg-layer {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 0;
  pointer-events: none; /* 允许点击穿透 */
  
  .bg-shape {
    position: absolute; border-radius: 50%; filter: blur(80px);
  }
  .shape-1 { width: 400rpx; height: 400rpx; background: rgba(79, 172, 254, 0.12); top: -100rpx; right: -100rpx; }
  .shape-2 { width: 300rpx; height: 300rpx; background: rgba(251, 194, 235, 0.15); top: 30%; left: -100rpx; }
}

/* 2. 滚动容器 */
.scroll-content {
  height: 100%;
  width: 100%;
  position: relative;
  z-index: 10;
}

.main-wrapper {
  padding: 20rpx 30rpx 40rpx;
}

/* A. 欢迎区 */
.welcome-section {
  display: flex; justify-content: space-between; align-items: flex-start;
  margin-top: 20rpx; margin-bottom: 50rpx;
  
  .text-group {
    .date { font-size: 24rpx; color: #999; margin-bottom: 6rpx; letter-spacing: 1rpx; text-transform: uppercase; }
    .greet { font-size: 44rpx; font-weight: 800; color: #333; margin-bottom: 8rpx; }
    .sub-greet { font-size: 26rpx; color: #666; }
  }
  
  .avatar-box {
    width: 100rpx; height: 100rpx;
    border-radius: 30rpx;
    background: #fff;
    box-shadow: $shadow-md;
    display: flex; align-items: center; justify-content: center;
    overflow: hidden;
    border: 4rpx solid #fff;
    
    .avatar-img { width: 100%; height: 100%; }
    .avatar-placeholder { font-size: 40rpx; font-weight: bold; color: #4facfe; }
  }
}

/* B. 核心数据看板 */
.stats-panel {
  display: flex; justify-content: space-between; margin-bottom: 50rpx;
  
  .stat-big-card {
    width: 48%; height: 240rpx;
    border-radius: 36rpx;
    padding: 30rpx;
    position: relative;
    overflow: hidden;
    box-shadow: $shadow-md;
    transition: transform 0.2s;
    
    &:active { transform: scale(0.98); }
    
    .inner {
      position: relative; z-index: 2; height: 100%;
      display: flex; flex-direction: column; justify-content: space-between;
    }
    
    .icon-circle {
      width: 70rpx; height: 70rpx; border-radius: 50%;
      background: rgba(255,255,255,0.25);
      backdrop-filter: blur(5px);
      display: flex; align-items: center; justify-content: center;
      font-size: 34rpx;
    }
    
    .data {
      .num { font-size: 56rpx; font-weight: 800; color: #fff; line-height: 1; display: block; margin-bottom: 10rpx; }
      .lbl { font-size: 24rpx; color: rgba(255,255,255,0.9); }
    }
    
    .bg-text {
      position: absolute; bottom: -20rpx; right: -10rpx;
      font-size: 80rpx; font-weight: 900;
      color: rgba(255,255,255,0.1);
      transform: rotate(-15deg);
      z-index: 1;
    }
    
    &.blue { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
    &.pink { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
  }
}

/* C. 菜单宫格 */
.menu-section {
  .section-header {
    font-size: 34rpx; font-weight: bold; color: #333;
    margin-bottom: 30rpx; display: block; padding-left: 10rpx;
  }
  
  .menu-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24rpx;
  }
  
  .menu-card {
    background: $glass-bg;
    backdrop-filter: blur(15px);
    border-radius: 32rpx;
    padding: 30rpx;
    display: flex; align-items: center;
    border: 1px solid rgba(255,255,255,0.6);
    box-shadow: $shadow-sm;
    position: relative;
    overflow: hidden;
    
    .icon-bg {
      width: 80rpx; height: 80rpx; border-radius: 24rpx;
      display: flex; align-items: center; justify-content: center;
      font-size: 36rpx; margin-right: 20rpx;
      flex-shrink: 0;
      
      &.c-blue { background: rgba(79, 172, 254, 0.1); color: #4facfe; }
      &.c-green { background: rgba(66, 211, 146, 0.1); color: #42d392; }
      &.c-purple { background: rgba(161, 140, 209, 0.1); color: #a18cd1; }
      &.c-orange { background: rgba(255, 154, 158, 0.1); color: #ff9a9e; }
    }
    
    .menu-info {
      flex: 1;
      .title { font-size: 28rpx; font-weight: bold; color: #333; display: block; margin-bottom: 4rpx; }
      .desc { font-size: 20rpx; color: #999; }
    }
    
    .arrow { color: #ddd; font-size: 24rpx; font-weight: 300; }
    
    &.card-hover { transform: scale(0.98); background: #fff; }
  }
}

/* 动画 */
.fade-in-down { animation: fadeInDown 0.6s ease-out; }
.fade-in-up { animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>