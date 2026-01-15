<template>
  <view class="page-root">
    <PCHeader current="box" />
    <view class="pc-placeholder"></view>
    <view class="mobile-header">
      <view class="header-content">
        <text class="title">📦 我的宝箱库</text>
        <text class="subtitle">管理你的 {{ store.boxList.length }} 个收纳空间</text>
      </view>
    </view>
    <scroll-view scroll-y class="list-scroll" :show-scrollbar="false">
      <view class="list-body">
        <view v-for="box in store.boxList" :key="box.id" class="box-card" @click="goDetail(box.id)" hover-class="card-hover">
          <view class="card-top">
            <view class="box-name">{{ box.boxName }}</view>
            <view class="box-code">ID: {{ box.boxCode }}</view>
          </view>
          <view class="card-btm">
            <view class="status-tag" :class="{ active: box.status === 1 }">
              {{ box.status === 1 ? '启用中' : '已停用' }}
            </view>
            <view class="type-tag">
              {{ box.boxType === 1 ? '普通箱' : '冷藏箱' }}
            </view>
          </view>
        </view>
        <view v-if="store.boxList.length === 0" class="empty-state">
          <text>还没有盒子，点击右下角添加一个吧~</text>
        </view>
      </view>
    </scroll-view>
    <view class="fab" @click="goEdit()" hover-class="fab-hover">+</view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app';
import { useBoxStore } from '@/stores/boxStore';
import PCHeader from '@/components/PCHeader.vue';

const store = useBoxStore();

onShow(async () => {
  await store.fetchUserBoxes();
});

const goDetail = (id: number) => uni.navigateTo({ url: `/pages/box/boxdetail?id=${id}` });
const goEdit = () => uni.navigateTo({ url: '/pages/box/boxedit' });
</script>
<style lang="scss" scoped>
/* 暖色主题变量 */
$bg-color: #FFF9F0;
$header-gradient: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%); /* 香芋紫渐变 */
$fab-gradient: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);
$shadow-soft: 0 8rpx 20rpx rgba(161, 140, 209, 0.2);

.page-root { 
  min-height: 100vh; 
  background-color: $bg-color; 
}

/* PC 占位 */
.pc-placeholder {
  display: none; height: 80px;
  @media screen and (min-width: 768px) { display: block; }
}

/* 手机头部 */
.mobile-header {
  background: $header-gradient;
  margin-bottom: 80rpx;
  padding: 30rpx 30rpx 20rpx; /* 底部加高，为了圆弧效果 */
  border-bottom-left-radius: 60rpx;
  border-bottom-right-radius: 60rpx;
  box-shadow: $shadow-soft;
  
  @media screen and (min-width: 768px) { display: none; }
  
  .header-content {
    margin-top: 20rpx;
    .title { font-size: 44rpx; font-weight: 800; color: #fff; display: block; margin-bottom: 8rpx; text-shadow: 2rpx 2rpx 4rpx rgba(0,0,0,0.1); }
    .subtitle { font-size: 26rpx; color: rgba(255,255,255,0.9); }
  }
}

/* 列表区域 */
.list-scroll { 
  height: 100%;
  margin-top: -40rpx; /* 向上重叠 Header */
  @media screen and (min-width: 768px) { margin-top: 20rpx; }
}

.list-body { padding: 0 30rpx 200rpx; }

.box-card {
  background: #fff;
  border-radius: 32rpx;
  padding: 30rpx;
  margin-bottom: 24rpx;
  display: flex; align-items: center;
  box-shadow: 0 4rpx 15rpx rgba(0,0,0,0.03);
  border: 1px solid rgba(255,255,255,1);
  transition: transform 0.1s;
  
  &.card-hover { transform: scale(0.98); background: #fafafa; }
  
  .card-icon {
    width: 100rpx; height: 100rpx; 
    background: #F3E5F5; color: #BA68C8; /* 浅紫底色 */
    border-radius: 28rpx; display: flex; align-items: center; justify-content: center;
    font-size: 44rpx; margin-right: 24rpx;
  }
  
  .card-info {
    flex: 1;
    .top-row { display: flex; align-items: center; margin-bottom: 6rpx; }
    .name { font-size: 32rpx; font-weight: bold; color: #333; margin-right: 15rpx; }
    
    .status-badge {
      font-size: 20rpx; padding: 2rpx 10rpx; border-radius: 10rpx;
      &.online { background: #E8F5E9; color: #4CAF50; }
      &.offline { background: #eee; color: #999; }
    }
    
    .desc { font-size: 24rpx; color: #999; font-family: monospace; }
  }
  
  .arrow-btn {
    width: 60rpx; height: 60rpx; border-radius: 50%;
    background: #f8f8f8; color: #ccc;
    display: flex; align-items: center; justify-content: center;
    font-weight: bold;
  }
}

.empty-state { text-align: center; color: #ccc; margin-top: 100rpx; font-size: 28rpx; }

/* 悬浮按钮 FAB */
.fab {
  position: fixed; bottom: 100rpx; right: 40rpx;
  width: 110rpx; height: 110rpx; border-radius: 50%;
  background: $fab-gradient;
  color: #fff; font-size: 60rpx; font-weight: 300;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 10rpx 25rpx rgba(255, 154, 158, 0.4);
  z-index: 100;
  transition: transform 0.2s;
  
  &.fab-hover { transform: scale(0.9); }
  
  @media screen and (min-width: 768px) {
    right: 80rpx; bottom: 80rpx;
  }
}
</style>