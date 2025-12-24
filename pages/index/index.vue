<template>
  <view class="home-container">
    <PCHeader current="index" />
    <view class="pc-placeholder"></view>

    <view class="mobile-header">
      <view class="header-content">
        <view class="text-group">
          <text class="date">{{ currentDate }}</text>
          <text class="greet">Hi, {{ user?.username || '收纳达人' }} ✨</text>
        </view>
        <view class="avatar-box" @click="go('/pages/user/user')">
          <image v-if="user?.avatar" :src="user.avatar" class="avatar-img" mode="aspectFill" />
          <view v-else class="avatar-placeholder">{{ user?.username?.charAt(0).toUpperCase() || 'U' }}</view>
        </view>
      </view>
    </view>

    <scroll-view scroll-y class="scroll-content" :show-scrollbar="false">
      
      <view class="main-wrapper">
        
        <view class="stats-panel fade-in-up">
          <view class="stat-big-card warm-orange" @click="go('/pages/box/boxlist')">
            <view class="inner">
              <view class="icon-circle">📦</view>
              <view class="data">
                <text class="num">{{ stats.boxes }}</text>
                <text class="lbl">盒子总数</text>
              </view>
            </view>
            <text class="bg-text">BOX</text>
          </view>
          
          <view class="stat-big-card warm-pink" @click="go('/pages/item/itemlist')">
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
              <view class="icon-bg c-orange">📦</view>
              <view class="menu-info">
                <text class="title">盒子管理</text>
                <text class="desc">录入与编辑</text>
              </view>
              <text class="arrow">→</text>
            </view>

            <view class="menu-card" hover-class="card-hover" @click="go('/pages/item/itemlist')">
              <view class="icon-bg c-pink">🔍</view>
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
              <view class="icon-bg c-blue">⚙️</view>
              <view class="menu-info">
                <text class="title">系统设置</text>
                <text class="desc">账号与偏好</text>
              </view>
              <text class="arrow">→</text>
            </view>
          </view>
        </view>
        
        <view style="height: 100rpx;"></view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onShow, onLoad } from '@dcloudio/uni-app';
import type { UserDTO } from '@/common/types';
import { getBoxList, getItemList } from '@/api/index';
import { useUserStore } from '@/stores';

import PCHeader from '@/components/PCHeader.vue';

const user = ref<UserDTO | null>(null);
const stats = ref({ boxes: 0, items: 0 });
const userStore = useUserStore();
const statusBarHeight = ref(20); 
const currentDate = ref('');

onLoad(() => {
  const sysInfo = uni.getSystemInfoSync();
  if (sysInfo.statusBarHeight) {
    statusBarHeight.value = sysInfo.statusBarHeight;
  }
  
  const now = new Date();
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  currentDate.value = `${months[now.getMonth()]} ${now.getDate()}, ${now.getFullYear()}`;
});

onShow(async () => {
  const u = userStore.currentUser || uni.getStorageSync('userInfo');
  if (u) {
    user.value = u;
    await loadStats(u.id);
  }
  
  // 检查设备逻辑（可选，如果 App.vue 已经做了可以省略，但加上更保险）
  const sys = uni.getSystemInfoSync();
  if (sys.windowWidth > 768) uni.hideTabBar();
  else uni.showTabBar();
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
/* --- 变量定义 (Warm Theme) --- */
$bg-color: #FFF9F0;         /* 奶油白背景 */
$glass-bg: #FFFFFF;         /* 卡片背景改为纯白 */
$shadow-sm: 0 8rpx 20rpx rgba(255, 154, 158, 0.15); /* 柔和粉色阴影 */
$shadow-md: 0 12rpx 30rpx rgba(255, 154, 158, 0.25);
$shadow-lg: 0 15rpx 40rpx rgba(255, 154, 158, 0.35); /* PC端悬浮阴影 */
$primary-gradient: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%); /* 糖果粉渐变 */

.home-container {
  height: 100vh;
  width: 100vw;
  position: relative;
  overflow: hidden;
  background: $bg-color;
  display: flex;
  flex-direction: column;
}

/* 1. PC 占位符 */
.pc-placeholder {
  display: none;
  /* 假设 PC Header 高度约 60px - 80px */
  height: 60px; 
  flex-shrink: 0;
  @media screen and (min-width: 768px) {
    display: block;
  }
}

/* 2. 手机端 Header */
.mobile-header {
  background: $primary-gradient;
  margin-bottom: 60rpx; /* 底部留白给圆弧 */
  border-bottom-left-radius: 60rpx;
  border-bottom-right-radius: 60rpx;
  box-shadow: $shadow-md;
  flex-shrink: 0;
  
  @media screen and (min-width: 768px) {
    display: none; /* 电脑端隐藏 */
  }

  .header-content {
    padding: 20rpx 40rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .text-group {
    .date { 
      font-size: 24rpx; color: rgba(255,255,255,0.8); 
      margin-bottom: 6rpx; letter-spacing: 1rpx; text-transform: uppercase; 
    }
    .greet { 
      font-size: 44rpx; font-weight: 800; color: #fff; 
      text-shadow: 2rpx 2rpx 4rpx rgba(0,0,0,0.1);
    }
  }
  
  .avatar-box {
    width: 90rpx; height: 90rpx;
    border-radius: 50%;
    background: rgba(255,255,255,0.25);
    border: 4rpx solid #fff;
    display: flex; align-items: center; justify-content: center;
    overflow: hidden;
    cursor: pointer;
    
    .avatar-img { width: 100%; height: 100%; }
    .avatar-placeholder { font-size: 36rpx; font-weight: bold; color: #fff; }
  }
}

/* 3. 滚动容器 */
.scroll-content {
  flex: 1;
  height: 0; /* 配合 flex: 1 确保滚动正常 */
  width: 100%;
  position: relative;
  
  /* 移动端：让内容稍微向上浮动，盖住 Header 的底部圆弧 */
  margin-top: -30rpx; 
  padding-top: 0;
  
  @media screen and (min-width: 768px) {
    margin-top: 0;
    /* PC端优化滚动条 */
    & ::-webkit-scrollbar {
      width: 8px;
      height: 8px;
    }
    & ::-webkit-scrollbar-thumb {
      background-color: rgba(0, 0, 0, 0.1);
      border-radius: 4px;
    }
  }
}

/* 核心布局容器 */
.main-wrapper {
  padding: 0 30rpx 40rpx;

  /* --- PC端响应式布局优化 --- */
  @media screen and (min-width: 768px) {
    max-width: 1200px; /* 限制最大宽度，防止大屏拉伸 */
    margin: 0 auto;    /* 居中显示 */
    padding: 40px 20px; /* 增加内边距 */
  }
}

/* A. 统计看板 */
.stats-panel {
  display: flex; 
  justify-content: space-between; 
  margin-bottom: 40rpx;
  gap: 40rpx;
  .stat-big-card {
    width: 48%; 
    height: 240rpx;
    border-radius: 36rpx;
    padding: 30rpx;
    position: relative;
    overflow: hidden;
    box-shadow: $shadow-sm;
    transition: all 0.3s ease; /* 平滑过渡 */
    cursor: pointer;
    
    /* 移动端点击效果 */
    &:active { transform: scale(0.98); }

    /* PC端 Hover 效果 */
    @media screen and (min-width: 768px) {
      height: 200px; /* PC端稍微加高 */
      &:hover {
        transform: translateY(-5px);
        box-shadow: $shadow-lg;
      }
    }
    
    .inner {
      position: relative; z-index: 2; height: 100%;
      display: flex; flex-direction: column; justify-content: space-between;
    }
    
    .icon-circle {
      width: 70rpx; height: 70rpx; border-radius: 50%;
      background: rgba(255,255,255,0.3);
      backdrop-filter: blur(5px);
      display: flex; align-items: center; justify-content: center;
      font-size: 34rpx;
    }
    
    .data {
      .num { font-size: 56rpx; font-weight: 900; color: #fff; line-height: 1; display: block; margin-bottom: 10rpx; }
      .lbl { font-size: 24rpx; color: rgba(255,255,255,0.95); font-weight: bold;}
    }
    
    .bg-text {
      position: absolute; bottom: -20rpx; right: -10rpx;
      font-size: 80rpx; font-weight: 900;
      color: rgba(255,255,255,0.15);
      transform: rotate(-15deg);
      z-index: 1;
      pointer-events: none; /* 防止遮挡点击 */
    }
    
    /* 暖色渐变修改 */
    &.warm-orange { background: linear-gradient(135deg, #fad0c4 0%, #ffd1ff 100%); .icon-circle { color: #fff; } }
    &.warm-pink { background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%); .icon-circle { color: #fff; } }
  }
}

/* B. 菜单宫格 */
.menu-section {
  .section-header {
    font-size: 32rpx; font-weight: 800; color: #333;
    margin-bottom: 24rpx; display: block; padding-left: 10rpx;
    
    @media screen and (min-width: 768px) {
      font-size: 24px;
      margin-bottom: 30rpx;
    }
  }
  
  .menu-grid {
    display: grid;
    /* 移动端保持两列 */
    grid-template-columns: 1fr 1fr; 
    gap: 24rpx;

    /* PC端改为四列 */
    @media screen and (min-width: 768px) {
      grid-template-columns: repeat(4, 1fr);
      gap: 30rpx;
    }
  }
  
  .menu-card {
    background: $glass-bg;
    border-radius: 32rpx;
    padding: 30rpx;
    display: flex; align-items: center;
    border: 1px solid rgba(255,255,255,1); /* 纯白边框 */
    box-shadow: $shadow-sm;
    position: relative;
    overflow: hidden;
    transition: all 0.3s ease;
    cursor: pointer;
    
    .icon-bg {
      width: 80rpx; height: 80rpx; border-radius: 24rpx;
      display: flex; align-items: center; justify-content: center;
      font-size: 36rpx; margin-right: 20rpx;
      flex-shrink: 0;
      
      /* 暖色系背景 */
      &.c-orange { background: #FFF3E0; color: #FFB74D; }
      &.c-pink { background: #FCE4EC; color: #F48FB1; }
      &.c-purple { background: #F3E5F5; color: #BA68C8; }
      &.c-blue { background: #E3F2FD; color: #64B5F6; }
    }
    
    .menu-info {
      flex: 1;
      .title { font-size: 28rpx; font-weight: bold; color: #333; display: block; margin-bottom: 4rpx; }
      .desc { font-size: 20rpx; color: #999; }
    }
    
    .arrow { color: #eee; font-size: 24rpx; font-weight: 300; }
    
    /* 交互效果 */
    &.card-hover { transform: scale(0.98); background: #fafafa; }
    
    @media screen and (min-width: 768px) {
      padding: 40rpx 30rpx;
      &:hover {
        transform: translateY(-5px);
        box-shadow: $shadow-lg;
        background: #fff;
      }
      /* PC端箭头隐藏或调整 */
      .arrow { opacity: 0; transition: opacity 0.3s; }
      &:hover .arrow { opacity: 1; color: #FF9A9E; }
    }
  }
}

/* 动画 */
.fade-in-up { animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>