{
type: uploaded file
fileName: itemlist.vue
fullContent:
<template>
  <view class="page-container">
    <PCHeader current="item" />
    <view class="pc-placeholder"></view>
    
    <view class="search-section fade-in-down">
      <view class="search-bar">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          v-model="keyword"
          placeholder="搜索物品名称 / 编码 / 标签..."
          placeholder-style="color: #bbb; font-size: 28rpx;"
          confirm-type="search"
          @confirm="onSearch"
        />
        <view class="scan-btn" @click="onScan">
            <text class="scan-icon">📷</text>
        </view>
      </view>
    </view>

    <scroll-view 
      scroll-y 
      class="result-list fade-in-up" 
      :show-scrollbar="false"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="list-padding">
        <view v-for="item in displayList" :key="item.id" class="item-card" @click="goDetail(item.id)" hover-class="card-hover">
          <view class="icon-box">
            <text>{{ getItemIcon(item.itemTag) }}</text>
          </view>
          <view class="content">
            <view class="top-row">
              <text class="name">{{ getDisplayName(item) }}</text>
              <text class="code" v-if="item.itemCode">#{{ item.itemCode }}</text>
            </view>
            <view class="btm-row">
              <view class="tags" v-if="item.itemTag">
                <text class="tag">{{ item.itemTag }}</text>
              </view>
              <text class="loc" v-if="item.boxId">📍 {{ item.boxId }}号箱</text>
              <text class="loc removed" v-else>📤 已取出</text>
            </view>
          </view>
          <view class="arrow">→</view>
        </view>

        <view v-if="displayList.length === 0" class="empty-state">
          <image src="/static/empty-box.png" mode="aspectFit" class="empty-img" v-if="false"/> 
          <text v-if="keyword">没有找到 "{{ keyword }}" 相关的物品 🧣</text>
          <text v-else>暂时没有物品，点击右下角添加一个吧 ✨</text>
        </view>
      </view>
    </scroll-view>

    <view class="fab" @click="goAdd" hover-class="fab-hover">+</view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { useItemStore } from '@/stores/itemStore';
import type { ItemDTO } from '@/common/types';
import PCHeader from '@/components/PCHeader.vue';

const store = useItemStore();
const keyword = ref('');
const isRefreshing = ref(false);

// Use the Getter from store for filtering
const displayList = computed(() => {
  if (keyword.value.trim()) {
    // FIX: Use searchLocalItems (getter) instead of filterLocalItems
    return store.searchLocalItems(keyword.value);
  }
  return store.itemList;
});

const loadData = async () => {
  // Reset specific query params to ensure we get 'all' or 'default' list
  store.updateQueryParams({ boxId: undefined }); 
  await store.fetchItemPage({ page: 1, size: 20 }); // Or fetchItemList for all
};

onShow(() => {
  loadData();
});

const onRefresh = async () => {
  isRefreshing.value = true;
  await loadData();
  setTimeout(() => {
    isRefreshing.value = false;
    uni.showToast({ title: '刷新成功', icon: 'none' });
  }, 500);
};

const onSearch = () => {
  // Triggered by Enter key, logic handled by computed
};

const onScan = () => {
  uni.scanCode({
    success: (res) => {
        keyword.value = res.result;
        uni.showToast({ title: '已识别: ' + res.result, icon: 'none' });
    }
  });
};

const goDetail = (id: number) => uni.navigateTo({ url: `/pages/item/itemedit?id=${id}` });
const goAdd = () => uni.navigateTo({ url: '/pages/item/itemedit' });

const getDisplayName = (item: ItemDTO) => {
  if (item.manualEditName) return item.manualEditName;
  if (item.autoRecognizeName) return item.autoRecognizeName;
  return item.itemCode || '未命名物品';
};

const getItemIcon = (tag?: string) => {
  if (!tag) return '📦';
  if (tag.includes('衣')) return '👕';
  if (tag.includes('数码') || tag.includes('电子')) return '📱';
  if (tag.includes('书')) return '📚';
  if (tag.includes('药')) return '💊';
  if (tag.includes('食')) return '🍟';
  return '🧸';
};
</script>

<style lang="scss" scoped>
/* 暖色主题 */
$bg-color: #FFF9F0;
$primary-pink: #FF9A9E;
$search-btn-gradient: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);

.page-container {
  height: 100vh;
  display: flex; flex-direction: column;
  background-color: $bg-color;
  position: relative;
}

.pc-placeholder {
  display: none; height: 80px;
  @media screen and (min-width: 768px) { display: block; }
}

.search-section {
  padding: 20rpx 30rpx;
  background: $bg-color;
  z-index: 10;
}

.search-bar {
  background: #fff;
  border-radius: 50rpx;
  height: 90rpx;
  display: flex; align-items: center;
  padding: 0 20rpx 0 30rpx;
  box-shadow: 0 8rpx 20rpx rgba(161, 140, 209, 0.1);
  border: 2px solid #fff;
  
  .search-icon { font-size: 32rpx; margin-right: 20rpx; opacity: 0.5; }
  
  .search-input {
    flex: 1; height: 100%; font-size: 30rpx; color: #333;
  }

  .scan-btn {
      width: 60rpx; height: 60rpx;
      display: flex; align-items: center; justify-content: center;
      margin-left: 10rpx;
      opacity: 0.7;
      &:active { opacity: 1; transform: scale(0.9); }
  }
}

.result-list {
  flex: 1;
  overflow: hidden; 
}

.list-padding { padding: 10rpx 30rpx 150rpx; }

.item-card {
  background: #fff;
  border-radius: 32rpx;
  padding: 30rpx;
  margin-bottom: 24rpx;
  display: flex; align-items: center;
  box-shadow: 0 4rpx 15rpx rgba(0,0,0,0.02);
  border: 1px solid rgba(255,255,255,0.6);
  transition: all 0.1s;
  
  &.card-hover { transform: scale(0.98); background: #fafafa; }
  
  .icon-box {
    width: 90rpx; height: 90rpx;
    background: #FFF0F5; color: #333;
    border-radius: 24rpx; display: flex; align-items: center; justify-content: center;
    font-size: 44rpx; margin-right: 24rpx;
  }
  
  .content { flex: 1; }
  
  .top-row {
    display: flex; justify-content: space-between; margin-bottom: 8rpx;
    .name { font-size: 32rpx; font-weight: bold; color: #333; }
    .code { font-size: 24rpx; color: #ccc; font-family: monospace; }
  }
  
  .btm-row {
    display: flex; align-items: center;
    .tags { margin-right: 15rpx; }
    .tag { 
      font-size: 20rpx; color: #a18cd1; 
      background: #F3E5F5; 
      padding: 4rpx 12rpx; border-radius: 8rpx;
    }
    .loc { font-size: 22rpx; color: #999; }
    .loc.removed { color: #FF9A9E; }
  }

  .arrow { color: #eee; font-weight: bold; margin-left: 20rpx; }
}

.empty-state { 
    text-align: center; color: #ccc; margin-top: 100rpx; font-size: 28rpx; 
    display: flex; flex-direction: column; align-items: center; gap: 20rpx;
}

.fab {
  position: fixed; bottom: 100rpx; right: 40rpx;
  width: 110rpx; height: 110rpx; border-radius: 50%;
  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
  color: #fff; font-size: 60rpx; font-weight: 300;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 10rpx 25rpx rgba(161, 140, 209, 0.4);
  z-index: 100;
  
  &.fab-hover { transform: scale(0.9); }
  
  @media screen and (min-width: 768px) {
    right: 80rpx; bottom: 80rpx;
  }
}

.fade-in-down { animation: fadeInDown 0.6s ease-out; }
.fade-in-up { animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>
}