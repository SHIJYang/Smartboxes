<template>
  <view class="page">
    <u-navbar title="登录" />
    <view class="form">
      <u-input v-model="form.username" placeholder="账号" />
      <u-input v-model="form.password" placeholder="密码" type="password" />
      <u-button type="primary" @click="login">登录</u-button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const form = ref({ username: '', password: '' })

const login = () => {
  if (!form.value.username || !form.value.password) {
    return uni.showToast({ title: '请输入账号密码', icon: 'none' })
  }
  // TODO: 登录逻辑
  uni.showToast({ title: '登录成功' })
  setTimeout(() => uni.reLaunch({ url: '/pages/index/index' }), 600)
}
</script>

<style scoped>
.page { padding: 24rpx; }
.form { display: grid; gap: 24rpx; margin-top: 24rpx; }
</style>
