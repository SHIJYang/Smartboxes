<template>
  <view class="login">
    <view class="logo">
      <image src="/static/logo.png" mode="aspectFit"></image>
    </view>
    <view class="title">智能收纳管理系统</view>
    <view class="form">
      <input class="inp" v-model="uid" type="number" placeholder="请输入用户ID (默认: 1001)" />
      <button class="btn" @click="doLogin" :loading="loggingIn">登 录</button>
    </view>
    <view class="tips">
      <text>提示：默认用户ID为1001，可直接登录</text>
    </view>
  </view>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { login } from '@/api/index';
const uid = ref('1001');
const loggingIn = ref(false);

const doLogin = async () => {
  if (!uid.value) {
    uni.showToast({ title: '请输入用户ID', icon: 'none' });
    return;
  }
  
  loggingIn.value = true;
  try {
    const res = await login(parseInt(uid.value));
    if (res.code === 200) {
      uni.setStorageSync('userInfo', res.data);
      uni.showToast({ title: '登录成功', icon: 'success' });
      setTimeout(() => {
        uni.switchTab({ url: '/pages/index/index' });
      }, 1000);
    } else {
      uni.showToast({ title: res.message || '登录失败', icon: 'none' });
    }
  } catch (error) {
    uni.showToast({ title: '登录异常', icon: 'none' });
  } finally {
    loggingIn.value = false;
  }
};
</script>
<style>
.login { padding: 50px 30px; background: linear-gradient(to bottom, #007aff, #ffffff); min-height: 100vh; display: flex; flex-direction: column; align-items: center; }
.logo { width: 100px; height: 100px; margin-bottom: 20px; }
.logo image { width: 100%; height: 100%; }
.title { font-size: 24px; font-weight: bold; text-align: center; margin-bottom: 50px; color: #fff; }
.form { width: 100%; }
.inp { background: #fff; height: 50px; border-radius: 25px; padding: 0 20px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.btn { background: #ff9800; color: #fff; border-radius: 25px; height: 50px; font-size: 16px; font-weight: 500; }
.tips { margin-top: 30px; text-align: center; color: #fff; font-size: 14px; }
</style>