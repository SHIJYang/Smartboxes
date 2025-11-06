<!-- components/对话气泡.vue -->
<template>
  <view :class="['bubble-wrapper', { 'self': isSelf }]">
    <!-- 头像（仅对方显示） -->
    <u-avatar
      v-if="!isSelf"
      :src="avatar"
      size="60"
      style="margin-right: 20rpx;"
    />

    <!-- 气泡 -->
    <view :class="['bubble', { 'self-bubble': isSelf }]">
      <text class="bubble-text">{{ text }}</text>
      
      <!-- 推荐物品列表 -->
      <view v-if="recommendItems && recommendItems.length > 0" class="recommend-items">
        <text class="recommend-title">推荐物品：</text>
        <view v-for="item in recommendItems" :key="item.id" class="recommend-item">
          <text class="item-name">{{ item.manual_edit_name }}</text>
          <text class="item-box">(在{{ item.box_id }}号盒)</text>
        </view>
      </view>
    </view>

    <!-- 自己的头像（右侧） -->
    <u-avatar
      v-if="isSelf"
      :src="avatar"
      size="60"
      style="margin-left: 20rpx;"
    />
  </view>
</template>

<script setup>
defineProps({
  text: String,
  isSelf: { type: Boolean, default: false }, // true = 自己发的
  avatar: { type: String, default: '/static/avatar/default.png' },
  recommendItems: { // 新增推荐物品属性
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.bubble-wrapper {
  display: flex;
  align-items: flex-start;
  margin: 20rpx 0;
}
.self {
  flex-direction: row-reverse;
}
.bubble {
  max-width: 480rpx;
  padding: 24rpx;
  background-color: #f0f0f0;
  border-radius: 16rpx 16rpx 16rpx 0;
}
.self-bubble {
  background-color: #4A90E2;
  color: white;
  border-radius: 16rpx 16rpx 0 16rpx;
}
.bubble-text {
  font-size: 28rpx;
  line-height: 1.5;
}

/* 推荐物品样式 */
.recommend-items {
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid rgba(0, 0, 0, 0.1);
}

.recommend-title {
  font-size: 24rpx;
  font-weight: 600;
  color: #666;
  display: block;
  margin-bottom: 8rpx;
}

.self-bubble .recommend-title {
  color: rgba(255, 255, 255, 0.9);
}

.recommend-item {
  display: flex;
  align-items: center;
  margin: 4rpx 0;
}

.item-name {
  font-size: 26rpx;
  color: #333;
}

.self-bubble .item-name {
  color: white;
}

.item-box {
  font-size: 22rpx;
  color: #888;
  margin-left: 8rpx;
}

.self-bubble .item-box {
  color: rgba(255, 255, 255, 0.7);
}
</style>