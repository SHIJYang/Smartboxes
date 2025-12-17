<template>
  <view class="user-container">
    <view class="bg-shape shape-1"></view>
    <view class="bg-shape shape-2"></view>

    <view class="content-wrapper" v-if="userStore.userInfo || userStore.token">
      
      <view class="user-card fade-in-down">
        <view class="avatar-box">
          <image 
            v-if="userStore.userInfo?.avatar" 
            :src="userStore.userInfo.avatar" 
            mode="aspectFill" 
            class="avatar-img"
          />
          <view v-else class="avatar-placeholder">
            {{ userStore.userInfo?.username?.charAt(0).toUpperCase() || 'U' }}
          </view>
        </view>
        
        <view class="info-box">
          <text class="username">{{ userStore.userName }}</text>
          <text class="email">{{ userStore.userInfo?.email || '暂无邮箱绑定' }}</text>
          <view class="badge">普通用户</view>
        </view>
      </view>

      <view class="menu-group fade-in-up">
        <view class="menu-item" hover-class="item-hover">
          <view class="left">
            <text class="icon">⚙️</text>
            <text class="label">系统设置</text>
          </view>
          <text class="arrow">›</text>
        </view>
        
        <view class="menu-item" hover-class="item-hover">
          <view class="left">
            <text class="icon">🔔</text>
            <text class="label">消息通知</text>
          </view>
          <text class="arrow">›</text>
        </view>

        <view class="menu-item" hover-class="item-hover">
          <view class="left">
            <text class="icon">🛡️</text>
            <text class="label">隐私安全</text>
          </view>
          <text class="arrow">›</text>
        </view>
      </view>

      <view class="action-area fade-in-up">
        <button class="btn-logout" @click="handleLogout">退出登录</button>
      </view>
    </view>
    
    <view v-else class="empty-state">
      <text>正在加载用户信息...</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app';
import { useUserStore } from '@/stores/user';

// 1. 初始化 Store
const userStore = useUserStore();

// 2. 页面显示时检查登录状态
onShow(() => {
  // 如果 Store 中没有 Token 且本地存储也没有 (即完全未登录)，跳转登录
  if (!userStore.isLoggedIn) {
    const token = uni.getStorageSync('token');
    if (!token) {
      uni.redirectTo({ url: '/pages/user/login' });
    } else {
      // 极端情况：有token但store被重置，尝试恢复（通常 store 初始化时已从 storage 读取）
      // 这里可以加一个 fetchUserInfo 的接口调用来刷新用户信息
    }
  }
});

// 3. 退出登录
const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出当前账号吗？',
    confirmColor: '#ff6b81',
    success: (res) => {
      if (res.confirm) {
        userStore.logout(); // 调用 Store 的 action
        // logout 内部已包含逻辑，但为了保险可以手动跳转
        // uni.reLaunch({ url: '/pages/user/login' }); // Store 中已包含此逻辑
      }
    }
  });
};
</script>

<style lang="scss" scoped>
/* 样式变量 */
$glass-bg: rgba(255, 255, 255, 0.75);
$shadow: 0 20rpx 40rpx rgba(0, 0, 0, 0.08);

.user-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef2f3 100%);
  position: relative;
  padding: 30rpx;
  overflow: hidden;
}

/* 背景装饰球 */
.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
}
.shape-1 { width: 300px; height: 300px; background: rgba(79, 172, 254, 0.2); top: -100px; right: -50px; }
.shape-2 { width: 200px; height: 200px; background: rgba(255, 107, 129, 0.15); bottom: 10%; left: -50px; }

.content-wrapper {
  position: relative;
  z-index: 10;
}

/* 用户卡片 */
.user-card {
  background: $glass-bg;
  backdrop-filter: blur(20px);
  border-radius: 30rpx;
  padding: 5