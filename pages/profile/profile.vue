<template>
  <view class="profile-container">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <u-avatar :src="userInfo.avatar" size="140" shape="circle" />
      <view class="user-info">
        <text class="username">{{ userInfo.nickname || "未登录" }}</text>
        <text class="user-id">ID: {{ userInfo.userId || "--" }}</text>
        <view class="user-actions">
          <u-button
            type="primary"
            size="small"
            plain
            @click="navigateTo('/pages/settings/settings')"
            >编辑资料</u-button
          >
          <u-button type="error" size="small" plain @click="logout"
            >退出</u-button
          >
        </view>
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
        icon="star"
        title="我的收藏"
        @click="navigateTo('/pages/favorite/index')"
      />
      <u-cell
        icon="time"
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
import { ref, onMounted } from "vue";
import http from "@/utils/request";
import { platform } from "@/utils/platform";

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

const cacheSize = ref("0MB");

const navigateTo = (url) => {
  if (!url) return;
  uni.navigateTo({ url });
};

// APP专属方法
const checkUpdate = () => {
  if (!platform.isApp) {
    uni.showToast({ title: "仅限 App 平台", icon: "none" });
    return;
  }
  // TODO: 检查更新逻辑（占位）
  uni.showToast({ title: "检查更新（演示）", icon: "none" });
};

// 小程序专属方法
const feedback = () => {
  if (!platform.isWeapp) {
    uni.showToast({ title: "仅限微信小程序", icon: "none" });
    return;
  }
  // 跳转到反馈页面或打开弹窗（占位）
  uni.showToast({ title: "打开反馈页面", icon: "none" });
};

const logout = () => {
  // 简单退出演示：清缓存并返回登录页（占位）
  uni.clearStorageSync();
  uni.showToast({ title: "已退出", icon: "none" });
  // navigateTo('/pages/login/index')
};

const clearCache = () => {
  // 占位：计算并清理缓存
  uni.clearStorageSync();
  cacheSize.value = "0MB";
  uni.showToast({ title: "缓存已清理", icon: "none" });
};

onMounted(async () => {
  try {
    const res = await http.get("/user/info");
    // request 返回的是 data 对象（兼容 mock 与真实接口）
    if (res) {
      userInfo.value.avatar = res.avatar || userInfo.value.avatar;
      userInfo.value.nickname = res.nickname || userInfo.value.nickname;
      userInfo.value.userId = res.id || userInfo.value.userId;
      if (res.stats) {
        stats.value.boxCount = res.stats.boxCount ?? 0;
        stats.value.itemCount = res.stats.itemCount ?? 0;
        stats.value.categoryCount = res.stats.categoryCount ?? 0;
      }
    }
  } catch (err) {
    uni.showToast({ title: "用户数据加载失败", icon: "none" });
  }
});
</script>

<style scoped lang="scss">
.profile-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: env(safe-area-inset-bottom, 16px);
}
.user-card {
  display: flex;
  align-items: center;
  padding: 40rpx 30rpx 30rpx 30rpx;
  background: #fff;
  border-radius: 18rpx;
  margin: 24rpx 18rpx 0 18rpx;
  box-shadow: 0 4rpx 16rpx #e4e7ed44;
}
.user-info {
  margin-left: 30rpx;
  flex: 1;
}
.username {
  font-size: 36rpx;
  font-weight: bold;
  color: #222;
}
.user-id {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}
.user-actions {
  margin-top: 18rpx;
  display: flex;
  gap: 18rpx;
}
.stats-grid {
  display: flex;
  padding: 30rpx 18rpx;
  background: #fff;
  margin: 18rpx 18rpx 0 18rpx;
  border-radius: 18rpx;
  box-shadow: 0 2rpx 8rpx #e4e7ed22;
}
.stat-item {
  flex: 1;
  text-align: center;
}
.stat-num {
  font-size: 38rpx;
  font-weight: bold;
  color: #2979ff;
}
.stat-label {
  font-size: 24rpx;
  color: #666;
  margin-top: 10rpx;
}
.share-btn {
  background: none;
  margin: 0;
  padding: 0;
  line-height: 1;
  &::after {
    border: none;
  }
}
</style>
