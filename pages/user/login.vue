<template>
  <view class="login-container">
    <view class="bubble bubble-1"></view>
    <view class="bubble bubble-2"></view>
    <view class="bubble bubble-3"></view>

    <view class="content-box">
      <view class="header fade-in-down">
        <view class="logo-box">
          <text class="logo-icon">✨</text>
        </view>
        <text class="app-name">魔法收纳盒</text>
        <text class="sub-title">Magic Box System</text>
      </view>

      <view class="card fade-in-up">
        <view class="input-group" :class="{ 'input-focus': focusField === 'account' }">
          <view class="icon-wrapper">👤</view>
          <input 
            class="inp" 
            v-model="formData.userAccount" 
            type="text" 
            placeholder="账号" 
            placeholder-class="placeholder-style"
            @focus="focusField = 'account'"
            @blur="focusField = ''"
          />
        </view>
        
        <view class="input-group" :class="{ 'input-focus': focusField === 'password' }">
          <view class="icon-wrapper">🔐</view>
          <input 
            class="inp" 
            v-model="formData.userPassword" 
            type="safe-password" 
            password
            placeholder="密码" 
            placeholder-class="placeholder-style"
            @focus="focusField = 'password'"
            @blur="focusField = ''"
            @confirm="handleLogin"
          />
        </view>
        
        <button 
          class="btn-login" 
          hover-class="btn-hover" 
          @click="handleLogin" 
          :loading="loggingIn"
          :disabled="loggingIn"
        >
          {{ loggingIn ? '登录中...' : '登录' }}
        </button>

        <view class="footer-links">
          <text class="link-text" @click="toRegister">注册新账号</text>
          <text class="divider">|</text>
          <text class="link-text" @click="toForgetPwd">忘记密码?</text>
        </view>
      </view>
    </view>
    
    <view class="copyright">
      © 2025 Magic Storage Corp.
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import type { LoginRequest } from '@/common/types';
import { useStores } from '@/stores';

const { userStore } = useStores();

const formData = reactive<LoginRequest>({ userAccount: '', userPassword: '' });
const loggingIn = ref(false);
const focusField = ref('');

const toRegister = () => uni.navigateTo({ url: '/pages/user/register' });
const toForgetPwd = () => uni.showToast({ title: '请联系管理员重置密码', icon: 'none' });

const handleLogin = async () => {
  if (!formData.userAccount || !formData.userPassword) {
    uni.showToast({ title: '请填写账号和密码', icon: 'none' });
    return;
  }
  
  loggingIn.value = true;
  try {
    const result = await userStore.login(formData);
    
    if (result.success) {
      uni.showToast({ title: '欢迎回家~', icon: 'success' });
      
      // 延迟跳转到首页
      setTimeout(() => {
        uni.switchTab({ url: '/pages/index/index' });
      }, 800);
    } else {
      uni.showToast({ title: result.message || '登录失败', icon: 'none' });
    }
  } catch (error) {
    console.error('登录错误:', error);
    uni.showToast({ title: '网络连接失败', icon: 'none' });
  } finally {
    loggingIn.value = false;
  }
};
</script>

<style lang="scss" scoped>
/* 暖色渐变主题 */
$bg-gradient: linear-gradient(to top, #fff1eb 0%, #ace0f9 100%); /* 温暖的晨曦色 */
$card-bg: rgba(255, 255, 255, 0.85);
$btn-gradient: linear-gradient(120deg, #a18cd1 0%, #fbc2eb 100%); /* 香芋紫渐变 */
$primary-pink: #FF9A9E;

.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #FFF9F0 0%, #FFF0F5 100%); /* 奶油白 -> 浅粉 */
  position: relative;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  overflow: hidden;
}

/* 气泡动画 - 糖果色 */
.bubble {
  position: absolute; border-radius: 50%; filter: blur(60px); z-index: 0;
  animation: float 8s infinite ease-in-out;
}
.bubble-1 { width: 400rpx; height: 400rpx; background: rgba(255, 154, 158, 0.2); top: -100rpx; left: -100rpx; }
.bubble-2 { width: 300rpx; height: 300rpx; background: rgba(161, 140, 209, 0.2); bottom: 50rpx; right: -80rpx; animation-delay: -3s; }
.bubble-3 { width: 200rpx; height: 200rpx; background: rgba(255, 215, 0, 0.15); top: 20%; right: 10%; animation-delay: -5s; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(30px); }
}

.content-box { width: 100%; padding: 0 60rpx; z-index: 10; }

.header {
  text-align: center; margin-bottom: 60rpx;
  
  .logo-box {
    width: 140rpx; height: 140rpx; background: #fff; border-radius: 40rpx;
    margin: 0rpx auto 60rpx; display: flex; align-items: center; justify-content: center;
    box-shadow: 0 15rpx 35rpx rgba(161, 140, 209, 0.15);
    animation: float 6s infinite ease-in-out;
    .logo-icon { font-size: 70rpx; }
  }
  
  .app-name { font-size: 50rpx; font-weight: 900; color: #333; letter-spacing: 2rpx; display: block; }
  .sub-title { font-size: 24rpx; color: #999; letter-spacing: 4rpx; margin-top: 10rpx; text-transform: uppercase; }
}

.card {
  background: $card-bg; backdrop-filter: blur(25px);
  border-radius: 40rpx; padding: 60rpx 40rpx;
  box-shadow: 0 20rpx 60rpx rgba(161, 140, 209, 0.15);
  border: 2px solid #fff;
}

.input-group {
  display: flex; align-items: center;
  background: #FFF5F7; /* 极淡的粉色背景 */
  border-radius: 30rpx; padding: 0 30rpx; margin-bottom: 30rpx;
  height: 100rpx; border: 2rpx solid transparent; transition: all 0.3s;
  
  .icon-wrapper { 
    font-size: 36rpx; margin-right: 24rpx; opacity: 0.6; 
    transition: all 0.3s;
  }
  
  .inp { 
    flex: 1; height: 100%; font-size: 30rpx; color: #333; 
    background: transparent; border: none; outline: none;
  }
  
  &.input-focus {
    background: #fff; border-color: $primary-pink;
    box-shadow: 0 0 20rpx rgba(255, 154, 158, 0.2);
    .icon-wrapper { opacity: 1; transform: scale(1.1); }
  }
}

.btn-login {
  margin-top: 60rpx; height: 100rpx; line-height: 100rpx;
  background: $btn-gradient; color: #fff; font-size: 34rpx; font-weight: 900;
  border-radius: 50rpx; box-shadow: 0 15rpx 30rpx rgba(161, 140, 209, 0.4);
  border: none; position: relative; overflow: hidden;
  
  &::after { border: none; }
  
  &[disabled] { 
    opacity: 0.7; filter: grayscale(0.5); 
  }
  
  &::before {
    content: ''; position: absolute; top: 0; left: -100%; width: 100%; height: 100%;
    background: linear-gradient(120deg, rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.3));
    transition: all 0.6s ease; z-index: -1;
  }
  
  &:hover::before, &.btn-hover::before {
    left: 100%;
  }
}

.btn-hover { transform: scale(0.98); opacity: 0.95; }

.footer-links {
  margin-top: 40rpx; display: flex; justify-content: center; align-items: center;
  .link-text { 
    font-size: 26rpx; color: #888; padding: 10rpx 20rpx; font-weight: bold;
    transition: all 0.3s;
    
    &:hover, &:active {
      color: $primary-pink;
    }
  }
  .divider { color: #eee; margin: 0 10rpx; }
}

.copyright { 
  position: absolute; bottom: 40rpx; left: 0; right: 0; 
  font-size: 22rpx; color: #aaa; opacity: 0.6; text-align: center;
}

.fade-in-down { animation: fadeInDown 0.8s ease-out; }
.fade-in-up { animation: fadeInUp 0.8s ease-out; }

@keyframes fadeInDown { 
  from { opacity: 0; transform: translateY(-30px); } 
  to { opacity: 1; transform: translateY(0); } 
}

@keyframes fadeInUp { 
  from { opacity: 0; transform: translateY(30px); } 
  to { opacity: 1; transform: translateY(0); } 
}

.placeholder-style {
  color: #ccc; font-size: 28rpx;
}

@media screen and (min-width: 768px) {
  .content-box {
    width: 600rpx; margin: 0 auto;
  }
  
  .card {
    padding: 60rpx 50rpx;
  }
}
</style>