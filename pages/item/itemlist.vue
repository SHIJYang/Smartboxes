<template>
  <view class="container">
    <view class="search-box">
      <input v-model="keyword" placeholder="搜索物品名称/标签" confirm-type="search" @confirm="search" />
      <text @click="search">搜索</text>
    </view>
    <view class="list">
      <view v-for="i in list" :key="i.id" class="item-card" @click="goDetail(i.id)">
        <text class="name">{{ i.itemName }}</text>
        <text class="tag">{{ i.itemTag }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { getItemList } from '@/api/index';
import type { ItemDTO } from '@/common/types';

const keyword = ref('');
const list = ref<ItemDTO[]>([]);

const search = async () => {
  // 模拟搜索: 获取所有 Mock 数据然后在前端过滤 (仅演示用)
  const res = await getItemList({ itemTag: keyword.value });
  if (res.code === 200) {
    list.value = res.data.filter(i => 
      !keyword.value || i.itemName.includes(keyword.value) || (i.itemTag && i.itemTag.includes(keyword.value))
    );
  }
};

const goDetail = (id: number) => uni.navigateTo({ url: `/pages/item/itemedit/itemedit?id=${id}` });
</script>

<style>
.container { padding: 15px; background-color: #f5f5f5; min-height: 100vh; }
.search-box { display: flex; align-items: center; background: #fff; padding: 5px 15px; border-radius: 20px; margin-bottom: 15px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }
.search-box input { flex: 1; margin-right: 10px; height: 36px; font-size: 14px; }
.search-box text { color: #007aff; font-size: 14px; font-weight: 500; }
.item-card { background: #fff; padding: 15px; margin-bottom: 10px; border-radius: 8px; display: flex; justify-content: space-between; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }
.name { font-size: 16px; color: #333; font-weight: 500; }
.tag { font-size: 12px; color: #007aff; background: #e1f3d8; padding: 3px 8px; border-radius: 12px; font-weight: 500; }
</style>