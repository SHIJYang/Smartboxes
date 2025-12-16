<template>
  <view class="user-page">
    <view class="u-header">
      <view class="avatar">
        <image v-if="user.avatar" :src="user.avatar" mode="aspectFill"></image>
        <view v-else class="avatar-placeholder">👤</view>
      </view>
      <text class="name">{{ user.username || '未登录' }}</text>
      <text class="email">{{ user.email || '暂无邮箱信息' }}</text>
    </view>
    <view class="menu">
      <view class="cell" @click="logout">
        <text class="cell-icon">🚪</text>
        <text class="cell-text">退出登录</text>
        <text class="cell-arrow">›</text>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">

import { ref, onMounted } from 'vue';
	import { onShow, onLoad } from '@dcloudio/uni-app';
const user = ref<any>({});

onShow(() => {
  const userInfo = uni.getStorageSync('userInfo');
  if (userInfo) {
    user.value = userInfo;
  } else {
    // 如果没有用户信息，跳转到登录页面
    uni.redirectTo({ url: '/pages/user/login' });
  }
});

const logout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: function (res) {
      if (res.confirm) {
        uni.removeStorageSync('userInfo');
        uni.removeStorageSync('token');
        uni.showToast({ title: '已退出登录', icon: 'success' });
        setTimeout(() => {
          uni.redirectTo({ url: '/pages/user/login' });
        }, 1000);
      }
    }
  });
};
</script>
<style>
.user-page { background: #f5f5f5; min-height: 100vh; }
.u-header { background: linear-gradient(to right, #007aff, #00bcff); padding: 40px 20px; text-align: center; margin-bottom: 10px; }
.avatar { width: 80px; height: 80px; border-radius: 50%; margin: 0 auto 15px; overflow: hidden; background: #fff; display: flex; align-items: center; justify-content: center; }
.avatar image { width: 100%; height: 100%; }
.avatar-placeholder { font-size: 40px; color: #007aff; }
.name { color: #fff; font-size: 20px; font-weight: 500; margin-bottom: 5px; }
.email { color: rgba(255, 255, 255, 0.8); font-size: 14px; }
.menu { background: #fff; margin-top: 10px; }
.cell { display: flex; align-items: center; padding: 15px 20px; border-bottom: 1px solid #eee; }
.cell-icon { font-size: 18px; margin-right: 10px; }
.cell-text { flex: 1; font-size: 16px; color: #333; }
.cell-arrow { color: #ccc; font-size: 20px; }
</style>