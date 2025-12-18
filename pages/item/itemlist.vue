<template>
  <view class="page-container">
    <view class="bg-shape shape-1"></view>

    <view class="search-section fade-in-down">
      <view class="search-bar">
        <text class="search-icon">🔍</text>
        <input 
          class="search-input"
          v-model="keyword" 
          placeholder="搜索物品名称 / 标签..." 
          placeholder-style="color: #bbb"
          confirm-type="search" 
          @confirm="search" 
        />
        <view class="search-btn" @click="search">GO</view>
      </view>
    </view>

    <scroll-view scroll-y class="result-list fade-in-up">
      <view v-for="item in list" :key="item.id" class="item-card" @click="goDetail(item.id)" hover-class="card-hover">
        <view class="icon-box">🏷️</view>
        <view class="content">
          <view class="top-row">
            <text class="name">{{ item.itemName }}</text>
            <text class="price">¥{{ item.price }}</text>
          </view>
          <view class="btm-row">
            <view class="tags" v-if="item.itemTag">
              <text class="tag">{{ item.itemTag }}</text>
            </view>
            <text class="loc">📍 {{ item.boxId }}号箱</text>
          </view>
        </view>
      </view>
      
      <view v-if="list.length === 0 && keyword" class="empty-state">
        <text>未找到相关物品</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getItemList } from '@/api/index';
import type { ItemDTO } from '@/common/types';

const keyword = ref('');
const list = ref<ItemDTO[]>([]);

// 初始化加载部分数据
onMounted(() => search());

const search = async () => {
  const res = await getItemList({ itemTag: keyword.value });
  if (res.code === 200) {
    // 简单的前端过滤模拟
    const kw = keyword.value.toLowerCase();
    list.value = res.data.filter(i => 
      !kw || 
      i.itemName.toLowerCase().includes(kw) || 
      (i.itemTag && i.itemTag.toLowerCase().includes(kw))
    );
  }
};

const goDetail = (id: number) => uni.navigateTo({ url: `/pages/item/itemedit?id=${id}` });
</script>

<style lang="scss" scoped>
$primary-color: #4facfe;

.page-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef2f3 100%);
  position: relative;
  overflow: hidden;
}

.bg-shape {
  position: absolute;
  width: 300rpx; height: 300rpx;
  background: rgba(79, 172, 254, 0.1);
  border-radius: 50%;
  top: -50rpx; left: -50rpx;
  filter: blur(80px);
}

.search-section {
  padding: 30rpx;
  z-index: 10;
}

.search-bar {
  background: #fff;
  border-radius: 50rpx;
  height: 100rpx;
  display: flex;
  align-items: center;
  padding: 0 20rpx 0 40rpx;
  box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.06);
  
  .search-icon { font-size: 32rpx; margin-right: 20rpx; }
  
  .search-input {
    flex: 1;
    height: 100%;
    font-size: 30rpx;
    color: #333;
  }
  
  .search-btn {
    background: linear-gradient(90deg, #a18cd1, #fbc2eb);
    color: #fff;
    font-size: 26rpx;
    font-weight: bold;
    padding: 12rpx 30rpx;
    border-radius: 40rpx;
    box-shadow: 0 4rpx 10rpx rgba(161, 140, 209, 0.3);
  }
}

.result-list {
  flex: 1;
  padding: 0 30rpx;
  box-sizing: border-box;
}

.item-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border-radius: 30rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  align-items: center;
  box-shadow: 0 5rpx 20rpx rgba(0,0,0,0.03);
  transition: all 0.2s;
  
  &.card-hover { transform: translateY(2rpx); box-shadow: none; }
  
  .icon-box {
    width: 80rpx; height: 80rpx;
    background: #f0f2f5;
    border-radius: 20rpx;
    display: flex; align-items: center; justify-content: center;
    font-size: 40rpx;
    margin-right: 24rpx;
  }
  
  .content { flex: 1; }
  
  .top-row {
    display: flex; justify-content: space-between; margin-bottom: 10rpx;
    .name { font-size: 32rpx; font-weight: bold; color: #333; }
    .price { font-size: 30rpx; color: #ff9800; font-weight: 500; }
  }
  
  .btm-row {
    display: flex; justify-content: space-between; align-items: center;
    .tag { 
      font-size: 20rpx; color: #4facfe; 
      background: rgba(79, 172, 254, 0.1); 
      padding: 4rpx 12rpx; border-radius: 8rpx;
    }
    .loc { font-size: 22rpx; color: #999; }
  }
}

.empty-state { text-align: center; color: #ccc; margin-top: 100rpx; font-size: 28rpx; }

.fade-in-down { animation: fadeInDown 0.6s ease-out; }
.fade-in-up { animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>