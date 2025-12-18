<template>
  <view class="page-container" v-if="box">
    <view class="bg-shape shape-1"></view>

    <view class="header fade-in-down">
      <view class="title-row">
        <text class="box-name">{{ box.boxName }}</text>
        <view class="edit-btn" @click="goEdit">设置</view>
      </view>
      
      <view class="status-grid">
        <view class="status-item">
          <text class="label">状态</text>
          <text class="value" :class="box.status===1?'on':'off'">{{ box.status===1?'在线':'离线' }}</text>
        </view>
        <view class="divider"></view>
        <view class="status-item">
          <text class="label">剩余电量</text>
          <text class="value battery">{{ box.battery }}%</text>
        </view>
        <view class="divider"></view>
        <view class="status-item">
          <text class="label">信号强度</text>
          <text class="value">{{ box.rssi || '-60' }}dBm</text>
        </view>
      </view>
    </view>

    <view class="content-body fade-in-up">
      <view class="section-header">
        <text class="section-title">盒内物品 ({{ items.length }})</text>
        <view class="add-mini-btn" @click="addItem">+ 添加</view>
      </view>

      <view class="item-list">
        <view v-for="item in items" :key="item.id" class="item-row" @click="goItem(item.id)" hover-class="row-hover">
          <view class="item-left">
            <view class="tag-dot"></view>
            <view>
              <text class="item-name">{{ item.itemName }}</text>
              <text class="item-tag" v-if="item.itemTag">{{ item.itemTag }}</text>
            </view>
          </view>
          <text class="item-price">¥{{ item.price }}</text>
        </view>
        
        <view v-if="items.length === 0" class="empty-tip">
          盒子是空的，快放点东西吧~
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { getBoxDetail, getItemList } from '@/api/index';
import type { BoxDTO, ItemDTO } from '@/common/types';

const box = ref<BoxDTO>();
const items = ref<ItemDTO[]>([]);
const boxId = ref(0);

// 使用 onShow 保证返回时刷新
onShow(() => {
  if(boxId.value) loadData();
});

onLoad((opt: any) => {
  if (opt.id) {
    boxId.value = parseInt(opt.id);
    loadData();
  }
});

const loadData = async () => {
  const bRes = await getBoxDetail(boxId.value);
  if (bRes.code === 200) box.value = bRes.data;

  const iRes = await getItemList({ boxId: boxId.value });
  if (iRes.code === 200) items.value = iRes.data;
};

const goEdit = () => uni.navigateTo({ url: `/pages/box/boxedit?id=${boxId.value}` });
const addItem = () => uni.navigateTo({ url: `/pages/item/itemadd?boxId=${boxId.value}` });
const goItem = (id: number) => uni.navigateTo({ url: `/pages/item/itemedit?id=${id}` });
</script>

<style lang="scss" scoped>
$glass-bg: rgba(255, 255, 255, 0.9);
$primary-color: #4facfe;

.page-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef2f3 100%);
  padding: 30rpx;
  position: relative;
}

.bg-shape {
  position: absolute;
  width: 400rpx; height: 400rpx;
  background: radial-gradient(circle, rgba(161, 140, 209, 0.2) 0%, rgba(251, 194, 235, 0.1) 100%);
  border-radius: 50%;
  top: -100rpx; right: -100rpx;
  filter: blur(60px);
  z-index: 0;
}

.header {
  position: relative;
  z-index: 10;
  margin-bottom: 40rpx;
  
  .title-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    
    .box-name { font-size: 48rpx; font-weight: 800; color: #333; }
    .edit-btn { 
      background: rgba(255,255,255,0.8); 
      padding: 10rpx 24rpx; 
      border-radius: 30rpx; 
      font-size: 24rpx; 
      color: #666; 
      box-shadow: 0 4rpx 10rpx rgba(0,0,0,0.05);
    }
  }
}

.status-grid {
  background: $glass-bg;
  backdrop-filter: blur(20px);
  border-radius: 30rpx;
  padding: 30rpx;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.05);
  
  .status-item {
    text-align: center;
    display: flex; 
    flex-direction: column;
    .label { font-size: 24rpx; color: #999; margin-bottom: 8rpx; }
    .value { font-size: 30rpx; font-weight: bold; color: #333; }
    .value.on { color: #2ecc71; }
    .value.off { color: #999; }
    .value.battery { color: $primary-color; }
  }
  
  .divider { width: 2rpx; height: 40rpx; background: #eee; }
}

.content-body { position: relative; z-index: 10; }

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  
  .section-title { font-size: 32rpx; font-weight: bold; color: #444; }
  .add-mini-btn { color: $primary-color; font-size: 28rpx; font-weight: bold; }
}

.item-list {
  background: #fff;
  border-radius: 30rpx;
  padding: 10rpx 30rpx;
  box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.03);
}

.item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1px solid #f8f8f8;
  
  &:last-child { border-bottom: none; }
  &.row-hover { opacity: 0.7; background: #fafafa; }
  
  .item-left {
    display: flex;
    align-items: center;
    
    .tag-dot { width: 12rpx; height: 12rpx; background: #FF9A9E; border-radius: 50%; margin-right: 20rpx; }
    
    .item-name { font-size: 30rpx; color: #333; font-weight: 500; margin-right: 15rpx; }
    .item-tag { 
      font-size: 20rpx; 
      color: #fff; 
      background: #a18cd1; 
      padding: 2rpx 10rpx; 
      border-radius: 8rpx; 
    }
  }
  
  .item-price { font-size: 28rpx; color: #666; font-family: monospace; }
}

.empty-tip { padding: 40rpx; text-align: center; color: #ccc; font-size: 26rpx; }

/* 简单动画 */
.fade-in-down { animation: fadeInDown 0.6s ease-out; }
.fade-in-up { animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>