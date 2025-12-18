<template>
  <view class="page-container">
    <view class="bg-shape shape-1"></view>
    <view class="bg-shape shape-2"></view>

    <view class="content-wrapper">
      <view class="header-title fade-in-down">我的盒子</view>

      <view class="list-container fade-in-up">
        <view 
          v-for="box in list" 
          :key="box.id" 
          class="box-card" 
          @click="goDetail(box.id)"
          hover-class="card-hover"
        >
          <view class="card-header">
            <view class="icon-wrapper">
              <text class="box-icon">📦</text>
            </view>
            <view class="info">
              <text class="name">{{ box.boxName }}</text>
              <text class="code">ID: {{ box.boxCode }}</text>
            </view>
            <view :class="['status-badge', box.status === 1 ? 'online' : 'offline']">
              <text class="dot"></text>
              {{ box.status === 1 ? '在线' : '离线' }}
            </view>
          </view>
          
          <view class="card-footer">
            <text class="sub-info">最后连接: {{ formatDate(box.lastHeartbeatTime) }}</text>
            <text class="arrow">→</text>
          </view>
        </view>
      </view>
    </view>

    <view class="fab-btn fade-in-up" @click="goEdit()">
      <text class="plus">+</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getBoxList } from '@/api/index';
import type { BoxDTO } from '@/common/types';

const list = ref<BoxDTO[]>([]);

onShow(async () => {
  const res = await getBoxList({ userId: 1001 }); // 假设当前用户ID
  if (res.code === 200) list.value = res.data;
});

const goDetail = (id: number) => uni.navigateTo({ url: `/pages/box/boxdetail?id=${id}` });
const goEdit = () => uni.navigateTo({ url: '/pages/box/boxedit' });

const formatDate = (str?: string) => str ? str.split(' ')[0] : '刚刚';
</script>

<style lang="scss" scoped>
/* 引用 user.vue 风格变量 */
$glass-bg: rgba(255, 255, 255, 0.85);
$primary-gradient: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);

.page-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef2f3 100%);
  position: relative;
  padding: 30rpx;
  overflow: hidden;
}

/* 装饰球 */
.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
}
.shape-1 { width: 300px; height: 300px; background: rgba(79, 172, 254, 0.15); top: -100px; left: -50px; }
.shape-2 { width: 250px; height: 250px; background: rgba(255, 154, 158, 0.15); bottom: 100px; right: -80px; }

.content-wrapper { position: relative; z-index: 10; padding-bottom: 150rpx; }

.header-title {
  font-size: 40rpx;
  font-weight: 800;
  color: #333;
  margin-bottom: 30rpx;
  margin-left: 10rpx;
}

.box-card {
  background: $glass-bg;
  backdrop-filter: blur(20px);
  border-radius: 30rpx;
  padding: 30rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.04);
  border: 1px solid rgba(255,255,255,0.6);
  transition: transform 0.2s;
}

.card-hover { transform: scale(0.98); }

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.icon-wrapper {
  width: 90rpx;
  height: 90rpx;
  background: #f0f7ff;
  border-radius: 25rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
  .box-icon { font-size: 44rpx; }
}

.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  .name { font-size: 32rpx; font-weight: bold; color: #333; margin-bottom: 4rpx; }
  .code { font-size: 22rpx; color: #999; font-family: monospace; }
}

.status-badge {
  padding: 8rpx 16rpx;
  border-radius: 30rpx;
  font-size: 22rpx;
  display: flex;
  align-items: center;
  font-weight: 500;
  
  .dot { width: 10rpx; height: 10rpx; border-radius: 50%; margin-right: 8rpx; }
  
  &.online { background: rgba(76, 217, 100, 0.15); color: #2ecc71; .dot { background: #2ecc71; } }
  &.offline { background: rgba(144, 147, 153, 0.15); color: #909399; .dot { background: #909399; } }
}

.card-footer {
  border-top: 1px solid rgba(0,0,0,0.03);
  padding-top: 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .sub-info { font-size: 22rpx; color: #aaa; }
  .arrow { color: #ccc; font-size: 28rpx; }
}

/* 悬浮按钮 */
.fab-btn {
  position: fixed;
  bottom: 160rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 110rpx;
  height: 110rpx;
  background: $primary-gradient;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10rpx 30rpx rgba(79, 172, 254, 0.4);
  z-index: 99;
  
  .plus { color: #fff; font-size: 60rpx; font-weight: 300; margin-top: -6rpx; }
  &:active { transform: translateX(-50%) scale(0.95); }
}

/* 动画 */
.fade-in-down { animation: fadeInDown 0.6s ease-out; }
.fade-in-up { animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInDown { from { opacity: 0; transform: translateY(-20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>