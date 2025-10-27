<template>
  <view class="container">
    <u-navbar title="我的收纳空间" title-bold title-color="#333" />

    <view class="body">
      <u-empty
        v-if="!loading && spaceList.length === 0"
        text="暂无收纳空间"
        mode="data"
        class="empty-state"
      >
        <u-button type="primary" size="mini" @click="addSpace"
          >立即创建</u-button
        >
      </u-empty>

      <view v-else-if="loading" class="loading-wrap">
        <u-loading mode="circle" size="50" color="#409eff" text="加载中..." />
      </view>

      <scroll-view v-else scroll-y enable-flex class="space-list">
        <view class="grid">
          <boxcard
            v-for="space in spaceList"
            :key="space.id"
            :name="space.name || space.boxName || '未命名'"
            :category="space.category || space.boxCode || 'box'"
            :item-count="space.itemCount ?? 0"
            :location="space.location || ''"
            :last-modified="space.lastModified || space.lastHeartbeatTime || ''"
            :battery-level="space.batteryLevel || space.battery || 0"
            :is-charging="space.isCharging || false"
            @click="gotoSpace(space.id)"
            class="card"
          />
        </view>
      </scroll-view>
    </view>

    <u-fab
      icon="add"
      position="bottomRight"
      :offset="[32, 88]"
      @click="addSpace"
    />
  </view>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getBoxes } from "@/mock/api.js";
import boxcard from "@/components/boxcard.vue";

const spaceList = ref([]);
const loading = ref(false);
const listHeight = ref(0);

onMounted(() => {
  const sysInfo = uni.getSystemInfoSync();
  const statusBar = sysInfo.statusBarHeight || 0;
  // 导航高度在不同平台略有不同，兼容性处理
  const navBar = sysInfo.platform === "ios" ? 44 : 48;
  const safeBottom =
    sysInfo.safeArea && sysInfo.safeArea.bottom
      ? sysInfo.screenHeight - sysInfo.safeArea.bottom
      : 0;
  listHeight.value = sysInfo.windowHeight - statusBar - navBar - 120; // 预留顶部/底部空间
  loadData();
});

const loadData = async () => {
  loading.value = true;
  try {
    const res = await getBoxes();
    // getBoxes 返回 { code:200, data: [...] }，兼容各种实现
    if (res && res.data) {
      spaceList.value = res.data;
    } else if (Array.isArray(res)) {
      spaceList.value = res;
    } else {
      spaceList.value = [];
    }
  } catch (err) {
    uni.showToast({ title: "加载失败", icon: "none" });
    spaceList.value = [];
  } finally {
    loading.value = false;
  }
};

const gotoSpace = (id) => {
  if (!id) return;
  uni.navigateTo({ url: `/pages/space/space?id=${id}` });
};

const addSpace = () => {
  uni.showToast({ title: "功能开发中", icon: "none" });
};
</script>

<style scoped>
.container {
  min-height: 100vh;
  background-color: #f8f9fa;
  padding-bottom: env(safe-area-inset-bottom, 16px);
  display: flex;
  flex-direction: column;
}
.body {
  flex: 1;
  padding: 20rpx;
  box-sizing: border-box;
}
.loading-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60vh;
}
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60vh;
  flex-direction: column;
  gap: 12rpx;
}
.space-list {
  flex: 1;
}
.grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  padding-bottom: 20rpx;
}
.card {
  width: calc(50% - 12rpx);
  box-sizing: border-box;
}
</style>
