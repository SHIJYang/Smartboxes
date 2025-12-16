<template>
  <view class="page">
    <view class="banner">
      <text class="welcome">欢迎回来，{{ user?.username || '用户' }}</text>
      <text class="sub">您的智能物品管家已就绪</text>
    </view>
    <view class="stats">
      <view class="stat-card">
        <text class="stat-value">{{ stats.boxes }}</text>
        <text class="stat-label">盒子数量</text>
      </view>
      <view class="stat-card">
        <text class="stat-value">{{ stats.items }}</text>
        <text class="stat-label">物品总数</text>
      </view>
    </view>
    <view class="nav-grid">
      <view class="nav-item" @click="go('/pages/box/boxlist')">
        <text class="nav-icon">📦</text>
        <text class="nav-text">盒子管理</text>
      </view>
      <view class="nav-item" @click="go('/pages/item/itemlist')">
        <text class="nav-icon">🔍</text>
        <text class="nav-text">物品查找</text>
      </view>
      <view class="nav-item" @click="go('/pages/chat/chat')">
        <text class="nav-icon">🤖</text>
        <text class="nav-text">AI 助手</text>
      </view>
      <view class="nav-item" @click="go('/pages/user/user')">
        <text class="nav-icon">👤</text>
        <text class="nav-text">个人中心</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import type { UserDTO } from '@/common/types';
import { getBoxList, getItemList } from '@/api/index';

const user = ref<UserDTO | null>(null);
const stats = ref({
  boxes: 0,
  items: 0
});

onMounted(async () => {
  const u = uni.getStorageSync('userInfo');
  if (u) {
    user.value = u;
    // 加载统计信息
    await loadStats(u.id);
  } else {
    uni.navigateTo({ url: '/pages/user/login' });
  }
});

const loadStats = async (userId: number) => {
  try {
    // 获取盒子数量
    const boxRes = await getBoxList(userId);
    if (boxRes.code === 200) {
      stats.value.boxes = boxRes.data.length;
    }
    
    // 获取物品数量
    const itemRes = await getItemList({});
    if (itemRes.code === 200) {
      stats.value.items = itemRes.data.length;
    }
  } catch (error) {
    console.error('加载统计信息失败:', error);
  }
};

const go = (url: string) => {
  // 对于 tab 页面使用 switchTab，其他页面使用 navigateTo
  if (url === '/pages/index/index' || url === '/pages/box/boxlist' || url === '/pages/chat/chat' || url === '/pages/user/user') {
    uni.switchTab({ url });
  } else {
    uni.navigateTo({ url });
  }
};
</script>

<style>
.page { padding: 20px; background: #f5f5f5; min-height: 100vh; }
.banner { background: linear-gradient(to right, #007aff, #00bcff); padding: 30px 20px; border-radius: 12px; color: #fff; margin-bottom: 20px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }
.welcome { font-size: 22px; font-weight: bold; display: block; margin-bottom: 5px; }
.sub { font-size: 14px; opacity: 0.9; }
.stats { display: flex; gap: 15px; margin-bottom: 20px; }
.stat-card { flex: 1; background: #fff; padding: 20px; border-radius: 12px; text-align: center; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }
.stat-value { font-size: 24px; font-weight: bold; color: #007aff; display: block; }
.stat-label { font-size: 14px; color: #666; }
.nav-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.nav-item { background: #fff; padding: 25px 10px; text-align: center; border-radius: 12px; font-size: 16px; font-weight: 500; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); transition: all 0.2s ease; }
.nav-item:active { transform: scale(0.98); background: #f0f8ff; }
.nav-icon { font-size: 24px; display: block; margin-bottom: 8px; }
.nav-text { color: #333; }
</style>