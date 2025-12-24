<template>
  <view class="user-container">
    <PCHeader current="user" />
    <view class="pc-placeholder"></view>

    <view class="bg-shape shape-1"></view>
    <view class="bg-shape shape-2"></view>

    <view class="content-wrapper" v-if="userStore.currentUser && userStore.isLoggedIn">
      
      <view class="user-card fade-in-down">
        <view class="avatar-box">
          <image 
            v-if="userStore.currentUser?.avatar" 
            :src="userStore.currentUser.avatar" 
            mode="aspectFill" 
            class="avatar-img"
          />
          <view v-else class="avatar-placeholder">
            {{ userStore.currentUser?.username?.charAt(0).toUpperCase() || 'U' }}
          </view>
        </view>
        
        <view class="info-box">
          <text class="username">{{ userStore.currentUser?.username || '魔法学徒' }}</text>
          <text class="email">{{ userStore.currentUser?.email || '点击绑定魔法信箱' }}</text>
          <view class="badge">✨ Lv.1 见习收纳师</view>
        </view>
      </view>

      <view class="menu-group fade-in-up">
        <view class="menu-item" hover-class="item-hover">
          <view class="left">
            <text class="icon">⚙️</text>
            <text class="label">设置</text>
          </view>
          <text class="arrow">›</text>
        </view>
        
        <view class="menu-item" hover-class="item-hover">
          <view class="left">
            <text class="icon">🔔</text>
            <text class="label">通知</text>
          </view>
          <text class="arrow">›</text>
        </view>

        <view class="menu-item" hover-class="item-hover">
          <view class="left">
            <text class="icon">🛡️</text>
            <text class="label">隐私</text>
          </view>
          <text class="arrow">›</text>
        </view>
      </view>

      <view class="action-area fade-in-up">
        <button class="btn-logout" hover-class="btn-hover" @click="handleLogout">退出当前世界</button>
      </view>
    </view>
    
    <view v-else class="empty-state">
      <text>正在召唤用户信息...</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app';
import PCHeader from '@/components/PCHeader.vue';
import { useStores } from '@/stores';

const { userStore } = useStores();

onShow(() => {
  if (!userStore.isLoggedIn) {
    // 检查是否有token
    userStore.checkLoginStatus();
    
    if (!userStore.isLoggedIn) {
      uni.redirectTo({ url: '/pages/user/login' });
    }
  }
});

const handleLogout = () => {
  uni.showModal({
    title: '离开',
    content: '要暂时离开魔法世界吗？',
    confirmColor: '#FF9A9E',
    success: (res) => {
      if (res.confirm) {
        userStore.logout();
      }
    }
  });
};
</script>

<style lang="scss" scoped>
/* 暖色主题 */
$bg-color: #FFF9F0;
$glass-bg: rgba(255, 255, 255, 0.85);
$shadow-soft: 0 10rpx 30rpx rgba(161, 140, 209, 0.15);
$primary-pink: #FF9A9E;

.user-container {
  min-height: 100vh;
  background-color: $bg-color;
  position: relative;
  padding: 30rpx;
  overflow: hidden;
  box-sizing: border-box;
}

.pc-placeholder {
  display: none; height: 80px;
  @media screen and (min-width: 768px) { display: block; }
}

/* 背景装饰球 */
.bg-shape {
  position: absolute; border-radius: 50%; filter: blur(80px); z-index: 0; pointer-events: none;
}
.shape-1 { width: 400rpx; height: 400rpx; background: rgba(255, 154, 158, 0.15); top: -50rpx; right: -50rpx; }
.shape-2 { width: 300rpx; height: 300rpx; background: rgba(161, 140, 209, 0.15); bottom: 100rpx; left: -50rpx; }

.content-wrapper { position: relative; z-index: 10; }

.user-card {
  background: $glass-bg; backdrop-filter: blur(20px);
  border-radius: 40rpx; padding: 50rpx 40rpx;
  display: flex; align-items: center;
  box-shadow: $shadow-soft; margin-bottom: 40rpx;
  border: 2px solid #fff;

  .avatar-box {
    width: 130rpx; height: 130rpx; border-radius: 50%;
    margin-right: 30rpx; box-shadow: 0 10rpx 20rpx rgba(0,0,0,0.05);
    overflow: hidden; background: #fff; flex-shrink: 0; border: 4rpx solid #fff;

    .avatar-img { width: 100%; height: 100%; }
    .avatar-placeholder {
      width: 100%; height: 100%;
      background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
      color: #fff; display: flex; align-items: center; justify-content: center;
      font-size: 50rpx; font-weight: bold;
    }
  }

  .info-box {
    flex: 1; display: flex; flex-direction: column;
    .username { font-size: 38rpx; font-weight: 900; color: #333; margin-bottom: 8rpx; }
    .email { font-size: 24rpx; color: #888; margin-bottom: 16rpx; }
    .badge {
      align-self: flex-start;
      background: #FFF0F5; color: $primary-pink;
      font-size: 22rpx; font-weight: bold;
      padding: 6rpx 20rpx; border-radius: 20rpx;
    }
  }
}

.menu-group {
  background: #fff; border-radius: 40rpx;
  padding: 10rpx 30rpx; box-shadow: $shadow-soft; margin-bottom: 50rpx;

  .menu-item {
    display: flex; justify-content: space-between; align-items: center;
    padding: 36rpx 0; border-bottom: 1px solid #f9f9f9;
    
    &:last-child { border-bottom: none; }
    &.item-hover { opacity: 0.6; }

    .left {
      display: flex; align-items: center;
      .icon { font-size: 38rpx; margin-right: 24rpx; }
      .label { font-size: 30rpx; color: #444; font-weight: bold; }
    }
    .arrow { color: #ddd; font-size: 32rpx; font-family: monospace; }
  }
}

.action-area {
  padding: 0 20rpx;
  .btn-logout {
    background: #fff; color: $primary-pink;
    border: 2rpx solid #ffebee; border-radius: 50rpx;
    font-size: 32rpx; font-weight: bold;
    height: 100rpx; line-height: 100rpx;
    box-shadow: 0 10rpx 20rpx rgba(255, 154, 158, 0.1);
    &::after { border: none; }
    &.btn-hover { background: #FFF5F7; transform: scale(0.99); }
  }
}

.empty-state { display: flex; justify-content: center; align-items: center; height: 80vh; color: #ccc; font-size: 28rpx; }

.fade-in-down { animation: fadeInDown 0.8s ease-out; }
.fade-in-up { animation: fadeInUp 0.8s ease-out; }
@keyframes fadeInDown { from { opacity: 0; transform: translateY(-30px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
</style>