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
  avatar: { type: String, default: '/static/avatar/default.png' }
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
</style>