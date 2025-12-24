<template>
  <view class="container">
    <view class="search-bar">
      <input 
        class="search-input" 
        v-model="searchKeyword" 
        placeholder="输入标签或编码搜索" 
        confirm-type="search"
        @confirm="doSearch"
      />
      <view class="search-btn" @click="doSearch">搜索</view>
    </view>

    <scroll-view scroll-y class="result-list">
      <view v-for="item in resultList" :key="item.id" class="item-card">
        <view class="item-main">
          <text class="name">{{ getDisplayName(item) }}</text>
          <text class="code" v-if="item.itemCode">#{{ item.itemCode }}</text>
          <text class="tag" v-if="item.itemTag">{{ item.itemTag }}</text>
        </view>
        <view class="item-sub">
          <text>位置: {{ item.boxId }}号箱</text>
          <text v-if="item.autoRecognizeName">自动识别: {{ item.autoRecognizeName }}</text>
        </view>
      </view>
      
      <view v-if="hasSearched && resultList.length === 0" class="empty">
        <text>未找到相关物品</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { getItemList } from '@/api/index';
import type { ItemDTO } from '@/common/types';

const searchKeyword = ref('');
const resultList = ref<ItemDTO[]>([]);
const hasSearched = ref(false);

const doSearch = async () => {
  uni.showLoading({ title: '搜索中' });
  hasSearched.value = true;
  
  try {
    const res = await getItemList({});
    
    if (res.code === 200) {
      // 前端过滤实现搜索
      const keyword = searchKeyword.value.toLowerCase();
      resultList.value = res.data.filter(item => 
        (item.itemCode && item.itemCode.toLowerCase().includes(keyword)) ||
        (item.itemTag && item.itemTag.toLowerCase().includes(keyword)) ||
        (item.autoRecognizeName && item.autoRecognizeName.toLowerCase().includes(keyword)) ||
        (item.manualEditName && item.manualEditName.toLowerCase().includes(keyword))
      );
    }
  } catch (e) {
    uni.showToast({ title: '搜索失败', icon: 'none' });
  } finally {
    uni.hideLoading();
  }
};

// 获取显示名称
const getDisplayName = (item: ItemDTO) => {
  if (item.manualEditName) return item.manualEditName;
  if (item.autoRecognizeName) return item.autoRecognizeName;
  return item.itemCode || '未命名物品';
};
</script>

<style lang="scss">
.container { background: #f5f7fa; min-height: 100vh; display: flex; flex-direction: column; }
.search-bar {
  padding: 10px 15px; background: #fff; display: flex; align-items: center;
  .search-input { 
    flex: 1; background: #f2f2f2; height: 36px; border-radius: 18px; 
    padding: 0 15px; font-size: 14px; 
  }
  .search-btn { margin-left: 10px; color: #007aff; font-size: 16px; }
}
.result-list { flex: 1; padding: 15px; }
.item-card {
  background: #fff; padding: 15px; border-radius: 8px; margin-bottom: 10px;
  .item-main {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;
    .name { font-size: 16px; font-weight: bold; flex: 1; margin-right: 10px; }
    .code { font-size: 12px; color: #666; background: #f2f2f2; padding: 2px 6px; border-radius: 4px; margin-right: 8px; }
    .tag { font-size: 12px; background: #e1f3d8; color: #67c23a; padding: 2px 6px; border-radius: 4px; }
  }
  .item-sub { font-size: 13px; color: #999; display: flex; gap: 15px; }
}
.empty { text-align: center; color: #999; margin-top: 50px; }
</style>