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
          placeholder="搜索物品编码 / 标签..." 
          placeholder-style="color: #bbb; font-size: 28rpx;"
          confirm-type="search" 
          @confirm="search" 
        />
        <view class="search-btn" @click="search" hover-class="btn-hover">搜索</view>
      </view>
    </view>

    <scroll-view scroll-y class="result-list fade-in-up" :show-scrollbar="false">
      <view class="list-padding">
        <view v-for="item in list" :key="item.id" class="item-card" @click="goDetail(item.id)" hover-class="card-hover">
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
              <text class="loc">📍 {{ item.boxId }}号箱</text>
            </view>
          </view>
          
          <view class="arrow">→</view>
        </view>
        
        <view v-if="list.length === 0" class="empty-state">
          <text v-if="keyword">没有找到 "{{ keyword }}" 相关的物品</text>
          <text v-else>暂时没有物品，点击右下角添加</text>
        </view>
      </view>
    </scroll-view>

    <view class="fab" @click="goAdd" hover-class="fab-hover">+</view>
  </view>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { storeToRefs } from 'pinia'; // 建议使用 storeToRefs 保持响应式
import type { ItemDTO } from '@/common/types';
import PCHeader from '@/components/PCHeader.vue';

// 引入相关的 Store
import { useItemStore } from '@/stores/itemStore';
import { useUserStore } from '@/stores/userStore';

const itemStore = useItemStore();
const userStore = useUserStore();

// 使用 storeToRefs 获取列表，这样 store 更新时页面会自动刷新
const { itemList: list } = storeToRefs(itemStore);
const keyword = ref('');

// 每次显示页面都刷新数据
onShow(() => {
  search();
});

const search = async () => {
  if (keyword.value.trim()) {
    // 如果有关键词，调用按条件查询（这里假设 fetchItemPage 会处理 queryParams）
    await itemStore.fetchItemPage({ 
      name: keyword.value,
      itemCode: keyword.value,
      page: 1 
    });
  } else {
    // 如果没有关键词，获取当前用户的全量物品
    await itemStore.fetchUserItems();
  }
};

const goDetail = (id: number) => uni.navigateTo({ url: `/pages/item/itemedit?id=${id}` });
const goAdd = () => uni.navigateTo({ url: '/pages/item/itemedit' });

// 获取显示名称
const getDisplayName = (item: ItemDTO) => {
  return item.manualEditName || item.autoRecognizeName || item.itemCode || '未命名物品';
};

// 根据标签返回 Emoji
const getItemIcon = (tag?: string) => {
  if (!tag) return '📦';
  const t = tag.toLowerCase();
  if (t.includes('衣')) return '👕';
  if (t.includes('数码') || t.includes('电子') || t.includes('机')) return '📱';
  if (t.includes('书') || t.includes('文具')) return '📚';
  if (t.includes('药') || t.includes('医')) return '💊';
  if (t.includes('食') || t.includes('吃')) return '🍎';
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
  background: $bg-color; /* 与背景同色 */
  z-index: 10;
}

.search-bar {
  background: #fff;
  border-radius: 50rpx;
  height: 90rpx;
  display: flex; align-items: center;
  padding: 0 10rpx 0 30rpx;
  box-shadow: 0 8rpx 20rpx rgba(161, 140, 209, 0.1);
  border: 2px solid #fff;
  
  .search-icon { font-size: 32rpx; margin-right: 20rpx; opacity: 0.5; }
  
  .search-input {
    flex: 1; height: 100%; font-size: 30rpx; color: #333;
  }
  
  .search-btn {
    background: $search-btn-gradient;
    color: #fff; font-size: 28rpx; font-weight: bold;
    padding: 12rpx 34rpx; border-radius: 40rpx;
    box-shadow: 0 4rpx 10rpx rgba(255, 154, 158, 0.3);
    transition: transform 0.1s;
  }
  .btn-hover { transform: scale(0.95); opacity: 0.9; }
}

.result-list {
  flex: 1;
  /* 解决滚动条问题 */
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
    .price { font-size: 30rpx; color: #FF9A9E; font-weight: bold; }
  }
  
  .btm-row {
    display: flex; justify-content: space-between; align-items: center;
    .tag { 
      font-size: 20rpx; color: #a18cd1; 
      background: #F3E5F5; 
      padding: 4rpx 12rpx; border-radius: 8rpx;
    }
    .loc { font-size: 22rpx; color: #999; }
  }

  .arrow { color: #eee; font-weight: bold; margin-left: 20rpx; }
}

.empty-state { text-align: center; color: #ccc; margin-top: 100rpx; font-size: 28rpx; }

/* 悬浮按钮 FAB */
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