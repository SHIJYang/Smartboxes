{
type: uploaded file
fileName: boxdetail.vue
fullContent:
<template>
  <view class="page-container" v-if="boxStore.currentBox">
    <PCHeader current="box" />
    <view class="pc-placeholder"></view>
    <view class="bg-shape shape-1"></view>
    
    <view class="header fade-in-down">
      <view class="title-row">
        <view>
          <text class="box-name">{{ boxStore.currentBox.boxName }}</text>
          <text class="box-code">ID: {{ boxStore.currentBox.boxCode }}</text>
        </view>
        <view class="edit-btn" @click="goEdit">设置 ⚙️</view>
      </view>
      
      <view class="status-grid">
        <view class="status-item">
          <text class="status-label">状态</text>
          <text class="status-value" :class="{ active: boxStore.currentBox.status === 1 }">
            {{ boxStore.currentBox.status === 1 ? '在线' : '离线' }}
          </text>
        </view>
        <view class="status-item">
          <text class="status-label">电池</text>
          <text class="status-value battery">
            {{ boxStore.currentBox.battery || 100 }}%
          </text>
        </view>
        <view class="status-item">
          <text class="status-label">物品</text>
          <text class="status-value">{{ boxItems.length }}</text>
        </view>
      </view>

      <view class="remote-ctrl" v-if="boxStore.currentBox.status === 1">
          <button class="open-btn" @click="openBox" :loading="opening">🔓 远程开箱</button>
      </view>
    </view>
    
    <view class="content-body fade-in-up">
      <view class="section-header">
        <text class="section-title">📦 盒内物品 ({{ boxItems.length }})</text>
        <view class="add-mini-btn" @click="addItem">+ 放入物品</view>
      </view>
      
      <view class="item-list">
        <view v-for="item in boxItems" :key="item.id" class="item-row" @click="goItem(item.id)" hover-class="row-hover">
          <view class="item-left">
            <view class="tag-dot" :class="getColorClass(item.itemTag)"></view>
            <view>
              <text class="item-name">{{ getDisplayName(item) }}</text>
              <view class="tags-row" v-if="item.itemTag">
                <text class="item-tag">{{ item.itemTag }}</text>
              </view>
            </view>
          </view>
          <text class="arrow">></text>
        </view>
        
        <view v-if="boxItems.length === 0" class="empty-tip">
          <text class="emoji">🍃</text>
          <view>空空如也，快放点东西吧~</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { useBoxStore, useItemStore } from '@/stores';
import PCHeader from '@/components/PCHeader.vue';
import type { ItemDTO } from '@/common/types';

const boxStore = useBoxStore();
const itemStore = useItemStore();
const boxId = ref(0);
const opening = ref(false);

onLoad((opt: any) => {
  if (opt.id) {
    boxId.value = parseInt(opt.id);
  }
});

onShow(async () => {
  if (boxId.value) {
      await boxStore.fetchBoxDetail(boxId.value);
      // Fetch all items to ensure we have the list
      await itemStore.fetchAllItems({ boxId: boxId.value }); 
  }
});

// Use computed to filter items for THIS box from the store
const boxItems = computed(() => {
    return itemStore.getItemsByBoxId(boxId.value);
});

const goEdit = () => uni.navigateTo({ url: `/pages/box/boxedit?id=${boxId.value}` });
const addItem = () => uni.navigateTo({ url: `/pages/item/itemedit?boxId=${boxId.value}` });
const goItem = (id: number) => uni.navigateTo({ url: `/pages/item/itemedit?id=${id}` });

const openBox = () => {
    opening.value = true;
    // Simulate API call
    setTimeout(() => {
        opening.value = false;
        uni.showToast({ title: '盒子已打开 🔓', icon: 'success' });
    }, 1500);
};

const getDisplayName = (item: ItemDTO) => {
  if (item.manualEditName) return item.manualEditName;
  if (item.autoRecognizeName) return item.autoRecognizeName;
  return item.itemCode || '未命名物品';
};

const getColorClass = (tag?: string) => {
    // Return CSS class based on tag (simplified logic)
    if (!tag) return 'yellow';
    return 'purple';
};
</script>

<style lang="scss" scoped>
$bg-color: #FFF9F0;
$primary-pink: #FF9A9E;

.page-container {
  min-height: 100vh;
  background-color: $bg-color;
  padding: 30rpx;
  position: relative;
}

.pc-placeholder {
  display: none; height: 80px;
  @media screen and (min-width: 768px) { display: block; }
}

.bg-shape {
  position: absolute;
  width: 400rpx; height: 400rpx;
  background: radial-gradient(circle, rgba(255, 154, 158, 0.15) 0%, rgba(255, 255, 255, 0) 70%);
  border-radius: 50%;
  top: -100rpx; right: -100rpx;
  z-index: 0; pointer-events: none;
}

.header {
  position: relative; z-index: 10; margin-bottom: 40rpx;
  
  .title-row {
    display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 30rpx;
    
    .box-name { font-size: 56rpx; font-weight: 800; color: #333; display: block; }
    .box-code { font-size: 26rpx; color: #999; font-family: monospace; background: #fff; padding: 4rpx 10rpx; border-radius: 8rpx;}
    .edit-btn { 
      background: #fff; padding: 12rpx 28rpx; border-radius: 30rpx; font-size: 26rpx; font-weight: bold; color: #666; 
      box-shadow: 0 4rpx 10rpx rgba(0,0,0,0.05); border: 1px solid #eee;
    }
  }
}

.status-grid {
  background: #fff;
  border-radius: 32rpx;
  padding: 40rpx 0;
  display: flex; justify-content: space-around; align-items: center;
  box-shadow: 0 8rpx 30rpx rgba(161, 140, 209, 0.1);
  margin-bottom: 30rpx;
  
  .status-item {
    text-align: center;
    .label { font-size: 24rpx; color: #999; margin-bottom: 10rpx; display: block;}
    .value { font-size: 34rpx; font-weight: 900; color: #333; }
    .value.active { color: #4CAF50; }
    .value.battery { color: $primary-pink; }
  }
}

.remote-ctrl {
    .open-btn {
        background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
        color: white; border-radius: 40rpx; font-weight: bold;
        box-shadow: 0 8rpx 20rpx rgba(161, 140, 209, 0.3);
        &::after { border: none; }
    }
}

.content-body { position: relative; z-index: 10; }

.section-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; padding: 0 10rpx;
  .section-title { font-size: 34rpx; font-weight: 800; color: #333; }
  .add-mini-btn { 
    color: #fff; background: $primary-pink; font-size: 24rpx; font-weight: bold; 
    padding: 10rpx 24rpx; border-radius: 24rpx;
    box-shadow: 0 4rpx 10rpx rgba(255, 154, 158, 0.3);
  }
}

.item-list {
  background: #fff; border-radius: 32rpx; padding: 10rpx 30rpx;
  box-shadow: 0 8rpx 30rpx rgba(0,0,0,0.02);
}

.item-row {
  display: flex; justify-content: space-between; align-items: center;
  padding: 30rpx 0; border-bottom: 1px solid #f9f9f9;
  
  &:last-child { border-bottom: none; }
  &.row-hover { opacity: 0.6; }
  
  .item-left {
    display: flex; align-items: center;
    .tag-dot { width: 16rpx; height: 16rpx; background: #FFD54F; border-radius: 50%; margin-right: 24rpx; }
    .tag-dot.purple { background: #BA68C8; }
    .item-name { font-size: 30rpx; color: #333; font-weight: bold; margin-right: 15rpx; }
    .item-tag { font-size: 20rpx; color: #a18cd1; background: #F3E5F5; padding: 4rpx 10rpx; border-radius: 8rpx; }
  }
  .arrow { color: #eee; }
}

.empty-tip { 
    padding: 60rpx; text-align: center; color: #ccc; font-size: 26rpx; 
    display: flex; flex-direction: column; align-items: center;
    .emoji { font-size: 50rpx; margin-bottom: 10rpx; opacity: 0.5; }
}

.fade-in-down { animation: fadeInDown 0.6s ease-out; }
.fade-in-up { animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>
}