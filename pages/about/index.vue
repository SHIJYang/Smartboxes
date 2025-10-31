<template>
  <view class="page">
    <u-navbar title="关于我们" />
    <view class="content">
      <image class="logo" src="/static/logo.png" mode="aspectFit" />
      <text class="name">智能收纳盒</text>
      <text class="ver">版本 v1.0.0</text>
      <text class="desc">帮助你高效管理物品与空间。</text>
    </view>
  </view>
</template>

<script setup>
</script>

<style scoped>
.page { padding: 40rpx 32rpx; text-align: center; }
.logo { width: 160rpx; height: 160rpx; margin: 40rpx auto; }
.name { display: block; font-size: 36rpx; font-weight: 600; margin-top: 16rpx; }
.ver { display: block; color: #888; margin: 8rpx 0 24rpx; }
.desc { color: #555; }
</style>
