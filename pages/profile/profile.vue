<template>
  <view class="profile-container" :class="{ 'platform-container': true }">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <u-avatar :src="userInfo.avatar" size="120" />
      <view class="user-info">
        <text class="username">{{ userInfo.nickname || "未登录" }}</text>
        <text class="user-id">ID: {{ userInfo.userId || "--" }}</text>
      </view>
    </view>

    <!-- 数据概览 -->
    <view class="stats-grid">
      <view class="stat-item">
        <text class="stat-num">{{ stats.boxCount }}</text>
        <text class="stat-label">收纳盒</text>
      </view>
      <view class="stat-item">
        <text class="stat-num">{{ stats.itemCount }}</text>
        <text class="stat-label">物品</text>
      </view>
      <view class="stat-item">
        <text class="stat-num">{{ stats.categoryCount }}</text>
        <text class="stat-label">分类</text>
      </view>
    </view>

    <!-- 功能列表 -->
    <u-cell-group>
      <u-cell
        icon="edit"
        title="我的收藏"
        @click="navigateTo('/pages/favorite/index')"
      />
      <u-cell
        icon="clock"
        title="操作历史"
        @click="navigateTo('/pages/history/index')"
      />
      <u-cell
        icon="setting"
        title="设置"
        @click="navigateTo('/pages/settings/settings')"
      />
    </u-cell-group>

    <!-- 平台特定功能 -->
    <!-- #ifdef APP-PLUS -->
    <u-cell-group margin-top="20">
      <u-cell title="检查更新" @click="checkUpdate" />
      <u-cell title="清理缓存" :value="cacheSize" @click="clearCache" />
    </u-cell-group>
    <!-- #endif -->

    <!-- #ifdef MP-WEIXIN -->
    <u-cell-group margin-top="20">
      <button class="share-btn" open-type="share">
        <u-cell title="分享给好友" icon="share" />
      </button>
      <u-cell title="意见反馈" @click="feedback" />
    </u-cell-group>
    <!-- #endif -->
  </view>
</template>

<script setup>
import { ref } from "vue";

const userInfo = ref({
  avatar: "/static/avatar/default.png",
  nickname: "默认用户",
  userId: "10001",
});

const stats = ref({
  boxCount: 0,
  itemCount: 0,
  categoryCount: 0,
});

const navigateTo = (url) => {
  uni.navigateTo({ url });
};

// APP专属方法
const checkUpdate = () => {
  if (!platform.isApp) return;
  // TODO: 检查更新逻辑
};

// 小程序专属方法
const feedback = () => {
  if (!platform.isWeapp) return;
  uni.navigateToMiniProgram({
    shortLink: "微信反馈模板路径",
  });
};
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background: #f5f5f5;
}
.user-card {
  display: flex;
  align-items: center;
  padding: 40rpx;
  background: #fff;
}
.user-info {
  margin-left: 30rpx;
}
.username {
  font-size: 32rpx;
  font-weight: bold;
}
.user-id {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}
.stats-grid {
  display: flex;
  padding: 30rpx;
  background: #fff;
  margin-top: 20rpx;
}
.stat-item {
  flex: 1;
  text-align: center;
}
.stat-num {
  font-size: 36rpx;
  font-weight: bold;
  color: #2979ff;
}
.stat-label {
  font-size: 24rpx;
  color: #666;
  margin-top: 10rpx;
}
</style>

.share-btn { background: none; margin: 0; padding: 0; line-height: 1; &::after {
border: none; } }
