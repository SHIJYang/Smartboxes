<template>
  <view class="container">
    <view class="search-bar">
      <input 
        class="search-input" 
        v-model="searchKeyword" 
        placeholder="输入标签或名称搜索" 
        confirm-type="search"
        @confirm="doSearch"
      />
      <view class="search-btn" @click="doSearch">搜索</view>
    </view>

    <scroll-view scroll-y class="result-list">
      <view v-for="item in resultList" :key="item.id" class="item-card">
        <view class="item-main">
          <text class="name">{{ item.itemName }}</text>
          <text class="tag" v-if="item.itemTag">{{ item.itemTag }}</text>
        </view>
        <view class="item-sub">
          <text>位置: {{ item.boxId }}号箱</text>
          <text>价格: ¥{{ item.price }}</text>
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
    // 调用 GET /api/items/list，这里简单地将关键词作为 itemTag 传递
    // 实际业务中可能需要后端支持模糊搜索或前端过滤 Mock 数据
    const res = await getItemList({ 
      itemTag: searchKeyword.value 
    });

    if (res.code === 200) {
      // **重要**：如果是 Mock 模式，前端做一个简单的过滤模拟真实搜索
      // 如果是真实后端，这步由后端 SQL 完成
      const keyword = searchKeyword.value.toLowerCase();
      resultList.value = res.data.filter(item => 
        (item.itemName && item.itemName.includes(keyword)) ||
        (item.itemTag && item.itemTag.includes(keyword))
      );
    }
  } catch (e) {
    uni.showToast({ title: '搜索失败', icon: 'none' });
  } finally {
    uni.hideLoading();
  }
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
    display: flex; justify-content: space-between; margin-bottom: 8px;
    .name { font-size: 16px; font-weight: bold; }
    .tag { font-size: 12px; background: #e1f3d8; color: #67c23a; padding: 2px 6px; border-radius: 4px; }
  }
  .item-sub { font-size: 13px; color: #999; display: flex; gap: 15px; }
}
.empty { text-align: center; color: #999; margin-top: 50px; }
</style>