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
          <text class="username">{{ userStore.userName || '用户' }}</text>
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
        <button class="btn-logout" hover-class="btn-hover" @click="handleLogout">退出登录</button>
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
      // 极端情况：有token但store被重置，尝试恢复
      // userStore.fetchUserInfo(); // 实际项目中可以在这里补充获取用户信息的逻辑
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
        // 确保跳转回登录页，根据 store 实现情况，有时需要手动跳转
        uni.reLaunch({ url: '/pages/user/login' });
      }
    }
  });
};
</script>

<style lang="scss" scoped>
/* 样式变量 */
$glass-bg: rgba(255, 255, 255, 0.75);
$shadow: 0 20rpx 40rpx rgba(0, 0, 0, 0.08);
$text-main: #333;
$text-sub: #999;

.user-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef2f3 100%);
  position: relative;
  padding: 30rpx;
  overflow: hidden;
  box-sizing: border-box;
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
  padding: 40rpx; /* 修复了这里被截断的 padding */
  display: flex;
  align-items: center;
  box-shadow: $shadow;
  margin-bottom: 40rpx;
  border: 1px solid rgba(255,255,255,0.6);

  .avatar-box {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    margin-right: 30rpx;
    box-shadow: 0 10rpx 20rpx rgba(0,0,0,0.1);
    overflow: hidden;
    background: #fff;
    flex-shrink: 0;

    .avatar-img {
      width: 100%;
      height: 100%;
    }

    .avatar-placeholder {
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 48rpx;
      font-weight: bold;
    }
  }

  .info-box {
    flex: 1;
    display: flex;
    flex-direction: column;

    .username {
      font-size: 36rpx;
      font-weight: bold;
      color: $text-main;
      margin-bottom: 8rpx;
    }

    .email {
      font-size: 24rpx;
      color: $text-sub;
      margin-bottom: 12rpx;
    }

    .badge {
      align-self: flex-start;
      background: rgba(79, 172, 254, 0.15);
      color: #4facfe;
      font-size: 20rpx;
      padding: 4rpx 16rpx;
      border-radius: 20rpx;
    }
  }
}

/* 菜单组 */
.menu-group {
  background: #fff;
  border-radius: 30rpx;
  padding: 10rpx 30rpx;
  box-shadow: $shadow;
  margin-bottom: 50rpx;

  .menu-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 34rpx 0;
    border-bottom: 1px solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }

    &.item-hover {
      opacity: 0.7;
      background-color: #f9f9f9; 
    }

    .left {
      display: flex;
      align-items: center;

      .icon {
        font-size: 36rpx;
        margin-right: 24rpx;
      }
      .label {
        font-size: 30rpx;
        color: #333;
      }
    }

    .arrow {
      color: #ccc;
      font-size: 32rpx;
      font-family: monospace;
    }
  }
}

/* 退出按钮 */
.action-area {
  padding: 0 20rpx;

  .btn-logout {
    background: #fff;
    color: #ff6b81;
    border: 2rpx solid #ff6b81;
    border-radius: 50rpx;
    font-size: 32rpx;
    font-weight: bold;
    height: 90rpx;
    line-height: 90rpx;
    box-shadow: 0 10rpx 20rpx rgba(255, 107, 129, 0.15);
    
    &::after { border: none; }
    
    &.btn-hover {
      background: #ff6b81;
      color: #fff;
      transform: scale(0.99);
    }
  }
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  color: #999;
  font-size: 28rpx;
}

/* 进场动画 */
.fade-in-down {
  animation: fadeInDown 0.8s ease-out;
}
.fade-in-up {
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-30px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>